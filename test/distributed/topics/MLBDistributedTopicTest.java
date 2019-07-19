package distributed.topics;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import dao.ContestDao;
import dao.IContestDao;
import dao.ISportsDao;
import distributed.DistributedTopic;
import distributed.topics.mlb.MLBDistributedTopic;
import models.contest.*;
import models.sports.*;
import models.user.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import stats.translator.IFantasyPointTranslator;
import stats.updateprocessor.IUpdateProcessor;
import utilities.BaseTest;
import utils.ContestIdGeneratorImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 6/20/14.
 * Modified by gislas on 8/8/14.
 */
public class MLBDistributedTopicTest extends BaseTest {
    private MLBDistributedTopic mlbDistributedTopic;
    private DistributedTopic fantasyPointTopic;

    private Athlete athlete;
    private Team team;
    private Team team2;
    private Sport sport;
    private League league;
    private LineupSpot lineupSpot;
    private Lineup lineup;
    private Entry entry;
    private User user;
    private Contest contest;
    private ContestType contestType;
    private SportEvent sportEvent;
    private ContestGrouping grouping;
    private ContestState contestState;
    private AthleteSportEventInfo athleteSportEventInfo;

    private ISportsDao sportsDao;
    private IContestDao contestDao;

    private List<Team> teams;

    private IFantasyPointTranslator translator;
    private IUpdateProcessor updateProcessor;

    private String mlbTopicLabel = GlobalConstants.TOPIC_REALTIME_PREFIX + GlobalConstants.SPORT_MLB + "_TEST";

    private String socketXMLRootName = GlobalConstants.STATS_INC_MLB_SOCKET_ROOT_NODE_NAME;

    @Before
    public void setUp() {
        startHazelcast();

        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//        translator = new MLBFantasyPointTranslator((ScoringRulesManager) context.getBean("ScoringRulesManager"));
        translator = context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);

        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        contestDao = new ContestDao(new ContestIdGeneratorImpl());

        updateProcessor = context.getBean("MLBStatsUpdateProcessor", IUpdateProcessor.class);

        mlbDistributedTopic = new MLBDistributedTopic(mlbTopicLabel, translator, updateProcessor, sportsDao, contestDao, GlobalConstants.STATS_INC_MLB_SOCKET_ROOT_NODE_NAME);

        // Set up Sport
        sport = new Sport(Sport.FOOTBALL.getName());
        Ebean.save(sport);

        // Set up League
        league = new League(sport, League.NFL.getName(), League.NFL.getAbbreviation(), League.NFL.getDisplayName(), true);
        Ebean.save(league);

        // Set up Team
        team = new Team(league, "Boston", "Red Sox", "BOS", 1);
        sportsDao.saveTeam(team);

        team2 = new Team(league, "New York", "Yankees", "NYY", 2);
        sportsDao.saveTeam(team2);

        teams = new ArrayList<>();
        teams.add(team);
        teams.add(team2);

        // Set up Athlete
        athlete = new Athlete(200060, "Angel", "Pagan", team, "1");
        athlete.setTeam(team);
        Ebean.save(athlete);

        // Set up AppUser
        user = new User();
        user.setEmail("dan.maclean@ruckusgaming.com");
        user.setFirstName("Dan");
        user.setLastName("MacLean");
        user.setPassword("test");
        user.setUserName("terrorsquid");
        Ebean.save(user);

        // Set up SportEvent
        sportEvent = new SportEvent(1284165, league, new Date(), "test", "test", 60, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        // Set up Contest Grouping
        grouping = new ContestGrouping(ContestGrouping.MLB_ALL.getName(), ContestGrouping.MLB_ALL.getLeague());
        Ebean.save(grouping);

        // Set up Contest
        List<SportEvent> sportEvents = new ArrayList<>();
        sportEvents.add(sportEvent);

        SportEventGroupingType type = new SportEventGroupingType(league, "", null);
        Ebean.save(type);
        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEvents, type);
        Ebean.save(Arrays.asList(type, sportEventGrouping));

        contest = new Contest(contestType, "212312", league, 2, true, 100, 1, 50000, sportEventGrouping, null, null);
        Ebean.save(contest);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete, new BigDecimal("0"), "{}", "[]");
        Ebean.save(athleteSportEventInfo);

        // Set up Lineup and LineupSpot
        lineupSpot = new LineupSpot(athlete, Position.BS_THIRD_BASE, athleteSportEventInfo);
        List<LineupSpot> lineupSpots = new ArrayList<>();
        lineupSpots.add(lineupSpot);
        lineup = new Lineup("My Lineup", user, league, contest.getSportEventGrouping());
        lineup.setLineupSpots(lineupSpots);
        Ebean.save(lineup);

        // Set up Entry
        entry = new Entry(user, contest, lineup);
        entry.setPoints(5);     // Our athlete starts with 5 points, so put them here, too.
        Ebean.save(entry);

        List<Entry> entries = new ArrayList<Entry>();
        entries.add(entry);
        lineup.setEntries(entries);
        Ebean.save(lineup);

        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_SINGLE_LABEL, league, GlobalConstants.SCORING_MLB_SINGLE_FACTOR));
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
        mlbDistributedTopic.stop();
        fantasyPointTopic.stop();

        athlete = null;
        team = null;
        team2 = null;
        sport = null;
        league = null;
        lineup = null;
        lineupSpot = null;
        entry = null;
        user = null;
        contest = null;
        contestType = null;
        sportEvent = null;
        grouping = null;
        contestState = null;

        teams = null;
    }

    @Test
    public void testOnMessage_Walk() {
        athleteSportEventInfo.setFantasyPoints(new BigDecimal("5.00"));
        Ebean.save(athleteSportEventInfo);

        String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<MLB-event>\n" +
                "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                "   <gamestate>\n" +
                "      <game status=\"In-Progress\" status-id=\"2\" reason=\"\" inning=\"10\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"10\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                "   </gamestate>\n" +
                "   <double-header double-header=\"false\"/>\n" +
                "   <double-header-game-number number=\"0\"/>\n" +
                "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                "   <league id=\"2\" league=\"NL\"/>\n" +
                "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                "            <event-details>\n" +
                "               <event sequence=\"22\" id=\"14\" code=\"106\" name=\"Walk\" balls=\"4\" strikes=\"2\" outs-bef=\"1\" outs-aft=\"1\" segment-number=\"1\" segment-division=\"Top\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"1\" vis-score=\"0\" home-score=\"0\" team-id=\"26\" team-global-id=\"250\" team-city=\"San Francisco\" team-name=\"Giants\" team-alias=\"SF\"/>\n" +
                "                  <batter id=\"6966\" global-id=\"168575\" first-name=\"Marco\" last-name=\"Scutaro\" batting-slot=\"2\" end-base=\"1\"/>\n" +
                "                  <pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\"/>\n" +
                "                  <pitch sequence=\"Strike,Foul,Ball,Ball,Ball,Foul,Foul,Foul,Ball\"/>\n" +
                "                  <description text=\"M.Scutaro walked on a full count.\"/>\n" +
                "            </event-details>\n" +
                "            \n" +
                "   <baseball-mlb-boxscore-home-team-stats>\n" +
                "     <baseball-mlb-boxscore-team-stats>\n" +
                "       <at-bats at-bats=\"36\"/>\n" +
                "       <runs runs=\"3\"/>\n" +
                "       <hits hits=\"9\"/>\n" +
                "       <runs-batted-in runs-batted-in=\"3\"/>\n" +
                "       <total-bases total-bases=\"10\"/>\n" +
                "       <walks walks=\"2\"/>\n" +
                "       <strike-outs strike-outs=\"6\"/>\n" +
                "       <runners-left-on-base runners=\"9\"/>\n" +
                "       <double-plays number=\"2\"/>\n" +
                "       <triple-plays number=\"0\"/>\n" +
                "       <double-plays>\n" +
                "         <fielders>\n" +
                "           <fielder pos=\"C\" id=\"8373\" global-id=\"225850\" first-name=\"A.J.\" last-name=\"Ellis\"/>\n" +
                "           <fielder pos=\"SS\" id=\"6793\" global-id=\"75296\" first-name=\"Nick\" last-name=\"Punto\"/>\n" +
                "         </fielders>\n" +
                "         <description description=\"A.Ellis to Punto\"/>\n" +
                "       </double-plays>\n" +
                "       <double-plays>\n" +
                "         <fielders>\n" +
                "           <fielder pos=\"3B\" id=\"6698\" global-id=\"8757\" first-name=\"Juan\" last-name=\"Uribe\"/>\n" +
                "           <fielder pos=\"1B\" id=\"7054\" global-id=\"184104\" first-name=\"Adrian\" last-name=\"Gonzalez\"/>\n" +
                "         </fielders>\n" +
                "         <description description=\"Uribe to Ad.Gonzalez\"/>\n" +
                "       </double-plays>\n" +
                "       <passed-balls>\n" +
                "         <name first-name=\"Tim\" last-name=\"Federowicz\"/>\n" +
                "         <player-code id=\"9075\" global-id=\"454959\" uniform=\"18\"/>\n" +
                "         <game-total number=\"1\"/>\n" +
                "       </passed-balls>\n" +
                "       <total-innings total=\"10\"/>\n" +
                "     </baseball-mlb-boxscore-team-stats>\n" +
                "   </baseball-mlb-boxscore-home-team-stats>\n" +
                "   <baseball-mlb-boxscore-visiting-team-stats>\n" +
                "     <baseball-mlb-boxscore-team-stats>\n" +
                "       <at-bats at-bats=\"35\"/>\n" +
                "       <runs runs=\"2\"/>\n" +
                "       <hits hits=\"9\"/>\n" +
                "       <runs-batted-in runs-batted-in=\"2\"/>\n" +
                "       <total-bases total-bases=\"13\"/>\n" +
                "       <walks walks=\"5\"/>\n" +
                "       <strike-outs strike-outs=\"7\"/>\n" +
                "       <runners-left-on-base runners=\"9\"/>\n" +
                "       <double-plays number=\"0\"/>\n" +
                "       <triple-plays number=\"0\"/>\n" +
                "       <errors>\n" +
                "         <name first-name=\"Matt\" last-name=\"Cain\"/>\n" +
                "         <player-code id=\"7495\" global-id=\"202716\" uniform=\"18\"/>\n" +
                "         <game-total number=\"1\"/>\n" +
                "         <season-total number=\"1\"/>\n" +
                "       </errors>\n" +
                "       <total-innings total=\"10\"/>\n" +
                "     </baseball-mlb-boxscore-team-stats>\n" +
                "   </baseball-mlb-boxscore-visiting-team-stats>\n" +
                "   <baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "   </baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "   <baseball-mlb-boxscore-visiting-team-batting-lineup>\n" +
                "     <baseball-mlb-boxscore-batting-lineup>\n" +
                "       <name first-name=\"Angel\" last-name=\"Pagan\"/>\n" +
                "       <player-code id=\"7717\" global-id=\"200060\" uniform=\"16\"/>\n" +
                "       <player-position position=\"CF\" id=\"8\"/>\n" +
                "       <batting-slot slot=\"1\"/>\n" +
                "       <at-bats at-bats=\"5\" season=\"233\"/>\n" +
                "       <runs runs=\"0\" season=\"37\"/>\n" +
                "       <hits hits=\"2\" season=\"66\"/>\n" +
                "       <doubles doubles=\"0\" season=\"13\"/>\n" +
                "       <triples triples=\"0\" season=\"2\"/>\n" +
                "       <home-runs home-runs=\"0\" season=\"3\"/>\n" +
                "       <stolen-bases stolen-bases=\"1\" season=\"9\"/>\n" +
                "       <caught-stealing caught-stealing=\"0\" season=\"4\"/>\n" +
                "       <runs-batted-in runs-batted-in=\"0\" season=\"25\"/>\n" +
                "       <total-bases total-bases=\"2\" season=\"92\"/>\n" +
                "       <walks walks=\"0\" season=\"19\"/>\n" +
                "       <strike-outs strike-outs=\"0\" season=\"30\"/>\n" +
                "       <left-on-base number=\"1\"/>\n" +
                "       <plate-appearances number=\"5\" season=\"254\"/>\n" +
                "       <hit-by-pitch number=\"0\" season=\"0\"/>\n" +
                "       <ground-into-double-play number=\"0\" season=\"1\"/>\n" +
                "       <sacrifice-flies number=\"0\" season=\"2\"/>\n" +
                "       <sacrifice-hits number=\"0\" season=\"0\"/>\n" +
                "       <batting-average average=\".283\"/>\n" +
                "       <on-base-percentage percentage=\".335\"/>\n" +
                "       <slugging-percentage percentage=\".395\"/>\n" +
                "       <started-game started-game=\"true\"/>\n" +
                "       <entered-game inning=\"1\"/>\n" +
                "       <slot-index number=\"1\"/>\n" +
                "       <picked-off number=\"0\" season=\"0\"/>\n" +
                "     </baseball-mlb-boxscore-batting-lineup>\n" +
                "   </baseball-mlb-boxscore-visiting-team-batting-lineup>\n" +
                "</MLB-event>\n";

        mlbDistributedTopic.process(statData);

        // Indicate that the update is done.
        mlbDistributedTopic.process("");

        AthleteSportEventInfo updatedASEI = sportsDao.findAthleteSportEventInfo(athlete, sportEvent);
        Entry updatedEntry = contestDao.findEntry(entry.getId());
        try {
            JSONObject statsJsonObject = new JSONObject(updatedASEI.getStats());
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_DOUBLE_LABEL).getString("amount").equals("0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_DOUBLE_LABEL).getString("fpp").equals("0.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_TRIPLE_LABEL).getString("amount").equals("0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_TRIPLE_LABEL).getString("fpp").equals("0.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_HOMERUN_LABEL).getString("amount").equals("0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_HOMERUN_LABEL).getString("fpp").equals("0.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_SINGLE_LABEL).getString("amount").equals("2"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_SINGLE_LABEL).getString("fpp").equals("6.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).getString("amount").equals("0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).getString("fpp").equals("0.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_RUN_LABEL).getString("amount").equals("0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_RUN_LABEL).getString("fpp").equals("0.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_WALK_LABEL).getString("amount").equals("0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_WALK_LABEL).getString("fpp").equals("0.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL).getString("amount").equals("0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL).getString("fpp").equals("0.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL).getString("amount").equals("1"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL).getString("fpp").equals("5.0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL).getString("amount").equals("0"));
            assertTrue(statsJsonObject.getJSONObject(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL).getString("fpp").equals("0.0"));
        } catch (JSONException e) {
            fail(e.getMessage());
        }
        assertTrue(updatedASEI.getTimeline().equals("[\"+6.00 - M.Scutaro walked on a full count.\"]"));
        assertTrue(updatedASEI.getFantasyPoints().doubleValue() == 11.0);
        assertTrue(updatedEntry.getPoints() == 11.0);
    }

    @Test
    public void testOnMessage_PitchOnly() {
        athleteSportEventInfo.setFantasyPoints(new BigDecimal("5.00"));
        Ebean.save(athleteSportEventInfo);

        String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<MLB-event>\n" +
                "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                "   <gamestate>\n" +
                "      <game status=\"Final\" status-id=\"2\" reason=\"\" inning=\"10\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"10\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                "   </gamestate>\n" +
                "   <double-header double-header=\"false\"/>\n" +
                "   <double-header-game-number number=\"0\"/>\n" +
                "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                "   <league id=\"2\" league=\"NL\"/>\n" +
                "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                "            <event-details>\n" +
                "               <event sequence=\"22\" id=\"9\" code=\"106\" name=\"Walk\" balls=\"4\" strikes=\"2\" outs-bef=\"1\" outs-aft=\"1\" segment-number=\"1\" segment-division=\"Top\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"1\" vis-score=\"0\" home-score=\"0\" team-id=\"26\" team-global-id=\"250\" team-city=\"San Francisco\" team-name=\"Giants\" team-alias=\"SF\">\n" +
                "                  <batter id=\"6966\" global-id=\"168575\" first-name=\"Marco\" last-name=\"Scutaro\" batting-slot=\"2\" end-base=\"1\"/>\n" +
                "                  <pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\"/>\n" +
                "                  <pitch sequence=\"Strike,Foul,Ball,Ball,Ball,Foul,Foul,Foul,Ball\"/>\n" +
                "                  <description text=\"M.Scutaro walked on a full count.\"/>\n" +
                "               </event>\n" +
                "            </event-details>\n" +
                "            \n" +
                "   <baseball-mlb-boxscore-home-team-stats>\n" +
                "     <baseball-mlb-boxscore-team-stats>\n" +
                "       <at-bats at-bats=\"36\"/>\n" +
                "       <runs runs=\"3\"/>\n" +
                "       <hits hits=\"9\"/>\n" +
                "       <runs-batted-in runs-batted-in=\"3\"/>\n" +
                "       <total-bases total-bases=\"10\"/>\n" +
                "       <walks walks=\"2\"/>\n" +
                "       <strike-outs strike-outs=\"6\"/>\n" +
                "       <runners-left-on-base runners=\"9\"/>\n" +
                "       <double-plays number=\"2\"/>\n" +
                "       <triple-plays number=\"0\"/>\n" +
                "       <double-plays>\n" +
                "         <fielders>\n" +
                "           <fielder pos=\"C\" id=\"8373\" global-id=\"225850\" first-name=\"A.J.\" last-name=\"Ellis\"/>\n" +
                "           <fielder pos=\"SS\" id=\"6793\" global-id=\"75296\" first-name=\"Nick\" last-name=\"Punto\"/>\n" +
                "         </fielders>\n" +
                "         <description description=\"A.Ellis to Punto\"/>\n" +
                "       </double-plays>\n" +
                "       <double-plays>\n" +
                "         <fielders>\n" +
                "           <fielder pos=\"3B\" id=\"6698\" global-id=\"8757\" first-name=\"Juan\" last-name=\"Uribe\"/>\n" +
                "           <fielder pos=\"1B\" id=\"7054\" global-id=\"184104\" first-name=\"Adrian\" last-name=\"Gonzalez\"/>\n" +
                "         </fielders>\n" +
                "         <description description=\"Uribe to Ad.Gonzalez\"/>\n" +
                "       </double-plays>\n" +
                "       <passed-balls>\n" +
                "         <name first-name=\"Tim\" last-name=\"Federowicz\"/>\n" +
                "         <player-code id=\"9075\" global-id=\"454959\" uniform=\"18\"/>\n" +
                "         <game-total number=\"1\"/>\n" +
                "       </passed-balls>\n" +
                "       <total-innings total=\"10\"/>\n" +
                "     </baseball-mlb-boxscore-team-stats>\n" +
                "   </baseball-mlb-boxscore-home-team-stats>\n" +
                "   <baseball-mlb-boxscore-visiting-team-stats>\n" +
                "     <baseball-mlb-boxscore-team-stats>\n" +
                "       <at-bats at-bats=\"35\"/>\n" +
                "       <runs runs=\"2\"/>\n" +
                "       <hits hits=\"9\"/>\n" +
                "       <runs-batted-in runs-batted-in=\"2\"/>\n" +
                "       <total-bases total-bases=\"13\"/>\n" +
                "       <walks walks=\"5\"/>\n" +
                "       <strike-outs strike-outs=\"7\"/>\n" +
                "       <runners-left-on-base runners=\"9\"/>\n" +
                "       <double-plays number=\"0\"/>\n" +
                "       <triple-plays number=\"0\"/>\n" +
                "       <errors>\n" +
                "         <name first-name=\"Matt\" last-name=\"Cain\"/>\n" +
                "         <player-code id=\"7495\" global-id=\"202716\" uniform=\"18\"/>\n" +
                "         <game-total number=\"1\"/>\n" +
                "         <season-total number=\"1\"/>\n" +
                "       </errors>\n" +
                "       <total-innings total=\"10\"/>\n" +
                "     </baseball-mlb-boxscore-team-stats>\n" +
                "   </baseball-mlb-boxscore-visiting-team-stats>\n" +
                "   <baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "   </baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "   <baseball-mlb-boxscore-visiting-team-batting-lineup>\n" +
                "     <baseball-mlb-boxscore-batting-lineup>\n" +
                "       <name first-name=\"Angel\" last-name=\"Pagan\"/>\n" +
                "       <player-code id=\"7717\" global-id=\"200060\" uniform=\"16\"/>\n" +
                "       <player-position position=\"CF\" id=\"8\"/>\n" +
                "       <batting-slot slot=\"1\"/>\n" +
                "       <at-bats at-bats=\"5\" season=\"233\"/>\n" +
                "       <runs runs=\"0\" season=\"37\"/>\n" +
                "       <hits hits=\"2\" season=\"66\"/>\n" +
                "       <doubles doubles=\"0\" season=\"13\"/>\n" +
                "       <triples triples=\"0\" season=\"2\"/>\n" +
                "       <home-runs home-runs=\"0\" season=\"3\"/>\n" +
                "       <stolen-bases stolen-bases=\"1\" season=\"9\"/>\n" +
                "       <caught-stealing caught-stealing=\"0\" season=\"4\"/>\n" +
                "       <runs-batted-in runs-batted-in=\"0\" season=\"25\"/>\n" +
                "       <total-bases total-bases=\"2\" season=\"92\"/>\n" +
                "       <walks walks=\"0\" season=\"19\"/>\n" +
                "       <strike-outs strike-outs=\"0\" season=\"30\"/>\n" +
                "       <left-on-base number=\"1\"/>\n" +
                "       <plate-appearances number=\"5\" season=\"254\"/>\n" +
                "       <hit-by-pitch number=\"0\" season=\"0\"/>\n" +
                "       <ground-into-double-play number=\"0\" season=\"1\"/>\n" +
                "       <sacrifice-flies number=\"0\" season=\"2\"/>\n" +
                "       <sacrifice-hits number=\"0\" season=\"0\"/>\n" +
                "       <batting-average average=\".283\"/>\n" +
                "       <on-base-percentage percentage=\".335\"/>\n" +
                "       <slugging-percentage percentage=\".395\"/>\n" +
                "       <started-game started-game=\"true\"/>\n" +
                "       <entered-game inning=\"1\"/>\n" +
                "       <slot-index number=\"1\"/>\n" +
                "       <picked-off number=\"0\" season=\"0\"/>\n" +
                "     </baseball-mlb-boxscore-batting-lineup>\n" +
                "   </baseball-mlb-boxscore-visiting-team-batting-lineup>\n" +
                "</MLB-event>\n";

        mlbDistributedTopic.process(statData);

        // Indicate that the update is done.
        mlbDistributedTopic.process("");

        AthleteSportEventInfo updatedASEI = sportsDao.findAthleteSportEventInfo(athlete, sportEvent);
        Entry updatedEntry = contestDao.findEntry(entry.getId());
        assertTrue(updatedASEI.getStats().equals("{}"));
        assertTrue(updatedASEI.getTimeline().equals("[]"));
        assertTrue(updatedASEI.getFantasyPoints().doubleValue() == 5.0);
        assertTrue(updatedEntry.getPoints() == 5.0);
    }
}
