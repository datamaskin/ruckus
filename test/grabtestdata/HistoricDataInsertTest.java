package grabtestdata;

import service.ScoringRulesService;
import com.avaje.ebean.Ebean;
import models.sports.League;
import models.sports.Sport;
import models.sports.Team;
import stats.parser.TeamParser;
import stats.parser.mlb.BattingParser;
import stats.parser.mlb.FieldingParser;
import stats.parser.mlb.PitchingParser;
import stats.translator.mlb.FantasyPointTranslator;
import utilities.BaseTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mwalsh on 7/7/14.
 */
public class HistoricDataInsertTest extends BaseTest {

    private Sport baseball;

//    @Before
    public void setup() throws Exception {
        baseball = new Sport(1, "Baseball");
        League MLB = new League(baseball, "MLB", "MLB", "Major League", true);
        Ebean.save(Arrays.asList(baseball, MLB));

        byte[] output = Files.readAllBytes(Paths.get("test_files/mlb_all_teams.json"));
        String results = new String(output, "UTF8");
        List<Team> teams = new TeamParser().parse(results);
        Ebean.save(teams);
    }

//    @Test
    public void test() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        File directory = new File("test_files/athlete_stats");
        FantasyPointTranslator fantasyPointTranslator = new FantasyPointTranslator(new ScoringRulesService());
        BattingParser battingParser = new BattingParser(fantasyPointTranslator);
        FieldingParser fieldingParser = new FieldingParser();
        PitchingParser pitchingParser = new PitchingParser(fantasyPointTranslator);

        for(File file: directory.listFiles()){
            byte[] output = Files.readAllBytes(Paths.get("test_files/athlete_stats/"+file.getName()));
            String results = new String(output, "UTF8");
            System.out.println(file.getName()+"->"+results.length());

            battingParser.parse(results);
            fieldingParser.parse(results);
            pitchingParser.parse(results);
        }


        LocalDateTime end = LocalDateTime.now();
        System.out.println("started: " + start);
        System.out.println("finished: " + end);
    }

}
