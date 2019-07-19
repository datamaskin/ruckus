package distributed;

import play.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * DistributedSocket designed for connecting to the Contest Simulator.
 */
public class DistributedSimulatorSocket extends DistributedSocket {
    /**
     * The id of the SportEvent that we want to stream.
     */
    private int sportEventId;

    public DistributedSimulatorSocket(int sportEventId) {
        super();

        this.sportEventId = sportEventId;
    }

    /**
     * Tell the socket server which game we want.
     *
     * @param listeningSocket   Our socket connection to the contest simulator.
     * @param in                The input stream of the socket.
     * @throws IOException      Socket errors.
     */
    protected void handlePreConnectionInteraction(Socket listeningSocket, BufferedReader in) throws IOException {
        PrintWriter writer = new PrintWriter(listeningSocket.getOutputStream(), true);
        writer.println("" + sportEventId);

        String result = in.readLine();
        if(result.startsWith("Error")) {
            Logger.error(result);
        }
        else {
            Logger.info(result);
        }
    }

    public void setSportEventId(int sportEventId) {
        this.sportEventId = sportEventId;
    }
}
