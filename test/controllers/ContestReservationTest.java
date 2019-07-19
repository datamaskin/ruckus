package controllers;

import service.LineupService;
import com.avaje.ebean.Ebean;
import dao.ContestDao;
import dao.ISportsDao;
import dao.SportsDao;
import models.contest.*;
import models.sports.*;
import models.user.User;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Created by mwalsh on 7/2/14.
 * Modified by gislas on 8/7/14. (Should not need MLB to pass test for ContestReservation)
 */
public class ContestReservationTest extends BaseTest {

    private ContestDao dao;
    private ContestType contestType;
    private League league;
    private Sport sport;
    private SportEvent sportEvent;
    private List<Team> teams;
    private List<SportEvent> sportEvents;
    private List<ContestPayout> contestPayouts;
    private ContestGrouping grouping;
    private ContestState contestState;
    private SportEventGrouping sportEventGrouping;

    @Before
    public void setup() {
        ISportsDao sportsDao = new SportsDao();
        dao = new ContestDao(new ContestIdGeneratorImpl());
        LineupService lineupManager = context.getBean("LineupManager", LineupService.class);

        teams = new ArrayList<>();
        Team team = new Team(league, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);
        teams.add(team);

        Team team2 = new Team(league, "Baltimore", "Ravens", "BAL", 2);
        sportsDao.saveTeam(team2);
        teams.add(team2);

        sportEvent = new SportEvent(1, league, new Date(), "test", "test", 60, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        contestPayouts = new ArrayList<>();
        ContestPayout contestPayout = new ContestPayout(1, 1, 10000);
        contestPayouts.add(contestPayout);

        SportEventGroupingType type = new SportEventGroupingType(league, "NFL ALL", null);
        sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));
    }

    @Test
    public void testContestReservation() {
        Contest contest = new Contest(contestType, "212312", league, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        Ebean.save(contest);

        User user = new User();
        Ebean.save(user);
        dao.reserveEntries(user, contest, 5);

        User user2 = new User();
        Ebean.save(user2);
        dao.reserveEntries(user2, contest, 3);

        List<Entry> entries1 = dao.findEntries(user, contest);
        assertEquals(5, entries1.size());

        List<Entry> entries2 = dao.findEntries(user2, contest);
        assertEquals(3, entries2.size());
    }


    @Test
    public void testContestEntryDelete() {
        Contest contest = new Contest(contestType, "212312", league, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        Ebean.save(contest);

        User user = new User();
        Ebean.save(user);
        dao.reserveEntries(user, contest, 5);

        List<Entry> entries1 = dao.findEntries(user, contest);
        assertEquals(5, entries1.size());

        Entry entry = entries1.get(0);
        dao.deleteEntry(entry);

        List<Entry> entries2 = dao.findEntries(user, contest);
        assertEquals(4, entries2.size());
    }


}
