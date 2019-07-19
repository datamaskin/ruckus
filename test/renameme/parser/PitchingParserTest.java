package renameme.parser;

import service.ScoringRulesService;
import com.avaje.ebean.Ebean;
import models.stats.mlb.StatsMlbPitching;
import org.junit.Test;
import renameme.FileStatsRetriever;
import stats.parser.mlb.PitchingParser;
import stats.translator.mlb.FantasyPointTranslator;
import utilities.BaseTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mwalsh on 7/6/14.
 * Modified by gislas on 8/18/14.
 */
public class PitchingParserTest extends BaseTest {

    @Test
    public void testPitchingParser() {
        String results = new FileStatsRetriever("test_files/mlb_pitching_stats.json").getResults();

        FantasyPointTranslator fantasyPointTranslator = new FantasyPointTranslator(new ScoringRulesService());
        PitchingParser parserPitching = new PitchingParser(fantasyPointTranslator);

        List<StatsMlbPitching> statsPitching = parserPitching.parse(results);
        List<StatsMlbPitching> listPitching = Ebean.find(StatsMlbPitching.class).findList();
        assertEquals(listPitching.size(), statsPitching.size());

        assertTrue(statsPitching.get(0).getEventId() == 1378563);
    }
}
