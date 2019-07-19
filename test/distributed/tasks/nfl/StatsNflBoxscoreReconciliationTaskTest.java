package distributed.tasks.nfl;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import dao.*;
import models.contest.*;
import models.sports.*;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.edge.TestScoringRulesService;
import stats.parser.nfl.BoxscoreParser;
import stats.retriever.nfl.NflBoxscoreRetriever;
import stats.statsinc.FileStatProvider;
import stats.translator.nfl.FantasyPointTranslator;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;
import utils.TimeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by dmaclean on 8/20/14.
 */
public class StatsNflBoxscoreReconciliationTaskTest extends BaseTest {

    User user1;
    User user2;

    Contest contest;

    Entry entry1;
    Entry entry2;

    Lineup lineup1;
    Lineup lineup2;

    SportEventGrouping sportEventGrouping;
    SportEventGroupingType sportEventGroupingType;

    ContestPayout contestPayout;

    SportEvent sportEvent;

    Team cleveland;
    Team washington;

    Athlete alfredMorris;
    Athlete kirkCousins;
    Athlete benTate;
    Athlete johnnyManziel;

    AthleteSportEventInfo athleteSportEventInfoMorris;
    AthleteSportEventInfo athleteSportEventInfoCousins;
    AthleteSportEventInfo athleteSportEventInfoTate;
    AthleteSportEventInfo athleteSportEventInfoManziel;

    ISportsDao sportsDao;
    IContestDao contestDao;
    IUserDao userDao;

    StatsNflBoxscoreReconciliationTask boxscoreReconciliationTask;
    BoxscoreParser parser;
    NflBoxscoreRetriever boxscoreRetriever;
    FileStatProvider statProvider;

    @Before
    public void setUp() {
        sportsDao = new SportsDao();
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        userDao = new UserDao();

        cleveland = new Team(League.NFL, "Cleveland", "Browns", "CLE", 329);
        sportsDao.saveTeam(cleveland);
        washington = new Team(League.NFL, "Washington", "Redskins", "WAS", 363);
        sportsDao.saveTeam(washington);

        sportEvent = new SportEvent(1420061, League.NFL, new Date(), "{}", "{}", 60, false, 2014, 1, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        sportsDao.saveSportEvent(sportEvent);

        alfredMorris = new Athlete(382365, "Alfred", "Morris", washington, "1");
        sportsDao.saveAthlete(alfredMorris);
        kirkCousins = new Athlete(403308, "Kirk", "Cousins", washington, "2");
        sportsDao.saveAthlete(kirkCousins);
        benTate = new Athlete(323146, "Ben", "Tate", cleveland, "3");
        sportsDao.saveAthlete(benTate);
        johnnyManziel = new Athlete(593578, "Johnny", "Manziel", cleveland, "4");
        sportsDao.saveAthlete(johnnyManziel);

        athleteSportEventInfoMorris = new AthleteSportEventInfo(sportEvent, alfredMorris, new BigDecimal("10"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoMorris);
        athleteSportEventInfoCousins = new AthleteSportEventInfo(sportEvent, kirkCousins, new BigDecimal("11"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoCousins);
        athleteSportEventInfoTate = new AthleteSportEventInfo(sportEvent, benTate, new BigDecimal("12"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoTate);
        athleteSportEventInfoManziel = new AthleteSportEventInfo(sportEvent, johnnyManziel, new BigDecimal("10"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoManziel);

        sportEventGroupingType = new SportEventGroupingType(League.NFL, "test", Arrays.asList(new SportEventDateRangeSelector(DayOfWeek.FRIDAY, 0, 0, DayOfWeek.MONDAY, 0, 0)));
        Ebean.save(sportEventGroupingType);

        sportEventGrouping = new SportEventGrouping(Arrays.asList(sportEvent), sportEventGroupingType);
        contestDao.saveSportEventGrouping(sportEventGrouping);

        user1 = new User();
        user1.setUserName("terrorsquid");
        userDao.saveUser(user1);

        user2 = new User();
        user2.setUserName("dmaclean");
        userDao.saveUser(user2);

        contestPayout = new ContestPayout(1, 1, 190);

        contest = new Contest(ContestType.H2H, "ABC", League.NFL, 2, true, 100, 1, 5000000, sportEventGrouping, Arrays.asList(contestPayout), null);
        contest.setContestState(ContestState.complete);
        contestDao.saveContest(contest);

        lineup1 = new Lineup("l1", user1, League.NFL, sportEventGrouping);
        lineup1.setLineupSpots(Arrays.asList(
                new LineupSpot(alfredMorris, Position.FB_RUNNINGBACK, athleteSportEventInfoMorris),
                new LineupSpot(kirkCousins, Position.FB_QUARTERBACK, athleteSportEventInfoCousins)
        ));
        contestDao.saveLineup(lineup1);

        lineup2 = new Lineup("l2", user2, League.NFL, sportEventGrouping);
        lineup2.setLineupSpots(Arrays.asList(
                new LineupSpot(benTate, Position.FB_RUNNINGBACK, athleteSportEventInfoTate),
                new LineupSpot(johnnyManziel, Position.FB_QUARTERBACK, athleteSportEventInfoManziel)
        ));
        contestDao.saveLineup(lineup2);

        entry1 = new Entry(user1, contest, lineup1);
        contestDao.saveEntry(entry1);
        entry2 = new Entry(user2, contest, lineup2);
        contestDao.saveEntry(entry2);

        lineup1.setEntries(Arrays.asList(entry1));
        contestDao.saveLineup(lineup1);
        lineup2.setEntries(Arrays.asList(entry2));
        contestDao.saveLineup(lineup2);

        statProvider = new FileStatProvider();
        statProvider.setFilename("test_files/nfl_boxscores/nfl_event_boxscore_20140818_CLE_WAS.json");

        parser = new BoxscoreParser(new FantasyPointTranslator(new TestScoringRulesService()));
        boxscoreRetriever = new NflBoxscoreRetriever(parser, statProvider);
        boxscoreReconciliationTask = new StatsNflBoxscoreReconciliationTask(new TimeService(), boxscoreRetriever);
    }

    @After
    public void tearDown() {
        user1 = null;
        user2 = null;

        contest = null;

        entry1 = null;
        entry2 = null;

        lineup1 = null;
        lineup2 = null;

        sportEventGrouping = null;
        sportEventGroupingType = null;

        contestPayout = null;

        sportEvent = null;

        cleveland = null;
        washington = null;

        alfredMorris = null;
        kirkCousins = null;
        benTate = null;
        johnnyManziel = null;

        athleteSportEventInfoMorris = null;
        athleteSportEventInfoCousins = null;
        athleteSportEventInfoTate = null;
        athleteSportEventInfoManziel = null;

        sportsDao = null;
        contestDao = null;
        userDao = null;

        boxscoreReconciliationTask = null;
        parser = null;
        boxscoreRetriever = null;
        statProvider = null;
    }

    @Test
    public void testExecute_Completed() {
        try {
            boxscoreReconciliationTask.execute();

            Contest updatedContest = contestDao.findContest(contest.getId());
            assertNotNull(updatedContest.getReconciledTime());

            // Alfred Morris fps
            AthleteSportEventInfo updatedASEI = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoMorris.getId());
            assertEquals(new BigDecimal("2.90"), updatedASEI.getFantasyPoints());

            // Ben Tate fps
            updatedASEI = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoTate.getId());
            assertEquals(new BigDecimal("5.10"), updatedASEI.getFantasyPoints());

            // Kirk Cousins fps
            updatedASEI = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoCousins.getId());
            assertEquals(new BigDecimal("8.80"), updatedASEI.getFantasyPoints());

            // Johnny Manziel fps
            updatedASEI = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoManziel.getId());
            assertEquals(new BigDecimal("6.50"), updatedASEI.getFantasyPoints());

            // Entry 1 fps
            Entry updatedEntry = contestDao.findEntry(entry1.getId());
            assertEquals(new BigDecimal("11.70"), new BigDecimal(updatedEntry.getPoints()).setScale(2, RoundingMode.HALF_EVEN));

            // Entry 2 fps
            updatedEntry = contestDao.findEntry(entry2.getId());
            assertEquals(new BigDecimal("11.60"), new BigDecimal(updatedEntry.getPoints()).setScale(2, RoundingMode.HALF_EVEN));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testExecute_NonCompleted() {
        contest.setContestState(ContestState.active);
        contestDao.saveContest(contest);

        try {
            boxscoreReconciliationTask.execute();

            Contest updatedContest = contestDao.findContest(contest.getId());
            assertNull(updatedContest.getReconciledTime());
            assertEquals(ContestState.active, updatedContest.getContestState());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
