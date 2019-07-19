package managers.views;

import service.AthleteExposureService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import dao.ISportsDao;
import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.contest.*;
import models.sports.*;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import utilities.BaseTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 6/30/14.
 */
public class AthleteExposureManagerTest extends BaseTest {

    private TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
    private TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};

    private AthleteExposureService manager;

    private ObjectMapper mapper = new ObjectMapper();

    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Athlete athleteEdelman;
    private Athlete athleteDavidOrtiz;
    private Team team;
    private Team redSox;
    private Lineup lineup;
    private Lineup lineup2;
    private Entry entry;
    private Entry entry2;
    private User user;
    private Contest contest;
    private Contest contest2;
    private Contest mlbContest;
    private SportEvent sportEvent;
    private SportEvent sportEventBaseball;
    private ContestGrouping grouping;
    private SportEventGrouping sportEventGrouping;

    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;
    AthleteSportEventInfo athleteSportEventInfoEdelman;
    AthleteSportEventInfo athleteSportEventInfoOrtiz;

    ISportsDao sportsDao;

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        sportsDao = context.getBean("sportsDao", ISportsDao.class);

        manager = new AthleteExposureService();

        // Set up Team
        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        redSox = new Team(League.MLB, "Boston", "Red Sox", "BOS", 2);
        sportsDao.saveTeam(redSox);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        sportsDao.saveAthlete(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", team, "87");
        athleteGronk.setPositions(Arrays.asList(Position.FB_TIGHT_END));
        sportsDao.saveAthlete(athleteGronk);

        athleteEdelman = new Athlete(3, "Julian", "Edelman", team, "80");
        athleteEdelman.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
        sportsDao.saveAthlete(athleteEdelman);

        athleteDavidOrtiz = new Athlete(4, "David", "Ortiz", redSox, "12");
        athleteDavidOrtiz.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
        sportsDao.saveAthlete(athleteDavidOrtiz);

        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        sportEventBaseball = new SportEvent(2, League.NFL, new Date(), "Sox", "Sox", 9, false, 2014, -1, 1);
        Ebean.save(sportEventBaseball);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.NFL_FULL.getName(), League.NFL);
        Ebean.save(grouping);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));

        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        contest2 = new Contest(ContestType.H2H, "212313", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        contest2.setContestState(ContestState.active);
        Ebean.save(contest2);

        mlbContest = new Contest(ContestType.DOUBLE_UP, "212314", League.MLB, 2, true, 100, 1, 50000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        mlbContest.setContestState(ContestState.active);
        Ebean.save(mlbContest);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.00"), "{\"passingYards\":100}", "[\"test1\"]");
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("12.00"), "{\"receivingYards\":100}", "[\"test2\"]");
        Ebean.save(athleteSportEventInfoGronk);
        athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteEdelman, new BigDecimal("11.00"), "{\"receivingYards\":90}", "[\"test3\"]");
        Ebean.save(athleteSportEventInfoEdelman);
        athleteSportEventInfoOrtiz = new AthleteSportEventInfo(sportEventBaseball, athleteDavidOrtiz, new BigDecimal("5"), "[]", "[]");
        Ebean.save(athleteSportEventInfoOrtiz);

        // Lineup spots
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        List<LineupSpot> lineupSpots2 = new ArrayList<>();
        lineupSpots2.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineup2 = new Lineup("My Lineup 2", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots2);
        Ebean.save(lineup2);

        Lineup mlbLineup = new Lineup("Baseball lineup", user, League.MLB, mlbContest.getSportEventGrouping());
        mlbLineup.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteDavidOrtiz, Position.BS_FIRST_BASE, athleteSportEventInfoOrtiz)
        ));
        Ebean.save(mlbLineup);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        Ebean.save(entry);

        entry2 = new Entry(user, contest2, lineup2);
        Ebean.save(entry2);

        Entry mlbEntry = new Entry(user, mlbContest, mlbLineup);
        Ebean.save(mlbEntry);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);
        Ebean.save(lineup2);

        mlbLineup.setEntries(Arrays.asList(mlbEntry));
        Ebean.save(mlbLineup);
    }

    @After
    public void tearDown() {
        manager = null;

        athleteTomBrady = null;
        athleteGronk = null;
        team = null;
        lineup = null;
        entry = null;
        user = null;
        contest = null;
        sportEvent = null;
        grouping = null;

        athleteSportEventInfoBrady = null;
        athleteSportEventInfoGronk = null;
    }

    @Test
    public void testGetAthleteExposure_AllAthletes() {
        try {
            String result = manager.getAthleteExposure(user);
            List<Map<String, Integer>> resultMap = mapper.readValue(result, listTypeReference);

            assertEquals(3, resultMap.size());
            for(Map<String, Integer> entry: resultMap) {
                if(entry.get("athleteId") == athleteTomBrady.getId()) {
                    assertEquals(200, entry.get("exposure").intValue());
                }
                else if(entry.get("athleteId") == athleteGronk.getId()) {
                    assertEquals(100, entry.get("exposure").intValue());
                }
                else if(entry.get("athleteId") == athleteDavidOrtiz.getId()) {
                    assertEquals(100, entry.get("exposure").intValue());
                }
            }

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAthleteExposure() {
        Contest contest3 = new Contest(ContestType.DOUBLE_UP, "123", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        contest3.setContestState(ContestState.complete);
        Ebean.save(contest3);

        Lineup lineup3 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup3.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)
        ));
        DaoFactory.getContestDao().saveLineup(lineup3);

        Entry entry3 = new Entry(user, contest3, lineup3);
        DaoFactory.getContestDao().saveEntry(entry3);

        String result = null;
        try {
            result = manager.getAthleteExposure(user, String.valueOf(athleteSportEventInfoBrady.getId()));
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            Map<String, Object> data = mapper.readValue(result, typeReference);

            assertEquals(2, data.get("contestsEntered"));
            assertEquals(2, data.get("totalContests"));
            assertEquals(200, data.get("totalEntryFees"));
            assertEquals(200, data.get("totalExposure"));

            List<Map<String, Object>> contestTypes = (List<Map<String, Object>>) data.get("contestTypes");
            Map<String, Object> doubleUpData = contestTypes.get(0);
            assertEquals(ContestType.DOUBLE_UP.getName(), doubleUpData.get("type"));
            assertEquals(ContestType.DOUBLE_UP.getAbbr(), doubleUpData.get("abbr"));
            assertEquals(100, doubleUpData.get("entryFees"));
            assertEquals(1, doubleUpData.get("numEntered"));

            Map<String, Object> h2hData = contestTypes.get(1);
            assertEquals(ContestType.H2H.getName(), h2hData.get("type"));
            assertEquals(ContestType.H2H.getAbbr(), h2hData.get("abbr"));
            assertEquals(100, h2hData.get("entryFees"));
            assertEquals(1, h2hData.get("numEntered"));

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAthleteExposure_OneContestCancelled() {
        /*
         * Cancel contest 2.  We shouldn't see any H2Hs show up now.
         */
        contest2.setContestState(ContestState.cancelled);
        DaoFactory.getContestDao().saveContest(contest2);


        Contest contest3 = new Contest(ContestType.DOUBLE_UP, "123", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        contest3.setContestState(ContestState.complete);
        Ebean.save(contest3);

        Lineup lineup3 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup3.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)
        ));
        DaoFactory.getContestDao().saveLineup(lineup3);

        Entry entry3 = new Entry(user, contest3, lineup3);
        DaoFactory.getContestDao().saveEntry(entry3);

        String result = null;
        try {
            result = manager.getAthleteExposure(user, String.valueOf(athleteSportEventInfoBrady.getId()));
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            Map<String, Object> data = mapper.readValue(result, typeReference);

            assertTrue((Integer) data.get("contestsEntered") == 1);
            assertTrue((Integer) data.get("totalContests") == 1);
            assertTrue((Integer) data.get("totalEntryFees") == 100);
            assertTrue((Integer) data.get("totalExposure") == 100);

            List<Map<String, Object>> contestTypes = (List<Map<String, Object>>) data.get("contestTypes");
            assertEquals(1, contestTypes.size());
            Map<String, Object> doubleUpData = contestTypes.get(0);
            assertTrue(doubleUpData.get("type").equals(ContestType.DOUBLE_UP.getName()));
            assertTrue(doubleUpData.get("abbr").equals(ContestType.DOUBLE_UP.getAbbr()));
            assertTrue((Integer) doubleUpData.get("entryFees") == 100);
            assertTrue((Integer) doubleUpData.get("numEntered") == 1);

//            Map<String, Object> h2hData = contestTypes.get(1);
//            assertTrue(h2hData.get("type").equals(ContestType.H2H.getName()));
//            assertTrue(h2hData.get("abbr").equals(ContestType.H2H.getAbbr()));
//            assertTrue((Integer) h2hData.get("entryFees") == 100);
//            assertTrue((Integer) h2hData.get("numEntered") == 1);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAthleteExposure_NullId() {
        String result = null;
        try {
            result = manager.getAthleteExposure(user, null);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            Map<String, Object> data = mapper.readValue(result, typeReference);

            assertTrue(data.get("error").equals("Unable to parse id for AthleteSportEventInfo."));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
