package distributed.tasks.lifecycle;

import com.avaje.ebean.Ebean;
import dao.ContestDao;
import dao.IContestDao;
import dao.IUserDao;
import dao.UserDao;
import models.contest.*;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.sports.SportEventGroupingType;
import models.user.User;
import models.wallet.UserWallet;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mwalsh on 7/25/14.
 */
public class ContestClosePayoutsTest extends BaseTest {

    private IContestDao contestDao;
    private IUserDao userDao;
    private ContestType contestType;
    private String urlId;
    private League league;
    private SportEventGrouping sportEventGrouping;
    private User _1user;
    private User _2user;
    private User _3user;
    private User _4user;
    private User _5user;
    private User _6user;
    private User _7user;
    private User _8user;
    private User _9user;
    private User _10user;

    @Before
    public void setup() {
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        userDao = new UserDao();
        contestType = ContestType.NORMAL;
        urlId = "something";
        league = League.NFL;

        SportEventGroupingType sportEventGroupingType = new SportEventGroupingType(League.NFL, "doesn't matter", null);
        Ebean.save(sportEventGroupingType);

        SportEvent sportEvent = new SportEvent(123, League.NFL, new Date(), "", "", 90, false, 2014, 1, 1);
        Ebean.save(sportEvent);

        sportEventGrouping = new SportEventGrouping(
                Arrays.asList(sportEvent),
                sportEventGroupingType);
        Ebean.save(sportEventGrouping);

        _1user = new User();
        _1user.setId(10L);
        Ebean.save(_1user);
        Ebean.save(new UserWallet(_1user));
        userDao.plusUsd(_1user, 500);

        _2user = new User();
        _2user.setId(20L);
        Ebean.save(_2user);
        Ebean.save(new UserWallet(_2user));
        userDao.plusUsd(_2user, 500);

        _3user = new User();
        _3user.setId(30L);
        Ebean.save(_3user);
        Ebean.save(new UserWallet(_3user));
        userDao.plusUsd(_3user, 500);

        _4user = new User();
        _4user.setId(40L);
        Ebean.save(_4user);
        Ebean.save(new UserWallet(_4user));
        userDao.plusUsd(_4user, 500);

        _5user = new User();
        _5user.setId(50L);
        Ebean.save(_5user);
        Ebean.save(new UserWallet(_5user));
        userDao.plusUsd(_5user, 500);

        _6user = new User();
        _6user.setId(60L);
        Ebean.save(_6user);
        Ebean.save(new UserWallet(_6user));
        userDao.plusUsd(_6user, 500);

        _7user = new User();
        _7user.setId(70L);
        Ebean.save(_7user);
        Ebean.save(new UserWallet(_7user));
        userDao.plusUsd(_7user, 500);

        _8user = new User();
        _8user.setId(80L);
        Ebean.save(_8user);
        Ebean.save(new UserWallet(_8user));
        userDao.plusUsd(_8user, 500);

        _9user = new User();
        _9user.setId(90L);
        Ebean.save(_9user);
        Ebean.save(new UserWallet(_9user));
        userDao.plusUsd(_9user, 500);

        _10user = new User();
        _10user.setId(100L);
        Ebean.save(_10user);
        Ebean.save(new UserWallet(_10user));
        userDao.plusUsd(_10user, 500);
    }

    @Test
    public void testHeadsUp_1_Winner() {
        List<ContestPayout> contestPayouts = Arrays.asList(new ContestPayout(1, 1, 380));
        ContestTemplate createdFrom = null;
        Contest contest = new Contest(contestType, urlId, league, 2,
                true, 200, 1, 5000000, sportEventGrouping,
                contestPayouts, createdFrom);
        contest.proceedNext();
        contest.proceedNext();
        contest.proceedNext();
        contest.proceedNext();
        Ebean.save(contest);

        Entry _1Entry = new Entry(_1user, contest, null);
        _1Entry.setPoints(101);

        Entry _2Entry = new Entry(_2user, contest, null);
        _2Entry.setPoints(100);

        Ebean.save(Arrays.asList(_1Entry, _2Entry));

        contestDao.closeContest(contest);

        UserWallet _1wallet = userDao.getUserWallet(_1user);
        UserWallet _2wallet = userDao.getUserWallet(_2user);

        assertEquals(500 + 380, _1wallet.getUsd());
        assertEquals(500, _2wallet.getUsd());
    }

    @Test
    public void testHeadsUp_Tie() {
        List<ContestPayout> contestPayouts = Arrays.asList(new ContestPayout(1, 1, 380));
        ContestTemplate createdFrom = null;
        Contest contest = new Contest(contestType, urlId, league, 2,
                true, 200, 1, 5000000, sportEventGrouping,
                contestPayouts, createdFrom);
        contest.proceedNext();
        contest.proceedNext();
        contest.proceedNext();
        contest.proceedNext();
        Ebean.save(contest);

        Entry _1Entry = new Entry(_1user, contest, null);
        _1Entry.setPoints(101.0);

        Entry _2Entry = new Entry(_2user, contest, null);
        _2Entry.setPoints(101.0);

        Ebean.save(Arrays.asList(_1Entry, _2Entry));

        contestDao.closeContest(contest);

        UserWallet _1wallet = userDao.getUserWallet(_1user);
        UserWallet _2wallet = userDao.getUserWallet(_2user);

        assertEquals(500+(380/2), _1wallet.getUsd());
        assertEquals(500+(380/2), _2wallet.getUsd());

    }

    @Test
    public void test6ManContest_NoTies() {
        List<ContestPayout> contestPayouts = Arrays.asList(
                new ContestPayout(1, 1, 1790),
                new ContestPayout(2, 2, 970)
        );
        ContestTemplate createdFrom = null;
        Contest contest = new Contest(contestType, urlId, league, 6,
                true, 500, 1, 5000000, sportEventGrouping,
                contestPayouts, createdFrom);
        contest.proceedNext();
        contest.proceedNext();
        contest.proceedNext();
        contest.proceedNext();
        Ebean.save(contest);

        Entry _1Entry = new Entry(_1user, contest, null);
        _1Entry.setPoints(101.0);

        Entry _2Entry = new Entry(_2user, contest, null);
        _2Entry.setPoints(100.0);

        Entry _3Entry = new Entry(_3user, contest, null);
        _3Entry.setPoints(99.0);

        Entry _4Entry = new Entry(_4user, contest, null);
        _4Entry.setPoints(98.0);

        Entry _5Entry = new Entry(_5user, contest, null);
        _5Entry.setPoints(97.0);

        Entry _6Entry = new Entry(_6user, contest, null);
        _6Entry.setPoints(96.0);

        Ebean.save(Arrays.asList(_1Entry, _2Entry, _3Entry, _4Entry, _5Entry, _6Entry));

        contestDao.closeContest(contest);

        UserWallet _1wallet = userDao.getUserWallet(_1user);
        UserWallet _2wallet = userDao.getUserWallet(_2user);
        UserWallet _3wallet = userDao.getUserWallet(_3user);
        UserWallet _4wallet = userDao.getUserWallet(_4user);
        UserWallet _5wallet = userDao.getUserWallet(_5user);
        UserWallet _6wallet = userDao.getUserWallet(_6user);

        assertEquals(500+1790, _1wallet.getUsd());
        assertEquals(500+970, _2wallet.getUsd());
        assertEquals(500, _3wallet.getUsd());
        assertEquals(500, _4wallet.getUsd());
        assertEquals(500, _5wallet.getUsd());
        assertEquals(500, _6wallet.getUsd());

    }

    @Test
    public void test10ManContest_LotsOfTies() {
        List<ContestPayout> contestPayouts = Arrays.asList(
                new ContestPayout(1, 5, 500),
                new ContestPayout(6, 8, 350),
                new ContestPayout(9, 10, 100)
        );
        ContestTemplate createdFrom = null;
        Contest contest = new Contest(contestType, urlId, league, 10,
                true, 500, 1, 5000000, sportEventGrouping,
                contestPayouts, createdFrom);
        contest.proceedNext();
        contest.proceedNext();
        contest.proceedNext();
        contest.proceedNext();
        Ebean.save(contest);

        Entry _1Entry = new Entry(_1user, contest, null);
        _1Entry.setPoints(101.0);

        Entry _2Entry = new Entry(_2user, contest, null);
        _2Entry.setPoints(100.0);

        Entry _3Entry = new Entry(_3user, contest, null);
        _3Entry.setPoints(99.0);

        Entry _4Entry = new Entry(_4user, contest, null);
        _4Entry.setPoints(98.0);

        Entry _5Entry = new Entry(_5user, contest, null);
        _5Entry.setPoints(97.0);

        Entry _6Entry = new Entry(_6user, contest, null);
        _6Entry.setPoints(97.0);

        Entry _7Entry = new Entry(_7user, contest, null);
        _7Entry.setPoints(97.0);

        Entry _8Entry = new Entry(_8user, contest, null);
        _8Entry.setPoints(97.0);

        Entry _9Entry = new Entry(_9user, contest, null);
        _9Entry.setPoints(97.0);

        Entry _10Entry = new Entry(_10user, contest, null);
        _10Entry.setPoints(96.0);

        Ebean.save(Arrays.asList(_1Entry, _2Entry, _3Entry, _4Entry, _5Entry, _6Entry, _7Entry, _8Entry, _9Entry, _10Entry));

        contestDao.closeContest(contest);

        UserWallet _1wallet = userDao.getUserWallet(_1user);
        UserWallet _2wallet = userDao.getUserWallet(_2user);
        UserWallet _3wallet = userDao.getUserWallet(_3user);
        UserWallet _4wallet = userDao.getUserWallet(_4user);
        UserWallet _5wallet = userDao.getUserWallet(_5user);
        UserWallet _6wallet = userDao.getUserWallet(_6user);
        UserWallet _7wallet = userDao.getUserWallet(_7user);
        UserWallet _8wallet = userDao.getUserWallet(_8user);
        UserWallet _9wallet = userDao.getUserWallet(_9user);
        UserWallet _10wallet = userDao.getUserWallet(_10user);

        assertEquals(500+500, _1wallet.getUsd());
        assertEquals(500+500, _2wallet.getUsd());
        assertEquals(500+500, _3wallet.getUsd());
        assertEquals(500+500, _4wallet.getUsd());
        assertEquals(500+330, _5wallet.getUsd());
        assertEquals(500+330, _6wallet.getUsd());
        assertEquals(500+330, _7wallet.getUsd());
        assertEquals(500+330, _8wallet.getUsd());
        assertEquals(500+330, _9wallet.getUsd());
        assertEquals(500+100, _10wallet.getUsd());

    }

}
