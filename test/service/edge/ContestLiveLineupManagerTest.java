package service.edge;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ContestDao;
import dao.IContestDao;
import dao.ISportsDao;
import dao.IUserDao;
import models.contest.*;
import models.sports.*;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.ContestLiveLineupService;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by dmaclean on 7/24/14.
 */
public class ContestLiveLineupManagerTest extends BaseTest {
    private ContestLiveLineupService manager;

    ObjectMapper mapper = new ObjectMapper();
    TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};

    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;

    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Team team;
    private Team team2;
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
    IUserDao userDao;

    @Before
    public void setUp() {
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        userDao = context.getBean("userDao", IUserDao.class);
        contestDao = new ContestDao(new ContestIdGeneratorImpl());

        manager = context.getBean("ContestLiveLineupManager", ContestLiveLineupService.class);

        // Set up Team
        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        team2 = new Team(League.NFL, "Baltimore", "Ravens", "BAL", 2);
        sportsDao.saveTeam(team2);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        sportsDao.saveAthlete(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", team, "87");
        athleteGronk.setPositions(Arrays.asList(Position.FB_TIGHT_END));
        sportsDao.saveAthlete(athleteGronk);

        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        userDao.saveUser(user);

        user2 = new User();
        user2.setEmail("dmaclean82@gmail.com");
        user2.setFirstName("Dan");
        user2.setLastName("MacLean");
        user2.setPassword("test");
        user2.setUserName("dmaclean");
        userDao.saveUser(user2);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "{\"homeId\":\"1\",\"homeTeam\":\"NE\",\"awayId\":\"2\",\"awayTeam\":\"BAL\",\"homeScore\":0,\"awayScore\":0}",
                "{\"homeId\":\"1\",\"homeTeam\":\"NE\",\"awayId\":\"2\",\"awayTeam\":\"BAL\",\"homeScore\":0,\"awayScore\":0}", 60, false, 2014, -1, 1);
        sportEvent.setTeams(Arrays.asList(team, team2));
        sportsDao.saveSportEvent(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        contestDao.saveContestGrouping(grouping);

        ContestPayout payout = new ContestPayout(1, 1, 180);
        List<ContestPayout> payouts = new ArrayList<ContestPayout>();
        payouts.add(payout);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, payouts, null);
        contestDao.saveContest(contest);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("0.00"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK), "[]");
        athleteSportEventInfoBrady.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("0.00"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_TIGHT_END), "[]");
        athleteSportEventInfoGronk.setIndicator(GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoGronk);

        // Set up Lineup and LineupSpot
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(Arrays.asList(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)));
        contestDao.saveLineup(lineup);

        lineup2 = new Lineup("My other lineup", user2, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(Arrays.asList(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)));
        contestDao.saveLineup(lineup2);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100.501);
        contestDao.saveEntry(entry);

        entry2 = new Entry(user2, contest, lineup2);
        entry2.setPoints(90.501);
        contestDao.saveEntry(entry2);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        contestDao.saveLineup(lineup);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);
        contestDao.saveLineup(lineup2);

        contest.setCurrentEntries(2);
        contestDao.saveContest(contest);
    }

    @After
    public void tearDown() {
        manager = null;

        athleteTomBrady = null;
        athleteGronk = null;
        team = null;
        lineup = null;
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
    public void testGetLinupAsJson_NE_Home() {
        try {
            List<Map<String, Object>> boxscoreBrady = mapper.readValue(athleteSportEventInfoBrady.getStats(), typeReference);
            for(Map<String, Object> entry: boxscoreBrady) {
                entry.put("amount", 1);
            }
            athleteSportEventInfoBrady.setStats(mapper.writeValueAsString(boxscoreBrady));

            List<Map<String, Object>> boxscoreGronk = mapper.readValue(athleteSportEventInfoGronk.getStats(), typeReference);
            for(Map<String, Object> entry: boxscoreGronk) {
                entry.put("amount", 1);
            }
            athleteSportEventInfoGronk.setStats(mapper.writeValueAsString(boxscoreGronk));
            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoBrady);
            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoGronk);

            String result = manager.getLinupAsJson(lineup.getId());


            List<Map<String, Object>> resultList = mapper.readValue(result, typeReference);

            assertTrue(resultList.size() == 2);

            Map<String, Object> athlete1 = resultList.get(0);
            assertTrue((Integer) athlete1.get("id") == athleteTomBrady.getStatProviderId());
            assertTrue((Integer) athlete1.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athlete1.get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue(athlete1.get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athlete1.get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue((Integer) athlete1.get("unitsRemaining") == athleteSportEventInfoBrady.getSportEvent().getUnitsRemaining());

            List<Map<String, Object>> statsList = mapper.readValue((String) athlete1.get("stats"), typeReference);
            assertEquals(8, statsList.size());
            assertEquals(GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, statsList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR, statsList.get(0).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, statsList.get(1).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_ABBR, statsList.get(1).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, statsList.get(2).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_ABBR, statsList.get(2).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, statsList.get(3).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_ABBR, statsList.get(3).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, statsList.get(4).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEPTIONS_ABBR, statsList.get(4).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, statsList.get(5).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_ABBR, statsList.get(5).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, statsList.get(6).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_ABBR, statsList.get(6).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, statsList.get(7).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_ABBR, statsList.get(7).get("abbr"));

            assertTrue(athlete1.get("fpp").toString().equals("0.0"));
            assertEquals("NEvBAL", athlete1.get("matchup"));
            assertTrue((Integer) athlete1.get("indicator") == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);

            Map<String, Object> athlete2 = resultList.get(1);
            assertTrue((Integer) athlete2.get("id") == athleteGronk.getStatProviderId());
            assertTrue((Integer) athlete2.get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());
            assertTrue(athlete2.get("position").equals(athleteGronk.getPositions().get(0).getAbbreviation()));
            assertTrue(athlete2.get("firstName").equals(athleteGronk.getFirstName()));
            assertTrue(athlete2.get("lastName").equals(athleteGronk.getLastName()));
            assertTrue((Integer) athlete2.get("unitsRemaining") == athleteSportEventInfoGronk.getSportEvent().getUnitsRemaining());

            statsList = mapper.readValue((String) athlete2.get("stats"), typeReference);
            assertEquals(8, statsList.size());
            assertEquals(GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, statsList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR, statsList.get(0).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, statsList.get(1).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_ABBR, statsList.get(1).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, statsList.get(2).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_ABBR, statsList.get(2).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, statsList.get(3).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_ABBR, statsList.get(3).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, statsList.get(4).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEPTIONS_ABBR, statsList.get(4).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, statsList.get(5).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_ABBR, statsList.get(5).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, statsList.get(6).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_ABBR, statsList.get(6).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, statsList.get(7).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_ABBR, statsList.get(7).get("abbr"));

            assertTrue(athlete2.get("fpp").toString().equals("0.0"));
            assertEquals("NEvBAL", athlete2.get("matchup"));
            assertTrue((Integer) athlete2.get("indicator") == GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLinupAsJson_NE_Away() {
        try {
            List<Map<String, Object>> boxscoreBrady = mapper.readValue(athleteSportEventInfoBrady.getStats(), typeReference);
            for(Map<String, Object> entry: boxscoreBrady) {
                entry.put("amount", 1);
            }
            athleteSportEventInfoBrady.setStats(mapper.writeValueAsString(boxscoreBrady));

            List<Map<String, Object>> boxscoreGronk = mapper.readValue(athleteSportEventInfoGronk.getStats(), typeReference);
            for(Map<String, Object> entry: boxscoreGronk) {
                entry.put("amount", 1);
            }
            athleteSportEventInfoGronk.setStats(mapper.writeValueAsString(boxscoreGronk));

            sportEvent.setShortDescription("{\"homeId\":\"2\",\"homeTeam\":\"BAL\",\"awayId\":\"1\",\"awayTeam\":\"NE\",\"homeScore\":0,\"awayScore\":0}");
            sportsDao.saveSportEvent(sportEvent);

            athleteSportEventInfoBrady.setSportEvent(sportEvent);
            athleteSportEventInfoGronk.setSportEvent(sportEvent);
            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoBrady);
            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoGronk);

            String result = manager.getLinupAsJson(lineup.getId());
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};

            List<Map<String, Object>> resultList = mapper.readValue(result, typeReference);

            assertTrue(resultList.size() == 2);

            Map<String, Object> athlete1 = resultList.get(0);
            assertTrue((Integer) athlete1.get("id") == athleteTomBrady.getStatProviderId());
            assertTrue((Integer) athlete1.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athlete1.get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue(athlete1.get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athlete1.get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue((Integer) athlete1.get("unitsRemaining") == athleteSportEventInfoBrady.getSportEvent().getUnitsRemaining());
            List<Map<String, Object>> statsList = mapper.readValue((String) athlete1.get("stats"), typeReference);
            assertEquals(8, statsList.size());
            assertEquals(GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, statsList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR, statsList.get(0).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, statsList.get(1).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_ABBR, statsList.get(1).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, statsList.get(2).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_ABBR, statsList.get(2).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, statsList.get(3).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_ABBR, statsList.get(3).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, statsList.get(4).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEPTIONS_ABBR, statsList.get(4).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, statsList.get(5).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_ABBR, statsList.get(5).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, statsList.get(6).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_ABBR, statsList.get(6).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, statsList.get(7).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_ABBR, statsList.get(7).get("abbr"));


            assertTrue(athlete1.get("fpp").toString().equals("0.0"));
            assertEquals("NE@BAL", athlete1.get("matchup"));
            assertTrue((Integer) athlete1.get("indicator") == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);

            Map<String, Object> athlete2 = resultList.get(1);
            assertTrue((Integer) athlete2.get("id") == athleteGronk.getStatProviderId());
            assertTrue((Integer) athlete2.get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());
            assertTrue(athlete2.get("position").equals(athleteGronk.getPositions().get(0).getAbbreviation()));
            assertTrue(athlete2.get("firstName").equals(athleteGronk.getFirstName()));
            assertTrue(athlete2.get("lastName").equals(athleteGronk.getLastName()));
            assertTrue((Integer) athlete2.get("unitsRemaining") == athleteSportEventInfoGronk.getSportEvent().getUnitsRemaining());

            statsList = mapper.readValue((String) athlete2.get("stats"), typeReference);
            assertEquals(8, statsList.size());
            assertEquals(GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, statsList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR, statsList.get(0).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, statsList.get(1).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_ABBR, statsList.get(1).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, statsList.get(2).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_ABBR, statsList.get(2).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, statsList.get(3).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_ABBR, statsList.get(3).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, statsList.get(4).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEPTIONS_ABBR, statsList.get(4).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, statsList.get(5).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_ABBR, statsList.get(5).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, statsList.get(6).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_ABBR, statsList.get(6).get("abbr"));
            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, statsList.get(7).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_ABBR, statsList.get(7).get("abbr"));

            assertTrue(athlete2.get("fpp").toString().equals("0.0"));
            assertEquals("NE@BAL", athlete2.get("matchup"));
            assertTrue((Integer) athlete2.get("indicator") == GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLinupAsJson_NE_Home_NoStatsYet() {
        try {
            String result = manager.getLinupAsJson(lineup.getId());

            List<Map<String, Object>> resultList = mapper.readValue(result, typeReference);

            assertTrue(resultList.size() == 2);

            Map<String, Object> athlete1 = resultList.get(0);
            assertTrue((Integer) athlete1.get("id") == athleteTomBrady.getStatProviderId());
            assertTrue((Integer) athlete1.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athlete1.get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue(athlete1.get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athlete1.get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue((Integer) athlete1.get("unitsRemaining") == athleteSportEventInfoBrady.getSportEvent().getUnitsRemaining());

            List<Map<String, Object>> statsList = mapper.readValue((String) athlete1.get("stats"), typeReference);
            assertEquals(0, statsList.size());

            assertTrue(athlete1.get("fpp").toString().equals("0.0"));
            assertEquals("NEvBAL", athlete1.get("matchup"));
            assertTrue((Integer) athlete1.get("indicator") == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);

            Map<String, Object> athlete2 = resultList.get(1);
            assertTrue((Integer) athlete2.get("id") == athleteGronk.getStatProviderId());
            assertTrue((Integer) athlete2.get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());
            assertTrue(athlete2.get("position").equals(athleteGronk.getPositions().get(0).getAbbreviation()));
            assertTrue(athlete2.get("firstName").equals(athleteGronk.getFirstName()));
            assertTrue(athlete2.get("lastName").equals(athleteGronk.getLastName()));
            assertTrue((Integer) athlete2.get("unitsRemaining") == athleteSportEventInfoGronk.getSportEvent().getUnitsRemaining());

            statsList = mapper.readValue((String) athlete2.get("stats"), typeReference);
            assertEquals(0, statsList.size());

            assertTrue(athlete2.get("fpp").toString().equals("0.0"));
            assertEquals("NEvBAL", athlete2.get("matchup"));
            assertTrue((Integer) athlete2.get("indicator") == GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLinupAsJson_NE_Away_NoStatsYet() {
        try {
            sportEvent.setShortDescription("{\"homeId\":\"2\",\"homeTeam\":\"BAL\",\"awayId\":\"1\",\"awayTeam\":\"NE\",\"homeScore\":0,\"awayScore\":0}");
            sportsDao.saveSportEvent(sportEvent);

            athleteSportEventInfoBrady.setSportEvent(sportEvent);
            athleteSportEventInfoGronk.setSportEvent(sportEvent);
            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoBrady);
            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoGronk);

            String result = manager.getLinupAsJson(lineup.getId());
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};

            List<Map<String, Object>> resultList = mapper.readValue(result, typeReference);

            assertTrue(resultList.size() == 2);

            Map<String, Object> athlete1 = resultList.get(0);
            assertTrue((Integer) athlete1.get("id") == athleteTomBrady.getStatProviderId());
            assertTrue((Integer) athlete1.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(athlete1.get("position").equals(athleteTomBrady.getPositions().get(0).getAbbreviation()));
            assertTrue(athlete1.get("firstName").equals(athleteTomBrady.getFirstName()));
            assertTrue(athlete1.get("lastName").equals(athleteTomBrady.getLastName()));
            assertTrue((Integer) athlete1.get("unitsRemaining") == athleteSportEventInfoBrady.getSportEvent().getUnitsRemaining());
            List<Map<String, Object>> statsList = mapper.readValue((String) athlete1.get("stats"), typeReference);
            assertEquals(0, statsList.size());

            assertTrue(athlete1.get("fpp").toString().equals("0.0"));
            assertEquals("NE@BAL", athlete1.get("matchup"));
            assertTrue((Integer) athlete1.get("indicator") == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);

            Map<String, Object> athlete2 = resultList.get(1);
            assertTrue((Integer) athlete2.get("id") == athleteGronk.getStatProviderId());
            assertTrue((Integer) athlete2.get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());
            assertTrue(athlete2.get("position").equals(athleteGronk.getPositions().get(0).getAbbreviation()));
            assertTrue(athlete2.get("firstName").equals(athleteGronk.getFirstName()));
            assertTrue(athlete2.get("lastName").equals(athleteGronk.getLastName()));
            assertTrue((Integer) athlete2.get("unitsRemaining") == athleteSportEventInfoGronk.getSportEvent().getUnitsRemaining());

            statsList = mapper.readValue((String) athlete2.get("stats"), typeReference);
            assertEquals(0, statsList.size());

            assertTrue(athlete2.get("fpp").toString().equals("0.0"));
            assertEquals("NE@BAL", athlete2.get("matchup"));
            assertTrue((Integer) athlete2.get("indicator") == GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
