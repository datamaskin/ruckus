package renameme.parser;

import service.ScoringRulesService;
import com.avaje.ebean.Ebean;
import models.stats.mlb.StatsMlbBatting;
import org.junit.Test;
import renameme.FileStatsRetriever;
import stats.parser.mlb.BattingParser;
import stats.translator.mlb.FantasyPointTranslator;
import utilities.BaseTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dmaclean on 7/8/14.
 */
public class BattingParserTest extends BaseTest {
    @Test
    public void testBattingParser() {
        String results = new FileStatsRetriever("test_files/mlb_batting_stats.json").getResults();
        BattingParser parserBatting = new BattingParser(new FantasyPointTranslator(new ScoringRulesService()));

        List<StatsMlbBatting> statsBatting = parserBatting.parse(results);
        List<StatsMlbBatting> listBatting = Ebean.find(StatsMlbBatting.class).findList();
        assertEquals(listBatting.size(), statsBatting.size());

        assertTrue(statsBatting.get(0).getEventId() == 1378361);
    }
}
