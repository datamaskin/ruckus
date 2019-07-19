package managers.scoring;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.GlobalConstants;
import service.ScoringRulesService;
import models.sports.League;
import models.contest.ScoringRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 6/11/14.
 */
public class ScoringRulesManagerTest extends BaseTest {
    private ScoringRulesService manager;

//    private Sport football;
//    private Sport baseball;
//    private League nfl;
//    private League mlb;

    @Before
    public void setUp() {
        manager = new ScoringRulesService();

//        Sport.FOOTBALL.save();
//        Sport.BASEBALL.save();
//        League.NFL.save();
//        League.MLB.save();
//        football = new Sport(Sport.FOOTBALL.getId(), Sport.FOOTBALL.getName());
//        Ebean.save(football);
//        baseball = new Sport(Sport.BASEBALL.getId(), Sport.BASEBALL.getName());
//        Ebean.save(baseball);
//        nfl = new League(League.NFL.getId(), football, League.NFL.getName(), League.NFL.getAbbreviation(), League.NFL.getDisplayName(), true);
//        Ebean.save(nfl);
//        mlb = new League(League.MLB.getId(), baseball, League.MLB.getName(), League.MLB.getAbbreviation(), League.MLB.getDisplayName(), true);
//        Ebean.save(mlb);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGenerateFilters() throws JsonProcessingException {
        startHazelcast();

        Ebean.save(new ScoringRule(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, League.NFL, GlobalConstants.SCORING_NFL_PASSING_YARDS_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, League.NFL, GlobalConstants.SCORING_NFL_RUSHING_YARDS_FACTOR));
        Ebean.save(new ScoringRule("homeRun", League.MLB, new BigDecimal("4")));

        Map<String, List> rules = manager.generateFilters();

        assertTrue(rules.size() == 2);

        String json = manager.retrieveScoringRulesAsJson();
        String expected = "{" +
                "\"" + League.MLB.getAbbreviation().toLowerCase() + "\":[" +
                "{\"name\":\"homeRun\",\"points\":4.00}" +
                "]," +
                "\"" + League.NFL.getAbbreviation().toLowerCase() + "\":[" +
                "{\"name\":\"" + GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL + "\",\"points\":0.04}," +
                "{\"name\":\"" + GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL + "\",\"points\":0.10}" +
                "]" +
                "}";
        System.out.println(json);
        System.out.println(expected);
        assertTrue(json.equals(expected));

        rules = manager.retrieveScoringRules();

        List<Map<String, Object>> nflRules = rules.get(League.NFL.getAbbreviation().toLowerCase());
        assertTrue(nflRules.size() == 2);
        assertTrue(nflRules.get(0).get("name").equals(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL)
                && (double)(nflRules.get(0).get("points")) == GlobalConstants.SCORING_NFL_PASSING_YARDS_FACTOR.doubleValue());
        assertTrue(nflRules.get(1).get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL)
                && (double)(nflRules.get(1).get("points")) == GlobalConstants.SCORING_NFL_RUSHING_YARDS_FACTOR.doubleValue());

        List<Map<String, Object>> mlbRules = rules.get(League.MLB.getAbbreviation().toLowerCase());
        assertTrue(mlbRules.size() == 1);
        assertTrue(mlbRules.get(0).get("name").equals("homeRun")
                && (double)(mlbRules.get(0).get("points")) == 4);


        Map<String, Map<String, BigDecimal>> mapForFPTranslation = manager.retrieveScoringRulesAsMaps();
        assertTrue(mapForFPTranslation.size() == 2);

        Map<String, BigDecimal> nflMapRules = mapForFPTranslation.get(League.NFL.getAbbreviation().toLowerCase());
        Map<String, BigDecimal> mlbMapRules = mapForFPTranslation.get(League.MLB.getAbbreviation().toLowerCase());

        assertTrue(nflMapRules.size() == 2);
        assertTrue(nflMapRules.get(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL).compareTo(GlobalConstants.SCORING_NFL_PASSING_YARDS_FACTOR) == 0);
        assertTrue(nflMapRules.get(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL).compareTo(GlobalConstants.SCORING_NFL_RUSHING_YARDS_FACTOR) == 0);

        assertTrue(mlbMapRules.size() == 1);
        assertTrue(mlbMapRules.get("homeRun").compareTo(new BigDecimal("4")) == 0);
    }
}
