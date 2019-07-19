package managers.views;

import service.AthletePercentOwnedService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ISportsDao;
import distributed.DistributedServices;
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
 * Created by dmaclean on 7/1/14.
 */
public class AthletePercentOwnedManagerTest extends BaseTest {
    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;
    AthleteSportEventInfo athleteSportEventInfoEdelman;
    AthleteSportEventInfo athleteSportEventInfoDobson;
    AthleteSportEventInfo athleteSportEventInfoThompkins;
    AthleteSportEventInfo athleteSportEventInfoAmendola;
    ISportsDao sportsDao;
    private AthletePercentOwnedService manager;
    private TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
    };
    private ObjectMapper mapper = new ObjectMapper();
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Athlete athleteEdelman;
    private Athlete athleteDobson;
    private Athlete athleteThompkins;
    private Athlete athleteAmendola;
    private Team team;
    private LineupSpot lineupSpot;
    private LineupSpot lineupSpot2;
    private LineupSpot lineupSpot3;
    private Lineup lineup;
    private Lineup lineup2;
    private Lineup lineup3;
    private Lineup lineup4;
    private Entry entry;
    private Entry entry2;
    private Entry entry3;
    private Entry entry4;
    private User user;
    private User user2;
    private User user3;
    private User user4;
    private Contest contest;
    private Contest contest2;
    private SportEvent sportEvent;
    private ContestGrouping grouping;
    private ContestPayout contestPayout;

    @Before
    public void setUp() {
        startHazelcast();

        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        sportsDao = context.getBean("sportsDao", ISportsDao.class);

        Map<String, String> athletePercentOwnedMap = DistributedServices.getInstance().getMap(GlobalConstants.ATHLETE_PERCENT_OWNED_MAP);
        athletePercentOwnedMap.clear();

        manager = new AthletePercentOwnedService();

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

        athleteDobson = new Athlete(4, "Aaron", "Dobson", team, "81");
        athleteDobson.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
        Ebean.save(athleteDobson);

        athleteThompkins = new Athlete(5, "Kembrell", "Thompkins", team, "82");
        athleteThompkins.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
        Ebean.save(athleteThompkins);

        athleteAmendola = new Athlete(6, "Danny", "Amendola", team, "83");
        athleteAmendola.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
        Ebean.save(athleteAmendola);

        // Set up AppUser
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

        user3 = new User();
        user3.setEmail("dmaclean82@gmail.com");
        user3.setFirstName("Dan");
        user3.setLastName("MacLean");
        user3.setPassword("test");
        user3.setUserName("dmaclean");
        Ebean.save(user3);

        user3 = new User();
        user3.setEmail("mick@ruckusgaming.com");
        user3.setFirstName("Mick");
        user3.setLastName("Giles");
        user3.setPassword("test");
        user3.setUserName("SeligKcim");
        Ebean.save(user3);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.NFL_FULL.getName(), League.NFL);
        Ebean.save(grouping);

        // Set up payouts
        contestPayout = new ContestPayout(1, 1, 100);
        ArrayList<ContestPayout> contestPayouts = new ArrayList<>();
        contestPayouts.add(contestPayout);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));

        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest.setReconciledTime(null);
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        contest2 = new Contest(ContestType.H2H, "212313", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest2.setReconciledTime(null);
        contest2.setContestState(ContestState.active);
        Ebean.save(contest2);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.00"), "{\"passingYards\":100}", "[\"test1\"]");
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("12.00"), "{\"receivingYards\":100}", "[\"test2\"]");
        Ebean.save(athleteSportEventInfoGronk);
        athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteEdelman, new BigDecimal("11.00"), "{\"receivingYards\":90}", "[\"test3\"]");
        Ebean.save(athleteSportEventInfoEdelman);
        athleteSportEventInfoDobson = new AthleteSportEventInfo(sportEvent, athleteDobson, new BigDecimal("10.00"), "{\"receivingYards\":90}", "[\"test3\"]");
        Ebean.save(athleteSportEventInfoDobson);
        athleteSportEventInfoThompkins = new AthleteSportEventInfo(sportEvent, athleteThompkins, new BigDecimal("9.00"), "{\"receivingYards\":90}", "[\"test3\"]");
        Ebean.save(athleteSportEventInfoThompkins);
        athleteSportEventInfoAmendola = new AthleteSportEventInfo(sportEvent, athleteAmendola, new BigDecimal("8.00"), "{\"receivingYards\":90}", "[\"test3\"]");
        Ebean.save(athleteSportEventInfoAmendola);

        // Set up Lineup and LineupSpot
//        lineupSpot = new LineupSpot(athleteTomBrady, position, athleteSportEventInfoBrady);
//        lineupSpot2 = new LineupSpot(athleteGronk, position2, athleteSportEventInfoGronk);
//        lineupSpot3 = new LineupSpot(athleteEdelman, position2, athleteSportEventInfoEdelman);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        lineupSpots.add(new LineupSpot(athleteEdelman, Position.FB_WIDE_RECEIVER, athleteSportEventInfoEdelman));
        lineupSpots.add(new LineupSpot(athleteDobson, Position.FB_WIDE_RECEIVER, athleteSportEventInfoDobson));
        lineupSpots.add(new LineupSpot(athleteAmendola, Position.FB_WIDE_RECEIVER, athleteSportEventInfoAmendola));
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        List<LineupSpot> lineupSpots2 = new ArrayList<>();
        lineupSpots2.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots2.add(new LineupSpot(athleteAmendola, Position.FB_WIDE_RECEIVER, athleteSportEventInfoAmendola));
        lineup2 = new Lineup("My Lineup 2", user2, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots2);
        Ebean.save(lineup2);

        List<LineupSpot> lineupSpots3 = new ArrayList<>();
        lineupSpots3.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots3.add(new LineupSpot(athleteAmendola, Position.FB_WIDE_RECEIVER, athleteSportEventInfoAmendola));
        lineupSpots3.add(new LineupSpot(athleteDobson, Position.FB_WIDE_RECEIVER, athleteSportEventInfoDobson));
        lineup3 = new Lineup("My Lineup 3", user3, League.NFL, contest.getSportEventGrouping());
        lineup3.setLineupSpots(lineupSpots3);
        Ebean.save(lineup3);

        List<LineupSpot> lineupSpots4 = new ArrayList<>();
        lineupSpots4.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots4.add(new LineupSpot(athleteAmendola, Position.FB_WIDE_RECEIVER, athleteSportEventInfoAmendola));
        lineupSpots4.add(new LineupSpot(athleteThompkins, Position.FB_WIDE_RECEIVER, athleteSportEventInfoThompkins));
        lineup4 = new Lineup("My Lineup 4", user4, League.NFL, contest.getSportEventGrouping());
        lineup4.setLineupSpots(lineupSpots4);
        Ebean.save(lineup4);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(100);
        Ebean.save(entry);

        entry2 = new Entry(user2, contest, lineup2);
        entry2.setPoints(110);
        Ebean.save(entry2);

        entry3 = new Entry(user3, contest, lineup3);
        entry3.setPoints(90);
        Ebean.save(entry3);

        entry4 = new Entry(user4, contest, lineup4);
        entry4.setPoints(80);
        Ebean.save(entry4);

        lineup.setEntries(Arrays.asList(entry));
        Ebean.save(lineup);

        lineup2.setEntries(Arrays.asList(entry2));
        Ebean.save(lineup2);

        lineup3.setEntries(Arrays.asList(entry3));
        Ebean.save(lineup3);

        lineup4.setEntries(Arrays.asList(entry4));
        Ebean.save(lineup4);
    }

    @After
    public void tearDown() {
        manager = null;

        athleteTomBrady = null;
        athleteGronk = null;
        athleteAmendola = null;
        athleteDobson = null;
        athleteEdelman = null;
        athleteThompkins = null;
        team = null;
        lineup = null;
        lineupSpot = null;
        lineupSpot2 = null;
        entry = null;
        user = null;
        contest = null;
        sportEvent = null;
        grouping = null;

        athleteSportEventInfoBrady = null;
        athleteSportEventInfoGronk = null;
        athleteSportEventInfoAmendola = null;
        athleteSportEventInfoEdelman = null;
        athleteSportEventInfoThompkins = null;
        athleteSportEventInfoDobson = null;
    }

    @Test
    public void testGetPercentOwned() {
        try {
            String result = manager.getPercentOwned(contest.getUrlId(), athleteSportEventInfoEdelman.getId(), entry.getId());
            Map<String, Object> data = mapper.readValue(result, typeReference);

            assertTrue(data.size() == 4);
            String position = (String) data.get("position");
            assertTrue(position.equals(athleteSportEventInfoEdelman.getAthlete().getPositions().get(0).getAbbreviation()));
            List<Map<String, Object>> all = (List<Map<String, Object>>) data.get("all");
            assertTrue(all.size() == 4);
            assertTrue((Double) all.get(0).get("fpp") == 11.0
                    && all.get(0).get("firstName").equals("Julian")
                    && all.get(0).get("lastName").equals("Edelman")
                    && (Double) all.get(0).get("percentOwned") == 0.25
                    && (Integer) all.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoEdelman.getId());
            assertTrue((Double) all.get(1).get("fpp") == 10.0
                    && all.get(1).get("firstName").equals("Aaron")
                    && all.get(1).get("lastName").equals("Dobson")
                    && (Double) all.get(1).get("percentOwned") == 0.50
                    && (Integer) all.get(1).get("athleteSportEventInfoId") == athleteSportEventInfoDobson.getId());
            assertTrue((Double) all.get(2).get("fpp") == 9.0
                    && all.get(2).get("firstName").equals("Kembrell")
                    && all.get(2).get("lastName").equals("Thompkins")
                    && (Double) all.get(2).get("percentOwned") == 0.25
                    && (Integer) all.get(2).get("athleteSportEventInfoId") == athleteSportEventInfoThompkins.getId());
            assertTrue((Double) all.get(3).get("fpp") == 8.0
                    && all.get(3).get("firstName").equals("Danny")
                    && all.get(3).get("lastName").equals("Amendola")
                    && (Double) all.get(3).get("percentOwned") == 1.0
                    && (Integer) all.get(3).get("athleteSportEventInfoId") == athleteSportEventInfoAmendola.getId());

            List<Map<String, Object>> tenPercent = (List<Map<String, Object>>) data.get("tenPercent");
            assertTrue((Double) tenPercent.get(0).get("fpp") == 11.0
                    && tenPercent.get(0).get("firstName").equals("Julian")
                    && tenPercent.get(0).get("lastName").equals("Edelman")
                    && (Double) tenPercent.get(0).get("percentOwned") == 0.25
                    && (Integer) tenPercent.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoEdelman.getId());
            assertTrue((Double) tenPercent.get(1).get("fpp") == 10.0
                    && tenPercent.get(1).get("firstName").equals("Aaron")
                    && tenPercent.get(1).get("lastName").equals("Dobson")
                    && (Double) tenPercent.get(1).get("percentOwned") == 0.50
                    && (Integer) tenPercent.get(1).get("athleteSportEventInfoId") == athleteSportEventInfoDobson.getId());
            assertTrue((Double) tenPercent.get(2).get("fpp") == 8.0
                    && tenPercent.get(2).get("firstName").equals("Danny")
                    && tenPercent.get(2).get("lastName").equals("Amendola")
                    && (Double) tenPercent.get(2).get("percentOwned") == 1.0
                    && (Integer) tenPercent.get(2).get("athleteSportEventInfoId") == athleteSportEventInfoAmendola.getId());

            List<Map<String, Object>> above = (List<Map<String, Object>>) data.get("above");
            assertTrue(above.size() == 1);
            assertTrue((Double) above.get(0).get("fpp") == 8.0
                    && above.get(0).get("firstName").equals("Danny")
                    && above.get(0).get("lastName").equals("Amendola")
                    && (Double) above.get(0).get("percentOwned") == 1.0
                    && (Integer) above.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoAmendola.getId());

//            assertTrue(data != null &&
//                    ((Integer)data.get("athleteSportEventInfoId")) == athleteSportEventInfoBrady.id &&
//                    (Double)data.get("percentOwned") == 1.0);
//
//            result = manager.getPercentOwned(contest.getUrlId(), String.valueOf(athleteSportEventInfoGronk.id));
//            data = mapper.readValue(result, typeReference);
//            assertTrue(data != null &&
//                    ((Integer)data.get("athleteSportEventInfoId")) == athleteSportEventInfoGronk.id &&
//                    (Double)data.get("percentOwned") == 0.5);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetPercentOwned_FirstPlace() {
        try {
            String result = manager.getPercentOwned(contest.getUrlId(), athleteSportEventInfoBrady.getId(), entry2.getId());
            Map<String, Object> data = mapper.readValue(result, typeReference);

            assertTrue(data.size() == 4);
            String position = (String) data.get("position");
            assertTrue(position.equals(athleteSportEventInfoBrady.getAthlete().getPositions().get(0).getAbbreviation()));
            List<Map<String, Object>> all = (List<Map<String, Object>>) data.get("all");
            assertTrue(all.size() == 1);
            assertTrue((Double) all.get(0).get("fpp") == 10.0
                    && all.get(0).get("firstName").equals("Tom")
                    && all.get(0).get("lastName").equals("Brady")
                    && (Double) all.get(0).get("percentOwned") == 1.0
                    && (Integer) all.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());

            List<Map<String, Object>> tenPercent = (List<Map<String, Object>>) data.get("tenPercent");
            assertTrue((Double) tenPercent.get(0).get("fpp") == 10.0
                    && tenPercent.get(0).get("firstName").equals("Tom")
                    && tenPercent.get(0).get("lastName").equals("Brady")
                    && (Double) tenPercent.get(0).get("percentOwned") == 1.0
                    && (Integer) tenPercent.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());

            List<Map<String, Object>> above = (List<Map<String, Object>>) data.get("above");
            assertEquals(0, above.size());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetPercentOwned_LastPlace() {
        try {
            String result = manager.getPercentOwned(contest.getUrlId(), athleteSportEventInfoBrady.getId(), entry4.getId());
            Map<String, Object> data = mapper.readValue(result, typeReference);

            assertTrue(data.size() == 4);
            String position = (String) data.get("position");
            assertTrue(position.equals(athleteSportEventInfoBrady.getAthlete().getPositions().get(0).getAbbreviation()));
            List<Map<String, Object>> all = (List<Map<String, Object>>) data.get("all");
            assertTrue(all.size() == 1);
            assertTrue((Double) all.get(0).get("fpp") == 10.0
                    && all.get(0).get("firstName").equals("Tom")
                    && all.get(0).get("lastName").equals("Brady")
                    && (Double) all.get(0).get("percentOwned") == 1.0
                    && (Integer) all.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());

            List<Map<String, Object>> tenPercent = (List<Map<String, Object>>) data.get("tenPercent");
            assertTrue((Double) tenPercent.get(0).get("fpp") == 10.0
                    && tenPercent.get(0).get("firstName").equals("Tom")
                    && tenPercent.get(0).get("lastName").equals("Brady")
                    && (Double) tenPercent.get(0).get("percentOwned") == 1.0
                    && (Integer) tenPercent.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());

            List<Map<String, Object>> above = (List<Map<String, Object>>) data.get("above");
            assertEquals(1, above.size());
            assertTrue((Double) tenPercent.get(0).get("fpp") == 10.0
                    && tenPercent.get(0).get("firstName").equals("Tom")
                    && tenPercent.get(0).get("lastName").equals("Brady")
                    && (Double) tenPercent.get(0).get("percentOwned") == 1.0
                    && (Integer) tenPercent.get(0).get("athleteSportEventInfoId") == athleteSportEventInfoBrady.getId());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetPercentOwned_NoId() {
        TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
        };

        try {
            String result = manager.getPercentOwned(null, null, null);
            Map<String, Object> data = mapper.readValue(result, mapTypeReference);

            assertTrue(data != null && data.get("error").equals("Unable to parse id for AthleteSportEventInfo or Contest."));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
