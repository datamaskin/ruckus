package stats.updateprocessor.mlb;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.IContestDao;
import dao.ISportsDao;
import models.sports.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import play.Logger;
import stats.updateprocessor.FantasyPointAthleteUpdateEvent;
import stats.updateprocessor.FantasyPointUpdateEvent;
import utilities.BaseTest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by dmaclean on 6/17/14.
 */
public class UpdateProcessorTest extends BaseTest {
//    private MLBFantasyPointTranslator translator;
    private UpdateProcessor updateProcessor;

    private ISportsDao sportsDao;
    private IContestDao contestDao;

    private Team team;
    private Team team2;
    private Athlete athlete1;
    private Athlete athlete2;
    private SportEvent sportEvent;
    private AthleteSportEventInfo athleteSportEventInfo;
    private AthleteSportEventInfo athleteSportEventInfo2;

    private ArrayList<Team> teams;

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static XPathFactory xpathFactory = XPathFactory.newInstance();

    @Before
    public void setUp() {
        ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        contestDao = context.getBean("contestDao", IContestDao.class);
//        translator = new MLBFantasyPointTranslator((ScoringRulesManager) context.getBean("ScoringRulesManager"));
        updateProcessor = context.getBean("MLBStatsUpdateProcessor", UpdateProcessor.class);

        team = new Team(League.MLB, "Boston", "Red Sox", "BOS", 1);
        sportsDao.saveTeam(team);

        team2 = new Team(League.MLB, "New York", "Yankees", "NYY", 2);
        sportsDao.saveTeam(team2);

        teams = new ArrayList<>();
        teams.add(team);
        teams.add(team2);

//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_SINGLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_SINGLE_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_DOUBLE_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_TRIPLE_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HOMERUN_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_BATTED_IN_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_RUN_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WALK_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_HIT_BY_PITCH_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STOLEN_BASE_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL, League.MLB, GlobalConstants.SCORING_MLB_CAUGHT_STEALING_FACTOR));
//
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, League.MLB, GlobalConstants.SCORING_MLB_INNING_PITCHED_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_STRIKEOUT_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_WIN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_WIN_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, League.MLB, GlobalConstants.SCORING_MLB_EARNED_RUN_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_WALK_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, League.MLB, GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_FACTOR));
//        Ebean.save(new ScoringRule(GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL, League.MLB, GlobalConstants.SCORING_MLB_COMPLETE_GAME_FACTOR));
    }

    @After
    public void tearDown() {
        updateProcessor = null;

        teams = null;
        team = null;
        athlete1 = null;
        athlete2 = null;
        sportEvent = null;
        athleteSportEventInfo = null;
        athleteSportEventInfo2 = null;
    }

    @Test
    public void testUpdateIndicators_FinalBoxScore() {
        team = new Team(League.MLB, "Los Angeles", "Dodgers", "LAD", 243);
        sportsDao.saveTeam(team);

        team2 = new Team(League.MLB, "San Francisco", "Giants", "SFG", 250);
        sportsDao.saveTeam(team2);

        athlete1 = new Athlete(184104, "Adrian", "Gonzalez", team, "1");
        athlete1.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
        sportsDao.saveAthlete(athlete1);
        athlete2 = new Athlete(202716, "Matt", "Cain", team2, "2");
        athlete2.setPositions(Arrays.asList(Position.BS_PITCHER));
        sportsDao.saveAthlete(athlete2);
        Athlete athleteHanleyRamirez = new Athlete(201879, "Hanley", "Ramirez", team, "2");
        athleteHanleyRamirez.setPositions(Arrays.asList(Position.BS_SHORT_STOP));
        sportsDao.saveAthlete(athleteHanleyRamirez);
        Athlete athleteCarlCrawford = new Athlete(12345, "Carl", "Crawford", team, "2");
        athleteCarlCrawford.setPositions(Arrays.asList(Position.BS_OUTFIELD));
        sportsDao.saveAthlete(athleteCarlCrawford);
        Athlete athleteBusterPosey = new Athlete(6789, "Buster", "Posey", team2, "2");
        athleteBusterPosey.setPositions(Arrays.asList(Position.BS_CATCHER));
        sportsDao.saveAthlete(athleteBusterPosey);

        sportEvent = new SportEvent(1284165, League.MLB, new Date(), "{}", "{}", 9, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        sportsDao.saveSportEvent(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete1, new BigDecimal("0.00"), "[]", "[]");
        athleteSportEventInfo.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
        athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent, athlete2, new BigDecimal("0.00"), "[]", "[]");
        athleteSportEventInfo2.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo2);
        AthleteSportEventInfo athleteSportEventInfoRamirez = new AthleteSportEventInfo(sportEvent, athleteHanleyRamirez, new BigDecimal("0.00"), "[]", "[]");
        athleteSportEventInfoRamirez.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRamirez);
        AthleteSportEventInfo athleteSportEventInfoCrawford = new AthleteSportEventInfo(sportEvent, athleteCarlCrawford, new BigDecimal("0.00"), "[]", "[]");
        athleteSportEventInfoCrawford.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoCrawford);
        AthleteSportEventInfo athleteSportEventInfoPosey = new AthleteSportEventInfo(sportEvent, athleteBusterPosey, new BigDecimal("0.00"), "[]", "[]");
        athleteSportEventInfoPosey.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoPosey);

        String data = "<MLB-event>\n" +
                "<gamecode code=\"330912119\" global-id=\"1284165\"/>\n" +
                " <gamestate>\n" +
                "  <game status=\"Final\" status-id=\"1\" reason=\"\" inning=\"1\" balls=\"0\" strikes=\"0\" outs=\"2\" segment-number=\"1\" segment-division=\"0\" active-state=\"true\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                "  <batter id=\"7054\" global-id=\"184104\" first-name=\"Adrian\" last-name=\"Gonzalez\" batting-slot=\"4\" hand=\"L\"/>\n" +
                "  <pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\"/>\n" +
                "  <runner id=\"7488\" global-id=\"201879\" first-name=\"Hanley\" last-name=\"Ramirez\" base=\"1\"/>\n" +
                " </gamestate>\n" +
                "  <home-team>\n" +
                "<team-name name=\"Dodgers\" alias=\"LAD\"/>\n" +
                "<team-city city=\"Los Angeles\"/>\n" +
                "<team-code id=\"19\" global-id=\"243\"/>\n" +
                "   <record wins=\"85\" losses=\"60\" ties=\"0\" pct=\".586\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\" wins=\"14\" losses=\"3\"/>\n" +
                "    <next-up-batter id=\"7710\" global-id=\"226249\" first-name=\"Andre\" last-name=\"Ethier\" hand=\"L\"/>\n" +
                "    <next-up-batter-two id=\"6698\" global-id=\"8757\" first-name=\"Juan\" last-name=\"Uribe\" hand=\"R\"/>\n" +
                "    <next-up-batter-three id=\"8373\" global-id=\"225850\" first-name=\"A.J.\" last-name=\"Ellis\" hand=\"R\"/>\n" +
                "    <due-up due=\"false\"/>\n" +
                "  </home-team>\n" +
                "  <home-score type=\"runs\" type-id=\"1\" number=\"1\"/>\n" +
                "  <home-score type=\"hits\" type-id=\"2\" number=\"1\"/>\n" +
                "  <home-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "  <visiting-team>\n" +
                "<team-name name=\"Giants\" alias=\"SF\"/>\n" +
                "<team-city city=\"San Francisco\"/>\n" +
                "<team-code id=\"26\" global-id=\"250\"/>\n" +
                "   <record wins=\"66\" losses=\"80\" ties=\"0\" pct=\".452\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\" wins=\"8\" losses=\"9\"/>\n" +
                "    <next-up-batter id=\"7963\" global-id=\"262071\" first-name=\"Hunter\" last-name=\"Pence\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"8326\" global-id=\"254551\" first-name=\"Pablo\" last-name=\"Sandoval\" hand=\"S\"/>\n" +
                "    <next-up-batter-three id=\"8945\" global-id=\"454517\" first-name=\"Brandon\" last-name=\"Crawford\" hand=\"L\"/>\n" +
                "    <due-up due=\"true\"/>\n" +
                "  </visiting-team>\n" +
                "  <visiting-score type=\"runs\" type-id=\"1\" number=\"0\"/>\n" +
                "  <visiting-score type=\"hits\" type-id=\"2\" number=\"0\"/>\n" +
                "  <visiting-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                " <event-details>\n" +
                "  <event sequence=\"25\" id=\"4\" code=\"1\" name=\"Single\" balls=\"0\" strikes=\"1\" outs-bef=\"2\" outs-aft=\"2\" rbi=\"0\" segment-number=\"1\" segment-division=\"Bottom\" dir=\"M\" dist=\"157\" bat-type=\"G\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"1\" vis-score=\"0\" home-score=\"0\" team-id=\"19\" team-global-id=\"0\" team-city=\"Los Angeles\" team-name=\"Dodgers\" team-alias=\"LAD\"/>\n" +
                "  <batter id=\"7488\" global-id=\"201879\" first-name=\"Hanley\" last-name=\"Ramirez\" batting-slot=\"3\" hand=\"R\" end-base=\"1\"/>\n" +
                "  <pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\"/>\n" +
                "  <pitch sequence=\"Foul,InPlay\"/>\n" +
                "  <description text=\"H.Ramirez singled to center.\"/>\n" +
                " </event-details>\n" +
                "</MLB-event>";

        try {
            InputSource source = new InputSource(new StringReader(data));

            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(source);

            FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
            fantasyPointUpdateEvent.setSportEvent(sportEvent);

            updateProcessor.updateIndicators(fantasyPointUpdateEvent, doc);

            for(Map.Entry<Integer, Integer> entry : fantasyPointUpdateEvent.getIndicators().entrySet()) {
                assertEquals(entry.getValue().intValue(), GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
            }

            int[] ids = {
                    athleteSportEventInfo.getId(),
                    athleteSportEventInfo2.getId(),
                    athleteSportEventInfoCrawford.getId(),
                    athleteSportEventInfoPosey.getId(),
                    athleteSportEventInfoRamirez.getId()
            };
            for(int i=0; i<ids.length; i++) {
                AthleteSportEventInfo athleteSportEventInfo1 = sportsDao.findAthleteSportEventInfo(ids[i]);
                assertEquals(athleteSportEventInfo1.getIndicator(), GlobalConstants.INDICATOR_TEAM_OFF_FIELD);
            }
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateTimeline_BatterOut() {

    }

    @Test
    public void testNoFantasyPointImpact() {
        Athlete angelPagan = new Athlete(200060, "Angel", "Pagan", team, "1");
        sportsDao.saveAthlete(angelPagan);

        Athlete zachGreinke = new Athlete(202781, "Zach", "Greinke", team, "2");
        sportsDao.saveAthlete(zachGreinke);

        Athlete mattCain = new Athlete(1234, "Matt", "Cain", team, "12");
        sportsDao.saveAthlete(mattCain);

        sportEvent = new SportEvent(1284165, League.MLB, new Date(), "test", "test", 9, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, angelPagan, new BigDecimal("0.00"), "{}", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);

        AthleteSportEventInfo athleteSportEventInfoZachGreinke = new AthleteSportEventInfo(sportEvent, zachGreinke, new BigDecimal("0.00"), "{}", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoZachGreinke);

        AthleteSportEventInfo athleteSportEventInfoMattCain = new AthleteSportEventInfo(sportEvent, mattCain, new BigDecimal("0.00"), "{}", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoMattCain);

        String data = "<MLB-event>\n" +
                "<gamecode code=\"330912119\" global-id=\"1284165\"/>\n" +
                " <gamestate>\n" +
                "  <game status=\"In-Progress\" status-id=\"1\" reason=\"\" inning=\"1\" balls=\"1\" strikes=\"0\" outs=\"0\" segment-number=\"1\" segment-division=\"1\" active-state=\"true\" restart=\"false\" pitch-sequence=\"Ball,\"/>\n" +
                "  <batter id=\"7717\" global-id=\"200060\" first-name=\"Angel\" last-name=\"Pagan\" batting-slot=\"1\" hand=\"S\"/>\n" +
                "  <pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\"/>\n" +
                " </gamestate>\n" +
                "  <home-team>\n" +
                "<team-name name=\"Dodgers\" alias=\"LAD\"/>\n" +
                "<team-city city=\"Los Angeles\"/>\n" +
                "<team-code id=\"19\" global-id=\"243\"/>\n" +
                "   <record wins=\"85\" losses=\"60\" ties=\"0\" pct=\".586\"/>\n" +
                "   <innings>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\" wins=\"14\" losses=\"3\"/>\n" +
                "    <next-up-batter id=\"9341\" global-id=\"659910\" first-name=\"Yasiel\" last-name=\"Puig\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"6870\" global-id=\"75597\" first-name=\"Carl\" last-name=\"Crawford\" hand=\"L\"/>\n" +
                "    <next-up-batter-three id=\"7488\" global-id=\"201879\" first-name=\"Hanley\" last-name=\"Ramirez\" hand=\"R\"/>\n" +
                "    <due-up due=\"true\"/>\n" +
                "  </home-team>\n" +
                "  <home-score type=\"runs\" type-id=\"1\" number=\"0\"/>\n" +
                "  <home-score type=\"hits\" type-id=\"2\" number=\"0\"/>\n" +
                "  <home-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "  <visiting-team>\n" +
                "<team-name name=\"Giants\" alias=\"SF\"/>\n" +
                "<team-city city=\"San Francisco\"/>\n" +
                "<team-code id=\"26\" global-id=\"250\"/>\n" +
                "   <record wins=\"66\" losses=\"80\" ties=\"0\" pct=\".452\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\" wins=\"8\" losses=\"9\"/>\n" +
                "    <next-up-batter id=\"6966\" global-id=\"168575\" first-name=\"Marco\" last-name=\"Scutaro\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"8795\" global-id=\"327177\" first-name=\"Brandon\" last-name=\"Belt\" hand=\"L\"/>\n" +
                "    <next-up-batter-three id=\"8578\" global-id=\"454353\" first-name=\"Buster\" last-name=\"Posey\" hand=\"R\"/>\n" +
                "    <due-up due=\"false\"/>\n" +
                "  </visiting-team>\n" +
                "  <visiting-score type=\"runs\" type-id=\"1\" number=\"0\"/>\n" +
                "  <visiting-score type=\"hits\" type-id=\"2\" number=\"0\"/>\n" +
                "  <visiting-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "</MLB-event>";

        FantasyPointUpdateEvent event = updateProcessor.process(data);
        assertTrue(event == null);
//        assertTrue(event.getIndicators().get(200060) == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
//        assertTrue(event.getIndicators().get(202781) == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
//        assertTrue(event.getIndicators().get(1234) == GlobalConstants.INDICATOR_TEAM_OFF_FIELD);


//        assertTrue("Expected map to be empty", events.isEmpty());
//        fail("Re-evaluate");
    }

    @Test
    public void testParseBoxScores() {
        startHazelcast();

        team = new Team(League.MLB, "Los Angeles", "Dodgers", "LAD", 243);
        sportsDao.saveTeam(team);

        team2 = new Team(League.MLB, "San Francisco", "Giants", "SFG", 250);
        sportsDao.saveTeam(team2);

        athlete1 = new Athlete(184104, "Adrian", "Gonzalez", team, "1");
        athlete1.setPositions(Arrays.asList(Position.BS_FIRST_BASE));
        sportsDao.saveAthlete(athlete1);
        athlete2 = new Athlete(202716, "Matt", "Cain", team2, "2");
        athlete2.setPositions(Arrays.asList(Position.BS_PITCHER));
        sportsDao.saveAthlete(athlete2);
        Athlete athleteHanleyRamirez = new Athlete(201879, "Hanley", "Ramirez", team, "2");
        athleteHanleyRamirez.setPositions(Arrays.asList(Position.BS_SHORT_STOP));
        sportsDao.saveAthlete(athleteHanleyRamirez);
        Athlete athleteCarlCrawford = new Athlete(12345, "Carl", "Crawford", team, "2");
        athleteCarlCrawford.setPositions(Arrays.asList(Position.BS_OUTFIELD));
        sportsDao.saveAthlete(athleteCarlCrawford);
        Athlete athleteBusterPosey = new Athlete(6789, "Buster", "Posey", team2, "2");
        athleteBusterPosey.setPositions(Arrays.asList(Position.BS_CATCHER));
        sportsDao.saveAthlete(athleteBusterPosey);

        sportEvent = new SportEvent(1284165, League.MLB, new Date(), "{}", "{}", 9, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        sportsDao.saveSportEvent(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete1, new BigDecimal("0.00"), "{}", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
        athleteSportEventInfo2 = new AthleteSportEventInfo(sportEvent, athlete2, new BigDecimal("0.00"), "{}", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo2);
        AthleteSportEventInfo athleteSportEventInfoRamirez = new AthleteSportEventInfo(sportEvent, athleteHanleyRamirez, new BigDecimal("0.00"), "{}", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRamirez);
        AthleteSportEventInfo athleteSportEventInfoCrawford = new AthleteSportEventInfo(sportEvent, athleteCarlCrawford, new BigDecimal("0.00"), "{}", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoCrawford);
        AthleteSportEventInfo athleteSportEventInfoPosey = new AthleteSportEventInfo(sportEvent, athleteBusterPosey, new BigDecimal("0.00"), "{}", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoPosey);

        String data = "<MLB-event>\n" +
                "<gamecode code=\"330912119\" global-id=\"1284165\"/>\n" +
                " <gamestate>\n" +
                "  <game status=\"In-Progress\" status-id=\"1\" reason=\"\" inning=\"1\" balls=\"0\" strikes=\"0\" outs=\"2\" segment-number=\"1\" segment-division=\"0\" active-state=\"true\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                "  <batter id=\"7054\" global-id=\"184104\" first-name=\"Adrian\" last-name=\"Gonzalez\" batting-slot=\"4\" hand=\"L\"/>\n" +
                "  <pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\"/>\n" +
                "  <runner id=\"7488\" global-id=\"201879\" first-name=\"Hanley\" last-name=\"Ramirez\" base=\"1\"/>\n" +
                " </gamestate>\n" +
                "  <home-team>\n" +
                "<team-name name=\"Dodgers\" alias=\"LAD\"/>\n" +
                "<team-city city=\"Los Angeles\"/>\n" +
                "<team-code id=\"19\" global-id=\"243\"/>\n" +
                "   <record wins=\"85\" losses=\"60\" ties=\"0\" pct=\".586\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\" wins=\"14\" losses=\"3\"/>\n" +
                "    <next-up-batter id=\"7710\" global-id=\"226249\" first-name=\"Andre\" last-name=\"Ethier\" hand=\"L\"/>\n" +
                "    <next-up-batter-two id=\"6698\" global-id=\"8757\" first-name=\"Juan\" last-name=\"Uribe\" hand=\"R\"/>\n" +
                "    <next-up-batter-three id=\"8373\" global-id=\"225850\" first-name=\"A.J.\" last-name=\"Ellis\" hand=\"R\"/>\n" +
                "    <due-up due=\"false\"/>\n" +
                "  </home-team>\n" +
                "  <home-score type=\"runs\" type-id=\"1\" number=\"1\"/>\n" +
                "  <home-score type=\"hits\" type-id=\"2\" number=\"1\"/>\n" +
                "  <home-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "  <visiting-team>\n" +
                "<team-name name=\"Giants\" alias=\"SF\"/>\n" +
                "<team-city city=\"San Francisco\"/>\n" +
                "<team-code id=\"26\" global-id=\"250\"/>\n" +
                "   <record wins=\"66\" losses=\"80\" ties=\"0\" pct=\".452\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\" wins=\"8\" losses=\"9\"/>\n" +
                "    <next-up-batter id=\"7963\" global-id=\"262071\" first-name=\"Hunter\" last-name=\"Pence\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"8326\" global-id=\"254551\" first-name=\"Pablo\" last-name=\"Sandoval\" hand=\"S\"/>\n" +
                "    <next-up-batter-three id=\"8945\" global-id=\"454517\" first-name=\"Brandon\" last-name=\"Crawford\" hand=\"L\"/>\n" +
                "    <due-up due=\"true\"/>\n" +
                "  </visiting-team>\n" +
                "  <visiting-score type=\"runs\" type-id=\"1\" number=\"0\"/>\n" +
                "  <visiting-score type=\"hits\" type-id=\"2\" number=\"0\"/>\n" +
                "  <visiting-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                " <event-details>\n" +
                "  <event sequence=\"25\" id=\"4\" code=\"1\" name=\"Single\" balls=\"0\" strikes=\"1\" outs-bef=\"2\" outs-aft=\"2\" rbi=\"0\" segment-number=\"1\" segment-division=\"Bottom\" dir=\"M\" dist=\"157\" bat-type=\"G\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"1\" vis-score=\"0\" home-score=\"0\" team-id=\"19\" team-global-id=\"0\" team-city=\"Los Angeles\" team-name=\"Dodgers\" team-alias=\"LAD\"/>\n" +
                "  <batter id=\"7488\" global-id=\"201879\" first-name=\"Hanley\" last-name=\"Ramirez\" batting-slot=\"3\" hand=\"R\" end-base=\"1\"/>\n" +
                "  <pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\"/>\n" +
                "  <pitch sequence=\"Foul,InPlay\"/>\n" +
                "  <description text=\"H.Ramirez singled to center.\"/>\n" +
                " </event-details>\n" +
                "<baseball-mlb-boxscore-home-team-stats>\n" +
                "<baseball-mlb-boxscore-team-stats>\n" +
                "<at-bats at-bats=\"3\"/>\n" +
                "<runs runs=\"0\"/>\n" +
                "<hits hits=\"1\"/>\n" +
                "<runs-batted-in runs-batted-in=\"0\"/>\n" +
                "<total-bases total-bases=\"1\"/>\n" +
                "<walks walks=\"0\"/>\n" +
                "<strike-outs strike-outs=\"0\"/>\n" +
                "<runners-left-on-base runners=\"0\"/>\n" +
                "<double-plays number=\"0\"/>\n" +
                "<triple-plays number=\"0\"/>\n" +
                "<total-innings total=\"1\"/>\n" +
                "</baseball-mlb-boxscore-team-stats>\n" +
                "</baseball-mlb-boxscore-home-team-stats>\n" +
                "<baseball-mlb-boxscore-visiting-team-stats>\n" +
                "<baseball-mlb-boxscore-team-stats>\n" +
                "<at-bats at-bats=\"3\"/>\n" +
                "<runs runs=\"0\"/>\n" +
                "<hits hits=\"0\"/>\n" +
                "<runs-batted-in runs-batted-in=\"0\"/>\n" +
                "<total-bases total-bases=\"0\"/>\n" +
                "<walks walks=\"1\"/>\n" +
                "<strike-outs strike-outs=\"0\"/>\n" +
                "<runners-left-on-base runners=\"1\"/>\n" +
                "<double-plays number=\"0\"/>\n" +
                "<triple-plays number=\"0\"/>\n" +
                "<total-innings total=\"1\"/>\n" +
                "</baseball-mlb-boxscore-team-stats>\n" +
                "</baseball-mlb-boxscore-visiting-team-stats>\n" +
                "<baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "<baseball-mlb-boxscore-batting-lineup>\n" +
                "<name first-name=\"Hanley\" last-name=\"Ramirez\"/>\n" +
                "<player-code id=\"7488\" global-id=\"201879\" uniform=\"13\"/>\n" +
                "<player-position position=\"SS\" id=\"6\"/>\n" +
                "<batting-hand hand=\"R\"/>\n" +
                "<batting-slot slot=\"3\"/>\n" +
                "<at-bats at-bats=\"1\" season=\"282\"/>\n" +
                "<runs runs=\"0\" season=\"57\"/>\n" +
                "<hits hits=\"1\" season=\"97\"/>\n" +
                "<doubles doubles=\"0\" season=\"25\"/>\n" +
                "<triples triples=\"0\" season=\"2\"/>\n" +
                "<home-runs home-runs=\"0\" season=\"18\"/>\n" +
                "<stolen-bases stolen-bases=\"0\" season=\"10\"/>\n" +
                "<caught-stealing caught-stealing=\"0\" season=\"2\"/>\n" +
                "<runs-batted-in runs-batted-in=\"0\" season=\"53\"/>\n" +
                "<total-bases total-bases=\"1\" season=\"180\"/>\n" +
                "<walks walks=\"0\" season=\"22\"/>\n" +
                "<strike-outs strike-outs=\"0\" season=\"47\"/>\n" +
                "<left-on-base number=\"0\"/>\n" +
                "<plate-appearances number=\"1\" season=\"308\"/>\n" +
                "<hit-by-pitch number=\"0\" season=\"2\"/>\n" +
                "<ground-into-double-play number=\"0\" season=\"5\"/>\n" +
                "<sacrifice-flies number=\"0\" season=\"2\"/>\n" +
                "<sacrifice-hits number=\"0\" season=\"0\"/>\n" +
                "<batting-average average=\".344\"/>\n" +
                "<on-base-percentage percentage=\".393\"/>\n" +
                "<slugging-percentage percentage=\".638\"/>\n" +
                "<started-game started-game=\"true\"/>\n" +
                "<entered-game inning=\"1\"/>\n" +
                "<slot-index number=\"1\"/>\n" +
                "<picked-off number=\"0\" season=\"0\"/>\n" +
                "</baseball-mlb-boxscore-batting-lineup>\n" +
                "</baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "<baseball-mlb-boxscore-visiting-team-batting-lineup>\n" +
                "</baseball-mlb-boxscore-visiting-team-batting-lineup>\n" +
                "<baseball-mlb-boxscore-home-team-pitching-lineup>\n" +
                "</baseball-mlb-boxscore-home-team-pitching-lineup>\n" +
                "<baseball-mlb-boxscore-visiting-team-pitching-lineup>\n" +
                "<baseball-mlb-boxscore-pitching-lineup>\n" +
                "<name first-name=\"Matt\" last-name=\"Cain\"/>\n" +
                "<player-code id=\"7495\" global-id=\"202716\" uniform=\"18\"/>\n" +
                "<throwing-hand hand=\"R\"/>\n" +
                "<sequence number=\"1\"/>\n" +
                "<wins number=\"8\"/>\n" +
                "<losses number=\"9\"/>\n" +
                "<saves saves=\"0\"/>\n" +
                "<innings-pitched innings=\"0.2\" season=\"163.1\"/>\n" +
                "<games-pitched games=\"28\" starts=\"28\"/>\n" +
                "<hits hits=\"1\" season=\"140\"/>\n" +
                "<runs runs=\"0\" season=\"80\"/>\n" +
                "<earned-runs earned-runs=\"0\" season=\"79\"/>\n" +
                "<home-runs-allowed home-runs=\"0\" season=\"21\"/>\n" +
                "<walks walks=\"0\" season=\"51\"/>\n" +
                "<strike-outs strike-outs=\"0\" season=\"143\"/>\n" +
                "<pitch-count count=\"7\"/>\n" +
                "<balls balls=\"2\"/>\n" +
                "<strikes strikes=\"5\"/>\n" +
                "<batters-faced batters-faced=\"3\" season=\"675\"/>\n" +
                "<opponent-at-bats at-bats=\"3\" season=\"614\"/>\n" +
                "<ground-balls number=\"1\" season=\"178\"/>\n" +
                "<fly-balls number=\"2\" season=\"179\"/>\n" +
                "<inherited-runners runners=\"0\"/>\n" +
                "<inherited-stranded stranded=\"0\"/>\n" +
                "<hit-batsmen number=\"0\" season=\"4\"/>\n" +
                "<balks number=\"0\" season=\"0\"/>\n" +
                "<wild-pitches number=\"0\" season=\"1\"/>\n" +
                "<earned-run-average average=\"4.35\"/>\n" +
                "<opponent-batting-average average=\".228\"/>\n" +
                "<entered-game inning=\"1\"/>\n" +
                "<doubles number=\"0\" season=\"30\"/>\n" +
                "<triples number=\"0\" season=\"5\"/>\n" +
                "<gidp number=\"0\" season=\"8\"/>\n" +
                "<stolen-bases number=\"0\" season=\"14\"/>\n" +
                "<pickoffs number=\"0\" season=\"0\"/>\n" +
                "<walks-and-hits-per-inning average=\"1.17\"/>\n" +
                "</baseball-mlb-boxscore-pitching-lineup>\n" +
                "</baseball-mlb-boxscore-visiting-team-pitching-lineup>\n" +
                "</MLB-event>";

        FantasyPointUpdateEvent event = updateProcessor.process(data);

        assertTrue(event.getCurrentUnitOfTime() == 1);
        assertTrue(event.getSportEvent().getUnitsRemaining() == 8);

        assertTrue(event.getEventDescription().equals("H.Ramirez singled to center."));

        assertTrue(event.getAwayScore() == 0);
        assertTrue(event.getHomeScore() == 1);

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};
        try {
            Map<String, Object> scoreData = mapper.readValue(event.getSportEvent().getShortDescription(), typeReference);
            assertTrue((Integer) scoreData.get("homeScore") == 1);
            assertTrue((Integer) scoreData.get("awayScore") == 0);
        } catch (IOException e) {
            Logger.error("Unable to parse the SportEvent short description for " + sportEvent.getId() + ": " + e.getMessage());
        }

        /*
         * Indicators
         */
        assertTrue(event.getIndicators().get(184104) == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        assertTrue(event.getIndicators().get(202716) == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        assertTrue(event.getIndicators().get(201879) == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        assertTrue(event.getIndicators().get(12345) == GlobalConstants.INDICATOR_TEAM_ON_FIELD);
        assertTrue(event.getIndicators().get(6789) == GlobalConstants.INDICATOR_TEAM_OFF_FIELD);

        assertTrue(sportsDao.findAthleteSportEventInfo(athleteSportEventInfo.getId()).getIndicator() == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        assertTrue(sportsDao.findAthleteSportEventInfo(athleteSportEventInfo2.getId()).getIndicator() == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        assertTrue(sportsDao.findAthleteSportEventInfo(athleteSportEventInfoRamirez.getId()).getIndicator() == GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
        assertTrue(sportsDao.findAthleteSportEventInfo(athleteSportEventInfoCrawford.getId()).getIndicator() == GlobalConstants.INDICATOR_TEAM_ON_FIELD);
        assertTrue(sportsDao.findAthleteSportEventInfo(athleteSportEventInfoPosey.getId()).getIndicator() == GlobalConstants.INDICATOR_TEAM_OFF_FIELD);



        assertTrue(event.getFantasyPointAthleteUpdateEventList().size() == 2);

        /*
         * Pitcher
         */
        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = event.getFantasyPointAthleteUpdateEventList().get(0);

        assertTrue(fantasyPointAthleteUpdateEvent.getFantasyPointDelta().compareTo(new BigDecimal("-0.6")) == 0);
        assertTrue(fantasyPointAthleteUpdateEvent.getFantasyPoints().compareTo(new BigDecimal("0.9")) == 0);

        String timeline = fantasyPointAthleteUpdateEvent.getTimeline();
        try {
            List<Map<String, Object>> timelineList = mapper.readValue(timeline, listTypeReference);
            assertTrue(timelineList.size() == 1);
            Map<String, Object> timelineValues = timelineList.get(0);
            assertTrue(timelineValues.get("timestamp") instanceof Long);
            assertTrue(timelineValues.get("description").equals("H.Ramirez singled to center."));
            assertTrue(timelineValues.get("fpChange").equals("-0.6"));
            assertTrue((Integer) timelineValues.get("athleteSportEventInfoId") == fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }

        String boxscore = fantasyPointAthleteUpdateEvent.getBoxscore();
        try {
            List<Map<String, Object>> boxScoreList = mapper.readValue(boxscore, listTypeReference);
            assertTrue(boxScoreList.size() == 8);

            for(Map<String, Object> boxScoreEntry: boxScoreList) {
                if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL)) {
                    assertTrue(boxScoreEntry.get("abbr").equals(GlobalConstants.SCORING_MLB_INNINGS_PITCHED_ABBR));
                    assertTrue((Double) boxScoreEntry.get("amount") == 0.2);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 1.5);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 1);
                    assertTrue((Double) boxScoreEntry.get("fpp") == -0.6);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_WIN_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
            }
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).compareTo(new BigDecimal("0.2")) == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL).intValue() == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL).intValue() == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL).intValue() == 1);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL).intValue() == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL).intValue() == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_WIN_LABEL).intValue() == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL).intValue() == 0);
        }
        catch(Exception e) {
            fail(e.getMessage());
        }




        /*
         * Batter
         */
        fantasyPointAthleteUpdateEvent = event.getFantasyPointAthleteUpdateEventList().get(1);

        assertTrue(fantasyPointAthleteUpdateEvent.getFantasyPointDelta().compareTo(new BigDecimal("3")) == 0);
        assertTrue(fantasyPointAthleteUpdateEvent.getFantasyPoints().compareTo(new BigDecimal("3")) == 0);

        timeline = fantasyPointAthleteUpdateEvent.getTimeline();
        try {
            List<Map<String, Object>> timelineList = mapper.readValue(timeline, listTypeReference);
            assertTrue(timelineList.size() == 1);
            Map<String, Object> timelineValues = timelineList.get(0);
            assertTrue(timelineValues.get("timestamp") instanceof Long);
            assertTrue(timelineValues.get("description").equals("H.Ramirez singled to center."));
            assertTrue(timelineValues.get("fpChange").equals("+3.0"));
            assertTrue((Integer) timelineValues.get("athleteSportEventInfoId") == fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId());
        }
        catch(Exception e) {
            fail(e.getMessage());
        }

        boxscore = fantasyPointAthleteUpdateEvent.getBoxscore();
        try {
            List<Map<String, Object>> boxScoreList = mapper.readValue(boxscore, listTypeReference);
            assertTrue(boxScoreList.size() == 10);

            for(Map<String, Object> boxScoreEntry: boxScoreList) {
                if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_DOUBLE_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_TRIPLE_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_HOMERUN_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_SINGLE_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 1);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 3);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_RUN_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_WALK_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
                else if(boxScoreEntry.get("name").equals(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL)) {
                    assertTrue((Integer) boxScoreEntry.get("amount") == 0);
                    assertTrue((Double) boxScoreEntry.get("fpp") == 0);
                }
            }


//            Map<String, BigDecimal> boxScoreValues = boxScoreList.get(0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_DOUBLE_LABEL) == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_TRIPLE_LABEL) == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_HOMERUN_LABEL) ==  0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_HIT_LABEL) == 1);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_SINGLE_LABEL) == 1);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL) == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_RUN_LABEL) == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_WALK_LABEL) == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL) == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL) == 0);
//            assertTrue(boxScoreValues.get(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL) == 0);
        }
        catch(Exception e) {
            fail(e.getMessage());
        }




//
//        SportEvent updatedSportEvent = SportEvent.findByStatsId(sportEvent.getStatProviderId());
//        assertTrue(String.format("Units remaining is %s, expected 8", updatedSportEvent.getUnitsRemaining()), updatedSportEvent.getUnitsRemaining() == 8);
//
//        assertTrue(events.size() == 2);
//        assertTrue(events.get(0).getAthleteId() == 201879
//                && events.get(0).getFantasyPoints().doubleValue() == 3.0
//                && events.get(0).getLatestDelta().doubleValue() == 3.0
//                && events.get(0).getAwayScore() == 0
//                && events.get(0).getHomeScore() == 0);
//        assertTrue(events.get(1).getAthleteId() == 202716
//                && events.get(1).getFantasyPoints().doubleValue() == 0.885
//                && events.get(1).getLatestDelta().doubleValue() == 0.885
//                && events.get(0).getAwayScore() == 0
//                && events.get(0).getHomeScore() == 0);
    }

//    @Test
    public void testProcessMessage_NoEventDetails() {
        String statData = "<MLB-event>\n" +
                "<gamecode code=\"330912119\" global-id=\"1284165\"/>\n" +
                " <gamestate>\n" +
                "  <game status=\"In-Progress\" status-id=\"1\" reason=\"\" inning=\"1\" balls=\"1\" strikes=\"0\" outs=\"0\" segment-number=\"1\" segment-division=\"1\" active-state=\"true\" restart=\"false\" pitch-sequence=\"Ball,\"/>\n" +
                "  <batter id=\"7717\" global-id=\"200060\" first-name=\"Angel\" last-name=\"Pagan\" batting-slot=\"1\" hand=\"S\"/>\n" +
                "  <pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\"/>\n" +
                " </gamestate>\n" +
                "  <home-team>\n" +
                "<team-name name=\"Dodgers\" alias=\"LAD\"/>\n" +
                "<team-city city=\"Los Angeles\"/>\n" +
                "<team-code id=\"19\" global-id=\"243\"/>\n" +
                "   <record wins=\"85\" losses=\"60\" ties=\"0\" pct=\".586\"/>\n" +
                "   <innings>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\" wins=\"14\" losses=\"3\"/>\n" +
                "    <next-up-batter id=\"9341\" global-id=\"659910\" first-name=\"Yasiel\" last-name=\"Puig\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"6870\" global-id=\"75597\" first-name=\"Carl\" last-name=\"Crawford\" hand=\"L\"/>\n" +
                "    <next-up-batter-three id=\"7488\" global-id=\"201879\" first-name=\"Hanley\" last-name=\"Ramirez\" hand=\"R\"/>\n" +
                "    <due-up due=\"true\"/>\n" +
                "  </home-team>\n" +
                "  <home-score type=\"runs\" type-id=\"1\" number=\"0\"/>\n" +
                "  <home-score type=\"hits\" type-id=\"2\" number=\"0\"/>\n" +
                "  <home-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "  <visiting-team>\n" +
                "<team-name name=\"Giants\" alias=\"SF\"/>\n" +
                "<team-city city=\"San Francisco\"/>\n" +
                "<team-code id=\"26\" global-id=\"250\"/>\n" +
                "   <record wins=\"66\" losses=\"80\" ties=\"0\" pct=\".452\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\" wins=\"8\" losses=\"9\"/>\n" +
                "    <next-up-batter id=\"6966\" global-id=\"168575\" first-name=\"Marco\" last-name=\"Scutaro\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"8795\" global-id=\"327177\" first-name=\"Brandon\" last-name=\"Belt\" hand=\"L\"/>\n" +
                "    <next-up-batter-three id=\"8578\" global-id=\"454353\" first-name=\"Buster\" last-name=\"Posey\" hand=\"R\"/>\n" +
                "    <due-up due=\"false\"/>\n" +
                "  </visiting-team>\n" +
                "  <visiting-score type=\"runs\" type-id=\"1\" number=\"0\"/>\n" +
                "  <visiting-score type=\"hits\" type-id=\"2\" number=\"0\"/>\n" +
                "  <visiting-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "</MLB-event>";

//        List<FantasyPointUpdateEvent> events = updateProcessor.process(statData);
//
//        assertTrue(events.isEmpty());
        fail("Re-evaluate");
    }

//    @Test
    public void testProcessMessage_EventDetails_NonRelevantEvent() {
        String statData = "<MLB-event>\n" +
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
                "\t\t<event-details>\n" +
                "\t\t   <event sequence=\"3\" id=\"1\" code=\"96\" name=\"Lineup Change\" balls=\"0\" strikes=\"0\" outs-bef=\"0\" outs-aft=\"0\" segment-number=\"\" segment-division=\"\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"0\" vis-score=\"0\" home-score=\"0\" team-id=\"26\" team-global-id=\"250\" team-city=\"San Francisco\" team-name=\"Giants\" team-alias=\"SF\">\n" +
                "\t\t\t  <player id=\"7717\" global-id=\"200060\" first-name=\"Angel\" last-name=\"Pagan\" batting-slot=\"1\" pos-id=\"8\" pos-name=\"CF\"/>\n" +
                "\t\t   </event>\n" +
                "\t\t</event-details>\n" +
                "   </MLB-event>";

//        List<FantasyPointUpdateEvent> events = updateProcessor.process(statData);
//
//        assertTrue(events.isEmpty());
        fail("Re-evaluate");
    }

//    @Test
    public void testProcessMessage_EventDetails_RelevantEvent() {
        startHazelcast();

        athlete1 = new Athlete(200060, "Player1", "test", team, "1");
        Ebean.save(athlete1);

        sportEvent = new SportEvent(1284165, League.MLB, new Date(), "test", "test", 9, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete1, new BigDecimal("0.00"), "{}", "[]");
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
                "  <home-team>\n" +
                "<team-name name=\"Dodgers\" alias=\"LAD\"/>\n" +
                "<team-city city=\"Los Angeles\"/>\n" +
                "<team-code id=\"19\" global-id=\"243\"/>\n" +
                "   <record wins=\"85\" losses=\"60\" ties=\"0\" pct=\".586\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\" wins=\"14\" losses=\"3\"/>\n" +
                "    <next-up-batter id=\"7710\" global-id=\"226249\" first-name=\"Andre\" last-name=\"Ethier\" hand=\"L\"/>\n" +
                "    <next-up-batter-two id=\"6698\" global-id=\"8757\" first-name=\"Juan\" last-name=\"Uribe\" hand=\"R\"/>\n" +
                "    <next-up-batter-three id=\"8373\" global-id=\"225850\" first-name=\"A.J.\" last-name=\"Ellis\" hand=\"R\"/>\n" +
                "    <due-up due=\"false\"/>\n" +
                "  </home-team>\n" +
                "  <home-score type=\"runs\" type-id=\"1\" number=\"2\"/>\n" +
                "  <home-score type=\"hits\" type-id=\"2\" number=\"1\"/>\n" +
                "  <home-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "  <visiting-team>\n" +
                "<team-name name=\"Giants\" alias=\"SF\"/>\n" +
                "<team-city city=\"San Francisco\"/>\n" +
                "<team-code id=\"26\" global-id=\"250\"/>\n" +
                "   <record wins=\"66\" losses=\"80\" ties=\"0\" pct=\".452\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\" wins=\"8\" losses=\"9\"/>\n" +
                "    <next-up-batter id=\"7963\" global-id=\"262071\" first-name=\"Hunter\" last-name=\"Pence\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"8326\" global-id=\"254551\" first-name=\"Pablo\" last-name=\"Sandoval\" hand=\"S\"/>\n" +
                "    <next-up-batter-three id=\"8945\" global-id=\"454517\" first-name=\"Brandon\" last-name=\"Crawford\" hand=\"L\"/>\n" +
                "    <due-up due=\"true\"/>\n" +
                "  </visiting-team>\n" +
                "  <visiting-score type=\"runs\" type-id=\"1\" number=\"1\"/>\n" +
                "  <visiting-score type=\"hits\" type-id=\"2\" number=\"1\"/>\n" +
                "  <visiting-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
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

//        List<FantasyPointUpdateEvent> events = updateProcessor.process(statData);
//
//        assertTrue(events.size() == 1);
//
//        assertTrue(events.get(0).getAwayScore() == 1
//                && events.get(0).getHomeScore() == 2);
//
//        SportEvent updatedSportEvent = SportEvent.findByStatsId(sportEvent.getStatProviderId());
//        assertTrue(String.format("unitsRemaining is %s, expected 0", updatedSportEvent.getUnitsRemaining()), updatedSportEvent.getUnitsRemaining() == 0);
        fail("Re-evaluate");
    }

//    @Test
    public void testProcessMessage_EventDetails_RelevantEvent_ExistingFantasyPoints() {
        startHazelcast();

        athlete1 = new Athlete(200060, "Player1", "test", team, "1");
        Ebean.save(athlete1);

        sportEvent = new SportEvent(1284165, League.MLB, new Date(), "test", "test", 9, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete1, new BigDecimal("5.00"), "{}", "[]");
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
                "  <home-team>\n" +
                "<team-name name=\"Dodgers\" alias=\"LAD\"/>\n" +
                "<team-city city=\"Los Angeles\"/>\n" +
                "<team-code id=\"19\" global-id=\"243\"/>\n" +
                "   <record wins=\"85\" losses=\"60\" ties=\"0\" pct=\".586\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\" wins=\"14\" losses=\"3\"/>\n" +
                "    <next-up-batter id=\"7710\" global-id=\"226249\" first-name=\"Andre\" last-name=\"Ethier\" hand=\"L\"/>\n" +
                "    <next-up-batter-two id=\"6698\" global-id=\"8757\" first-name=\"Juan\" last-name=\"Uribe\" hand=\"R\"/>\n" +
                "    <next-up-batter-three id=\"8373\" global-id=\"225850\" first-name=\"A.J.\" last-name=\"Ellis\" hand=\"R\"/>\n" +
                "    <due-up due=\"false\"/>\n" +
                "  </home-team>\n" +
                "  <home-score type=\"runs\" type-id=\"1\" number=\"2\"/>\n" +
                "  <home-score type=\"hits\" type-id=\"2\" number=\"1\"/>\n" +
                "  <home-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "  <visiting-team>\n" +
                "<team-name name=\"Giants\" alias=\"SF\"/>\n" +
                "<team-city city=\"San Francisco\"/>\n" +
                "<team-code id=\"26\" global-id=\"250\"/>\n" +
                "   <record wins=\"66\" losses=\"80\" ties=\"0\" pct=\".452\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\" wins=\"8\" losses=\"9\"/>\n" +
                "    <next-up-batter id=\"7963\" global-id=\"262071\" first-name=\"Hunter\" last-name=\"Pence\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"8326\" global-id=\"254551\" first-name=\"Pablo\" last-name=\"Sandoval\" hand=\"S\"/>\n" +
                "    <next-up-batter-three id=\"8945\" global-id=\"454517\" first-name=\"Brandon\" last-name=\"Crawford\" hand=\"L\"/>\n" +
                "    <due-up due=\"true\"/>\n" +
                "  </visiting-team>\n" +
                "  <visiting-score type=\"runs\" type-id=\"1\" number=\"1\"/>\n" +
                "  <visiting-score type=\"hits\" type-id=\"2\" number=\"1\"/>\n" +
                "  <visiting-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
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

//        List<FantasyPointUpdateEvent> events = updateProcessor.process(statData);
//
//        AthleteSportEventInfo athleteSportEventInfo1 = AthleteSportEventInfo.findByAthleteAndSportEvent(athlete1, sportEvent);
//        try {
//            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};
//            List<Map<String, Object>> data = mapper.readValue(athleteSportEventInfo1.getStats(), typeReference);
//            assertTrue(data.get(0).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_DOUBLE_LABEL));
//            assertTrue((Integer) data.get(0).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(0).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(1).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_TRIPLE_LABEL));
//            assertTrue((Integer) data.get(1).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(1).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(2).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_HOMERUN_LABEL));
//            assertTrue((Integer) data.get(2).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(2).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(3).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_SINGLE_LABEL));
//            assertTrue((Integer) data.get(3).get(JSON_FIELD_AMOUNT) == 2);
//            assertTrue((Double) data.get(3).get(JSON_FIELD_FPP) == 6.0);
//            assertTrue(data.get(4).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL));
//            assertTrue((Integer) data.get(4).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(4).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(5).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_RUN_LABEL));
//            assertTrue((Integer) data.get(5).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(5).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(6).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_WALK_LABEL));
//            assertTrue((Integer) data.get(6).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(6).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(7).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL));
//            assertTrue((Integer) data.get(7).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(7).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(8).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL));
//            assertTrue((Integer) data.get(8).get(JSON_FIELD_AMOUNT) == 1);
//            assertTrue((Double) data.get(8).get(JSON_FIELD_FPP) == 5.0);
//            assertTrue(data.get(9).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL));
//            assertTrue((Integer) data.get(9).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(9).get(JSON_FIELD_FPP) == 0.0);
//
//            List<Map<String, Object>> timelineData = mapper.readValue(athleteSportEventInfo1.getTimeline(), typeReference);
//            assertTrue(timelineData.size() == 1);
//            assertTrue(timelineData.get(0).get("description").equals("M.Scutaro walked on a full count."));
//            assertTrue(timelineData.get(0).get("fpChange").equals("+6.00"));
//            assertTrue(!timelineData.get(0).get("timestamp").equals(""));
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//
//        SportEvent updatedSportEvent = SportEvent.findByStatsId(sportEvent.getStatProviderId());
//        assertTrue(String.format("unitsRemaining is %s, expected 0", updatedSportEvent.getUnitsRemaining()), updatedSportEvent.getUnitsRemaining() == 0);
//
//        assertTrue(events.size() == 1);
//        assertTrue(events.get(0).getAthleteId() == 200060 && events.get(0).getFantasyPoints().doubleValue() == 11.0 && events.get(0).getLatestDelta().doubleValue() == 6.0);
        fail("Re-evaluate");
    }

//    @Test
    public void testProcessMessage_EventDetails_RelevantEvent_Pitcher() {
        startHazelcast();

        athlete1 = new Athlete(253985, "JA", "Happ", team, "1");
        Ebean.save(athlete1);

        sportEvent = new SportEvent(1284165, League.MLB, new Date(), "test", "test", 9, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete1, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfo);

        String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<MLB-event>\n" +
                "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                "   <gamestate>\n" +
                "      <game status=\"In-Progress\" status-id=\"2\" reason=\"\" inning=\"7\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"7\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                "   </gamestate>\n" +
                "   <double-header double-header=\"false\"/>\n" +
                "   <double-header-game-number number=\"0\"/>\n" +
                "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                "   <league id=\"2\" league=\"NL\"/>\n" +
                "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                "  <home-team>\n" +
                "<team-name name=\"Dodgers\" alias=\"LAD\"/>\n" +
                "<team-city city=\"Los Angeles\"/>\n" +
                "<team-code id=\"19\" global-id=\"243\"/>\n" +
                "   <record wins=\"85\" losses=\"60\" ties=\"0\" pct=\".586\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7257\" global-id=\"202781\" first-name=\"Zack\" last-name=\"Greinke\" hand=\"R\" wins=\"14\" losses=\"3\"/>\n" +
                "    <next-up-batter id=\"7710\" global-id=\"226249\" first-name=\"Andre\" last-name=\"Ethier\" hand=\"L\"/>\n" +
                "    <next-up-batter-two id=\"6698\" global-id=\"8757\" first-name=\"Juan\" last-name=\"Uribe\" hand=\"R\"/>\n" +
                "    <next-up-batter-three id=\"8373\" global-id=\"225850\" first-name=\"A.J.\" last-name=\"Ellis\" hand=\"R\"/>\n" +
                "    <due-up due=\"false\"/>\n" +
                "  </home-team>\n" +
                "  <home-score type=\"runs\" type-id=\"1\" number=\"2\"/>\n" +
                "  <home-score type=\"hits\" type-id=\"2\" number=\"1\"/>\n" +
                "  <home-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
                "  <visiting-team>\n" +
                "<team-name name=\"Giants\" alias=\"SF\"/>\n" +
                "<team-city city=\"San Francisco\"/>\n" +
                "<team-code id=\"26\" global-id=\"250\"/>\n" +
                "   <record wins=\"66\" losses=\"80\" ties=\"0\" pct=\".452\"/>\n" +
                "   <innings>\n" +
                "    <inning number=\"1\" score=\"0\"/>\n" +
                "   </innings>\n" +
                "    <current-pitcher id=\"7495\" global-id=\"202716\" first-name=\"Matt\" last-name=\"Cain\" hand=\"R\" wins=\"8\" losses=\"9\"/>\n" +
                "    <next-up-batter id=\"7963\" global-id=\"262071\" first-name=\"Hunter\" last-name=\"Pence\" hand=\"R\"/>\n" +
                "    <next-up-batter-two id=\"8326\" global-id=\"254551\" first-name=\"Pablo\" last-name=\"Sandoval\" hand=\"S\"/>\n" +
                "    <next-up-batter-three id=\"8945\" global-id=\"454517\" first-name=\"Brandon\" last-name=\"Crawford\" hand=\"L\"/>\n" +
                "    <due-up due=\"true\"/>\n" +
                "  </visiting-team>\n" +
                "  <visiting-score type=\"runs\" type-id=\"1\" number=\"1\"/>\n" +
                "  <visiting-score type=\"hits\" type-id=\"2\" number=\"1\"/>\n" +
                "  <visiting-score type=\"errors\" type-id=\"3\" number=\"0\"/>\n" +
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
                "       <total-innings total=\"7\"/>\n" +
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
                "       <total-innings total=\"7\"/>\n" +
                "     </baseball-mlb-boxscore-team-stats>\n" +
                "   </baseball-mlb-boxscore-visiting-team-stats>\n" +
                "   <baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "   </baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "<baseball-mlb-boxscore-home-team-pitching-lineup>\n" +
                "<baseball-mlb-boxscore-pitching-lineup>\n" +
                "<name first-name=\"J.A.\" last-name=\"Happ\"/>\n" +
                "<player-code id=\"8061\" global-id=\"253985\" uniform=\"48\"/>\n" +
                "<throwing-hand hand=\"L\"/>\n" +
                "<sequence number=\"1\"/>\n" +
                "<wins number=\"4\"/>\n" +
                "<losses number=\"5\"/>\n" +
                "<saves saves=\"0\"/>\n" +
                "<innings-pitched innings=\"0.2\" season=\"69.2\"/>\n" +
                "<games-pitched games=\"15\" starts=\"15\"/>\n" +
                "<hits hits=\"0\" season=\"68\"/>\n" +
                "<runs runs=\"0\" season=\"44\"/>\n" +
                "<earned-runs earned-runs=\"0\" season=\"39\"/>\n" +
                "<home-runs-allowed home-runs=\"0\" season=\"7\"/>\n" +
                "<walks walks=\"0\" season=\"36\"/>\n" +
                "<strike-outs strike-outs=\"1\" season=\"56\"/>\n" +
                "<pitch-count count=\"14\"/>\n" +
                "<balls balls=\"4\"/>\n" +
                "<strikes strikes=\"10\"/>\n" +
                "<batters-faced batters-faced=\"2\" season=\"315\"/>\n" +
                "<opponent-at-bats at-bats=\"2\" season=\"273\"/>\n" +
                "<ground-balls number=\"0\" season=\"84\"/>\n" +
                "<fly-balls number=\"1\" season=\"95\"/>\n" +
                "<inherited-runners runners=\"0\"/>\n" +
                "<inherited-stranded stranded=\"0\"/>\n" +
                "<hit-batsmen number=\"0\" season=\"2\"/>\n" +
                "<balks number=\"0\" season=\"0\"/>\n" +
                "<wild-pitches number=\"0\" season=\"4\"/>\n" +
                "<earned-run-average average=\"5.04\"/>\n" +
                "<opponent-batting-average average=\".249\"/>\n" +
                "<entered-game inning=\"1\"/>\n" +
                "<doubles number=\"0\" season=\"15\"/>\n" +
                "<triples number=\"0\" season=\"2\"/>\n" +
                "<gidp number=\"0\" season=\"3\"/>\n" +
                "<stolen-bases number=\"0\" season=\"9\"/>\n" +
                "<pickoffs number=\"0\" season=\"0\"/>\n" +
                "<walks-and-hits-per-inning average=\"1.51\"/>\n" +
                "</baseball-mlb-boxscore-pitching-lineup>\n" +
                "</baseball-mlb-boxscore-home-team-pitching-lineup>" +
                "</MLB-event>\n";

//        List<FantasyPointUpdateEvent> events = updateProcessor.process(statData);
//
//        AthleteSportEventInfo athleteSportEventInfo1 = AthleteSportEventInfo.findByAthleteAndSportEvent(athlete1, sportEvent);
//        try {
//            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};
//            List<Map<String, Object>> data = mapper.readValue(athleteSportEventInfo1.getStats(), typeReference);
//            assertTrue(data.get(0).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL));
//            assertTrue((Double) data.get(0).get(JSON_FIELD_AMOUNT) == 0.2);
//            assertTrue((Double) data.get(0).get(JSON_FIELD_FPP) == 1.485);
//            assertTrue(data.get(1).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL));
//            assertTrue((Integer) data.get(1).get(JSON_FIELD_AMOUNT) == 1);
//            assertTrue((Double) data.get(1).get(JSON_FIELD_FPP) == 2.0);
//            assertTrue(data.get(2).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL));
//            assertTrue((Integer) data.get(2).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(2).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(3).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL));
//            assertTrue((Integer) data.get(3).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(3).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(4).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL));
//            assertTrue((Integer) data.get(4).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(4).get(JSON_FIELD_FPP) == 0.0);
//            assertTrue(data.get(5).get(JSON_FIELD_NAME).equals(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL));
//            assertTrue((Integer) data.get(5).get(JSON_FIELD_AMOUNT) == 0);
//            assertTrue((Double) data.get(5).get(JSON_FIELD_FPP) == 0.0);
//
////            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};
//            List<Map<String, Object>> timelineData = mapper.readValue(athleteSportEventInfo1.getTimeline(), typeReference);
////            assertTrue(athleteSportEventInfo1.timeline.equals("[\"+3.4850 - M.Scutaro walked on a full count.\"]"));
//            assertTrue(timelineData.size() == 1);
//            assertTrue(!timelineData.get(0).get("timestamp").equals(""));
//            assertTrue(timelineData.get(0).get("description").equals("M.Scutaro walked on a full count."));
//            assertTrue(timelineData.get(0).get("fpChange").equals("+3.4850"));
//            assertTrue((Integer) timelineData.get(0).get("athleteSportEventInfoId") == athleteSportEventInfo1.getId());
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//
//        SportEvent updatedSportEvent = SportEvent.findByStatsId(sportEvent.getStatProviderId());
//        assertTrue(String.format("unitsRemaining is %s, expected 2", updatedSportEvent.getUnitsRemaining()), updatedSportEvent.getUnitsRemaining() == 2);
//
//        assertTrue(events.size() == 1);
//        assertTrue(events.get(0).getAthleteId() == 253985
//                && events.get(0).getFantasyPoints().doubleValue() == 3.485
//                && events.get(0).getLatestDelta().doubleValue() == 3.485
//                && events.get(0).getHomeScore() == 2
//                && events.get(0).getAwayScore() == 1);
        fail("Re-evaluate");
    }

    @Test
    public void testProcessMessage_LineupChange() {
        startHazelcast();

        athlete1 = new Athlete(253985, "JA", "Happ", team, "1");
        Ebean.save(athlete1);

        sportEvent = new SportEvent(1284165, League.MLB, new Date(), "test", "test", 9, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete1, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfo);

        String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<MLB-event>\n" +
                "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                "   <gamestate>\n" +
                "      <game status=\"Final\" status-id=\"2\" reason=\"\" inning=\"7\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"7\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                "   </gamestate>\n" +
                "   <double-header double-header=\"false\"/>\n" +
                "   <double-header-game-number number=\"0\"/>\n" +
                "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                "   <league id=\"2\" league=\"NL\"/>\n" +
                "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                "            <event-details>\n" +
                "               <event sequence=\"1\" id=\"1\" code=\"96\" name=\"Lineup Change\" balls=\"0\" strikes=\"0\" outs-bef=\"0\" outs-aft=\"0\" rbi=\"0\" segment-number=\"\" segment-division=\"\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"0\" vis-score=\"0\" home-score=\"0\" team-id=\"3\" team-global-id=\"0\" team-city=\"Los Angeles\" team-name=\"Angels\" team-alias=\"LAA\"/>\n" +
                "               <player id=\"8968\" global-id=\"390597\" first-name=\"Collin\" last-name=\"Cowgill\" batting-slot=\"1\" pos-id=\"8\" pos-name=\"CF\"/>\n" +
                "               <pitch sequence=\"\"/>\n" +
                "               <description text=\"\"/>\n" +
                "            </event-details>" +
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
                "       <total-innings total=\"7\"/>\n" +
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
                "       <total-innings total=\"7\"/>\n" +
                "     </baseball-mlb-boxscore-team-stats>\n" +
                "   </baseball-mlb-boxscore-visiting-team-stats>\n" +
                "   <baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "   </baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "<baseball-mlb-boxscore-home-team-pitching-lineup>\n" +
                "<baseball-mlb-boxscore-pitching-lineup>\n" +
                "<name first-name=\"J.A.\" last-name=\"Happ\"/>\n" +
                "<player-code id=\"8061\" global-id=\"253985\" uniform=\"48\"/>\n" +
                "<throwing-hand hand=\"L\"/>\n" +
                "<sequence number=\"1\"/>\n" +
                "<wins number=\"4\"/>\n" +
                "<losses number=\"5\"/>\n" +
                "<saves saves=\"0\"/>\n" +
                "<innings-pitched innings=\"0.2\" season=\"69.2\"/>\n" +
                "<games-pitched games=\"15\" starts=\"15\"/>\n" +
                "<hits hits=\"0\" season=\"68\"/>\n" +
                "<runs runs=\"0\" season=\"44\"/>\n" +
                "<earned-runs earned-runs=\"0\" season=\"39\"/>\n" +
                "<home-runs-allowed home-runs=\"0\" season=\"7\"/>\n" +
                "<walks walks=\"0\" season=\"36\"/>\n" +
                "<strike-outs strike-outs=\"1\" season=\"56\"/>\n" +
                "<pitch-count count=\"14\"/>\n" +
                "<balls balls=\"4\"/>\n" +
                "<strikes strikes=\"10\"/>\n" +
                "<batters-faced batters-faced=\"2\" season=\"315\"/>\n" +
                "<opponent-at-bats at-bats=\"2\" season=\"273\"/>\n" +
                "<ground-balls number=\"0\" season=\"84\"/>\n" +
                "<fly-balls number=\"1\" season=\"95\"/>\n" +
                "<inherited-runners runners=\"0\"/>\n" +
                "<inherited-stranded stranded=\"0\"/>\n" +
                "<hit-batsmen number=\"0\" season=\"2\"/>\n" +
                "<balks number=\"0\" season=\"0\"/>\n" +
                "<wild-pitches number=\"0\" season=\"4\"/>\n" +
                "<earned-run-average average=\"5.04\"/>\n" +
                "<opponent-batting-average average=\".249\"/>\n" +
                "<entered-game inning=\"1\"/>\n" +
                "<doubles number=\"0\" season=\"15\"/>\n" +
                "<triples number=\"0\" season=\"2\"/>\n" +
                "<gidp number=\"0\" season=\"3\"/>\n" +
                "<stolen-bases number=\"0\" season=\"9\"/>\n" +
                "<pickoffs number=\"0\" season=\"0\"/>\n" +
                "<walks-and-hits-per-inning average=\"1.51\"/>\n" +
                "</baseball-mlb-boxscore-pitching-lineup>\n" +
                "</baseball-mlb-boxscore-home-team-pitching-lineup>" +
                "</MLB-event>\n";

//        List<FantasyPointUpdateEvent> events = updateProcessor.process(statData);
//
//        assertTrue(events.isEmpty());
        fail("Re-evaluate");
    }

    @Test
    public void testProcessMessage_FinalBoxScore() {
        startHazelcast();

        athlete1 = new Athlete(253985, "JA", "Happ", team, "1");
        Ebean.save(athlete1);

        sportEvent = new SportEvent(1284165, League.MLB, new Date(), "test", "test", 9, false, 2014, -1, 1);
        sportEvent.setTeams(teams);
        Ebean.save(sportEvent);

        athleteSportEventInfo = new AthleteSportEventInfo(sportEvent, athlete1, new BigDecimal("0.00"), "{}", "[]");
        Ebean.save(athleteSportEventInfo);

        String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<MLB-event>\n" +
                "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                "   <gamestate>\n" +
                "      <game status=\"Final\" status-id=\"2\" reason=\"\" inning=\"9\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"9\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                "   </gamestate>" +
                "   <double-header double-header=\"false\"/>\n" +
                "   <double-header-game-number number=\"0\"/>\n" +
                "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                "   <league id=\"2\" league=\"NL\"/>\n" +
                "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                "            <event-details>\n" +
                "               <event sequence=\"1\" id=\"1\" code=\"96\" name=\"Lineup Change\" balls=\"0\" strikes=\"0\" outs-bef=\"0\" outs-aft=\"0\" rbi=\"0\" segment-number=\"\" segment-division=\"\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"0\" vis-score=\"0\" home-score=\"0\" team-id=\"3\" team-global-id=\"0\" team-city=\"Los Angeles\" team-name=\"Angels\" team-alias=\"LAA\"/>\n" +
                "               <player id=\"8968\" global-id=\"390597\" first-name=\"Collin\" last-name=\"Cowgill\" batting-slot=\"1\" pos-id=\"8\" pos-name=\"CF\"/>\n" +
                "               <pitch sequence=\"\"/>\n" +
                "               <description text=\"\"/>\n" +
                "            </event-details>" +
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
                "       <total-innings total=\"7\"/>\n" +
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
                "       <total-innings total=\"7\"/>\n" +
                "     </baseball-mlb-boxscore-team-stats>\n" +
                "   </baseball-mlb-boxscore-visiting-team-stats>\n" +
                "   <baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "   </baseball-mlb-boxscore-home-team-batting-lineup>\n" +
                "<baseball-mlb-boxscore-home-team-pitching-lineup>\n" +
                "<baseball-mlb-boxscore-pitching-lineup>\n" +
                "<name first-name=\"J.A.\" last-name=\"Happ\"/>\n" +
                "<player-code id=\"8061\" global-id=\"253985\" uniform=\"48\"/>\n" +
                "<throwing-hand hand=\"L\"/>\n" +
                "<sequence number=\"1\"/>\n" +
                "<wins number=\"4\"/>\n" +
                "<losses number=\"5\"/>\n" +
                "<saves saves=\"0\"/>\n" +
                "<innings-pitched innings=\"0.2\" season=\"69.2\"/>\n" +
                "<games-pitched games=\"15\" starts=\"15\"/>\n" +
                "<hits hits=\"0\" season=\"68\"/>\n" +
                "<runs runs=\"0\" season=\"44\"/>\n" +
                "<earned-runs earned-runs=\"0\" season=\"39\"/>\n" +
                "<home-runs-allowed home-runs=\"0\" season=\"7\"/>\n" +
                "<walks walks=\"0\" season=\"36\"/>\n" +
                "<strike-outs strike-outs=\"1\" season=\"56\"/>\n" +
                "<pitch-count count=\"14\"/>\n" +
                "<balls balls=\"4\"/>\n" +
                "<strikes strikes=\"10\"/>\n" +
                "<batters-faced batters-faced=\"2\" season=\"315\"/>\n" +
                "<opponent-at-bats at-bats=\"2\" season=\"273\"/>\n" +
                "<ground-balls number=\"0\" season=\"84\"/>\n" +
                "<fly-balls number=\"1\" season=\"95\"/>\n" +
                "<inherited-runners runners=\"0\"/>\n" +
                "<inherited-stranded stranded=\"0\"/>\n" +
                "<hit-batsmen number=\"0\" season=\"2\"/>\n" +
                "<balks number=\"0\" season=\"0\"/>\n" +
                "<wild-pitches number=\"0\" season=\"4\"/>\n" +
                "<earned-run-average average=\"5.04\"/>\n" +
                "<opponent-batting-average average=\".249\"/>\n" +
                "<entered-game inning=\"1\"/>\n" +
                "<doubles number=\"0\" season=\"15\"/>\n" +
                "<triples number=\"0\" season=\"2\"/>\n" +
                "<gidp number=\"0\" season=\"3\"/>\n" +
                "<stolen-bases number=\"0\" season=\"9\"/>\n" +
                "<pickoffs number=\"0\" season=\"0\"/>\n" +
                "<walks-and-hits-per-inning average=\"1.51\"/>\n" +
                "</baseball-mlb-boxscore-pitching-lineup>\n" +
                "</baseball-mlb-boxscore-home-team-pitching-lineup>" +
                "</MLB-event>\n";

//        List<FantasyPointUpdateEvent> events = updateProcessor.process(statData);
//
//        SportEvent updatedSportEvent = SportEvent.findByStatsId(1284165);
//        assertTrue(updatedSportEvent.isComplete());
//
//        assertTrue(events.isEmpty());
        fail("Re-evaluate");
    }

    @Test
    public void testShouldProcessMessage_RosterMessage() {
        DocumentBuilder documentBuilder;

        try {
            String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<MLB-event>\n" +
                    "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                    "   <gamestate>\n" +
                    "      <game status=\"Pre-Game\" status-id=\"0\" reason=\"\" inning=\"9\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"9\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                    "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                    "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                    "   </gamestate>" +
                    "   <double-header double-header=\"false\"/>\n" +
                    "   <double-header-game-number number=\"0\"/>\n" +
                    "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                    "   <league id=\"2\" league=\"NL\"/>\n" +
                    "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                    "</MLB-event>\n";

            InputSource source = new InputSource(new StringReader(statData));

            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(source);

            assertTrue(!updateProcessor.shouldProcessMessage(doc));
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }

//    @Test
    public void testShouldProcessMessage_LineupMessage() {
        DocumentBuilder documentBuilder;

        try {
            String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<MLB-event>\n" +
                    "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                    "   <gamestate>\n" +
                    "      <game status=\"Pre-Game\" status-id=\"0\" reason=\"\" inning=\"9\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"9\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                    "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                    "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                    "   </gamestate>" +
                    "   <double-header double-header=\"false\"/>\n" +
                    "   <double-header-game-number number=\"0\"/>\n" +
                    "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                    "   <league id=\"2\" league=\"NL\"/>\n" +
                    "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                    "            <event-details>\n" +
                    "  <event sequence=\"16\" id=\"1\" code=\"97\" name=\"Lineup Change\" balls=\"0\" strikes=\"0\" outs-bef=\"0\" outs-aft=\"0\" rbi=\"0\" segment-number=\"\" segment-division=\"\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"0\" vis-score=\"0\" home-score=\"0\" team-id=\"14\" team-global-id=\"0\" team-city=\"Toronto\" team-name=\"Blue Jays\" team-alias=\"Tor\"/>\n" +
                    "  <player id=\"7835\" global-id=\"201169\" first-name=\"Rajai\" last-name=\"Davis\" batting-slot=\"6\" pos-id=\"10\" pos-name=\"DH\"/>\n" +
                    "  <pitch sequence=\"\"/>\n" +
                    "  <description text=\"\"/>\n" +
                    " </event-details>" +
                    "</MLB-event>\n";

            InputSource source = new InputSource(new StringReader(statData));

            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(source);

            assertTrue(!updateProcessor.shouldProcessMessage(doc));
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }

//    @Test
    public void testShouldProcessMessage_SinglePitchMessage() {
        DocumentBuilder documentBuilder;

        try {
            String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<MLB-event>\n" +
                    "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                    "   <gamestate>\n" +
                    "      <game status=\"In-Progress\" status-id=\"2\" reason=\"\" inning=\"9\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"9\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                    "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                    "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                    "   </gamestate>" +
                    "   <double-header double-header=\"false\"/>\n" +
                    "   <double-header-game-number number=\"0\"/>\n" +
                    "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                    "   <league id=\"2\" league=\"NL\"/>\n" +
                    "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                    "            <event-details>\n" +
                    "               <event sequence=\"1\" id=\"9\" code=\"96\" name=\"Pitches Only\" balls=\"0\" strikes=\"0\" outs-bef=\"0\" outs-aft=\"0\" rbi=\"0\" segment-number=\"\" segment-division=\"\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"0\" vis-score=\"0\" home-score=\"0\" team-id=\"3\" team-global-id=\"0\" team-city=\"Los Angeles\" team-name=\"Angels\" team-alias=\"LAA\"/>\n" +
                    "               <player id=\"8968\" global-id=\"390597\" first-name=\"Collin\" last-name=\"Cowgill\" batting-slot=\"1\" pos-id=\"8\" pos-name=\"CF\"/>\n" +
                    "               <pitch sequence=\"\"/>\n" +
                    "               <description text=\"\"/>\n" +
                    "            </event-details>" +
                    "</MLB-event>\n";

            InputSource source = new InputSource(new StringReader(statData));

            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(source);

            assertTrue(!updateProcessor.shouldProcessMessage(doc));
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }

//    @Test
    public void testShouldProcessMessage_GameEventMessage() {
        DocumentBuilder documentBuilder;

        try {
            String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<MLB-event>\n" +
                    "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                    "   <gamestate>\n" +
                    "      <game status=\"Final\" status-id=\"2\" reason=\"\" inning=\"9\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"9\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                    "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                    "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                    "   </gamestate>" +
                    "   <double-header double-header=\"false\"/>\n" +
                    "   <double-header-game-number number=\"0\"/>\n" +
                    "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                    "   <league id=\"2\" league=\"NL\"/>\n" +
                    "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                    "            <event-details>\n" +
                    "               <event sequence=\"1\" id=\"1\" code=\"96\" name=\"Lineup Change\" balls=\"0\" strikes=\"0\" outs-bef=\"0\" outs-aft=\"0\" rbi=\"0\" segment-number=\"\" segment-division=\"\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"0\" vis-score=\"0\" home-score=\"0\" team-id=\"3\" team-global-id=\"0\" team-city=\"Los Angeles\" team-name=\"Angels\" team-alias=\"LAA\"/>\n" +
                    "               <player id=\"8968\" global-id=\"390597\" first-name=\"Collin\" last-name=\"Cowgill\" batting-slot=\"1\" pos-id=\"8\" pos-name=\"CF\"/>\n" +
                    "               <pitch sequence=\"\"/>\n" +
                    "               <description text=\"\"/>\n" +
                    "            </event-details>" +
                    "</MLB-event>\n";

            InputSource source = new InputSource(new StringReader(statData));

            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(source);

            assertTrue(updateProcessor.shouldProcessMessage(doc));
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }

//    @Test
    public void testShouldProcessMessage_FinalBoxScoreMessage() {
        DocumentBuilder documentBuilder;

        try {
            String statData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<MLB-event>\n" +
                    "   <gamecode  code=\"330912119\" global-id=\"1284165\"/>\n" +
                    "   <gamestate>\n" +
                    "      <game status=\"Final\" status-id=\"2\" reason=\"\" inning=\"9\" balls=\"\" strikes=\"\" outs=\"\" segment-number=\"9\" segment-division=\"0\" active-state=\"false\" restart=\"false\" pitch-sequence=\"\"/>\n" +
                    "      <batter id=\"\" global-id=\"\" first-name=\"\" last-name=\"\" batting-slot=\"\"/>\n" +
                    "      <pitcher id=\"\" global-id=\"\" first-name=\"\" last-name=\"\"/>\n" +
                    "   </gamestate>" +
                    "   <double-header double-header=\"false\"/>\n" +
                    "   <double-header-game-number number=\"0\"/>\n" +
                    "   <gametype id=\"1\" type=\"Regular Season\"/>\n" +
                    "   <league id=\"2\" league=\"NL\"/>\n" +
                    "   <stadium name=\"Dodger Stadium\" city=\"Los Angeles\" state=\"California\"/>\n" +
                    "            <event-details>\n" +
                    "               <event sequence=\"1\" id=\"1\" code=\"96\" name=\"Lineup Change\" balls=\"0\" strikes=\"0\" outs-bef=\"0\" outs-aft=\"0\" rbi=\"0\" segment-number=\"\" segment-division=\"\" dir=\"\" dist=\"\" bat-type=\"\" scored=\"false\" base-sit-bef=\"0\" base-sit-aft=\"0\" vis-score=\"0\" home-score=\"0\" team-id=\"3\" team-global-id=\"0\" team-city=\"Los Angeles\" team-name=\"Angels\" team-alias=\"LAA\"/>\n" +
                    "               <player id=\"8968\" global-id=\"390597\" first-name=\"Collin\" last-name=\"Cowgill\" batting-slot=\"1\" pos-id=\"8\" pos-name=\"CF\"/>\n" +
                    "               <pitch sequence=\"\"/>\n" +
                    "               <description text=\"\"/>\n" +
                    "            </event-details>" +
                    "</MLB-event>\n";

            InputSource source = new InputSource(new StringReader(statData));

            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(source);

            assertTrue(updateProcessor.shouldProcessMessage(doc));
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }
}
