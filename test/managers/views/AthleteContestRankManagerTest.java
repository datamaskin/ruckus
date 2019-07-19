package managers.views;

import service.AthleteContestRankService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 6/30/14.
 */
public class AthleteContestRankManagerTest extends BaseTest {
    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;
    AthleteSportEventInfo athleteSportEventInfoEdelman;
    ISportsDao sportsDao;
    private TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};
    private TypeReference<Map<String, Object>> errorTypeReference = new TypeReference<Map<String, Object>>() {};
    private AthleteContestRankService manager;
    private ObjectMapper mapper = new ObjectMapper();
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Athlete athleteEdelman;
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
    private SportEvent sportEvent;
    private ContestGrouping grouping;

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        sportsDao = context.getBean("sportsDao", ISportsDao.class);

        manager = new AthleteContestRankService();

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

        user2 = new User();
        user2.setEmail("dan@ruckusgaming.com");
        user2.setFirstName("Dan");
        user2.setLastName("MacLean");
        user2.setPassword("test");
        user2.setUserName("dmaclean");
        Ebean.save(user2);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.NFL_FULL.getName(), League.NFL);
        Ebean.save(grouping);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));

        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        contest2 = new Contest(ContestType.H2H, "212313", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        contest2.setContestState(ContestState.active);
        Ebean.save(contest2);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.00"), "{\"passingYards\":100}", "[\"test1\"]");
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("12.00"), "{\"receivingYards\":100}", "[\"test2\"]");
        Ebean.save(athleteSportEventInfoGronk);
        athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteEdelman, new BigDecimal("11.00"), "{\"receivingYards\":90}", "[\"test3\"]");
        Ebean.save(athleteSportEventInfoEdelman);

        // Set up Lineup and LineupSpot
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        List<LineupSpot> lineupSpots2 = new ArrayList<>();
        lineupSpots2.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineup2 = new Lineup("My Lineup 2", user2, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots2);
        Ebean.save(lineup2);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);

        entry2 = new Entry(user, contest2, lineup);
        entry.setPoints(105);
        Ebean.save(entry2);

        entry3 = new Entry(user2, contest2, lineup2);
        entry.setPoints(110);
        Ebean.save(entry3);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);
        Ebean.save(lineup2);
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
        entry3 = null;
        user = null;
        contest = null;
        sportEvent = null;
        grouping = null;

        athleteSportEventInfoBrady = null;
        athleteSportEventInfoGronk = null;
    }

    @Test
    public void getAthleteContestRanks() {
        String result = null;
        try {
            result = manager.getAthleteContestRanks(user, athleteSportEventInfoBrady.getId());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);
            assertTrue(data.size() == 2);

            Map<String, Object> dataMap1 = data.get(0);
            assertTrue((Integer) dataMap1.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(dataMap1.get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) dataMap1.get("entryFee") == contest.getEntryFee());
            Map<String, Object> contestTypeMap = (Map<String, Object>) dataMap1.get("contestType");
            assertTrue(contestTypeMap.get("abbr").equals(ContestType.DOUBLE_UP.getAbbr()));
            assertTrue(contestTypeMap.get("name").equals(ContestType.DOUBLE_UP.getName()));
            assertTrue((Integer) dataMap1.get("currentEntries") == contest.getCurrentEntries());
            assertTrue((Integer) dataMap1.get("capacity") == contest.getCapacity());
            assertEquals(dataMap1.get("rank"), 1);
            assertEquals(contest.getUrlId(), dataMap1.get("contestId"));

            Map<String, Object> dataMap2 = data.get(1);
            assertTrue((Integer) dataMap2.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
            assertTrue(dataMap2.get("league").equals(League.NFL.getAbbreviation()));
            assertTrue((Integer) dataMap2.get("entryFee") == contest2.getEntryFee());
            contestTypeMap = (Map<String, Object>) dataMap2.get("contestType");
            assertTrue(contestTypeMap.get("abbr").equals(ContestType.H2H.getAbbr()));
            assertTrue(contestTypeMap.get("name").equals(ContestType.H2H.getName()));
            assertTrue((Integer) dataMap2.get("currentEntries") == contest2.getCurrentEntries());
            assertTrue((Integer) dataMap2.get("capacity") == contest2.getCapacity());
            assertEquals(dataMap2.get("rank"), 1);
            assertTrue(dataMap2.get("opp").equals(user2.getUserName()));
            assertEquals(contest2.getUrlId(), dataMap2.get("contestId"));

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getAthleteContestRanks_NoId() {
        String result = null;
        try {
            result = manager.getAthleteContestRanks(user, null);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            Map<String, Object> data = mapper.readValue(result, errorTypeReference);

            assertTrue(data.get("error").equals("Unable to parse id for AthleteSportEventInfo."));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
