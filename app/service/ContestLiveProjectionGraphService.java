package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.contest.Contest;
import models.contest.ContestState;
import models.contest.Lineup;
import models.stats.predictive.StatsProjectionGraphData;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;


/**
 * Created by dmaclean on 6/26/14.
 */
public class ContestLiveProjectionGraphService extends AbstractCachingService {
    private ObjectMapper mapper = new ObjectMapper();

    @Cacheable(value = "projectionGraph")
    public String getGraphAsJson(Contest contest, Lineup lineup) throws JsonProcessingException {
        if (!contest.getContestState().equals(ContestState.active)
                && !contest.getContestState().equals(ContestState.complete)
                && !contest.getContestState().equals(ContestState.history)) {
            return new ObjectMapper().writeValueAsString(new HashMap<>());
        }
        return mapper.writeValueAsString(new StatsProjectionGraphData(contest, lineup));
    }

    @CacheEvict(value = "projectionGraph")
    public void updateGraph(Contest contest, Lineup lineup) {

    }

    @Override
    @CacheEvict(value = "projectionGraph", allEntries = true)
    public void flushAllCaches() {
    }
}
