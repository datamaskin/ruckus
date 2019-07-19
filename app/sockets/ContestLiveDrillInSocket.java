package sockets;

import auth.AuthenticationException;
import service.ContestLiveDrillinService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import common.ClientMessage;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedTopic;
import models.contest.Contest;
import models.contest.Entry;
import models.contest.Lineup;
import models.contest.LineupSpot;
import models.sports.AthleteSportEventInfo;
import models.sports.SportEvent;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * consolidated feed for projection graph and all detailed live updates
 */
@ManagedService(path = "/ws/contestlive/{contestId}", interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        HeartbeatInterceptor.class,
        SuspendTrackerInterceptor.class
})
public class ContestLiveDrillInSocket extends AbstractSocket {
    public static final String SOCKET_MESSAGE_ALL = "CONTESTLIVEDETAIL_ALL";
    public static final String SOCKET_MESSAGE_ENTRY_UPDATE = "CONTESTLIVEDETAIL_ENTRY_UPDATE";
    public static final String SOCKET_MESSAGE_SPORTEVENT_UPDATE = "CONTESTLIVEDETAIL_SPORTEVENT_UPDATE";
    public static final String SOCKET_MESSAGE_ATHLETESPORTEVENTINFO_UPDATE = "CONTESTLIVEDETAIL_ATHLETESPORTEVENTINFO_UPDATE";
    public static final String SOCKET_MESSAGE_ATHLETE_GENERAL_UPDATE = "CONTESTLIVEDETAIL_ATHLETE_GENERAL_UPDATE";

    private ContestLiveDrillinService manager = new ContestLiveDrillinService();

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<Map<Integer, Integer>> indicatorTypeReference = new TypeReference<Map<Integer, Integer>>() {
    };

    /**
     * The contest id that the client is interested in monitoring.
     */
    @PathParam("contestId")
    private String contestId;

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
        final User user = getCurrentUser(r);
        manager.setUser(user);

        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        if (contest == null) {
            String error = "Invalid contest id " + contestId;
            Logger.error(error);
            r.getResponse().write("{error: " + error + "}");
            return;
        }

        List<Entry> entries = DaoFactory.getContestDao().findEntries(contest);

        /**
         * Set up the topics for entries, lineups, athletes, and sport events.
         */

        /*
         * Entries
         */
        List<AthleteSportEventInfo> athleteSportEventInfoList = new ArrayList<>();
        for (Entry entry : entries) {
            Lineup lineup = entry.getLineup();
            for (LineupSpot lineupSpot : lineup.getLineupSpots()) {
                if(!athleteSportEventInfoList.contains(lineupSpot.getAthleteSportEventInfo())) {
                    athleteSportEventInfoList.add(lineupSpot.getAthleteSportEventInfo());
                }
            }


            DistributedTopic topic = new DistributedTopic(GlobalConstants.FANTASY_POINT_UPDATE_ENTRY + entry.getId()) {
                @Override
                public void handleMessage(Message<String> message) {
                    if (r.isCancelled()) {
                        this.stop();
                        return;
                    }

                    ClientMessage clientMessage = new ClientMessage();
                    clientMessage.setType(SOCKET_MESSAGE_ENTRY_UPDATE);

                    try {
                        // Deserialize string into an Entry object.
//                        Entry updatedEntry = mapper.readValue(message.getMessageObject(), Entry.class);
//                        Logger.info("ENTRY UPDATE: " + updatedEntry.getId() + " " + updatedEntry.getPoints());

                        clientMessage.setJsonPayload(/*manager.getEntryUpdateAsJson(updatedEntry)*/ message.getMessageObject());

                        String json = mapper.writeValueAsString(clientMessage);

                        Logger.debug(json);
                        writeData(r, json);
                    } catch (IOException e) {
                        Logger.error("ERROR: " + e.getMessage(), e);
                        writeData(r, "{error: \"" + e.getMessage() + "\"}");
                    }
                }
            };
            topic.start();
        }

        /*
         * Sport Events
         */
        for (SportEvent sportEvent : contest.getSportEventGrouping().getSportEvents()) {
            DistributedTopic topic = new DistributedTopic(GlobalConstants.FANTASY_POINT_UPDATE_SPORT_EVENT + sportEvent.getId()) {
                @Override
                public void handleMessage(Message<String> message) {
                    if (r.isCancelled()) {
                        this.stop();
                        return;
                    }

                    ClientMessage clientMessage = new ClientMessage();
                    clientMessage.setType(SOCKET_MESSAGE_SPORTEVENT_UPDATE);

                    try {
                        // Deserialize string into a SportEvent object.
//                        SportEvent updatedSportEvent = mapper.readValue(message.getMessageObject(), SportEvent.class);
//                        Logger.info("SPORT EVENT UPDATE: " + updatedSportEvent.getId() + " " + updatedSportEvent.getUnitsRemaining() + " " + updatedSportEvent.getShortDescription());

                        clientMessage.setJsonPayload(/*manager.getSportEventUpdateAsJson(updatedSportEvent)*/ message.getMessageObject());

                        String json = mapper.writeValueAsString(clientMessage);

                        Logger.debug(json);
                        writeData(r, json);
                    } catch (IOException e) {
                        Logger.error("ERROR: " + e.getMessage(), e);
                        writeData(r, "{error: \"" + e.getMessage() + "\"}");
                    }
                }
            };
            topic.start();
        }

        /*
         * AthleteSportEventInfos
         */
        for (AthleteSportEventInfo athleteSportEventInfo : athleteSportEventInfoList) {
            DistributedTopic topic = new DistributedTopic(GlobalConstants.FANTASY_POINT_UPDATE_ATHLETE_DRILLIN + athleteSportEventInfo.getId()) {
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
//                        AthleteSportEventInfo updatedASEI = mapper.readValue(message.getMessageObject(), AthleteSportEventInfo.class);
//                        Logger.info("ASEI UPDATE: " + updatedASEI.getAthlete().getLastName() + " " + updatedASEI.getFantasyPoints().doubleValue());
                        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
                        Map<String, Object> data = mapper.readValue(message.getMessageObject(), typeReference);
                        Logger.info("ASEI UPDATE: " + data.get("firstName") + " " + data.get("lastName") + " - " + data.get("timeline"));
                        Logger.info("ASEI UPDATE: " + data.get("firstName") + " " + data.get("lastName") + " - " + data.get("stats"));

                        clientMessage.setJsonPayload(/*manager.getAthleteSportEventInfoUpdateAsJson(updatedASEI)*/ message.getMessageObject());
//                        Logger.info("\t\tASEI UPDATE: " + clientMessage.getPayload());

                        String json = mapper.writeValueAsString(clientMessage);

                        Logger.debug(json);
                        writeData(r, json);
                    } catch (IOException e) {
                        Logger.error("ERROR: " + e.getMessage(), e);
                        writeData(r, "{error: \"" + e.getMessage() + "\"}");
                    }
                }
            };
            topic.start();

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

                        String json = mapper.writeValueAsString(clientMessage);
//                        Logger.info("ContestLiveDetails ATHLETE GENERAL UPDATE: " + json);

                        Logger.debug(json);
                        writeData(r, json);
                    } catch (IOException e) {
                        Logger.error("ERROR: " + e.getMessage(), e);
                        writeData(r, "{error: \"" + e.getMessage() + "\"}");
                    }
                }
            };
            athleteGeneralUpdateTopic.start();
        }


        try {
            ClientMessage clientMessage = new ClientMessage();
            clientMessage.setType(SOCKET_MESSAGE_ALL);
            clientMessage.setJsonPayload(manager.getInitialLoadAsJson(contest));
            r.getResponse().write(mapper.writeValueAsBytes(clientMessage));
        } catch (IOException e) {
            Logger.error("ERROR: " + e.getMessage(), e);
            r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
        }
    }
}
