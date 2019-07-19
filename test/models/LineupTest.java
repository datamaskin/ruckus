package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.*;
import models.contest.*;
import models.sports.*;
import models.stats.nfl.StatsNflProjection;
import models.stats.nfl.StatsNflProjectionDefense;
import models.stats.predictive.StatsProjection;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 6/27/14.
 */
public class LineupTest extends BaseTest {
    private Lineup lineup;

    private User user;

    private Entry entry;
    private Contest contest;
    private ContestType contestType;
    private SportEvent sportEvent;
    private SportEvent sportEvent1;
    private ContestPayout contestPayout;
    private SportEventGrouping sportEventGrouping;

    private Athlete athlete;
    private Athlete defense;
    private AthleteSportEventInfo athleteSportEventInfo;
    private AthleteSportEventInfo athleteSportEventInfoDefense;

    private List<SportEvent> sportEvents;

    private ISportsDao sportsDao;
    private IContestDao contestDao;
    private IStatsDao statsDao;

    private StatsNflProjection statsNflProjection;
    private StatsNflProjectionDefense statsNflProjectionDefense;

    @Before
    public void setUp() {
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        statsDao = new StatsDao();

        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        lineup = new Lineup("test", user, League.MLB, null);
        Ebean.save(lineup);

        // Set up ContestType

        /*contestType = new ContestType(1, ContestType.DOUBLE_UP.getName(), ContestType.DOUBLE_UP.getAbbr());
        Ebean.save(contestType); dup rec error*/

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.MLB, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);
        sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        // Set up payouts
        contestPayout = new ContestPayout(1, 1, 100);
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.MLB, "", null);
        Ebean.save(type);
        sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        contest = new Contest(contestType, "212312", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        // Set up Entry
        entry = new Entry(user, contest, null);
        entry.setPoints(100);
        Ebean.save(entry);

        Team patriots = new Team(League.NFL, "", "", "", 123);
        sportsDao.saveTeam(patriots);

        sportEvent1 = new SportEvent(12345, League.NFL, new Date(), "", "", 60, false, 2014, 1, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        sportsDao.saveSportEvent(sportEvent1);

        athlete = new Athlete(1, "Tom", "Brady", patriots, "12");
        athlete.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        sportsDao.saveAthlete(athlete);
        defense = new Athlete(2, "", "Patriots", patriots, "DEF");
        defense.setPositions(Arrays.asList(Position.FB_DEFENSE));
        sportsDao.saveAthlete(defense);
        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent1, athlete, BigDecimal.ZERO, sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
        athleteSportEventInfoDefense = new AthleteSportEventInfo(sportEvent1, defense, BigDecimal.ZERO, sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefense);

        lineup.setLineupSpots(Arrays.asList(
                new LineupSpot(athlete, Position.FB_QUARTERBACK, athleteSportEventInfo),
                new LineupSpot(defense, Position.FB_DEFENSE, athleteSportEventInfoDefense)
        ));
        contestDao.saveLineup(lineup);

        statsNflProjection = new StatsNflProjection();
        statsNflProjection.setAthlete(athlete);
        statsNflProjection.setSportEvent(sportEvent1);
        statsNflProjection.setProjectedFpp(10.0f);
        statsNflProjection.setProjectedFppMod(10.5f);
        Ebean.save(statsNflProjection);

        statsNflProjectionDefense = new StatsNflProjectionDefense();
        statsNflProjectionDefense.setAthlete(defense);
        statsNflProjectionDefense.setSportEvent(sportEvent1);
        statsNflProjectionDefense.setProjectedFpp(5.0f);
        statsNflProjectionDefense.setProjectedFppMod(5.5f);
        Ebean.save(statsNflProjectionDefense);
    }

    @After
    public void tearDown() {
        lineup = null;

        user = null;

        entry = null;
        contest = null;
        contestType = null;
        sportEvent = null;
        contestPayout = null;
    }

    @Test
    public void testFindByUserAndContest() {
        User user2 = new User();
        user2.setEmail("matt.walsh@ruckusgaming.com");
        user2.setFirstName("Matt");
        user2.setLastName("Walsh");
        user2.setPassword("test");
        user2.setUserName("walshms");
        Ebean.save(user2);

        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Contest contest2 = new Contest(contestType, "212315", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest2.setStartTime(new Date());
        contest2.setContestState(ContestState.complete);
        Ebean.save(contest2);

        lineup = new Lineup("My Lineup", user, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup);

        Lineup lineup2 = new Lineup("My Lineup2", user, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup2);

        Lineup lineup3 = new Lineup("My Lineup3", user2, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup3);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);

        Entry entry2 = new Entry(user, contest2, lineup2);
        entry2.setPoints(110);
        Ebean.save(entry2);

        Entry entry3 = new Entry(user2, contest, lineup3);
        entry3.setPoints(90);
        Ebean.save(entry3);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);

        List<Entry> entries3 = new ArrayList<>();
        entries3.add(entry3);
        lineup3.setEntries(entries3);

        Ebean.save(lineup);
        Ebean.save(lineup2);
        Ebean.save(lineup3);

        List<Lineup> lineups = contestDao.findLineups(user, contest);
        assertTrue(lineups.size() == 1);
        assertTrue(lineups.get(0).getId() == lineup.getId());
    }

    @Test
    public void testFindByContestCompatibility_SameGrouping() {
        User user2 = new User();
        user2.setEmail("matt.walsh@ruckusgaming.com");
        user2.setFirstName("Matt");
        user2.setLastName("Walsh");
        user2.setPassword("test");
        user2.setUserName("walshms");
        Ebean.save(user2);

        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Contest contest2 = new Contest(contestType, "212315", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest2.setStartTime(new Date());
        contest2.setContestState(ContestState.complete);
        Ebean.save(contest2);

        lineup = new Lineup("My Lineup", user, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup);

        Lineup lineup2 = new Lineup("My Lineup2", user, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup2);

        Lineup lineup3 = new Lineup("My Lineup3", user2, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup3);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);

        Entry entry2 = new Entry(user, contest2, lineup2);
        entry2.setPoints(110);
        Ebean.save(entry2);

        Entry entry3 = new Entry(user2, contest, lineup3);
        entry3.setPoints(90);
        Ebean.save(entry3);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);

        List<Entry> entries3 = new ArrayList<>();
        entries3.add(entry3);
        lineup3.setEntries(entries3);

        Ebean.save(lineup);
        Ebean.save(lineup2);
        Ebean.save(lineup3);

        List<Lineup> lineups = contestDao.findLineups(user, contest);
        assertTrue(lineups.size() == 1);
        assertTrue(lineups.get(0).getId() == lineup.getId());
    }

    @Test
    public void testFindByContestCompatibility_DifferentGrouping() {
        SportEventGrouping sportEventGrouping2;
        SportEventGroupingType type2 = new SportEventGroupingType(League.MLB, "second grouping type", null);
        Ebean.save(type2);
        sportEventGrouping2 = new SportEventGrouping(sportEvents, type2);
        Ebean.save(sportEventGrouping2);

        User user2 = new User();
        user2.setEmail("matt.walsh@ruckusgaming.com");
        user2.setFirstName("Matt");
        user2.setLastName("Walsh");
        user2.setPassword("test");
        user2.setUserName("walshms");
        Ebean.save(user2);

        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Contest contest2 = new Contest(contestType, "212315", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping2, contestPayouts, null);
        contest2.setStartTime(new Date());
        contest2.setContestState(ContestState.complete);
        Ebean.save(contest2);

        lineup = new Lineup("My Lineup", user, League.MLB, sportEventGrouping);
        Ebean.save(lineup);

        Lineup lineup2 = new Lineup("My Lineup2", user, League.MLB, sportEventGrouping2);
        Ebean.save(lineup2);

        Lineup lineup3 = new Lineup("My Lineup3", user2, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup3);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);

        Entry entry2 = new Entry(user, contest2, lineup2);
        entry2.setPoints(110);
        Ebean.save(entry2);

        Entry entry3 = new Entry(user2, contest, lineup3);
        entry3.setPoints(90);
        Ebean.save(entry3);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);

        List<Entry> entries3 = new ArrayList<>();
        entries3.add(entry3);
        lineup3.setEntries(entries3);

        Ebean.save(lineup);
        Ebean.save(lineup2);
        Ebean.save(lineup3);

        List<Lineup> lineups = contestDao.findLineups(user, contest.getSportEventGrouping());
        assertTrue(lineups.size() == 1);
        assertTrue(lineups.get(0).getId() == lineup.getId());

        lineups = contestDao.findLineups(user, contest2.getSportEventGrouping());
        assertTrue(lineups.size() == 1);
        assertTrue(lineups.get(0).getId() == lineup2.getId());
    }

    @Test
    public void testFindHistorical() {
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);

        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        Contest contest2 = new Contest(contestType, "212315", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest2.setStartTime(cal.getTime());
        contest2.setContestState(ContestState.complete);
        Ebean.save(contest2);

        cal.add(Calendar.DATE, -1);

        Contest contest3 = new Contest(contestType, "212316", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest3.setStartTime(cal.getTime());
        contest3.setContestState(ContestState.complete);
        Ebean.save(contest3);

        lineup = new Lineup("My Lineup", user, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup);

        Lineup lineup2 = new Lineup("My Lineup2", user, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup2);

        Lineup lineup3 = new Lineup("My Lineup3", user, League.MLB, contest.getSportEventGrouping());
        Ebean.save(lineup3);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);

        Entry entry2 = new Entry(user, contest2, lineup2);
        entry2.setPoints(110);
        Ebean.save(entry2);

        Entry entry3 = new Entry(user, contest3, lineup3);
        entry3.setPoints(90);
        Ebean.save(entry3);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);

        List<Entry> entries3 = new ArrayList<>();
        entries3.add(entry3);
        lineup3.setEntries(entries3);

        Ebean.save(lineup);
        Ebean.save(lineup2);
        Ebean.save(lineup3);


        List<Lineup> lineups = contestDao.findLineups(user, Arrays.asList(ContestState.complete));
        assertTrue(lineups.size() > 0);
        assertEquals(contest2.getId(), lineups.get(0).getEntries().get(0).getContest().getId());
    }

    @Test
    public void testUpdatePerformanceData_MLB_FirstUpdateFirstBucket() {
        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 1);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertEquals(2, data.size());
            assertEquals(0, data.get(0).compareTo(new BigDecimal("5")));
            assertEquals(0, data.get(1).compareTo(new BigDecimal("5")));

            assertEquals(18, projectedData.size());

            BigDecimal total = new BigDecimal(5);
            BigDecimal pointsPerUnitTime = new BigDecimal((10 - 5)).divide(new BigDecimal(8), 2, RoundingMode.HALF_EVEN);
            pointsPerUnitTime = pointsPerUnitTime.setScale(2, RoundingMode.HALF_EVEN);
            for (int i = 0; i < 8; i++) {
                BigDecimal progression = new BigDecimal(i).multiply(pointsPerUnitTime);
                assertEquals(0, projectedData.get(i * 2).compareTo(total.add(progression)));
                assertEquals(0, projectedData.get(i * 2 + 1).compareTo(total.add(progression)));
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_MLB_FirstUpdateSecondBucket() {
        BigDecimal zero = new BigDecimal(0);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 2);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertTrue(data.get(0).compareTo(new BigDecimal("0")) == 0);
            assertTrue(data.get(1).compareTo(new BigDecimal("0")) == 0);
            assertTrue(data.get(2).compareTo(new BigDecimal("5")) == 0);
            assertTrue(data.get(3).compareTo(new BigDecimal("5")) == 0);

            BigDecimal total = new BigDecimal(5);
            BigDecimal pointsPerUnitTime = new BigDecimal((10 - 5)).divide(new BigDecimal(7), 2, RoundingMode.HALF_UP);
            pointsPerUnitTime = pointsPerUnitTime.setScale(2, RoundingMode.HALF_EVEN);
            for (int i = 0; i < 7; i++) {
                BigDecimal progression = new BigDecimal(i).multiply(pointsPerUnitTime);
                assertTrue(projectedData.get(i * 2).compareTo(total.add(progression)) == 0);
                assertTrue(projectedData.get(i * 2 + 1).compareTo(total.add(progression)) == 0);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_MLB_SecondUpdateFirstBucket() {
        BigDecimal zero = new BigDecimal(0);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 1);
            lineup.updatePerformanceData(new BigDecimal("2"), 2);
            lineup.updatePerformanceData(new BigDecimal("1"), 1);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertTrue(data.get(0).compareTo(new BigDecimal("6")) == 0);
            assertTrue(data.get(1).compareTo(new BigDecimal("6")) == 0);
            assertTrue(data.get(2).compareTo(new BigDecimal("8")) == 0);
            assertTrue(data.get(3).compareTo(new BigDecimal("8")) == 0);

            BigDecimal total = new BigDecimal(8);
            BigDecimal pointsPerUnitTime = new BigDecimal((10 - 8)).divide(new BigDecimal(7), 2, RoundingMode.HALF_UP);
            pointsPerUnitTime = pointsPerUnitTime.setScale(2, RoundingMode.HALF_EVEN);
            for (int i = 0; i < 7; i++) {
                BigDecimal progression = new BigDecimal(i).multiply(pointsPerUnitTime);
                assertTrue(projectedData.get(i * 2).compareTo(total.add(progression)) == 0);
                assertTrue(projectedData.get(i * 2 + 1).compareTo(total.add(progression)) == 0);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_MLB_UpdateLastBucket() {
        BigDecimal zero = new BigDecimal(0);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 1);
            lineup.updatePerformanceData(new BigDecimal("5"), 9);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertTrue(data.size() == 20);
            assertTrue(projectedData.isEmpty());

            for (int i = 0; i < 16; i++) {
                assertTrue(data.get(i).compareTo(new BigDecimal("5")) == 0);
            }

            assertTrue(data.get(16).compareTo(new BigDecimal("10")) == 0);
            assertTrue(data.get(17).compareTo(new BigDecimal("10")) == 0);
            assertTrue(data.get(18).compareTo(new BigDecimal("10")) == 0);
            assertTrue(data.get(19).compareTo(new BigDecimal("10")) == 0);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_MLB_RoutineSecondBucket() {
        BigDecimal zero = new BigDecimal(0);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 2);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertTrue(data.get(0).compareTo(new BigDecimal("0")) == 0);
            assertTrue(data.get(1).compareTo(new BigDecimal("0")) == 0);
            assertTrue(data.get(2).compareTo(new BigDecimal("5")) == 0);
            assertTrue(data.get(3).compareTo(new BigDecimal("5")) == 0);

            BigDecimal total = new BigDecimal(5);
            BigDecimal pointsPerUnitTime = new BigDecimal((10 - total.intValue())).divide(new BigDecimal(7), 2, RoundingMode.HALF_UP);
            pointsPerUnitTime = pointsPerUnitTime.setScale(2, RoundingMode.HALF_EVEN);
            for (int i = 0; i < 7; i++) {
                BigDecimal progression = new BigDecimal(i).multiply(pointsPerUnitTime);
                assertTrue(projectedData.get(i * 2).compareTo(total.add(progression)) == 0);
                assertTrue(projectedData.get(i * 2 + 1).compareTo(total.add(progression)) == 0);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_NFL_FirstUpdateFirstBucket() {
        lineup.setLeague(League.NFL);
        contestDao.saveLineup(lineup);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 1);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertEquals(1, data.size());
            assertEquals(0, data.get(0).compareTo(new BigDecimal("5")));

            assertEquals(19, projectedData.size());

            BigDecimal total = new BigDecimal("5");
            BigDecimal totalProjection = new BigDecimal(statsNflProjection.getProjectedFppMod() + statsNflProjectionDefense.getProjectedFppMod()).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal pointsPerUnitTime = totalProjection.divide(new BigDecimal(20), 2, RoundingMode.HALF_EVEN);
            for (int i = 0; i < 18; i++) {
                BigDecimal progression = new BigDecimal(i).multiply(pointsPerUnitTime);
                assertEquals(0, projectedData.get(i).compareTo(total.add(progression)));
            }

            StatsProjection statsProjection = statsDao.findStatsProjection(athleteSportEventInfo);
            assertEquals(10.0, statsProjection.getProjection());
            assertEquals(10.5, statsProjection.getProjectionMod());

            StatsProjection statsProjectionDefense = statsDao.findStatsProjection(athleteSportEventInfoDefense);
            assertEquals(5.0, statsProjectionDefense.getProjection());
            assertEquals(5.5, statsProjectionDefense.getProjectionMod());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_NFL_FirstUpdateSecondBucket() {
        lineup.setLeague(League.NFL);
        contestDao.saveLineup(lineup);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 4);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertTrue(data.get(0).compareTo(new BigDecimal("0")) == 0);
            assertTrue(data.get(1).compareTo(new BigDecimal("5")) == 0);

            BigDecimal total = new BigDecimal(5);
            BigDecimal totalProjection = new BigDecimal(statsNflProjection.getProjectedFppMod() + statsNflProjectionDefense.getProjectedFppMod()).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal pointsPerUnitTime = totalProjection.divide(new BigDecimal(20), 2, RoundingMode.HALF_EVEN);
            pointsPerUnitTime = pointsPerUnitTime.setScale(2, RoundingMode.HALF_EVEN);
            for (int i = 0; i < 17; i++) {
                BigDecimal progression = new BigDecimal(i).multiply(pointsPerUnitTime);
                assertTrue(projectedData.get(i).compareTo(total.add(progression)) == 0);
            }

            StatsProjection statsProjection = statsDao.findStatsProjection(athleteSportEventInfo);
            assertEquals(10.0, statsProjection.getProjection());
            assertEquals(10.5, statsProjection.getProjectionMod());

            StatsProjection statsProjectionDefense = statsDao.findStatsProjection(athleteSportEventInfoDefense);
            assertEquals(5.0, statsProjectionDefense.getProjection());
            assertEquals(5.5, statsProjectionDefense.getProjectionMod());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_NFL_SecondUpdateFirstBucket() {
        lineup.setLeague(League.NFL);
        contestDao.saveLineup(lineup);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 1);
            lineup.updatePerformanceData(new BigDecimal("2"), 4);
            lineup.updatePerformanceData(new BigDecimal("1"), 1);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertTrue(data.get(0).compareTo(new BigDecimal("6")) == 0);
            assertTrue(data.get(1).compareTo(new BigDecimal("8")) == 0);

            BigDecimal total = new BigDecimal(8);
            BigDecimal totalProjection = new BigDecimal(statsNflProjection.getProjectedFppMod() + statsNflProjectionDefense.getProjectedFppMod()).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal pointsPerUnitTime = totalProjection.divide(new BigDecimal(20), 2, RoundingMode.HALF_EVEN);
            pointsPerUnitTime = pointsPerUnitTime.setScale(2, RoundingMode.HALF_EVEN);
            for (int i = 0; i < 17; i++) {
                BigDecimal progression = new BigDecimal(i).multiply(pointsPerUnitTime);
                assertTrue(projectedData.get(i).compareTo(total.add(progression)) == 0);
            }

            StatsProjection statsProjection = statsDao.findStatsProjection(athleteSportEventInfo);
            assertEquals(10.0, statsProjection.getProjection());
            assertEquals(10.5, statsProjection.getProjectionMod());

            StatsProjection statsProjectionDefense = statsDao.findStatsProjection(athleteSportEventInfoDefense);
            assertEquals(5.0, statsProjectionDefense.getProjection());
            assertEquals(5.5, statsProjectionDefense.getProjectionMod());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_NFL_RoutineSecondBucket() {
        lineup.setLeague(League.NFL);
        contestDao.saveLineup(lineup);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 5);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertTrue(data.get(0).compareTo(new BigDecimal("0")) == 0);
            assertTrue(data.get(1).compareTo(new BigDecimal("5")) == 0);

            BigDecimal total = new BigDecimal(5);
            BigDecimal totalProjection = new BigDecimal(statsNflProjection.getProjectedFppMod() + statsNflProjectionDefense.getProjectedFppMod()).setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal pointsPerUnitTime = totalProjection.divide(new BigDecimal(20), 2, RoundingMode.HALF_EVEN);
            for (int i = 0; i < 17; i++) {
                BigDecimal progression = new BigDecimal(i).multiply(pointsPerUnitTime);
                assertTrue(projectedData.get(i).compareTo(total.add(progression)) == 0);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerformanceData_NFL_UpdateLastBucket() {
        lineup.setLeague(League.NFL);
        contestDao.saveLineup(lineup);

        try {
            lineup.updatePerformanceData(new BigDecimal("5"), 1);
            lineup.updatePerformanceData(new BigDecimal("5"), 59);

            assertTrue(lineup.getPerformanceData() != null);
            assertTrue(lineup.getProjectedPerformanceData() != null);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
            };
            List<BigDecimal> data = mapper.readValue(lineup.getPerformanceData(), typeReference);
            List<BigDecimal> projectedData = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            assertTrue(data.size() == 20);
            assertTrue(projectedData.isEmpty());

            for (int i = 0; i < 19; i++) {
                assertEquals(0, data.get(i).compareTo(new BigDecimal("5")));
            }
            assertEquals(0, data.get(19).compareTo(new BigDecimal("10")));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRetrieveProjections() {
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);

        Team tigers = new Team(League.MLB, "Detroit", "Tigers", "DET", 230);
        sportsDao.saveTeam(tigers);

//        Position firstBase = new Position(1, Position.BS_FIRST_BASE.getName(), Position.BS_FIRST_BASE.getAbbreviation(), Sport.BASEBALL);
//        Ebean.save(firstBase);

        List<Position> positions = new ArrayList<>();
        positions.add(Position.BS_FIRST_BASE);

        Athlete miguelCabrera = new Athlete(213968, "Miguel", "Cabrera", tigers, "12");
        miguelCabrera.setPositions(positions);
        Ebean.save(miguelCabrera);

        Athlete albertPujols = new Athlete(12345, "Albert", "Pujols", tigers, "11");
        albertPujols.setPositions(positions);
        Ebean.save(albertPujols);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        SportEvent sportEvent1 = new SportEvent(1378361, League.MLB, new Date(), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent1);
        SportEvent sportEvent2 = new SportEvent(1419285, League.MLB, new Date(), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent2);
        SportEvent sportEvent3 = new SportEvent(1380745, League.MLB, new Date(), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent3);


        AthleteSportEventInfo athleteSportEventInfo1 = new AthleteSportEventInfo(sportEvent1, miguelCabrera, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo1);

        AthleteSportEventInfo athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent2, miguelCabrera, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo2);

        AthleteSportEventInfo athleteSportEventInfo3 = new AthleteSportEventInfo(sportEvent3, miguelCabrera, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo3);

        AthleteSportEventInfo athleteSportEventInfoPujols = new AthleteSportEventInfo(sportEvent1, albertPujols, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfoPujols);

        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent1);
        Contest contest2 = new Contest(contestType, "212315", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest2.setStartTime(cal.getTime());
        contest2.setContestState(ContestState.complete);
        Ebean.save(contest2);

        cal.add(Calendar.DATE, -1);

        Contest contest3 = new Contest(contestType, "212316", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest3.setStartTime(cal.getTime());
        contest3.setContestState(ContestState.complete);
        Ebean.save(contest3);

        StatsProjection statsProjectionPujols = new StatsProjection(athleteSportEventInfoPujols, 10, 10);
        Ebean.save(statsProjectionPujols);

        StatsProjection statsProjectionCabrera = new StatsProjection(athleteSportEventInfo3, 15, 15);
        Ebean.save(statsProjectionCabrera);


        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(albertPujols, Position.BS_FIRST_BASE, athleteSportEventInfoPujols));
        lineupSpots.add(new LineupSpot(miguelCabrera, Position.BS_FIRST_BASE, athleteSportEventInfo3));
        Lineup lineup = new Lineup("My Lineup", user, League.MLB, sportEventGrouping);
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);

        Entry entry2 = new Entry(user, contest2, lineup);
        entry2.setPoints(110);
        Ebean.save(entry2);

        Entry entry3 = new Entry(user, contest3, lineup);
        entry3.setPoints(90);
        Ebean.save(entry3);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);

        BigDecimal result = contestDao.findProjection(lineup);
        assertTrue(result.compareTo(new BigDecimal(25)) == 0);

//        List<Entry> entries2 = new ArrayList<>();
//        entries2.add(entry2);
//        Lineup lineup2 = new Lineup("My Lineup2", user, league, contest.getSportEventGrouping());
//        lineup2.entries = entries2;
//        lineup2.save();
//
//        List<Entry> entries3 = new ArrayList<>();
//        entries3.add(entry3);
//        Lineup lineup3 = new Lineup("My Lineup3", user, league, contest.getSportEventGrouping());
//        lineup3.entries = entries3;
//        lineup3.save();
    }

    private void setupNflPerformanceTests() {

    }
}
