package service.edge;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import dao.ISportsDao;
import dao.SportsDao;
import models.sports.*;
import models.stats.nfl.StatsNflAthleteByEvent;
import models.stats.nfl.StatsNflDefenseByEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stats.manager.nfl.DefenseVsPositionManager;
import utilities.BaseTest;
import utils.TimeService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by dmaclean on 8/12/14.
 */
public class DefenseVsPositionManagerTest extends BaseTest {

    private ISportsDao sportsDao;
    private DefenseVsPositionManager defenseVsPositionManager;

    Date startTime;
    Date startTimeLastYear;
    Date startTimeTwoYearsAgo;

    @Before
    public void setUp() {
        this.sportsDao = new SportsDao();

        this.defenseVsPositionManager = new DefenseVsPositionManager(new TimeService());

        startTime = Date.from(Instant.now());
        startTimeLastYear = Date.from(ZonedDateTime.now().withYear(startTime.getYear()-1).toInstant());
        startTimeTwoYearsAgo = Date.from(ZonedDateTime.now().withYear(startTime.getYear()-2).toInstant());
    }

    @After
    public void tearDown() {
        this.sportsDao = null;
    }

    @Test
    public void testCalculateDefenseVsPosition_Quarterbacks() {
        Date startTime = new Date();

        Team patriots = new Team(League.NFL, "", "Patriots", "NE", 111);
        sportsDao.saveTeam(patriots);

        Team ravens = new Team(League.NFL, "", "Ravens", "BAL", 112);
        sportsDao.saveTeam(ravens);

        Team dolphins = new Team(League.NFL, "", "Dolphins", "MIA", 113);
        sportsDao.saveTeam(dolphins);

        Athlete athletePatriots = new Athlete(111, "", "Patriots", patriots, "");
        sportsDao.saveAthlete(athletePatriots);

        Athlete athleteRavens = new Athlete(112, "", "Ravens", ravens, "");
        sportsDao.saveAthlete(athleteRavens);

        Athlete athleteDolphins = new Athlete(113, "", "Dolphins", dolphins, "");
        sportsDao.saveAthlete(athleteDolphins);

        Athlete athleteTomBrady = new Athlete(1, "Tom", "Brady", patriots, "");
        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        sportsDao.saveAthlete(athleteTomBrady);

        Athlete athleteJoeFlacco = new Athlete(2, "Joe", "Flacco", ravens, "");
        athleteTomBrady.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        sportsDao.saveAthlete(athleteJoeFlacco);

        SportEvent sportEvent = new SportEvent(123, League.NFL, startTime, "", "", 60, false, 2014, 1, 1);
        sportEvent.setTeams(Arrays.asList(patriots, ravens));
        sportsDao.saveSportEvent(sportEvent);

        SportEvent sportEvent2 = new SportEvent(124, League.NFL, startTime, "", "", 60, false, 2014, 1, 1);
        sportEvent.setTeams(Arrays.asList(dolphins, ravens));
        sportsDao.saveSportEvent(sportEvent2);

        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athleteTomBrady, BigDecimal.ZERO, "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);

        AthleteSportEventInfo athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent, athleteJoeFlacco, BigDecimal.ZERO, "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo2);

        StatsNflDefenseByEvent statsNflDefenseByEvent = new StatsNflDefenseByEvent();
        statsNflDefenseByEvent.setSportEvent(sportEvent);
        statsNflDefenseByEvent.setAthlete(athletePatriots);
        statsNflDefenseByEvent.setOpponent(ravens);
        statsNflDefenseByEvent.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflDefenseByEvent.setStartTime(startTimeLastYear);
        Ebean.save(statsNflDefenseByEvent);

        StatsNflDefenseByEvent statsNflDefenseByEvent2 = new StatsNflDefenseByEvent();
        statsNflDefenseByEvent2.setSportEvent(sportEvent);
        statsNflDefenseByEvent2.setAthlete(athleteRavens);
        statsNflDefenseByEvent2.setOpponent(patriots);
        statsNflDefenseByEvent2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflDefenseByEvent2.setStartTime(startTimeLastYear);
        Ebean.save(statsNflDefenseByEvent2);

        StatsNflDefenseByEvent statsNflDefenseByEvent3 = new StatsNflDefenseByEvent();
        statsNflDefenseByEvent3.setSportEvent(sportEvent2);
        statsNflDefenseByEvent3.setAthlete(athleteDolphins);
        statsNflDefenseByEvent3.setOpponent(ravens);
        statsNflDefenseByEvent3.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflDefenseByEvent3.setStartTime(startTimeLastYear);
        Ebean.save(statsNflDefenseByEvent3);

        StatsNflAthleteByEvent statsNflAthleteByEvent = new StatsNflAthleteByEvent();
        statsNflAthleteByEvent.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEvent.setTeam(patriots);
        statsNflAthleteByEvent.setOpponentId(ravens.getStatProviderId());
        statsNflAthleteByEvent.setAthlete(athleteTomBrady);
        statsNflAthleteByEvent.setSportEvent(sportEvent);
        statsNflAthleteByEvent.setStartTime(startTimeLastYear);
        statsNflAthleteByEvent.setPosition(Position.FB_QUARTERBACK.getAbbreviation());
        statsNflAthleteByEvent.setFppInThisEvent(new BigDecimal("10"));

        Ebean.save(statsNflAthleteByEvent);

        StatsNflAthleteByEvent statsNflAthleteByEvent2 = new StatsNflAthleteByEvent();
        statsNflAthleteByEvent2.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEvent2.setTeam(ravens);
        statsNflAthleteByEvent2.setOpponentId(patriots.getStatProviderId());
        statsNflAthleteByEvent2.setAthlete(athleteJoeFlacco);
        statsNflAthleteByEvent2.setSportEvent(sportEvent);
        statsNflAthleteByEvent2.setStartTime(startTimeTwoYearsAgo);
        statsNflAthleteByEvent2.setPosition(Position.FB_QUARTERBACK.getAbbreviation());
        statsNflAthleteByEvent2.setFppInThisEvent(new BigDecimal("8"));

        Ebean.save(statsNflAthleteByEvent2);

        StatsNflAthleteByEvent statsNflAthleteByEvent3 = new StatsNflAthleteByEvent();
        statsNflAthleteByEvent3.setEventTypeId(GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        statsNflAthleteByEvent3.setTeam(ravens);
        statsNflAthleteByEvent3.setOpponentId(dolphins.getStatProviderId());
        statsNflAthleteByEvent3.setAthlete(athleteJoeFlacco);
        statsNflAthleteByEvent3.setSportEvent(sportEvent2);
        statsNflAthleteByEvent3.setStartTime(startTimeTwoYearsAgo);
        statsNflAthleteByEvent3.setPosition(Position.FB_QUARTERBACK.getAbbreviation());
        statsNflAthleteByEvent3.setFppInThisEvent(new BigDecimal("2"));

        Ebean.save(statsNflAthleteByEvent2);

        Map<Integer, Map<Integer, Integer>> dvpCache = new HashMap<>();
        defenseVsPositionManager.setDvpCache(dvpCache);

        Map<Integer, Integer> positionRanks = defenseVsPositionManager.calculateDefenseVsPosition(startTime, Position.FB_QUARTERBACK);
        assertEquals(1, positionRanks.get(dolphins.getId()).intValue());
        assertEquals(2, positionRanks.get(patriots.getId()).intValue());
        assertEquals(3, positionRanks.get(ravens.getId()).intValue());
    }
}
