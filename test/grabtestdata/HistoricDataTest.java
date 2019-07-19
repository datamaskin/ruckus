package grabtestdata;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import models.sports.Athlete;
import models.sports.League;
import models.sports.Sport;
import models.sports.Team;
import stats.parser.AthleteStatsParser;
import stats.parser.TeamParser;
import stats.provider.mlb.StatsIncProviderMLB;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mwalsh on 7/7/14.
 */
public class HistoricDataTest {

    public void setup() throws Exception {
        Sport baseball = new Sport(1, "Baseball");
        League MLB = new League(baseball, "MLB", "MLB", "Major League", true);
        Ebean.save(Arrays.asList(baseball, MLB));

        byte[] output = Files.readAllBytes(Paths.get("test_files/mlb_all_teams.json"));
        String results = new String(output, "UTF8");
        List<Team> teams = new TeamParser().parse(results);
        Ebean.save(teams);
    }

    ExecutorService executor = Executors.newFixedThreadPool(5);

    public void test() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        byte[] output = Files.readAllBytes(Paths.get("athletes_2012.json"));
        String results = new String(output, "UTF8");
        List<Athlete> athletes = new AthleteStatsParser().parse(results);

        int calls = 0;
        for(Athlete athlete: athletes){
            int year = 2014;
            executor.submit(() -> doSomething(athlete, year));
            calls++;
        }

        LocalDateTime end = LocalDateTime.now();
        System.out.println("started: " + start);
        System.out.println("finished: " + end);
        System.out.println("done " + calls + " in " + start.until(end, ChronoUnit.MINUTES));
    }

    private void doSomething(Athlete athlete, int year){
        String blah = athlete.getStatProviderId()+":"+athlete.getLastName()+","+athlete.getFirstName();
        Map<String, String> map = new HashMap<>();
        map.put(GlobalConstants.STATS_INC_KEY_RESOURCE,
                String.format("stats/players/%d/events/", athlete.getStatProviderId()));
        map.put("sinceYearLast", String.valueOf(year));
        try{
            String something = new StatsIncProviderMLB().getStats(map);
            Files.write(Paths.get("test_files/athlete_stats/" + athlete.getStatProviderId() + "_" + year + ".json"), something.getBytes());
//            System.out.println("success: " + blah);
        } catch (Exception e){
            System.out.println("error:" + blah + ":" + e);
        }
    }

}
