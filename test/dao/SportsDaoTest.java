package dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import models.sports.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 7/17/14.
 */
public class SportsDaoTest extends BaseTest {
    private String BOXSCORE_JSON_FIELD_NAME = "name";
    private String BOXSCORE_JSON_FIELD_AMOUNT = "amount";
    private String BOXSCORE_JSON_FIELD_FPP = "fpp";
    private String BOXSCORE_JSON_FIELD_ABBR = "abbr";
    private String BOXSCORE_JSON_FIELD_ID = "id";

    private SportsDao sportsDao;

    Date startTime;
    Date startTimeLastYear;
    Date startTimeTwoYearsAgo;

    @Before
    public void setUp() {
        sportsDao = new SportsDao();

        startTime = Date.from(Instant.now());
        startTimeLastYear = Date.from(ZonedDateTime.now().withYear(startTime.getYear()-1).toInstant());
        startTimeTwoYearsAgo = Date.from(ZonedDateTime.now().withYear(startTime.getYear()-2).toInstant());
    }

    @After
    public void tearDown() {
        sportsDao = null;
    }

    @Test
    public void testCreateInitialJsonForAthleteBoxscore_MLB_Pitcher() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};

        String result = sportsDao.createInitialJsonForAthleteBoxscore(Position.BS_PITCHER);
        try {
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);
            assertTrue(data.size() == 8);

            for(Map<String, Object> entry: data) {
                boolean found = false;
                for (int i = 0; i < GlobalConstants.STATS_ARRAY_FOR_MLB_PITCHER.length; i++) {
                    if(entry.get(BOXSCORE_JSON_FIELD_NAME).equals(GlobalConstants.STATS_ARRAY_FOR_MLB_PITCHER[i])) {
                        found = true;
                        assertTrue((Integer) entry.get(BOXSCORE_JSON_FIELD_AMOUNT) == 0);
                        assertTrue((Integer) entry.get(BOXSCORE_JSON_FIELD_FPP) == 0);
                    }
                }

                if(!found) {
                    fail("Did not find anything for " + entry.get(BOXSCORE_JSON_FIELD_NAME));
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateInitialJsonForAthleteBoxscore_MLB_Batter() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};

        String result = sportsDao.createInitialJsonForAthleteBoxscore(Position.BS_FIRST_BASE);
        try {
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);
            assertTrue(data.size() == 10);
            String[] statsForBatters = {
                    GlobalConstants.SCORING_MLB_DOUBLE_LABEL,
                    GlobalConstants.SCORING_MLB_TRIPLE_LABEL,
                    GlobalConstants.SCORING_MLB_HOMERUN_LABEL,
                    GlobalConstants.SCORING_MLB_SINGLE_LABEL,
                    GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL,
                    GlobalConstants.SCORING_MLB_RUN_LABEL,
                    GlobalConstants.SCORING_MLB_WALK_LABEL,
                    GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL,
                    GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL,
                    GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL
            };

            for(Map<String, Object> entry: data) {
                boolean found = false;
                for (int i = 0; i < statsForBatters.length; i++) {
                    if(entry.get(BOXSCORE_JSON_FIELD_NAME).equals(statsForBatters[i])) {
                        found = true;
                        assertTrue((Integer) entry.get(BOXSCORE_JSON_FIELD_AMOUNT) == 0);
                        assertTrue((Integer) entry.get(BOXSCORE_JSON_FIELD_FPP) == 0);
                    }
                }

                if(!found) {
                    fail("Did not find anything for " + entry.get(BOXSCORE_JSON_FIELD_NAME));
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateInitialJsonForAthleteBoxscore_NFL_Offense() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};

        String result = sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK);
        try {
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);
            assertTrue(data.size() == 11);

            for(Map<String, Object> entry: data) {
                boolean found = false;
                for (int i = 0; i < GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE.length; i++) {
                    if(entry.get(BOXSCORE_JSON_FIELD_NAME).equals(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i])) {
                        found = true;
                        assertTrue((Integer) entry.get(BOXSCORE_JSON_FIELD_AMOUNT) == 0);
                        assertTrue((Integer) entry.get(BOXSCORE_JSON_FIELD_FPP) == 0);
                        assertTrue(entry.get(BOXSCORE_JSON_FIELD_ABBR).equals(GlobalConstants.SCORING_NFL_NAME_TO_ABBR_MAP.get(entry.get(BOXSCORE_JSON_FIELD_NAME))));
                        assertEquals(GlobalConstants.SCORING_NFL_NAME_TO_ID_MAP.get(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i]), entry.get(BOXSCORE_JSON_FIELD_ID));
                    }
                }

                if(!found) {
                    fail("Did not find anything for " + entry.get(BOXSCORE_JSON_FIELD_NAME));
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateInitialJsonForAthleteBoxscore_NFL_Defense() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};

        String result = sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE);
        try {
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);
            assertEquals(9, data.size());

            for(Map<String, Object> entry: data) {
                boolean found = false;
                for (int i = 0; i < GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE.length; i++) {
                    if(entry.get(BOXSCORE_JSON_FIELD_NAME).equals(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i])) {
                        found = true;
                        assertTrue((Integer) entry.get(BOXSCORE_JSON_FIELD_AMOUNT) == 0);
                        assertTrue((Integer) entry.get(BOXSCORE_JSON_FIELD_FPP) == 0);
                        assertTrue(entry.get(BOXSCORE_JSON_FIELD_ABBR).equals(GlobalConstants.SCORING_NFL_NAME_TO_ABBR_MAP.get(entry.get(BOXSCORE_JSON_FIELD_NAME))));
                        assertEquals(GlobalConstants.SCORING_NFL_NAME_TO_ID_MAP.get(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i]), entry.get(BOXSCORE_JSON_FIELD_ID));
                    }
                }

                if(!found) {
                    fail("Did not find anything for " + entry.get(BOXSCORE_JSON_FIELD_NAME));
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

//    @Test
//    public void testFindPreviousSportEvent() {
//        SportEvent sportEvent = new SportEvent(123, League.NFL, startTime, "", "", 60, false, 2014, 1, 1);
//        SportEvent sportEvent2 = new SportEvent(1234, League.NFL, startTimeLastYear, "", "", 60, false, 2014, 1, 1);
//        SportEvent sportEvent3 = new SportEvent(1235, League.NFL, startTimeTwoYearsAgo, "", "", 60, false, 2014, 1, 1);
//
//        sportsDao.saveSportEvent(sportEvent);
//        sportsDao.saveSportEvent(sportEvent2);
//        sportsDao.saveSportEvent(sportEvent3);
//
//        Athlete athlete = new Athlete(1, "Test", "User", null, "1");
//        sportsDao.saveAthlete(athlete);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete, BigDecimal.ZERO, "", "");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//        AthleteSportEventInfo athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent2, athlete, BigDecimal.ZERO, "", "");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo2);
//        AthleteSportEventInfo athleteSportEventInfo3 = new AthleteSportEventInfo(sportEvent3, athlete, BigDecimal.ZERO, "", "");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo3);
//
//        AthleteSportEventInfo previous = sportsDao.findPreviousAthleteSportEventInfo(athleteSportEventInfo);
//        assertEquals(athleteSportEventInfo2.getId(), previous.getId());
//    }
//
//    @Test
//    public void testCalculateFantasyPointsPerGameNFL() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Athlete athlete = new Athlete(1, "", "New England Patriots", patriots, "");
//        athlete.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(athlete);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        StatsNflDefenseByEvent statsNflDefenseByEvent = new StatsNflDefenseByEvent();
//        statsNflDefenseByEvent.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflDefenseByEvent.setAthlete(athlete);
//        statsNflDefenseByEvent.setSportEvent(sportEvent);
//        statsNflDefenseByEvent.setStartTime(startTimeLastYear);
//        statsNflDefenseByEvent.setFppInThisEvent(new BigDecimal("10"));
//        Ebean.save(statsNflDefenseByEvent);
//
//        StatsNflDefenseByEvent statsNflDefenseByEvent2 = new StatsNflDefenseByEvent();
//        statsNflDefenseByEvent2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflDefenseByEvent2.setAthlete(athlete);
//        statsNflDefenseByEvent2.setSportEvent(sportEvent);
//        statsNflDefenseByEvent2.setStartTime(startTimeTwoYearsAgo);
//        statsNflDefenseByEvent2.setFppInThisEvent(new BigDecimal("8"));
//        Ebean.save(statsNflDefenseByEvent2);
//
//        assertEquals(new BigDecimal("9.00"), sportsDao.calculateFantasyPointsPerGameNFL(new StatsNFLFantasyPointTranslator(new TestScoringRulesManager()), new TimeService(),
//                athleteSportEventInfo, 17, new HashMap<>()));
//    }
//
//    @Test
//    public void testCalculateRankNfl_defense() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athletePatriots = new Athlete(111, "", "New England Patriots", patriots, "");
//        athletePatriots.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(athletePatriots);
//
//        Athlete athleteRavens = new Athlete(112, "", "Baltimore Ravens", ravens, "");
//        athleteRavens.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(athleteRavens);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athletePatriots, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        AthleteSportEventInfo athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent, athleteRavens, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        StatsNflDefenseByEvent statsNflDefenseByEvent = new StatsNflDefenseByEvent();
//        statsNflDefenseByEvent.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflDefenseByEvent.setAthlete(athletePatriots);
//        statsNflDefenseByEvent.setSportEvent(sportEvent);
//        statsNflDefenseByEvent.setStartTime(startTimeLastYear);
//        statsNflDefenseByEvent.setFppInThisEvent(new BigDecimal("10"));
//        Ebean.save(statsNflDefenseByEvent);
//
//        StatsNflDefenseByEvent statsNflDefenseByEvent2 = new StatsNflDefenseByEvent();
//        statsNflDefenseByEvent2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflDefenseByEvent2.setAthlete(athletePatriots);
//        statsNflDefenseByEvent2.setSportEvent(sportEvent);
//        statsNflDefenseByEvent2.setStartTime(startTimeTwoYearsAgo);
//        statsNflDefenseByEvent2.setFppInThisEvent(new BigDecimal("8"));
//        Ebean.save(statsNflDefenseByEvent2);
//
//        StatsNflDefenseByEvent statsNflDefenseByEvent3 = new StatsNflDefenseByEvent();
//        statsNflDefenseByEvent3.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflDefenseByEvent3.setAthlete(athleteRavens);
//        statsNflDefenseByEvent3.setSportEvent(sportEvent);
//        statsNflDefenseByEvent3.setStartTime(startTimeLastYear);
//        statsNflDefenseByEvent3.setFppInThisEvent(new BigDecimal("8"));
//        Ebean.save(statsNflDefenseByEvent3);
//
//        StatsNflDefenseByEvent statsNflDefenseByEvent4 = new StatsNflDefenseByEvent();
//        statsNflDefenseByEvent4.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflDefenseByEvent4.setAthlete(athleteRavens);
//        statsNflDefenseByEvent4.setSportEvent(sportEvent);
//        statsNflDefenseByEvent4.setStartTime(startTimeTwoYearsAgo);
//        statsNflDefenseByEvent4.setFppInThisEvent(new BigDecimal("6"));
//        Ebean.save(statsNflDefenseByEvent4);
//
//        Map<String, Map<Integer, Double>> rank = new HashMap<>();
//        int[] result = sportsDao.calculateRankNfl(Position.FB_DEFENSE, new StatsNFLFantasyPointTranslator(new TestScoringRulesManager()), athleteSportEventInfo, rank);
//        assertEquals(1, result[0]);
//        assertEquals(2, result[1]);
//
//        result = sportsDao.calculateRankNfl(Position.FB_DEFENSE, new StatsNFLFantasyPointTranslator(new TestScoringRulesManager()), athleteSportEventInfo2, rank);
//        assertEquals(2, result[0]);
//        assertEquals(2, result[1]);
//    }
//
//    @Test
//    public void testCalculateStatAverages_NFL_Defense() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athletePatriots = new Athlete(111, "", "New England Patriots", patriots, "");
//        athletePatriots.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(athletePatriots);
//
//        Athlete athleteRavens = new Athlete(112, "", "Baltimore Ravens", ravens, "");
//        athleteRavens.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(athleteRavens);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athletePatriots, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        StatsNflDefenseByEvent statsNflDefenseByEvent = new StatsNflDefenseByEvent();
//        statsNflDefenseByEvent.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflDefenseByEvent.setAthlete(athletePatriots);
//        statsNflDefenseByEvent.setSportEvent(sportEvent);
//        statsNflDefenseByEvent.setStartTime(startTimeLastYear);
//        statsNflDefenseByEvent.setFppInThisEvent(new BigDecimal("10"));
//        statsNflDefenseByEvent.setPointsAllowed(20);
//        statsNflDefenseByEvent.setBlockedPuntOrFieldGoalReturnTouchdowns(0);
//        statsNflDefenseByEvent.setFumbleRecoveryTouchdowns(1);
//        statsNflDefenseByEvent.setInterceptionReturnTouchdowns(2);
//        statsNflDefenseByEvent.setKickReturnTouchdowns(4);
//        statsNflDefenseByEvent.setPuntReturnTouchdowns(1);
//        statsNflDefenseByEvent.setSafeties(2);
//        statsNflDefenseByEvent.setInterceptions(6);
//        statsNflDefenseByEvent.setFumbleRecoveries(15);
//        statsNflDefenseByEvent.setBlockedKicks(6);
//        statsNflDefenseByEvent.setSacks(20);
//        Ebean.save(statsNflDefenseByEvent);
//
//        StatsNflDefenseByEvent statsNflDefenseByEvent2 = new StatsNflDefenseByEvent();
//        statsNflDefenseByEvent2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflDefenseByEvent2.setAthlete(athletePatriots);
//        statsNflDefenseByEvent2.setSportEvent(sportEvent);
//        statsNflDefenseByEvent2.setStartTime(startTimeTwoYearsAgo);
//        statsNflDefenseByEvent2.setFppInThisEvent(new BigDecimal("8"));
//        statsNflDefenseByEvent2.setPointsAllowed(10);
//        statsNflDefenseByEvent2.setBlockedPuntOrFieldGoalReturnTouchdowns(2);
//        statsNflDefenseByEvent2.setFumbleRecoveryTouchdowns(0);
//        statsNflDefenseByEvent2.setInterceptionReturnTouchdowns(4);
//        statsNflDefenseByEvent2.setKickReturnTouchdowns(2);
//        statsNflDefenseByEvent2.setPuntReturnTouchdowns(0);
//        statsNflDefenseByEvent2.setSafeties(4);
//        statsNflDefenseByEvent2.setInterceptions(2);
//        statsNflDefenseByEvent2.setFumbleRecoveries(5);
//        statsNflDefenseByEvent2.setBlockedKicks(0);
//        statsNflDefenseByEvent2.setSacks(10);
//        Ebean.save(statsNflDefenseByEvent2);
//
//        Map<String, BigDecimal> averages = sportsDao.calculateStatAverages(athleteSportEventInfo, 17);
//
//        int index = 0;
//        for(Map.Entry<String, BigDecimal> entry: averages.entrySet()) {
//            if(index == 0) {
//                assertEquals(GlobalConstants.STATS_NFL_POINTS_ALLOWED, entry.getKey());
//                assertEquals(new BigDecimal("15.00"), entry.getValue());
//            }
//            else if(index == 1) {
//                assertEquals(GlobalConstants.STATS_NFL_DEFENSIVE_TOUCHDOWNS, entry.getKey());
//                assertEquals(new BigDecimal("8.00"), entry.getValue());
//            }
//            else if(index == 2) {
//                assertEquals(GlobalConstants.STATS_NFL_SAFETIES, entry.getKey());
//                assertEquals(new BigDecimal("3.00"), entry.getValue());
//            }
//            else if(index == 3) {
//                assertEquals(GlobalConstants.STATS_NFL_INTERCEPTIONS, entry.getKey());
//                assertEquals(new BigDecimal("4.00"), entry.getValue());
//            }
//            else if(index == 4) {
//                assertEquals(GlobalConstants.STATS_NFL_FUMBLE_RECOVERIES, entry.getKey());
//                assertEquals(new BigDecimal("10.00"), entry.getValue());
//            }
//            else if(index == 5) {
//                assertEquals(GlobalConstants.STATS_NFL_BLOCKED_KICKS, entry.getKey());
//                assertEquals(new BigDecimal("3.00"), entry.getValue());
//            }
//            else if(index == 6) {
//                assertEquals(GlobalConstants.STATS_NFL_SACKS, entry.getKey());
//                assertEquals(new BigDecimal("15.00"), entry.getValue());
//            }
//
//            index++;
//        }
//    }
//
//    @Test
//    public void testCalculateStatAverages_NFL_Defense_NoStats() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athletePatriots = new Athlete(111, "", "New England Patriots", patriots, "");
//        athletePatriots.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(athletePatriots);
//
//        Athlete athleteRavens = new Athlete(112, "", "Baltimore Ravens", ravens, "");
//        athleteRavens.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(athleteRavens);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athletePatriots, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        Map<String, BigDecimal> averages = sportsDao.calculateStatAverages(athleteSportEventInfo, 17);
//
//        int index = 0;
//        for(Map.Entry<String, BigDecimal> entry: averages.entrySet()) {
//            if(index == 0) {
//                assertEquals(GlobalConstants.STATS_NFL_POINTS_ALLOWED, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 1) {
//                assertEquals(GlobalConstants.STATS_NFL_DEFENSIVE_TOUCHDOWNS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 2) {
//                assertEquals(GlobalConstants.STATS_NFL_SAFETIES, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 3) {
//                assertEquals(GlobalConstants.STATS_NFL_INTERCEPTIONS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 4) {
//                assertEquals(GlobalConstants.STATS_NFL_FUMBLE_RECOVERIES, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 5) {
//                assertEquals(GlobalConstants.STATS_NFL_BLOCKED_KICKS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 6) {
//                assertEquals(GlobalConstants.STATS_NFL_SACKS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//
//            index++;
//        }
//    }
//
//    @Test
//    public void testCalculateStatAverages_NFL_Quarterback() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athleteTomBrady = new Athlete(111, "Tom", "Brady", patriots, "");
//        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
//        sportsDao.saveAthlete(athleteTomBrady);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athleteTomBrady, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        StatsNflAthleteByEvent statsNflAthleteByEvent = new StatsNflAthleteByEvent();
//        statsNflAthleteByEvent.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflAthleteByEvent.setAthlete(athleteTomBrady);
//        statsNflAthleteByEvent.setSportEvent(sportEvent);
//        statsNflAthleteByEvent.setStartTime(startTimeLastYear);
//        statsNflAthleteByEvent.setFppInThisEvent(new BigDecimal("10"));
//        statsNflAthleteByEvent.setPassingTouchdowns(10);
//        statsNflAthleteByEvent.setPassingYards(100);
//        statsNflAthleteByEvent.setPassingAttempts(30);
//        statsNflAthleteByEvent.setRushingTouchdowns(2);
//        statsNflAthleteByEvent.setRushingYards(20);
//        statsNflAthleteByEvent.setRushingAttempts(4);
//        statsNflAthleteByEvent.setPassingSacked(2);
//        statsNflAthleteByEvent.setPassingInterceptions(2);
//        statsNflAthleteByEvent.setFumblesLostTotal(2);
//
//        Ebean.save(statsNflAthleteByEvent);
//
//        StatsNflAthleteByEvent statsNflAthleteByEvent2 = new StatsNflAthleteByEvent();
//        statsNflAthleteByEvent2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflAthleteByEvent2.setAthlete(athleteTomBrady);
//        statsNflAthleteByEvent2.setSportEvent(sportEvent);
//        statsNflAthleteByEvent2.setStartTime(startTimeTwoYearsAgo);
//        statsNflAthleteByEvent2.setFppInThisEvent(new BigDecimal("8"));
//        statsNflAthleteByEvent2.setPassingTouchdowns(6);
//        statsNflAthleteByEvent2.setPassingYards(80);
//        statsNflAthleteByEvent2.setPassingAttempts(20);
//        statsNflAthleteByEvent2.setRushingTouchdowns(0);
//        statsNflAthleteByEvent2.setRushingYards(10);
//        statsNflAthleteByEvent2.setRushingAttempts(2);
//        statsNflAthleteByEvent2.setPassingSacked(0);
//        statsNflAthleteByEvent2.setPassingInterceptions(0);
//        statsNflAthleteByEvent2.setFumblesLostTotal(0);
//
//        Ebean.save(statsNflAthleteByEvent2);
//
//        Map<String, BigDecimal> averages = sportsDao.calculateStatAverages(athleteSportEventInfo, 17);
//
//        int index = 0;
//        for(Map.Entry<String, BigDecimal> entry: averages.entrySet()) {
//            if(index == 0) {
//                assertEquals(GlobalConstants.STATS_NFL_PASSING_TOUCHDOWNS, entry.getKey());
//                assertEquals(new BigDecimal("8.00"), entry.getValue());
//            }
//            else if(index == 1) {
//                assertEquals(GlobalConstants.STATS_NFL_PASSING_YARDS, entry.getKey());
//                assertEquals(new BigDecimal("90.00"), entry.getValue());
//            }
//            else if(index == 2) {
//                assertEquals(GlobalConstants.STATS_NFL_PASSING_ATTEMPTS, entry.getKey());
//                assertEquals(new BigDecimal("25.00"), entry.getValue());
//            }
//            else if(index == 3) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, entry.getKey());
//                assertEquals(new BigDecimal("1.00"), entry.getValue());
//            }
//            else if(index == 4) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_YARDS, entry.getKey());
//                assertEquals(new BigDecimal("15.00"), entry.getValue());
//            }
//            else if(index == 5) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, entry.getKey());
//                assertEquals(new BigDecimal("3.00"), entry.getValue());
//            }
//            else if(index == 6) {
//                assertEquals(GlobalConstants.STATS_NFL_SACKS, entry.getKey());
//                assertEquals(new BigDecimal("1.00"), entry.getValue());
//            }
//            else if(index == 7) {
//                assertEquals(GlobalConstants.STATS_NFL_INTERCEPTIONS, entry.getKey());
//                assertEquals(new BigDecimal("1.00"), entry.getValue());
//            }
//            else if(index == 8) {
//                assertEquals(GlobalConstants.STATS_NFL_FUMBLES, entry.getKey());
//                assertEquals(new BigDecimal("1.00"), entry.getValue());
//            }
//
//            index++;
//        }
//    }
//
//    @Test
//    public void testCalculateStatAverages_NFL_Quarterback_NoStats() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athleteTomBrady = new Athlete(111, "Tom", "Brady", patriots, "");
//        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
//        sportsDao.saveAthlete(athleteTomBrady);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athleteTomBrady, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        Map<String, BigDecimal> averages = sportsDao.calculateStatAverages(athleteSportEventInfo, 17);
//
//        int index = 0;
//        for(Map.Entry<String, BigDecimal> entry: averages.entrySet()) {
//            if(index == 0) {
//                assertEquals(GlobalConstants.STATS_NFL_PASSING_TOUCHDOWNS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 1) {
//                assertEquals(GlobalConstants.STATS_NFL_PASSING_YARDS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 2) {
//                assertEquals(GlobalConstants.STATS_NFL_PASSING_ATTEMPTS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 3) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 4) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_YARDS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 5) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 6) {
//                assertEquals(GlobalConstants.STATS_NFL_SACKS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 7) {
//                assertEquals(GlobalConstants.STATS_NFL_INTERCEPTIONS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 8) {
//                assertEquals(GlobalConstants.STATS_NFL_FUMBLES, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//
//            index++;
//        }
//    }
//
//    @Test
//    public void testCalculateStatAverages_NFL_RunningBack() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athlete = new Athlete(111, "Stevan", "Ridley", patriots, "");
//        athlete.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(athlete);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        StatsNflAthleteByEvent statsNflAthleteByEvent = new StatsNflAthleteByEvent();
//        statsNflAthleteByEvent.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflAthleteByEvent.setAthlete(athlete);
//        statsNflAthleteByEvent.setSportEvent(sportEvent);
//        statsNflAthleteByEvent.setStartTime(startTimeLastYear);
//        statsNflAthleteByEvent.setFppInThisEvent(new BigDecimal("10"));
//        statsNflAthleteByEvent.setReceivingTouchdowns(10);
//        statsNflAthleteByEvent.setReceivingYards(100);
//        statsNflAthleteByEvent.setReceivingTargets(30);
//        statsNflAthleteByEvent.setReceivingReceptions(6);
//        statsNflAthleteByEvent.setRushingTouchdowns(2);
//        statsNflAthleteByEvent.setRushingYards(20);
//        statsNflAthleteByEvent.setRushingAttempts(4);
//        statsNflAthleteByEvent.setFumblesLostTotal(2);
//
//        Ebean.save(statsNflAthleteByEvent);
//
//        StatsNflAthleteByEvent statsNflAthleteByEvent2 = new StatsNflAthleteByEvent();
//        statsNflAthleteByEvent2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflAthleteByEvent2.setAthlete(athlete);
//        statsNflAthleteByEvent2.setSportEvent(sportEvent);
//        statsNflAthleteByEvent2.setStartTime(startTimeTwoYearsAgo);
//        statsNflAthleteByEvent2.setFppInThisEvent(new BigDecimal("8"));
//        statsNflAthleteByEvent2.setReceivingTouchdowns(6);
//        statsNflAthleteByEvent2.setReceivingYards(80);
//        statsNflAthleteByEvent2.setReceivingTargets(20);
//        statsNflAthleteByEvent2.setReceivingReceptions(2);
//        statsNflAthleteByEvent2.setRushingTouchdowns(0);
//        statsNflAthleteByEvent2.setRushingYards(40);
//        statsNflAthleteByEvent2.setRushingAttempts(2);
//        statsNflAthleteByEvent2.setFumblesLostTotal(0);
//
//        Ebean.save(statsNflAthleteByEvent2);
//
//        Map<String, BigDecimal> averages = sportsDao.calculateStatAverages(athleteSportEventInfo, 17);
//
//        int index = 0;
//        assertEquals(8, averages.size());
//        for(Map.Entry<String, BigDecimal> entry: averages.entrySet()) {
//            if(index == 0) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, entry.getKey());
//                assertEquals(new BigDecimal("1.00"), entry.getValue());
//            }
//            else if(index == 1) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_YARDS, entry.getKey());
//                assertEquals(new BigDecimal("30.00"), entry.getValue());
//            }
//            else if(index == 2) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, entry.getKey());
//                assertEquals(new BigDecimal("3.00"), entry.getValue());
//            }
//            else if(index == 3) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_TOUCHDOWNS, entry.getKey());
//                assertEquals(new BigDecimal("8.00"), entry.getValue());
//            }
//            else if(index == 4) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_YARDS, entry.getKey());
//                assertEquals(new BigDecimal("90.00"), entry.getValue());
//            }
//            else if(index == 5) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_TARGETS, entry.getKey());
//                assertEquals(new BigDecimal("25.00"), entry.getValue());
//            }
//            else if(index == 6) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEPTIONS, entry.getKey());
//                assertEquals(new BigDecimal("4.00"), entry.getValue());
//            }
//            else if(index == 7) {
//                assertEquals(GlobalConstants.STATS_NFL_FUMBLES, entry.getKey());
//                assertEquals(new BigDecimal("1.00"), entry.getValue());
//            }
//
//            index++;
//        }
//    }
//
//    @Test
//    public void testCalculateStatAverages_NFL_RunningBack_NoStats() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athlete = new Athlete(111, "Stevan", "Ridley", patriots, "");
//        athlete.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(athlete);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        Map<String, BigDecimal> averages = sportsDao.calculateStatAverages(athleteSportEventInfo, 17);
//
//        int index = 0;
//        assertEquals(8, averages.size());
//        for(Map.Entry<String, BigDecimal> entry: averages.entrySet()) {
//            if(index == 0) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 1) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_YARDS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 2) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 3) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_TOUCHDOWNS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 4) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_YARDS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 5) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_TARGETS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 6) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEPTIONS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 7) {
//                assertEquals(GlobalConstants.STATS_NFL_FUMBLES, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//
//            index++;
//        }
//    }
//
//    @Test
//    public void testCalculateStatAverages_NFL_WideReceiver() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athlete = new Athlete(111, "Danny", "Amendola", patriots, "");
//        athlete.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
//        sportsDao.saveAthlete(athlete);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        StatsNflAthleteByEvent statsNflAthleteByEvent = new StatsNflAthleteByEvent();
//        statsNflAthleteByEvent.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflAthleteByEvent.setAthlete(athlete);
//        statsNflAthleteByEvent.setSportEvent(sportEvent);
//        statsNflAthleteByEvent.setStartTime(startTimeLastYear);
//        statsNflAthleteByEvent.setFppInThisEvent(new BigDecimal("10"));
//        statsNflAthleteByEvent.setReceivingTouchdowns(10);
//        statsNflAthleteByEvent.setReceivingYards(100);
//        statsNflAthleteByEvent.setReceivingTargets(30);
//        statsNflAthleteByEvent.setReceivingReceptions(6);
//        statsNflAthleteByEvent.setRushingTouchdowns(2);
//        statsNflAthleteByEvent.setRushingYards(20);
//        statsNflAthleteByEvent.setRushingAttempts(4);
//        statsNflAthleteByEvent.setFumblesLostTotal(2);
//
//        Ebean.save(statsNflAthleteByEvent);
//
//        StatsNflAthleteByEvent statsNflAthleteByEvent2 = new StatsNflAthleteByEvent();
//        statsNflAthleteByEvent2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
//        statsNflAthleteByEvent2.setAthlete(athlete);
//        statsNflAthleteByEvent2.setSportEvent(sportEvent);
//        statsNflAthleteByEvent2.setStartTime(startTimeTwoYearsAgo);
//        statsNflAthleteByEvent2.setFppInThisEvent(new BigDecimal("8"));
//        statsNflAthleteByEvent2.setReceivingTouchdowns(6);
//        statsNflAthleteByEvent2.setReceivingYards(80);
//        statsNflAthleteByEvent2.setReceivingTargets(20);
//        statsNflAthleteByEvent2.setReceivingReceptions(2);
//        statsNflAthleteByEvent2.setRushingTouchdowns(0);
//        statsNflAthleteByEvent2.setRushingYards(40);
//        statsNflAthleteByEvent2.setRushingAttempts(2);
//        statsNflAthleteByEvent2.setFumblesLostTotal(0);
//
//        Ebean.save(statsNflAthleteByEvent2);
//
//        Map<String, BigDecimal> averages = sportsDao.calculateStatAverages(athleteSportEventInfo, 17);
//
//        int index = 0;
//        assertEquals(8, averages.size());
//        for(Map.Entry<String, BigDecimal> entry: averages.entrySet()) {
//            if(index == 0) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_TOUCHDOWNS, entry.getKey());
//                assertEquals(new BigDecimal("8.00"), entry.getValue());
//            }
//            else if(index == 1) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_YARDS, entry.getKey());
//                assertEquals(new BigDecimal("90.00"), entry.getValue());
//            }
//            else if(index == 2) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_TARGETS, entry.getKey());
//                assertEquals(new BigDecimal("25.00"), entry.getValue());
//            }
//            else if(index == 3) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEPTIONS, entry.getKey());
//                assertEquals(new BigDecimal("4.00"), entry.getValue());
//            }
//            else if(index == 4) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, entry.getKey());
//                assertEquals(new BigDecimal("1.00"), entry.getValue());
//            }
//            else if(index == 5) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_YARDS, entry.getKey());
//                assertEquals(new BigDecimal("30.00"), entry.getValue());
//            }
//            else if(index == 6) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, entry.getKey());
//                assertEquals(new BigDecimal("3.00"), entry.getValue());
//            }
//            else if(index == 7) {
//                assertEquals(GlobalConstants.STATS_NFL_FUMBLES, entry.getKey());
//                assertEquals(new BigDecimal("1.00"), entry.getValue());
//            }
//
//            index++;
//        }
//    }
//
//    @Test
//    public void testCalculateStatAverages_NFL_WideReceiver_NoStats() {
//        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
//        sportsDao.saveTeam(patriots);
//
//        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
//        sportsDao.saveTeam(ravens);
//
//        Athlete athlete = new Athlete(111, "Danny", "Amendola", patriots, "");
//        athlete.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
//        sportsDao.saveAthlete(athlete);
//
//        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, 1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete, BigDecimal.ZERO, "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
//
//        Map<String, BigDecimal> averages = sportsDao.calculateStatAverages(athleteSportEventInfo, 17);
//
//        int index = 0;
//        assertEquals(8, averages.size());
//        for(Map.Entry<String, BigDecimal> entry: averages.entrySet()) {
//            if(index == 0) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_TOUCHDOWNS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 1) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_YARDS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 2) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEIVING_TARGETS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 3) {
//                assertEquals(GlobalConstants.STATS_NFL_RECEPTIONS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 4) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_TOUCHDOWNS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 5) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_YARDS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 6) {
//                assertEquals(GlobalConstants.STATS_NFL_RUSHING_ATTEMPTS, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//            else if(index == 7) {
//                assertEquals(GlobalConstants.STATS_NFL_FUMBLES, entry.getKey());
//                assertEquals(BigDecimal.ZERO, entry.getValue());
//            }
//
//            index++;
//        }
//    }
}
