package models;

import service.ScoringRulesService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ISportsDao;
import dao.SportsDao;
import models.contest.*;
import models.sports.*;
import models.stats.mlb.StatsMlbBatting;
import models.stats.mlb.StatsMlbPitching;
import models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.data.validation.Validation;
import renameme.FileStatsRetriever;
import stats.parser.mlb.BattingParser;
import stats.parser.mlb.PitchingParser;
import stats.translator.IFantasyPointTranslator;
import stats.translator.mlb.FantasyPointTranslator;
import utilities.BaseTest;
import utils.ITimeService;
import utils.TimeService;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by dan on 6/10/14.
 */
public class AthleteSportEventInfoTest extends BaseTest {

    private Athlete athlete;
    private SportEvent sportEvent;
    private AthleteSportEventInfo athleteSportEventInfo;

    private Team team;
    private Team team2;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");

    private ISportsDao sportsDao;

    private ITimeService timeService = new TimeService();

    @Before
    public void setUp() {
        sportsDao = context.getBean("sportsDao", ISportsDao.class);

        team = new Team(League.NFL, "New England", "Patriots", "NE", 1);
        sportsDao.saveTeam(team);

        team2 = new Team(League.NFL, "Baltimore", "Ravens", "BAL", 2);
        sportsDao.saveTeam(team2);

        athlete = new Athlete(1, "Tom", "Brady", team, "12");
        Ebean.save(athlete);

        sportEvent = new SportEvent(111, League.NFL, new Date(), "test", "test", 60, false, 0, 0, 1);
        sportEvent.setTeams(Arrays.asList(team, team2));
        Ebean.save(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo();

        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_SINGLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_SINGLE_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_DOUBLE_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_TRIPLE_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HOMERUN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_BATTED_IN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WALK_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HIT_BY_PITCH_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STOLEN_BASE_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL, League.MLB, GlobalConstants.SCORING_MLB_CAUGHT_STEALING_FACTOR));

        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, League.MLB, GlobalConstants.SCORING_MLB_INNING_PITCHED_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STRIKEOUT_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_WIN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WIN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_EARNED_RUN_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_WALK_FACTOR));
        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_FACTOR));
    }

    @After
    public void tearDown() {
        athleteSportEventInfo = null;
        sportEvent = null;
        athlete = null;
    }

    @Test
    public void createAndRetrieveAthleteSportEventInfo() {
        athleteSportEventInfo.setAthlete(athlete);
        athleteSportEventInfo.setSportEvent(sportEvent);
        athleteSportEventInfo.setFantasyPoints(new BigDecimal(10));

        /*
         * Test find with sport event
         */
        List<AthleteSportEventInfo> athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(sportEvent);
        assertTrue(athleteSportEventInfoList.isEmpty());
        Ebean.save(athleteSportEventInfo);

        athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(sportEvent);

        assertTrue(athleteSportEventInfoList.size() == 1);

//        assertTrue(athleteSportEventInfoList.get(0).athlete.equals(athlete));
        assertTrue(athleteSportEventInfoList.get(0).getSportEvent().equals(sportEvent));
        assertTrue(athleteSportEventInfoList.get(0).getFantasyPoints().intValue() == 10);

        /*
         * Test find with athlete
         */
        athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(athlete);
        assertTrue(athleteSportEventInfoList.size() == 1);

//        assertTrue(athleteSportEventInfoList.get(0).athlete.equals(athlete));
        assertTrue(athleteSportEventInfoList.get(0).getSportEvent().equals(sportEvent));
        assertTrue(athleteSportEventInfoList.get(0).getFantasyPoints().intValue() == 10);

        /*
         * Test find with both
         */
        athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(athlete);

        Ebean.save(athleteSportEventInfo);

        AthleteSportEventInfo instance = sportsDao.findAthleteSportEventInfo(athlete, sportEvent);
        assertTrue(instance != null);

//        assertTrue(instance.athlete.equals(athlete));
        assertTrue(instance.getSportEvent().equals(sportEvent));
        assertTrue(instance.getFantasyPoints().intValue() == 10);

        /*
         * Test find with Athlete and date.
         */
//        instance = sportsDao.findAthleteSportEventInfo(athlete, new Date());
//        assertTrue(instance != null);

//        assertTrue(instance.athlete.equals(athlete));
//        assertTrue(instance.getSportEvent().equals(sportEvent));
//        assertTrue(instance.getFantasyPoints().intValue() == 10);
    }

    @Test
    public void testValidations() {
        athleteSportEventInfo.setAthlete(athlete);
        athleteSportEventInfo.setSportEvent(sportEvent);
        athleteSportEventInfo.setFantasyPoints(new BigDecimal(10));

        Collection<ConstraintViolation<AthleteSportEventInfo>> errors = Validation.getValidator().validate(athleteSportEventInfo);
        assertTrue(errors.isEmpty());

        /*
         * Test SportEvent requiredness
         */
        athleteSportEventInfo.setSportEvent(null);
        errors = Validation.getValidator().validate(athleteSportEventInfo);
        assertTrue(errors.size() == 1);
        athleteSportEventInfo.setSportEvent(sportEvent);

        /*
         * Test athlete requiredness
         */
        athleteSportEventInfo.setAthlete(null);
        errors = Validation.getValidator().validate(athleteSportEventInfo);
        assertTrue(errors.size() == 1);
        athleteSportEventInfo.setAthlete(athlete);
    }

    @Test
    public void testFindByContest() {
        // Set up Athlete
        Athlete athleteJulianEdelman = new Athlete(3, "Julian", "Edelman", team, "80");
        Ebean.save(athleteJulianEdelman);

        Athlete athleteFlacco = new Athlete(2, "Joe", "Flacco", team2, "7");
        Ebean.save(athleteFlacco);

        // Set up AppUser
        User user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        SportEvent sportEvent2 = new SportEvent(2, League.NFL, new Date(), "test2", "test2", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent2);

        // Set up Contest Grouping
        ContestGrouping grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        Contest contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, null, null);
        Ebean.save(contest);

        AthleteSportEventInfo athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athlete, new BigDecimal("0.00"), "{}", "[]");
        AthleteSportEventInfo athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteJulianEdelman, new BigDecimal("0.00"), "{}", "[]");
        AthleteSportEventInfo athleteSportEventInfoFlacco = new AthleteSportEventInfo(sportEvent2, athleteFlacco, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfoBrady);
        Ebean.save(athleteSportEventInfoEdelman);
        Ebean.save(athleteSportEventInfoFlacco);

        // Set up Lineup and LineupSpot
        LineupSpot lineupSpot = new LineupSpot(athleteJulianEdelman, Position.FB_WIDE_RECEIVER, athleteSportEventInfoEdelman);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(lineupSpot);
        Lineup lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        // Set up Entry
        Entry entry = new Entry(user, contest, lineup);
        Ebean.save(entry);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);


        List<AthleteSportEventInfo> athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(contest);
        assertTrue(athleteSportEventInfoList.size() == 2);
        assertTrue(athleteSportEventInfoList.get(0).getId() == athlete.getId());
        assertTrue(athleteSportEventInfoList.get(1).getId() == athleteJulianEdelman.getId());
    }

    @Test
    public void testFindByContestAndPosition() {
        // Set up Athlete
        Athlete athleteJulianEdelman = new Athlete(3, "Julian", "Edelman", team, "80");
        athleteJulianEdelman.setPositions(Arrays.asList(Position.FB_WIDE_RECEIVER));
        Ebean.save(athleteJulianEdelman);

        Athlete athleteFlacco = new Athlete(2, "Joe", "Flacco", team2, "7");
        athleteFlacco.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        Ebean.save(athleteFlacco);

        // Set up AppUser
        User user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        SportEvent sportEvent2 = new SportEvent(2, League.NFL, new Date(), "test2", "test2", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent2);

        // Set up Contest Grouping
        ContestGrouping grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);
        sportEvents.add(sportEvent2);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        Contest contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, null, null);
        Ebean.save(contest);

        AthleteSportEventInfo athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athlete, new BigDecimal("2.00"), "{}", "[]");
        AthleteSportEventInfo athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteJulianEdelman, new BigDecimal("0.00"), "{}", "[]");
        AthleteSportEventInfo athleteSportEventInfoFlacco = new AthleteSportEventInfo(sportEvent2, athleteFlacco, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfoBrady);
        Ebean.save(athleteSportEventInfoEdelman);
        Ebean.save(athleteSportEventInfoFlacco);

        // Set up Lineup and LineupSpot
        LineupSpot lineupSpot = new LineupSpot(athleteJulianEdelman, Position.FB_WIDE_RECEIVER, athleteSportEventInfoEdelman);
        LineupSpot lineupSpot2 = new LineupSpot(athleteFlacco, Position.FB_QUARTERBACK, athleteSportEventInfoFlacco);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(lineupSpot);
        lineupSpots.add(lineupSpot2);
        Lineup lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        List<LineupSpot> lineupSpots2 = new ArrayList<>();
        lineupSpots2.add(new LineupSpot(athleteJulianEdelman, Position.FB_WIDE_RECEIVER, athleteSportEventInfoEdelman));
        lineupSpots2.add(new LineupSpot(athleteFlacco, Position.FB_QUARTERBACK, athleteSportEventInfoFlacco));
        lineupSpots2.add(new LineupSpot(athlete, Position.FB_QUARTERBACK, athleteSportEventInfoBrady));
        Lineup lineup2 = new Lineup("My Lineup 2", user, League.NFL, contest.getSportEventGrouping());
        lineup2.setLineupSpots(lineupSpots2);
        Ebean.save(lineup2);

        // Set up Entry
        Entry entry = new Entry(user, contest, lineup);
        Ebean.save(entry);

        Entry entry2 = new Entry(user, contest, lineup2);
        Ebean.save(entry2);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(entry2);
        lineup2.setEntries(entries2);
        Ebean.save(lineup2);

        List<AthleteSportEventInfo> athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(contest, Position.FB_QUARTERBACK, null);
        assertTrue(athleteSportEventInfoList.size() == 2);
        assertTrue(athleteSportEventInfoList.get(0).getId() == athlete.getId());
        assertTrue(athleteSportEventInfoList.get(1).getId() == athleteFlacco.getId());

        List<Lineup> lineups = new ArrayList<>();
        lineups.add(lineup);
        athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(contest, Position.FB_QUARTERBACK, lineups);
        assertTrue(athleteSportEventInfoList.size() == 1);
        assertTrue(athleteSportEventInfoList.get(0).getId() == athleteFlacco.getId());

    }

    @Test
    public void testFindByLineup() {
        // Set up Athlete
        Athlete athleteJulianEdelman = new Athlete(3, "Julian", "Edelman", team, "80");
        Ebean.save(athleteJulianEdelman);

        Athlete athleteFlacco = new Athlete(2, "Joe", "Flacco", team2, "7");
        Ebean.save(athleteFlacco);

        // Set up AppUser
        User user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        // Set up ContestType
//        ContestType contestType = new ContestType(1, ContestType.DOUBLE_UP.getName(), ContestType.DOUBLE_UP.getAbbr());
//        Ebean.save(contestType);

        // Set up SportEvent
        sportEvent = new SportEvent(1, League.NFL, new Date(), "test", "test", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent);

        SportEvent sportEvent2 = new SportEvent(2, League.NFL, new Date(), "test2", "test2", 60, false, 2014, -1, 1);
        Ebean.save(sportEvent2);

        // Set up Contest Grouping
        ContestGrouping grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up Contest
        ArrayList<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(League.NFL, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(sportEventGrouping);

        Contest contest = new Contest(ContestType.DOUBLE_UP, "212312", League.NFL, 2, true, 100, 1, 50000, sportEventGrouping, null, null);
        Ebean.save(contest);

        AthleteSportEventInfo athleteSportEventInfoBrady = new AthleteSportEventInfo(sportEvent, athlete, new BigDecimal("0.00"), "{}", "[]");
        AthleteSportEventInfo athleteSportEventInfoEdelman = new AthleteSportEventInfo(sportEvent, athleteJulianEdelman, new BigDecimal("0.00"), "{}", "[]");
        AthleteSportEventInfo athleteSportEventInfoFlacco = new AthleteSportEventInfo(sportEvent2, athleteFlacco, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfoBrady);
        Ebean.save(athleteSportEventInfoEdelman);
        Ebean.save(athleteSportEventInfoFlacco);

        // Set up Lineup and LineupSpot
        LineupSpot lineupSpot = new LineupSpot(athleteJulianEdelman, Position.FB_QUARTERBACK, athleteSportEventInfoEdelman);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(lineupSpot);
        Lineup lineup = new Lineup("My Lineup", user, League.NFL, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        // Set up Entry
        Entry entry = new Entry(user, contest, lineup);
        Ebean.save(entry);

        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);

        List<AthleteSportEventInfo> athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(lineup);
        assertTrue(athleteSportEventInfoList.size() == 1);
        assertTrue(athleteSportEventInfoList.get(0).getId() == athleteJulianEdelman.getId());
    }

    @Test
    public void testGetStatAverages_MLB_Batter() {
        try {
            Team tigers = new Team(League.MLB, "Detroit", "Tigers", "DET", 230);
            sportsDao.saveTeam(tigers);

            Athlete miguelCabrera = new Athlete(213968, "Miguel", "Cabrera", tigers, "12");
            miguelCabrera.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
            Ebean.save(miguelCabrera);

            SportEvent sportEvent1 = new SportEvent(1378361, League.MLB, simpleDateFormat.parse("05/12/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent1);
            SportEvent sportEvent2 = new SportEvent(1419285, League.MLB, simpleDateFormat.parse("06/19/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent2);
            SportEvent sportEvent3 = new SportEvent(1380745, League.MLB, simpleDateFormat.parse("06/25/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent3);

            AthleteSportEventInfo athleteSportEventInfo1 = new AthleteSportEventInfo(sportEvent1, miguelCabrera, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfo1);

            AthleteSportEventInfo athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent2, miguelCabrera, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfo2);

            AthleteSportEventInfo athleteSportEventInfo3 = new AthleteSportEventInfo(sportEvent3, miguelCabrera, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfo3);

            String results = new FileStatsRetriever("test_files/mlb_batting_stats.json").getResults();
            BattingParser parserBatting = new BattingParser(new FantasyPointTranslator(new ScoringRulesService()));

            parserBatting.parse(results);

            ISportsDao dao = new SportsDao();
            Map<String, BigDecimal> averages = dao.calculateStatAverages(athleteSportEventInfo3, 2);

            assertTrue(averages.get(GlobalConstants.STATS_MLB_EXTRA_BASE_HITS).compareTo(new BigDecimal("0.50")) == 0);
            assertTrue(averages.get(GlobalConstants.STATS_MLB_AT_BATS).compareTo(new BigDecimal("3.00")) == 0);
            assertTrue(averages.get(GlobalConstants.STATS_MLB_ON_BASE_PLUS_SLUGGING).compareTo(new BigDecimal("1.09")) == 0);
            assertTrue(averages.get(GlobalConstants.STATS_MLB_RBIS).compareTo(new BigDecimal("1.00")) == 0);
            assertTrue(averages.get(GlobalConstants.STATS_MLB_STRIKEOUTS).compareTo(new BigDecimal("0.00")) == 0);
            assertTrue(averages.get(GlobalConstants.STATS_MLB_WALKS).compareTo(new BigDecimal("0.50")) == 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetStatAverages_MLB_Pitcher() throws Exception {
        Team giants = new Team(League.MLB, "San Francisco", "Giants", "SF", 230);
        sportsDao.saveTeam(giants);

        Athlete timLincecum = new Athlete(326472, "Tim", "Lincecum", giants, "12");
        timLincecum.setPositions(Arrays.asList(Position.BS_PITCHER));
        Ebean.save(timLincecum);

        SportEvent sportEvent1 = new SportEvent(1380308, League.MLB, simpleDateFormat.parse("06/13/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent1);
        SportEvent sportEvent2 = new SportEvent(1380320, League.MLB, simpleDateFormat.parse("06/25/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent2);
        SportEvent sportEvent3 = new SportEvent(1380341, League.MLB, simpleDateFormat.parse("07/01/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent3);

        AthleteSportEventInfo athleteSportEventInfo1 = new AthleteSportEventInfo(sportEvent1, timLincecum, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo1);

        AthleteSportEventInfo athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent2, timLincecum, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo2);

        AthleteSportEventInfo athleteSportEventInfo3 = new AthleteSportEventInfo(sportEvent3, timLincecum, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo3);

        String results = new FileStatsRetriever("test_files/mlb_pitching_stats.json").getResults();

        FantasyPointTranslator fantasyPointTranslator = new FantasyPointTranslator(new ScoringRulesService());
        PitchingParser parserPitching = new PitchingParser(fantasyPointTranslator);

        parserPitching.parse(results);

        ISportsDao dao = new SportsDao();
        Map<String, BigDecimal> averages = dao.calculateStatAverages(athleteSportEventInfo3, 2);
        assertTrue(averages.get(GlobalConstants.STATS_MLB_INNINGS_PITCHED).compareTo(new BigDecimal("7.50")) == 0);
        assertTrue(averages.get(GlobalConstants.STATS_MLB_WALKS).compareTo(new BigDecimal("1.50")) == 0);
        assertTrue(averages.get(GlobalConstants.STATS_MLB_STRIKEOUT_TO_WALK_RATIO).compareTo(new BigDecimal("4.50")) == 0);
        assertTrue(averages.get(GlobalConstants.STATS_MLB_OPP_BATTING_AVG).compareTo(new BigDecimal("0.08")) == 0);
        assertTrue(averages.get(GlobalConstants.STATS_MLB_OPPONENT_OBA).compareTo(new BigDecimal("0.13")) == 0);
    }

    @Test
    public void testCalculateFantasyPointsPerGame_MLB_Batter() throws Exception {
        Team tigers = new Team(League.MLB, "Detroit", "Tigers", "DET", 230);
        sportsDao.saveTeam(tigers);

        Athlete miguelCabrera = new Athlete(213968, "Miguel", "Cabrera", tigers, "12");
        miguelCabrera.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
        Ebean.save(miguelCabrera);

        SportEvent sportEvent1 = new SportEvent(1378361, League.MLB, simpleDateFormat.parse("05/12/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent1);
        SportEvent sportEvent2 = new SportEvent(1419285, League.MLB, simpleDateFormat.parse("06/19/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent2);
        SportEvent sportEvent3 = new SportEvent(1380745, League.MLB, simpleDateFormat.parse("06/25/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
        Ebean.save(sportEvent3);

        AthleteSportEventInfo athleteSportEventInfo1 = new AthleteSportEventInfo(sportEvent1, miguelCabrera, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo1);

        AthleteSportEventInfo athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent2, miguelCabrera, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo2);

        AthleteSportEventInfo athleteSportEventInfo3 = new AthleteSportEventInfo(sportEvent3, miguelCabrera, new BigDecimal(0), "", "");
        Ebean.save(athleteSportEventInfo3);

        String results = new FileStatsRetriever("test_files/mlb_batting_stats.json").getResults();
        BattingParser parserBatting = new BattingParser(new FantasyPointTranslator(new ScoringRulesService()));

        parserBatting.parse(results);
        List<StatsMlbBatting> statsBatting = Ebean.find(StatsMlbBatting.class).findList();
                List<StatsMlbBatting> removal = new ArrayList<>();
        for (StatsMlbBatting statsMlbBatting : statsBatting) {
            if (statsMlbBatting.getEventId() != 1419285 && statsMlbBatting.getEventId() != 1378361) {
                removal.add(statsMlbBatting);
            }
        }
        for (StatsMlbBatting statsMlbBatting : removal) {
            statsBatting.remove(statsMlbBatting);
        }
        Ebean.save(statsBatting);

        IFantasyPointTranslator translator = context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);

        BigDecimal result = sportsDao.calculateFantasyPointsPerGame(translator, timeService, athleteSportEventInfo2, 17);
        assertNotEquals(result.intValue(), 0);

    }

    @Test
    public void testCalculateFantasyPointsPerGame_MLB_Pitcher() {
        try {
            Team giants = new Team(League.MLB, "San Francisco", "Giants", "SF", 230);
            sportsDao.saveTeam(giants);

            Athlete timLincecum = new Athlete(326472, "Tim", "Lincecum", giants, "12");
            timLincecum.setPositions(Arrays.asList(Position.BS_PITCHER));
            Ebean.save(timLincecum);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            SportEvent sportEvent1 = new SportEvent(1380308, League.MLB, simpleDateFormat.parse("06/13/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent1);
            SportEvent sportEvent2 = new SportEvent(1380320, League.MLB, simpleDateFormat.parse("06/25/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent2);
            SportEvent sportEvent3 = new SportEvent(1380341, League.MLB, simpleDateFormat.parse("07/01/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent3);

            AthleteSportEventInfo athleteSportEventInfo1 = new AthleteSportEventInfo(sportEvent1, timLincecum, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfo1);

            AthleteSportEventInfo athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent2, timLincecum, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfo2);

            AthleteSportEventInfo athleteSportEventInfo3 = new AthleteSportEventInfo(sportEvent3, timLincecum, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfo3);

            String results = new FileStatsRetriever("test_files/mlb_pitching_stats.json").getResults();

            FantasyPointTranslator fantasyPointTranslator = new FantasyPointTranslator(new ScoringRulesService());
            PitchingParser parserPitching = new PitchingParser(fantasyPointTranslator);

            parserPitching.parse(results);
            List<StatsMlbPitching> statsPitching = Ebean.find(StatsMlbPitching.class).findList();
            List<StatsMlbPitching> removal = new ArrayList<>();
            for (StatsMlbPitching statsMlbPitching : statsPitching) {
                if (statsMlbPitching.getEventId() != 1380320 && statsMlbPitching.getEventId() != 1380308) {
                    removal.add(statsMlbPitching);
                }
            }
            for (StatsMlbPitching statsMlbPitching : removal) {
                statsPitching.remove(statsMlbPitching);
            }
            Ebean.save(statsPitching);

            IFantasyPointTranslator translator = context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);
            BigDecimal result = sportsDao.calculateFantasyPointsPerGame(translator, timeService, athleteSportEventInfo2, 17);
            assertNotEquals(new BigDecimal(0.0), result);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCalculateRank_MLB_Pitcher() {
        try {
            Team giants = new Team(League.MLB, "San Francisco", "Giants", "SF", 230);
            sportsDao.saveTeam(giants);

            Athlete timLincecum = new Athlete(326472, "Tim", "Lincecum", giants, "12");
            timLincecum.setPositions(Arrays.asList(Position.BS_PITCHER));
            Ebean.save(timLincecum);

            Athlete claytonKershaw = new Athlete(12345, "Clayton", "Kershaw", giants, "11");
            claytonKershaw.setPositions(Arrays.asList(Position.BS_PITCHER));
            Ebean.save(claytonKershaw);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            SportEvent sportEvent1 = new SportEvent(1380308, League.MLB, simpleDateFormat.parse("06/13/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent1);
            SportEvent sportEvent2 = new SportEvent(1380320, League.MLB, simpleDateFormat.parse("06/25/2013 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent2);

            AthleteSportEventInfo athleteSportEventInfoLincecum = new AthleteSportEventInfo(sportEvent1, timLincecum, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfoLincecum);

            AthleteSportEventInfo athleteSportEventInfoKershaw = new AthleteSportEventInfo(sportEvent1, claytonKershaw, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfoKershaw);

            /*
             * Set up stats for Kershaw.
             */
            StatsMlbPitching statsMlbPitchingKershaw = new StatsMlbPitching();
            statsMlbPitchingKershaw.setStatProviderId(12345);
            statsMlbPitchingKershaw.setEventId(1380308);
            statsMlbPitchingKershaw.setInningsPitched(9.0);  // 20.25
            statsMlbPitchingKershaw.setStrikeoutsTotal(9);   // 18
            statsMlbPitchingKershaw.setRunsAllowedEarnedRuns(2); // -4
            statsMlbPitchingKershaw.setHitsAllowedTotal(2);      // -1.2
            statsMlbPitchingKershaw.setWalksTotal(0);
            statsMlbPitchingKershaw.setHitBatsmen(0);
            Ebean.save(statsMlbPitchingKershaw);

            StatsMlbPitching statsMlbPitchingKershaw2 = new StatsMlbPitching();
            statsMlbPitchingKershaw2.setStatProviderId(12345);
            statsMlbPitchingKershaw2.setEventId(1380320);
            statsMlbPitchingKershaw2.setInningsPitched(1.0);
            statsMlbPitchingKershaw2.setStrikeoutsTotal(0);
            statsMlbPitchingKershaw2.setRunsAllowedEarnedRuns(12);
            statsMlbPitchingKershaw2.setHitsAllowedTotal(12);
            statsMlbPitchingKershaw2.setWalksTotal(5);
            statsMlbPitchingKershaw2.setHitBatsmen(0);
            Ebean.save(statsMlbPitchingKershaw2);

            /*
             * Set up stats for Lincecum.
             */
            String results = new FileStatsRetriever("test_files/mlb_pitching_stats.json").getResults();
            FantasyPointTranslator fantasyPointTranslator = new FantasyPointTranslator(new ScoringRulesService());
            PitchingParser parserPitching = new PitchingParser(fantasyPointTranslator);

            parserPitching.parse(results);
            List<StatsMlbPitching> statsPitching = Ebean.find(StatsMlbPitching.class).findList();
            List<StatsMlbPitching> removal = new ArrayList<>();
            for (StatsMlbPitching statsMlbPitching : statsPitching) {
                if (statsMlbPitching.getEventId() != 1380320 && statsMlbPitching.getEventId() != 1380308) {
                    removal.add(statsMlbPitching);
                }
            }
            for (StatsMlbPitching statsMlbPitching : removal) {
                statsPitching.remove(statsMlbPitching);
            }
            Ebean.save(statsPitching);

            IFantasyPointTranslator translator = context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);
            int[] result = sportsDao.calculateRank(Position.BS_PITCHER, translator, athleteSportEventInfoKershaw, 2014, League.MLB, 17);
            assertTrue(result.length == 2 && result[0] == 1 && result[1] == 2);

            result = sportsDao.calculateRank(Position.BS_PITCHER, translator, athleteSportEventInfoLincecum, 2014, League.MLB, 17);
            assertTrue(result.length == 2 && result[0] == 2 && result[1] == 2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCalculateRank_MLB_Batter() {
        try {
            Team tigers = new Team(League.MLB, "Detroit", "Tigers", "DET", 230);
            sportsDao.saveTeam(tigers);

            Athlete miguelCabrera = new Athlete(213968, "Miguel", "Cabrera", tigers, "12");
            miguelCabrera.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
            Ebean.save(miguelCabrera);

            Athlete albertPujols = new Athlete(12345, "Albert", "Pujols", tigers, "11");
            albertPujols.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
            Ebean.save(albertPujols);

            SportEvent sportEvent1 = new SportEvent(1378361, League.MLB, simpleDateFormat.parse("05/12/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent1);
            SportEvent sportEvent2 = new SportEvent(1419285, League.MLB, simpleDateFormat.parse("06/19/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent2);


            AthleteSportEventInfo athleteSportEventInfoCabrera = new AthleteSportEventInfo(sportEvent1, miguelCabrera, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfoCabrera);

            AthleteSportEventInfo athleteSportEventInfoPujols = new AthleteSportEventInfo(sportEvent1, albertPujols, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfoPujols);

            /*
             * Set up stats for Pujols.
             */
            StatsMlbBatting statsMlbBattingPujols = new StatsMlbBatting();
            statsMlbBattingPujols.setStatProviderId(12345);
            statsMlbBattingPujols.setEventId(1378361);
            statsMlbBattingPujols.setHitsSingles(2);
            statsMlbBattingPujols.setHitsDoubles(2);
            statsMlbBattingPujols.setHitsTriples(2);
            statsMlbBattingPujols.setHitsHomeRuns(2);
            statsMlbBattingPujols.setRunsBattedInTotal(2);
            statsMlbBattingPujols.setRunsScored(2);
            statsMlbBattingPujols.setWalksTotal(2);
            statsMlbBattingPujols.setHitByPitch(2);
            statsMlbBattingPujols.setStolenBasesTotal(2);
            statsMlbBattingPujols.setStolenBasesCaughtStealing(0);
            Ebean.save(statsMlbBattingPujols);

            StatsMlbBatting statsMlbBattingPujols2 = new StatsMlbBatting();
            statsMlbBattingPujols2.setStatProviderId(12345);
            statsMlbBattingPujols2.setEventId(1419285);
            statsMlbBattingPujols2.setHitsSingles(0);
            statsMlbBattingPujols2.setHitsDoubles(0);
            statsMlbBattingPujols2.setHitsTriples(0);
            statsMlbBattingPujols2.setHitsHomeRuns(0);
            statsMlbBattingPujols2.setRunsBattedInTotal(0);
            statsMlbBattingPujols2.setRunsScored(0);
            statsMlbBattingPujols2.setWalksTotal(0);
            statsMlbBattingPujols2.setHitByPitch(0);
            statsMlbBattingPujols2.setStolenBasesTotal(0);
            statsMlbBattingPujols2.setStolenBasesCaughtStealing(100);
            Ebean.save(statsMlbBattingPujols2);

            String results = new FileStatsRetriever("test_files/mlb_batting_stats.json").getResults();
            BattingParser parserBatting = new BattingParser(new FantasyPointTranslator(new ScoringRulesService()));

            parserBatting.parse(results);
            List<StatsMlbBatting> statsBatting = Ebean.find(StatsMlbBatting.class).findList();
            List<StatsMlbBatting> removal = new ArrayList<>();
            for (StatsMlbBatting statsMlbBatting : statsBatting) {
                if (statsMlbBatting.getEventId() != 1419285 && statsMlbBatting.getEventId() != 1378361) {
                    removal.add(statsMlbBatting);
                }
            }
            for (StatsMlbBatting statsMlbBatting : removal) {
                statsBatting.remove(statsMlbBatting);
            }
            Ebean.save(statsBatting);

            IFantasyPointTranslator translator = context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);
            int[] result = sportsDao.calculateRank(Position.BS_FIRST_BASE, translator, athleteSportEventInfoPujols, 2014, League.MLB, 17);
            assertTrue(result.length == 2 && result[0] == 1 && result[1] == 2);

            result = sportsDao.calculateRank(Position.BS_FIRST_BASE, translator, athleteSportEventInfoCabrera, 2014, League.MLB, 17);
            assertTrue(result.length == 2 && result[0] == 2 && result[1] == 2);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testCalculateDefenseVsPosition() {
        try {
            Team tigers = new Team(League.MLB, "Detroit", "Tigers", "DET", 230);
            sportsDao.saveTeam(tigers);

            Team redSox = new Team(League.MLB, "Boston", "Red Sox", "BOS", 231);
            sportsDao.saveTeam(redSox);

            List<Team> teams = new ArrayList<>();
            teams.add(tigers);
            teams.add(redSox);

            Athlete miguelCabrera = new Athlete(213968, "Miguel", "Cabrera", tigers, "12");
            miguelCabrera.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
            Ebean.save(miguelCabrera);

            Athlete albertPujols = new Athlete(12345, "Albert", "Pujols", tigers, "11");
            albertPujols.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
            Ebean.save(albertPujols);

            SportEvent sportEvent1 = new SportEvent(1378361, League.MLB, simpleDateFormat.parse("05/12/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent1);
            SportEvent sportEvent2 = new SportEvent(1419285, League.MLB, simpleDateFormat.parse("06/19/2014 19:05"), "desc", "shortDesc", 9, false, 2014, -1, 1);
            Ebean.save(sportEvent2);

            AthleteSportEventInfo athleteSportEventInfoCabrera = new AthleteSportEventInfo(sportEvent1, miguelCabrera, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfoCabrera);

            AthleteSportEventInfo athleteSportEventInfoPujols = new AthleteSportEventInfo(sportEvent1, albertPujols, new BigDecimal(0), "", "");
            Ebean.save(athleteSportEventInfoPujols);

            /*
             * Set up stats for Pujols.
             */
            StatsMlbBatting statsMlbBattingPujols = new StatsMlbBatting();
            statsMlbBattingPujols.setStatProviderId(12345);
            statsMlbBattingPujols.setEventId(1378361);
            statsMlbBattingPujols.setHitsSingles(2);
            statsMlbBattingPujols.setHitsDoubles(2);
            statsMlbBattingPujols.setHitsTriples(2);
            statsMlbBattingPujols.setHitsHomeRuns(2);
            statsMlbBattingPujols.setRunsBattedInTotal(2);
            statsMlbBattingPujols.setRunsScored(2);
            statsMlbBattingPujols.setWalksTotal(2);
            statsMlbBattingPujols.setHitByPitch(2);
            statsMlbBattingPujols.setStolenBasesTotal(2);
            statsMlbBattingPujols.setStolenBasesCaughtStealing(0);
            Ebean.save(statsMlbBattingPujols);

            StatsMlbBatting statsMlbBattingPujols2 = new StatsMlbBatting();
            statsMlbBattingPujols2.setStatProviderId(12345);
            statsMlbBattingPujols2.setEventId(1419285);
            statsMlbBattingPujols2.setHitsSingles(0);
            statsMlbBattingPujols2.setHitsDoubles(0);
            statsMlbBattingPujols2.setHitsTriples(0);
            statsMlbBattingPujols2.setHitsHomeRuns(0);
            statsMlbBattingPujols2.setRunsBattedInTotal(0);
            statsMlbBattingPujols2.setRunsScored(0);
            statsMlbBattingPujols2.setWalksTotal(0);
            statsMlbBattingPujols2.setHitByPitch(0);
            statsMlbBattingPujols2.setStolenBasesTotal(0);
            statsMlbBattingPujols2.setStolenBasesCaughtStealing(100);
            Ebean.save(statsMlbBattingPujols2);

            String results = new FileStatsRetriever("test_files/mlb_batting_stats.json").getResults();
            BattingParser parserBatting = new BattingParser(new FantasyPointTranslator(new ScoringRulesService()));

            parserBatting.parse(results);
            List<StatsMlbBatting> statsBatting = Ebean.find(StatsMlbBatting.class).findList();
            List<StatsMlbBatting> removal = new ArrayList<>();
            for (StatsMlbBatting statsMlbBatting : statsBatting) {
                if (statsMlbBatting.getEventId() != 1419285 && statsMlbBatting.getEventId() != 1378361) {
                    removal.add(statsMlbBatting);
                }
            }
            for (StatsMlbBatting statsMlbBatting : removal) {
                statsBatting.remove(statsMlbBatting);
            }
            Ebean.save(statsBatting);

            IFantasyPointTranslator translator = context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);
            int[] result = sportsDao.calculateRank(Position.BS_FIRST_BASE, translator, athleteSportEventInfoPujols, 2014, League.MLB, 17);
            assertTrue(result.length == 2 && result[0] == 1 && result[1] == 2);

            result = sportsDao.calculateRank(Position.BS_FIRST_BASE, translator, athleteSportEventInfoCabrera, 2014, League.MLB, 17);
            assertTrue(result.length == 2 && result[0] == 2 && result[1] == 2);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testDetermineStatsForDisplay_MLB_Batter() {
        AthleteSportEventInfo asei = new AthleteSportEventInfo();
        SportEvent sportEvent1 = new SportEvent(123, League.MLB, new Date(), "", "", 60, false, 2014, -1, 1);
        asei.setSportEvent(sportEvent1);
        asei.setStats(sportsDao.createInitialJsonForAthleteBoxscore(Position.BS_FIRST_BASE));

        String result = asei.determineStatsForDisplay();
        TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Map<String, Object>> resultList = mapper.readValue(result, listTypeReference);

            assertEquals(GlobalConstants.SCORING_MLB_SINGLE_LABEL, resultList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_SINGLE_ABBR, resultList.get(0).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, resultList.get(1).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_DOUBLE_ABBR, resultList.get(1).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, resultList.get(2).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_TRIPLE_ABBR, resultList.get(2).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, resultList.get(3).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_HOME_RUN_ABBR, resultList.get(3).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, resultList.get(4).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_RBI_ABBR, resultList.get(4).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_RUN_LABEL, resultList.get(5).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_RUN_ABBR, resultList.get(5).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_WALK_LABEL, resultList.get(6).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_WALK_ABBR, resultList.get(6).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, resultList.get(7).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_ABBR, resultList.get(7).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, resultList.get(8).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_STOLEN_BASE_ABBR, resultList.get(8).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL, resultList.get(9).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_ABBR, resultList.get(9).get("abbr"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDetermineStatsForDisplay_MLB_Pitcher() {
        AthleteSportEventInfo asei = new AthleteSportEventInfo();
        SportEvent sportEvent1 = new SportEvent(123, League.MLB, new Date(), "", "", 60, false, 2014, -1, 1);
        asei.setSportEvent(sportEvent1);
        asei.setStats(sportsDao.createInitialJsonForAthleteBoxscore(Position.BS_PITCHER));

        String result = asei.determineStatsForDisplay();
        TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Map<String, Object>> resultList = mapper.readValue(result, listTypeReference);

            assertEquals(GlobalConstants.SCORING_MLB_WIN_LABEL, resultList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_WIN_ABBR, resultList.get(0).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL, resultList.get(1).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_COMPLETE_GAME_ABBR, resultList.get(1).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, resultList.get(2).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_STRIKEOUT_ABBR, resultList.get(2).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, resultList.get(3).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_EARNED_RUN_ABBR, resultList.get(3).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, resultList.get(4).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_HITS_ALLOWED_ABBR, resultList.get(4).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, resultList.get(5).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_INNINGS_PITCHED_ABBR, resultList.get(5).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, resultList.get(6).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_WALK_ABBR, resultList.get(6).get("abbr"));
            assertEquals(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, resultList.get(7).get("name"));
            assertEquals(GlobalConstants.SCORING_MLB_HIT_BATSMEN_ABBR, resultList.get(7).get("abbr"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDetermineStatsForDisplay_NFL_Offensive() {
        Athlete athlete1 = new Athlete(12345, "Tom", "Brady", null, "12");
        athlete1.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        AthleteSportEventInfo asei = new AthleteSportEventInfo();
        SportEvent sportEvent1 = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
        asei.setSportEvent(sportEvent1);
        asei.setAthlete(athlete1);
        asei.setStats(sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK));

        try {
            TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};
            ObjectMapper mapper = new ObjectMapper();

            List<Map<String, Object>> stats = mapper.readValue(asei.getStats(), listTypeReference);
            stats.get(0).put("amount", 1);
            stats.get(0).put("fpp", new BigDecimal("6.00"));
            asei.setStats(mapper.writeValueAsString(stats));

            String result = asei.determineStatsForDisplay();

            List<Map<String, Object>> resultList = mapper.readValue(result, listTypeReference);

            assertEquals(1, resultList.size());
            assertEquals(GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, resultList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR, resultList.get(0).get("abbr"));
            assertEquals(1, resultList.get(0).get("amount"));
            assertEquals(6.0, resultList.get(0).get("fpp"));
//            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, resultList.get(1).get("name"));
//            assertEquals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_ABBR, resultList.get(1).get("abbr"));
//            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, resultList.get(2).get("name"));
//            assertEquals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_ABBR, resultList.get(2).get("abbr"));
//            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, resultList.get(3).get("name"));
//            assertEquals(GlobalConstants.SCORING_NFL_PASSING_YARDS_ABBR, resultList.get(3).get("abbr"));
//            assertEquals(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, resultList.get(4).get("name"));
//            assertEquals(GlobalConstants.SCORING_NFL_RECEPTIONS_ABBR, resultList.get(4).get("abbr"));
//            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, resultList.get(5).get("name"));
//            assertEquals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_ABBR, resultList.get(5).get("abbr"));
//            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, resultList.get(6).get("name"));
//            assertEquals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_ABBR, resultList.get(6).get("abbr"));
//            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, resultList.get(7).get("name"));
//            assertEquals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_ABBR, resultList.get(7).get("abbr"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDetermineStatsForDisplay_NFL_Defensive_PA_Sack() {
        Athlete athlete1 = new Athlete(12345, "Patriots", "Defense", null, "12");
        athlete1.setPositions(Arrays.asList(Position.FB_DEFENSE));
        AthleteSportEventInfo asei = new AthleteSportEventInfo();
        SportEvent sportEvent1 = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
        asei.setSportEvent(sportEvent1);
        asei.setAthlete(athlete1);
        asei.setStats("[{\"amount\":17,\"fpp\":3.50,\"name\":\"Points Allowed\",\"id\":\"NFL_26\",\"abbr\":\"PA\"}," +
                "{\"amount\":0,\"fpp\":0.00,\"name\":\"Interception Return TD\",\"id\":\"NFL_21\",\"abbr\":\"TDs\"}," +
                "{\"amount\":0,\"fpp\":0.00,\"name\":\"Fumble Recovery TD\",\"id\":\"NFL_22\",\"abbr\":\"TDs\"}," +
                "{\"amount\":0,\"fpp\":0.00,\"name\":\"Blocked Punt or FG Return TD\",\"id\":\"NFL_23\",\"abbr\":\"TDs\"}," +
                "{\"amount\":0,\"fpp\":0.00,\"name\":\"Safety\",\"id\":\"NFL_24\",\"abbr\":\"SAF\"}," +
                "{\"amount\":3,\"fpp\":6.00,\"name\":\"Fumble Recovery\",\"id\":\"NFL_20\",\"abbr\":\"FUM\"}," +
                "{\"amount\":1,\"fpp\":2.00,\"name\":\"Def. Interception\",\"id\":\"NFL_19\",\"abbr\":\"INT\"}," +
                "{\"amount\":0,\"fpp\":0.00,\"name\":\"Blocked Kick\",\"id\":\"NFL_25\",\"abbr\":\"BLK\"}," +
                "{\"amount\":3,\"fpp\":3.00,\"name\":\"Sack\",\"id\":\"NFL_18\",\"abbr\":\"SK\"}]");

        try {
            TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};
            ObjectMapper mapper = new ObjectMapper();

            String result = asei.determineStatsForDisplay();

            List<Map<String, Object>> resultList = mapper.readValue(result, listTypeReference);

            assertEquals(4, resultList.size());
            assertEquals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, resultList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_ABBR, resultList.get(0).get("abbr"));
            assertEquals(17, resultList.get(0).get("amount"));
            assertEquals(3.5, resultList.get(0).get("fpp"));
            assertEquals(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL, resultList.get(1).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_ABBR, resultList.get(1).get("abbr"));
            assertEquals(3, resultList.get(1).get("amount"));
            assertEquals(6.0, resultList.get(1).get("fpp"));
            assertEquals(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL, resultList.get(2).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_INTERCEPTION_ABBR, resultList.get(2).get("abbr"));
            assertEquals(1, resultList.get(2).get("amount"));
            assertEquals(2.0, resultList.get(2).get("fpp"));
            assertEquals(GlobalConstants.SCORING_NFL_SACK_LABEL, resultList.get(3).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_SACK_ABBR, resultList.get(3).get("abbr"));
            assertEquals(3, resultList.get(3).get("amount"));
            assertEquals(3.0, resultList.get(3).get("fpp"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDetermineStatsForDisplay_NFL_Defensive_NoFPs() {
        Athlete athlete1 = new Athlete(12345, "Patriots", "Defense", null, "12");
        athlete1.setPositions(Arrays.asList(Position.FB_DEFENSE));
        AthleteSportEventInfo asei = new AthleteSportEventInfo();
        SportEvent sportEvent1 = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
        asei.setSportEvent(sportEvent1);
        asei.setFantasyPoints(BigDecimal.ZERO);
        asei.setAthlete(athlete1);
        asei.setStats(sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE));

        try {
            TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};
            ObjectMapper mapper = new ObjectMapper();

            List<Map<String, Object>> stats = mapper.readValue(asei.getStats(), listTypeReference);
            stats.get(1).put("amount", 1);
            stats.get(1).put("fpp", new BigDecimal("6.00"));
            asei.setStats(mapper.writeValueAsString(stats));


            String result = asei.determineStatsForDisplay();

            List<Map<String, Object>> resultList = mapper.readValue(result, listTypeReference);

            assertEquals(1, resultList.size());
//            assertEquals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, resultList.get(0).get("name"));
//            assertEquals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_ABBR, resultList.get(0).get("abbr"));
//            assertEquals(0, resultList.get(0).get("amount"));
//            assertEquals(12, resultList.get(0).get("fpp"));
            assertEquals(GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, resultList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR, resultList.get(0).get("abbr"));
            assertEquals(1, resultList.get(0).get("amount"));
            assertEquals(6.0, resultList.get(0).get("fpp"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDetermineStatsForDisplay_NFL_Defensive_FPs() {
        Athlete athlete1 = new Athlete(12345, "Patriots", "Defense", null, "12");
        athlete1.setPositions(Arrays.asList(Position.FB_DEFENSE));
        AthleteSportEventInfo asei = new AthleteSportEventInfo();
        SportEvent sportEvent1 = new SportEvent(123, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
        asei.setSportEvent(sportEvent1);
        asei.setFantasyPoints(GlobalConstants.SCORING_NFL_DEFENSE_INITIAL_POINTS);
        asei.setAthlete(athlete1);
        asei.setStats(sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE));

        try {
            TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};
            ObjectMapper mapper = new ObjectMapper();

            List<Map<String, Object>> stats = mapper.readValue(asei.getStats(), listTypeReference);
            stats.get(1).put("amount", 1);
            stats.get(1).put("fpp", new BigDecimal("6.00"));
            asei.setStats(mapper.writeValueAsString(stats));


            String result = asei.determineStatsForDisplay();

            List<Map<String, Object>> resultList = mapper.readValue(result, listTypeReference);

            assertEquals(2, resultList.size());
            assertEquals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, resultList.get(0).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_ABBR, resultList.get(0).get("abbr"));
            assertEquals(0, resultList.get(0).get("amount"));
//            assertEquals(12, resultList.get(0).get("fpp"));
            assertEquals(GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, resultList.get(1).get("name"));
            assertEquals(GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR, resultList.get(1).get("abbr"));
            assertEquals(1, resultList.get(1).get("amount"));
            assertEquals(6.0, resultList.get(1).get("fpp"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDetermineMatchupDisplay_Home() {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> shortDescription = new HashMap<>();
        shortDescription.put("homeId", String.valueOf(team.getStatProviderId()));

        try {
            athleteSportEventInfo.setSportEvent(sportEvent);
            athleteSportEventInfo.setAthlete(athlete);
            sportEvent.setShortDescription(mapper.writeValueAsString(shortDescription));
            assertEquals("NEvBAL", athleteSportEventInfo.determineMatchupDisplay());
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDetermineMatchupDisplay_Away() {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> shortDescription = new HashMap<>();
        shortDescription.put("homeId", String.valueOf(team2.getStatProviderId()));

        try {
            athleteSportEventInfo.setSportEvent(sportEvent);
            athleteSportEventInfo.setAthlete(athlete);
            sportEvent.setShortDescription(mapper.writeValueAsString(shortDescription));
            assertEquals("NE@BAL", athleteSportEventInfo.determineMatchupDisplay());
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDetermineOpponent() {
        athleteSportEventInfo.setSportEvent(sportEvent);
        athleteSportEventInfo.setAthlete(athlete);
        assertEquals("BAL", athleteSportEventInfo.determineOpponent());
    }

    @Test
    public void testDetermineOpponent_OtherTeam() {
        Athlete athlete2 = new Athlete(1233424, "Joe", "Flacco", team2, "7");
        sportsDao.saveAthlete(athlete2);

        athleteSportEventInfo.setSportEvent(sportEvent);
        athleteSportEventInfo.setAthlete(athlete2);
        assertEquals("NE", athleteSportEventInfo.determineOpponent());
    }
}
