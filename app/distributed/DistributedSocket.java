package distributed;

import common.GlobalConstants;
import play.Logger;
import play.Play;
import simulator.ContestSimulationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mgiles on 5/12/14.
 */
public class DistributedSocket {

    private Socket listeningSocket;
    private String statsHost;
    private int sportsPort;
    private String username;
    private String password;
    private DistributedTopic topic;
    private Long lastMessageReceived;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public DistributedSocket() {
    }

    public DistributedSocket(String leagueAbbreviation) {
        sportsPort = Integer.parseInt(Play.application().configuration().getString(GlobalConstants.CONFIG_STATS_SOCKET_PORT + leagueAbbreviation.toLowerCase()));
        statsHost = Play.application().configuration().getString(GlobalConstants.CONFIG_STATS_SOCKET_URL);
        this.username = Play.application().configuration().getString(GlobalConstants.CONFIG_STATS_SOCKET_USERNAME);
        this.password = Play.application().configuration().getString(GlobalConstants.CONFIG_STATS_SOCKET_PASSWORD);
    }

    public DistributedTopic getTopic() {
        return topic;
    }

    public void setTopic(DistributedTopic topic) {
        this.topic = topic;
    }

    public void start() throws IOException {
        if (ContestSimulationManager.isSimulation()) {
            statsHost = Play.application().configuration().getString(GlobalConstants.CONFIG_SIMULATOR_SOCKET_URL);
            sportsPort = Integer.parseInt(Play.application().configuration().getString(GlobalConstants.CONFIG_SIMULATOR_SOCKET_PORT));
        }
        listeningSocket = new Socket(statsHost, sportsPort);
        final BufferedReader in = new BufferedReader(new InputStreamReader(listeningSocket.getInputStream()));

        handlePreConnectionInteraction(listeningSocket, in);

        topic.start();

        executor.submit(() -> {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    lastMessageReceived = System.currentTimeMillis();
                    topic.publish(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void stop() throws IOException {
        if (listeningSocket != null) {
            listeningSocket.getInputStream().close();
            listeningSocket.close();
            listeningSocket = null;
        }

        if (topic != null) {
            topic.stop();
            topic = null;
        }
    }

    public boolean isConnected() {
        return listeningSocket != null && listeningSocket.isConnected();
    }


    protected void handlePreConnectionInteraction(Socket listeningSocket, BufferedReader in) throws IOException {
        authenticate(listeningSocket, in);
    }

    /**
     * Authenticate with Stats for the socket connection we're trying to make.
     *
     * @param listeningSocket The socket connection we're trying to establish.
     * @param in              A BufferedReader to read data in from the socket.
     * @throws IOException
     */
    private void authenticate(Socket listeningSocket, BufferedReader in) throws IOException {
        PrintWriter writer = new PrintWriter(listeningSocket.getOutputStream(), true);
        String s = in.readLine();
        Logger.info(s);
        writer.println(username);
        s = in.readLine();
        Logger.info(s);
        writer.println(password);
        s = in.readLine();
        Logger.info(s);
    }

    public Long getLastMessageReceived() {
        return lastMessageReceived;
    }
}
