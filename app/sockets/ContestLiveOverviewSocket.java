package sockets;

import auth.AuthenticationException;
import service.ContestLiveOverviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import common.ClientMessage;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedTopic;
import models.contest.Contest;
import models.contest.ContestState;
import models.contest.Entry;
import models.contest.Lineup;
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
import play.Logger;
import utils.ITimeService;
import utils.TimeService;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * The socket endpoint that serves up data for the contest overview page.
 * <p>
 * For the overview, we need to know:
 * - What place an entry is in.
 * - How many fantasy points it has.
 */
@ManagedService(path = "/ws/contestliveoverview/{type}", interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        HeartbeatInterceptor.class,
        SuspendTrackerInterceptor.class
})
public class ContestLiveOverviewSocket extends AbstractSocket {
    public static final String SOCKET_MESSAGE_ALL = "CONTESTLIVEOVERVIEW_ALL";
    public static final String SOCKET_MESSAGE_UPDATE = "CONTESTLIVEOVERVIEW_UPDATE";
    public static final String SOCKET_MESSAGE_CONTEST_STATE_UPDATE = "CONTESTLIVEOVERVIEW_CONTEST_STATE_UPDATE";

    public static final String OVERVIEW_TYPE_HISTORY = "history";
    Map<Contest, List<Entry>> entriesForContests = new HashMap<>();
    private List<Entry> entries = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    private ITimeService timeService = new TimeService();

    private ContestLiveOverviewService manager = new ContestLiveOverviewService(timeService);

    @PathParam
    private String type;

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     *
     * @param event
     */
    @Override
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        super.disconnect(event);
        for (Entry entry : entries) {
            DistributedTopic topic = (DistributedTopic) event.getResource().session().getAttribute(GlobalConstants.FANTASY_POINT_UPDATE_ENTRY + entry.getId());
            if (topic != null) {
                topic.stop();
            }
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
        boolean historical = type != null && type.equals(OVERVIEW_TYPE_HISTORY);

        final User user = getCurrentUser(r);
        manager.setUser(user);

        List<ContestState> contestStates = new ArrayList<>();
        List<Lineup> lineups = null;
        if (historical) {
            contestStates.add(ContestState.history);
            Date date = Date.from(timeService.getNow().minus(3, ChronoUnit.DAYS));
            lineups = DaoFactory.getContestDao().findHistoricalLineups(user, ContestState.history, date);
        } else {
            contestStates.add(ContestState.complete);
            contestStates.add(ContestState.active);
            lineups = DaoFactory.getContestDao().findLineups(user, contestStates);
        }

        /*
         * Set up topics to listen to changes on each lineup and contest of interest.
         *
         * For lineups, we're listening for changes to determine how well that lineup is doing in each contest
         * it has entries in.
         *
         * For contests, we're interested in state changes.
         */
        for (Lineup lineup : lineups) {
            List<Contest> contests = DaoFactory.getContestDao().findContests(lineup, null);

            DistributedTopic topic = new DistributedTopic(GlobalConstants.FANTASY_POINT_UPDATE_LINEUP + lineup.getId()) {
                @Override
                public void handleMessage(Message<String> message) {
                    if (r.isCancelled()) {
                        this.stop();
                        return;
                    }

                    try {
                        Lineup updatedLineup = mapper.readValue(message.getMessageObject(), Lineup.class);

                        for (Contest contest : contests) {
                            ClientMessage clientMessage = new ClientMessage();
                            clientMessage.setType(SOCKET_MESSAGE_UPDATE);
                            clientMessage.setJsonPayload(manager.getOverviewLineupAsJson(contest, updatedLineup));
//                            r.getResponse().write(mapper.writeValueAsBytes(clientMessage));
                            writeData(r, mapper.writeValueAsString(clientMessage));
                        }
                    } catch (IOException e) {
                        Logger.error("ERROR: " + e.getMessage(), e);
                        writeData(r, "{error: \"" + e.getMessage() + "\"}");
                    }
                }
            };
            Logger.info("Listening for lineup updates at " + GlobalConstants.FANTASY_POINT_UPDATE_LINEUP + lineup.getId());
            topic.start();

            // Listen for updates on a contest state.
            for (Contest contest : contests) {
                DistributedTopic contestTopic = new DistributedTopic(GlobalConstants.CONTEST_STATE_UPDATE_TOPIC + contest.getId()) {
                    @Override
                    public void handleMessage(Message<String> message) {
                        if (r.isCancelled()) {
                            this.stop();
                            return;
                        }

                        try {
                            ClientMessage clientMessage = new ClientMessage();
                            clientMessage.setType(SOCKET_MESSAGE_CONTEST_STATE_UPDATE);
                            clientMessage.setJsonPayload(message.getMessageObject());
                            writeData(r, mapper.writeValueAsString(clientMessage));
                        }
                        catch(IOException e) {
                            Logger.error("ERROR: " + e.getMessage(), e);
                            writeData(r, "{error: \"" + e.getMessage() + "\"}");
                        }
                    }
                };
                contestTopic.start();
            }
        }

        try {
            ClientMessage clientMessage = new ClientMessage();
            clientMessage.setType(SOCKET_MESSAGE_ALL);
            if (historical) {
                clientMessage.setJsonPayload(manager.getHistoricalOverviewAsJson(user, 3));
            } else {
                clientMessage.setJsonPayload(manager.getOverviewAsJson(user));
            }

            r.getResponse().write(mapper.writeValueAsString(clientMessage));
        } catch (IOException e) {
            Logger.error("ERROR: " + e.getMessage(), e);
            r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
        }
    }
}
