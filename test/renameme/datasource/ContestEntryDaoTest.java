package renameme.datasource;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import controllers.LineupValidationDuplicateAthleteException;
import controllers.LineupValidationException;
import dao.IContestDao;
import dao.ISportsDao;
import dao.IUserDao;
import models.contest.*;
import models.sports.*;
import models.user.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import utilities.BaseTest;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mwalsh on 7/10/14.
 * Modified by gislas on 8/11/14.
 */
public class ContestEntryDaoTest extends BaseTest {

    private IContestDao contestDao;
    private ISportsDao sportsDao;
    private IUserDao userDao;

    private User user;
    private SportEvent sportEvent;
    private SportEvent sportEvent2;
    private ContestGrouping grouping;
    private Contest contest;
    private Lineup lineup;
    private Team team;
    private Team team2;
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Athlete athleteJoeFlacco;
    private AthleteSalary athleteSalaryTomBrady;
    private AthleteSalary athleteSalaryGronk;
    private AthleteSalary athleteSalaryJoeFlacco;
    private AthleteSportEventInfo athleteSportEventInfoBrady;
    private AthleteSportEventInfo athleteSportEventInfoGronk;
    private AthleteSportEventInfo athleteSportEventInfoFlacco;

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        contestDao = context.getBean("contestDao", IContestDao.class);
        userDao = context.getBean("userDao", IUserDao.class);

        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        team2 = new Team(League.NFL, "Baltimore", "Ravens", "BAL", 2);
        sportsDao.saveTeam(team2);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        Ebean.save(athleteTomBrady);

        athleteGronk = new Athlete(2, "Rob", "Gronkowski", team, "87");
        Ebean.save(athleteGronk);

        athleteJoeFlacco = new Athlete(3, "Joe", "Flacco", team2, "7");
        Ebean.save(athleteJoeFlacco);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        sportEvent2 = new SportEvent(2, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent2);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.00"), "{\"passingYards\":100}", "[\"test1\"]");
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("12.00"), "{\"receivingYards\":100}", "[\"test2\"]");
        Ebean.save(athleteSportEventInfoGronk);
        athleteSportEventInfoFlacco = new AthleteSportEventInfo(sportEvent2, athleteJoeFlacco, new BigDecimal("12.00"), "{\"passingYards\":100}", "[\"test2\"]");
        Ebean.save(athleteSportEventInfoFlacco);

        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        userDao.plusUsd(user, 100000);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.NFL_FULL.getName(), League.NFL);
        Ebean.save(grouping);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(Arrays.asList(sportEvent, sportEvent2), type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));

        // Set up salaries
        athleteSalaryGronk = new AthleteSalary(athleteGronk, sportEventGrouping, 500000);
        sportsDao.saveAthleteSalary(athleteSalaryGronk);
        athleteSalaryTomBrady = new AthleteSalary(athleteTomBrady, sportEventGrouping, 500000);
        sportsDao.saveAthleteSalary(athleteSalaryTomBrady);
        athleteSalaryJoeFlacco = new AthleteSalary(athleteJoeFlacco, sportEventGrouping, 500000);
        sportsDao.saveAthleteSalary(athleteSalaryJoeFlacco);

        contest = new Contest(ContestType.H2H, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                Arrays.asList(new ContestPayout(1, 1, 50)), null);

        contest.setContestState(ContestState.open);
        Ebean.save(contest);

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_QUARTERBACK.FB_TIGHT_END, athleteSportEventInfoGronk));
        lineupSpots.add(new LineupSpot(athleteJoeFlacco, Position.FB_QUARTERBACK.FB_QUARTERBACK, athleteSportEventInfoFlacco));
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);
    }

    @Test
    public void testRetrieval() {
        SportEventGroupingType sportEventGroupingType = new SportEventGroupingType(League.NFL, "grouping",
                Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.FRIDAY, 1, 1, DayOfWeek.WEDNESDAY, 3, 4)));
        Ebean.save(sportEventGroupingType);
    }

    @Test
    public void testJoinContest_UnsavedLineup() {
        assertEquals(0, contest.getCurrentEntries());

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_QUARTERBACK.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_SUCCESS, status);

        Lineup updatedLineup = contestDao.findLineup(lineup2.getId());
        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(1, lineup2.getEntries().size());
        assertEquals(1, updatedLineup.getEntries().size());
        assertEquals(numEntries + 1, updatedContest.getCurrentEntries());
        assertEquals(ContestState.open.getId(), updatedContest.getContestState().getId());

        List<Entry> entries = contestDao.findEntries(contest);
        assertEquals(1, entries.size());
        assertEquals(lineup2.getId(), entries.get(0).getLineup().getId());
    }

    @Test
    public void testJoinContest_ContestFull() {
        assertEquals(0, contest.getCurrentEntries());

        // Set up AppUser
        User user2 = new User();
        user2.setEmail("dmaclean82@gmail.com");
        user2.setFirstName("Dan");
        user2.setLastName("MacLean");
        user2.setPassword("test");
        user2.setUserName("dmaclean");
        Ebean.save(user2);

        User user3 = new User();
        user3.setEmail("matt.walsh@ruckusgaming.com");
        user3.setFirstName("Matt");
        user3.setLastName("Walsh");
        user3.setPassword("test");
        user3.setUserName("walshms");
        Ebean.save(user3);

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_QUARTERBACK.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup 2", user2, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        List<LineupSpot> lineupSpots2 = new ArrayList<>();
        lineupSpots2.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots2.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup3 = new Lineup("My Lineup 3", user3, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots2);

        /*
         * First lineup joins contest - OK.
         */
        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup);
        assertEquals(GlobalConstants.CONTEST_ENTRY_SUCCESS, status);

        List<Entry> entries = contestDao.findEntries(contest);
        assertEquals(1, entries.size());
        assertEquals(lineup.getId(), entries.get(0).getLineup().getId());

        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries + 1, updatedContest.getCurrentEntries());
        assertEquals(ContestState.open.getId(), updatedContest.getContestState().getId());

        /*
         * Second lineup joins contest - OK.
         */
        numEntries = updatedContest.getCurrentEntries();
        status = contestDao.joinContest(user2, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_SUCCESS, status);

        entries = contestDao.findEntries(contest);
        assertEquals(2, entries.size());
        assertEquals(lineup.getId(), entries.get(0).getLineup().getId());
        assertEquals(lineup2.getId(), entries.get(1).getLineup().getId());

        updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries + 1, updatedContest.getCurrentEntries());
        assertEquals(ContestState.locked.getId(), updatedContest.getContestState().getId());

        /*
         * Third lineup joins contest - FULL.
         */
        numEntries = updatedContest.getCurrentEntries();
        status = contestDao.joinContest(user, contest, lineup3);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_FULL, status);

        entries = contestDao.findEntries(contest);
        assertEquals(2, entries.size());
        assertEquals(lineup.getId(), entries.get(0).getLineup().getId());
        assertEquals(lineup2.getId(), entries.get(1).getLineup().getId());

        updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
    }

    @Test
    public void testJoinContest_ContestStarted() {
        assertEquals(0, contest.getCurrentEntries());

        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_STARTED, status);

        Lineup updatedLineup = contestDao.findLineup(lineup2.getId());
        assertEquals(null, updatedLineup);
        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
        assertEquals(ContestState.active.getId(), updatedContest.getContestState().getId());

        List<Entry> entries = contestDao.findEntries(contest);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testJoinContest_ContestFull_RosterLocked() {
        assertTrue(contest.getCurrentEntries() == 0);

        contest.setContestState(ContestState.rosterLocked);
        Ebean.save(contest);

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_FULL, status);

        Lineup updatedLineup = contestDao.findLineup(lineup2.getId());
        assertEquals(null, updatedLineup);
        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
        assertEquals(ContestState.rosterLocked.getId(), updatedContest.getContestState().getId());

        List<Entry> entries = contestDao.findEntries(contest);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testJoinContest_ContestFull_EntryLocked() {
        assertEquals(0, contest.getCurrentEntries());

        contest.setContestState(ContestState.locked);
        Ebean.save(contest);

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_FULL, status);

        Lineup updatedLineup = contestDao.findLineup(lineup2.getId());
        assertEquals(null, updatedLineup);
        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
        assertEquals(ContestState.locked.getId(), updatedContest.getContestState().getId());

        List<Entry> entries = contestDao.findEntries(contest);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testJoinContest_ContestCancelled() {
        assertEquals(0, contest.getCurrentEntries());

        contest.setContestState(ContestState.cancelled);
        Ebean.save(contest);

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN, status);

        Lineup updatedLineup = contestDao.findLineup(lineup2.getId());
        assertEquals(null, updatedLineup);
        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
        assertEquals(ContestState.cancelled.getId(), updatedContest.getContestState().getId());

        List<Entry> entries = contestDao.findEntries(contest);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testJoinContest_InvalidContestId() {
        assertEquals(0, contest.getCurrentEntries());

        contest.setContestState(ContestState.active);
        Ebean.save(contest);

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID, status);

        Lineup updatedLineup = contestDao.findLineup(lineup2.getId());
        assertEquals(null, updatedLineup);
        Contest updatedContest = contestDao.findContest(contest.getId());
        assertTrue(lineup2.getEntries().isEmpty());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
        assertEquals(ContestState.active.getId(), updatedContest.getContestState().getId());

        List<Entry> entries = contestDao.findEntries(contest);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testJoinContest_SecondEntryInNonMultiEntry() {
        assertEquals(0, contest.getCurrentEntries());

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        /*
         * First line up enters - OK.
         */
        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_SUCCESS, status);

        List<Entry> entries = contestDao.findEntries(contest);
        assertEquals(1, entries.size());
        assertEquals(lineup2.getId(), entries.get(0).getLineup().getId());

        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries + 1, updatedContest.getCurrentEntries());
        assertEquals(ContestState.open.getId(), updatedContest.getContestState().getId());

        /*
         * Second lineup enters - Error on same user.
         */
        numEntries = updatedContest.getCurrentEntries();
        status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_SINGLE_ENTRY_DUPE, status);

        entries = contestDao.findEntries(contest);
        assertEquals(1, entries.size());
        assertEquals(lineup2.getId(), entries.get(0).getLineup().getId());

        updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
        assertEquals(ContestState.open.getId(), updatedContest.getContestState().getId());
    }

    @Test
    public void testJoinContest_IncompatibleLineup() {
        assertTrue(contest.getCurrentEntries() == 0);

        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType newType = new SportEventGroupingType(League.NFL, "Another grouping", null);
        Ebean.save(newType);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, newType);
        Ebean.save(sportEventGrouping);

        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, sportEventGrouping);
        lineup2.setLineupSpots(lineupSpots);

        /*
         * First line up enters - not compatible.
         */
        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_INCOMPATIBLE_LINEUP, status);

        List<Entry> entries = contestDao.findEntries(contest);
        assertTrue(entries.isEmpty());

        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
        assertEquals(ContestState.open.getId(), updatedContest.getContestState().getId());
    }

    @Test
    public void testJoinContest() {
        assertEquals(0, contest.getCurrentEntries());

        int numEntries = contest.getCurrentEntries();
        contestDao.joinContest(user, contest, lineup);

        Lineup updatedLineup = contestDao.findLineup(lineup.getId());
        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(1, lineup.getEntries().size());
        assertEquals(1, updatedLineup.getEntries().size());
        assertEquals(numEntries + 1, updatedContest.getCurrentEntries());
        assertEquals(ContestState.open.getId(), updatedContest.getContestState().getId());

        List<Entry> entries = contestDao.findEntries(contest);
        assertEquals(1, entries.size());
        assertEquals(lineup.getId(), entries.get(0).getLineup().getId());
    }


    @Test
    public void testJoinContestToFill() {
        assertTrue(contest.getCurrentEntries() == 0);

        // Set up AppUser
        User user2 = new User();
        user2.setEmail("dmaclean82@gmail.com");
        user2.setFirstName("Dan");
        user2.setLastName("MacLean");
        user2.setPassword("test");
        user2.setUserName("dmaclean");
        Ebean.save(user2);

        contestDao.joinContest(user, contest, lineup);

        //add second entry
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user2, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);
        contestDao.saveLineup(lineup2);
        contestDao.joinContest(user2, contest, lineup2);

        Lineup updatedLineup = contestDao.findLineup(lineup.getId());
        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(1, lineup.getEntries().size());
        assertEquals(1, updatedLineup.getEntries().size());
        assertEquals(2, updatedContest.getCurrentEntries());
        assertEquals(ContestState.locked.getId(), updatedContest.getContestState().getId());

        List<Entry> entries = contestDao.findEntries(contest);
        assertEquals(2, entries.size());
        assertEquals(lineup.getId(), entries.get(0).getLineup().getId());
        assertEquals(lineup2.getId(), entries.get(1).getLineup().getId());

        Contest newContest = Ebean.find(Contest.class)
                .fetch("contestPayouts")
                .where().eq("id", 2).findUnique();
        assertEquals(contest.calculatePrizePool(), newContest.calculatePrizePool());
    }

    @Test
    public void testJoinContest_Overbudget() {
        assertTrue(contest.getCurrentEntries() == 0);

        List<LineupSpot> lineupSpots = new ArrayList<>(); //error line after omitting the top two lines of the test.
        lineupSpots.add(new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        lineupSpots.add(new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk));
        Lineup lineup2 = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots);

        /*
         * Set the contest salary to something really low.
         */
        contest.setSalaryCap(1);
        Ebean.save(contest);

        int numEntries = contest.getCurrentEntries();
        int status = contestDao.joinContest(user, contest, lineup2);
        assertEquals(GlobalConstants.CONTEST_ENTRY_ERROR_OVER_SALARY_CAP, status);

        List<Entry> entries = contestDao.findEntries(contest);
        assertTrue(entries.isEmpty());

        Contest updatedContest = contestDao.findContest(contest.getId());
        assertEquals(numEntries, updatedContest.getCurrentEntries());
        assertEquals(ContestState.open.getId(), updatedContest.getContestState().getId());
    }

    @Test
    public void testValidateLineup_DuplicateAthletes() {
        Exception exception = null;

        try {
            contestDao.validateLineup(lineup, 5000000, Arrays.asList(
                    new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                    new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady)
            ));
        }
        catch(LineupValidationException e) {
            exception = e;
        }

        assertEquals(true, exception != null && exception instanceof LineupValidationDuplicateAthleteException);
    }

    @Test
    public void testValidateLineup_IncorrectNumberOfLineupSpots() {
        Exception exception = null;

        try {
            contestDao.validateLineup(lineup, 6000000, Arrays.asList(
                    new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK, athleteSportEventInfoBrady),
                    new LineupSpot(athleteGronk, Position.FB_TIGHT_END, athleteSportEventInfoGronk)
            ));
        }
        catch(LineupValidationException e) {
            exception = e;
        }

        assertEquals(true, exception != null && exception.getMessage().equals(GlobalConstants.LINEUP_SIZE_INVALID_ERROR));
    }

    @Test
    public void testFindLineups_NotDistinct() {
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK.FB_QUARTERBACK, athleteSportEventInfoBrady),
                new LineupSpot(athleteGronk, Position.FB_QUARTERBACK.FB_TIGHT_END, athleteSportEventInfoGronk),
                new LineupSpot(athleteJoeFlacco, Position.FB_QUARTERBACK.FB_QUARTERBACK, athleteSportEventInfoFlacco)
        ));
        Ebean.save(lineup);

        Entry entry = new Entry(user, contest, lineup);
        contestDao.saveEntry(entry);

        Entry entry2 = new Entry(user, contest, lineup);
        contestDao.saveEntry(entry2);

        List<Lineup> lineups = contestDao.findLineups(user, Arrays.asList(ContestState.open, ContestStateActive.locked));
        assertEquals(2, lineups.size());
    }

    @Test
    public void testFindLineups_Distinct() {
        lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(Arrays.asList(
                new LineupSpot(athleteTomBrady, Position.FB_QUARTERBACK.FB_QUARTERBACK, athleteSportEventInfoBrady),
                new LineupSpot(athleteGronk, Position.FB_QUARTERBACK.FB_TIGHT_END, athleteSportEventInfoGronk),
                new LineupSpot(athleteJoeFlacco, Position.FB_QUARTERBACK.FB_QUARTERBACK, athleteSportEventInfoFlacco)
        ));
        Ebean.save(lineup);

        Entry entry = new Entry(user, contest, lineup);
        contestDao.saveEntry(entry);

        Entry entry2 = new Entry(user, contest, lineup);
        contestDao.saveEntry(entry2);

        List<Lineup> lineups = contestDao.findLineups(user, Arrays.asList(ContestState.open, ContestStateActive.locked), true);
        assertEquals(1, lineups.size());
    }

    @Test
    public void testFindNonTerminalContests() {
        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(Arrays.asList(sportEvent, sportEvent2), type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));

        // Open contest is represented by the instance attribute contest.
        Contest entryLockedContest = new Contest(ContestType.H2H, "11", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                Arrays.asList(new ContestPayout(1, 1, 50)), null);
        entryLockedContest.setContestState(ContestState.locked);
        contestDao.saveContest(entryLockedContest);

        Contest rosterLockedContest = new Contest(ContestType.H2H, "22", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                Arrays.asList(new ContestPayout(1, 1, 50)), null);
        rosterLockedContest.setContestState(ContestState.rosterLocked);
        contestDao.saveContest(rosterLockedContest);

        Contest activeContest = new Contest(ContestType.H2H, "33", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                Arrays.asList(new ContestPayout(1, 1, 50)), null);
        activeContest.setContestState(ContestState.active);
        contestDao.saveContest(activeContest);

        Contest completeContest = new Contest(ContestType.H2H, "44", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                Arrays.asList(new ContestPayout(1, 1, 50)), null);
        completeContest.setContestState(ContestState.complete);
        contestDao.saveContest(completeContest);

        Contest cancelledContest = new Contest(ContestType.H2H, "55", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                Arrays.asList(new ContestPayout(1, 1, 50)), null);
        cancelledContest.setContestState(ContestState.cancelled);
        contestDao.saveContest(cancelledContest);

        Contest historyContest = new Contest(ContestType.H2H, "66", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                Arrays.asList(new ContestPayout(1, 1, 50)), null);
        historyContest.setContestState(ContestState.locked);
        contestDao.saveContest(historyContest);

        Contest uninitializedContest = new Contest(ContestType.H2H, "77", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping,
                Arrays.asList(new ContestPayout(1, 1, 50)), null);
        uninitializedContest.setContestState(new ContestStateUninitialized());
        contestDao.saveContest(uninitializedContest);

        boolean entryLockedFound = false;
        boolean openFound = false;
        boolean rosterLockedFound = false;
        boolean activeFound = false;
        boolean completeFound = false;
        boolean cancelledFound = false;
        boolean historyFound = false;
        boolean uninitializedFound = false;

        List<Contest> nonTerminalContests = contestDao.findNonTerminalContests();
        for(Contest contest: nonTerminalContests) {
            if(contest.getContestState().equals(ContestState.locked))       entryLockedFound = true;
            else if(contest.getContestState().equals(ContestState.open))    openFound = true;
            else if(contest.getContestState().equals(ContestState.rosterLocked))    rosterLockedFound = true;
            else if(contest.getContestState().equals(ContestState.active))    activeFound = true;
            else if(contest.getContestState().equals(ContestState.complete))    completeFound = true;
            else if(contest.getContestState().equals(ContestState.cancelled))    cancelledFound = true;
            else if(contest.getContestState().equals(ContestState.history))    historyFound = true;
            else if(contest.getContestState().getId() == new ContestStateUninitialized().getId())    uninitializedFound = true;
        }

        assertEquals(true, entryLockedFound);
        assertEquals(true, openFound);
        assertEquals(true, rosterLockedFound);
        assertEquals(true, activeFound);
        assertEquals(true, completeFound);
        assertEquals(true, uninitializedFound);
        assertEquals(false, cancelledFound);
        assertEquals(false, historyFound);
    }
}
