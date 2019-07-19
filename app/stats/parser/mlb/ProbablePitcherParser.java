package stats.parser.mlb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import stats.parser.IStatsParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmaclean on 7/15/14.
 */
public class ProbablePitcherParser implements IStatsParser<Integer> {
    public List<Integer> parse(String results) {
        List<Integer> ids = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(results);
            JSONArray eventTypes = obj.getJSONArray("apiResults").getJSONObject(0)
                    .getJSONObject("league")
                    .getJSONObject("season")
                    .getJSONArray("eventType");
            for (int index = 0; index < eventTypes.length(); index++) {
                JSONArray events = eventTypes.getJSONObject(index).getJSONArray("events");
                for (int eventIndex = 0; eventIndex < events.length(); eventIndex++) {
                    JSONObject event = events.getJSONObject(eventIndex);
                    JSONArray pitchers = event.getJSONArray("pitchers");
                    for (int pitcherIndex = 0; pitcherIndex < pitchers.length(); pitcherIndex++) {
                        JSONObject pitcher = pitchers.getJSONObject(pitcherIndex);
                        int id = pitcher.getJSONObject("player").getInt("playerId");
                        ids.add(id);
                    }
                }
            }

            return ids;
        } catch (JSONException e) {
            play.Logger.error("error parsing", e);
            return null;
        }
    }
}
