package stats.parser;

import common.GlobalConstants;
import dao.ISportsDao;
import dao.SportsDao;
import models.sports.Athlete;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.Team;
import org.junit.After;
import org.junit.Before;
import stats.parser.nfl.DefenseParser;
import utilities.BaseTest;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 8/6/14.
 */
public class StatsNflDefenseParserTest extends BaseTest {

    private DefenseParser parser;
    private Athlete athlete;
    private SportEvent sportEvent;
    private Team team;
    private Team opponent1;
    private Team opponent2;
    private Team opponent3;
    private Team opponent4;
    private Team opponent5;
    private Team opponent6;
    private Team opponent7;
    private Team opponent8;
    private Team opponent9;
    private Team opponent10;
    private Team opponent11;
    private Team opponent12;
    private Team opponent13;
    private Team opponent14;
    private Team opponent15;
    private Team opponent16;

    private ISportsDao sportsDao;

    @Before
    public void setUp() {
        sportsDao = new SportsDao();
        parser = new DefenseParser();

        team = new Team(League.NFL, "Buffalo", "Bills", "BUF", 324);
        sportsDao.saveTeam(team);
        opponent1 = new Team(League.NFL, "New England", "Patriots", "NE", 348);
        sportsDao.saveTeam(opponent1);
        opponent2 = new Team(League.NFL, "Carolina", "Panthers", "CAR", 364);
        sportsDao.saveTeam(opponent2);
        opponent3 = new Team(League.NFL, "New York", "Jets", "NYJ", 352);
        sportsDao.saveTeam(opponent3);
        athlete = new Athlete(324, "", "Buffalo Bills", team, "");
        sportsDao.saveAthlete(athlete);

        sportEvent = new SportEvent(1321708, League.NFL, new Date(), "", "", 60, false, 2013, 1, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON);
        sportsDao.saveSportEvent(sportEvent);
    }

    @After
    public void tearDown() {
        parser = null;
    }

//    @Test
//    public void testParse() {
//        FileStatProvider fileStatProvider = new FileStatProvider();
//        fileStatProvider.setFilename("test_files/nfl_events_defense.json");
//        try {
//            String json = fileStatProvider.getStats(null);
//            List<StatsNflDefenseByEvent> statsNflDefenseByEvents = parser.parse(json);
//
//            assertEquals(16, statsNflDefenseByEvents.size());
//            assertEquals(athlete, statsNflDefenseByEvents.get(0).getAthlete());
//            assertEquals(opponent1, statsNflDefenseByEvents.get(0).getOpponent());
//            assertEquals(2013, statsNflDefenseByEvents.get(0).getSeason());
//            assertEquals(sportEvent, statsNflDefenseByEvents.get(0).getSportEvent());
//            assertEquals(1, statsNflDefenseByEvents.get(0).getWeek());
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
}
