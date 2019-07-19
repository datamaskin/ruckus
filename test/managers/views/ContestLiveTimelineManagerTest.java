package managers.views;

import service.ContestLiveTimelineService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 7/3/14.
 * Modified by gislas on 8/11/14.
 */
public class ContestLiveTimelineManagerTest extends BaseTest {

    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;
    ISportsDao sportsDao;
    private ContestLiveTimelineService timelineManager;
    private ObjectMapper mapper = new ObjectMapper();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalConstants.TIMELINE_TIMESTAMP_FORMAT);
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Team team;
    private LineupSpot lineupSpot;
    private LineupSpot lineupSpot2;
    private Lineup lineup;
    private Entry entry;
    private Position position;
    private Position position2;
    private User user;
    private Contest contest;
    private ContestType contestType;
    private SportEvent sportEvent;
    private ContestGrouping grouping;
    private ContestState contestState;
    private ContestPayout contestPayout;

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        sportsDao = context.getBean("sportsDao", ISportsDao.class);

        timelineManager = new ContestLiveTimelineService();

        // Set up Team
        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        Ebean.save(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", team, "87");
        Ebean.save(athleteGronk);

        // Set up Position
        position = new Position(100, Position.FB_QUARTERBACK.getName(), Position.FB_QUARTERBACK.getAbbreviation(), Sport.FOOTBALL); //The possible actual error line from the first parameter.
        Ebean.save(position);
        position2 = new Position(200, Position.FB_TIGHT_END.getName(), Position.FB_TIGHT_END.getAbbreviation(), Sport.FOOTBALL); //Another error line for the same reasons try with FB_TIGHT_END
        Ebean.save(position2);

        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        // Set up ContestType
        contestType = new ContestType(100, ContestType.DOUBLE_UP.getName(), ContestType.DOUBLE_UP.getAbbr()); //Another error line, changed to 100. Used to be 1, may be ambiguous.
        Ebean.save(contestType);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up Contest State
//        contestState = new ContestStateActive();
//        Ebean.save(contestState); //error line for PRIMARY as int 4. Being set in the constructor without parameters. Removed as a result.

        // Set up Contest Payout
        contestPayout = new ContestPayout(1, 1, 10000);
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        // Contest start time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        contest = new Contest(contestType, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest.setStartTime(cal.getTime());
        contest.setContestState(contestState);
        Ebean.save(contest);

        contest.setCurrentEntries(1);
        Ebean.save(contest);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("0.00"), "[]", "[]");
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("0.00"), "[]", "[]");
        Ebean.save(athleteSportEventInfoBrady);
        Ebean.save(athleteSportEventInfoGronk);

        // Set up Lineup and LineupSpot
        lineupSpot = new LineupSpot(athleteTomBrady, position, athleteSportEventInfoBrady);
        lineupSpot2 = new LineupSpot(athleteGronk, position2, athleteSportEventInfoGronk);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(lineupSpot);
        lineupSpots.add(lineupSpot2);
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);
    }

    @After
    public void tearDown() {
        timelineManager = null;

        athleteTomBrady = null;
        athleteGronk = null;
        team = null;
        lineup = null;
        lineupSpot = null;
        lineupSpot2 = null;
        entry = null;
        position = null;
        user = null;
        contest = null;
        contestType = null;
        sportEvent = null;
        grouping = null;
        contestState = null;

        athleteSportEventInfoBrady = null;
        athleteSportEventInfoGronk = null;
    }

    @Test
    public void testGetLineupTimeline() {
        TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
        };

        ArrayList<Map<String, Object>> timelineBrady = new ArrayList<>();
        ArrayList<Map<String, Object>> timelineGronk = new ArrayList<>();

        Map<String, Object> data1 = new HashMap<>();
        Map<String, Object> data2 = new HashMap<>();
        Map<String, Object> data3 = new HashMap<>();
        Map<String, Object> data4 = new HashMap<>();

        try {
            // 3
            data1.put("timestamp", simpleDateFormat.parse("07/03/2014 08:15 PM EDT"));
            data1.put("description", "Brady passes 15 yards");
            data1.put("fpChange", "+0.75");
            data1.put("athleteSportEventInfoId", athleteSportEventInfoBrady.getId());

            // 2
            data2.put("timestamp", simpleDateFormat.parse("07/03/2014 08:20 PM EDT"));
            data2.put("description", "Brady passes for touchdown");
            data2.put("fpChange", "+4");
            data2.put("athleteSportEventInfoId", athleteSportEventInfoBrady.getId());

            // 4
            data3.put("timestamp", simpleDateFormat.parse("07/03/2014 08:00 PM EDT"));
            data3.put("description", "Gronk catches pass for 15 yards");
            data3.put("fpChange", "+1.5");
            data3.put("athleteSportEventInfoId", athleteSportEventInfoGronk.getId());

            // 1
            data4.put("timestamp", simpleDateFormat.parse("07/03/2014 08:30 PM EDT"));
            data4.put("description", "Gronk receives touchdown");
            data4.put("fpChange", "+6");
            data4.put("athleteSportEventInfoId", athleteSportEventInfoGronk.getId());
        } catch (ParseException e) {
            fail(e.getMessage());
        }

        timelineBrady.add(data2);
        timelineBrady.add(data1);
        timelineGronk.add(data4);
        timelineGronk.add(data3);

        try {
            athleteSportEventInfoBrady.setTimeline(mapper.writeValueAsString(timelineBrady));
            athleteSportEventInfoGronk.setTimeline(mapper.writeValueAsString(timelineGronk));
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }

        Ebean.save(athleteSportEventInfoBrady);
        Ebean.save(athleteSportEventInfoGronk);

        try {
            String result = timelineManager.getLineupTimeline(lineup.getId());
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);

            assertTrue(data.size() == 4);

            // Item 1
            Map<String, Object> item1 = data.get(0);
            assertTrue(simpleDateFormat.format(new Date((Long) item1.get("timestamp"))).equals("07/03/2014 08:30 PM EDT"));
            assertTrue(item1.get("description").equals("Gronk receives touchdown"));
            assertTrue(item1.get("fpChange").equals("+6"));
            assertTrue((Integer) item1.get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());

            // Item 2
            Map<String, Object> item2 = data.get(1);
            assertTrue(simpleDateFormat.format(new Date((Long) item2.get("timestamp"))).equals("07/03/2014 08:20 PM EDT"));
            assertTrue(item2.get("description").equals("Brady passes for touchdown"));
            assertTrue(item2.get("fpChange").equals("+4"));
            assertTrue((Integer) item2.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());

            // Item 3
            Map<String, Object> item3 = data.get(2);
            assertTrue(simpleDateFormat.format(new Date((Long) item3.get("timestamp"))).equals("07/03/2014 08:15 PM EDT"));
            assertTrue(item3.get("description").equals("Brady passes 15 yards"));
            assertTrue(item3.get("fpChange").equals("+0.75"));
            assertTrue((Integer) item3.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());

            // Item 4
            Map<String, Object> item4 = data.get(3);
            assertTrue(simpleDateFormat.format(new Date((Long) item4.get("timestamp"))).equals("07/03/2014 08:00 PM EDT"));
            assertTrue(item4.get("description").equals("Gronk catches pass for 15 yards"));
            assertTrue(item4.get("fpChange").equals("+1.5"));
            assertTrue((Integer) item4.get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLineupTimeline_MultipleDays() {
        TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
        };

        ArrayList<Map<String, Object>> timelineBrady = new ArrayList<>();
        ArrayList<Map<String, Object>> timelineGronk = new ArrayList<>();

        Map<String, Object> data1 = new HashMap<>();
        Map<String, Object> data2 = new HashMap<>();
        Map<String, Object> data3 = new HashMap<>();
        Map<String, Object> data4 = new HashMap<>();

        try {
            // 4
            data1.put("timestamp", simpleDateFormat.parse("07/03/2014 08:15 PM EDT"));
            data1.put("description", "Brady passes 15 yards");
            data1.put("fpChange", "+0.75");
            data1.put("athleteSportEventInfoId", athleteSportEventInfoBrady.getId());

            // 3
            data2.put("timestamp", simpleDateFormat.parse("07/03/2014 08:20 PM EDT"));
            data2.put("description", "Brady passes for touchdown");
            data2.put("fpChange", "+4");
            data2.put("athleteSportEventInfoId", athleteSportEventInfoBrady.getId());

            // 1
            data3.put("timestamp", simpleDateFormat.parse("07/04/2014 01:10 AM EDT"));
            data3.put("description", "Gronk catches pass for 15 yards");
            data3.put("fpChange", "+1.5");
            data3.put("athleteSportEventInfoId", athleteSportEventInfoGronk.getId());

            // 2
            data4.put("timestamp", simpleDateFormat.parse("07/03/2014 08:30 PM EDT"));
            data4.put("description", "Gronk receives touchdown");
            data4.put("fpChange", "+6");
            data4.put("athleteSportEventInfoId", athleteSportEventInfoGronk.getId());
        } catch (ParseException e) {
            fail(e.getMessage());
        }

        timelineBrady.add(data2);
        timelineBrady.add(data1);
        timelineGronk.add(data4);
        timelineGronk.add(data3);

        try {
            athleteSportEventInfoBrady.setTimeline(mapper.writeValueAsString(timelineBrady));
            athleteSportEventInfoGronk.setTimeline(mapper.writeValueAsString(timelineGronk));
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }

        Ebean.save(athleteSportEventInfoBrady);
        Ebean.save(athleteSportEventInfoGronk);

        try {
            String result = timelineManager.getLineupTimeline(lineup.getId());
            List<Map<String, Object>> data = mapper.readValue(result, typeReference);

            assertTrue(data.size() == 4);

            // Item 1
            Map<String, Object> item4 = data.get(0);
            assertTrue(simpleDateFormat.format(new Date((Long) item4.get("timestamp"))).equals("07/04/2014 01:10 AM EDT"));
            assertTrue(item4.get("description").equals("Gronk catches pass for 15 yards"));
            assertTrue(item4.get("fpChange").equals("+1.5"));
            assertTrue((Integer) item4.get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());

            // Item 2
            Map<String, Object> item1 = data.get(1);
            assertTrue(simpleDateFormat.format(new Date((Long) item1.get("timestamp"))).equals("07/03/2014 08:30 PM EDT"));
            assertTrue(item1.get("description").equals("Gronk receives touchdown"));
            assertTrue(item1.get("fpChange").equals("+6"));
            assertTrue((Integer) item1.get("athleteSportEventInfoId") == athleteSportEventInfoGronk.getId());

            // Item 3
            Map<String, Object> item2 = data.get(2);
            assertTrue(simpleDateFormat.format(new Date((Long) item2.get("timestamp"))).equals("07/03/2014 08:20 PM EDT"));
            assertTrue(item2.get("description").equals("Brady passes for touchdown"));
            assertTrue(item2.get("fpChange").equals("+4"));
            assertTrue((Integer) item2.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());

            // Item 4
            Map<String, Object> item3 = data.get(3);
            assertTrue(simpleDateFormat.format(new Date((Long) item3.get("timestamp"))).equals("07/03/2014 08:15 PM EDT"));
            assertTrue(item3.get("description").equals("Brady passes 15 yards"));
            assertTrue(item3.get("fpChange").equals("+0.75"));
            assertTrue((Integer) item3.get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
