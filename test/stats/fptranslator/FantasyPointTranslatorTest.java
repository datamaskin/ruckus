package stats.fptranslator;

import service.edge.TestScoringRulesService;
import common.GlobalConstants;
import stats.translator.nfl.FantasyPointTranslator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 6/9/14.
 */
public class FantasyPointTranslatorTest {
    private FantasyPointTranslator translator;

    @Before
    public void setUp() {
        translator = new FantasyPointTranslator(new TestScoringRulesService());
    }

    @After
    public void tearDown() {
        translator = null;
    }

    @Test
    public void testCalculateFantasyPoints() {
        Map<String, BigDecimal> stats = new HashMap<>();

        /*
         * Sack
         */
        stats.put(GlobalConstants.SCORING_NFL_SACK_LABEL, new BigDecimal(1));
        assertTrue(translator.calculateFantasyPoints(stats).compareTo(GlobalConstants.SCORING_NFL_SACK_FACTOR) == 0);

        /*
         * Interception
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL, new BigDecimal(1));
        assertTrue(translator.calculateFantasyPoints(stats).compareTo(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_FACTOR) == 0);

        /*
         * Fumble Recovery
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL, new BigDecimal(1));
        assertTrue(translator.calculateFantasyPoints(stats).compareTo(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_FACTOR) == 0);

        /*
         * Interception Return TD
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL, new BigDecimal(1));
        assertTrue(translator.calculateFantasyPoints(stats).compareTo(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_FACTOR) == 0);

        /*
         * Fumble Recovery TD
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL, new BigDecimal(1));
        assertTrue(translator.calculateFantasyPoints(stats).compareTo(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_FACTOR) == 0);

        /*
         * Blocked Punt or FG Return TD
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL, new BigDecimal(1));
        assertTrue(translator.calculateFantasyPoints(stats).compareTo(GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_FACTOR) == 0);

        /*
         * Safety
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_SAFETY_LABEL, new BigDecimal(1));
        assertTrue(translator.calculateFantasyPoints(stats).compareTo(GlobalConstants.SCORING_NFL_SAFETY_FACTOR) == 0);

        /*
         * Blocked Kick
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL, new BigDecimal(1));
        assertTrue(translator.calculateFantasyPoints(stats).compareTo(GlobalConstants.SCORING_NFL_BLOCKED_KICK_FACTOR) == 0);

        /*
         * 0 Points Allowed
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(1));
        assertEquals(new BigDecimal("11.5"), translator.calculateFantasyPoints(stats));

        /*
         * 1-6 Points Allowed
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(3));
        assertEquals(new BigDecimal("10.5"), translator.calculateFantasyPoints(stats));

        /*
         * 7-13 Points Allowed
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(10));
        assertEquals(new BigDecimal("7.0"), translator.calculateFantasyPoints(stats));

        /*
         * 14-20 Points Allowed
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(18));
        assertEquals(new BigDecimal("3.0"), translator.calculateFantasyPoints(stats));

        /*
         * 21-27 Points Allowed
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(25));
        assertEquals(new BigDecimal("-0.5"), translator.calculateFantasyPoints(stats));

        /*
         * 28-34 Points Allowed
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(30));
        assertEquals(new BigDecimal("-3.0"), translator.calculateFantasyPoints(stats));

        /*
         * 35+ Points Allowed
         */
        stats.clear();
        stats.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(40));
        assertEquals(new BigDecimal("-8.0"), translator.calculateFantasyPoints(stats));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_1_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 1);
        eventData.put("yards", 9);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("0.9"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_1_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 1);
        eventData.put("yards", 9);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("6.9"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_2_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 2);
        eventData.put("yards", 9);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("0.36"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_2_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 2);
        eventData.put("yards", 9);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("4.36"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_4_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 4);
        eventData.put("yards", 9);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_4_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 4);
        eventData.put("yards", 9);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("6"), result.get(0));
        assertEquals(new BigDecimal("6"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_5_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 5);
        eventData.put("yards", 9);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_5_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 5);
        eventData.put("yards", 0);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("6"), result.get(0));
        assertEquals(new BigDecimal("6"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_6() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 6);
        eventData.put("yards", 0);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("-1"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_8() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 6);
        eventData.put("yards", 0);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("-1"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_9() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 9);
        eventData.put("yards", 0);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("4"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_10() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 10);
        eventData.put("yards", 9);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("2"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_11() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 11);
        eventData.put("yards", 9);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("2"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_13_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 13);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_13_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 13);
        eventData.put("yards", 2);
        eventData.put("points", 2);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("2"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_14_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 14);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_14_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 14);
        eventData.put("yards", 2);
        eventData.put("points", 2);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("2"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_17_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 17);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("0.7"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_17_Score_TwoPointConversion() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 17);
        eventData.put("yards", 0);
        eventData.put("points", 2);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("2.5"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_17_Score_Touchdown() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 17);
        eventData.put("yards", 2);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("6.7"), result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_21() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 21);
        eventData.put("yards", 0);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("1"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_23_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 23);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("2"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_23_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 23);
        eventData.put("yards", 2);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("8"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_29_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 29);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_29_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 29);
        eventData.put("yards", 2);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("6"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_30_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 30);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_30_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 30);
        eventData.put("yards", 2);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("6"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_31_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 31);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_31_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 31);
        eventData.put("yards", 2);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 456);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(new BigDecimal("6"), result.get(0));
        assertEquals(new BigDecimal("6"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_32_NoPossessionChange() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 32);
        eventData.put("eventType", 8);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 123);
        eventData.put("possessionAfter", 123);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_32_NoScore() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 32);
        eventData.put("eventType", 8);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 123);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("2"), result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_32_NoLostFumbleOnKickoff() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 32);
        eventData.put("eventType", 7);
        eventData.put("yards", 2);
        eventData.put("points", 0);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 123);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(BigDecimal.ZERO, result.get(1));
    }

    @Test
    public void testDetermineFantasyPointIncrementForEvent_32_Score() {
        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", 32);
        eventData.put("eventType", 8);
        eventData.put("yards", 2);
        eventData.put("points", 6);
        eventData.put("defenseId", 123);
        eventData.put("possessionBefore", 123);
        eventData.put("possessionAfter", 456);

        List<BigDecimal> result = translator.determineFantasyPointIncrementForEvent(eventData);
        assertEquals(BigDecimal.ZERO, result.get(0));
        assertEquals(new BigDecimal("8"), result.get(1));
    }
}
