package stats.translator;

import service.ScoringRulesService;
import service.edge.TestScoringRulesService;
import common.GlobalConstants;
import models.contest.ScoringRule;
import models.sports.League;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stats.translator.mlb.FantasyPointTranslator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by dmaclean on 7/21/14.
 */
public class FantasyPointTranslatorTest {
    private FantasyPointTranslator fantasyPointTranslator;
    private ScoringRulesService scoringRulesManager;

    @Before
    public void setUp() {


        scoringRulesManager = new TestScoringRulesService();
        fantasyPointTranslator = new FantasyPointTranslator(scoringRulesManager);

        Map<String, List> scoringRules = new HashMap<>();
        List<ScoringRule> scoringRulesForMLB = new ArrayList<>();

        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_SINGLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_SINGLE_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_DOUBLE_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_TRIPLE_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HOMERUN_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_BATTED_IN_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WALK_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HIT_BY_PITCH_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STOLEN_BASE_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL, League.MLB, GlobalConstants.SCORING_MLB_CAUGHT_STEALING_FACTOR));

        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, League.MLB, GlobalConstants.SCORING_MLB_INNING_PITCHED_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STRIKEOUT_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_WIN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WIN_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_EARNED_RUN_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_WALK_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_FACTOR));
        scoringRulesForMLB.add(new ScoringRule(GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL, League.MLB, GlobalConstants.SCORING_MLB_COMPLETE_GAME_FACTOR));

//        List<Map> leagueRules = new ArrayList<>();
//        for(ScoringRule scoringRule: scoringRulesForMLB) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("name", scoringRule.getRuleName());
//            map.put("points", scoringRule.getScoringFactor());
//            leagueRules.add(map);
//        }
        Map<String, BigDecimal> mlbRules = new HashMap<>();
        for(ScoringRule scoringRule: scoringRulesForMLB) {
            mlbRules.put(scoringRule.getRuleName(), scoringRule.getScoringFactor());
        }

        Map<String, Map<String, BigDecimal>> allRules = new HashMap<>();
        allRules.put(League.MLB.getAbbreviation().toLowerCase(), mlbRules);
    }

    @After
    public void tearDown() {
        scoringRulesManager = null;
        fantasyPointTranslator = null;
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_1() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 1);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        assertTrue(fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData).isEmpty());
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_2() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 2);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        assertTrue(fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData).isEmpty());
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_3() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 3);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        assertTrue(fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData).isEmpty());
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_4() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 4);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("3")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_4_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 4);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("5")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_6() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 6);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("5")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_6_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 6);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("7")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }
    @Test
    public void testDetermineFantasyPointIncrementForEvent_7() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 7);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("8")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_7_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 7);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("10")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }
    @Test
    public void testDetermineFantasyPointIncrementForEvent_8() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 8);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("10")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-2.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_8_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 8);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("12")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-2.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_9() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 9);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.isEmpty());
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_11() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 11);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_12() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 12);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_13() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 13);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_13_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 13);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("4")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_14() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 14);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_14_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 14);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("4")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_15() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 15);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_15_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 15);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("4")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("-0.6")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_16() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 16);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("2.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_17() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 17);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.isEmpty());
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_18() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 18);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.isEmpty());
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_19() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 19);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("1.5")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_19_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 19);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("1.5")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_20() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 20);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_20_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 20);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_21() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 21);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("2")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_21_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 21);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("2")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_22() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 22);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("2")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_22_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 22);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("2")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_24() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 24);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_24_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 24);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_25() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 25);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_25_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 25);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_26() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 26);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_26_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 26);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_27() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 27);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_27_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 27);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_28() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 28);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_28_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 28);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_29() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 29);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_29_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 29);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_30() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 30);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_30_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 30);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_31() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 31);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_31_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 31);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_32() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 32);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_32_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 32);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_33() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 33);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_33_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 33);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_34() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 34);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_34_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 34);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_35() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 35);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_35_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 35);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_36() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 36);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_36_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 36);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_37() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 37);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_37_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 37);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_38() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 38);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_38_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 38);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("0.75")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_41() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 41);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 0);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("0")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("1.5")) == 0);
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_41_rbi() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 41);
        eventData.put("outsBefore", 0);
        eventData.put("outsAfter", 0);
        eventData.put("rbi", 1);

        List<BigDecimal> fantasyPoints = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        assertTrue(fantasyPoints.get(0).compareTo(new BigDecimal("2")) == 0);
        assertTrue(fantasyPoints.get(1).compareTo(new BigDecimal("1.5")) == 0);
    }
}
