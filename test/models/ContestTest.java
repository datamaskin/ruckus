package models;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import dao.ContestDao;
import dao.DaoFactory;
import dao.IUserDao;
import models.contest.*;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.sports.SportEventGroupingType;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stats.translator.IFantasyPointTranslator;
import stats.translator.nfl.FantasyPointTranslator;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by dan on 4/10/14.
 * Modified by gislas on 8/8/14.
 */
public class ContestTest extends BaseTest {
    private ContestPayout payout;

    private List<SportEvent> sportEvents;

    private SportEvent s1;
    private SportEvent s2;

    private ContestGrouping grouping;

    private SportEventGrouping sportEventGrouping;
    private ContestDao contestDao;
    private IUserDao userDao;

    //Changed some of the parameters for functions to NFL stuff, however getting database errors from the testing more than anything else. Don't think string
    //descriptions matter. Passed tests. 

    @Before
    public void setUp() {
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        userDao = DaoFactory.getUserDao();

        payout = new ContestPayout(1, 25, 1000);

        sportEvents = new ArrayList<>();
        s1 = new SportEvent(1, League.NFL, new Date(), "Yankees vs RedSox", "test", 9, false, 2014, -1, 1); //changed parameters for league and teams (Yankees vs RedSox)
        Ebean.save(s1);
        s2 = new SportEvent(2, League.NFL, new Date(), "Braves vs Mariners", "test", 9, false, 2014, -1, 1); //changed parameters for league and teams (Braves vs Mariners)
        Ebean.save(s2);
        sportEvents.add(s1);
        sportEvents.add(s2);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.NFL_FULL.getName(), ContestGrouping.NFL_FULL.getLeague()); //changed parameters from MLB_ALL to NFL_FULL
        Ebean.save(grouping);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null); //changed parameters for league
        Ebean.save(type);
        sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

    }

    @After
    public void tearDown() {
        payout = null;
        grouping = null;
    }

    public void testFindById() {
        Contest newContest = new Contest(ContestType.H2H, "test", League.NFL, 5, true,
                1, 1, 5000000, sportEventGrouping,
                new ArrayList<ContestPayout>(), null
        ); //changed parameters for league
        Ebean.save(newContest);

        Contest queriedContest = contestDao.findContest(newContest.getId());
        assertTrue(queriedContest.getId() == newContest.getId());
    }

    @Test
    public void createAndRetrieveContest() {
        List<ContestPayout> payouts = new ArrayList<>();
        payouts.add(payout);

        Contest newContest = new Contest(ContestType.H2H, "someUrl", League.NFL, 5, true,
                1, 1, 5000000, sportEventGrouping,
                payouts, null
        ); //changed parameters for league
        newContest.setContestState(ContestState.active);
        Ebean.save(newContest);
        List<Contest> contests = contestDao.findContests(ContestState.active);

        assertTrue(contests.size() == 1);
        assertTrue(contests.get(0).getContestType().equals(ContestType.H2H));
        assertTrue(contests.get(0).getLeague().equals(League.NFL)); //changed league parameter
        assertTrue(contests.get(0).getEntryFee() == 1);
    }

    @Test
    public void testFindContestsForUser_OneActive() {
        User user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        Contest contest = new Contest(ContestType.H2H, "123", League.NFL, 2, true, 100, 1,
                5000000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        contest.setContestState(ContestState.active); //changed parameters for league
        Ebean.save(contest);

        Entry entry = new Entry(user, contest, null);
        Ebean.save(entry);

        // Make sure we can find the active contest.
        List<ContestState> contestStates = new ArrayList<>();
        contestStates.add(ContestState.active);
        List<Contest> contests = contestDao.findContests(user, contestStates);
        assertTrue(contests.size() == 1);
        assertTrue(contests.get(0).getId() == contest.getId());
    }

    @Test
    public void testFindContestsForUser_OneInactive() {
        User user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        Contest contest = new Contest(ContestType.H2H, "123", League.NFL, 2, false, 100, 1,
                5000000, sportEventGrouping, new ArrayList<>(), null); //changed parameters for league
        contest.setContestState(ContestState.cancelled);
        Ebean.save(contest);

        Entry entry = new Entry(user, contest, null);
        Ebean.save(entry);

        List<ContestState> contestStates = new ArrayList<>();
        contestStates.add(ContestState.cancelled);
        List<Contest> contests = contestDao.findContests(user, contestStates);
        assertTrue(contests.size() == 1);
        assertTrue(contests.get(0).getId() == contest.getId());

        contestStates = new ArrayList<>();
        contestStates.add(ContestState.active);
        contests = contestDao.findContests(user, contestStates);
        assertTrue(contests.isEmpty());
    }

    @Test
    public void testFindContestsForUser_NoEntries() {
        User user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        Contest contest = new Contest(ContestType.H2H, "123", League.NFL, 2, true, 100, 1,
                5000000, sportEventGrouping, new ArrayList<>(), null); //changed parameters for league
        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        List<ContestState> contestStates = new ArrayList<>();
        contestStates.add(ContestState.active);
        List<Contest> contests = contestDao.findContests(user, contestStates);
        assertTrue(contests.isEmpty());
    }

    @Test
    public void testFindContestsForLobby() {
        // One existing active contest.
        Contest openContest = new Contest(ContestType.H2H, "456", League.NFL, 2, true, 100, 1,
                5000000, sportEventGrouping, new ArrayList<>(), null); //changed parameters for league
        openContest.setContestState(ContestState.open);
        Ebean.save(openContest);

        Contest cancelledContest = new Contest(ContestType.H2H, "789", League.NFL, 2, true, 100, 1,
                5000000, sportEventGrouping, new ArrayList<>(), null); //changed parameters for league
        cancelledContest.setContestState(ContestState.cancelled);
        Ebean.save(cancelledContest);

        List<Contest> lobby = contestDao.findNonTerminalContests();
        assertTrue(lobby.size() == 1);
        assertTrue(lobby.get(0).getId() == openContest.getId());

    }

    @Test
    public void testCalculateRemainingAllowedEntries_SingleEntry() {
        User user = new User();
        user.setEmail("dmaclean@email.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("dmaclean");
        userDao.saveUser(user);

        Contest newContest = new Contest(ContestType.H2H, "test", League.NFL, 5, true,
                1, 1, 5000000, sportEventGrouping, new ArrayList<ContestPayout>(), null); //changed parameters for league
        newContest.setCurrentEntries(0);
        contestDao.saveContest(newContest);

        assertTrue(newContest.calculateRemainingAllowedEntries(user) == 1);

        Entry entry = new Entry(user, newContest, null);
        contestDao.saveEntry(entry);

        assertTrue(newContest.calculateRemainingAllowedEntries(user) == 0);
    }

    @Test
    public void testCalculateRemainingAllowedEntries_MultiEntry() {
        User user = new User();
        user.setEmail("dmaclean@email.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("dmaclean");
        userDao.saveUser(user);

        Contest newContest = new Contest(ContestType.H2H, "test", League.NFL, 5, true,
                1, 3, 5000000, sportEventGrouping, new ArrayList<ContestPayout>(), null); //changed parameters for league
        newContest.setCurrentEntries(0);
        contestDao.saveContest(newContest);

        assertTrue(newContest.calculateRemainingAllowedEntries(user) == 3);

        Entry entry = new Entry(user, newContest, null);
        contestDao.saveEntry(entry);

        assertTrue(newContest.calculateRemainingAllowedEntries(user) == 2);
    }

    @Test
    public void testGetStatsFantasyPointTranslator_NFL() {
        Contest newContest = new Contest(ContestType.H2H, "test", League.NFL, 5, true,
                1, 1, 5000000, sportEventGrouping, new ArrayList<ContestPayout>(), null);

        IFantasyPointTranslator translator = newContest.getStatsFantasyPointTranslator(context);
        assertEquals(FantasyPointTranslator.class, translator.getClass());
    }

    @Test
    public void testGetStatsFantasyPointTranslator_MLB() {
        Contest newContest = new Contest(ContestType.H2H, "test", League.MLB, 5, true,
                1, 1, 5000000, sportEventGrouping, new ArrayList<ContestPayout>(), null);

        IFantasyPointTranslator translator = newContest.getStatsFantasyPointTranslator(context);
        assertEquals(stats.translator.mlb.FantasyPointTranslator.class, translator.getClass());
    }

    @Test
    public void testGetSuggestedContests_Freeroll() {
        User user = new User();
        user.setUserName("terrorsquid");
        userDao.saveUser(user);

        Lineup lineup = new Lineup("My lineup", user, League.NFL, sportEventGrouping);
        contestDao.saveLineup(lineup);

        Contest gpp = new Contest(ContestType.GPP, "testgpp", League.NFL, 5, true,
                100, 1, 5000000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        gpp.setCurrentEntries(0);
        gpp.setContestState(ContestState.open);
        contestDao.saveContest(gpp);

        Contest h2h = new Contest(ContestType.H2H, "testh2h", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        h2h.setCurrentEntries(0);
        h2h.setContestState(ContestState.open);
        contestDao.saveContest(h2h);

        Contest satellite = new Contest(ContestType.SATELLITE, "testsatellite", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        satellite.setCurrentEntries(0);
        satellite.setContestState(ContestState.open);
        contestDao.saveContest(satellite);

        Contest normal = new Contest(ContestType.NORMAL, "testnormal", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        normal.setCurrentEntries(0);
        normal.setContestState(ContestState.open);
        contestDao.saveContest(normal);

        Contest normal2 = new Contest(ContestType.NORMAL, "testnormal2", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        normal2.setCurrentEntries(0);
        normal2.setContestState(ContestState.open);
        contestDao.saveContest(normal2);

        Contest doubleUp = new Contest(ContestType.DOUBLE_UP, "testdoubleup", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        doubleUp.setCurrentEntries(0);
        doubleUp.setContestState(ContestState.open);
        contestDao.saveContest(doubleUp);

        Map<String, Object> results = gpp.getSuggestedContests(user, GlobalConstants.CONTEST_ENTRY_SUCCESS, lineup);
        assertNotNull(results);

        assertNull(results.get("duplicateContest"));

        List<Contest> additionalContests = (List<Contest>) results.get("additionalContests");
        assertEquals(3, additionalContests.size());
        assertEquals(normal, additionalContests.get(0));
        assertEquals(normal2, additionalContests.get(1));
        assertEquals(doubleUp, additionalContests.get(2));
    }

    @Test
    public void testGetSuggestedContests_FreerollSuggestionAlreadyEntered() {
        User user = new User();
        user.setUserName("terrorsquid");
        userDao.saveUser(user);

        Lineup lineup = new Lineup("My lineup", user, League.NFL, sportEventGrouping);
        contestDao.saveLineup(lineup);

        Contest gpp = new Contest(ContestType.GPP, "testgpp", League.NFL, 5, true,
                100, 1, 5000000, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        gpp.setCurrentEntries(0);
        gpp.setContestState(ContestState.open);
        contestDao.saveContest(gpp);

        Contest h2h = new Contest(ContestType.H2H, "testh2h", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        h2h.setCurrentEntries(0);
        h2h.setContestState(ContestState.open);
        contestDao.saveContest(h2h);

        Contest satellite = new Contest(ContestType.SATELLITE, "testsatellite", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        satellite.setCurrentEntries(0);
        satellite.setContestState(ContestState.open);
        contestDao.saveContest(satellite);

        Contest normal = new Contest(ContestType.NORMAL, "testnormal", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        normal.setCurrentEntries(0);
        normal.setContestState(ContestState.open);
        contestDao.saveContest(normal);

        Contest normal2 = new Contest(ContestType.NORMAL, "testnormal2", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        normal2.setCurrentEntries(0);
        normal2.setContestState(ContestState.open);
        contestDao.saveContest(normal2);

        Contest doubleUp = new Contest(ContestType.DOUBLE_UP, "testdoubleup", League.NFL, 5, true,
                200, 1, 0, sportEventGrouping, new ArrayList<ContestPayout>(), null);
        doubleUp.setCurrentEntries(0);
        doubleUp.setContestState(ContestState.open);
        contestDao.saveContest(doubleUp);

        Entry entry = new Entry(user, gpp, lineup);
        contestDao.saveEntry(entry);

        lineup.setEntries(Arrays.asList(entry));
        contestDao.saveLineup(lineup);

        Map<String, Object> results = gpp.getSuggestedContests(user, GlobalConstants.CONTEST_ENTRY_SUCCESS, lineup);
        assertNotNull(results);

        assertNull(results.get("duplicateContest"));

        List<Contest> additionalContests = (List<Contest>) results.get("additionalContests");
        assertEquals(false, additionalContests.contains(gpp));
    }

    //TODO: Edit the the test validations to not return errors from a few conventions, may not even be called.

//    @Test
//    public void testValidations() {
//        Contest contest = new Contest(ContestType.H2H, League.NFL, 5, 10, true,
//                "1st - $50, 2nd - $25", 1, false, false, ContestGrouping.ALL, 50000, cal.getTime()); //error line - cannot find symbol, symbol cal

//        Collection<ConstraintViolation<Contest>> errors = Validation.getValidator().validate(contest); //error line - cannot find symbol, ConstraintViolation symbol
//        assertTrue(errors.size() == 0);

        /*
        Test contest type requiredness
         */
//        contest.type = null; //error line - cannot find symbol, symbol type
//        errors = Validation.getValidator().validate(contest); //error line - cannot find symbol, symbol Validation
//        assertTrue(errors.size() == 1);
//        contest.type = ContestType.H2H; //error line - cannot find symbol, symbol sport

        /*
        Test contest sport requiredness
         */
//        contest.sport = null; //error line - cannot find symbol, symbol sport
//        errors = Validation.getValidator().validate(contest); //error line - cannot find symbol, symbol Validation
//        assertTrue(errors.size() == 1);
//        contest.sport = Sport.NFL; //error line - cannot find symbol, symbol Sport

        /*
        Test contest size min value
         */
//        contest.currentEntries = -1;
//        errors = Validation.getValidator().validate(contest);
//        assertTrue(errors.size() == 1);
//        contest.currentEntries = 0; //error line - currentEntries has private access in Contest

        /*
        Test contest capacity min value
         */
//        contest.capacity = 1; //error line - capacity has private access in Contest
//        errors = Validation.getValidator().validate(contest); //error line - cannot find symbol, symbol Validation
//        assertTrue(errors.size() == 1);
//        contest.capacity = 2; // error line - capacity has private access in Contest

        /*
        Test contest payout requiredness
         */
//        contest.payout = null; //error line - cannot find symbol, symbol payout
//       errors = Validation.getValidator().validate(contest); //error line - cannot find symbol, symbol Validation
//       assertTrue(errors.size() == 1);
//        contest.payout = "1st - $50, 2nd - $25"; //error line - cannot find symbol, symbol payout

       /*
        Test contest entry fee min value
         */
//        contest.entryFee = -1; //error line - entryFee has private access in Contest
//        errors = Validation.getValidator().validate(contest); //error line - cannot find symbol, symbol Validation
//        assertTrue(errors.size() == 1);
//        contest.entryFee = 1; //error line - entryFee has private access in Contest

        /*
        Test contest grouping requiredness
         */
//        contest.grouping = null; //error line - cannot find symbol, symbol grouping
//        errors = Validation.getValidator().validate(contest); //error line - cannot find symbol, symbol Validation
//        assertTrue(errors.size() == 1);
//        contest.grouping = Contest.CONTEST_GROUPING_ALL;//error line - cannot find symbol, symbol grouping / cannot find symbol, symbol CONTEST_GROUPING_ALL

        /*
        Test contest start time requiredness
         */
//        contest.startTime = null;//error line - startTime has private access in Contest
//        errors = Validation.getValidator().validate(contest); //error line - cannot find symbol, symbol Validation
//        assertTrue(errors.size() == 1);
//        contest.startTime = cal.getTime(); //error line - cannot find symbol, symbol cal
//    }
}
