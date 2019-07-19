package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.ScoringRule;
import models.sports.League;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import play.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * Manager to handle organizing ScoringRule objects into a structure appropriate for the client side, adding the
 * JSON version of the structure to cache, and serving up the JSON when requested.
 */
public class ScoringRulesService extends AbstractCachingService {

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Retrieves a JSON representation of the ScoringRule objects.
     *
     * @return A string containing the entire scoring rules information.
     */
    @Cacheable(value = "scoringRules")
    public String retrieveScoringRulesAsJson() throws JsonProcessingException {
        Map scoringRules = generateFilters();
        String scorintRulesStr = mapper.writeValueAsString(scoringRules);
        return scorintRulesStr;
    }

    /**
     * Retrieve all the scoring rules as a Map from the cache.
     *
     * @return A Map containing Maps of all scoring rules, by league.
     */
    @Cacheable(value = "scoringRulesMap")
    public Map<String, List> retrieveScoringRules() throws JsonProcessingException {
        String rules = retrieveScoringRulesAsJson();

        try {
            TypeReference<Map<String, List>> typeRef = new TypeReference<Map<String, List>>() {
            };

            return mapper.readValue(rules, typeRef);
        } catch (IOException e) {
            Logger.error(e.getMessage());
            return null;
        }
    }

    @Cacheable(value = "scoringRulesMapOfMaps")
    public Map<String, Map<String, BigDecimal>> retrieveScoringRulesAsMaps() throws JsonProcessingException {
        Map<String, List> mapOfLists = retrieveScoringRules();

        Map<String, Map<String, BigDecimal>> mapOfMaps = new HashMap<>();
        for (Map.Entry<String, List> entry : mapOfLists.entrySet()) {
            Map<String, BigDecimal> newMap = new HashMap<>();
            List<Map<String, Object>> list = entry.getValue();
            //Check to see if the name is one of the names we want to pass before adding to the map.
            for (Map<String, Object> map : list) {
                BigDecimal bigDecimal = new BigDecimal((double) map.get("points"));
                bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
                newMap.put((String) map.get("name"), bigDecimal);
            }

            mapOfMaps.put(entry.getKey(), newMap);
        }

        return mapOfMaps;
    }

    /**
     * Generates a map of maps containing scoring rule name/value pairs.  The inner maps are keyed by league name.
     *
     * @return A map of maps containing scoring rules for each league.
     */
    public Map<String, List> generateFilters() {
        Map<String, List> rules = new HashMap<>();

        for (League league : League.ALL_LEAGUES) {
            List<ScoringRule> scoringRuleList = DaoFactory.getContestDao().findScoringRules(league);


            List<Map> leagueRules = new ArrayList<>();
            for (ScoringRule scoringRule : scoringRuleList) {
                Map<String, Object> map = new HashMap<>();

                 map.put("name", scoringRule.getRuleName());
                 map.put("points", scoringRule.getScoringFactor());
                 leagueRules.add(map);
            }

            rules.put(league.getAbbreviation().toLowerCase(), leagueRules);
        }

        return rules;
    }

    //Sets containing the NFL name info to be separated into offense and defense in a more concise manner. (Modified by Gabriel Islas)
    public final static Set<Object> offenseEvents = new HashSet<>(Arrays.asList("Kick Return TD", "Punt Return TD", "Rushing TD", "Receiving TD", "Interception Return TD",
            "Fumble Recovery TD"));
    public final static Set<Object> defenseEvents = new HashSet<>(Arrays.asList("Kick Return TD", "Punt Return TD", "Rushing TD", "Receiving TD", "Interception Return TD",
            "Fumble Recovery TD", "Non-Passing TD", "Passing TD", "2 Point Conversion", "Reception", "Rushing Yards","Receiving Yards", "Passing Yards",
            "Fumble Lost", "Interception"));

    /**
     * Generates a format for a map that ties itself strictly to NFL format for the contest preview.
     *
     * @return A map containing the scoring rules and specified format for just the NFL league.
     */
    public Map<String, List> getNflScoringRules() {
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "scoringRulesMapOfMaps", allEntries = true),
            @CacheEvict(value = "scoringRulesMap", allEntries = true),
            @CacheEvict(value = "scoringRules", allEntries = true)})
    public void flushAllCaches() {}
}
