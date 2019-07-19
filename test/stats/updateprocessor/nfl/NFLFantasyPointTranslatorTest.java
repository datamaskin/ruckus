package stats.updateprocessor.nfl;

import service.edge.TestScoringRulesService;
import org.junit.After;
import org.junit.Before;
import stats.translator.nfl.FantasyPointTranslator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by dan on 6/9/14.
 */
public class NFLFantasyPointTranslatorTest {
    private FantasyPointTranslator translator;

    @Before
    public void setUp() {
        translator = new FantasyPointTranslator(new TestScoringRulesService());
    }

    @After
    public void tearDown() {
        translator = null;
    }

//    @Test
//    public void testPingEvent() {
//        String data = "Ping Sun Jan 20 14:02:00 2013";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue("Expected map to be empty", events.isEmpty());
//    }
//
//    @Test
//    public void testNoFantasyPointImpact() {
//        String data = "20130120001|ENDTRANS";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue("Expected map to be empty", events.isEmpty());
//    }
//
//    @Test
//    public void testCompletePass_NoTouchdown() {
//        String data = "20130120001|CP,1,8780,7203,M,16,1,10,47,0,1,Tackled,8380,0.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 2);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        // QB
//        assertTrue(event.getAthleteId() == 8780 && event.getFantasyPoints().doubleValue() == 0.64 && event.getEventDescription().equals("+0.64 16 passing yards"));
//
//        event = events.get(1);
//        // Receiver
//        assertTrue(event.getAthleteId() == 7203 && event.getFantasyPoints().doubleValue() == 1.6 && event.getEventDescription().equals("+1.6 16 receiving yards"));
//    }
//
//    @Test
//    public void testCompletePass_Touchdown() {
//        String data = "20130120001|CP,1,8780,24793,L,46,1,0,0,6,1,Touchdown,0,0.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 2);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        // QB
//        assertTrue(event.getAthleteId() == 8780 && event.getFantasyPoints().doubleValue() == 5.84 && event.getEventDescription().equals("+5.84 46 passing yards, passing touchdown"));
//
//        event = events.get(1);
//        // Receiver
//        assertTrue(event.getAthleteId() == 24793 && event.getFantasyPoints().doubleValue() == 10.6 && event.getEventDescription().equals("+10.6 46 receiving yards, receiving touchdown"));
//    }
//
//    @Test
//    public void testRush_NoTouchdown() {
//        String data = "20130120001|RUSH,1,6913,R,1,2,9,63,0,1,Tackled,24066,8351,right tackle.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 6913 && event.getFantasyPoints().doubleValue() == 0.1 && event.getEventDescription().equals("+0.1 1 rushing yards"));
//    }
//
//    @Test
//    public void testRush_Touchdown() {
//        String data = "20130120001|RUSH,25,25771,R,15,1,0,0,6,25,Touchdown,0,0,right end.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 25771 && event.getFantasyPoints().doubleValue() == 7.5 && event.getEventDescription().equals("+7.5 15 rushing yards, rushing touchdown"));
//    }
//
//    @Test
//    public void testFumble_FumblingTeamRecovers() {
//        String data = "20130120017|FUM,33,7139,33,0,0,T,3,4,84,0,33,8787,F,0,0.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.isEmpty());
//    }
//
//    @Test
//    public void testFumble_FumblingTeamLosesBall() {
//        String data = "20130120001|FUM,1,8780,25,24794,0,T,1,10,63,0,25,0,F,0,0.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 8780 && event.getFantasyPoints().doubleValue() == -1 && event.getEventDescription().equals("-1 lost fumble"));
//    }
//
//    @Test
//    public void testPuntReturn_Touchdown() {
//        String data = "20130120017|PR,33,7952,17,7027,0,F,1,10,69,6,17,24362,0,0.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 7952 && event.getFantasyPoints().doubleValue() == 6 && event.getEventDescription().equals("+6 punt return touchdown"));
//    }
//
//    @Test
//    public void testPuntReturn_NoTouchdown() {
//        String data = "20130120017|PR,33,7952,17,7027,0,F,1,10,69,0,17,24362,0,0.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.isEmpty());
//    }
//
//    @Test
//    public void testKickReturn_Touchdown() {
//        String data = "20130120017|KR,33,8327,9,F,1,10,80,6,33,7867,35,Touchdown,9078,0.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 8327 && event.getFantasyPoints().doubleValue() == 6 && event.getEventDescription().equals("+6 kick return touchdown"));
//    }
//
//    @Test
//    public void testKickReturn_NoTouchdown() {
//        String data = "20130120017|KR,17,0,0,T,1,10,80,0,17,26534,35,Touchback,0,0.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.isEmpty());
//    }
//
//    @Test
//    public void testFieldGoalMade_31Yards() {
//        String data = "20130120017|FGA,17,7867,31,1,0,0,3,17,24126,25378,Made field goal.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 7867 && event.getFantasyPoints().doubleValue() == 3 && event.getEventDescription().equals("+3 31 yard field goal made"));
//    }
//
//    @Test
//    public void testFieldGoalMade_41Yards() {
//        String data = "20130120017|FGA,17,7867,41,1,0,0,3,17,24126,25378,Made field goal.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 7867 && event.getFantasyPoints().doubleValue() == 4 && event.getEventDescription().equals("+4 41 yard field goal made"));
//    }
//
//    @Test
//    public void testFieldGoalMade_51Yards() {
//        String data = "20130120017|FGA,17,7867,51,1,0,0,3,17,24126,25378,Made field goal.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 7867 && event.getFantasyPoints().doubleValue() == 5 && event.getEventDescription().equals("+5 51 yard field goal made"));
//    }
//
//    @Test
//    public void testFieldGoalMissed() {
//        String data = "20130120001|FGA,25,4587,38,1,10,72,0,1,0,0,Missed field goal.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.isEmpty());
//    }
//
//    @Test
//    public void testPATMade() {
//        String data = "20130120001|PAT,25,4587,1,0,0,Score(Good).";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 4587 && event.getFantasyPoints().doubleValue() == 1 && event.getEventDescription().equals("+1 PAT made"));
//    }
//
//    @Test
//    public void testPATMissed() {
//        String data = "20130120001|PAT,25,4587,0,0,0,Missed.";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.isEmpty());
//    }
//
//    @Test
//    public void testTwoPointConversionGood_Passing() {
//        String data = "20130120001|2PP,1,8780,7203,1,0,0,2,1,Good (Score)";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 2);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 8780 && event.getFantasyPoints().doubleValue() == 2 && event.getEventDescription().equals("+2 Two-point conversion made"));
//
//        event = events.get(1);
//        assertTrue(event.getAthleteId() == 7203 && event.getFantasyPoints().doubleValue() == 2 && event.getEventDescription().equals("+2 Two-point conversion made"));
//    }
//
//    @Test
//    public void testTwoPointConversionNoGood_Passing() {
//        String data = "20130120001|2PP,1,8780,7203,1,0,0,0,1,No Good Pass Incomplete";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.isEmpty());
//    }
//
//    @Test
//    public void testTwoPointConversionGood_Rushing() {
//        String data = "20130120001|2PR,1,8780,L,1,0,0,2,1,Good (Score)";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.size() == 1);
//
//        FantasyPointUpdateEvent event = events.get(0);
//        assertTrue(event.getAthleteId() == 8780 && event.getFantasyPoints().doubleValue() == 2 && event.getEventDescription().equals("+2 Two-point conversion made"));
//    }
//
//    @Test
//    public void testTwoPointConversionNoGood_Rushing() {
//        String data = "20130120001|2PP,1,8780,L,1,0,0,0,1,No Good Pass Incomplete";
//
//        List<FantasyPointUpdateEvent> events = translator.translate(data);
//
//        assertTrue(events.isEmpty());
//    }
}
