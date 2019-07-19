package sockets;

import auth.AuthenticationException;
import service.ContestLiveProjectionGraphService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import common.ClientMessage;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedTopic;
import models.contest.Contest;
import models.contest.Entry;
import models.contest.Lineup;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.PathParam;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import play.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Socket endpoint to support pushing data used to generate the projection graph.
 */
@ManagedService(path = "/ws/projectiongraph/{contestId}/{lineup1Id}/{lineup2Id}", interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        HeartbeatInterceptor.class,
        SuspendTrackerInterceptor.class
})
public class ContestLiveProjectionGraph extends AbstractSocket {

    public static final String SOCKET_MESSAGE_ALL = "CONTESTLIVEPROJECTIONGRAPH_ALL";
    public static final String SOCKET_MESSAGE_UPDATE = "CONTESTLIVEPROJECTIONGRAPH_UPDATE";

    private ObjectMapper mapper = new ObjectMapper();

    private ContestLiveProjectionGraphService manager = new ContestLiveProjectionGraphService();

    @PathParam("contestId")
    private String contestId;

    @PathParam("lineup1Id")
    private String lineup1IdStr;

    @PathParam("lineup2Id")
    private String lineup2IdStr;

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     *
     * @param event
     */
    @Override
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        super.disconnect(event);
        DistributedTopic topic = (DistributedTopic) event.getResource().session().getAttribute(GlobalConstants.TOPIC_FANTASY_POINTS);
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

        /*
         * Validate inputs.
         */
        int lineup1Id = -1;
        int lineup2Id = -1;
        try {
            lineup1Id = Integer.parseInt(lineup1IdStr);
            lineup2Id = Integer.parseInt(lineup2IdStr);
        } catch (NumberFormatException e) {
            if (lineup1Id == -1) {
                String error = "Error parsing contest id and/or lineup 1 id.  Please ensure they are both valid integers.";
                Logger.error(error);
                r.getResponse().write("{error: " + error + "}");
                return;
            }
            if (lineup2Id == -1) {
                Logger.info("No id provided for second lineup.");
            }
        }

        List<Lineup> lineups = new ArrayList<>();
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        Lineup lineup = DaoFactory.getContestDao().findLineup(lineup1Id);
        lineups.add(lineup);

        if (lineup2Id != -1) {
            Lineup secondLineup = DaoFactory.getContestDao().findLineup(lineup2Id);
            lineups.add(secondLineup);
        }

        /**
         * Set up the topics.
         */
        for (Lineup currLineup : lineups) {
            DistributedTopic topic = new DistributedTopic(GlobalConstants.FANTASY_POINT_UPDATE_LINEUP + currLineup.getId()) {

                @Override
                public void handleMessage(Message<String> message) {
                    if (r.isCancelled()) {
                        this.stop();
                        return;
                    }

                    ClientMessage clientMessage = new ClientMessage();
                    clientMessage.setType(SOCKET_MESSAGE_UPDATE);

                    try {
                        Lineup updatedLineup = mapper.readValue(message.getMessageObject(), Lineup.class);

                        clientMessage.setJsonPayload(manager.getGraphAsJson(contest, updatedLineup));

                        String json = mapper.writeValueAsString(clientMessage);

                        Logger.debug(json);
                        writeData(r, json);
                    } catch (IOException e) {
                        Logger.error("ERROR: " + e.getMessage(), e);
                        r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
                    }
                }
            };
            topic.start();
        }

        try {
            ClientMessage clientMessage = new ClientMessage();
            clientMessage.setType(SOCKET_MESSAGE_ALL);
            clientMessage.setJsonPayload(manager.getGraphAsJson(contest, lineup));
            r.getResponse().write(mapper.writeValueAsBytes(clientMessage));
        } catch (IOException e) {
            Logger.error("ERROR: " + e.getMessage(), e);
            r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
        }
    }
}
