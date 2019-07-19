package models.contestlive;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ISportsDao;
import models.contest.*;
import models.sports.*;
import models.stats.predictive.StatsProjectionGraphData;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import utilities.BaseTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 7/3/14.
 */
public class StatsProjectionGraphDataTest extends BaseTest {

    AthleteSportEventInfo athleteSportEventInfoBrady;
    private StatsProjectionGraphData statsProjectionGraphData;
    private ObjectMapper mapper;
    private Athlete athleteTomBrady;
    private Team team;
    private Sport sport;
    private League league;
    private LineupSpot lineupSpot;
    private Lineup lineup;
    private Entry entry;
    private Position position;
    private User user;
    private Contest contest;
    private ContestType contestType;
    private SportEvent sportEvent;
    private ContestGrouping grouping;
    private ContestState contestState;
    private ContestPayout contestPayout;

    private ISportsDao sportsDao;

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");

        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        statsProjectionGraphData = new StatsProjectionGraphData();

        mapper = new ObjectMapper();

        // Set up Sport
        sport = new Sport(Sport.FOOTBALL.getName());
        Ebean.save(sport);

        // Set up League
        league = new League(sport, League.NFL.getName(), League.NFL.getAbbreviation(), League.NFL.getDisplayName(), true);
        Ebean.save(league);

        // Set up Team
        team = new Team(league, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        // Set up Athlete
        athleteTomBrady = new Athlete(1, "Tom", "Brady", team, "12");
        Ebean.save(athleteTomBrady);

        // Set up Position
        /*position = new Position(2, "Quarterback", "QB", sport);*/
        /*Ebean.save(position);*/

        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        // Set up ContestType
        contestType = new ContestType(1, ContestType.DOUBLE_UP.getName(), ContestType.DOUBLE_UP.getAbbr());
//        Ebean.save(contestType);

        // Set up SportEvent
        sportEvent = new SportEvent(1, league, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up Contest State
        /*contestState = new ContestStateActive();
        Ebean.save(contestState);*/

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

        SportEventGroupingType type = new SportEventGroupingType(league, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        contest = new Contest(contestType, "212312", league, 2, true, 100, 1, 50000, sportEventGrouping, contestPayouts, null);
        contest.setStartTime(cal.getTime());
        contest.setContestState(contestState);
        Ebean.save(contest);

        contest.setCurrentEntries(1);
        Ebean.save(contest);

        athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athleteTomBrady, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfoBrady);

        // Set up Lineup and LineupSpot
        lineupSpot = new LineupSpot(athleteTomBrady, position, athleteSportEventInfoBrady);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(lineupSpot);
        lineup = new Lineup("My Lineup", user, league, contest.getSportEventGrouping());
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
        statsProjectionGraphData = null;

        mapper = null;

        athleteTomBrady = null;
        team = null;
        sport = null;
        league = null;
        lineup = null;
        lineupSpot = null;
        entry = null;
        position = null;
        user = null;
        contest = null;
        contestType = null;
        sportEvent = null;
        grouping = null;
        contestState = null;

        athleteSportEventInfoBrady = null;
    }

    @Test
    public void testProcessPerformanceData() {
        List<BigDecimal> performanceData = new ArrayList<>();
        List<BigDecimal> projectedPerformanceData = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            if (i < 10) {
                performanceData.add(new BigDecimal(i));
            } else {
                projectedPerformanceData.add(new BigDecimal(i + 0.5));
            }
        }

        try {
            lineup.setPerformanceData(mapper.writeValueAsString(performanceData));
            lineup.setProjectedPerformanceData(mapper.writeValueAsString(projectedPerformanceData));
            statsProjectionGraphData = new StatsProjectionGraphData(contest, lineup);

            /*
             * Evaluate current performance data.
             */
            assertTrue(statsProjectionGraphData.getCurrentPerformanceData().size() == 10);

            int j = 0;
            for (List<BigDecimal> list : statsProjectionGraphData.getCurrentPerformanceData()) {
                assertTrue(list.get(0).compareTo(new BigDecimal(j)) == 0);
                assertTrue(list.get(1).compareTo(new BigDecimal(j)) == 0);

                j++;
            }

            /*
             * Evaluate projected performance data.
             */
            assertTrue(statsProjectionGraphData.getProjectedPerformanceData().size() == 10);

            for (List<BigDecimal> list : statsProjectionGraphData.getProjectedPerformanceData()) {
                assertTrue(list.get(0).compareTo(new BigDecimal(j)) == 0);
                assertTrue(list.get(1).compareTo(new BigDecimal(j + 0.5)) == 0);

                j++;
            }
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }
}
