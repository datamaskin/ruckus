package models;

import com.avaje.ebean.Ebean;
import dao.ContestDao;
import dao.DaoFactory;
import dao.IUserDao;
import models.contest.*;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.sports.SportEventGroupingType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dan on 4/10/14.
 */
public class AnonymousContestTest extends BaseTest {
    private ContestPayout payout;

    private List<SportEvent> sportEvents;

    private SportEvent s1;
    private SportEvent s2;

    private ContestGrouping grouping;

    private SportEventGrouping sportEventGrouping;
    private ContestDao contestDao;
    private IUserDao userDao;

    private int ENTRY_FEE = 500;

    @Before
    public void setUp() {
        contestDao = new ContestDao(new ContestIdGeneratorImpl());
        userDao = DaoFactory.getUserDao();

        payout = new ContestPayout(1, 1, (int)(ENTRY_FEE * 0.92));

        sportEvents = new ArrayList<>();
        s1 = new SportEvent(1, League.MLB, new Date(), "Yankees vs RedSox", "test", 9, false, 2014, -1, 1);
        Ebean.save(s1);
        s2 = new SportEvent(2, League.MLB, new Date(), "Braves vs Mariners", "test", 9, false, 2014, -1, 1);
        Ebean.save(s2);
        sportEvents.add(s1);
        sportEvents.add(s2);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        SportEventGroupingType type = new SportEventGroupingType(League.MLB, "", null);
        Ebean.save(type);
        sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);
    }

    @After
    public void tearDown() {
        payout = null;
        grouping = null;
    }

    @Test
    public void createAndRetrieveContest() {
        List<ContestPayout> payouts = new ArrayList<>();
        payouts.add(payout);

        Contest newContest = new Contest(ContestType.ANONYMOUS_H2H, "", League.MLB, 5, true, 1, 1, 5000000, sportEventGrouping, payouts, null);

        newContest.setContestState(ContestState.active);

        Ebean.save(newContest);
        List<Contest> contests = contestDao.findContests(ContestState.active);

    }

}
