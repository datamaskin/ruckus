package managers.views;

import service.AthleteContestRankService;
import service.AthleteExposureService;
import service.ContestLiveAthleteService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ContestDao;
import dao.IContestDao;
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
 * Created by dmaclean on 6/29/14.
 */
public class ContestLiveAthleteManagerTest extends BaseTest {

    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;
    AthleteSportEventInfo athleteSportEventInfoEdelman;
    private TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
    };
    private TypeReference<Map<String, Object>> seDescTypeRef = new TypeReference<Map<String, Object>>() {
    };
    private ObjectMapper mapper = new ObjectMapper();
    private ContestLiveAthleteService contestLiveAthleteManager;
    private AthleteExposureService athleteExposureManager;
    private AthleteContestRankService athleteContestRankManager;
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Athlete athleteEdelman;
    private Team team;
    private Team team2;
    private LineupSpot lineupSpot;
    private LineupSpot lineupSpot2;
    private LineupSpot lineupSpot3;
    private Lineup lineup;
    private Lineup lineup2;
    private Lineup lineup3;
    private Entry entry;
    private Entry entry2;
    private Entry entry3;
    private User user;
    private Contest contest;
    private Contest contest2;
    private SportEvent sportEvent;
    private ContestGrouping grouping;
    private ContestPayout contestPayout;

    private ISportsDao sportsDao;
    private IContestDao contestDao;

    @Before
    public void setUp() {
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        contestDao = new ContestDao(new ContestIdGeneratorImpl());

        contestLiveAthleteManager = new ContestLiveAthleteService();
        athleteContestRankManager = new AthleteContestRankService();
        athleteExposureManager = new AthleteExposureService();
        contestLiveAthleteManager.setAthleteContestRankManager(athleteContestRankManager);
        contestLiveAthleteManager.setAthleteExposureManager(athleteExposureManager);

        // Set up Team
        List<Team> teams = new ArrayList<>();
        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);
        teams.add(team);

        team2 = new Team(League.NFL, "Baltimore", "Ravens", "BAL", 2);
        sportsDao.saveTeam(team2);
        teams.add(team2);

        List<Position> quarterback = new ArrayList<>();
        List<Position> receiver = new ArrayList<>();

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        Ebean.save(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", team, "87");
        athleteGronk.setPositions(Arrays.asList(Position.FB_TIGHT_END));
        Ebean.save(athleteGronk);

        athleteEdelman = new Athlete(3, "Julian", "Edelman", team, "80");
        athleteEdelman.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
        Ebean.save(athleteEdelman);


        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        // Set up Contest Payout
        contestPayout = new ContestPayout(1, 1, 10000);
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        Map<String, Object> matchupData = new HashMap<>();
        matchupData.put("homeId", 1);
        matchupData.put("homeTeam", "NE");
        matchupData.put("homeScore", 10);
        matchupData.put("awayId", 2);
        matchupData.put("awayTeam", "Bal");
        matchupData.put("awayScore", 21);

        // Set up SportEvent
        try {
            sportEvent = new SportEvent(1, League.NFL, new Date(), "test", mapper.writeValueAsString(matchupData), 60, false, 2014, -1, 1);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(Arrays.asList(sportEvent), type);
        Ebean.save(sportEventGrouping);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);
        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        contest2 = new Contest(ContestType.DOUBLE_UP, "212313", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest2.setContestState(ContestState.active);
        Ebean.save(contest2);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.00"),
                sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK),
                "[{\"timestamp\":12345,\"description\":\"test\", \"fpChange\":\"+1\",\"athleteSportEventInfoId\":1}]");
        athleteSportEventInfoBrady.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("12.00"),
                sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_TIGHT_END),
                "[{\"timestamp\":12345,\"description\":\"test\", \"fpChange\":\"+1\",\"athleteSportEventInfoId\":2}]");
        athleteSportEventInfoGronk.setIndicator(GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
        Ebean.save(athleteSportEventInfoGronk);
        athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteEdelman, new BigDecimal("11.00"),
                sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_WIDE_RECEIVER),
                "[{\"timestamp\":12345,\"description\":\"test\", \"fpChange\":\"+1\",\"athleteSportEventInfoId\":3}]");
        athleteSportEventInfoEdelman.setIndicator(GlobalConstants.INDICATOR_TEAM_ON_FIELD);
        Ebean.save(athleteSportEventInfoEdelman);

        // Set up Lineup and LineupSpot
        lineupSpot = new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady);
        lineupSpot2 = new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk);
        lineupSpot3 = new LineupSpot(athleteEdelman, Position.FB_WIDE_RECEIVER, athleteSportEventInfoEdelman);
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(Arrays.asList(lineupSpot, lineupSpot2));
        Ebean.save(lineup);

        lineup2 = new Lineup("My Lineup 2", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(Arrays.asList(lineupSpot3));
        Ebean.save(lineup2);

        lineup3 = new Lineup("My Lineup 3", user, League.NFL, contest.getSportEventGrouping());
        lineup3.setLineupSpots(Arrays.asList(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)));
        Ebean.save(lineup3);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        Ebean.save(entry);

        entry2 = new Entry(user, contest2, lineup2);
        Ebean.save(entry2);

        entry3 = new Entry(user, contest, lineup3);
        Ebean.save(entry3);

        lineup.setEntries(Arrays.asList(entry));
        Ebean.save(lineup);

        lineup2.setEntries(Arrays.asList(entry2));
        Ebean.save(lineup2);

        lineup3.setEntries(Arrays.asList(entry3));
        Ebean.save(lineup3);
    }

    @After
    public void tearDown() {
        contestLiveAthleteManager = null;

        athleteTomBrady = null;
        athleteGronk = null;
        team = null;
        team2 = null;
        lineup = null;
        lineup2 = null;
        lineup3 = null;
        lineupSpot = null;
        lineupSpot2 = null;
        entry = null;
        entry2 = null;
        entry3 = null;
        user = null;
        contest = null;
        sportEvent = null;
        grouping = null;

        athleteSportEventInfoBrady = null;
        athleteSportEventInfoGronk = null;
    }

    @Test
    public void testGetAthletesForContestAsJson() {
        try {
            String json = contestLiveAthleteManager.getAthletesForContestAsJson(user, contest.getUrlId());
            List<Map<String, Object>> data = mapper.readValue(json, typeReference);

            assertTrue(data.size() == 2);

            ArrayList stats;
            ArrayList timeline;
            HashMap timelineEntry;

            for(Map<String, Object> resultEntry: data) {
                if(resultEntry.get("lastName").equals("Brady")) {
                    assertTrue(resultEntry.containsKey("exposure"));        // Fully tested elsewhere
                    assertTrue(resultEntry.containsKey("ranks"));           // Fully tested elsewhere
                    assertEquals("10.0", resultEntry.get("fpp").toString());
                    assertTrue((Integer) resultEntry.get("indicator") == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
                    stats = (ArrayList) resultEntry.get("stats");
                    timeline = (ArrayList) resultEntry.get("timeline");

                    assertEquals(11, stats.size());

                    timelineEntry = (HashMap) timeline.get(0);

                    assertTrue(timeline.size() == 1);
                    assertTrue((Integer) timelineEntry.get("timestamp") == 12345);
                    assertTrue(timelineEntry.get("description").equals("test"));
                    assertTrue(timelineEntry.get("fpChange").equals("+1"));
                    assertTrue((Integer) timelineEntry.get("athleteSportEventInfoId") == 1);
                }
                else if(resultEntry.get("lastName").equals("Gronkowski")) {
                    assertTrue(resultEntry.containsKey("exposure"));        // Fully tested elsewhere
                    assertTrue(resultEntry.containsKey("ranks"));           // Fully tested elsewhere
                    assertEquals("12.0", resultEntry.get("fpp").toString());
                    assertTrue((Integer) resultEntry.get("indicator") == GlobalConstants.INDICATOR_TEAM_OFF_FIELD);

                    stats = (ArrayList) resultEntry.get("stats");
                    timeline = (ArrayList) resultEntry.get("timeline");

                    assertEquals(11, stats.size());

                    timelineEntry = (HashMap) timeline.get(0);

                    assertTrue(timeline.size() == 1);
                    assertTrue((Integer) timelineEntry.get("timestamp") == 12345);
                    assertTrue(timelineEntry.get("description").equals("test"));
                    assertTrue(timelineEntry.get("fpChange").equals("+1"));
                    assertTrue((Integer) timelineEntry.get("athleteSportEventInfoId") == 2);
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAthleteForContestAsJson() {
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
        };

        try {
            String json = contestLiveAthleteManager.getAthleteForContestAsJson(user, contest.getUrlId(), athleteSportEventInfoBrady.getId(), false);
            Map<String, Object> data = mapper.readValue(json, typeRef);
            ArrayList stats = (ArrayList) data.get("stats");
            ArrayList timeline = (ArrayList) data.get("timeline");

            assertTrue(!data.containsKey("exposure"));
            assertTrue(!data.containsKey("ranks"));
            assertEquals(10.0, data.get("fpp"));
            assertEquals(Position.FB_QUARTERBACK.getAbbreviation(), data.get("position"));
            assertEquals("12", data.get("uniform"));
            assertEquals(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY, data.get("indicator"));
            assertEquals("https://dm63aeeijtc75.cloudfront.net/" + athleteSportEventInfoBrady.getSportEvent().getLeague().getAbbreviation().toLowerCase() +
                    "/" + athleteSportEventInfoBrady.getAthlete().getStatProviderId(), data.get("image"));
            assertEquals("Tom", data.get("firstName"));
            assertEquals("Brady", data.get("lastName"));
            assertEquals(sportEvent.getUnitsRemaining(), data.get("unitsRemaining"));

            assertEquals(false, stats.isEmpty());

            HashMap timelineEntry = (HashMap) timeline.get(0);

            assertTrue(timeline.size() == 1);
            assertTrue((Integer) timelineEntry.get("timestamp") == 12345);
            assertTrue(timelineEntry.get("description").equals("test"));
            assertTrue(timelineEntry.get("fpChange").equals("+1"));
            assertTrue((Integer) timelineEntry.get("athleteSportEventInfoId") == 1);

            Map<String, Object> matchupData = (Map<String, Object>) data.get("matchup");
            assertTrue((Integer) matchupData.get("sportEventId") == 1);
            assertTrue((Integer) matchupData.get("homeId") == 1);
            assertTrue(matchupData.get("homeTeam").equals("NE"));
            assertTrue((Integer) matchupData.get("homeScore") == 10);
            assertTrue((Integer) matchupData.get("awayId") == 2);
            assertTrue(matchupData.get("awayTeam").equals("Bal"));
            assertTrue((Integer) matchupData.get("awayScore") == 21);
            assertEquals(1, matchupData.get("athleteTeamId"));

//                    data.get("matchup").equals("NE vs BAL"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAthletesForUserAsJson() {
        try {
            String json = contestLiveAthleteManager.getAthletesForUserAsJson(user);
            List<Map<String, Object>> data = mapper.readValue(json, typeReference);

            assertTrue(data.size() == 3);

            ArrayList stats;
            ArrayList timeline;
            HashMap timelineEntry;

            for(Map<String, Object> resultEntry: data) {
                if(resultEntry.get("lastName").equals("Brady")) {
                    assertTrue(resultEntry.containsKey("exposure"));        // Fully tested elsewhere
                    assertTrue(resultEntry.containsKey("ranks"));           // Fully tested elsewhere
                    assertEquals("10.0", resultEntry.get("fpp").toString());
                    stats = (ArrayList) resultEntry.get("stats");
                    timeline = (ArrayList) resultEntry.get("timeline");

                    assertEquals(11, stats.size());

                    timelineEntry = (HashMap) timeline.get(0);

                    assertTrue(timeline.size() == 1);
                    assertTrue((Integer) timelineEntry.get("timestamp") == 12345);
                    assertTrue(timelineEntry.get("description").equals("test"));
                    assertTrue(timelineEntry.get("fpChange").equals("+1"));
                    assertEquals(athleteSportEventInfoBrady.getId(), timelineEntry.get("athleteSportEventInfoId"));
                }
                else if(resultEntry.get("lastName").equals("Gronkowski")) {

                    assertTrue(resultEntry.containsKey("exposure"));        // Fully tested elsewhere
                    assertTrue(resultEntry.containsKey("ranks"));           // Fully tested elsewhere
                    assertEquals("12.0", resultEntry.get("fpp").toString());

                    stats = (ArrayList) resultEntry.get("stats");
                    timeline = (ArrayList) resultEntry.get("timeline");

                    assertEquals(11, stats.size());

                    timelineEntry = (HashMap) timeline.get(0);

                    assertTrue(timeline.size() == 1);
                    assertTrue((Integer) timelineEntry.get("timestamp") == 12345);
                    assertTrue(timelineEntry.get("description").equals("test"));
                    assertTrue(timelineEntry.get("fpChange").equals("+1"));
                    assertEquals(athleteSportEventInfoGronk.getId(), timelineEntry.get("athleteSportEventInfoId"));
                }
                else if(resultEntry.get("lastName").equals("Edelman")) {

                    assertEquals("11.0", resultEntry.get("fpp").toString());

                    stats = (ArrayList) resultEntry.get("stats");
                    timeline = (ArrayList) resultEntry.get("timeline");

                    assertEquals(11, stats.size());

                    timelineEntry = (HashMap) timeline.get(0);

                    assertTrue(timeline.size() == 1);
                    assertTrue((Integer) timelineEntry.get("timestamp") == 12345);
                    assertTrue(timelineEntry.get("description").equals("test"));
                    assertTrue(timelineEntry.get("fpChange").equals("+1"));
                    assertEquals(athleteSportEventInfoEdelman.getId(), timelineEntry.get("athleteSportEventInfoId"));
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAthletesForUserAsJson_OpenOrLockedContests() {
        try {
            /*
             * Set contests to pre-game (open or locked).
             */
            contest.setContestState(ContestState.open);
            contestDao.saveContest(contest);

            contest2.setContestState(ContestState.locked);
            contestDao.saveContest(contest2);

            String json = contestLiveAthleteManager.getAthletesForUserAsJson(user);
            List<Map<String, Object>> data = mapper.readValue(json, typeReference);

            assertEquals(0, data.size());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
