package managers.views;

import service.ContestLiveDrillinService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ContestDao;
import dao.IContestDao;
import dao.ISportsDao;
import dao.SportsDao;
import models.contest.*;
import models.sports.*;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by dmaclean on 6/26/14.
 */
public class ContestLiveDrillinManagerTest extends BaseTest {

    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;
    private ContestLiveDrillinService manager;
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Team team;
    private LineupSpot lineupSpot;
    private LineupSpot lineupSpot2;
    private Lineup lineup;
    private Lineup lineup2;
    private Entry entry;
    private Entry entry2;
    private User user;
    private User user2;
    private Contest contest;
    private SportEvent sportEvent;
    private ContestGrouping grouping;

    ISportsDao sportsDao;
    IContestDao contestDao;

    ObjectMapper mapper = new ObjectMapper();
    TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
    };

    @Before
    public void setUp() {
        sportsDao = new SportsDao();
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        manager = new ContestLiveDrillinService();

        // Set up Team
        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        Ebean.save(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", team, "87");
        athleteGronk.setPositions(Arrays.asList(Position.FB_TIGHT_END));
        Ebean.save(athleteGronk);

        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        user2 = new User();
        user2.setEmail("dmaclean82@gmail.com");
        user2.setFirstName("Dan");
        user2.setLastName("MacLean");
        user2.setPassword("test");
        user2.setUserName("dmaclean");
        Ebean.save(user2);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "{\"homeId\":\"236\",\"homeTeam\":\"Sea\",\"awayId\":\"233\",\"awayTeam\":\"Min\",\"homeScore\":0,\"awayScore\":0}",
                "{\"homeId\":\"236\",\"homeTeam\":\"Sea\",\"awayId\":\"233\",\"awayTeam\":\"Min\",\"homeScore\":0,\"awayScore\":0}", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, Arrays.asList(new ContestPayout(1, 1, 180)), null);
        Ebean.save(contest);

        Map<String, Object> timeline = new HashMap<>();
        timeline.put("description", "Brady throws 50 yards for touchdown.");
        timeline.put("timestamp", new Date().getTime());
        timeline.put("fpChange", "+6.0");
        timeline.put("athleteSportEventInfoId", 1);
        timeline.put("id", timeline.get("timestamp") + "_" + timeline.get("athleteSportEventInfoId"));
        timeline.put("published", false);

        try {
            athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.001"),
                    sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK), mapper.writeValueAsString(Arrays.asList(timeline)));
            athleteSportEventInfoBrady.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
            Ebean.save(athleteSportEventInfoBrady);
            athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("0.00"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_TIGHT_END), "[]");
            athleteSportEventInfoGronk.setIndicator(GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
            Ebean.save(athleteSportEventInfoGronk);
        }
        catch(Exception e) {
            fail(e.getMessage());
        }

        // Set up Lineup and LineupSpot
        lineupSpot = new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady);
        lineupSpot2 = new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(lineupSpot);
        lineupSpots.add(lineupSpot2);
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        List<LineupSpot> lineupSpots2 = new ArrayList<>();
        lineupSpots2.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots2.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        lineup2 = new Lineup("My other lineup", user2, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots2);
        Ebean.save(lineup2);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100.501);
        Ebean.save(entry);

        entry2 = new Entry(user2, contest, lineup2);
        entry2.setPoints(90.501);
        Ebean.save(entry2);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);
        Ebean.save(lineup2);

        contest.setCurrentEntries(2);
        Ebean.save(contest);
    }

    @After
    public void tearDown() {
        manager = null;

        athleteTomBrady = null;
        athleteGronk = null;
        team = null;
        lineup = null;
        lineupSpot = null;
        lineupSpot2 = null;
        entry = null;
        entry2 = null;
        user = null;
        user2 = null;
        contest = null;
        sportEvent = null;
        grouping = null;

        athleteSportEventInfoBrady = null;
        athleteSportEventInfoGronk = null;
    }

    @Test
    public void testGetInitialLoadAsJson() {
        try {
            String result = manager.getInitialLoadAsJson(contest);
            Map<String, Object> resultList = mapper.readValue(result, typeReference);
            assertTrue(resultList.containsKey("contest"));

            Map<String, Object> contestData = (Map<String, Object>) resultList.get("contest");
            assertTrue(contestData.get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) contestData.get("entryFee") == 100);
            assertTrue((Integer) contestData.get("currentEntries") == 2);
            assertTrue((Integer) contestData.get("prizePool") == 180);
            assertTrue(((List<ContestPayout>) contestData.get("payouts")).size() == 1);
            assertTrue(!contestData.containsKey("opp"));
            assertEquals(contest.getContestState().getName(), contestData.get("contestState"));
            assertEquals(true, contestData.get("startTime") instanceof Long);

            List<Map<String, Object>> entries = (List<Map<String, Object>>) resultList.get("entries");
            assertTrue(entries.size() == 2);
            Map<String, Object> entry1 = entries.get(0);
            assertTrue((Integer) entry1.get("id") == entry.getId());
            assertTrue(entry1.get("user").equals("terrorsquid"));
            assertTrue((Integer) entry1.get("unitsRemaining") == 120);
            assertEquals("100.5", entry1.get("fpp").toString());

            Map<String, Object> entry2Map = entries.get(1);
            assertTrue((Integer) entry2Map.get("id") == entry2.getId());
            assertTrue(entry2Map.get("user").equals("dmaclean"));
            assertTrue((Integer) entry2Map.get("unitsRemaining") == 120);
            assertEquals("90.5", entry2Map.get("fpp").toString());

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetInitialLoadAsJson_H2H() {
        contest.setContestType(ContestType.H2H);
        Ebean.save(contest);

        try {
            manager.setUser(user);
            String result = manager.getInitialLoadAsJson(contest);
            Map<String, Object> resultList = mapper.readValue(result, typeReference);
            assertTrue(resultList.containsKey("contest"));

            Map<String, Object> contestData = (Map<String, Object>) resultList.get("contest");
            assertTrue(contestData.get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) contestData.get("entryFee") == 100);
            assertTrue((Integer) contestData.get("currentEntries") == 2);
            assertTrue((Integer) contestData.get("prizePool") == 180);
            assertTrue(((List<ContestPayout>) contestData.get("payouts")).size() == 1);
            assertTrue(contestData.get("opp").equals(user2.getUserName()));
            assertEquals(contest.getContestState().getName(), contestData.get("contestState"));
            assertEquals(true, contestData.get("startTime") instanceof Long);

            List<Map<String, Object>> entries = (List<Map<String, Object>>) resultList.get("entries");
            assertTrue(entries.size() == 2);
            Map<String, Object> entry1 = entries.get(0);
            assertTrue((Integer) entry1.get("id") == entry.getId());
            assertTrue(entry1.get("user").equals("terrorsquid"));
            assertTrue((Integer) entry1.get("unitsRemaining") == 120);
            assertEquals("100.5", entry1.get("fpp").toString());

            Map<String, Object> entry2Map = entries.get(1);
            assertTrue((Integer) entry2Map.get("id") == entry2.getId());
            assertTrue(entry2Map.get("user").equals("dmaclean"));
            assertTrue((Integer) entry2Map.get("unitsRemaining") == 120);
            assertEquals("90.5", entry2Map.get("fpp").toString());

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetInitialLoadAsJson_H2H_OnlyOneEntry() {
        contest.setContestType(ContestType.H2H);
        Ebean.save(contest);

        entry2.setContest(null);
        contestDao.saveEntry(entry2);

        try {
            manager.setUser(user);
            String result = manager.getInitialLoadAsJson(contest);
            Map<String, Object> resultList = mapper.readValue(result, typeReference);
            assertTrue(resultList.containsKey("contest"));

            Map<String, Object> contestData = (Map<String, Object>) resultList.get("contest");
            assertTrue(contestData.get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) contestData.get("entryFee") == 100);
            assertTrue((Integer) contestData.get("currentEntries") == 2);
            assertTrue((Integer) contestData.get("prizePool") == 180);
            assertTrue(((List<ContestPayout>) contestData.get("payouts")).size() == 1);
            assertTrue(contestData.get("opp").equals(""));
            assertEquals(contest.getContestState().getName(), contestData.get("contestState"));
            assertEquals(true, contestData.get("startTime") instanceof Long);

            List<Map<String, Object>> entries = (List<Map<String, Object>>) resultList.get("entries");
            assertTrue(entries.size() == 1);
            Map<String, Object> entry1 = entries.get(0);
            assertTrue((Integer) entry1.get("id") == entry.getId());
            assertTrue(entry1.get("user").equals("terrorsquid"));
            assertTrue((Integer) entry1.get("unitsRemaining") == 120);
            assertEquals("100.5", entry1.get("fpp").toString());

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetEntryUpdateAsJson() {
        try {
            String result = manager.getEntryUpdateAsJson(entry);
            Map<String, Object> resultList = mapper.readValue(result, typeReference);
            assertTrue((Integer) resultList.get("id") == entry.getId());
            assertTrue(resultList.get("user").equals(entry.getUser().getUserName()));
            assertEquals("100.5", (resultList.get("fpp").toString()));
            assertTrue((Integer) resultList.get("unitsRemaining") == contestDao.calculateUnitsRemaining(entry));

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetSportEventUpdateAsJson() {
        try {
            String result = manager.getSportEventUpdateAsJson(sportEvent);
            Map<String, Object> resultList = mapper.readValue(result, typeReference);
            assertTrue((Integer) resultList.get("id") == sportEvent.getId());
            assertTrue(resultList.get("homeId").equals("236"));
            assertTrue(resultList.get("homeTeam").equals("Sea"));
            assertTrue(resultList.get("awayId").equals("233"));
            assertTrue(resultList.get("awayTeam").equals("Min"));
            assertTrue((Integer) resultList.get("homeScore") == 0);
            assertTrue((Integer) resultList.get("awayScore") == 0);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAthleteSportEventInfoUpdateAsJson() {
        try {
            String result = manager.getAthleteSportEventInfoUpdateAsJson(athleteSportEventInfoBrady);
            Map<String, Object> resultList = mapper.readValue(result, typeReference);
            assertTrue((Integer) resultList.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertEquals("10.0", resultList.get("fpp").toString());
            List<Map<String, Object>> boxscore = (ArrayList) resultList.get("stats");
            List<Map<String, Object>> timeline = (ArrayList) resultList.get("timeline");
            assertEquals(0, boxscore.size());
            assertEquals(1, timeline.size());
            assertTrue(resultList.get("firstName").equals(athleteSportEventInfoBrady.getAthlete().getFirstName()));
            assertTrue(resultList.get("lastName").equals(athleteSportEventInfoBrady.getAthlete().getLastName()));
            assertTrue((Integer) resultList.get("indicator") == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
            assertTrue((Integer) resultList.get("unitsRemaining") == athleteSportEventInfoBrady.getSportEvent().getUnitsRemaining());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
