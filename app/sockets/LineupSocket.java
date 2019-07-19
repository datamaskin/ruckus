package sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import common.ClientMessage;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import distributed.DistributedTopic;
import models.contest.Contest;
import models.contest.Lineup;
import models.contest.LineupSpot;
import models.sports.AthleteSportEventInfo;
import models.user.User;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import play.Logger;
import service.ContestLiveDrillinService;
import service.ContestLiveOverviewService;
import service.LineupService;

import java.io.IOException;
import java.util.*;

/**
 * This class represents the socket connection that a user makes to retrieve Lineup data and, subsequently, updates
 * on their lineups and the athletes in those lineups.
 *
 *
 */
@ManagedService(path = "/ws/lineups", interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        HeartbeatInterceptor.class,
        SuspendTrackerInterceptor.class
})
public class LineupSocket extends AbstractSocket {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SOCKET_MESSAGE_INITIAL_DATA = "LINEUPS_ALL";
    private static final String SOCKET_MESSAGE_LINEUP_UPDATE = "LINEUPS_LINEUP_UPDATE";
    private static final String SOCKET_MESSAGE_ATHLETESPORTEVENTINFO_UPDATE = "LINEUPS_ATHLETESPORTEVENTINFO_UPDATE";
    private static final String SOCKET_MESSAGE_ATHLETE_GENERAL_UPDATE = "LINEUPS_ATHLETE_GENERAL_UPDATE";
    private static final String SOCKET_MESSAGE_CONTEST_STATE_UPDATE = "LINEUPS_CONTEST_STATE_UPDATE";

    private ContestLiveOverviewService contestLiveOverviewManager = DistributedServices.getContext().getBean("ContestLiveOverviewManager", ContestLiveOverviewService.class);
    private ContestLiveDrillinService contestLiveDrillinManager = DistributedServices.getContext().getBean("ContestLiveDrillinManager", ContestLiveDrillinService.class);

    @Override
    public void onDisconnect(AtmosphereResourceEvent event) {
        super.disconnect(event);
    }

    /**
     * Invoked when the connection as been fully established and suspended, e.g ready for receiving messages.
     *
     * @param r
     */
    @Ready
    public void onReady(final AtmosphereResource r) throws IOException {
        final User user = getCurrentUser(r);
        LineupService lineupManager = DistributedServices.getContext().getBean("LineupManager", LineupService.class);

        try {
            if(user == null) {
                writeData(r, generateErrorResponse("The user's session has expired."));
                return;
            }

            List<Lineup> lineups = lineupManager.getLiveLineups(user);
            Set<Contest> uniqueContests = new HashSet<>();
            for(Lineup lineup: lineups) {
                List<Contest> contests = DaoFactory.getContestDao().findContests(lineup, null);
                uniqueContests.addAll(contests);
                List<AthleteSportEventInfo> athleteSportEventInfoList = retrieveAthleteSportEventInfosFromLineup(lineup);

                /*
                 * Lineup updates
                 */
                DistributedTopic lineupTopic = new DistributedTopic(GlobalConstants.FANTASY_POINT_UPDATE_LINEUP + lineup.getId()) {
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
                                clientMessage.setType(SOCKET_MESSAGE_LINEUP_UPDATE);
                                clientMessage.setJsonPayload(contestLiveOverviewManager.getOverviewLineupAsJson(contest, updatedLineup));
                                writeData(r, mapper.writeValueAsString(clientMessage));
                            }
                        } catch (IOException e) {
                            Logger.error("ERROR: " + e.getMessage(), e);
                            writeData(r, "{error: \"" + e.getMessage() + "\"}");
                        }
                    }
                };
                lineupTopic.start();

                /*
                 * AthleteSportEventInfo updates
                 */
                for (AthleteSportEventInfo athleteSportEventInfo : athleteSportEventInfoList) {
                    DistributedTopic athleteSportEventInfoTopic = new DistributedTopic(GlobalConstants.FANTASY_POINT_UPDATE_ATHLETE + athleteSportEventInfo.getId()) {
                        @Override
                        public void handleMessage(Message<String> message) {
                            if (r.isCancelled()) {
                                this.stop();
                                return;
                            }

                            ClientMessage clientMessage = new ClientMessage();
                            clientMessage.setType(SOCKET_MESSAGE_ATHLETESPORTEVENTINFO_UPDATE);

                            try {
                                // Deserialize string into an AthleteSportEventInfo object.
                                AthleteSportEventInfo updatedASEI = mapper.readValue(message.getMessageObject(), AthleteSportEventInfo.class);
                                clientMessage.setJsonPayload(contestLiveDrillinManager.getAthleteSportEventInfoUpdateAsJson(updatedASEI));
                                writeData(r, mapper.writeValueAsString(clientMessage));
                            } catch (IOException e) {
                                Logger.error("ERROR: " + e.getMessage(), e);
                                writeData(r, "{error: \"" + e.getMessage() + "\"}");
                            }
                        }
                    };
                    athleteSportEventInfoTopic.start();

                    /*
                     * General Athlete Update updates
                     */
                    DistributedTopic athleteGeneralUpdateTopic = new DistributedTopic(GlobalConstants.ATHLETE_GENERAL_UPDATE_TOPIC + athleteSportEventInfo.getId()) {
                        @Override
                        public void handleMessage(Message<String> message) {
                            if (r.isCancelled()) {
                                this.stop();
                                return;
                            }

                            ClientMessage clientMessage = new ClientMessage();
                            clientMessage.setType(SOCKET_MESSAGE_ATHLETE_GENERAL_UPDATE);

                            try {
                                clientMessage.setJsonPayload(message.getMessageObject());
                                writeData(r, mapper.writeValueAsString(clientMessage));
                            } catch (IOException e) {
                                Logger.error("ERROR: " + e.getMessage(), e);
                                writeData(r, "{error: \"" + e.getMessage() + "\"}");
                            }
                        }
                    };
                    athleteGeneralUpdateTopic.start();
                }
            }

            /*
             * Register a topic for each contest.
             */
            for(Contest contest: uniqueContests) {
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

            ClientMessage clientMessage = new ClientMessage();
            clientMessage.setType(SOCKET_MESSAGE_INITIAL_DATA);
            clientMessage.setJsonPayload(lineupManager.generateLiveLineupJson(lineups));
            writeData(r, mapper.writeValueAsString(clientMessage));
        } catch (IOException e) {
            writeData(r, generateErrorResponse("An error occurred while parsing the lineup data."));
        }
    }

    /**
     * Extracts all the AthleteSportEventInfo objects from a lineup.
     *
     * @param lineup        The lineup to extract ASEIs from.
     * @return              The list of ASEIs in the lineup.
     */
    private List<AthleteSportEventInfo> retrieveAthleteSportEventInfosFromLineup(Lineup lineup) {
        List<LineupSpot> lineupSpots = lineup.getLineupSpots();
        List<AthleteSportEventInfo> athleteSportEventInfos = new ArrayList<>();
        for(LineupSpot lineupSpot: lineupSpots) {
            athleteSportEventInfos.add(lineupSpot.getAthleteSportEventInfo());
        }

        return athleteSportEventInfos;
    }

    private String generateErrorResponse(String error) throws IOException {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", error);

        return mapper.writeValueAsString(errorMap);
    }
}
