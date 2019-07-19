package managers;

import service.LineupService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ContestDao;
import dao.IContestDao;
import dao.ISportsDao;
import dao.IUserDao;
import distributed.DistributedServices;
import models.contest.*;
import models.sports.*;
import models.stats.nfl.StatsNflAthleteByEvent;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;
import wallet.WalletException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by dmaclean on 7/15/14.
 */
public class LineupManagerTest extends BaseTest {
    private LineupService lineupManager;

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
    };
    private TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {};

    private User user;
    private User user2;

    private Team patriots;
    private Team ravens;
    private Team broncos;
    private Team raiders;

    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Athlete athleteEdelman;

    private SportEvent sportEvent;
    private SportEvent sportEvent2;

    private AthleteSportEventInfo athleteSportEventInfoBrady;
    private AthleteSportEventInfo athleteSportEventInfoGronk;
    private AthleteSportEventInfo athleteSportEventInfoEdelman;

    private ContestGrouping grouping;

    private ContestPayout contestPayout;

    private SportEventGroupingType type;
    private SportEventGroupingType type2;
    private SportEventGrouping sportEventGrouping;
    private SportEventGrouping sportEventGrouping2;

    private AthleteSalary salaryTomBrady;
    private AthleteSalary salaryGronk;
    private AthleteSalary salaryEdelman;
    private AthleteSalary salaryTomBrady2;
    private AthleteSalary salaryGronk2;
    private AthleteSalary salaryEdelman2;

    private Contest contest;
    private Contest contest2;

    private Lineup lineup;
    private Lineup lineup2;
    private Lineup lineup3;

    private Entry entry;

    private ISportsDao sportsDao;
    private IContestDao contestDao;
    private IUserDao userDao;

    @Before
    public void setUp() {
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        userDao = context.getBean("userDao", IUserDao.class);

        lineupManager = context.getBean("LineupManager", LineupService.class);

        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        user2 = new User();
        user2.setEmail("matt.walsh@ruckusgaming.com");
        user2.setFirstName("Matt");
        user2.setLastName("Walsh");
        user2.setPassword("test");
        user2.setUserName("walshms");
        Ebean.save(user2);

        userDao.plusUsd(user, 10000);
        userDao.plusUsd(user2, 10000);


        // Set up Team
        patriots = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(patriots);
        ravens = new Team(League.NFL, "Baltimore", "Ravens", "BAL", 2);
        sportsDao.saveTeam(ravens);
        broncos = new Team(League.NFL, "Denver", "Broncos", "DEN", 3);
        sportsDao.saveTeam(broncos);
        raiders = new Team(League.NFL, "Oakland", "Raiders", "OAK", 4);
        sportsDao.saveTeam(raiders);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", patriots, "12");
        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        Ebean.save(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", patriots, "87");
        athleteGronk.setPositions(Arrays.asList(Position.FB_TIGHT_END));
        Ebean.save(athleteGronk);

        athleteEdelman = new Athlete(3, "Julian", "Edelman", patriots, "80");
        athleteEdelman.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
        Ebean.save(athleteEdelman);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        sportEvent.setTeams(Arrays.asList(patriots, ravens));
        Ebean.save(sportEvent);

        sportEvent2 = new SportEvent(2, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        sportEvent2.setTeams(Arrays.asList(broncos, raiders));
        Ebean.save(sportEvent2);

        List<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);
        sportEvents.add(sportEvent2);


        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.00"), "{\"passingYards\":100}", "[\"test1\"]");
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("12.00"), "{\"receivingYards\":100}", "[\"test2\"]");
        Ebean.save(athleteSportEventInfoGronk);
        athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteEdelman, new BigDecimal("11.00"), "{\"receivingYards\":90}", "[\"test3\"]");
        Ebean.save(athleteSportEventInfoEdelman);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.NFL_FULL.getName(), League.NFL);
        Ebean.save(grouping);

        // Set up payouts
        contestPayout = new ContestPayout(1, 1, 100);
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        // Set up SportEventGrouping
        type = new SportEventGroupingType(League.NFL, "Grouping", null);
        Ebean.save(type);
        type2 = new SportEventGroupingType(League.NFL, "Another Grouping", null);
        Ebean.save(type2);
        sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);
        sportEventGrouping2 = new SportEventGrouping(sportEvents, type2);
        Ebean.save(sportEventGrouping2);

        // Set up AthleteSalary
        salaryTomBrady = new AthleteSalary(athleteTomBrady, sportEventGrouping, 500000);
        Ebean.save(salaryTomBrady);
        salaryGronk = new AthleteSalary(athleteGronk, sportEventGrouping, 400000);
        Ebean.save(salaryGronk);
        salaryEdelman = new AthleteSalary(athleteEdelman, sportEventGrouping, 300000);
        Ebean.save(salaryEdelman);
        salaryTomBrady2 = new AthleteSalary(athleteTomBrady, sportEventGrouping2, 400000);
        Ebean.save(salaryTomBrady2);
        salaryGronk2 = new AthleteSalary(athleteGronk, sportEventGrouping2, 300000);
        Ebean.save(salaryGronk2);
        salaryEdelman2 = new AthleteSalary(athleteEdelman, sportEventGrouping2, 200000);
        Ebean.save(salaryEdelman2);

        // Set up Contests
        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 5000000, sportEventGrouping, contestPayouts, null);
        contest.setStartTime(new Date());
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        contest2 = new Contest(ContestType.DOUBLE_UP, "212315", League.NFL, 2, true, 100, 1, 5000000, sportEventGrouping2, contestPayouts, null);
        contest2.setStartTime(new Date());
        contest2.setContestState(ContestState.active);
        Ebean.save(contest2);

        // Set up Lineups
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        lineup = new Lineup("test", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        lineup.setName("lineup");
        Ebean.save(lineup);

        List<LineupSpot> lineupSpots2 = new ArrayList<>();
        lineupSpots2.add(new LineupSpot(athleteEdelman, Position.FB_WIDE_RECEIVER, athleteSportEventInfoEdelman));
        lineup2 = new Lineup("My Lineup2", user, League.NFL, contest2.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots2);
        lineup2.setName("lineup2");
        Ebean.save(lineup2);

        List<LineupSpot> lineupSpots3 = new ArrayList<>();
        lineupSpots3.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        lineup3 = new Lineup("My Lineup3", user2, League.NFL, contest.getSportEventGrouping());
        lineup3.setLineupSpots(lineupSpots3);
        lineup3.setName("lineup3");
        Ebean.save(lineup3);


        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);
    }

    @After
    public void tearDown() {
        Map<String, BigDecimal> fppgCache = DistributedServices.getInstance().getMap(GlobalConstants.ATHLETE_FPPG_MAP);
        fppgCache.clear();

        lineupManager = null;
    }

    @Test
    public void testGetLiveLineups_BadUser() {
        String result = null;
        try {
            result = lineupManager.getLiveLineupsAsJson(null);
            Map<String, String> resultMap = mapper.readValue(result, mapTypeReference);

            assertEquals("The user's session has expired", resultMap.get("error"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLiveLineups() {
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Contest contest3 = new Contest(ContestType.DOUBLE_UP, "212319", League.NFL, 2, true, 100, 1, 5000000, sportEventGrouping2, contestPayouts, null);
        contest3.setStartTime(new Date());
        contest3.setContestState(ContestState.complete);
        Ebean.save(contest3);

        // Set up Lineups
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        lineupSpots.add(new LineupSpot(athleteEdelman, Position.FB_WIDE_RECEIVER, athleteSportEventInfoEdelman));
        Lineup lineup4 = new Lineup("test", user, League.NFL, contest.getSportEventGrouping());
        lineup4.setName("lineup4");
        lineup4.setLineupSpots(lineupSpots);
        Ebean.save(lineup4);

        // Set up Entry
        Entry entry2 = new Entry(user, contest, lineup2);
        entry2.setPoints(99);
        Ebean.save(entry2);

//        Entry entry2 = new Entry(user, contest, lineup3);
//        entry2.setPoints(99);
//        entry2);

        Entry entry3 = new Entry(user, contest3, lineup4);
        entry3.setPoints(99);
        Ebean.save(entry3);

        try {
            String result = lineupManager.getLiveLineupsAsJson(user);
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);

            /*
             *  Lineup 1
             */
            assertTrue(data.size() == 2);
            assertTrue((Integer) data.get(0).get("lineupId") == lineup.getId());
            assertTrue(data.get(0).get("lineupName").equals(lineup.getName()));
            assertTrue(data.get(0).get("startTime") instanceof Long);
            assertTrue((Integer) data.get(0).get("remainingSalary") == 4100000);
            assertEquals("22.0", data.get(0).get("fpp").toString());
            assertEquals(120, data.get(0).get("unitsRemaining"));

            List<Map<String, Object>> athleteData = (List<Map<String, Object>>) data.get(0).get("athletes");
            assertTrue(athleteData.size() == 2);
            assertTrue((Integer) athleteData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athleteData.get(0).get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athleteData.get(0).get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue(athleteData.get(0).get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(0).get("salary") == salaryTomBrady.salary);
            assertEquals("10.0", athleteData.get(0).get("fppg").toString());
            assertTrue((Integer) athleteData.get(0).get("timeRemaining") == 60);

            assertTrue((Integer) athleteData.get(1).get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());
            assertTrue(athleteData.get(1).get("firstName").equals(athleteGronk.getFirstName()));
            assertTrue(athleteData.get(1).get("lastName").equals(athleteGronk.getLastName()));
            assertTrue(athleteData.get(1).get("position").equals(athleteGronk.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(1).get("salary") == salaryGronk.salary);
            assertEquals("12.0", athleteData.get(1).get("fppg").toString());
            assertTrue((Integer) athleteData.get(1).get("timeRemaining") == 60);

            List<Map<String, Object>> contestData = (List<Map<String, Object>>) data.get(0).get("contests");
            assertTrue(contestData.size() == 1);
            assertTrue(((List<Integer>) contestData.get(0).get("entries")).size() == 1 && ((List<Integer>) contestData.get(0).get("entries")).contains(entry.getId()));
            assertTrue(contestData.get(0).get("league").equals(League.NFL.getAbbreviation()));
            Map<String, Object> contestType = (Map<String, Object>) contestData.get(0).get("contestType");
            assertTrue(contestType.get("name").equals(ContestType.DOUBLE_UP.getName()));
            assertTrue(contestType.get("abbr").equals(ContestType.DOUBLE_UP.getAbbr()));
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("allowedEntries") == contest.getAllowedEntries());
            assertTrue((Boolean) contestData.get(0).get("guaranteed") == contest.isGuaranteed());
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("currentEntries") == contest.getCurrentEntries());
            assertTrue((Integer) contestData.get(0).get("capacity") == contest.getCapacity());
            assertTrue(contestData.get(0).containsKey("payout"));
            assertTrue((Integer) contestData.get(0).get("grouping") == contest.getSportEventGrouping().getSportEventGroupingType().getId());
            assertTrue(contestData.get(0).get("id").equals(contest.getUrlId()));
            assertTrue(contestData.get(0).get("contestState").equals(ContestState.active.getName()));
            assertTrue(!contestData.get(0).containsKey("opp"));

            /*
             * Lineup 2
             */
            assertTrue((Integer) data.get(1).get("lineupId") == lineup2.getId());
            assertTrue(data.get(1).get("lineupName").equals(lineup2.getName()));
            assertTrue(data.get(1).get("startTime") instanceof Long);
            assertTrue((Integer) data.get(1).get("remainingSalary") == 4800000);
            assertEquals("11.0", data.get(1).get("fpp").toString());
            assertEquals(60, data.get(1).get("unitsRemaining"));

            athleteData = (List<Map<String, Object>>) data.get(1).get("athletes");
            assertTrue(athleteData.size() == 1);
            assertTrue((Integer) athleteData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoEdelman.getId());
            assertTrue(athleteData.get(0).get("firstName").equals(athleteEdelman.getFirstName()));
            assertTrue(athleteData.get(0).get("lastName").equals(athleteEdelman.getLastName()));
            assertTrue(athleteData.get(0).get("position").equals(athleteEdelman.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(0).get("salary") == salaryEdelman2.salary);
            assertEquals("11.0", athleteData.get(0).get("fppg").toString());
            assertTrue((Integer) athleteData.get(0).get("timeRemaining") == 60);

            contestData = (List<Map<String, Object>>) data.get(1).get("contests");
            assertTrue(contestData.size() == 1);
            assertTrue(((List<Integer>) contestData.get(0).get("entries")).size() == 1 && ((List<Integer>) contestData.get(0).get("entries")).contains(entry2.getId()));
            assertTrue(contestData.get(0).get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue(contestData.get(0).get("id").equals(contest.getUrlId()));
            contestType = (Map<String, Object>) contestData.get(0).get("contestType");
            assertTrue(contestType.get("name").equals(ContestType.DOUBLE_UP.getName()));
            assertTrue(contestType.get("abbr").equals(ContestType.DOUBLE_UP.getAbbr()));
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("allowedEntries") == contest.getAllowedEntries());
            assertTrue((Boolean) contestData.get(0).get("guaranteed") == contest.isGuaranteed());
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("currentEntries") == contest.getCurrentEntries());
            assertTrue((Integer) contestData.get(0).get("capacity") == contest.getCapacity());
            assertTrue(contestData.get(0).containsKey("payout"));
            assertTrue((Integer) contestData.get(0).get("grouping") == contest.getSportEventGrouping().getSportEventGroupingType().getId());
            assertTrue(contestData.get(0).get("contestState").equals(ContestState.active.getName()));
            assertTrue(!contestData.get(0).containsKey("opp"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLiveLineups_OpenState() {
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        contest.setContestState(ContestState.open);
        contestDao.saveContest(contest);

        contest2.setContestState(ContestState.open);
        contestDao.saveContest(contest2);

        // Set up Entry
        Entry entry2 = new Entry(user, contest, lineup2);
        entry2.setPoints(99);
        Ebean.save(entry2);

        StatsNflAthleteByEvent statsNflAthleteByEventBrady = new StatsNflAthleteByEvent();
        statsNflAthleteByEventBrady.setAthlete(athleteTomBrady);
        statsNflAthleteByEventBrady.setStartTime(Date.from(Instant.now().minus(Duration.ofDays(1))));
        statsNflAthleteByEventBrady.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEventBrady.setFppInThisEvent(new BigDecimal("9"));
        Ebean.save(statsNflAthleteByEventBrady);

        StatsNflAthleteByEvent statsNflAthleteByEventGronk = new StatsNflAthleteByEvent();
        statsNflAthleteByEventGronk.setAthlete(athleteGronk);
        statsNflAthleteByEventGronk.setStartTime(Date.from(Instant.now().minus(Duration.ofDays(1))));
        statsNflAthleteByEventGronk.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEventGronk.setFppInThisEvent(new BigDecimal("8"));
        Ebean.save(statsNflAthleteByEventGronk);

        StatsNflAthleteByEvent statsNflAthleteByEventEdelman = new StatsNflAthleteByEvent();
        statsNflAthleteByEventEdelman.setAthlete(athleteEdelman);
        statsNflAthleteByEventEdelman.setStartTime(Date.from(Instant.now().minus(Duration.ofDays(1))));
        statsNflAthleteByEventEdelman.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEventEdelman.setFppInThisEvent(new BigDecimal("6"));
        Ebean.save(statsNflAthleteByEventEdelman);

        try {
            String result = lineupManager.getLiveLineupsAsJson(user);
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);

            /*
             *  Lineup 1
             */
            assertTrue(data.size() == 2);
            assertTrue((Integer) data.get(0).get("lineupId") == lineup.getId());
            assertTrue(data.get(0).get("lineupName").equals(lineup.getName()));
            assertTrue(data.get(0).get("startTime") instanceof Long);
            assertTrue((Integer) data.get(0).get("remainingSalary") == 4100000);
            assertEquals("22.0", data.get(0).get("fpp").toString());
            assertEquals(120, data.get(0).get("unitsRemaining"));

            List<Map<String, Object>> athleteData = (List<Map<String, Object>>) data.get(0).get("athletes");
            assertTrue(athleteData.size() == 2);
            assertTrue((Integer) athleteData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athleteData.get(0).get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athleteData.get(0).get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue(athleteData.get(0).get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(0).get("salary") == salaryTomBrady.salary);
            assertEquals("9.0", athleteData.get(0).get("fppg").toString());
            assertTrue((Integer) athleteData.get(0).get("timeRemaining") == 60);

            assertTrue((Integer) athleteData.get(1).get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());
            assertTrue(athleteData.get(1).get("firstName").equals(athleteGronk.getFirstName()));
            assertTrue(athleteData.get(1).get("lastName").equals(athleteGronk.getLastName()));
            assertTrue(athleteData.get(1).get("position").equals(athleteGronk.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(1).get("salary") == salaryGronk.salary);
            assertEquals("8.0", athleteData.get(1).get("fppg").toString());
            assertTrue((Integer) athleteData.get(1).get("timeRemaining") == 60);

            List<Map<String, Object>> contestData = (List<Map<String, Object>>) data.get(0).get("contests");
            assertTrue(contestData.size() == 1);
            assertTrue(((List<Integer>) contestData.get(0).get("entries")).size() == 1 && ((List<Integer>) contestData.get(0).get("entries")).contains(entry.getId()));
            assertTrue(contestData.get(0).get("league").equals(League.NFL.getAbbreviation()));
            Map<String, Object> contestType = (Map<String, Object>) contestData.get(0).get("contestType");
            assertTrue(contestType.get("name").equals(ContestType.DOUBLE_UP.getName()));
            assertTrue(contestType.get("abbr").equals(ContestType.DOUBLE_UP.getAbbr()));
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("allowedEntries") == contest.getAllowedEntries());
            assertTrue((Boolean) contestData.get(0).get("guaranteed") == contest.isGuaranteed());
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("currentEntries") == contest.getCurrentEntries());
            assertTrue((Integer) contestData.get(0).get("capacity") == contest.getCapacity());
            assertTrue(contestData.get(0).containsKey("payout"));
            assertTrue((Integer) contestData.get(0).get("grouping") == contest.getSportEventGrouping().getSportEventGroupingType().getId());
            assertTrue(contestData.get(0).get("id").equals(contest.getUrlId()));
            assertTrue(contestData.get(0).get("contestState").equals(ContestState.open.getName()));
            assertTrue(!contestData.get(0).containsKey("opp"));

            /*
             * Lineup 2
             */
            assertTrue((Integer) data.get(1).get("lineupId") == lineup2.getId());
            assertTrue(data.get(1).get("lineupName").equals(lineup2.getName()));
            assertTrue(data.get(1).get("startTime") instanceof Long);
            assertTrue((Integer) data.get(1).get("remainingSalary") == 4800000);
            assertEquals("11.0", data.get(1).get("fpp").toString());
            assertEquals(60, data.get(1).get("unitsRemaining"));

            athleteData = (List<Map<String, Object>>) data.get(1).get("athletes");
            assertTrue(athleteData.size() == 1);
            assertTrue((Integer) athleteData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoEdelman.getId());
            assertTrue(athleteData.get(0).get("firstName").equals(athleteEdelman.getFirstName()));
            assertTrue(athleteData.get(0).get("lastName").equals(athleteEdelman.getLastName()));
            assertTrue(athleteData.get(0).get("position").equals(athleteEdelman.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(0).get("salary") == salaryEdelman2.salary);
            assertEquals("6.0", athleteData.get(0).get("fppg").toString());
            assertTrue((Integer) athleteData.get(0).get("timeRemaining") == 60);

            contestData = (List<Map<String, Object>>) data.get(1).get("contests");
            assertTrue(contestData.size() == 1);
            assertTrue(((List<Integer>) contestData.get(0).get("entries")).size() == 1 && ((List<Integer>) contestData.get(0).get("entries")).contains(entry2.getId()));
            assertTrue(contestData.get(0).get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue(contestData.get(0).get("id").equals(contest.getUrlId()));
            contestType = (Map<String, Object>) contestData.get(0).get("contestType");
            assertTrue(contestType.get("name").equals(ContestType.DOUBLE_UP.getName()));
            assertTrue(contestType.get("abbr").equals(ContestType.DOUBLE_UP.getAbbr()));
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("allowedEntries") == contest.getAllowedEntries());
            assertTrue((Boolean) contestData.get(0).get("guaranteed") == contest.isGuaranteed());
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("currentEntries") == contest.getCurrentEntries());
            assertTrue((Integer) contestData.get(0).get("capacity") == contest.getCapacity());
            assertTrue(contestData.get(0).containsKey("payout"));
            assertTrue((Integer) contestData.get(0).get("grouping") == contest.getSportEventGrouping().getSportEventGroupingType().getId());
            assertTrue(contestData.get(0).get("contestState").equals(ContestState.open.getName()));
            assertTrue(!contestData.get(0).containsKey("opp"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLiveLineups_H2H() {
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Contest contest3 = new Contest(ContestType.H2H, "212319", League.NFL, 2, true, 100, 1, 5000000, sportEventGrouping2, contestPayouts, null);
        contest3.setStartTime(new Date());
        contest3.setContestState(ContestState.active);
        Ebean.save(contest3);

        lineup3.setUser(user);
        Ebean.save(lineup3);

        // Set up Lineups
        Lineup lineup4 = new Lineup("test", user2, League.NFL, contest.getSportEventGrouping());
        lineup4.setName("lineup4");
        lineup4.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady)
        ));
        Ebean.save(lineup4);

        Entry entry2 = new Entry(user, contest3, lineup3);
        entry2.setPoints(99);
        Ebean.save(entry2);

        Entry entry3 = new Entry(user2, contest3, lineup4);
        entry3.setPoints(99);
        Ebean.save(entry3);

        lineup4.setEntries(Arrays.asList(entry3));
        Ebean.save(lineup4);

        lineup3.setEntries(Arrays.asList(entry2));
        Ebean.save(lineup3);

        StatsNflAthleteByEvent statsNflAthleteByEventBrady1 = new StatsNflAthleteByEvent();
        statsNflAthleteByEventBrady1.setAthlete(athleteTomBrady);
        statsNflAthleteByEventBrady1.setStartTime(Date.from(Instant.now().minus(Duration.ofDays(1))));
        statsNflAthleteByEventBrady1.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEventBrady1.setFppInThisEvent(new BigDecimal("10"));
        Ebean.save(statsNflAthleteByEventBrady1);

        StatsNflAthleteByEvent statsNflAthleteByEventBrady2 = new StatsNflAthleteByEvent();
        statsNflAthleteByEventBrady2.setAthlete(athleteTomBrady);
        statsNflAthleteByEventBrady2.setStartTime(Date.from(Instant.now().minus(Duration.ofDays(2))));
        statsNflAthleteByEventBrady2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEventBrady2.setFppInThisEvent(new BigDecimal("0"));
        Ebean.save(statsNflAthleteByEventBrady2);

        try {
            String result = lineupManager.getLiveLineupsAsJson(user2);
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);

            /*
             *  Lineup 1
             */
            assertEquals(1, data.size());
            assertEquals(lineup4.getId(), data.get(0).get("lineupId"));
            assertTrue(data.get(0).get("lineupName").equals(lineup4.getName()));
            assertTrue(data.get(0).get("startTime") instanceof Long);
            assertTrue((Integer) data.get(0).get("remainingSalary") == 4500000);
            assertEquals("10.0", data.get(0).get("fpp").toString());
            assertEquals(60, data.get(0).get("unitsRemaining"));

            List<Map<String, Object>> athleteData = (List<Map<String, Object>>) data.get(0).get("athletes");
            assertTrue(athleteData.size() == 1);
            assertTrue((Integer) athleteData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athleteData.get(0).get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athleteData.get(0).get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue(athleteData.get(0).get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(0).get("salary") == salaryTomBrady.salary);
            assertEquals("10.0", athleteData.get(0).get("fppg").toString());
            assertTrue((Integer) athleteData.get(0).get("timeRemaining") == 60);

            List<Map<String, Object>> contestData = (List<Map<String, Object>>) data.get(0).get("contests");
            assertTrue(contestData.size() == 1);
            assertTrue(((List<Integer>) contestData.get(0).get("entries")).size() == 1 &&
                    ((List<Integer>) contestData.get(0).get("entries")).contains(entry3.getId()));
            assertTrue(contestData.get(0).get("league").equals(League.NFL.getAbbreviation()));
            Map<String, Object> contestType = (Map<String, Object>) contestData.get(0).get("contestType");
            assertTrue(contestType.get("name").equals(ContestType.H2H.getName()));
            assertTrue(contestType.get("abbr").equals(ContestType.H2H.getAbbr()));
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest3.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("allowedEntries") == contest3.getAllowedEntries());
            assertTrue((Boolean) contestData.get(0).get("guaranteed") == contest3.isGuaranteed());
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest3.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("currentEntries") == contest3.getCurrentEntries());
            assertTrue((Integer) contestData.get(0).get("capacity") == contest3.getCapacity());
            assertTrue(contestData.get(0).containsKey("payout"));
            assertTrue((Integer) contestData.get(0).get("grouping") == contest3.getSportEventGrouping().getSportEventGroupingType().getId());
            assertTrue(contestData.get(0).get("id").equals(contest3.getUrlId()));
            assertTrue(contestData.get(0).get("contestState").equals(ContestState.active.getName()));
            assertEquals(user.getUserName(), contestData.get(0).get("opp"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLiveLineups_H2H_OpenState() {
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Contest contest3 = new Contest(ContestType.H2H, "212319", League.NFL, 2, true, 100, 1, 5000000, sportEventGrouping2, contestPayouts, null);
        contest3.setStartTime(new Date());
        contest3.setContestState(ContestState.open);
        Ebean.save(contest3);

        lineup3.setUser(user);
        Ebean.save(lineup3);

        // Set up Lineups
        Lineup lineup4 = new Lineup("test", user2, League.NFL, contest.getSportEventGrouping());
        lineup4.setName("lineup4");
        lineup4.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady)
        ));
        Ebean.save(lineup4);

        Entry entry2 = new Entry(user, contest3, lineup3);
        entry2.setPoints(99);
        Ebean.save(entry2);

        Entry entry3 = new Entry(user2, contest3, lineup4);
        entry3.setPoints(99);
        Ebean.save(entry3);

        lineup4.setEntries(Arrays.asList(entry3));
        Ebean.save(lineup4);

        lineup3.setEntries(Arrays.asList(entry2));
        Ebean.save(lineup3);

        StatsNflAthleteByEvent statsNflAthleteByEventBrady1 = new StatsNflAthleteByEvent();
        statsNflAthleteByEventBrady1.setAthlete(athleteTomBrady);
        statsNflAthleteByEventBrady1.setStartTime(Date.from(Instant.now().minus(Duration.ofDays(1))));
        statsNflAthleteByEventBrady1.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEventBrady1.setFppInThisEvent(new BigDecimal("10"));
        Ebean.save(statsNflAthleteByEventBrady1);

        StatsNflAthleteByEvent statsNflAthleteByEventBrady2 = new StatsNflAthleteByEvent();
        statsNflAthleteByEventBrady2.setAthlete(athleteTomBrady);
        statsNflAthleteByEventBrady2.setStartTime(Date.from(Instant.now().minus(Duration.ofDays(2))));
        statsNflAthleteByEventBrady2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEventBrady2.setFppInThisEvent(new BigDecimal("0"));
        Ebean.save(statsNflAthleteByEventBrady2);

        try {
            String result = lineupManager.getLiveLineupsAsJson(user2);
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);

            /*
             *  Lineup 1
             */
            assertEquals(1, data.size());
            assertEquals(lineup4.getId(), data.get(0).get("lineupId"));
            assertTrue(data.get(0).get("lineupName").equals(lineup4.getName()));
            assertTrue(data.get(0).get("startTime") instanceof Long);
            assertTrue((Integer) data.get(0).get("remainingSalary") == 4500000);
            assertEquals("10.0", data.get(0).get("fpp").toString());
            assertEquals(60, data.get(0).get("unitsRemaining"));

            List<Map<String, Object>> athleteData = (List<Map<String, Object>>) data.get(0).get("athletes");
            assertTrue(athleteData.size() == 1);
            assertTrue((Integer) athleteData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athleteData.get(0).get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athleteData.get(0).get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue(athleteData.get(0).get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(0).get("salary") == salaryTomBrady.salary);
            assertEquals("5.0", athleteData.get(0).get("fppg").toString());
            assertTrue((Integer) athleteData.get(0).get("timeRemaining") == 60);

            List<Map<String, Object>> contestData = (List<Map<String, Object>>) data.get(0).get("contests");
            assertTrue(contestData.size() == 1);
            assertTrue(((List<Integer>) contestData.get(0).get("entries")).size() == 1 &&
                    ((List<Integer>) contestData.get(0).get("entries")).contains(entry3.getId()));
            assertTrue(contestData.get(0).get("league").equals(League.NFL.getAbbreviation()));
            Map<String, Object> contestType = (Map<String, Object>) contestData.get(0).get("contestType");
            assertTrue(contestType.get("name").equals(ContestType.H2H.getName()));
            assertTrue(contestType.get("abbr").equals(ContestType.H2H.getAbbr()));
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest3.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("allowedEntries") == contest3.getAllowedEntries());
            assertTrue((Boolean) contestData.get(0).get("guaranteed") == contest3.isGuaranteed());
            assertTrue((Integer) contestData.get(0).get("entryFee") == contest3.getEntryFee());
            assertTrue((Integer) contestData.get(0).get("currentEntries") == contest3.getCurrentEntries());
            assertTrue((Integer) contestData.get(0).get("capacity") == contest3.getCapacity());
            assertTrue(contestData.get(0).containsKey("payout"));
            assertTrue((Integer) contestData.get(0).get("grouping") == contest3.getSportEventGrouping().getSportEventGroupingType().getId());
            assertTrue(contestData.get(0).get("id").equals(contest3.getUrlId()));
            assertTrue(contestData.get(0).get("contestState").equals(ContestState.open.getName()));
            assertEquals(user.getUserName(), contestData.get(0).get("opp"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLiveLineups_NullUser() {
        try {
            String result = lineupManager.getLiveLineupsAsJson(null);

            TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> data = mapper.readValue(result, mapTypeReference);

            assertTrue(data.get("error").equals("The user's session has expired"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLineupsByContest() {
        try {
            String result = lineupManager.getLineupsByContest(user, contest);
            String result2 = lineupManager.getLineupsByContest(user, contest2);

            List<Map<String, Object>> data = mapper.readValue(result, typeReference);

            assertTrue(data.size() == 1);
            assertTrue((Integer) data.get(0).get("lineupId") == lineup.getId());
            assertTrue(data.get(0).get("lineupName").equals(lineup.getName()));
            assertEquals(1, data.get(0).get("numEntries"));

            List<Map<String, Object>> athleteData = (List<Map<String, Object>>) data.get(0).get("athletes");
            assertTrue(athleteData.size() == 2);
            assertTrue((Integer) athleteData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athleteData.get(0).get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athleteData.get(0).get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue(athleteData.get(0).get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(0).get("salary") == salaryTomBrady.salary);


            assertTrue((Integer) athleteData.get(1).get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());
            assertTrue(athleteData.get(1).get("firstName").equals(athleteGronk.getFirstName()));
            assertTrue(athleteData.get(1).get("lastName").equals(athleteGronk.getLastName()));
            assertTrue(athleteData.get(1).get("position").equals(athleteGronk.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(1).get("salary") == salaryGronk.salary);


            data = mapper.readValue(result2, typeReference);

            assertTrue(data.size() == 1);
            assertTrue((Integer) data.get(0).get("lineupId") == lineup2.getId());
            assertTrue(data.get(0).get("lineupName").equals(lineup2.getName()));
            assertEquals(data.get(0).get("numEntries"), 0);

            athleteData = (List<Map<String, Object>>) data.get(0).get("athletes");
            assertTrue(athleteData.size() == 1);
            assertTrue((Integer) athleteData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoEdelman.getId());
            assertTrue(athleteData.get(0).get("firstName").equals(athleteEdelman.getFirstName()));
            assertTrue(athleteData.get(0).get("lastName").equals(athleteEdelman.getLastName()));
            assertTrue(athleteData.get(0).get("position").equals(athleteEdelman.getPositions().get(0).getAbbreviation()));
            assertTrue((Integer) athleteData.get(0).get("salary") == salaryEdelman2.salary);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLineupsByContest_OrderByNumEntries() {
        lineup2.setSportEventGrouping(contest.getSportEventGrouping());
        contestDao.saveLineup(lineup2);

        Entry entry1 = new Entry(user, contest, lineup2);
        contestDao.saveEntry(entry1);

        Entry entry2 = new Entry(user, contest, lineup2);
        contestDao.saveEntry(entry2);

        try {
            String result = lineupManager.getLineupsByContest(user, contest);

            List<Map<String, Object>> data = mapper.readValue(result, typeReference);

            assertEquals(2, data.size());
            assertEquals(lineup2.getId(), data.get(0).get("lineupId"));
            assertEquals(lineup2.getName(), data.get(0).get("lineupName"));
            assertEquals(2, data.get(0).get("numEntries"));

            assertEquals(lineup.getId(), data.get(1).get("lineupId"));
            assertEquals(lineup.getName(), data.get(1).get("lineupName"));
            assertEquals(1, data.get(1).get("numEntries"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidateLineup_OnlyOneSportEvent() {
        //TODO FIX
//        boolean exceptionThrown = false;
//        try {
//            lineupManager.validateLineup(League.NFL, lineup.getLineupSpots());
//        } catch (LineupValidationException e) {
//            exceptionThrown = true;
//        }
//
//        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidateLineup_Valid() {
        //TODO FIX
//        Team ravens = new Team(League.NFL, "Baltimore", "Ravens", "BAL", 3123213);
//        sportsDao.save(ravens);
//
//        Athlete joeFlacco = new Athlete(222, "Joe", "Flacco", ravens, "7");
//        Ebean.save(joeFlacco);
//
//        SportEvent sportEvent2 = new SportEvent(2, League.NFL, new Date(), "test2", "test2", 60, false);
//        Ebean.save(sportEvent2);
//
//        AthleteSportEventInfo athleteSportEventInfoJoeFlacco = new AthleteSportEventInfo(sportEvent2, joeFlacco, new BigDecimal(0), "", "");
//        Ebean.save(athleteSportEventInfoJoeFlacco);
//
//        lineup.getLineupSpots().add(new LineupSpot(joeFlacco, positionQB, athleteSportEventInfoJoeFlacco));
//        Ebean.save(lineup);
//
//        sportEventGrouping.getSportEvents().add(sportEvent2);
//        Ebean.save(sportEventGrouping);
//
//        contest.setSportEventGrouping(sportEventGrouping);
//        Ebean.save(contest);
//
//        try {
//            lineupManager.validateLineup(League.NFL, lineup.getLineupSpots());
//        } catch (LineupValidationException e) {
//            fail(e.getMessage());
//        }

        // If we got here then the test passed.
    }

    @Test
    public void testRemoveLineupFromContest_SingleEntry_Open() {
        startHazelcast();

        // Set up Contests
        Contest contestSingleEntry = new Contest(ContestType.DOUBLE_UP, "1", League.NFL, 10, true, 100, 1, 5000000, sportEventGrouping, new ArrayList<>(), null);
        contestSingleEntry.setContestState(ContestState.open);
        contestDao.saveContest(contestSingleEntry);

        Entry entry2 = new Entry(user, contestSingleEntry, lineup);
        entry2.setPoints(99);
        Ebean.save(entry2);

        // Manually deduct money
        try {
            userDao.minusUsd(user, contestSingleEntry.getEntryFee());
        } catch (WalletException e) {
            fail(e.getMessage());
        }

        List<Entry> entries = contestDao.findEntries(lineup, contestSingleEntry);
        assertTrue(entries.size() == 1);

        assertTrue(userDao.getUserWallet(user).getUsd() == 9900);

        try {
            String result = lineupManager.removeLineupFromContest(user, lineup, contestSingleEntry);
            Map<String, Object> resultMap = mapper.readValue(result, mapTypeReference);
            assertTrue(resultMap.get("contestId").equals(contestSingleEntry.getUrlId()));
            assertTrue((Integer) resultMap.get("lineupId") == lineup.getId());
            assertTrue((Integer) resultMap.get("code") == GlobalConstants.CONTEST_ENTRY_SUCCESS);

            entries = contestDao.findEntries(lineup, contestSingleEntry);
            assertTrue(entries.isEmpty());
            assertTrue(userDao.getUserWallet(user).getUsd() == 10000);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRemoveLineupFromContest_MultiEntry_Open() {
        startHazelcast();

        // Set up Contests
        Contest contestSingleEntry = new Contest(ContestType.GPP, "1", League.NFL, 10, true, 100, 2, 5000000, sportEventGrouping, new ArrayList<>(), null);
        contestSingleEntry.setContestState(ContestState.open);
        contestDao.saveContest(contestSingleEntry);

        Entry entry2 = new Entry(user, contestSingleEntry, lineup);
        entry2.setPoints(99);
        Ebean.save(entry2);

        // Manually deduct money
        try {
            userDao.minusUsd(user, contestSingleEntry.getEntryFee());
        } catch (WalletException e) {
            fail(e.getMessage());
        }

        Entry entry3 = new Entry(user, contestSingleEntry, lineup);
        entry3.setPoints(99);
        Ebean.save(entry3);

        // Manually deduct money
        try {
            userDao.minusUsd(user, contestSingleEntry.getEntryFee());
        } catch (WalletException e) {
            fail(e.getMessage());
        }

        List<Entry> entries = contestDao.findEntries(lineup, contestSingleEntry);
        assertTrue(entries.size() == 2);


        try {
            String result = lineupManager.removeLineupFromContest(user, lineup, contestSingleEntry);
            Map<String, Object> resultMap = mapper.readValue(result, mapTypeReference);
            assertTrue(resultMap.get("contestId").equals(contestSingleEntry.getUrlId()));
            assertTrue((Integer) resultMap.get("lineupId") == lineup.getId());
            assertTrue((Integer) resultMap.get("code") == GlobalConstants.CONTEST_ENTRY_SUCCESS);

            entries = contestDao.findEntries(lineup, contestSingleEntry);
            assertTrue(entries.isEmpty());
            assertTrue(userDao.getUserWallet(user).getUsd() == 10000);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRemoveLineupFromContest_SingleEntry_NotOpen() {
        startHazelcast();

        // Set up Contests
        Contest contestSingleEntry = new Contest(ContestType.DOUBLE_UP, "1", League.NFL, 10, true, 100, 1, 5000000, sportEventGrouping, new ArrayList<>(), null);
        contestSingleEntry.setContestState(ContestState.locked);
        contestDao.saveContest(contestSingleEntry);

        Entry entry2 = new Entry(user, contestSingleEntry, lineup);
        entry2.setPoints(99);
        Ebean.save(entry2);

        // Manually deduct money
        try {
            userDao.minusUsd(user, contestSingleEntry.getEntryFee());
        } catch (WalletException e) {
            fail(e.getMessage());
        }

        List<Entry> entries = contestDao.findEntries(lineup, contestSingleEntry);
        assertTrue(entries.size() == 1);

        try {
            String result = lineupManager.removeLineupFromContest(user, lineup, contestSingleEntry);
            Map<String, Object> resultMap = mapper.readValue(result, mapTypeReference);
            assertTrue(resultMap.get("contestId").equals(contestSingleEntry.getUrlId()));
            assertTrue((Integer) resultMap.get("lineupId") == lineup.getId());
            assertTrue((Integer) resultMap.get("code") == GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN);
            assertTrue(resultMap.get("description").equals("The specified contest is not open."));

            entries = contestDao.findEntries(lineup, contestSingleEntry);
            assertTrue(entries.size() == 1);
            assertTrue(userDao.getUserWallet(user).getUsd() == 9900);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRemoveLineupFromContest_MultiEntry_NotOpen() {
        startHazelcast();

        // Set up Contests
        Contest contestSingleEntry = new Contest(ContestType.GPP, "1", League.NFL, 10, true, 100, 2, 5000000, sportEventGrouping, new ArrayList<>(), null);
        contestSingleEntry.setContestState(ContestState.locked);
        contestDao.saveContest(contestSingleEntry);

        Entry entry2 = new Entry(user, contestSingleEntry, lineup);
        entry2.setPoints(99);
        Ebean.save(entry2);

        // Manually deduct money
        try {
            userDao.minusUsd(user, contestSingleEntry.getEntryFee());
        } catch (WalletException e) {
            fail(e.getMessage());
        }

        Entry entry3 = new Entry(user, contestSingleEntry, lineup);
        entry3.setPoints(99);
        Ebean.save(entry3);

        // Manually deduct money
        try {
            userDao.minusUsd(user, contestSingleEntry.getEntryFee());
        } catch (WalletException e) {
            fail(e.getMessage());
        }

        List<Entry> entries = contestDao.findEntries(lineup, contestSingleEntry);
        assertTrue(entries.size() == 2);

        try {
            String result = lineupManager.removeLineupFromContest(user, lineup, contestSingleEntry);
            Map<String, Object> resultMap = mapper.readValue(result, mapTypeReference);
            assertTrue(resultMap.get("contestId").equals(contestSingleEntry.getUrlId()));
            assertTrue((Integer) resultMap.get("lineupId") == lineup.getId());
            assertTrue((Integer) resultMap.get("code") == GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN);
            assertTrue(resultMap.get("description").equals("The specified contest is not open."));

            entries = contestDao.findEntries(lineup, contestSingleEntry);
            assertTrue(entries.size() == 2);
            assertTrue(userDao.getUserWallet(user).getUsd() == 9800);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateLineup_AllContests_Open() {
        contest.setContestState(ContestState.open);
        contestDao.saveContest(contest);

        Athlete patriotsDefense = new Athlete(10, "Patriots", "Defense", patriots, "0");
        sportsDao.saveAthlete(patriotsDefense);
        Athlete demaryiusThomas = new Athlete(11, "Demaryius", "Thomas", broncos, "11");
        sportsDao.saveAthlete(demaryiusThomas);
        Athlete janikowski = new Athlete(12, "Sebastian", "Janikowski", raiders, "12");
        sportsDao.saveAthlete(janikowski);
        Athlete stevenRidley = new Athlete(13, "Steven", "Ridley", patriots, "13");
        sportsDao.saveAthlete(stevenRidley);
        Athlete rayRice = new Athlete(14, "Ray", "Rice", ravens, "14");
        sportsDao.saveAthlete(rayRice);
        Athlete dannyAmendola = new Athlete(15, "Danny", "Amendola", patriots, "15");
        sportsDao.saveAthlete(dannyAmendola);
        Athlete aaronDobson = new Athlete(16, "Aaron", "Dobson", patriots, "16");
        sportsDao.saveAthlete(aaronDobson);

        AthleteSalary patriotsSalary = new AthleteSalary(patriotsDefense, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(patriotsSalary);
        AthleteSalary demaryiusThomasSalary = new AthleteSalary(demaryiusThomas, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(demaryiusThomasSalary);
        AthleteSalary janikowskiSalary = new AthleteSalary(janikowski, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(janikowskiSalary);
        AthleteSalary stevenRidleySalary = new AthleteSalary(stevenRidley, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(stevenRidleySalary);
        AthleteSalary rayRiceSalary = new AthleteSalary(rayRice, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(rayRiceSalary);
        AthleteSalary dannyAmendolaSalary = new AthleteSalary(dannyAmendola, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(dannyAmendolaSalary);
        AthleteSalary aaronDobsonSalary = new AthleteSalary(aaronDobson, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(aaronDobsonSalary);

        AthleteSportEventInfo athleteSportEventInfoPatriots = new AthleteSportEventInfo(sportEvent, patriotsDefense, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoPatriots);
        AthleteSportEventInfo athleteSportEventInfoDemaryiusThomas = new AthleteSportEventInfo(sportEvent2, demaryiusThomas, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDemaryiusThomas);
        AthleteSportEventInfo athleteSportEventInfoJanikowski = new AthleteSportEventInfo(sportEvent2, janikowski, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoJanikowski);
        AthleteSportEventInfo athleteSportEventInfoStevanRidley = new AthleteSportEventInfo(sportEvent, stevenRidley, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoStevanRidley);
        AthleteSportEventInfo athleteSportEventInfoRayRice = new AthleteSportEventInfo(sportEvent, rayRice, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRayRice);
        AthleteSportEventInfo athleteSportEventInfoDannyAmendola = new AthleteSportEventInfo(sportEvent, dannyAmendola, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDannyAmendola);
        AthleteSportEventInfo athleteSportEventInfoAaronDobson = new AthleteSportEventInfo(sportEvent, aaronDobson, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoAaronDobson);

        try {
            String result = lineupManager.updateLineup(user, lineup, Arrays.asList(
                    new LineupSpot(patriotsDefense, Position.FB_DEFENSE, athleteSportEventInfoPatriots),
                    new LineupSpot(demaryiusThomas, Position.FB_WIDE_RECEIVER, athleteSportEventInfoDemaryiusThomas),
                    new LineupSpot(janikowski, Position.FB_KICKER, athleteSportEventInfoJanikowski),
                    new LineupSpot(stevenRidley, Position.FB_RUNNINGBACK, athleteSportEventInfoStevanRidley),
                    new LineupSpot(rayRice, Position.FB_RUNNINGBACK, athleteSportEventInfoRayRice),
                    new LineupSpot(dannyAmendola, Position.FB_WIDE_RECEIVER, athleteSportEventInfoDannyAmendola),
                    new LineupSpot(aaronDobson, Position.FB_FLEX, athleteSportEventInfoAaronDobson),
                    new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                    new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)
            ), Arrays.asList(entry));

            Map<String, Object> resultMap = mapper.readValue(result, mapTypeReference);

            assertTrue((Integer) resultMap.get("code") == GlobalConstants.CONTEST_ENTRY_SUCCESS);
            assertTrue(entry.getLineup().equals(lineup));

            List<LineupSpot> lineupSpots = lineup.getLineupSpots();
            assertTrue(lineupSpots.size() == 9);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateLineup_SubsetOfContests_Open() {
        Entry entry2 = new Entry(user, contest, lineup);
        entry2.setPoints(100);
        Ebean.save(entry2);

        contest.setContestState(ContestState.open);
        contestDao.saveContest(contest);

        Athlete patriotsDefense = new Athlete(10, "Patriots", "Defense", patriots, "0");
        sportsDao.saveAthlete(patriotsDefense);
        Athlete demaryiusThomas = new Athlete(11, "Demaryius", "Thomas", broncos, "11");
        sportsDao.saveAthlete(demaryiusThomas);
        Athlete janikowski = new Athlete(12, "Sebastian", "Janikowski", raiders, "12");
        sportsDao.saveAthlete(janikowski);
        Athlete stevenRidley = new Athlete(13, "Steven", "Ridley", patriots, "13");
        sportsDao.saveAthlete(stevenRidley);
        Athlete rayRice = new Athlete(14, "Ray", "Rice", ravens, "14");
        sportsDao.saveAthlete(rayRice);
        Athlete dannyAmendola = new Athlete(15, "Danny", "Amendola", patriots, "15");
        sportsDao.saveAthlete(dannyAmendola);
        Athlete aaronDobson = new Athlete(16, "Aaron", "Dobson", patriots, "16");
        sportsDao.saveAthlete(aaronDobson);

        AthleteSalary patriotsSalary = new AthleteSalary(patriotsDefense, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(patriotsSalary);
        AthleteSalary demaryiusThomasSalary = new AthleteSalary(demaryiusThomas, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(demaryiusThomasSalary);
        AthleteSalary janikowskiSalary = new AthleteSalary(janikowski, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(janikowskiSalary);
        AthleteSalary stevenRidleySalary = new AthleteSalary(stevenRidley, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(stevenRidleySalary);
        AthleteSalary rayRiceSalary = new AthleteSalary(rayRice, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(rayRiceSalary);
        AthleteSalary dannyAmendolaSalary = new AthleteSalary(dannyAmendola, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(dannyAmendolaSalary);
        AthleteSalary aaronDobsonSalary = new AthleteSalary(aaronDobson, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(aaronDobsonSalary);

        AthleteSportEventInfo athleteSportEventInfoPatriots = new AthleteSportEventInfo(sportEvent, patriotsDefense, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoPatriots);
        AthleteSportEventInfo athleteSportEventInfoDemaryiusThomas = new AthleteSportEventInfo(sportEvent2, demaryiusThomas, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDemaryiusThomas);
        AthleteSportEventInfo athleteSportEventInfoJanikowski = new AthleteSportEventInfo(sportEvent2, janikowski, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoJanikowski);
        AthleteSportEventInfo athleteSportEventInfoStevanRidley = new AthleteSportEventInfo(sportEvent, stevenRidley, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoStevanRidley);
        AthleteSportEventInfo athleteSportEventInfoRayRice = new AthleteSportEventInfo(sportEvent, rayRice, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRayRice);
        AthleteSportEventInfo athleteSportEventInfoDannyAmendola = new AthleteSportEventInfo(sportEvent, dannyAmendola, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDannyAmendola);
        AthleteSportEventInfo athleteSportEventInfoAaronDobson = new AthleteSportEventInfo(sportEvent, aaronDobson, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoAaronDobson);

        try {
            String result = lineupManager.updateLineup(user, lineup, Arrays.asList(
                    new LineupSpot(patriotsDefense, Position.FB_DEFENSE, athleteSportEventInfoPatriots),
                    new LineupSpot(demaryiusThomas, Position.FB_WIDE_RECEIVER, athleteSportEventInfoDemaryiusThomas),
                    new LineupSpot(janikowski, Position.FB_KICKER, athleteSportEventInfoJanikowski),
                    new LineupSpot(stevenRidley, Position.FB_RUNNINGBACK, athleteSportEventInfoStevanRidley),
                    new LineupSpot(rayRice, Position.FB_RUNNINGBACK, athleteSportEventInfoRayRice),
                    new LineupSpot(dannyAmendola, Position.FB_WIDE_RECEIVER, athleteSportEventInfoDannyAmendola),
                    new LineupSpot(aaronDobson, Position.FB_FLEX, athleteSportEventInfoAaronDobson),
                    new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                    new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)
            ), Arrays.asList(entry));

            Map<String, Object> resultMap = mapper.readValue(result, mapTypeReference);

            assertTrue((Integer) resultMap.get("code") == GlobalConstants.CONTEST_ENTRY_SUCCESS);
            assertTrue(!entry.getLineup().equals(lineup));

            List<Lineup> lineups = contestDao.findLineups(user, contest);
            assertTrue(lineups.size() == 2);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateLineup_NotOpenOrLocked() {
        contest.setContestState(ContestState.active);
        contestDao.saveContest(contest);

        Athlete patriotsDefense = new Athlete(10, "Patriots", "Defense", patriots, "0");
        sportsDao.saveAthlete(patriotsDefense);
        Athlete demaryiusThomas = new Athlete(11, "Demaryius", "Thomas", broncos, "11");
        sportsDao.saveAthlete(demaryiusThomas);
        Athlete janikowski = new Athlete(12, "Sebastian", "Janikowski", raiders, "12");
        sportsDao.saveAthlete(janikowski);
        Athlete stevenRidley = new Athlete(13, "Steven", "Ridley", patriots, "13");
        sportsDao.saveAthlete(stevenRidley);
        Athlete rayRice = new Athlete(14, "Ray", "Rice", ravens, "14");
        sportsDao.saveAthlete(rayRice);
        Athlete dannyAmendola = new Athlete(15, "Danny", "Amendola", patriots, "15");
        sportsDao.saveAthlete(dannyAmendola);
        Athlete aaronDobson = new Athlete(16, "Aaron", "Dobson", patriots, "16");
        sportsDao.saveAthlete(aaronDobson);

        AthleteSalary patriotsSalary = new AthleteSalary(patriotsDefense, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(patriotsSalary);
        AthleteSalary demaryiusThomasSalary = new AthleteSalary(demaryiusThomas, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(demaryiusThomasSalary);
        AthleteSalary janikowskiSalary = new AthleteSalary(janikowski, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(janikowskiSalary);
        AthleteSalary stevenRidleySalary = new AthleteSalary(stevenRidley, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(stevenRidleySalary);
        AthleteSalary rayRiceSalary = new AthleteSalary(rayRice, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(rayRiceSalary);
        AthleteSalary dannyAmendolaSalary = new AthleteSalary(dannyAmendola, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(dannyAmendolaSalary);
        AthleteSalary aaronDobsonSalary = new AthleteSalary(aaronDobson, sportEventGrouping, 300000);
        sportsDao.saveAthleteSalary(aaronDobsonSalary);

        AthleteSportEventInfo athleteSportEventInfoPatriots = new AthleteSportEventInfo(sportEvent, patriotsDefense, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoPatriots);
        AthleteSportEventInfo athleteSportEventInfoDemaryiusThomas = new AthleteSportEventInfo(sportEvent2, demaryiusThomas, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDemaryiusThomas);
        AthleteSportEventInfo athleteSportEventInfoJanikowski = new AthleteSportEventInfo(sportEvent2, janikowski, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoJanikowski);
        AthleteSportEventInfo athleteSportEventInfoStevanRidley = new AthleteSportEventInfo(sportEvent, stevenRidley, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoStevanRidley);
        AthleteSportEventInfo athleteSportEventInfoRayRice = new AthleteSportEventInfo(sportEvent, rayRice, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRayRice);
        AthleteSportEventInfo athleteSportEventInfoDannyAmendola = new AthleteSportEventInfo(sportEvent, dannyAmendola, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDannyAmendola);
        AthleteSportEventInfo athleteSportEventInfoAaronDobson = new AthleteSportEventInfo(sportEvent, aaronDobson, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoAaronDobson);

        try {
            String result = lineupManager.updateLineup(user, lineup, Arrays.asList(
                    new LineupSpot(patriotsDefense, Position.FB_DEFENSE, athleteSportEventInfoPatriots),
                    new LineupSpot(demaryiusThomas, Position.FB_WIDE_RECEIVER, athleteSportEventInfoDemaryiusThomas),
                    new LineupSpot(janikowski, Position.FB_KICKER, athleteSportEventInfoJanikowski),
                    new LineupSpot(stevenRidley, Position.FB_RUNNINGBACK, athleteSportEventInfoStevanRidley),
                    new LineupSpot(rayRice, Position.FB_RUNNINGBACK, athleteSportEventInfoRayRice),
                    new LineupSpot(dannyAmendola, Position.FB_WIDE_RECEIVER, athleteSportEventInfoDannyAmendola),
                    new LineupSpot(aaronDobson, Position.FB_FLEX, athleteSportEventInfoAaronDobson),
                    new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                    new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)
            ), Arrays.asList(entry));

            Map<String, Object> resultMap = mapper.readValue(result, mapTypeReference);

            assertTrue((Integer) resultMap.get("code") == GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_STARTED);
            assertTrue(resultMap.get("description").equals("The lineup you are trying to edit has entries in contests that have already started."));
            assertTrue(entry.getLineup().equals(lineup));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
