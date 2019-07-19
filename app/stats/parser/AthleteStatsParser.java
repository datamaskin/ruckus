package stats.parser;

import dao.DaoFactory;
import models.sports.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AthleteStatsParser implements IStatsParser<Athlete> {

    @Override
    public List<Athlete> parse(String results) {
        try {
            JSONObject resultJson = new JSONObject(results);
            JSONObject apiResults = (JSONObject) resultJson.getJSONArray("apiResults").get(0);
            JSONArray athletesJson = apiResults.getJSONObject("league").getJSONArray("players");

            League league =
                    DaoFactory.getSportsDao().findLeague(apiResults.getJSONObject("league").getString(
                            "abbreviation"));

            if (league != null) {
                List<Athlete> athletesList = new ArrayList<>();
                for (int i = 0; i < athletesJson.length(); i++) {
                    JSONObject athlete = athletesJson.getJSONObject(i);
                    Athlete a = jsonToObject(league, athlete);
                    if (a != null) {
                        athletesList.add(a);
                    }
                }
                return athletesList;
            }

            throw new IllegalArgumentException("Could not parse results.");

        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Athlete jsonToObject(League league, JSONObject json) throws JSONException {
        List<Position> positions = new ArrayList<>();

        JSONArray positionsJson = json.getJSONArray("positions");
        Sport sport = league.getSport();
        for (int j = 0; j < positionsJson.length(); j++) {
            JSONObject positionJson = positionsJson.getJSONObject(j);
            String abbreviation = positionJson.getString("abbreviation");
            Position position = DaoFactory.getSportsDao().findPosition(abbreviation, sport);
            if (position != null && positions.contains(position) == false) {
                positions.add(position);
            }

            if(league.equals(League.NFL) && abbreviation.equalsIgnoreCase("FB")){
                positions.add(Position.FB_RUNNINGBACK);
            }
        }

        if (positions.isEmpty()) {
            return null;
        }

        if (sport.equals(Sport.FOOTBALL)
                && (positions.contains(Position.FB_RUNNINGBACK)
                || positions.contains(Position.FB_TIGHT_END)
                || positions.contains(Position.FB_WIDE_RECEIVER))) {
            positions.add(Position.FB_FLEX);
        } else if (sport.equals(Sport.BASEBALL) &&
                (positions.contains(Position.BS_OUTFIELD))) {
            positions.add(Position.BS_FLEX);
        }

        String uniform = "";
        String firstName = "";
        String lastName = "";
        int statProviderId = 0;

        try {
            firstName = json.getString("firstName");
        } catch (JSONException e) {
        }

        try {
            lastName = json.getString("lastName");
        } catch (JSONException e) {
        }

        try {
            uniform = json.getString("uniform");
        } catch (JSONException e) {
        }

        try {
            statProviderId = json.getInt("playerId");
        } catch (JSONException e) {
            return null;
        }

        Team team = null;
        if (json.has("team")) {
            int teamId = json.getJSONObject("team").getInt("teamId");
            team = DaoFactory.getSportsDao().findTeam(teamId);
        }

        Athlete athlete =
                new Athlete(
                        statProviderId,
                        firstName,
                        lastName,
                        team,
                        uniform);

        athlete.setPositions(positions);
        athlete.setActive(json.getBoolean("isActive"));

        return athlete;
    }

}
