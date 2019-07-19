package stats.retriever;

import common.GlobalConstants;
import models.sports.Athlete;
import models.sports.League;
import stats.parser.AthleteStatsParser;
import stats.provider.StatProviderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 6/5/14.
 */
public class AthleteRetriever implements IAthleteRetriever {

    @Override
    public List<Athlete> getAllAthletesForLeague(League league) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "participants");
            String s = StatProviderFactory.getStatsProvider(league.getAbbreviation()).getStats(map);
            List<Athlete> results = new AthleteStatsParser().parse(s);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Athlete getAthlete(League league, Integer id) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "participants/" + id);
            String s = StatProviderFactory.getStatsProvider(league.getAbbreviation()).getStats(map);
            List<Athlete> results = new AthleteStatsParser().parse(s);
            return results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
