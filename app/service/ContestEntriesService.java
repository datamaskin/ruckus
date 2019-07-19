package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.Contest;
import models.contest.Entry;
import org.json.JSONException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgiles on 6/19/14.
 */
public class ContestEntriesService extends AbstractCachingService {

    @Cacheable(value = "contestEntries")
    public String getContestEntriesAsJson(String contestId) throws JSONException, JsonProcessingException {
        List<Map<String, Object>> entryList = new ArrayList<>();
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        List<Entry> entries = DaoFactory.getContestDao().findEntries(contest);
        if (entries != null && entries.size() > 0) {
            for (Entry au : entries) {
                Map<String, Object> user = new HashMap<>();
                user.put("userId", au.getUser().getId());
                user.put("userName", au.getUser().getUserName());
                entryList.add(user);
            }
        }
        return new ObjectMapper().writeValueAsString(entryList);
    }

    @CacheEvict(value = "contestEntries")
    public void updateContestEntries(String contestId) {
    }

    @Override
    @CacheEvict(value = "contestEntries", allEntries = true)
    public void flushAllCaches() {}
}
