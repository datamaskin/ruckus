package stats.parser.mlb;

import com.avaje.ebean.Ebean;
import models.stats.mlb.StatsMlbFielding;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mwalsh on 7/5/14.
 */
public class FieldingParser {

    public void parse(String results) {
        try {

            JSONObject obj = new JSONObject(results);
            JSONArray eventTypes = obj.getJSONArray("apiResults").getJSONObject(0)
                    .getJSONObject("league")
                    .getJSONArray("players").getJSONObject(0)
                    .getJSONArray("seasons").getJSONObject(0)
                    .getJSONArray("eventType").getJSONObject(0)
                    .getJSONArray("splits").getJSONObject(0)
                    .getJSONArray("events");

            int statProviderId = obj.getJSONArray("apiResults").getJSONObject(0)
                    .getJSONObject("league")
                    .getJSONArray("players").getJSONObject(0)
                    .getInt("playerId");

            for (int index = 0; index < eventTypes.length(); index++) {
                JSONObject eventItem = eventTypes.getJSONObject(index);

                if (eventItem.getJSONObject("playerStats").has("fieldingStats")) {
                    JSONArray fieldingStatsArr = eventItem.getJSONObject("playerStats").getJSONArray("fieldingStats");

                    for (int fieldingStatsIndex = 0; fieldingStatsIndex < fieldingStatsArr.length(); fieldingStatsIndex++) {
                        JSONObject fieldingStats = fieldingStatsArr.getJSONObject(fieldingStatsIndex);
                        StatsMlbFielding stats = new StatsMlbFielding();
                        stats.setStatProviderId(statProviderId);
                        stats.setPosition(fieldingStats.getJSONObject("position").getString("name"));
                        stats.setAssists(fieldingStats.getInt("assists"));
                        stats.setBallsHitInZone(fieldingStats.getInt("ballsHitInZone"));
                        stats.setDoublePlays(fieldingStats.getInt("doublePlays"));
                        stats.setErrors(fieldingStats.getInt("errors"));
                        stats.setFieldingOuts(fieldingStats.getInt("fieldingOuts"));
                        stats.setHitsAllowed(fieldingStats.getInt("hitsAllowed"));
                        stats.setInnings(Float.parseFloat(fieldingStats.getString("innings")));
                        stats.setOpportunities(fieldingStats.getInt("opportunities"));
                        stats.setPutOuts(fieldingStats.getInt("putouts"));
                        stats.setTriplePlays(fieldingStats.getInt("triplePlays"));
                        Ebean.save(stats);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
