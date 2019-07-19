package stats.parser;

import dao.DaoFactory;
import models.sports.League;
import models.sports.Team;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamParser implements IStatsParser<Team> {

    @Override
    public List<Team> parse(String results) {
        try {
            JSONObject resultJson = new JSONObject(results);
            JSONObject apiResults = (JSONObject) resultJson.getJSONArray("apiResults").get(0);
            JSONArray conferences = apiResults.getJSONObject("league").getJSONObject("season").getJSONArray("conferences");

            League league = DaoFactory.getSportsDao().findLeague(apiResults.getJSONObject("league").getString("abbreviation"));

            if (league != null) {
                List<Team> teamList = new ArrayList<>();

                for (int i = 0; i < conferences.length(); i++) {
                    JSONArray divisions = conferences.getJSONObject(i).getJSONArray("divisions");
                    for (int j = 0; j < divisions.length(); j++) {
                        JSONArray teamJson = divisions.getJSONObject(j).getJSONArray("teams");
                        for (int k = 0; k < teamJson.length(); k++) {
                            Team team = new Team(league,
                                    teamJson.getJSONObject(k).getString("location"),
                                    teamJson.getJSONObject(k).getString("nickname"),
                                    teamJson.getJSONObject(k).getString("abbreviation"),
                                    teamJson.getJSONObject(k).getInt("teamId"));
                            teamList.add(team);
                        }
                    }
                }
                return teamList;
            }

            throw new IllegalArgumentException("Could not parse results.");

        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
