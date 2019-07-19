package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.Contest;
import models.sports.SportEvent;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgiles on 6/19/14.
 */
public class ContestEventsService extends AbstractCachingService {

    @Cacheable(value = "contestEvents")
    public String getContestEventsAsJson(String contestId) throws JsonProcessingException, JSONException {
        List<Map<Object, Object>> eventList = new ArrayList<>();
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        List<SportEvent> events = contest.getSportEventGrouping().getSportEvents();
        if (events != null && events.size() > 0) {
            for (SportEvent evt : events) {
                Map<Object, Object> eventMap = new HashMap<>();
                JSONObject desc = new JSONObject(evt.getShortDescription());
                JSONObject longDesc = new JSONObject(evt.getDescription());
                eventMap.put("eventId", evt.getStatProviderId());
                eventMap.put("homeId", desc.getString("homeId"));
                eventMap.put("awayId", desc.getString("awayId"));
                eventMap.put("homeTeam", desc.getString("homeTeam"));
                eventMap.put("awayTeam", desc.getString("awayTeam"));
                eventMap.put("startTime", evt.getStartTime());
                eventMap.put("venue", longDesc.getString("venue"));
                eventMap.put("location", longDesc.getString("location"));
                eventList.add(eventMap);
            }
        }
        return new ObjectMapper().writeValueAsString(eventList);
    }

    @CacheEvict(value = "contestEvents")
    public void removeContestEvents(String contestId) {
    }

    @Override
    @CacheEvict(value = "contestEvents", allEntries = true)
    public void flushAllCaches() {}
}
