package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.Lineup;
import models.contest.LineupSpot;
import models.sports.Team;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgiles on 6/21/14.
 */
public class ContestLiveLineupService extends AbstractCachingService {

    @Cacheable(value = "liveLineup")
    public String getLinupAsJson(int lineupId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};

        Lineup lineup = DaoFactory.getContestDao().findLineup(lineupId);
        List<Map> lineupList = new ArrayList<>();
        for (LineupSpot spot : lineup.getLineupSpots()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", spot.getAthlete().getStatProviderId());
            map.put("athleteSportEventInfoId", spot.getAthleteSportEventInfo().getId());
            map.put("position", spot.getPosition().getAbbreviation());
            map.put("firstName", spot.getAthlete().getFirstName());
            map.put("lastName", spot.getAthlete().getLastName());
            //TODO: need data on an athletes live activity. Perhaps through AthleteSportsEventInfo?
            map.put("unitsRemaining", spot.getAthleteSportEventInfo().getSportEvent().getUnitsRemaining());
            map.put("stats", spot.getAthleteSportEventInfo().determineStatsForDisplay());
            map.put("fpp", spot.getAthleteSportEventInfo().getFantasyPoints());
            map.put("indicator", spot.getAthleteSportEventInfo().getIndicator());

            List<Team> teams = spot.getAthleteSportEventInfo().getSportEvent().getTeams();
            map.put("matchup", spot.getAthleteSportEventInfo().determineMatchupDisplay());

            lineupList.add(map);
        }
        return new ObjectMapper().writeValueAsString(lineupList);
    }

    @CacheEvict(value = "liveLineup")
    public void updateLineup(int lineupId) {
        //TODO: need to update a topic somewhere. This call will invalidate the cache
    }

    @Override
    @CacheEvict(value = "liveLineup", allEntries = true)
    public void flushAllCaches() {}
}
