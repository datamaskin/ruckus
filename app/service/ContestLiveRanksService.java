package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.Contest;
import models.contest.ContestState;
import models.contest.Entry;
import models.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgiles on 6/20/14.
 */
public class ContestLiveRanksService extends AbstractCachingService {

    @Cacheable(value = "liveRank", key = "{#contestId, #user.id}")
    public String getRanksAsJson(String contestId, User user) throws JsonProcessingException {
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        if (!contest.getContestState().equals(ContestState.active)
                && !contest.getContestState().equals(ContestState.complete)
                && !contest.getContestState().equals(ContestState.history)) {
            return new ObjectMapper().writeValueAsString(new ArrayList<>());
        }
        boolean isInThisContest = false;

        List<Entry> entries = DaoFactory.getContestDao().findEntries(contest);
        List<Map> entryList = new ArrayList<>();
        for (Entry e : entries) {
            Map<String, Object> map = new HashMap<>();
            map.put("lineupId", e.getLineup().getId());
            map.put("user", e.getUser().getUserName());
            map.put("entryId", e.getId());
            map.put("unitsRemaining", DaoFactory.getContestDao().calculateUnitsRemaining(e));
            map.put("fpp", e.getPoints());
            if (user.equals(e.getUser())) {
                map.put("isMe", true);
                isInThisContest = true;
            }
            entryList.add(map);
        }
        if (!isInThisContest) {
            return new ObjectMapper().writeValueAsString(new ArrayList<>());
        }
        return new ObjectMapper().writeValueAsString(entryList);
    }

    @CacheEvict(value = "liveRank", allEntries = true)
    public void userJoined(String contestId, User user) {
        //TODO: need to update a topic somewhere. This call will invalidate the cache
    }

    @Caching(evict = {@CacheEvict(value = "liveRank", key = "contestId", allEntries = true)})
    public void updateRanks(String contestId, User user) {
        //TODO: need to update a topic somewhere. This call will invalidate the cache
    }

    @Override
    @CacheEvict(value = "liveRank", allEntries = true)
    public void flushAllCaches() {
    }
}
