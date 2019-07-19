package renameme.parser;

import com.avaje.ebean.Ebean;
import models.stats.nfl.StatsNflGameOdds;
import org.junit.Test;
import renameme.FileStatsRetriever;
import stats.parser.GameOddsParser;
import utilities.BaseTest;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by mwalsh on 7/4/14.
 * Modified by gislas on 8/18/14.
 */
public class StatsNflGameOddsParserTest extends BaseTest {

    @Test
    public void testParse(){
        String results = new FileStatsRetriever("test_files/nfl_odds.json").getResults(); //Might be the nfl testodds instead. f(Was mlb_odds.json)
        GameOddsParser parser = new GameOddsParser();

       // List<StatsNflGameOdds> odds = new ArrayList<>(); //cannot add parsed results to the list of odds, because the parse function doesn't return a value.
        //in other words, test will always fail, as you cannot initialize or assign through function either.
        parser.parse(results); //Should keep this code for now to see if compiles stop failing.
       // Ebean.save(odds);

        List<StatsNflGameOdds> retrievedList = Ebean.find(StatsNflGameOdds.class).findList();
        assertTrue(retrievedList.size() > 0);

    }

}
