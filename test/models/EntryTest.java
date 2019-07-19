package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;
import utils.TimeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 6/19/14.
 */
public class EntryTest extends BaseTest {
    AthleteSportEventInfo athleteSportEventInfoBrady;
    AthleteSportEventInfo athleteSportEventInfoGronk;
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Team team;
    private LineupSpot lineupSpot;
    private LineupSpot lineupSpot2;
    private Lineup lineup;
    private Entry entry;
    private User user;
    private Contest contest;
    private SportEvent sportEvent;
    private ContestGrouping grouping;
    private SportEventGrouping sportEventGrouping;
    private ContestPayout contestPayout;

    private ISportsDao sportsDao;
    private IContestDao contestDao;
    private IUserDao userDao;

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        userDao = context.getBean("userDao", IUserDao.class);
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        contestDao = new ContestDao(new ContestIdGeneratorImpl());

        // Set up Team
        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        Ebean.save(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", team, "87");
        Ebean.save(athleteGronk);

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

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up payouts
        contestPayout = new ContestPayout(1, 1, 100);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                new ArrayList<ContestPayout>(), null);
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfoGronk);

        // Set up Lineup and LineupSpot
        lineupSpot = new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady);
        lineupSpot2 = new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(lineupSpot);
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        Ebean.save(entry);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);
    }

    @After
    public void tearDown() {
        athleteTomBrady = null;
        athleteGronk = null;
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
    }

    @Test
    public void testFindByUser_AllEntries() {
        List<Entry> entries = contestDao.findEntries(user, Arrays.asList(ContestState.active, ContestState.cancelled,
                ContestState.complete, ContestState.locked, ContestState.open, ContestState.rosterLocked));
        assertTrue(entries.size() == 1);
        assertTrue(entries.get(0).getId() == entry.getId());

        User newUser = new User();
        newUser.setEmail("new.user@ruckusgaming.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setPassword("test");
        newUser.setUserName("newuser");
        Ebean.save(newUser);

        List<ContestState> contestStates = new ArrayList<>();
        contestStates.add(ContestState.active);

        entries = contestDao.findEntries(newUser, contestStates);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testFindByUser_ActiveContestsOnly_ActiveContest() {
        List<ContestState> contestStates = new ArrayList<>();
        contestStates.add(ContestState.active);

        List<Entry> entries = contestDao.findEntries(user, contestStates);
        assertTrue(entries.size() == 1);
        assertTrue(entries.get(0).getId() == entry.getId());
    }

    @Test
    public void testFindByUser_ActiveContestsOnly_InactiveContest() {
        Ebean.save(contest);

        List<ContestState> contestStates = new ArrayList<>();
        contestStates.add(ContestState.cancelled);

        List<Entry> entries = contestDao.findEntries(user, contestStates);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testFindEntriesForAthlete() {
        List<Entry> entryList = contestDao.findEntries(athleteSportEventInfoBrady);
        assertTrue("Expected one entry", !entryList.isEmpty());
        assertTrue(entryList.get(0).getContest().equals(contest) && entryList.get(0).getUser().equals(user));

        entryList = contestDao.findEntries(athleteSportEventInfoGronk);
        assertTrue("Expected zero entries", entryList.isEmpty());
    }

    @Test
    public void testFindById() {
        Entry foundEntry = contestDao.findEntry(entry.getId());
        assertTrue(foundEntry != null && foundEntry.getId() == entry.getId());
    }

    @Test
    public void testFindByLineupAndContest() {
        // Set up Entry
        Entry secondEntry = new Entry(user, contest, null);
        Ebean.save(secondEntry);

        List<Entry> entries = contestDao.findEntries(lineup, contest);
        assertTrue(entries.size() == 1);
        assertTrue(entries.get(0).getId() == entry.getId());
    }

    @Test
    public void testFindByContest() {
        // Set up Entry
        Entry secondEntry = new Entry(user, contest, lineup);
        secondEntry.setPoints(100);
        Ebean.save(secondEntry);

        List<Entry> entriesForLineup = new ArrayList<>();
        entriesForLineup.add(entry);
        lineup.setEntries(entriesForLineup);
        Ebean.save(lineup);

        entry.setPoints(80);
        Ebean.save(entry);

        List<Entry> entries = contestDao.findEntries(contest);
        assertTrue(entries.size() == 2);
        assertTrue(entries.get(0).getId() == entry.getId());
        assertTrue(entries.get(1).getId() == secondEntry.getId());
    }

    @Test
    public void testFindByUserAndContest() {
        // Set up Entry
        Entry secondEntry = new Entry(user, contest, lineup);
        Ebean.save(secondEntry);

        List<Entry> entriesForLineup = new ArrayList<>();
        entriesForLineup.add(entry);
        lineup.setEntries(entriesForLineup);
        Ebean.save(lineup);

        List<Entry> entries = contestDao.findEntries(user, contest);
        assertTrue(entries.size() == 2);
        assertTrue(entries.get(0).getId() == entry.getId() && entries.get(1).getId() == secondEntry.getId());
    }

//    @Test
//    public void testFindLineup() {
//        Lineup l = this.entry.findLineup();
//        assertNotNull(l);
//    }

    @Test
    public void testEntryDeserialization() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"id\":2,\n" +
                "\"user\":{\"id\":51,\"email\":\"dan.maclean@ruckusgaming.com\",\"userName\":\"terrorsquid\",\"firstName\":\"Dan\",\"lastName\":\"MacLean\",\"password\":\"$2a$10$n0cg5LrojA7wpLeEcOfjweNuQTSmqdDzIckPrUKFd/vShmMKoDJui\",\"version\":1403657494000},\n" +
                "\"contest\":{\"id\":1,\"urlId\":\"BZU2KyUl\",\n" +
                "\t\"contestType\":{\"id\":1,\"name\":\"Head to head\",\"abbr\":\"H2H\"},\n" +
                "\t\"league\":{\"id\":1,\n" +
                "\t\t\"sport\":{\"id\":2,\"name\":\"baseball\"},\n" +
                "\t\t\"name\":\"Major League Baseball\",\n" +
                "\t\t\"abbreviation\":\"MLB\",\n" +
                "\t\t\"displayName\":\"Major League Baseball\",\n" +
                "\t\t\"active\":true},\n" +
                "\t\"currentEntries\":2," +
                "\"capacity\":2," +
                "\"entryFee\":200," +
                "\"guaranteed\":false," +
                "\"allowedEntries\":1," +
                "\"sportEventGrouping\":{\n" +
                "\"sportEventGroupingType\":{" +
                "\"id\":4,\n" +
                "\"name\":\"ALL\"" +
                "}" +
                ",\"sportEvents\":[{" +
                "\"id\":1," +
                "\"statProviderId\":1378379," +
                "\"league\":{" +
                "\"id\":1," +
                "\"sport\":{\"id\":2,\"name\":\"baseball\"}," +
                "\"name\":\"Major League Baseball\"," +
                "\"abbreviation\":\"MLB\"," +
                "\"displayName\":\"Major League Baseball\"," +
                "\"active\":true}," +
                "\"startTime\":1403651100000," +
                "\"shortDescription\":\"{\\\"homeId\\\":\\\"225\\\",\\\"homeTeam\\\":\\\"Bal\\\",\\\"awayId\\\":\\\"228\\\",\\\"awayTeam\\\":\\\"CWS\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Baltimore Orioles\\\",\\\"awayTeam\\\":\\\"Chicago White Sox\\\",\\\"venue\\\":\\\"Oriole Park at Camden Yards\\\"}\"," +
                "\"teams\":[" +
                "{\"id\":17,\"statProviderId\":225,\"location\":\"Baltimore\",\"name\":\"Orioles\",\"abbreviation\":\"Bal\"}," +
                "{\"id\":26,\"statProviderId\":228,\"location\":\"Chicago\",\"name\":\"White Sox\",\"abbreviation\":\"CWS\"}]},{\"id\":2,\"statProviderId\":1378933,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403651100000,\"shortDescription\":\"{\\\"homeId\\\":\\\"246\\\",\\\"homeTeam\\\":\\\"Phi\\\",\\\"awayId\\\":\\\"252\\\",\\\"awayTeam\\\":\\\"Mia\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Philadelphia Phillies\\\",\\\"awayTeam\\\":\\\"Miami Marlins\\\",\\\"venue\\\":\\\"Citizens Bank Park\\\"}\",\"teams\":[{\"id\":6,\"statProviderId\":252,\"location\":\"Miami\",\"name\":\"Marlins\",\"abbreviation\":\"Mia\"},{\"id\":10,\"statProviderId\":246,\"location\":\"Philadelphia\",\"name\":\"Phillies\",\"abbreviation\":\"Phi\"}]},{\"id\":3,\"statProviderId\":1380552,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403651220000,\"shortDescription\":\"{\\\"homeId\\\":\\\"238\\\",\\\"homeTeam\\\":\\\"Tor\\\",\\\"awayId\\\":\\\"234\\\",\\\"awayTeam\\\":\\\"NYY\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Toronto Blue Jays\\\",\\\"awayTeam\\\":\\\"New York Yankees\\\",\\\"venue\\\":\\\"Rogers Centre\\\"}\",\"teams\":[{\"id\":19,\"statProviderId\":234,\"location\":\"New York\",\"name\":\"Yankees\",\"abbreviation\":\"NYY\"},{\"id\":20,\"statProviderId\":238,\"location\":\"Toronto\",\"name\":\"Blue Jays\",\"abbreviation\":\"Tor\"}]},{\"id\":4,\"statProviderId\":1379999,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403651400000,\"shortDescription\":\"{\\\"homeId\\\":\\\"254\\\",\\\"homeTeam\\\":\\\"TB\\\",\\\"awayId\\\":\\\"247\\\",\\\"awayTeam\\\":\\\"Pit\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Tampa Bay Rays\\\",\\\"awayTeam\\\":\\\"Pittsburgh Pirates\\\",\\\"venue\\\":\\\"Tropicana Field\\\"}\",\"teams\":[{\"id\":14,\"statProviderId\":247,\"location\":\"Pittsburgh\",\"name\":\"Pirates\",\"abbreviation\":\"Pit\"},{\"id\":16,\"statProviderId\":254,\"location\":\"Tampa Bay\",\"name\":\"Rays\",\"abbreviation\":\"TB\"}]},{\"id\":5,\"statProviderId\":1378825,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403651400000,\"shortDescription\":\"{\\\"homeId\\\":\\\"245\\\",\\\"homeTeam\\\":\\\"NYM\\\",\\\"awayId\\\":\\\"235\\\",\\\"awayTeam\\\":\\\"Oak\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"New York Mets\\\",\\\"awayTeam\\\":\\\"Oakland Athletics\\\",\\\"venue\\\":\\\"Citi Field\\\"}\",\"teams\":[{\"id\":9,\"statProviderId\":245,\"location\":\"New York\",\"name\":\"Mets\",\"abbreviation\":\"NYM\"},{\"id\":22,\"statProviderId\":235,\"location\":\"Oakland\",\"name\":\"Athletics\",\"abbreviation\":\"Oak\"}]},{\"id\":6,\"statProviderId\":1379263,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403654700000,\"shortDescription\":\"{\\\"homeId\\\":\\\"240\\\",\\\"homeTeam\\\":\\\"ChC\\\",\\\"awayId\\\":\\\"241\\\",\\\"awayTeam\\\":\\\"Cin\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Chicago Cubs\\\",\\\"awayTeam\\\":\\\"Cincinnati Reds\\\",\\\"venue\\\":\\\"Wrigley Field\\\"}\",\"teams\":[{\"id\":12,\"statProviderId\":240,\"location\":\"Chicago\",\"name\":\"Cubs\",\"abbreviation\":\"ChC\"},{\"id\":13,\"statProviderId\":241,\"location\":\"Cincinnati\",\"name\":\"Reds\",\"abbreviation\":\"Cin\"}]},{\"id\":7,\"statProviderId\":1380742,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403654700000,\"shortDescription\":\"{\\\"homeId\\\":\\\"237\\\",\\\"homeTeam\\\":\\\"Tex\\\",\\\"awayId\\\":\\\"230\\\",\\\"awayTeam\\\":\\\"Det\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Texas Rangers\\\",\\\"awayTeam\\\":\\\"Detroit Tigers\\\",\\\"venue\\\":\\\"Globe Life Park in Arlington\\\"}\",\"teams\":[{\"id\":24,\"statProviderId\":237,\"location\":\"Texas\",\"name\":\"Rangers\",\"abbreviation\":\"Tex\"},{\"id\":28,\"statProviderId\":230,\"location\":\"Detroit\",\"name\":\"Tigers\",\"abbreviation\":\"Det\"}]},{\"id\":8,\"statProviderId\":1379829,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403655000000,\"shortDescription\":\"{\\\"homeId\\\":\\\"242\\\",\\\"homeTeam\\\":\\\"Hou\\\",\\\"awayId\\\":\\\"239\\\",\\\"awayTeam\\\":\\\"Atl\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Houston Astros\\\",\\\"awayTeam\\\":\\\"Atlanta Braves\\\",\\\"venue\\\":\\\"Minute Maid Park\\\"}\",\"teams\":[{\"id\":7,\"statProviderId\":239,\"location\":\"Atlanta\",\"name\":\"Braves\",\"abbreviation\":\"Atl\"},{\"id\":25,\"statProviderId\":242,\"location\":\"Houston\",\"name\":\"Astros\",\"abbreviation\":\"Hou\"}]},{\"id\":9,\"statProviderId\":1379663,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403655000000,\"shortDescription\":\"{\\\"homeId\\\":\\\"232\\\",\\\"homeTeam\\\":\\\"Mil\\\",\\\"awayId\\\":\\\"244\\\",\\\"awayTeam\\\":\\\"Was\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Milwaukee Brewers\\\",\\\"awayTeam\\\":\\\"Washington Nationals\\\",\\\"venue\\\":\\\"Miller Park\\\"}\",\"teams\":[{\"id\":8,\"statProviderId\":244,\"location\":\"Washington\",\"name\":\"Nationals\",\"abbreviation\":\"Was\"},{\"id\":11,\"statProviderId\":232,\"location\":\"Milwaukee\",\"name\":\"Brewers\",\"abbreviation\":\"Mil\"}]},{\"id\":10,\"statProviderId\":1379434,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403655000000,\"shortDescription\":\"{\\\"homeId\\\":\\\"231\\\",\\\"homeTeam\\\":\\\"KC\\\",\\\"awayId\\\":\\\"243\\\",\\\"awayTeam\\\":\\\"LAD\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Kansas City Royals\\\",\\\"awayTeam\\\":\\\"Los Angeles Dodgers\\\",\\\"venue\\\":\\\"Ewing M. Kauffman Stadium\\\"}\",\"teams\":[{\"id\":3,\"statProviderId\":243,\"location\":\"Los Angeles\",\"name\":\"Dodgers\",\"abbreviation\":\"LAD\"},{\"id\":29,\"statProviderId\":231,\"location\":\"Kansas City\",\"name\":\"Royals\",\"abbreviation\":\"KC\"}]},{\"id\":11,\"statProviderId\":1379633,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403656800000,\"shortDescription\":\"{\\\"homeId\\\":\\\"251\\\",\\\"homeTeam\\\":\\\"Col\\\",\\\"awayId\\\":\\\"248\\\",\\\"awayTeam\\\":\\\"StL\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Colorado Rockies\\\",\\\"awayTeam\\\":\\\"St. Louis Cardinals\\\",\\\"venue\\\":\\\"Coors Field\\\"}\",\"teams\":[{\"id\":1,\"statProviderId\":251,\"location\":\"Colorado\",\"name\":\"Rockies\",\"abbreviation\":\"Col\"},{\"id\":15,\"statProviderId\":248,\"location\":\"St. Louis\",\"name\":\"Cardinals\",\"abbreviation\":\"StL\"}]},{\"id\":12,\"statProviderId\":1379531,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403660400000,\"shortDescription\":\"{\\\"homeId\\\":\\\"253\\\",\\\"homeTeam\\\":\\\"Ari\\\",\\\"awayId\\\":\\\"229\\\",\\\"awayTeam\\\":\\\"Cle\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Arizona Diamondbacks\\\",\\\"awayTeam\\\":\\\"Cleveland Indians\\\",\\\"venue\\\":\\\"Chase Field\\\"}\",\"teams\":[{\"id\":2,\"statProviderId\":253,\"location\":\"Arizona\",\"name\":\"Diamondbacks\",\"abbreviation\":\"Ari\"},{\"id\":27,\"statProviderId\":229,\"location\":\"Cleveland\",\"name\":\"Indians\",\"abbreviation\":\"Cle\"}]},{\"id\":13,\"statProviderId\":1378785,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403661900000,\"shortDescription\":\"{\\\"homeId\\\":\\\"227\\\",\\\"homeTeam\\\":\\\"LAA\\\",\\\"awayId\\\":\\\"233\\\",\\\"awayTeam\\\":\\\"Min\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Los Angeles Angels\\\",\\\"awayTeam\\\":\\\"Minnesota Twins\\\",\\\"venue\\\":\\\"Angel Stadium of Anaheim\\\"}\",\"teams\":[{\"id\":21,\"statProviderId\":227,\"location\":\"Los Angeles\",\"name\":\"Angels\",\"abbreviation\":\"LAA\"},{\"id\":30,\"statProviderId\":233,\"location\":\"Minnesota\",\"name\":\"Twins\",\"abbreviation\":\"Min\"}]},{\"id\":14,\"statProviderId\":1380703,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403662200000,\"shortDescription\":\"{\\\"homeId\\\":\\\"236\\\",\\\"homeTeam\\\":\\\"Sea\\\",\\\"awayId\\\":\\\"226\\\",\\\"awayTeam\\\":\\\"Bos\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"Seattle Mariners\\\",\\\"awayTeam\\\":\\\"Boston Red Sox\\\",\\\"venue\\\":\\\"Safeco Field\\\"}\",\"teams\":[{\"id\":18,\"statProviderId\":226,\"location\":\"Boston\",\"name\":\"Red Sox\",\"abbreviation\":\"Bos\"},{\"id\":23,\"statProviderId\":236,\"location\":\"Seattle\",\"name\":\"Mariners\",\"abbreviation\":\"Sea\"}]},{\"id\":15,\"statProviderId\":1380318,\"league\":{\"id\":1,\"sport\":{\"id\":2,\"name\":\"baseball\"},\"name\":\"Major League Baseball\",\"abbreviation\":\"MLB\",\"displayName\":\"Major League Baseball\",\"active\":true},\"startTime\":1403662500000,\"shortDescription\":\"{\\\"homeId\\\":\\\"250\\\",\\\"homeTeam\\\":\\\"SF\\\",\\\"awayId\\\":\\\"249\\\",\\\"awayTeam\\\":\\\"SD\\\"}\",\"description\":\"{\\\"homeTeam\\\":\\\"San Francisco Giants\\\",\\\"awayTeam\\\":\\\"San Diego Padres\\\",\\\"venue\\\":\\\"AT&T Park\\\"}\",\"teams\":[{\"id\":4,\"statProviderId\":249,\"location\":\"San Diego\",\"name\":\"Padres\",\"abbreviation\":\"SD\"},{\"id\":5,\"statProviderId\":250,\"location\":\"San Francisco\",\"name\":\"Giants\",\"abbreviation\":\"SF\"}]}]" +
                "}," +
                "\"salaryCap\":5000000," +
                "\"startTime\":1403651100000," +
                "\"contestPayouts\":[]," +
                "\"contestState\":{\"id\":1,\"name\":\"open\"},\"public\":true}," +
                "\"points\":14.945}";

        try {
            Entry entry = mapper.readValue(json, Entry.class);
            assertTrue(entry.getContest().getId() == 1
                    && entry.getUser().getId() == 51
                    && entry.getPoints() == 14.945);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetUnitsRemaining() {
        lineup.getLineupSpots().add(lineupSpot2);
        Ebean.save(lineup);

        sportEvent.setUnitsRemaining(45);
        Ebean.save(sportEvent);

        int unitsRemaining = contestDao.calculateUnitsRemaining(entry);
        assertTrue(String.format("Expected 90, got %s", unitsRemaining), unitsRemaining == 90);     // Gronk + Brady
    }

    @Test
    public void testGetH2HOpponent_H2H() {
        // Set up AppUser
        User user2 = new User();
        user2.setEmail("dmaclean82@gmail.com");
        user2.setFirstName("Dan");
        user2.setLastName("MacLean");
        user2.setPassword("test");
        user2.setUserName("dmaclean");
        userDao.saveUser(user2);

        contest.setContestType(ContestType.H2H);
        contest.setCurrentEntries(2);
        contestDao.saveContest(contest);

        // Set up Entry
        Entry secondEntry = new Entry(user2, contest, lineup);
        secondEntry.setPoints(100);
        contestDao.saveEntry(secondEntry);

        assertTrue(entry.determineH2HOpponent().equals(user2));
        assertTrue(secondEntry.determineH2HOpponent().equals(user));
    }

    @Test
    public void testGetH2HOpponent_NonH2H() {
// Set up AppUser
        User user2 = new User();
        user2.setEmail("dmaclean82@gmail.com");
        user2.setFirstName("Dan");
        user2.setLastName("MacLean");
        user2.setPassword("test");
        user2.setUserName("dmaclean");
        userDao.saveUser(user2);

        // Set up Entry
        Entry secondEntry = new Entry(user2, contest, lineup);
        secondEntry.setPoints(100);
        Ebean.save(secondEntry);

        assertTrue(entry.determineH2HOpponent() == null);
        assertTrue(secondEntry.determineH2HOpponent() == null);
    }

    public void testUpdateEntryFantasyPoints() {
        lineup.setLineupSpots(Arrays.asList(lineupSpot, lineupSpot2));
        contestDao.saveLineup(lineup);

        athleteSportEventInfoBrady.setFantasyPoints(new BigDecimal("10"));
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoBrady);

        entry.updateEntryFantasyPoints();
        Entry dbEntry = contestDao.findEntry(entry.getId());
        assertTrue(entry.getPoints() == 10 && dbEntry.getPoints() == 10);

        athleteSportEventInfoGronk.setFantasyPoints(new BigDecimal("5"));
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoGronk);

        entry.updateEntryFantasyPoints();
        dbEntry = contestDao.findEntry(entry.getId());
        assertTrue(entry.getPoints() == 15 && dbEntry.getPoints() == 15);
    }
}
