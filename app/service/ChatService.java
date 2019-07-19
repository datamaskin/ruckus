package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.ChatMessage;
import distributed.DistributedServices;
import play.Logger;

import java.util.LinkedList;

/**
 * Created by mgiles on 6/30/14.
 */
public class ChatService extends AbstractCachingService implements IChatService {
    private static final int MAX_MESSAGE = 10;

    @Override
    public void addMessage(String channel, ChatMessage message) {
        try {
            String sMessages = (String) DistributedServices.getInstance().getMap("chat").get(channel);
            if (sMessages == null) {
                sMessages = new ObjectMapper().writeValueAsString(new LinkedList<ChatMessage>());
            }
            LinkedList<ChatMessage> messages = new ObjectMapper().readValue(sMessages, LinkedList.class);

            messages.addFirst(message);
            if (messages.size() > MAX_MESSAGE) {
                messages.removeLast();
            }
            DistributedServices.getInstance().getMap("chat").put(channel, new ObjectMapper().writeValueAsString(messages));
        } catch (Exception e) {
            Logger.warn(e.getMessage());
        }
    }

    @Override
    public String getMessageList(String channel) throws JsonProcessingException {
        try {
            String sMessages = (String) DistributedServices.getInstance().getMap("chat").get(channel);
            if (sMessages == null) {
                sMessages = new ObjectMapper().writeValueAsString(new LinkedList<ChatMessage>());
            }
            LinkedList<ChatMessage> messages = new ObjectMapper().readValue(sMessages, LinkedList.class);
            return new ObjectMapper().writeValueAsString(messages);
        } catch (Exception e) {
            Logger.warn(e.getMessage());
        }
        return new ObjectMapper().writeValueAsString(new LinkedList<ChatMessage>());
    }

    @Override
    public void destroyChannel(String channel) {
        DistributedServices.getInstance().getMap("chat").remove(channel);
    }

    @Override
    public void flushAllCaches() {
        DistributedServices.getInstance().getMap("chat").clear();
    }
}
