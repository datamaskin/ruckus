package managers.views;

import service.ContestLiveOverviewService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ContestDao;
import dao.ISportsDao;
import models.contest.*;
import models.sports.*;
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
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 6/25/14.
 */
public class ContestLiveOverviewManagerTest extends BaseTest {
    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;
    ISportsDao sportsDao;
    ContestDao contestDao;
    private ContestLiveOverviewService manager;
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Team team;
    private Lineup lineup;
    private Lineup lineup2;
    private Entry entry;
    private Entry entry2;
    private Entry entry3;
    private User user;
    private User user2;
    private Contest contest;
    private Contest contest2;
    private Contest contest3;
    private SportEvent sportEvent;
    private ContestGrouping grouping;
    private ContestPayout contestPayout;
    private ContestPayout contestPayout2;
    private ContestPayout contestPayout3;

    SportEventGroupingType type;
    SportEventGrouping sportEventGrouping;

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        contestDao = new ContestDao(new ContestIdGeneratorImpl());

        manager = context.getBean("ContestLiveOverviewManager", ContestLiveOverviewService.class);

        // Set up Team
        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        sportsDao.saveAthlete(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", team, "87");
        sportsDao.saveAthlete(athleteGronk);

        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        user2 = new User();
        user2.setEmail("mwalsh@ruckusgaming.com");
        user2.setFirstName("Matt");
        user2.setLastName("Walsh");
        user2.setPassword("test");
        user2.setUserName("walshms");
        Ebean.save(user2);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up Contest Payout
        contestPayout = new ContestPayout(1, 1, 10000);
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        contestPayout2 = new ContestPayout(1, 1, 10000);
        ArrayList<ContestPayout> contestPayouts2 = new ArrayList<>();
        contestPayouts2.add(contestPayout2);

        contestPayout3 = new ContestPayout(1, 1, 10000);
        ArrayList<ContestPayout> contestPayouts3 = new ArrayList<>();
        contestPayouts3.add(contestPayout3);

        // Contest start time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest.setStartTime(cal.getTime());
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        contest2 = new Contest(ContestType.H2H, "212313", League.NFL, 3, true, 200, 1, 50000, sportEventGrouping, contestPayouts2, null);
        contest2.setStartTime(cal.getTime());
        contest2.setContestState(ContestState.active);
        Ebean.save(contest2);

        contest3 = new Contest(ContestType.DOUBLE_UP, "212314", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts3, null);
        contest3.setStartTime(cal.getTime());
        contest3.setContestState(ContestState.complete);
        Ebean.save(contest3);

        contest.setCurrentEntries(1);
        Ebean.save(contest);

        contest2.setCurrentEntries(2);
        Ebean.save(contest2);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("0.00"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK), "[]");
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("0.00"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_TIGHT_END), "[]");
        Ebean.save(athleteSportEventInfoGronk);

        // Set up Lineup and LineupSpot
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady)
        ));
        Ebean.save(lineup);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100.05);
        Ebean.save(entry);

        lineup.setEntries(Arrays.asList(entry));
        Ebean.save(lineup);

        lineup2 = new Lineup("My Second Lineup", user2, League.NFL, contest.getSportEventGrouping());
        lineup2.setEntries(Arrays.asList(entry2, entry3));
        lineup2.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)
        ));
        Ebean.save(lineup2);

        entry2 = new Entry(user2, contest2, lineup2);
        entry2.setPoints(90.05);
        Ebean.save(entry2);

        lineup2.setEntries(Arrays.asList(entry2));
        Ebean.save(lineup2);
    }

    @After
    public void tearDown() {
        manager = null;

        athleteTomBrady = null;
        athleteGronk = null;
        team = null;
        lineup = null;
        lineup2 = null;
        entry = null;
        entry2 = null;
        entry3 = null;
        user = null;
        contest = null;
        contest2 = null;
        contest3 = null;
        sportEvent = null;
        grouping = null;

        athleteSportEventInfoBrady = null;
        athleteSportEventInfoGronk = null;
    }

    /**
     * 2 Lineups entered in 2 contests across 3 entries.
     */
    @Test
    public void testGetOverviewAsJson() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
        };

        try {
            String result = manager.getOverviewAsJson(user);

            List<Map> resultList = mapper.readValue(result, typeRef);
            assertTrue(resultList.size() == 1);
            assertTrue((Integer) resultList.get(0).get("position") == 1);
            assertTrue((Double) resultList.get(0).get("fpp") == 100.05);
            assertTrue((Integer) resultList.get(0).get("payout") == 10000);
            assertTrue((Integer) resultList.get(0).get("projectedPayout") == 10000);
            assertTrue((Integer) resultList.get(0).get("unitsRemaining") == 60);
            assertTrue(resultList.get(0).get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) resultList.get(0).get("entryFee") == 100);
            assertTrue((Integer) ((Map) resultList.get(0).get("contestType")).get("id") == ContestType.DOUBLE_UP.getId());
            assertTrue(((Map) resultList.get(0).get("contestType")).get("name").equals(ContestType.DOUBLE_UP.getName()));
            assertTrue(((Map) resultList.get(0).get("contestType")).get("abbr").equals(ContestType.DOUBLE_UP.getAbbr()));
            assertTrue(resultList.get(0).get("contestId").equals(contest.getUrlId()));
            assertTrue((Integer) resultList.get(0).get("currentEntries") == contest.getCurrentEntries());
            assertTrue((Integer) resultList.get(0).get("multiplier") == 1);
            assertTrue((Integer) resultList.get(0).get("prizePool") == 10000);
            assertTrue(resultList.get(0).get("contestState").equals(contest.getContestState().getName()));
            assertTrue((Integer) resultList.get(0).get("lineupId") == 1);
            assertTrue(!resultList.get(0).containsKey("opp"));
            assertEquals(resultList.get(0).get("capacity"), 2);

            long time = contest.getStartTime().getTime() - (new Date()).getTime();
            assertTrue(((Integer) resultList.get(0).get("timeUntilStart")) > 0);
            assertTrue(((Long) resultList.get(0).get("startTime") > 0));

            List<Map<String, Object>> contestPayouts = (List<Map<String, Object>>) resultList.get(0).get("payouts");
            assertTrue(contestPayouts.size() == 1 && (Integer) contestPayouts.get(0).get("leadingPosition") == contestPayout.getLeadingPosition()
                    && (Integer) contestPayouts.get(0).get("trailingPosition") == contestPayout.getTrailingPosition()
                    && (Integer) contestPayouts.get(0).get("payoutAmount") == contestPayout.getPayoutAmount());

//            assertTrue((Integer)resultList.get(1).get("position") == 1);
//            assertTrue((Double) resultList.get(1).get("fpp") == 120.0);
//            assertTrue((Integer) resultList.get(1).get("payout") == 0);
//            assertTrue((Integer) resultList.get(1).get("projectedPayout") == 0);
//            assertTrue((Integer) resultList.get(1).get("unitsRemaining") == 60);
//            assertTrue(resultList.get(1).get("league").equals(League.NFL.getAbbreviation()));
//            assertTrue((Integer) resultList.get(1).get("buyinAmount") == 200);
//            assertTrue( (Integer) ((Map) resultList.get(1).get("contestType")).get("id") == 2);
//            assertTrue( ((Map) resultList.get(1).get("contestType")).get("name").equals(ContestType.H2H.getName()));
//            assertTrue( ((Map) resultList.get(1).get("contestType")).get("abbr").equals(ContestType.H2H.getAbbr()));
//            assertTrue(resultList.get(1).get("contestId").equals(contest2.getUrlId()));
//            assertTrue((Integer) resultList.get(1).get("currentEntries") == contest2.getCurrentEntries());
//            assertTrue((Integer) resultList.get(1).get("multiplier") == 2);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * 2 Lineups entered in 2 contests across 3 entries.
     */
    @Test
    public void testGetOverviewAsJson_H2H() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
        };

        try {
            Entry entryH2HOpp = new Entry(user, contest2, lineup);
            entryH2HOpp.setPoints(70);
            Ebean.save(entryH2HOpp);

            String result = manager.getOverviewAsJson(user2);

            List<Map> resultList = mapper.readValue(result, typeRef);
            assertTrue(resultList.size() == 1);
            assertTrue((Integer) resultList.get(0).get("position") == 1);
            assertTrue((Double) resultList.get(0).get("fpp") == 90.05);
            assertTrue((Integer) resultList.get(0).get("payout") == 10000);
            assertTrue((Integer) resultList.get(0).get("projectedPayout") == 10000);
            assertTrue((Integer) resultList.get(0).get("unitsRemaining") == 120);
            assertTrue(resultList.get(0).get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) resultList.get(0).get("entryFee") == 200);
            assertTrue((Integer) ((Map) resultList.get(0).get("contestType")).get("id") == ContestType.H2H.getId());
            assertTrue(((Map) resultList.get(0).get("contestType")).get("name").equals(ContestType.H2H.getName()));
            assertTrue(((Map) resultList.get(0).get("contestType")).get("abbr").equals(ContestType.H2H.getAbbr()));
            assertTrue(resultList.get(0).get("contestId").equals(contest2.getUrlId()));
            assertTrue((Integer) resultList.get(0).get("currentEntries") == contest2.getCurrentEntries());
            assertTrue((Integer) resultList.get(0).get("multiplier") == 1);
            assertTrue((Integer) resultList.get(0).get("prizePool") == 10000);
            assertTrue(resultList.get(0).get("contestState").equals(contest2.getContestState().getName()));
            assertTrue((Integer) resultList.get(0).get("lineupId") == lineup2.getId());
            assertTrue(resultList.get(0).get("opp").equals(user.getUserName()));
            assertEquals(resultList.get(0).get("capacity"), 3);

            long time = contest.getStartTime().getTime() - (new Date()).getTime();
            assertTrue(((Integer) resultList.get(0).get("timeUntilStart")) > 0);
            assertTrue(((Long) resultList.get(0).get("startTime") > 0));

            List<Map<String, Object>> contestPayouts = (List<Map<String, Object>>) resultList.get(0).get("payouts");
            assertTrue(contestPayouts.size() == 1 && (Integer) contestPayouts.get(0).get("leadingPosition") == contestPayout.getLeadingPosition()
                    && (Integer) contestPayouts.get(0).get("trailingPosition") == contestPayout.getTrailingPosition()
                    && (Integer) contestPayouts.get(0).get("payoutAmount") == contestPayout.getPayoutAmount());

//            assertTrue((Integer)resultList.get(1).get("position") == 1);
//            assertTrue((Double) resultList.get(1).get("fpp") == 120.0);
//            assertTrue((Integer) resultList.get(1).get("payout") == 0);
//            assertTrue((Integer) resultList.get(1).get("projectedPayout") == 0);
//            assertTrue((Integer) resultList.get(1).get("unitsRemaining") == 60);
//            assertTrue(resultList.get(1).get("league").equals(League.NFL.getAbbreviation()));
//            assertTrue((Integer) resultList.get(1).get("buyinAmount") == 200);
//            assertTrue( (Integer) ((Map) resultList.get(1).get("contestType")).get("id") == 2);
//            assertTrue( ((Map) resultList.get(1).get("contestType")).get("name").equals(ContestType.H2H.getName()));
//            assertTrue( ((Map) resultList.get(1).get("contestType")).get("abbr").equals(ContestType.H2H.getAbbr()));
//            assertTrue(resultList.get(1).get("contestId").equals(contest2.getUrlId()));
//            assertTrue((Integer) resultList.get(1).get("currentEntries") == contest2.getCurrentEntries());
//            assertTrue((Integer) resultList.get(1).get("multiplier") == 2);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetOverviewAsJSON_Completed() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
        };

        User user3 = new User();
        Ebean.save(user3);

        try {
            Entry e = new Entry(user3, contest3, lineup);
            e.setPoints(70);
            Ebean.save(e);

            String result = manager.getOverviewAsJson(user3);

            List<Map> resultList = mapper.readValue(result, typeRef);
            assertEquals(false, resultList.isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetOverviewAsJSON_Historical() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
        };

        User user3 = new User();
        Ebean.save(user3);

        contest3.setContestState(ContestState.history);
        contestDao.saveContest(contest3);

        try {
            Entry e = new Entry(user3, contest3, lineup);
            e.setPoints(70);
            Ebean.save(e);

            String result = manager.getOverviewAsJson(user3);

            List<Map> resultList = mapper.readValue(result, typeRef);
            assertEquals(true, resultList.isEmpty());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFindHistorical() {
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);

        Contest contest2 = new Contest(ContestType.DOUBLE_UP, "212315", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest2.setStartTime(cal.getTime());
        contest2.setContestState(ContestState.history);
        Ebean.save(contest2);

        cal.add(Calendar.DATE, -1);

        Contest contest3 = new Contest(ContestType.DOUBLE_UP, "212316", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest3.setStartTime(cal.getTime());
        contest3.setContestState(ContestState.history);
        Ebean.save(contest3);

        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        Ebean.save(lineup);

        Lineup lineup2 = new Lineup("My Lineup2", user, League.NFL, contest.getSportEventGrouping());
        Ebean.save(lineup2);

        Lineup lineup3 = new Lineup("My Lineup3", user, League.NFL, contest.getSportEventGrouping());
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

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        Date earliestStart = cal.getTime();

        List<Entry> entriesList = contestDao.findHistoricalEntries(
                user, ContestState.history, earliestStart);
        assertEquals(1, entriesList.size());
        assertEquals(entry2.getId(), entriesList.get(0).getId());

    }
}
