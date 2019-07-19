package stats.parser;

import service.edge.TestScoringRulesService;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stats.parser.nfl.BoxscoreParser;
import stats.translator.nfl.FantasyPointTranslator;
import utilities.BaseTest;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by dmaclean on 8/19/14.
 */
public class BoxscoreParserTest extends BaseTest {
    private BoxscoreParser boxscoreParser;

    @Before
    public void setUp() {
        boxscoreParser = new BoxscoreParser(new FantasyPointTranslator(new TestScoringRulesService()));
        boxscoreParser.setTranslator(new FantasyPointTranslator(new TestScoringRulesService()));
    }

    @After
    public void tearDown() {
        boxscoreParser = null;
    }

    @Test
    public void testNonFinalState() {
        try {
            String xml = FileUtils.readFileToString(new File("test_files/nfl_boxscores/nfl_event_boxscore_nonfinal.json"));
            List<Map<Integer, BigDecimal>> results = boxscoreParser.parse(xml);

            assertEquals(0, results.size());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFinalState() {
        try {
            String xml = FileUtils.readFileToString(new File("test_files/nfl_boxscores/nfl_event_boxscore.json"));
            List<Map<Integer, BigDecimal>> results = boxscoreParser.parse(xml);

            assertEquals(1, results.size());
            assertEquals(new BigDecimal("9.0"), results.get(0).get(364));
            assertEquals(new BigDecimal("2.0"), results.get(0).get(339));
            assertEquals(new BigDecimal("15.8"), results.get(0).get(381216));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFinalState_20140818_CLE_WAS() {
        try {
            String xml = FileUtils.readFileToString(new File("test_files/nfl_boxscores/nfl_event_boxscore_20140818_CLE_WAS.json"));
            List<Map<Integer, BigDecimal>> results = boxscoreParser.parse(xml);

            assertEquals(1, results.size());
            assertEquals(new BigDecimal("10.5"), results.get(0).get(363));
            assertEquals(new BigDecimal("16.0"), results.get(0).get(329));
            assertEquals(new BigDecimal("4.88"), results.get(0).get(450794));       // RG3
            assertEquals(new BigDecimal("8.1"), results.get(0).get(494724));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
