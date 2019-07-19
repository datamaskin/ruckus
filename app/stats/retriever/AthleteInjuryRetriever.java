package stats.retriever;

import common.GlobalConstants;
import models.sports.League;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import stats.provider.StatProviderFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwalsh on 6/30/14.
 */
public class AthleteInjuryRetriever implements IAthleteInjuryRetriever {

    @Override
    public Map<Integer, String> getAthleteInjuries(League league) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "injuries");

            LocalDate localDate = LocalDate.now();
            int season = localDate.getYear();
            if(league.getAbbreviation().equalsIgnoreCase("NFL")){
                if(localDate.getMonthValue() < 6){
                    season = season - 1;
                }
            }
            map.put("season", String.valueOf(season));

            String results = StatProviderFactory.getStatsProvider(league.getAbbreviation()).getStats(map);
            return parseInjuries(league, results);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<Integer,String> parseInjuries(League league, String results) {
        if (league == null) {
            throw new IllegalArgumentException("");
        }

        Map<Integer, String> injuries = new HashMap<>();

        try {
            JSONObject resultJson = new JSONObject(results);
            JSONObject apiResults = (JSONObject) resultJson.getJSONArray("apiResults").get(0);
            JSONArray injuriesJson = apiResults.getJSONObject("league").getJSONArray("seasons")
                    .getJSONObject(0).getJSONArray("injuries");

            for (int i = 0; i < injuriesJson.length(); i++) {
                JSONObject injuryEvent = injuriesJson.getJSONObject(i);
                int playerId = injuryEvent.getJSONObject("player").getInt("playerId");
                String injury = injuryEvent.getJSONObject("status").getString("description");
                String description = injuryEvent.getString("description");
                injuries.put(playerId, injury+"|"+description);
            }

            return injuries;

        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }

    }
}
