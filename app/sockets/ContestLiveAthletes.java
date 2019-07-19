package sockets;

import auth.AuthenticationException;
import service.ContestLiveAthleteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import common.ClientMessage;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import distributed.DistributedTopic;
import models.contest.Contest;
import models.contest.ContestState;
import models.sports.AthleteSportEventInfo;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Socket endpoint for pushing updates for individual athleteSportEventInfos.
 */
@ManagedService(path = "/ws/contestliveathletes/{contestId}", interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        HeartbeatInterceptor.class,
        SuspendTrackerInterceptor.class
})
public class ContestLiveAthletes extends AbstractSocket {

    public static final String SOCKET_MESSAGE_ATHLETE_GENERAL_UPDATE = "CONTESTATHLETE_ATHLETE_GENERAL_UPDATE";
    public static final String SOCKET_MESSAGE_CONTEST_ATHLETE_UPDATE = "CONTESTATHLETE_UPDATE";

    /**
     * The list of athleteSportEventInfos we're interested in monitoring.
     */
    private List<AthleteSportEventInfo> athleteSportEventInfos;

    private ContestLiveAthleteService contestLiveAthleteManager;

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

        for (AthleteSportEventInfo athleteSportEventInfo : athleteSportEventInfos) {
            DistributedTopic topic = (DistributedTopic) event.getResource().session().getAttribute(GlobalConstants.FANTASY_POINT_UPDATE_ATHLETE + athleteSportEventInfo.getAthlete().getStatProviderId());
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
        ObjectMapper mapper = new ObjectMapper();

        final User user = getCurrentUser(r);

        contestLiveAthleteManager = DistributedServices.getContext().getBean("ContestLiveAthleteManager", ContestLiveAthleteService.class);

        Contest contest;
        if (contestId != null) {
            contest = DaoFactory.getContestDao().findContest(contestId);
            athleteSportEventInfos = DaoFactory.getSportsDao().findAthleteSportEventInfos(contest);
        } else {
            List<ContestState> contestStates = new ArrayList<>();
            contestStates.add(ContestState.active);
            List<Contest> contests = DaoFactory.getContestDao().findContests(user, contestStates);
            for (Contest c : contests) {
                athleteSportEventInfos.addAll(DaoFactory.getSportsDao().findAthleteSportEventInfos(c));

                // Remove duplicates
                Set<AthleteSportEventInfo> aseiSet = new HashSet<>(athleteSportEventInfos);
                athleteSportEventInfos.clear();
                athleteSportEventInfos.addAll(aseiSet);
            }
        }


        for (AthleteSportEventInfo athleteSportEventInfo : athleteSportEventInfos) {
            DistributedTopic athleteUpdateTopic = new DistributedTopic(GlobalConstants.FANTASY_POINT_UPDATE_ATHLETE + athleteSportEventInfo.getId()) {

                @Override
                public void handleMessage(Message<String> message) {
                    if (r.isCancelled()) {
                        this.stop();
                        return;
                    }

                    ClientMessage clientMessage = new ClientMessage();
                    clientMessage.setType(SOCKET_MESSAGE_CONTEST_ATHLETE_UPDATE);

                    try {
                        // Deserialize string into an AthleteSportEventInfo object.
//                        AthleteSportEventInfo updatedASEI = mapper.readValue(message.getMessageObject(), AthleteSportEventInfo.class);
//                        String data = contestLiveAthleteManager.createJsonForAthleteSportEventInfo(user, updatedASEI, false);


                        clientMessage.setJsonPayload(/*data*/ message.getMessageObject());

                        String json = mapper.writeValueAsString(clientMessage);

                        Logger.debug(json);
//                        r.getResponse().write(json);
                        writeData(r, json);
                    } catch (IOException e) {
                        Logger.error("ERROR: " + e.getMessage(), e);
//                        r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
                        writeData(r, "{error: \"" + e.getMessage() + "\"}");
                    }
                }
            };
            athleteUpdateTopic.start();


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
                        clientMessage.setPayload(message.getMessageObject());

                        String json = mapper.writeValueAsString(clientMessage);
                        Logger.info("ContestLiveAthlete ATHLETE GENERAL UPDATE: " + json);

                        Logger.debug(json);
//                        r.getResponse().write(json);
                        writeData(r, json);
                    } catch (IOException e) {
                        Logger.error("ERROR: " + e.getMessage(), e);
//                        r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
                        writeData(r, "{error: \"" + e.getMessage() + "\"}");
                    }
                }
            };
            athleteGeneralUpdateTopic.start();
        }
    }
}
