package distributed.tasks;

import distributed.DistributedSocket;
import distributed.DistributedTopic;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmaclean on 7/24/14.
 */
public class StatsSocketConnectionTask extends DistributedTask {
    private List<DistributedSocket> sockets;

    public StatsSocketConnectionTask(List<DistributedSocket> sockets) {
        this.sockets = sockets;
    }

    @Override
    protected String execute() throws Exception {
        Logger.info("Checking status of Stats socket connections...");

        for(DistributedSocket distributedSocket: sockets) {
            if(System.currentTimeMillis() - distributedSocket.getLastMessageReceived() > (1000 * 60 * 5)) {
                Logger.error("Socket has not received an update (even a ping) in 5 minute.  Restarting...");
                DistributedTopic topic = distributedSocket.getTopic();

                distributedSocket.stop();
                distributedSocket.setTopic(topic);

                distributedSocket.start();
            }
        }

        return null;
    }

    public List<DistributedSocket> getSockets() {
        return sockets;
    }

    public void setSockets(List<DistributedSocket> sockets) {
        this.sockets = sockets;
    }
}
