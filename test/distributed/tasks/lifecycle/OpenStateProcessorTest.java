package distributed.tasks.lifecycle;

import service.IContestListService;
import com.avaje.ebean.Ebean;
import dao.*;
import models.contest.*;
import models.sports.*;
import models.user.User;
import models.wallet.UserWallet;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mwalsh on 7/25/14.
 */
public class OpenStateProcessorTest extends BaseTest {

    private OpenStateProcessor processor;
    private Instant now;
    private IContestListService contestListManager;

    private IContestDao contestDao;
    private ISportsDao sportsDao;
    private IUserDao userDao;
    private ContestType contestType;
    private String urlId;
    private League league;
    private SportEventGrouping sportEventGrouping;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;

    private Team team;
    private Team team2;
    private Athlete athleteTomBrady;
    private Athlete athleteGronk;
    private Athlete athleteJoeFlacco;
    private AthleteSportEventInfo athleteSportEventInfoBrady;
    private AthleteSportEventInfo athleteSportEventInfoGronk;
    private AthleteSportEventInfo athleteSportEventInfoFlacco;
    private SportEvent sportEvent;
    private SportEvent sportEvent2;
    private Contest contest;
    private List<LineupSpot> defaultLineup;

    @Before
    public void setup() {
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        sportsDao = new SportsDao();
        userDao = new UserDao();
        contestType = ContestType.NORMAL;
        urlId = "something";
        league = League.NFL;

        SportEventGroupingType sportEventGroupingType = new SportEventGroupingType(League.NFL, "doesn't matter", null);
        Ebean.save(sportEventGroupingType);

        sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 90, false, 2014, 1, 1);
        Ebean.save(sportEvent);

        sportEvent2 = new SportEvent(124, League.NFL, new Date(), "", "", 90, false, 2014, 1, 1);
        Ebean.save(sportEvent);

        sportEventGrouping = new SportEventGrouping(
                Arrays.asList(sportEvent, sportEvent2),
                sportEventGroupingType);
        Ebean.save(sportEventGrouping);

        user1 = new User();
        user1.setId(11L);
        Ebean.save(user1);
        Ebean.save(new UserWallet(user1));
        userDao.plusUsd(user1, STARTING_MONEY);

        user2 = new User();
        user2.setId(12L);
        Ebean.save(user2);
        Ebean.save(new UserWallet(user2));
        userDao.plusUsd(user2, STARTING_MONEY);

        user3 = new User();
        user3.setId(13L);
        Ebean.save(user3);
        Ebean.save(new UserWallet(user3));
        userDao.plusUsd(user3, STARTING_MONEY);

        user4 = new User();
        user4.setId(14L);
        Ebean.save(user4);
        Ebean.save(new UserWallet(user4));
        userDao.plusUsd(user4, STARTING_MONEY);

        user5 = new User();
        user5.setId(15L);
        Ebean.save(user5);
        Ebean.save(new UserWallet(user5));
        userDao.plusUsd(user5, STARTING_MONEY);

        user6 = new User();
        user6.setId(16L);
        Ebean.save(user6);
        Ebean.save(new UserWallet(user6));
        userDao.plusUsd(user6, STARTING_MONEY);

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

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("10.00"), "{\"passingYards\":100}", "[\"test1\"]");
        Ebean.save(athleteSportEventInfoBrady);
        athleteSportEventInfoGronk = new AthleteSportEventInfo(sportEvent, athleteGronk, new BigDecimal("12.00"), "{\"receivingYards\":100}", "[\"test2\"]");
        Ebean.save(athleteSportEventInfoGronk);
        athleteSportEventInfoFlacco = new AthleteSportEventInfo(sportEvent2, athleteJoeFlacco, new BigDecimal("12.00"), "{\"passingYards\":100}", "[\"test2\"]");
        Ebean.save(athleteSportEventInfoFlacco);

        AthleteSalary tomSalary = new AthleteSalary(athleteTomBrady, sportEventGrouping, 10000);
        AthleteSalary gronkSalary = new AthleteSalary(athleteGronk, sportEventGrouping, 10000);
        AthleteSalary flaccoSalary = new AthleteSalary(athleteJoeFlacco, sportEventGrouping, 10000);
        Ebean.save(Arrays.asList(tomSalary, gronkSalary, flaccoSalary));

        List<ContestPayout> contestPayouts = Arrays.asList(new ContestPayout(1, 1, 380));
        ContestTemplate createdFrom = null;
        contest = new Contest(contestType, urlId, league, CAPACITY,
                true, ENTRY_FEE, 1, 5000000, sportEventGrouping,
                contestPayouts, createdFrom);
        contest.proceedNext();
        Ebean.save(contest);

        int statProviderId = 100;
        defaultLineup = new ArrayList<>();
        for(LineupTemplate lineupTemplate: contestDao.findLineupTemplates(League.NFL)){
            for(int i = 0; i < lineupTemplate.getNumberOfAthletes(); i++){
                Team team = new Team(League.NFL, "", "", "", 200+statProviderId);
                Athlete athlete = new Athlete(statProviderId, "first"+statProviderId, "last"+statProviderId, team, ""+statProviderId);
                AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(i % 2 == 0 ? sportEvent : sportEvent2, athlete, new BigDecimal("10.00"), "{\"passingYards\":100}", "[\"test1\"]");
                AthleteSalary salary = new AthleteSalary(athlete, sportEventGrouping, 5000);
                Ebean.save(Arrays.asList(team, athlete, athleteSportEventInfo, salary));
                defaultLineup.add(new LineupSpot(athlete, lineupTemplate.getPosition(), athleteSportEventInfo));
                statProviderId++;
            }
        }

        Lineup lineup1 = new Lineup("", user1, League.NFL, sportEventGrouping);
        lineup1.setLineupSpots(defaultLineup);

        Lineup lineup2 = new Lineup("", user1, League.NFL, sportEventGrouping);
        lineup2.setLineupSpots(defaultLineup);

        Lineup lineup3 = new Lineup("", user1, League.NFL, sportEventGrouping);
        lineup3.setLineupSpots(defaultLineup);

        Lineup lineup4 = new Lineup("", user1, League.NFL, sportEventGrouping);
        lineup4.setLineupSpots(defaultLineup);

        Lineup lineup5 = new Lineup("", user1, League.NFL, sportEventGrouping);
        lineup5.setLineupSpots(defaultLineup);

        Lineup lineup6 = new Lineup("", user1, League.NFL, sportEventGrouping);
        lineup6.setLineupSpots(defaultLineup);

        contestDao.joinContest(user1, contest, lineup1);
        contestDao.joinContest(user2, contest, lineup2);
        contestDao.joinContest(user3, contest, lineup3);
        contestDao.joinContest(user4, contest, lineup4);
        contestDao.joinContest(user5, contest, lineup5);
        contestDao.joinContest(user6, contest, lineup6);

        assertEquals(STARTING_MONEY - ENTRY_FEE, userDao.getUserWallet(user1).getUsd());
        assertEquals(STARTING_MONEY - ENTRY_FEE, userDao.getUserWallet(user2).getUsd());
        assertEquals(STARTING_MONEY - ENTRY_FEE, userDao.getUserWallet(user3).getUsd());
        assertEquals(STARTING_MONEY - ENTRY_FEE, userDao.getUserWallet(user4).getUsd());
        assertEquals(STARTING_MONEY - ENTRY_FEE, userDao.getUserWallet(user5).getUsd());
        assertEquals(STARTING_MONEY - ENTRY_FEE, userDao.getUserWallet(user6).getUsd());
    }

    private final static int STARTING_MONEY = 1000;
    private final static int ENTRY_FEE = 200;
    private final static int CAPACITY = 10;

    @Test
    public void testCancelContest() {
        contestDao.cancelContest(contest);

        assertEquals(STARTING_MONEY, userDao.getUserWallet(user1).getUsd());
        assertEquals(STARTING_MONEY, userDao.getUserWallet(user2).getUsd());
        assertEquals(STARTING_MONEY, userDao.getUserWallet(user3).getUsd());
        assertEquals(STARTING_MONEY, userDao.getUserWallet(user4).getUsd());
        assertEquals(STARTING_MONEY, userDao.getUserWallet(user5).getUsd());
        assertEquals(STARTING_MONEY, userDao.getUserWallet(user6).getUsd());
    }

}
