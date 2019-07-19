package stats.updateprocessor.nfl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.DaoFactory;
import dao.ISportsDao;
import dao.SportsDao;
import models.sports.*;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import stats.updateprocessor.FantasyPointAthleteUpdateEvent;
import stats.updateprocessor.FantasyPointUpdateEvent;
import utilities.BaseTest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by dmaclean on 7/25/14.
 */
public class UpdateProcessorTest extends BaseTest {

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static XPathFactory xpathFactory = XPathFactory.newInstance();
    private static XPath xPath = xpathFactory.newXPath();
    Team team;
    Team team2;
    SportEvent sportEvent;
    Athlete runningBack;
    Athlete offensiveTeam;
    Athlete defensiveTeam;
    AthleteSportEventInfo athleteSportEventInfoRunningBack;
    AthleteSportEventInfo athleteSportEventInfoOffensiveTeam;
    AthleteSportEventInfo athleteSportEventInfoDefensiveTeam;
    ISportsDao sportsDao;
    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {
    };
    private UpdateProcessor processor;

    @Before
    public void setUp() {
        sportsDao = new SportsDao();
        processor = context.getBean("NFLStatsUpdateProcessor", UpdateProcessor.class);

        team = new Team(League.NFL, "", "", "", 123);
        sportsDao.saveTeam(team);
        team2 = new Team(League.NFL, "", "", "", 456);
        sportsDao.saveTeam(team2);

        sportEvent = new SportEvent(1, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
        sportsDao.saveSportEvent(sportEvent);

        runningBack = new Athlete(123456, "Running", "Back", team, "1");
        sportsDao.saveAthlete(runningBack);
        offensiveTeam = new Athlete(123, "Offensive", "Team", team, "1");
        sportsDao.saveAthlete(offensiveTeam);
        defensiveTeam = new Athlete(456, "Defensive", "Team", team2, "1");
        sportsDao.saveAthlete(defensiveTeam);

        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
    }

    @After
    public void tearDown() {
        processor = null;
    }

//    @Test
//    public void testShouldProcessMessage_Roster() {
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131117004\" global-id=\"1321883\"/>\n" +
//                " <coverage level=\"\"/>\n" +
//                " <gamestate status=\"4\" quarter=\"1\" minutes=\"15\" seconds=\"00\" team-possession-id=\"\" team-possession-global-id=\"\" yards-from-goal=\"0\" down=\"0\" distance=\"0\" segment-number=\"1\" active-state=\"false\" restart=\"false\" under-review=\"false\"/>\n" +
//                " <gametype type=\"1\" name=\"Regular Season\"/>\n" +
//                " <stadium name=\"Paul Brown Stadium\" city=\"Cincinnati\" state=\"Ohio\"/>" +
//                "</nfl-event>";
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            assertTrue(!processor.shouldProcessMessage(doc));
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testShouldProcessMessage_Play() {
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131117004\" global-id=\"1321883\"/>\n" +
//                " <coverage level=\"\"/>\n" +
//                " <gamestate status=\"4\" quarter=\"1\" minutes=\"15\" seconds=\"00\" team-possession-id=\"\" team-possession-global-id=\"\" yards-from-goal=\"0\" down=\"0\" distance=\"0\" segment-number=\"1\" active-state=\"false\" restart=\"false\" under-review=\"false\"/>\n" +
//                " <gametype type=\"1\" name=\"Regular Season\"/>\n" +
//                " <stadium name=\"Paul Brown Stadium\" city=\"Cincinnati\" state=\"Ohio\"/>" +
//                "<play id=\"1\">" +
//                "</play>" +
//                "</nfl-event>";
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            assertTrue(processor.shouldProcessMessage(doc));
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testShouldProcessMessage_GameSummary() {
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131117004\" global-id=\"1321883\"/>\n" +
//                " <coverage level=\"\"/>\n" +
//                " <gamestate status=\"4\" quarter=\"1\" minutes=\"15\" seconds=\"00\" team-possession-id=\"\" team-possession-global-id=\"\" yards-from-goal=\"0\" down=\"0\" distance=\"0\" segment-number=\"1\" active-state=\"false\" restart=\"false\" under-review=\"false\"/>\n" +
//                " <gametype type=\"1\" name=\"Regular Season\"/>\n" +
//                " <stadium name=\"Paul Brown Stadium\" city=\"Cincinnati\" state=\"Ohio\"/>" +
//                "<play id=\"1\">" +
//                "</play>" +
//                "</nfl-event>";
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            assertTrue(processor.shouldProcessMessage(doc));
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateSportEventStatus_InProgress() {
//        SportEvent sportEvent = new SportEvent(1, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131117004\" global-id=\"1321883\"/>\n" +
//                " <coverage level=\"\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"10\" seconds=\"00\" team-possession-id=\"\" team-possession-global-id=\"\" yards-from-goal=\"0\" down=\"0\" distance=\"0\" segment-number=\"1\" active-state=\"false\" restart=\"false\" under-review=\"false\"/>\n" +
//                " <gametype type=\"1\" name=\"Regular Season\"/>\n" +
//                " <stadium name=\"Paul Brown Stadium\" city=\"Cincinnati\" state=\"Ohio\"/>" +
//                "<play id=\"1\">" +
//                "</play>" +
//                "</nfl-event>";
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            processor.updateSportEventStatus(doc, fantasyPointUpdateEvent);
//
//            assertTrue(!sportEvent.isComplete());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateSportEventStatus_Complete() {
//        SportEvent sportEvent = new SportEvent(1, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131117004\" global-id=\"1321883\"/>\n" +
//                " <coverage level=\"\"/>\n" +
//                " <gamestate status=\"4\" quarter=\"4\" minutes=\"0\" seconds=\"00\" team-possession-id=\"\" team-possession-global-id=\"\" yards-from-goal=\"0\" down=\"0\" distance=\"0\" segment-number=\"1\" active-state=\"false\" restart=\"false\" under-review=\"false\"/>\n" +
//                " <gametype type=\"1\" name=\"Regular Season\"/>\n" +
//                " <stadium name=\"Paul Brown Stadium\" city=\"Cincinnati\" state=\"Ohio\"/>" +
//                "<play id=\"1\">" +
//                "</play>" +
//                "</nfl-event>";
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            processor.updateSportEventStatus(doc, fantasyPointUpdateEvent);
//
//            assertEquals(sportEvent.isComplete(), true);
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateTimeline_FirstEntry() {
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(null, null, new BigDecimal("0"), "[]", "[]");
//        athleteSportEventInfo.setId(1);
//
//        try {
//            FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
//            fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfo);
//            fantasyPointAthleteUpdateEvent.setTimeline(mapper.writeValueAsString(new ArrayList<>()));
//            fantasyPointAthleteUpdateEvent.setFantasyPointDelta(new BigDecimal("4"));
//
//            String eventDescription = "Some cool shit just happened.";
//
//            processor.updateTimeline(Arrays.asList(fantasyPointAthleteUpdateEvent), eventDescription, "1", false);  // TODO putting false as a default?
//
//            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
//            };
//            List<Map<String, Object>> timeline = mapper.readValue(fantasyPointAthleteUpdateEvent.getTimeline(), typeRef);
//
//            assertEquals(timeline.size(), 1);
//            assertEquals(timeline.get(0).get("timestamp") instanceof Long, true);
//            assertEquals(timeline.get(0).get("description"), eventDescription);
//            assertEquals(timeline.get(0).get("fpChange"), "+4");
//            assertEquals(timeline.get(0).get("athleteSportEventInfoId"), athleteSportEventInfo.getId());
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateTimeline_SecondEntry() {
//        List<Map<String, Object>> timeline = new ArrayList<>();
//        Map<String, Object> timelineEntry = new HashMap<>();
//        timelineEntry.put("timestamp", 123L);
//        timelineEntry.put("description", "First entry");
//        timelineEntry.put("fpChange", "+2");
//        timelineEntry.put("athleteSportEventInfoId", 1);
//        timeline.add(timelineEntry);
//
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(null, null, new BigDecimal("0"), "[]", "[]");
//        athleteSportEventInfo.setId(1);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfo);
//        fantasyPointAthleteUpdateEvent.setFantasyPointDelta(new BigDecimal("4"));
//
//        String eventDescription = "Some cool shit just happened.";
//
//        try {
//            fantasyPointAthleteUpdateEvent.setTimeline(mapper.writeValueAsString(timeline));
//            athleteSportEventInfo.setTimeline(mapper.writeValueAsString(timeline));
//            processor.updateTimeline(Arrays.asList(fantasyPointAthleteUpdateEvent), eventDescription, "1", false);
//
//            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
//            };
//            timeline = mapper.readValue(fantasyPointAthleteUpdateEvent.getTimeline(), typeRef);
//
//            assertEquals(timeline.size(), 2);
//            assertEquals(timeline.get(0).get("timestamp") instanceof Long, true);
//            assertEquals(timeline.get(0).get("description"), eventDescription);
//            assertEquals(timeline.get(0).get("fpChange"), "+4");
//            assertEquals(timeline.get(0).get("athleteSportEventInfoId"), athleteSportEventInfo.getId());
//
//            assertEquals(timeline.get(1).get("timestamp"), 123);
//            assertEquals(timeline.get(1).get("description"), "First entry");
//            assertEquals(timeline.get(1).get("fpChange"), "+2");
//            assertEquals(timeline.get(1).get("athleteSportEventInfoId"), athleteSportEventInfo.getId());
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateTimeline_NoFPUpdate() {
//        AthleteSportEventInfo athleteSportEventInfo = new AthleteSportEventInfo(null, null, new BigDecimal("0"), "[]", "[]");
//        athleteSportEventInfo.setId(1);
//
//        try {
//            FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
//            fantasyPointAthleteUpdateEvent.setTimeline(mapper.writeValueAsString(new ArrayList<>()));
//            fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfo);
//            fantasyPointAthleteUpdateEvent.setFantasyPointDelta(null);
//
//            String eventDescription = "Some cool shit just happened.";
//
//            processor.updateTimeline(Arrays.asList(fantasyPointAthleteUpdateEvent), eventDescription, "1", false);
//
//            assertEquals("[]", fantasyPointAthleteUpdateEvent.getTimeline());
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateFantasyPointChange_Rush_NoScore() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"21\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent2 = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent2.setAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent3 = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent3.setAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            Element play = (Element) xPath.evaluate("//nfl-event/play", doc, XPathConstants.NODE);
//            NodeList statIds = (NodeList) xPath.evaluate("//stat-id", play, XPathConstants.NODESET);
//            Element statId = (Element) statIds.item(0);
//
//            Map<String, Object> extraData = new HashMap<>();
//            extraData.put("play", play);
//            extraData.put("statId", statId);
//
//            processor.updateFantasyPointChange(Arrays.asList(fantasyPointAthleteUpdateEvent, fantasyPointAthleteUpdateEvent2, fantasyPointAthleteUpdateEvent3), doc, extraData);
//
//            assertEquals(0, fantasyPointAthleteUpdateEvent.getFantasyPointDelta().compareTo(new BigDecimal("0.6")));
//            assertEquals(BigDecimal.ZERO, fantasyPointAthleteUpdateEvent2.getFantasyPointDelta());
//            assertEquals(BigDecimal.ZERO, fantasyPointAthleteUpdateEvent3.getFantasyPointDelta());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateFantasyPointChange_Rush_Score() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"2\" minutes=\"3\" seconds=\"43\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"2\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"1567.0000000000000001\" sequence=\"143\" quarter=\"2\"  time=\"3:43\"  down=\"1\"  end-down=\"1\"  distance=\"3\"  end-distance=\"0\"  away-score-before=\"14\"  home-score-before=\"3\"  away-score-after=\"14\"  home-score-after=\"9\"  yards-from-goal=\"3\"  end-yards-from-goal=\"0\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"3\" direction=\"M\" points=\"6\" rush-type-id=\"7\" rush-type-name=\"Middle\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"9\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"9\"/>\n" +
//                "  </linescore>\n" +
//                " <first-downs number=\"7\" rushing=\"2\" passing=\"5\" penalty=\"0\"/>\n" +
//                " <rushing attempts=\"14\" yards=\"22\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"16\" seconds=\"17\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"14\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"7\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"7\"/>\n" +
//                "  </linescore>\n" +
//                " <time-of-possession minutes=\"10\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"10\" yards=\"16\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent2 = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent2.setAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent3 = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent3.setAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            Element play = (Element) xPath.evaluate("//nfl-event/play", doc, XPathConstants.NODE);
//            NodeList statIds = (NodeList) xPath.evaluate("//stat-id", play, XPathConstants.NODESET);
//            Element statId = (Element) statIds.item(0);
//
//            Map<String, Object> extraData = new HashMap<>();
//            extraData.put("play", play);
//            extraData.put("statId", statId);
//
//            processor.updateFantasyPointChange(Arrays.asList(fantasyPointAthleteUpdateEvent, fantasyPointAthleteUpdateEvent2, fantasyPointAthleteUpdateEvent3),
//                    doc, extraData);
//
//            assertEquals(0, fantasyPointAthleteUpdateEvent.getFantasyPointDelta().compareTo(new BigDecimal("6.3")));
//            assertEquals(0, fantasyPointAthleteUpdateEvent2.getFantasyPointDelta().compareTo(BigDecimal.ZERO));
//            assertEquals(BigDecimal.ZERO, fantasyPointAthleteUpdateEvent3.getFantasyPointDelta());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateFantasyPointChange_KickReturn_Score() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321731, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(216407, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(363, "Offensive", "Team", team, "1");       // Away
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(326, "Defensive", "Team", team2, "1");      // Home
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131020028\" global-id=\"1321731\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"2\" minutes=\"6\" seconds=\"15\" team-possession-id=\"3\" team-possession-global-id=\"326\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"2\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"1577.0000000000000002\" sequence=\"154\" quarter=\"2\"  time=\"6:15\"  down=\"4\"  end-down=\"1\"  distance=\"11\"  end-distance=\"0\"  away-score-before=\"10\"  home-score-before=\"17\"  away-score-after=\"16\"  home-score-after=\"17\"  yards-from-goal=\"72\"  end-yards-from-goal=\"100\"  possession=\"28\"  possession-global-id=\"363\"  end-possession=\"3\"  end-possession-global-id=\"326\"  event-type=\"5\"  continuation=\"false\"  details=\"Sav Rocca punts for 53 yards. Devin Hester return for 81 yards to Was0 for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"22\" description=\"Punt\" team-id=\"28\" player-id=\"8249\" global-team-id=\"363\" global-player-id=\"379102\" yards=\"53\"/>\n" +
//                "<stat-id id=\"5\" description=\"Punt Return\" team-id=\"3\" player-id=\"7806\" global-team-id=\"326\" global-player-id=\"216407\" yards=\"81\" points=\"6\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Redskins\" alias=\"Was\"/>\n" +
//                "  <team-city city=\"Washington\"/>\n" +
//                "  <team-code id=\"28\" global-id=\"363\"/>\n" +
//                "  <linescore score=\"17\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"3\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"14\"/>\n" +
//                "  </linescore>\n" +
//                " <punting punts=\"3\" yards=\"122\" average=\"40.7\"/>\n" +
//                " <time-of-possession minutes=\"16\" seconds=\"13\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Bears\" alias=\"Chi\"/>\n" +
//                "  <team-city city=\"Chicago\"/>\n" +
//                "  <team-code id=\"3\" global-id=\"326\"/>\n" +
//                "  <linescore score=\"16\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"10\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"6\"/>\n" +
//                "  </linescore>\n" +
//                " <fourth-down-efficiency made=\"0\" attempts=\"0\" percent=\"-\"/>\n" +
//                " <return-totals yards=\"156\" tds=\"1\"/>\n" +
//                " <punt-returns attempts=\"2\" yards=\"81\" tds=\"1\" long=\"81\" long-TD=\"true\"/>\n" +
//                " <time-of-possession minutes=\"7\" seconds=\"32\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"379102\" id=\"8249\"/>\n" +
//                " <punting punts=\"3\" yards=\"122\" blocked=\"0\" long=\"53\" in20=\"0\" touchbacks=\"0\" returns=\"2\" return-yards=\"81\" average=\"40.7\" net-average=\"13.7\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"216407\" id=\"7806\"/>\n" +
//                " <punt-returning returns=\"2\" yards=\"81\" long=\"81\" long-td=\"true\" tds=\"1\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent2 = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent2.setAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent3 = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent3.setAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            Element play = (Element) xPath.evaluate("//nfl-event/play", doc, XPathConstants.NODE);
//            NodeList statIds = (NodeList) xPath.evaluate("//stat-id", play, XPathConstants.NODESET);
//            Element statId = (Element) statIds.item(1);
//
//            Map<String, Object> extraData = new HashMap<>();
//            extraData.put("play", play);
//            extraData.put("statId", statId);
//
//            processor.updateFantasyPointChange(Arrays.asList(fantasyPointAthleteUpdateEvent, fantasyPointAthleteUpdateEvent2, fantasyPointAthleteUpdateEvent3),
//                    doc, extraData);
//
//            assertEquals(new BigDecimal("6.00"), fantasyPointAthleteUpdateEvent.getFantasyPointDelta());
//            assertEquals(BigDecimal.ZERO, fantasyPointAthleteUpdateEvent2.getFantasyPointDelta());
//            assertEquals(new BigDecimal("6.00"), fantasyPointAthleteUpdateEvent3.getFantasyPointDelta());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateFantasyPointChange_FieldGoalMade() {
//        team = new Team(League.NFL, "", "", "", 363);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 326);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321731, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(331927, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(363, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(326, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131020028\" global-id=\"1321731\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"11\" seconds=\"33\" team-possession-id=\"28\" team-possession-global-id=\"363\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"217.0000000000000001\" sequence=\"19\" quarter=\"1\"  time=\"11:33\"  down=\"4\"  end-down=\"1\"  distance=\"12\"  end-distance=\"0\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"3\"  yards-from-goal=\"20\"  end-yards-from-goal=\"0\"  possession=\"28\"  possession-global-id=\"363\"  end-possession=\"28\"  end-possession-global-id=\"363\"  event-type=\"3\"  continuation=\"false\"  details=\"Kai Forbath 38 yard field goal attempt is GOOD. Holder: Sav Rocca.\" >\n" +
//                "<stat-id id=\"3\" description=\"FGA\" team-id=\"28\" player-id=\"25648\" global-team-id=\"363\" global-player-id=\"331927\" yards=\"38\" points=\"3\"/>\n" +
//                "<stat-id id=\"34\" description=\"Made FG\" team-id=\"28\" player-id=\"25648\" global-team-id=\"363\" global-player-id=\"331927\" yards=\"38\"/>\n" +
//                "<stat-id id=\"36\" description=\"Holder\" team-id=\"28\" player-id=\"8249\" global-team-id=\"363\" global-player-id=\"379102\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Redskins\" alias=\"Was\"/>\n" +
//                "  <team-city city=\"Washington\"/>\n" +
//                "  <team-code id=\"28\" global-id=\"363\"/>\n" +
//                "  <linescore score=\"3\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"3\"/>\n" +
//                "  </linescore>\n" +
//                " <fourth-down-efficiency made=\"0\" attempts=\"0\" percent=\"-\"/>\n" +
//                " <time-of-possession minutes=\"3\" seconds=\"27\"/>\n" +
//                " <field-goals made=\"1\" attempts=\"1\" blocked=\"0\" long=\"38\" percent=\"100\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Bears\" alias=\"Chi\"/>\n" +
//                "  <team-city city=\"Chicago\"/>\n" +
//                "  <team-code id=\"3\" global-id=\"326\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"331927\" id=\"25648\"/>\n" +
//                " <field-goals made=\"1\" attempts=\"1\" blocked=\"0\" long=\"38\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent2 = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent2.setAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//
//        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent3 = new FantasyPointAthleteUpdateEvent();
//        fantasyPointAthleteUpdateEvent3.setAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            Element play = (Element) xPath.evaluate("//nfl-event/play", doc, XPathConstants.NODE);
//            NodeList statIds = (NodeList) xPath.evaluate("//stat-id", play, XPathConstants.NODESET);
//            Element statId = (Element) statIds.item(1);
//
//            Map<String, Object> extraData = new HashMap<>();
//            extraData.put("play", play);
//            extraData.put("statId", statId);
//
//            processor.updateFantasyPointChange(Arrays.asList(fantasyPointAthleteUpdateEvent, fantasyPointAthleteUpdateEvent2, fantasyPointAthleteUpdateEvent3),
//                    doc, extraData);
//
//            assertEquals(BigDecimal.ZERO, fantasyPointAthleteUpdateEvent.getFantasyPointDelta());
//            assertEquals(BigDecimal.ZERO, fantasyPointAthleteUpdateEvent2.getFantasyPointDelta());
//            assertEquals(BigDecimal.ZERO, fantasyPointAthleteUpdateEvent3.getFantasyPointDelta());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcessEventDetails_Rush_NoScore() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"21\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        DocumentBuilder documentBuilder;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//            fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//            processor.processEventDetails(fantasyPointUpdateEvent, doc);
//
//            assertEquals(3, fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().size());
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0);
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1);
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2);
//
//            // Athlete FP delta
//            assertEquals(new BigDecimal("0.60"), runningBackFPAUE.getFantasyPointDelta());
//
//            // Athlete Timeline
//            List<Map<String, Object>> timeline = mapper.readValue(runningBackFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.", timeline.get(0).get("description"));
//            assertEquals("+0.6", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoRunningBack.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//            // Home Team FP delta
//            assertEquals(new BigDecimal("0.0"), homeTeamFPAUE.getFantasyPointDelta());
//
//            // Home Team timeline
//            timeline = mapper.readValue(homeTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(0, timeline.size());
//
//            // Away Team FP delta
//            assertEquals(new BigDecimal("0.0"), awayTeamFPAUE.getFantasyPointDelta());
//
//            // Away Team timeline
//            timeline = mapper.readValue(awayTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(0, timeline.size());
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcessEventDetails_Rush_Score() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"2\" minutes=\"3\" seconds=\"43\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"2\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"1567.0000000000000001\" sequence=\"143\" quarter=\"2\"  time=\"3:43\"  down=\"1\"  end-down=\"1\"  distance=\"3\"  end-distance=\"0\"  away-score-before=\"14\"  home-score-before=\"3\"  away-score-after=\"14\"  home-score-after=\"9\"  yards-from-goal=\"3\"  end-yards-from-goal=\"0\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"3\" direction=\"M\" points=\"6\" rush-type-id=\"7\" rush-type-name=\"Middle\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"9\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"9\"/>\n" +
//                "  </linescore>\n" +
//                " <first-downs number=\"7\" rushing=\"2\" passing=\"5\" penalty=\"0\"/>\n" +
//                " <rushing attempts=\"14\" yards=\"22\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"16\" seconds=\"17\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"14\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"7\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"7\"/>\n" +
//                "  </linescore>\n" +
//                " <time-of-possession minutes=\"10\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"10\" yards=\"16\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            processor.processEventDetails(fantasyPointUpdateEvent, doc);
//
//            assertEquals(3, fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().size());
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0);
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1);
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2);
//
//            // Athlete FP delta
//            assertEquals(new BigDecimal("6.30"), runningBackFPAUE.getFantasyPointDelta());
//
//            // Athlete Timeline
//            List<Map<String, Object>> timeline = mapper.readValue(runningBackFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.", timeline.get(0).get("description"));
//            assertEquals("+6.3", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoRunningBack.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//            // Home Team FP delta
//            assertEquals(new BigDecimal("0.0"), homeTeamFPAUE.getFantasyPointDelta());
//
//            // Home Team timeline
//            timeline = mapper.readValue(homeTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(0, timeline.size());
//
//            // Away Team FP delta
//            assertEquals(new BigDecimal("-3.0"), awayTeamFPAUE.getFantasyPointDelta());
//
//            // Away Team timeline
//            timeline = mapper.readValue(awayTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.", timeline.get(0).get("description"));
//            assertEquals("-3.0", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoDefensiveTeam.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcessEventDetails_KickReturn_Score() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321731, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(216407, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(363, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(326, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131020028\" global-id=\"1321731\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"2\" minutes=\"6\" seconds=\"15\" team-possession-id=\"3\" team-possession-global-id=\"326\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"2\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"1577.0000000000000002\" sequence=\"154\" quarter=\"2\"  time=\"6:15\"  down=\"4\"  end-down=\"1\"  distance=\"11\"  end-distance=\"0\"  away-score-before=\"10\"  home-score-before=\"17\"  away-score-after=\"16\"  home-score-after=\"17\"  yards-from-goal=\"72\"  end-yards-from-goal=\"100\"  possession=\"28\"  possession-global-id=\"363\"  end-possession=\"3\"  end-possession-global-id=\"326\"  event-type=\"5\"  continuation=\"false\"  details=\"Sav Rocca punts for 53 yards. Devin Hester return for 81 yards to Was0 for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"22\" description=\"Punt\" team-id=\"28\" player-id=\"8249\" global-team-id=\"363\" global-player-id=\"379102\" yards=\"53\"/>\n" +
//                "<stat-id id=\"5\" description=\"Punt Return\" team-id=\"3\" player-id=\"7806\" global-team-id=\"326\" global-player-id=\"216407\" yards=\"81\" points=\"6\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Redskins\" alias=\"Was\"/>\n" +
//                "  <team-city city=\"Washington\"/>\n" +
//                "  <team-code id=\"28\" global-id=\"363\"/>\n" +
//                "  <linescore score=\"17\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"3\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"14\"/>\n" +
//                "  </linescore>\n" +
//                " <punting punts=\"3\" yards=\"122\" average=\"40.7\"/>\n" +
//                " <time-of-possession minutes=\"16\" seconds=\"13\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Bears\" alias=\"Chi\"/>\n" +
//                "  <team-city city=\"Chicago\"/>\n" +
//                "  <team-code id=\"3\" global-id=\"326\"/>\n" +
//                "  <linescore score=\"16\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"10\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"6\"/>\n" +
//                "  </linescore>\n" +
//                " <fourth-down-efficiency made=\"0\" attempts=\"0\" percent=\"-\"/>\n" +
//                " <return-totals yards=\"156\" tds=\"1\"/>\n" +
//                " <punt-returns attempts=\"2\" yards=\"81\" tds=\"1\" long=\"81\" long-TD=\"true\"/>\n" +
//                " <time-of-possession minutes=\"7\" seconds=\"32\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"379102\" id=\"8249\"/>\n" +
//                " <punting punts=\"3\" yards=\"122\" blocked=\"0\" long=\"53\" in20=\"0\" touchbacks=\"0\" returns=\"2\" return-yards=\"81\" average=\"40.7\" net-average=\"13.7\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"216407\" id=\"7806\"/>\n" +
//                " <punt-returning returns=\"2\" yards=\"81\" long=\"81\" long-td=\"true\" tds=\"1\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            processor.processEventDetails(fantasyPointUpdateEvent, doc);
//
//            assertEquals(3, fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().size());
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0);
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1);
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2);
//
//            // Athlete FP delta
//            assertEquals(new BigDecimal("6.00"), runningBackFPAUE.getFantasyPointDelta());
//
//            // Athlete Timeline
//            List<Map<String, Object>> timeline = mapper.readValue(runningBackFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Sav Rocca punts for 53 yards. Devin Hester return for 81 yards to Was0 for a TOUCHDOWN.", timeline.get(0).get("description"));
//            assertEquals("+6.0", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoRunningBack.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//            // Home Team FP delta
//            assertEquals(new BigDecimal("-3.0"), homeTeamFPAUE.getFantasyPointDelta());
//
//            // Home Team timeline
//            timeline = mapper.readValue(homeTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Sav Rocca punts for 53 yards. Devin Hester return for 81 yards to Was0 for a TOUCHDOWN.", timeline.get(0).get("description"));
//            assertEquals("-3.0", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoOffensiveTeam.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//            // Away Team FP delta
//            assertEquals(new BigDecimal("6.00"), awayTeamFPAUE.getFantasyPointDelta());
//
//            // Away Team timeline
//            timeline = mapper.readValue(awayTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Sav Rocca punts for 53 yards. Devin Hester return for 81 yards to Was0 for a TOUCHDOWN.", timeline.get(0).get("description"));
//            assertEquals("+6.0", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoDefensiveTeam.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcessEventDetails_FieldGoalMade() {
//        team = new Team(League.NFL, "", "", "", 363);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 326);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321731, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(331927, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(363, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(326, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131020028\" global-id=\"1321731\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"11\" seconds=\"33\" team-possession-id=\"28\" team-possession-global-id=\"363\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"217.0000000000000001\" sequence=\"19\" quarter=\"1\"  time=\"11:33\"  down=\"4\"  end-down=\"1\"  distance=\"12\"  end-distance=\"0\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"3\"  yards-from-goal=\"20\"  end-yards-from-goal=\"0\"  possession=\"28\"  possession-global-id=\"363\"  end-possession=\"28\"  end-possession-global-id=\"363\"  event-type=\"3\"  continuation=\"false\"  details=\"Kai Forbath 38 yard field goal attempt is GOOD. Holder: Sav Rocca.\" >\n" +
//                "<stat-id id=\"3\" description=\"FGA\" team-id=\"28\" player-id=\"25648\" global-team-id=\"363\" global-player-id=\"331927\" yards=\"38\" points=\"3\"/>\n" +
//                "<stat-id id=\"34\" description=\"Made FG\" team-id=\"28\" player-id=\"25648\" global-team-id=\"363\" global-player-id=\"331927\" yards=\"38\"/>\n" +
//                "<stat-id id=\"36\" description=\"Holder\" team-id=\"28\" player-id=\"8249\" global-team-id=\"363\" global-player-id=\"379102\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Redskins\" alias=\"Was\"/>\n" +
//                "  <team-city city=\"Washington\"/>\n" +
//                "  <team-code id=\"28\" global-id=\"363\"/>\n" +
//                "  <linescore score=\"3\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"3\"/>\n" +
//                "  </linescore>\n" +
//                " <fourth-down-efficiency made=\"0\" attempts=\"0\" percent=\"-\"/>\n" +
//                " <time-of-possession minutes=\"3\" seconds=\"27\"/>\n" +
//                " <field-goals made=\"1\" attempts=\"1\" blocked=\"0\" long=\"38\" percent=\"100\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Bears\" alias=\"Chi\"/>\n" +
//                "  <team-city city=\"Chicago\"/>\n" +
//                "  <team-code id=\"3\" global-id=\"326\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"331927\" id=\"25648\"/>\n" +
//                " <field-goals made=\"1\" attempts=\"1\" blocked=\"0\" long=\"38\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            processor.processEventDetails(fantasyPointUpdateEvent, doc);
//
//            assertEquals(3, fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().size());
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0);
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1);
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2);
//
//            // Athlete FP delta
//            assertEquals(BigDecimal.ZERO, runningBackFPAUE.getFantasyPointDelta());
//
//            // Athlete Timeline
//            List<Map<String, Object>> timeline = mapper.readValue(runningBackFPAUE.getTimeline(), listTypeReference);
//            assertEquals(0, timeline.size());
//
//            // Home Team FP delta
//            assertEquals(new BigDecimal("0.0"), homeTeamFPAUE.getFantasyPointDelta());
//
//            // Home Team timeline
//            timeline = mapper.readValue(homeTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(0, timeline.size());
//
//            // Away Team FP delta
//            assertEquals(new BigDecimal("-1.5"), awayTeamFPAUE.getFantasyPointDelta());
//
//            // Away Team timeline
//            timeline = mapper.readValue(awayTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Kai Forbath 38 yard field goal attempt is GOOD. Holder: Sav Rocca.", timeline.get(0).get("description"));
//            assertEquals("-1.5", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoDefensiveTeam.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcessEventDetails_Sack() {
//        team = new Team(League.NFL, "Arizona", "Cardinals", "ARI", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "Houston", "Texans", "HOU", 325);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321731, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(184503, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(325, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<?xml version=\"1.0\"?>\n" +
//                "<nfl-event>\n" +
//                "    <gamecode code=\"20140809022\" global-id=\"1421774\"/>\n" +
//                "    <coverage level=\"1\"/>\n" +
//                "    <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"56\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"85\" down=\"3\" distance=\"16\" segment-number=\"1\" active-state=\"true\" restart=\"false\" under-review=\"false\"/>\n" +
//                "    <play id=\"79.0000000000000001\" sequence=\"7\" quarter=\"1\" time=\"14:56\" down=\"2\" end-down=\"3\" distance=\"9\" end-distance=\"16\" away-score-before=\"0\" home-score-before=\"0\" away-score-after=\"0\" home-score-after=\"0\" yards-from-goal=\"78\" end-yards-from-goal=\"85\" possession=\"22\" possession-global-id=\"355\" end-possession=\"22\" end-possession-global-id=\"355\" event-type=\"2\" continuation=\"false\" details=\"Carson Palmer sacked at Ari15 for a loss of 7 yards by J.J. Watt.\">\n" +
//                "        <stat-id id=\"21\" description=\"Sack\" team-id=\"22\" player-id=\"6337\" global-team-id=\"355\" global-player-id=\"184503\" yards=\"-7\"/>\n" +
//                "        <stat-id id=\"19\" description=\"Tackle\" team-id=\"34\" player-id=\"24798\" global-team-id=\"325\" global-player-id=\"403362\"/>\n" +
//                "    </play>\n" +
//                "    <home-team>\n" +
//                "        <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "        <team-city city=\"Arizona\"/>\n" +
//                "        <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "        <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "            <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "        </linescore>\n" +
//                "        <time-of-possession minutes=\"0\" seconds=\"4\"/>\n" +
//                "    </home-team>\n" +
//                "    <visiting-team>\n" +
//                "        <team-name name=\"Texans\" alias=\"Hou\"/>\n" +
//                "        <team-city city=\"Houston\"/>\n" +
//                "        <team-code id=\"34\" global-id=\"325\"/>\n" +
//                "        <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "            <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "        </linescore>\n" +
//                "        <defense tackles=\"2\" assists=\"0\" sacks=\"1\" sack-yards=\"7\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                "        <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                "    </visiting-team>\n" +
//                "    <visiting-player-stats>\n" +
//                "        <visiting-player>\n" +
//                "            <player-code global-id=\"403362\" id=\"24798\"/>\n" +
//                "            <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"1\" sack-yards=\"7\" passes-defensed=\"0\"/>\n" +
//                "        </visiting-player>\n" +
//                "    </visiting-player-stats>\n" +
//                "</nfl-event>\n";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            processor.processEventDetails(fantasyPointUpdateEvent, doc);
//
//            assertEquals(3, fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().size());
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0);
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1);
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2);
//
//            // Athlete FP delta
//            assertEquals(BigDecimal.ZERO, runningBackFPAUE.getFantasyPointDelta());
//
//            // Athlete Timeline
//            List<Map<String, Object>> timeline = mapper.readValue(runningBackFPAUE.getTimeline(), listTypeReference);
//            assertEquals(0, timeline.size());
////            assertEquals("Carson Palmer sacked at Ari15 for a loss of 7 yards by J.J. Watt.", timeline.get(0).get("description"));
////            assertEquals("-1.0", timeline.get(0).get("fpChange"));
//
//            // Home Team FP delta
//            assertEquals(new BigDecimal("0.0"), homeTeamFPAUE.getFantasyPointDelta());
//
//            // Home Team timeline
//            timeline = mapper.readValue(homeTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(0, timeline.size());
//
//            // Away Team FP delta
//            assertEquals(new BigDecimal("1.00"), awayTeamFPAUE.getFantasyPointDelta());
//
//            // Away Team timeline
//            timeline = mapper.readValue(awayTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Carson Palmer sacked at Ari15 for a loss of 7 yards by J.J. Watt.", timeline.get(0).get("description"));
//            assertEquals("+1.0", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoDefensiveTeam.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcessEventDetails_StatCorrection() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        runningBack.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        offensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        defensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"21\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <play id=\"1567.0000000000000001\" sequence=\"143\" quarter=\"2\"  time=\"3:43\"  down=\"1\"  end-down=\"1\"  distance=\"3\"  end-distance=\"0\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"6\"  yards-from-goal=\"3\"  end-yards-from-goal=\"0\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"3\" direction=\"M\" points=\"6\" rush-type-id=\"7\" rush-type-name=\"Middle\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        List<Map<String, Object>> runningBackTimeline = new ArrayList<>();
//        Map<String, Object> timelineEntry = new HashMap<>();
//        timelineEntry.put("description", "Something");
//        runningBackTimeline.add(timelineEntry);
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            athleteSportEventInfoRunningBack.setTimeline(mapper.writeValueAsString(runningBackTimeline));
//            athleteSportEventInfoDefensiveTeam.setTimeline(mapper.writeValueAsString(runningBackTimeline));
//            athleteSportEventInfoOffensiveTeam.setTimeline(mapper.writeValueAsString(runningBackTimeline));
//
//
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            processor.processEventDetails(fantasyPointUpdateEvent, doc);
//
//            assertEquals(3, fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().size());
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0);
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1);
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2);
//
//            // Athlete FP delta
//            assertEquals(new BigDecimal("6.30"), runningBackFPAUE.getFantasyPointDelta());
//
//            // Athlete Timeline
//            List<Map<String, Object>> timeline = mapper.readValue(runningBackFPAUE.getTimeline(), listTypeReference);
//            assertEquals(2, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.", timeline.get(0).get("description"));
//            assertEquals("+6.3", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoRunningBack.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//            assertEquals(true, timeline.get(1).get("timestamp") instanceof Long);
//            assertEquals("Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.", timeline.get(1).get("description"));
//            assertEquals("+0.6", timeline.get(1).get("fpChange"));
//            assertEquals(athleteSportEventInfoRunningBack.getId(), timeline.get(1).get("athleteSportEventInfoId"));
//
//
//            // Home Team FP delta
//            assertEquals(new BigDecimal("0"), homeTeamFPAUE.getFantasyPointDelta());
//
//            // Home Team timeline
//            timeline = mapper.readValue(homeTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(0, timeline.size());
//
//            // Away Team FP delta
//            assertEquals(new BigDecimal("-3.0"), awayTeamFPAUE.getFantasyPointDelta());
//
//            // Away Team timeline
//            timeline = mapper.readValue(awayTeamFPAUE.getTimeline(), listTypeReference);
//            assertEquals(1, timeline.size());
//            assertEquals(true, timeline.get(0).get("timestamp") instanceof Long);
//            assertEquals("Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.", timeline.get(0).get("description"));
//            assertEquals("-3.0", timeline.get(0).get("fpChange"));
//            assertEquals(athleteSportEventInfoDefensiveTeam.getId(), timeline.get(0).get("athleteSportEventInfoId"));
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateAthleteBoxScore_Defense_Rush_NoScore() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"21\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            NodeList visitingPlayerStats = (NodeList) xPath.evaluate("//nfl-event/home-player-stats/home-player", doc, XPathConstants.NODESET);
//            Element visitingPlayer = (Element) visitingPlayerStats.item(0);
//
//            NodeList homeTeamStats = (NodeList) xPath.evaluate("//nfl-event/home-team", doc, XPathConstants.NODESET);
//            Element homeTeamPlayer = (Element) homeTeamStats.item(0);
//
//            NodeList visitingTeamrStats = (NodeList) xPath.evaluate("//nfl-event/visiting-team", doc, XPathConstants.NODESET);
//            Element visitingTeamPlayer = (Element) visitingTeamrStats.item(0);
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = new FantasyPointAthleteUpdateEvent();
//            homeTeamFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE));
//            homeTeamFPAUE.setAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = new FantasyPointAthleteUpdateEvent();
//            awayTeamFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE));
//            awayTeamFPAUE.setAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = new FantasyPointAthleteUpdateEvent();
//            runningBackFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK));
//            runningBackFPAUE.setAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, visitingPlayer, 2);
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, homeTeamPlayer, 0);
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, visitingTeamPlayer, 1);
//
//            assertEquals(new BigDecimal("0.60"), runningBackFPAUE.getFantasyPoints());
//            assertEquals(new BigDecimal("12.00"), homeTeamFPAUE.getFantasyPoints());
//            assertEquals(new BigDecimal("12.00"), awayTeamFPAUE.getFantasyPoints());
//
//            List<Map<String, Object>> rbBoxScore = mapper.readValue(runningBackFPAUE.getBoxscore(), listTypeReference);
//            for (Map<String, Object> entry : rbBoxScore) {
//                if (entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL)) {
//                    assertEquals(6, entry.get("amount"));
//                    assertEquals(0.6, entry.get("fpp"));
//                } else {
//                    assertEquals(0, entry.get("amount"));
//                    if (entry.get("fpp") instanceof Integer)
//                        assertEquals(0, entry.get("fpp"));
//                    else
//                        assertEquals(0.0, entry.get("fpp"));
//                }
//            }
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateAthleteBoxScore_Defense_Safety() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"2\" minutes=\"3\" seconds=\"43\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"2\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"1567.0000000000000001\" sequence=\"143\" quarter=\"2\"  time=\"3:43\"  down=\"1\"  end-down=\"1\"  distance=\"3\"  end-distance=\"0\"  away-score-before=\"14\"  home-score-before=\"3\"  away-score-after=\"14\"  home-score-after=\"9\"  yards-from-goal=\"3\"  end-yards-from-goal=\"0\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"3\" direction=\"M\" points=\"6\" rush-type-id=\"7\" rush-type-name=\"Middle\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"16\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"9\"/>\n" +
//                "  </linescore>\n" +
//                " <first-downs number=\"7\" rushing=\"2\" passing=\"5\" penalty=\"0\"/>\n" +
//                " <safeties safeties=\"1\"/>\n" +
//                "  <opponent-fumbles recovered=\"1\" yards=\"30\" tds=\"1\"/>" +
//                " <interception-returns attempts=\"1\" yards=\"45\" tds=\"1\" long=\"45\" long-TD=\"true\"/>" +
//                " <first-downs number=\"7\" rushing=\"2\" passing=\"5\" penalty=\"0\"/>\n" +
//                " <first-downs number=\"7\" rushing=\"2\" passing=\"5\" penalty=\"0\"/>\n" +
//                " <rushing attempts=\"14\" yards=\"22\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"16\" seconds=\"17\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"7\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"7\"/>\n" +
//                "  </linescore>\n" +
////                " <safeties safeties=\"1\"/>\n" +
////                "  <opponent-fumbles recovered=\"1\" yards=\"30\" tds=\"1\"/>" +
////                " <interception-returns attempts=\"1\" yards=\"45\" tds=\"1\" long=\"45\" long-TD=\"true\"/>" +
//                " <time-of-possession minutes=\"10\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"10\" yards=\"16\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            NodeList visitingPlayerStats = (NodeList) xPath.evaluate("//nfl-event/home-player-stats/home-player", doc, XPathConstants.NODESET);
//            Element visitingPlayer = (Element) visitingPlayerStats.item(0);
//
//            NodeList homeTeamStats = (NodeList) xPath.evaluate("//nfl-event/home-team", doc, XPathConstants.NODESET);
//            Element homeTeamPlayer = (Element) homeTeamStats.item(0);
//
//            NodeList visitingTeamrStats = (NodeList) xPath.evaluate("//nfl-event/visiting-team", doc, XPathConstants.NODESET);
//            Element visitingTeamPlayer = (Element) visitingTeamrStats.item(0);
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = new FantasyPointAthleteUpdateEvent();
//            homeTeamFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE));
//            homeTeamFPAUE.setAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = new FantasyPointAthleteUpdateEvent();
//            awayTeamFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE));
//            awayTeamFPAUE.setAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = new FantasyPointAthleteUpdateEvent();
//            runningBackFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK));
//            runningBackFPAUE.setAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, visitingPlayer, 2);
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, homeTeamPlayer, 0);
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, visitingTeamPlayer, 1);
//
//            assertEquals(new BigDecimal("32.00"), homeTeamFPAUE.getFantasyPoints());
//            assertEquals(new BigDecimal("11.00"), awayTeamFPAUE.getFantasyPoints());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateAthleteBoxScore_Defense_Rush_Score() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"2\" minutes=\"3\" seconds=\"43\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"2\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"1567.0000000000000001\" sequence=\"143\" quarter=\"2\"  time=\"3:43\"  down=\"1\"  end-down=\"1\"  distance=\"3\"  end-distance=\"0\"  away-score-before=\"14\"  home-score-before=\"3\"  away-score-after=\"14\"  home-score-after=\"9\"  yards-from-goal=\"3\"  end-yards-from-goal=\"0\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"3\" direction=\"M\" points=\"6\" rush-type-id=\"7\" rush-type-name=\"Middle\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"9\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"9\"/>\n" +
//                "  </linescore>\n" +
//                " <first-downs number=\"7\" rushing=\"2\" passing=\"5\" penalty=\"0\"/>\n" +
//                " <rushing attempts=\"14\" yards=\"22\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"16\" seconds=\"17\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"14\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"7\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"7\"/>\n" +
//                "  </linescore>\n" +
//                " <time-of-possession minutes=\"10\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"10\" yards=\"16\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            NodeList visitingPlayerStats = (NodeList) xPath.evaluate("//nfl-event/home-player-stats/home-player", doc, XPathConstants.NODESET);
//            Element visitingPlayer = (Element) visitingPlayerStats.item(0);
//
//            NodeList homeTeamStats = (NodeList) xPath.evaluate("//nfl-event/home-team", doc, XPathConstants.NODESET);
//            Element homeTeamPlayer = (Element) homeTeamStats.item(0);
//
//            NodeList visitingTeamrStats = (NodeList) xPath.evaluate("//nfl-event/visiting-team", doc, XPathConstants.NODESET);
//            Element visitingTeamPlayer = (Element) visitingTeamrStats.item(0);
//
//            FantasyPointAthleteUpdateEvent homeTeamFPAUE = new FantasyPointAthleteUpdateEvent();
//            homeTeamFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE));
//            homeTeamFPAUE.setAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//
//            FantasyPointAthleteUpdateEvent awayTeamFPAUE = new FantasyPointAthleteUpdateEvent();
//            awayTeamFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE));
//            awayTeamFPAUE.setAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//            FantasyPointAthleteUpdateEvent runningBackFPAUE = new FantasyPointAthleteUpdateEvent();
//            runningBackFPAUE.setBoxscore(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK));
//            runningBackFPAUE.setAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, visitingPlayer, 2);
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, homeTeamPlayer, 0);
//            processor.updateAthleteBoxScore(Arrays.asList(homeTeamFPAUE, awayTeamFPAUE, runningBackFPAUE), doc, visitingTeamPlayer, 1);
//
//            assertEquals(new BigDecimal("7.60"), runningBackFPAUE.getFantasyPoints());
//            assertEquals(new BigDecimal("5.00"), homeTeamFPAUE.getFantasyPoints());
//            assertEquals(new BigDecimal("7.50"), awayTeamFPAUE.getFantasyPoints());
//
//            List<Map<String, Object>> rbBoxScore = mapper.readValue(runningBackFPAUE.getBoxscore(), listTypeReference);
//            for (Map<String, Object> entry : rbBoxScore) {
//                if (entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL)) {
//                    assertEquals(16, entry.get("amount"));
//                    assertEquals(1.6, entry.get("fpp"));
//                } else if (entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL)) {
//                    assertEquals(1, entry.get("amount"));
//                    assertEquals(6.0, entry.get("fpp"));
//                } else {
//                    assertEquals(0, entry.get("amount"));
//                    if (entry.get("fpp") instanceof Integer)
//                        assertEquals(0, entry.get("fpp"));
//                    else
//                        assertEquals(0.0, entry.get("fpp"));
//                }
//            }
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateIndicators_RedZone() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        Athlete quarterback = new Athlete(1234, "Quarterback", "Test", team2, "1");
//        quarterback.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
//        sportsDao.saveAthlete(quarterback);
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        runningBack.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        offensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        defensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(defensiveTeam);
//
//        AthleteSportEventInfo athleteSportEventInfoQuarterback = new AthleteSportEventInfo(sportEvent, quarterback, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoQuarterback);
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"2\" minutes=\"3\" seconds=\"43\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"0\" down=\"1\" distance=\"0\" segment-number=\"2\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"1567.0000000000000001\" sequence=\"143\" quarter=\"2\"  time=\"3:43\"  down=\"1\"  end-down=\"1\"  distance=\"3\"  end-distance=\"0\"  away-score-before=\"14\"  home-score-before=\"3\"  away-score-after=\"14\"  home-score-after=\"9\"  yards-from-goal=\"3\"  end-yards-from-goal=\"0\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"3\" direction=\"M\" points=\"6\" rush-type-id=\"7\" rush-type-name=\"Middle\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"9\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"9\"/>\n" +
//                "  </linescore>\n" +
//                " <first-downs number=\"7\" rushing=\"2\" passing=\"5\" penalty=\"0\"/>\n" +
//                " <rushing attempts=\"14\" yards=\"22\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"16\" seconds=\"17\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"14\" timeouts-left=\"2\">\n" +
//                "   <quarter quarter=\"1\" score=\"7\"/>\n" +
//                "   <quarter quarter=\"2\" score=\"7\"/>\n" +
//                "  </linescore>\n" +
//                " <time-of-possession minutes=\"10\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"10\" yards=\"16\" average=\"1.6\" tds=\"1\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            processor.updateIndicators(fantasyPointUpdateEvent, doc);
//
//            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(athleteSportEventInfoQuarterback.getId()));
//            assertEquals(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY, (int) fantasyPointUpdateEvent.getIndicators().get(athleteSportEventInfoRunningBack.getId()));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(athleteSportEventInfoOffensiveTeam.getId()));
//            assertEquals(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY, (int) fantasyPointUpdateEvent.getIndicators().get(athleteSportEventInfoDefensiveTeam.getId()));
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateIndicators_NonRedZone() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        Athlete quarterback = new Athlete(1234, "Quarterback", "Test", team2, "1");
//        quarterback.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
//        sportsDao.saveAthlete(quarterback);
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        runningBack.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        offensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        defensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(defensiveTeam);
//
//        AthleteSportEventInfo athleteSportEventInfoQuarterback = new AthleteSportEventInfo(sportEvent, quarterback, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoQuarterback);
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), "[]", "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"21\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            processor.updateIndicators(fantasyPointUpdateEvent, doc);
//
//            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(athleteSportEventInfoQuarterback.getId()));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(athleteSportEventInfoRunningBack.getId()));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(athleteSportEventInfoOffensiveTeam.getId()));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(athleteSportEventInfoDefensiveTeam.getId()));
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateUnitsRemaining_StartOfGame() {
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"15\" seconds=\"00\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"80\" down=\"1\" distance=\"10\" segment-number=\"1\" active-state=\"true\" restart=\"true\"/>\n" +
//                " <play id=\"1.0000000000000001\" sequence=\"1\" quarter=\"1\"  time=\"15:00\"  down=\"\"  end-down=\"1\"  distance=\"0\"  end-distance=\"\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"\"  end-yards-from-goal=\"\"  possession=\"\"  possession-global-id=\"-1\"  end-possession=\"\"  end-possession-global-id=\"-1\"  event-type=\"62\"  continuation=\"false\"  details=\"Game Start Seattle at Arizona is underway.\" >\n" +
//                "</play>\n" +
//                " <play id=\"36.0000000000000002\" sequence=\"3\" quarter=\"1\"  time=\"15:00\"  down=\"\"  end-down=\"1\"  distance=\"\"  end-distance=\"0\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"100\"  end-yards-from-goal=\"20\"  possession=\"26\"  possession-global-id=\"361\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"38\"  continuation=\"false\"  details=\"Steven Hauschka kicks off. Touchback.\" >\n" +
//                "<stat-id id=\"27\" description=\"Kick\" team-id=\"26\" player-id=\"9066\" global-team-id=\"361\" global-player-id=\"406186\"/>\n" +
//                "<stat-id id=\"4\" description=\"Kick Return\" team-id=\"22\" player-id=\"\" global-team-id=\"355\" global-player-id=\"\" yards=\"0\" points=\"0\"/>\n" +
//                "<stat-id id=\"38\" description=\"Touchback\" team-id=\"26\" player-id=\"9066\" global-team-id=\"361\" global-player-id=\"406186\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <return-totals yards=\"0\" tds=\"0\"/>\n" +
//                " <kick-returns attempts=\"0\" yards=\"0\" tds=\"0\" long=\"0\" long-TD=\"false\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " <kickoffs number=\"1\" end-zone=\"1\" touchbacks=\"1\"/>\n" +
//                " </visiting-team>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"406186\" id=\"9066\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event> ";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            int currentTimeUnit = processor.updateUnitsRemaining(sportEvent, doc);
//
//            assertEquals(0, currentTimeUnit);
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateUnitsRemaining_MiddleOfGame() {
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"21\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            int currentTimeUnit = processor.updateUnitsRemaining(sportEvent, doc);
//
//            assertEquals(1, currentTimeUnit);
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testUpdateUnitsRemaining_EndOfGame() {
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"4\" quarter=\"4\" minutes=\"0\" seconds=\"00\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//            int currentTimeUnit = processor.updateUnitsRemaining(sportEvent, doc);
//
//            assertEquals(60, currentTimeUnit);
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testExtractGameScore() {
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"4\" quarter=\"4\" minutes=\"0\" seconds=\"00\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"3\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"3\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
//        fantasyPointUpdateEvent.setSportEvent(sportEvent);
//
//        DocumentBuilder documentBuilder = null;
//        try {
//            InputSource source = new InputSource(new StringReader(xml));
//            documentBuilder = dbFactory.newDocumentBuilder();
//            Document doc = documentBuilder.parse(source);
//
//            int[] gameScore = processor.extractGameScore(doc);
//            assertEquals(3, gameScore[0]);
//            assertEquals(0, gameScore[1]);
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcess() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        runningBack.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        offensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        defensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"21\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"1\" yards=\"6\" average=\"6.0\" tds=\"0\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        try {
//            FantasyPointUpdateEvent fantasyPointUpdateEvent = processor.process(xml);
//
//            assertEquals(false, fantasyPointUpdateEvent.isStatCorrection());
//
//            /*
//             * Indicators
//             */
//            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(299180));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(355));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(361));
//
//            sportEvent = sportsDao.findSportEvent(sportEvent.getStatProviderId());
//            assertEquals(false, sportEvent.isComplete());
//
//            assertEquals(59, sportEvent.getUnitsRemaining());
//
//            assertEquals("Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.", fantasyPointUpdateEvent.getEventDescription());
//
//            assertEquals(0, fantasyPointUpdateEvent.getAwayScore());
//            assertEquals(0, fantasyPointUpdateEvent.getHomeScore());
//
//            athleteSportEventInfoRunningBack = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoRunningBack.getId());
//            athleteSportEventInfoDefensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam.getId());
//            athleteSportEventInfoOffensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam.getId());
//
//            assertEquals(new BigDecimal("0.60"), athleteSportEventInfoRunningBack.getFantasyPoints());
//            assertEquals(new BigDecimal("12.00"), athleteSportEventInfoDefensiveTeam.getFantasyPoints());
//            assertEquals(new BigDecimal("12.00"), athleteSportEventInfoOffensiveTeam.getFantasyPoints());
//
//            List<Map<String, Object>> rbBoxScore = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2).getBoxscore(), listTypeReference);
//            for (Map<String, Object> entry : rbBoxScore) {
//                if (entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL)) {
//                    assertEquals(6, entry.get("amount"));
//                    assertEquals(0.6, entry.get("fpp"));
//                } else {
//                    assertEquals(0, entry.get("amount"));
//                    if (entry.get("fpp") instanceof Integer)
//                        assertEquals(0, entry.get("fpp"));
//                    else
//                        assertEquals(0.0, entry.get("fpp"));
//                }
//            }
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }

    @Test
    public void testProcess_AlexSmithPass() {
        team = new Team(League.NFL, "", "", "", 339);
        sportsDao.saveTeam(team);
        team2 = new Team(League.NFL, "", "", "", 347);
        sportsDao.saveTeam(team2);

        sportEvent = new SportEvent(1421417, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
        sportEvent.setTeams(Arrays.asList(team, team2));
        sportsDao.saveSportEvent(sportEvent);

        Athlete alexSmith = new Athlete(217357, "Alex", "Smith", team, "");
        alexSmith.setPositions(Arrays.asList(Position.FB_QUARTERBACK));
        sportsDao.saveAthlete(alexSmith);

        runningBack = new Athlete(495184, "Running", "Back", team, "1");
        runningBack.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
        sportsDao.saveAthlete(runningBack);
        offensiveTeam = new Athlete(339, "Offensive", "Team", team, "1");       // Home
        offensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
        sportsDao.saveAthlete(offensiveTeam);
        defensiveTeam = new Athlete(347, "Defensive", "Team", team2, "1");      // Away
        defensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
        sportsDao.saveAthlete(defensiveTeam);

        AthleteSportEventInfo athleteSportEventInfoSmith = new AthleteSportEventInfo(sportEvent, alexSmith, BigDecimal.ZERO, sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_QUARTERBACK), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoSmith);
        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);

        String xml = "<?xml version=\"1.0\"?>\n" +
                "<nfl-event>\n" +
                "    <gamecode code=\"20140823012\" global-id=\"1421417\"/>\n" +
                "    <coverage level=\"1\"/>\n" +
                "    <gamestate status=\"2\" quarter=\"1\" minutes=\"15\" seconds=\"00\" team-possession-id=\"12\" team-possession-global-id=\"339\" yards-from-goal=\"64\" down=\"1\" distance=\"10\" segment-number=\"1\" active-state=\"true\" restart=\"false\" under-review=\"false\"/>\n" +
                "    <play id=\"55.0000000000000001\" sequence=\"5\" quarter=\"1\" time=\"15:00\" down=\"1\" end-down=\"1\" distance=\"10\" end-distance=\"10\" away-score-before=\"0\" home-score-before=\"0\" away-score-after=\"0\" home-score-after=\"0\" yards-from-goal=\"80\" end-yards-from-goal=\"64\" possession=\"12\" possession-global-id=\"339\" end-possession=\"12\" end-possession-global-id=\"339\" event-type=\"2\" continuation=\"false\" details=\"Alex Smith pass to the left to Knile Davis for 16 yards to the KC36. Tackled by Xavier Rhodes.\">\n" +
                "        <stat-id id=\"2\" description=\"Pass\" team-id=\"12\" player-id=\"7177\" global-team-id=\"339\" global-player-id=\"217357\" yards=\"16\" direction=\"L\" points=\"0\"/>\n" +
                "        <stat-id id=\"17\" description=\"Reception\" team-id=\"12\" player-id=\"26719\" global-team-id=\"339\" global-player-id=\"495184\" yards=\"16\" direction=\"L\" points=\"0\"/>\n" +
                "        <stat-id id=\"19\" description=\"Tackle\" team-id=\"16\" player-id=\"26648\" global-team-id=\"347\" global-player-id=\"509368\"/>\n" +
                "    </play>\n" +
                "    <home-team>\n" +
                "        <team-name name=\"Chiefs\" alias=\"KC\"/>\n" +
                "        <team-city city=\"Kansas City\"/>\n" +
                "        <team-code id=\"12\" global-id=\"339\"/>\n" +
                "        <linescore score=\"0\" timeouts-left=\"3\">\n" +
                "            <quarter quarter=\"1\" score=\"0\"/>\n" +
                "        </linescore>\n" +
                "        <first-downs number=\"1\" rushing=\"0\" passing=\"1\" penalty=\"0\"/>\n" +
                "        <passing completions=\"1\" attempts=\"1\" interceptions=\"0\" net-yards=\"16\" average=\"16.0\" sacked=\"0\" yards-lost=\"0\" tds=\"0\"/>\n" +
                "        <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
                "    </home-team>\n" +
                "    <visiting-team>\n" +
                "        <team-name name=\"Vikings\" alias=\"Min\"/>\n" +
                "        <team-city city=\"Minnesota\"/>\n" +
                "        <team-code id=\"16\" global-id=\"347\"/>\n" +
                "        <linescore score=\"0\" timeouts-left=\"3\">\n" +
                "            <quarter quarter=\"1\" score=\"0\"/>\n" +
                "        </linescore>\n" +
                "        <defense tackles=\"1\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
                "        <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
                "    </visiting-team>\n" +
                "    <home-player-stats>\n" +
                "        <home-player>\n" +
                "            <player-code global-id=\"217357\" id=\"7177\"/>\n" +
                "            <passing completions=\"1\" attempts=\"1\" interceptions=\"0\" yards=\"16\" sacked=\"0\" yards-lost=\"0\" long=\"16\" long-td=\"false\" tds=\"0\" rating=\"118.8\"/>\n" +
                "        </home-player>\n" +
                "        <home-player>\n" +
                "            <player-code global-id=\"495184\" id=\"26719\"/>\n" +
                "            <receiving receptions=\"1\" yards=\"16\" long=\"16\" long-td=\"false\" tds=\"0\"/>\n" +
                "        </home-player>\n" +
                "    </home-player-stats>\n" +
                "    <visiting-player-stats>\n" +
                "        <visiting-player>\n" +
                "            <player-code global-id=\"509368\" id=\"26648\"/>\n" +
                "            <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
                "        </visiting-player>\n" +
                "    </visiting-player-stats>\n" +
                "</nfl-event>";

        try {
            FantasyPointUpdateEvent fantasyPointUpdateEvent = processor.process(xml);

            assertEquals(false, fantasyPointUpdateEvent.isStatCorrection());

            /*
             * Indicators
             */
            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(alexSmith.getId()));
            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(runningBack.getId()));
            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(offensiveTeam.getId()));
            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(defensiveTeam.getId()));

            sportEvent = sportsDao.findSportEvent(sportEvent.getStatProviderId());
            assertEquals(false, sportEvent.isComplete());

            assertEquals(60, sportEvent.getUnitsRemaining());

            assertEquals("Alex Smith pass to the left to Knile Davis for 16 yards to the KC36. Tackled by Xavier Rhodes.", fantasyPointUpdateEvent.getEventDescription());

            assertEquals(0, fantasyPointUpdateEvent.getAwayScore());
            assertEquals(0, fantasyPointUpdateEvent.getHomeScore());

            athleteSportEventInfoSmith = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoSmith.getId());
            athleteSportEventInfoRunningBack = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoRunningBack.getId());
            athleteSportEventInfoDefensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam.getId());
            athleteSportEventInfoOffensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam.getId());

            assertEquals(new BigDecimal("0.64"), athleteSportEventInfoSmith.getFantasyPoints());
            assertEquals(new BigDecimal("2.10"), athleteSportEventInfoRunningBack.getFantasyPoints());
            assertEquals(new BigDecimal("12.00"), athleteSportEventInfoDefensiveTeam.getFantasyPoints());
            assertEquals(new BigDecimal("12.00"), athleteSportEventInfoOffensiveTeam.getFantasyPoints());

            List<Map<String, Object>> rbBoxScore = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(3).getBoxscore(), listTypeReference);
            for (Map<String, Object> entry : rbBoxScore) {
                if (entry.get("name").equals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL)) {
                    assertEquals(16, entry.get("amount"));
                    assertEquals(1.6, entry.get("fpp"));
                } else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_RECEPTION_LABEL)) {
                    assertEquals(1, entry.get("amount"));
                    assertEquals(0.5, entry.get("fpp"));
                } else {
                    assertEquals(0, entry.get("amount"));
                    if (entry.get("fpp") instanceof Integer)
                        assertEquals(0, entry.get("fpp"));
                    else
                        assertEquals(0.0, entry.get("fpp"));
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

//    @Test
//    public void testProcess_Sack() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1421774, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportEvent.setTeams(Arrays.asList(team, team2));
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        runningBack.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        offensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(325, "Defensive", "Team", team2, "1");      // Away
//        defensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<?xml version=\"1.0\"?>\n" +
//                "<nfl-event>\n" +
//                "    <gamecode code=\"20140809022\" global-id=\"1421774\"/>\n" +
//                "    <coverage level=\"1\"/>\n" +
//                "    <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"56\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"85\" down=\"3\" distance=\"16\" segment-number=\"1\" active-state=\"true\" restart=\"false\" under-review=\"false\"/>\n" +
//                "    <play id=\"79.0000000000000001\" sequence=\"7\" quarter=\"1\" time=\"14:56\" down=\"2\" end-down=\"3\" distance=\"9\" end-distance=\"16\" away-score-before=\"0\" home-score-before=\"0\" away-score-after=\"0\" home-score-after=\"0\" yards-from-goal=\"78\" end-yards-from-goal=\"85\" possession=\"22\" possession-global-id=\"355\" end-possession=\"22\" end-possession-global-id=\"355\" event-type=\"2\" continuation=\"false\" details=\"Carson Palmer sacked at Ari15 for a loss of 7 yards by J.J. Watt.\">\n" +
//                "        <stat-id id=\"21\" description=\"Sack\" team-id=\"22\" player-id=\"6337\" global-team-id=\"355\" global-player-id=\"184503\" yards=\"-7\"/>\n" +
//                "        <stat-id id=\"19\" description=\"Tackle\" team-id=\"34\" player-id=\"24798\" global-team-id=\"325\" global-player-id=\"403362\"/>\n" +
//                "    </play>\n" +
//                "    <home-team>\n" +
//                "        <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "        <team-city city=\"Arizona\"/>\n" +
//                "        <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "        <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "            <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "        </linescore>\n" +
//                "        <time-of-possession minutes=\"0\" seconds=\"4\"/>\n" +
//                "    </home-team>\n" +
//                "    <visiting-team>\n" +
//                "        <team-name name=\"Texans\" alias=\"Hou\"/>\n" +
//                "        <team-city city=\"Houston\"/>\n" +
//                "        <team-code id=\"34\" global-id=\"325\"/>\n" +
//                "        <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "            <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "        </linescore>\n" +
//                "        <defense tackles=\"2\" assists=\"0\" sacks=\"1\" sack-yards=\"7\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                "        <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                "    </visiting-team>\n" +
//                "    <visiting-player-stats>\n" +
//                "        <visiting-player>\n" +
//                "            <player-code global-id=\"403362\" id=\"24798\"/>\n" +
//                "            <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"1\" sack-yards=\"7\" passes-defensed=\"0\"/>\n" +
//                "        </visiting-player>\n" +
//                "    </visiting-player-stats>\n" +
//                "</nfl-event>\n";
//
//        try {
//            FantasyPointUpdateEvent fantasyPointUpdateEvent = processor.process(xml);
//
//            assertEquals(false, fantasyPointUpdateEvent.isStatCorrection());
//
//            /*
//             * Indicators
//             */
////            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(299180));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(defensiveTeam.getId()));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(offensiveTeam.getId()));
//
//            sportEvent = sportsDao.findSportEvent(sportEvent.getStatProviderId());
//            assertEquals(false, sportEvent.isComplete());
//
//            assertEquals(59, sportEvent.getUnitsRemaining());
//
//            assertEquals("Carson Palmer sacked at Ari15 for a loss of 7 yards by J.J. Watt.", fantasyPointUpdateEvent.getEventDescription());
//
//            assertEquals(0, fantasyPointUpdateEvent.getAwayScore());
//            assertEquals(0, fantasyPointUpdateEvent.getHomeScore());
//
//            athleteSportEventInfoRunningBack = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoRunningBack.getId());
//            athleteSportEventInfoDefensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam.getId());
//            athleteSportEventInfoOffensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam.getId());
//
//            assertEquals(new BigDecimal("0.00"), athleteSportEventInfoRunningBack.getFantasyPoints());
//            assertEquals(new BigDecimal("13.00"), athleteSportEventInfoDefensiveTeam.getFantasyPoints());
//            assertEquals(new BigDecimal("12.00"), athleteSportEventInfoOffensiveTeam.getFantasyPoints());
//
//            List<Map<String, Object>> defenseTimeline = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1).getTimeline(), listTypeReference);
//            assertEquals(2, defenseTimeline.size());
//            assertEquals("+1.0", defenseTimeline.get(0).get("fpChange"));
//            assertEquals("Carson Palmer sacked at Ari15 for a loss of 7 yards by J.J. Watt.", defenseTimeline.get(0).get("description"));
//            assertEquals(true, defenseTimeline.get(0).get("timestamp") instanceof Long);
//            assertEquals(false, defenseTimeline.get(0).get("published"));
//            assertEquals(athleteSportEventInfoDefensiveTeam.getId(), defenseTimeline.get(0).get("athleteSportEventInfoId"));
//
//            assertEquals("+12", defenseTimeline.get(1).get("fpChange"));
//            assertEquals("The game has started, 0 points allowed.", defenseTimeline.get(1).get("description"));
//            assertEquals(true, defenseTimeline.get(1).get("timestamp") instanceof Long);
//            assertEquals(false, defenseTimeline.get(1).get("published"));
//            assertEquals(athleteSportEventInfoDefensiveTeam.getId(), defenseTimeline.get(1).get("athleteSportEventInfoId"));
//
//            List<Map<String, Object>> defenseBoxscore = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1).getBoxscore(), listTypeReference);
//            for (Map<String, Object> entry : defenseBoxscore) {
//                if (entry.get("name").equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL)) {
//                    assertEquals(0, entry.get("amount"));
//                    assertEquals(12.0, entry.get("fpp"));
//                } else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_SACK_LABEL)) {
//                    assertEquals(1, entry.get("amount"));
//                    assertEquals(1.0, entry.get("fpp"));
//                } else {
//                    assertEquals(0, entry.get("amount"));
//                    if (entry.get("fpp") instanceof Integer)
//                        assertEquals(0, entry.get("fpp"));
//                    else
//                        assertEquals(0.0, entry.get("fpp"));
//                }
//            }
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcess_StatCorrection() {
//        team = new Team(League.NFL, "", "", "", 355);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 361);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1321715, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportEvent.setTeams(Arrays.asList(team, team2));
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        runningBack.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(355, "Offensive", "Team", team, "1");       // Home
//        offensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(361, "Defensive", "Team", team2, "1");      // Away
//        defensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        String xml = "<nfl-event>\n" +
//                " <gamecode code=\"20131017022\" global-id=\"1321715\"/>\n" +
//                " <coverage level=\"1\"/>\n" +
//                " <gamestate status=\"2\" quarter=\"1\" minutes=\"14\" seconds=\"21\" team-possession-id=\"22\" team-possession-global-id=\"355\" yards-from-goal=\"73\" down=\"3\" distance=\"3\" segment-number=\"1\" active-state=\"true\" restart=\"false\"/>\n" +
//                " <play id=\"75.0000000000000001\" sequence=\"7\" quarter=\"1\"  time=\"14:21\"  down=\"2\"  end-down=\"3\"  distance=\"9\"  end-distance=\"3\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"0\"  yards-from-goal=\"79\"  end-yards-from-goal=\"73\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"6\" direction=\"R\" points=\"0\" rush-type-id=\"9\" rush-type-name=\"Off Right Tackle\"/>\n" +
//                "<stat-id id=\"19\" description=\"Tackle\" team-id=\"26\" player-id=\"24941\" global-team-id=\"361\" global-player-id=\"332735\"/>\n" +
//                "</play>\n" +
//                " <play id=\"1567.0000000000000001\" sequence=\"143\" quarter=\"1\"  time=\"3:43\"  down=\"1\"  end-down=\"1\"  distance=\"3\"  end-distance=\"0\"  away-score-before=\"0\"  home-score-before=\"0\"  away-score-after=\"0\"  home-score-after=\"6\"  yards-from-goal=\"3\"  end-yards-from-goal=\"0\"  possession=\"22\"  possession-global-id=\"355\"  end-possession=\"22\"  end-possession-global-id=\"355\"  event-type=\"1\"  continuation=\"false\"  details=\"Rashard Mendenhall rush to the middle for 3 yards for a TOUCHDOWN.\" >\n" +
//                "<stat-id id=\"1\" description=\"Rush\" team-id=\"22\" player-id=\"8800\" global-team-id=\"355\" global-player-id=\"299180\" yards=\"3\" direction=\"M\" points=\"6\" rush-type-id=\"7\" rush-type-name=\"Middle\"/>\n" +
//                "</play>\n" +
//                " <home-team>\n" +
//                "  <team-name name=\"Cardinals\" alias=\"Ari\"/>\n" +
//                "  <team-city city=\"Arizona\"/>\n" +
//                "  <team-code id=\"22\" global-id=\"355\"/>\n" +
//                "  <linescore score=\"6\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"6\"/>\n" +
//                "  </linescore>\n" +
//                " <rushing attempts=\"2\" yards=\"9\" average=\"4.5\" tds=\"1\" long=\"6\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"39\"/>\n" +
//                " </home-team>\n" +
//                " <visiting-team>\n" +
//                "  <team-name name=\"Seahawks\" alias=\"Sea\"/>\n" +
//                "  <team-city city=\"Seattle\"/>\n" +
//                "  <team-code id=\"26\" global-id=\"361\"/>\n" +
//                "  <linescore score=\"0\" timeouts-left=\"3\">\n" +
//                "   <quarter quarter=\"1\" score=\"0\"/>\n" +
//                "  </linescore>\n" +
//                " <defense tackles=\"2\" assists=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\" forced-fumbles=\"0\"/>\n" +
//                " <time-of-possession minutes=\"0\" seconds=\"0\"/>\n" +
//                " </visiting-team>\n" +
//                " <home-player-stats>\n" +
//                "  <home-player>\n" +
//                "   <player-code global-id=\"299180\" id=\"8800\"/>\n" +
//                " <rushing attempts=\"2\" yards=\"9\" average=\"4.5\" tds=\"1\" long=\"6\"/>\n" +
//                "  </home-player>\n" +
//                " </home-player-stats>\n" +
//                " <visiting-player-stats>\n" +
//                "  <visiting-player>\n" +
//                "   <player-code global-id=\"332735\" id=\"24941\"/>\n" +
//                " <defense tackles=\"1\" assists=\"0\" forced-fumbles=\"0\" sacks=\"0\" sack-yards=\"0\" passes-defensed=\"0\"/>\n" +
//                "  </visiting-player>\n" +
//                " </visiting-player-stats>\n" +
//                "</nfl-event>";
//
//        try {
//            FantasyPointUpdateEvent fantasyPointUpdateEvent = processor.process(xml);
//
//            assertEquals(true, fantasyPointUpdateEvent.isStatCorrection());
//
//            /*
//             * Indicators
//             */
//            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(299180));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(355));
//            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(361));
//
//            sportEvent = sportsDao.findSportEvent(sportEvent.getStatProviderId());
//            assertEquals(false, sportEvent.isComplete());
//
//            assertEquals(59, sportEvent.getUnitsRemaining());
//
//            assertEquals("Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.", fantasyPointUpdateEvent.getEventDescription());
//
//            assertEquals(0, fantasyPointUpdateEvent.getAwayScore());
//            assertEquals(6, fantasyPointUpdateEvent.getHomeScore());
//
//            athleteSportEventInfoRunningBack = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoRunningBack.getId());
//            athleteSportEventInfoDefensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam.getId());
//            athleteSportEventInfoOffensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam.getId());
//
//            assertEquals(new BigDecimal("6.90"), athleteSportEventInfoRunningBack.getFantasyPoints());
//            assertEquals(new BigDecimal("9.00"), athleteSportEventInfoDefensiveTeam.getFantasyPoints());
//            assertEquals(new BigDecimal("12.00"), athleteSportEventInfoOffensiveTeam.getFantasyPoints());
//
//            /*
//             * Box score for RB.
//             */
//            List<Map<String, Object>> rbBoxScore = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2).getBoxscore(), listTypeReference);
//            for (Map<String, Object> entry : rbBoxScore) {
//                if (entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL)) {
//                    assertEquals(9, entry.get("amount"));
//                    assertEquals(0.9, entry.get("fpp"));
//                } else if (entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL)) {
//                    assertEquals(1, entry.get("amount"));
//                    assertEquals(6.0, entry.get("fpp"));
//                } else {
//                    assertEquals(0, entry.get("amount"));
//                    if (entry.get("fpp") instanceof Integer)
//                        assertEquals(0, entry.get("fpp"));
//                    else
//                        assertEquals(0.0, entry.get("fpp"));
//                }
//            }
//
//            /*
//             * Box score for Defense
//             */
//            List<Map<String, Object>> defenseBoxScore = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1).getBoxscore(), listTypeReference);
//            for (Map<String, Object> entry : defenseBoxScore) {
//                if (entry.get("name").equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL)) {
//                    assertEquals(6, entry.get("amount"));
//                    assertEquals(9.0, entry.get("fpp"));
//                } else {
//                    assertEquals(0, entry.get("amount"));
//                    if (entry.get("fpp") instanceof Integer)
//                        assertEquals(0, entry.get("fpp"));
//                    else
//                        assertEquals(0.0, entry.get("fpp"));
//                }
//            }
//
//            /*
//             * Box score for Offense
//             */
//            List<Map<String, Object>> offenseBoxScore = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0).getBoxscore(), listTypeReference);
//            for (Map<String, Object> entry : offenseBoxScore) {
//                if (entry.get("name").equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL)) {
//                    assertEquals(0, entry.get("amount"));
//                    assertEquals(12.0, entry.get("fpp"));
//                } else {
//                    assertEquals(0, entry.get("amount"));
//                    if (entry.get("fpp") instanceof Integer)
//                        assertEquals(0, entry.get("fpp"));
//                    else
//                        assertEquals(0.0, entry.get("fpp"));
//                }
//            }
//
//            /*
//             * Timeline for Defense
//             */
//            List<Map<String, Object>> defenseTimeline = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1).getTimeline(), listTypeReference);
//            assertEquals(1, defenseTimeline.size());
//
//            /*
//             * Timeline for Offense
//             */
//            List<Map<String, Object>> offenseTimeline = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0).getTimeline(), listTypeReference);
//            assertEquals(0, offenseTimeline.size());
//
//            /*
//             * Timeline for Running back
//             */
//            List<Map<String, Object>> rbTimeline = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2).getTimeline(), listTypeReference);
//            assertEquals(2, rbTimeline.size());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcess_StatCorrection_BAL_SF() {
//        team = new Team(League.NFL, "", "", "", 366);
//        sportsDao.saveTeam(team);
//        team2 = new Team(League.NFL, "", "", "", 359);
//        sportsDao.saveTeam(team2);
//
//        sportEvent = new SportEvent(1421520, League.NFL, new Date(), "", "", 60, false, 2014, -1, 1);
//        sportEvent.setTeams(Arrays.asList(team, team2));
//        sportsDao.saveSportEvent(sportEvent);
//
//        runningBack = new Athlete(299180, "Running", "Back", team, "1");
//        runningBack.setPositions(Arrays.asList(Position.FB_RUNNINGBACK));
//        sportsDao.saveAthlete(runningBack);
//        offensiveTeam = new Athlete(366, "Baltimore", "Ravens", team, "1");       // Home
//        offensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(offensiveTeam);
//        defensiveTeam = new Athlete(359, "San Francisco", "49ers", team2, "1");      // Away
//        defensiveTeam.setPositions(Arrays.asList(Position.FB_DEFENSE));
//        sportsDao.saveAthlete(defensiveTeam);
//
//        athleteSportEventInfoRunningBack = new AthleteSportEventInfo(sportEvent, runningBack, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_RUNNINGBACK), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoRunningBack);
//        athleteSportEventInfoOffensiveTeam = new AthleteSportEventInfo(sportEvent, offensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam);
//        athleteSportEventInfoDefensiveTeam = new AthleteSportEventInfo(sportEvent, defensiveTeam, new BigDecimal("0"), sportsDao.createInitialJsonForAthleteBoxscore(Position.FB_DEFENSE), "[]");
//        sportsDao.saveAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam);
//
//        try {
//            String xml = FileUtils.readFileToString(new File("test_files/nfl_socket_feed_stat_correction.xml"));
//
//            FantasyPointUpdateEvent fantasyPointUpdateEvent = processor.process(xml);
//
//            assertEquals(true, fantasyPointUpdateEvent.isStatCorrection());
//
//            /*
//             * Indicators
//             */
////            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(299180));
////            assertEquals(GlobalConstants.INDICATOR_TEAM_OFF_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(355));
////            assertEquals(GlobalConstants.INDICATOR_TEAM_ON_FIELD, (int) fantasyPointUpdateEvent.getIndicators().get(361));
//
////            sportEvent = sportsDao.findSportEvent(sportEvent.getStatProviderId());
////            assertEquals(false, sportEvent.isComplete());
////
////            assertEquals(2, sportEvent.getUnitsRemaining());
////
////            assertEquals("Rashard Mendenhall rush to the right for 6 yards to the Ari27. Tackled by Richard Sherman.", fantasyPointUpdateEvent.getEventDescription());
////
////            assertEquals(0, fantasyPointUpdateEvent.getAwayScore());
////            assertEquals(6, fantasyPointUpdateEvent.getHomeScore());
//
//            athleteSportEventInfoRunningBack = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoRunningBack.getId());
//            athleteSportEventInfoDefensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoDefensiveTeam.getId());
//            athleteSportEventInfoOffensiveTeam = sportsDao.findAthleteSportEventInfo(athleteSportEventInfoOffensiveTeam.getId());
//
////            assertEquals(new BigDecimal("6.90"), athleteSportEventInfoRunningBack.getFantasyPoints());
////            assertEquals(new BigDecimal("9.00"), athleteSportEventInfoDefensiveTeam.getFantasyPoints());
////            assertEquals(new BigDecimal("12.00"), athleteSportEventInfoOffensiveTeam.getFantasyPoints());
//
//            /*
//             * Box score for RB.
//             */
////            List<Map<String, Object>> rbBoxScore = mapper.readValue(fantasyPointUpdateEvent.getStatsFantasyPointAthleteUpdateEventList().get(2).getBoxscore(), listTypeReference);
////            for(Map<String, Object> entry: rbBoxScore) {
////                if(entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL)) {
////                    assertEquals(9, entry.get("amount"));
////                    assertEquals(0.9, entry.get("fpp"));
////                }
////                else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL)) {
////                    assertEquals(1, entry.get("amount"));
////                    assertEquals(6.0, entry.get("fpp"));
////                }
////                else {
////                    assertEquals(0, entry.get("amount"));
////                    if(entry.get("fpp") instanceof Integer)
////                        assertEquals(0, entry.get("fpp"));
////                    else
////                        assertEquals(0.0, entry.get("fpp"));
////                }
////            }
//
//            /*
//             * Box score for Defense
//             */
////            List<Map<String, Object>> defenseBoxScore = mapper.readValue(fantasyPointUpdateEvent.getStatsFantasyPointAthleteUpdateEventList().get(1).getBoxscore(), listTypeReference);
////            for(Map<String, Object> entry: defenseBoxScore) {
////                if(entry.get("name").equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL)) {
////                    assertEquals(6, entry.get("amount"));
////                    assertEquals(9.0, entry.get("fpp"));
////                }
////                else {
////                    assertEquals(0, entry.get("amount"));
////                    if(entry.get("fpp") instanceof Integer)
////                        assertEquals(0, entry.get("fpp"));
////                    else
////                        assertEquals(0.0, entry.get("fpp"));
////                }
////            }
//
//            /*
//             * Box score for Offense
//             */
////            List<Map<String, Object>> offenseBoxScore = mapper.readValue(fantasyPointUpdateEvent.getStatsFantasyPointAthleteUpdateEventList().get(0).getBoxscore(), listTypeReference);
////            for(Map<String, Object> entry: offenseBoxScore) {
////                if(entry.get("name").equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL)) {
////                    assertEquals(0, entry.get("amount"));
////                    assertEquals(12.0, entry.get("fpp"));
////                }
////                else {
////                    assertEquals(0, entry.get("amount"));
////                    if(entry.get("fpp") instanceof Integer)
////                        assertEquals(0, entry.get("fpp"));
////                    else
////                        assertEquals(0.0, entry.get("fpp"));
////                }
////            }
//
//            /*
//             * Timeline for Defense (Away/49ers)
//             */
//            List<Map<String, Object>> defenseTimeline = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(1).getTimeline(), listTypeReference);
//            for (Map<String, Object> entry : defenseTimeline) {
//                System.out.println(entry.get("fpChange") + " ==== " + entry.get("description"));
//            }
////            assertEquals(1, defenseTimeline.size());
//
//            /*
//             * Timeline for Offense (Home/Ravens)
//             */
//            List<Map<String, Object>> offenseTimeline = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(0).getTimeline(), listTypeReference);
//            for (Map<String, Object> entry : offenseTimeline) {
//                System.out.println(entry.get("fpChange") + " ==== " + entry.get("description"));
//            }
////            assertEquals(0, offenseTimeline.size());
//
//            /*
//             * Timeline for Running back
//             */
//            List<Map<String, Object>> rbTimeline = mapper.readValue(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList().get(2).getTimeline(), listTypeReference);
////            assertEquals(2, rbTimeline.size());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
}
