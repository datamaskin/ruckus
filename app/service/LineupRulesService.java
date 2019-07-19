package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.LineupTemplate;
import models.sports.League;
import models.sports.Position;
import models.sports.Sport;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mgiles on 6/20/14.
 */
public class LineupRulesService extends AbstractCachingService {

    @Cacheable(value = "lineupRules")
    public String getLineupRulesAsJson(String leagueParam) throws JsonProcessingException {
        League league = DaoFactory.getSportsDao().findLeague(leagueParam);
        Map<Integer, Map> positions = new HashMap<>();
        for (LineupTemplate template : DaoFactory.getContestDao().findLineupTemplates(league)) {
            Map<String, Object> position = new HashMap<>();
            position.put("numberOfAthletes", template.getNumberOfAthletes());
            position.put("abbreviation", template.getPosition().getAbbreviation());
            if (league.getSport().equals(Sport.FOOTBALL)
                    && (template.getPosition().equals(Position.FB_RUNNINGBACK)
                    || template.getPosition().equals(Position.FB_TIGHT_END)
                    || template.getPosition().equals(Position.FB_WIDE_RECEIVER))) {
                position.put("flex", true);
            }
            if (league.getSport().equals(Sport.BASEBALL)
                    && template.getPosition().equals(Position.BS_OUTFIELD)) {
                position.put("flex", true);
            }
            positions.put(template.getPosition().getId(), position);
        }
        return new ObjectMapper().writeValueAsString(positions);
    }

    @Override
    @CacheEvict(value = "lineupRules", allEntries = true)
    public void flushAllCaches() {
    }
}
