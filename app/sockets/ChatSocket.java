package sockets;

import auth.AuthenticationException;
import service.IChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import common.ChatMessage;
import common.ClientMessage;
import distributed.DistributedServices;
import distributed.DistributedTopic;
import models.user.User;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.PathParam;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import utils.JacksonDecoder;
import utils.JacksonEncoder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by mgiles on 6/13/14.
 */
@ManagedService(path = "/ws/chat/{contestId}", interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        HeartbeatInterceptor.class,
        SuspendTrackerInterceptor.class
})
public class ChatSocket extends AbstractSocket {

    private DistributedTopic topic;

    @PathParam("contestId")
    private String contestId;

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     *
     * @param event
     */
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        super.disconnect(event);
        if (topic != null) {
            topic.stop();
        }
    }

    /**
     * Invoked when the connection as been fully established and suspended, e.g ready for receiving messages.
     *
     * @param r
     */
    @Ready
    public void onReady(final AtmosphereResource r) throws IOException {
        try {
            super.ready(r);
        } catch (AuthenticationException e) {
            r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
            r.close();
            return;
        }
        if (contestId != null) {
            try {
                final User user = getCurrentUser(r);
                IChatService cache = DistributedServices.getContext().getBean("ChatManager", IChatService.class);
                String messageJson = cache.getMessageList(contestId);
                LinkedList<Map<String, Object>> messages = new ObjectMapper().readValue(messageJson, LinkedList.class);
                for (Map<String, Object> map : messages) {
                    if (user.getUserName().equals(map.get("author"))) {
                        map.put("me", true);
                    }
                }
                ClientMessage message = new ClientMessage();
                message.setPayload(messages);
                message.setType("CHAT_ALL");
                r.getResponse().write(new ObjectMapper().writeValueAsString(message));

                topic = new DistributedTopic(contestId) {
                    @Override
                    public void handleMessage(Message<String> message) {
                        if (r.isCancelled()) {
                            this.stop();
                            return;
                        }
                        try {
                            ChatMessage chatMessage = new ObjectMapper().readValue(message.getMessageObject(), ChatMessage.class);
                            if (user.getUserName().equals(chatMessage.getAuthor())) {
                                chatMessage.setMe(true);
                            }
                            ClientMessage sMessage = new ClientMessage();
                            sMessage.setPayload(chatMessage);
                            sMessage.setType("CHAT_MSG");
                            r.getResponse().write(new ObjectMapper().writeValueAsString(sMessage));
                        } catch (IOException e) {
                            r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
                        }
                    }
                };
                topic.start();
            } catch (IOException e) {
                r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
            }
        } else {
            r.getResponse().write("{error: \"No contestId passed in\"}");
        }
    }

    /**
     * @param message an instance of {@link ChatMessage}
     * @return
     * @throws IOException
     */
    @org.atmosphere.config.service.Message(encoders = {JacksonEncoder.class}, decoders = {JacksonDecoder.class})
    public ChatMessage onMessage(AtmosphereResource r, ChatMessage message) throws IOException {
        final User user = getCurrentUser(r);
        message.setAuthor(user.getUserName());
        IChatService cache = DistributedServices.getContext().getBean("ChatManager", IChatService.class);
        cache.addMessage(contestId, message);
        DistributedServices.getInstance().getTopic(contestId).publish(new ObjectMapper().writeValueAsString(message));
        return null;
    }
}
