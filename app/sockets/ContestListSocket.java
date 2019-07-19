package sockets;

import auth.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import common.ClientMessage;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import distributed.DistributedTopic;
import models.contest.Contest;
import models.contest.ContestState;
import models.contest.ContestType;
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
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;
import service.IContestListService;

import java.io.IOException;
import java.util.*;

@ManagedService(path = "/ws/contests/{contestId}", interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        HeartbeatInterceptor.class,
        SuspendTrackerInterceptor.class
})
public class ContestListSocket extends AbstractSocket {

    @PathParam("contestId")
    private String contestId;

    private DistributedTopic topic;

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     *
     * @param event
     */
    @Override
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        super.disconnect(event);
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
        ClientMessage message = new ClientMessage();
        message.setType("CONTESTS_ALL");
        try {
            JSONArray jsonArray = new JSONArray();
            IContestListService cache = DistributedServices.getContext().getBean("ContestListManager", IContestListService.class);
            if (contestId != null) {
                jsonArray.put(new JSONObject(cache.getContestAsJson(contestId)));
                message.setJsonPayload(jsonArray.toString());
            } else {
                List<Contest> contests = DaoFactory.getContestDao().findContests(ContestState.open);
                List<ContestState> states = new ArrayList<>();
                states.add(ContestState.open);
                User me = getCurrentUser(r);
                List<Contest> myContests = DaoFactory.getContestDao().findContests(me, states);

                List<JSONObject> sorted = new ArrayList<>();
                for (Contest c : contests) {
                    JSONObject jContest = new JSONObject(cache.getContestAsJson(c.getUrlId()));
                    if (myContests.contains(c)) {
                        jContest.put("isEntered", true);
                    }
                    sorted.add(jContest);
                }
                Collections.sort(sorted, new Sorter());
                for (JSONObject jo : sorted) {
                    jsonArray.put(jo);
                }
                message.setJsonPayload(jsonArray.toString());
            }
            //Logger.debug(new ObjectMapper().writeValueAsString(message));
            r.getResponse().write(new ObjectMapper().writeValueAsString(message));
        } catch (Exception e) {
            Logger.error("ERROR: " + e.getMessage(), e);
            r.getResponse().write("{error: \"" + e.getMessage() + "\"}");
        }
        topic = new DistributedTopic(GlobalConstants.CONTEST_UPDATE_TOPIC) {
            @Override
            public void handleMessage(Message<String> tMessage) {
                if (r.isCancelled()) {
                    this.stop();
                    return;
                }
                String responseMessage = tMessage.getMessageObject();
                try {
                    ClientMessage msg = new ObjectMapper().readValue(tMessage.getMessageObject(), ClientMessage.class);
                    if (msg.getType().equals(GlobalConstants.CONTEST_UPDATE)
                            || msg.getType().equals(GlobalConstants.CONTEST_REMOVE)) {
                        JSONObject jMap = new JSONObject(msg.getPayload().toString());
                        List<ContestState> states = new ArrayList<>();
                        states.add(ContestState.open);
                        User me = getCurrentUser(r);
                        List<Contest> myContests = DaoFactory.getContestDao().findContests(me, states);
                        for (Contest c : myContests) {
                            if (c.getUrlId().equals(jMap.get("id"))) {
                                jMap.put("isEntered", true);
                                msg.setJsonPayload(jMap.toString());
                                responseMessage = new ObjectMapper().writeValueAsString(msg);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                r.getResponse().write(responseMessage);
            }
        };
        topic.start();
    }

    private class Sorter implements Comparator<JSONObject> {
        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            try {
                Boolean g1 = o1.getJSONObject("contestType").getString("abbr").equals("GPP");
                Boolean g2 = o2.getJSONObject("contestType").getString("abbr").equals("GPP");
                String a1 = o1.getJSONObject("contestType").getString("abbr");
                String a2 = o2.getJSONObject("contestType").getString("abbr");
                Integer e1 = o1.getInt("currentEntries");
                Integer e2 = o2.getInt("currentEntries");
                Integer c1 = o1.getInt("capacity");
                Integer c2 = o2.getInt("capacity");
                Integer b1 = o1.getInt("entryFee");
                Integer b2 = o2.getInt("entryFee");
                Date st1 = new ObjectMapper().readValue(o1.getString("startTime"), Date.class);
                Date st2 = new ObjectMapper().readValue(o2.getString("startTime"), Date.class);

                if (g1 || g2) {
                    if (g1 && g2) {
                        Integer p1 = o1.getInt("prizePool");
                        Integer p2 = o2.getInt("prizePool");
                        return p2.compareTo(p1);
                    } else {
                        return g2.compareTo(g1);
                    }
                } else if (a1.equals(ContestType.ANONYMOUS_H2H.getAbbr())
                        || a2.equals(ContestType.ANONYMOUS_H2H.getAbbr())) {
                    if (a1.equals(ContestType.ANONYMOUS_H2H.getAbbr())
                            && a2.equals(ContestType.ANONYMOUS_H2H.getAbbr())) {
                        Integer p1 = o1.getInt("entryFee");
                        Integer p2 = o2.getInt("entryFee");
                        return p1.compareTo(p2);
                    } else {
                        return a1.compareTo(a2);
                    }
                } else if (e1 > 0 || e2 > 0) {
                    if (e1 > 0 && e2 > 0) {
                        if (c1.equals(c2)) {
                            if (e1.equals(e2)) {
                                return b1.compareTo(b2);
                            } else {
                                return e2.compareTo(e1);
                            }
                        } else {
                            return c1.compareTo(c2);
                        }
                    } else {
                        return e2.compareTo(e1);
                    }
                } else {
                    //start time / contest type / entry fee / capacity
                    if (st1.equals(st2)) {
                        if (a1.equals(a2)) {
                            if (e1.equals(e2)) {
                                return c1.compareTo(c2);
                            } else {
                                return e2.compareTo(e1);
                            }
                        } else {
                            return a1.compareTo(a2);
                        }
                    } else {
                        return st1.compareTo(st2);
                    }
                }
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            }
            return 0;
        }
    }
}