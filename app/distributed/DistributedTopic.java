package distributed;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import distributed.DistributedServices;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by mgiles on 5/12/14.
 */

public abstract class DistributedTopic implements MessageListener<String> {

    private ITopic<String> topic;
    private String registrationId;
    protected Executor messageExecutor;

    public abstract void handleMessage(Message<String> message);

    /**
     * Invoked when a message is received for the added topic. Note that topic guarantees message ordering.
     * Therefore there is only one thread invoking onMessage. The user shouldn't keep the thread busy and preferably
     * dispatch it via an Executor. This will increase the performance of the topic.
     *
     * @param message received message
     */
    @Override
    public final void onMessage(Message<String> message) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                handleMessage(message);
            }
        };
        messageExecutor.execute(r);
    }

    public DistributedTopic(String uniqueTopic) {
        messageExecutor = Executors.newSingleThreadExecutor();
        HazelcastInstance instance = DistributedServices.getInstance();
        topic = instance.getTopic(uniqueTopic);
    }

    public void start() {
        if (registrationId == null) {
            registrationId = topic.addMessageListener(this);
        }
    }

    public void stop() {
        if (registrationId != null) {
            topic.removeMessageListener(registrationId);
        }
    }

    public void publish(String message) {
        if (registrationId != null) {
            topic.publish(message);
        }
    }
}
