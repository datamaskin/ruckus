package renameme.parser;

import service.ScoringRulesService;
import com.avaje.ebean.Ebean;
import dao.SportsDao;
import models.sports.Athlete;
import models.sports.Position;
import models.sports.Sport;
import models.stats.mlb.StatsMlbBatting;
import models.stats.mlb.StatsMlbFielding;
import models.stats.mlb.StatsMlbPitching;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import renameme.FileStatsRetriever;
import stats.parser.mlb.BattingParser;
import stats.parser.mlb.FieldingParser;
import stats.parser.mlb.PitchingParser;
import stats.translator.mlb.FantasyPointTranslator;
import utilities.BaseTest;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by mwalsh on 7/5/14.
 */
public class StatsMlbParserTest extends BaseTest {

    private Athlete athlete;
    private String results;

    @Before
    public void setup() {
        athlete = new Athlete(69, "firstName", "lastName", null, "");
        Ebean.save(athlete);
        results = new FileStatsRetriever("test_files/mlb_stats_year_to_date.json").getResults();
    }

    @Test
    public void testBattingParser() {
        BattingParser parserBatting = new BattingParser(new FantasyPointTranslator(new ScoringRulesService()));

        /*List<StatsMlbBatting> statsBatting = parserBatting.parse(results);
        Ebean.save(statsBatting);
        List<StatsMlbBatting> list = Ebean.find(StatsMlbBatting.class).findList();
        assertEquals(list.size(), statsBatting.size());*/
        parserBatting.parse(results);
        List<StatsMlbBatting> list = Ebean.find(StatsMlbBatting.class).findList();
        assertTrue(list.size() > 0);
    }

    @Test
    public void testFieldingParser() {
        SportsDao utilityDao = EasyMock.createMock(SportsDao.class);
        FieldingParser parserFielding = new FieldingParser();

        Position rightField = new Position(1, "Right field", "RF", Sport.BASEBALL);
        EasyMock.expect(utilityDao.findPosition(7)).andReturn(rightField);

        /*List<StatsMlbFielding> statsFielding = parserFielding.parse(results);
        Ebean.save(statsFielding);
        List<StatsMlbFielding> listFielding = Ebean.find(StatsMlbFielding.class).findList();
        assertEquals(listFielding.size(), statsFielding.size());*/

        parserFielding.parse(results);
        List<StatsMlbFielding> listFielding = Ebean.find(StatsMlbFielding.class).findList();
        assertTrue(listFielding.size() > 0);
    }

    @Test
    public void testPitchingParser() {
        FantasyPointTranslator fantasyPointTranslator = new FantasyPointTranslator(new ScoringRulesService());
        PitchingParser parserPitching = new PitchingParser(fantasyPointTranslator);

        /*List<StatsMlbPitching> statsPitching = parserPitching.parse(results);
        Ebean.save(statsPitching);
        List<StatsMlbPitching> listPitching = Ebean.find(StatsMlbPitching.class).findList();
        assertEquals(listPitching.size(), statsPitching.size());*/
        parserPitching.parse(results);
        List<StatsMlbPitching> listPitching = Ebean.find(StatsMlbPitching.class).findList();
        assertTrue(listPitching.size() > 0);
    }

}
