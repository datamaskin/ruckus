package stats.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.DaoFactory;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.Team;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ParserUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SportEventParser {

    public List<SportEvent> parse(League league, String results) {
        try {

            List<SportEvent> allEvents = new ArrayList<>();

            JSONObject obj = new JSONObject(results);
            JSONObject jseason = obj.getJSONArray("apiResults").getJSONObject(0)
                    .getJSONObject("league")
                    .getJSONObject("season");
            int season = jseason.optInt("season");

            JSONArray eventTypes = jseason.getJSONArray("eventType");
            for (int index = 0; index < eventTypes.length(); index++) {
                int eventTypeId = eventTypes.getJSONObject(index).getInt("eventTypeId");

                JSONArray events = eventTypes.getJSONObject(index).getJSONArray("events");
                for (int eventIndex = 0; eventIndex < events.length(); eventIndex++) {
                    JSONObject event = events.getJSONObject(eventIndex);
                    // Skip the Pro Bowl
                    if (eventTypeId == 3 && league.equals(League.NFL)) {
                        continue;
                    }
                    int statProviderId = event.getInt("eventId");
                    int week = -1;
                    if (league.equals(League.NFL)) {
                        week = event.optInt("week");
                    }
                    Date startTime = ParserUtil.getDate(event, "startDate");
                    if (startTime == null) {
                        continue;
                    }

                    int eventStatusId = event.getJSONObject("eventStatus").getInt("eventStatusId");
                    boolean complete = eventStatusId == GlobalConstants.STATS_INC_GAME_STATUS_FINAL_CODE;

                    String description = getDescription(event);
                    String shortDescription = getShortDescription(event);
                    int unitsRemaining = 0;
                    if (league.getAbbreviation().equals(League.MLB.getAbbreviation()) && !complete) {
                        unitsRemaining = 9 - event.getJSONObject("eventStatus").getInt("inning");
                    } else if (league.getAbbreviation().equals(League.NFL.getAbbreviation()) && !complete) {
                        unitsRemaining = 60;
                    }

                    SportEvent sportEvent = new SportEvent(statProviderId,
                            league, startTime, description,
                            shortDescription, unitsRemaining,
                            complete, season, week, eventTypeId);

                    JSONArray teamsArray = event.getJSONArray("teams");
                    List<Team> teams = new ArrayList<>();
                    for (int teamsIndex = 0; teamsIndex < teamsArray.length(); teamsIndex++) {
                        int teamStatId = teamsArray.getJSONObject(teamsIndex).getInt("teamId");
                        Team team = DaoFactory.getSportsDao().findTeam(teamStatId);
                        teams.add(team);
                    }
                    sportEvent.setTeams(teams);

                    allEvents.add(sportEvent);

                }
            }

            return allEvents;
        } catch (JSONException e) {
            play.Logger.error("error parsing", e);
            return null;
        } catch (JsonProcessingException e) {
            play.Logger.error("Could not retrieve description.", e);
            return null;
        }
    }

    private String getDescription(JSONObject event) throws JSONException, JsonProcessingException {
        GameDescription gd = new GameDescription();
        JSONArray teams = event.getJSONArray("teams");
        for (int teamIndex = 0; teamIndex < teams.length(); teamIndex++) {
            JSONObject team = teams.getJSONObject(teamIndex);
            String locationTypeName = team.getJSONObject("teamLocationType").getString("name");
            if (locationTypeName.equalsIgnoreCase("away")) {
                gd.awayTeam = team.getString("location") + " " + team.getString("nickname");
            } else if (locationTypeName.equalsIgnoreCase("home")) {
                gd.homeTeam = team.getString("location") + " " + team.getString("nickname");
            }
        }
        gd.venue = event.getJSONObject("venue").getString("name");
        gd.location = event.getJSONObject("venue").getString("city");
        if (event.getJSONObject("venue").has("state")) {
            gd.location += ", ";
            gd.location += event.getJSONObject("venue").getJSONObject("state").getString("abbreviation");
        }
        return new ObjectMapper().writeValueAsString(gd);
    }

    private String getShortDescription(JSONObject event) throws JSONException, JsonProcessingException {
        GameShortDescription gd = new GameShortDescription();
        JSONArray teams = event.getJSONArray("teams");
        for (int teamIndex = 0; teamIndex < teams.length(); teamIndex++) {
            JSONObject team = teams.getJSONObject(teamIndex);
            String locationTypeName = team.getJSONObject("teamLocationType").getString("name");
            if (locationTypeName.equalsIgnoreCase("away")) {
                gd.awayTeam = team.getString("abbreviation").toUpperCase();
                gd.awayId = team.getString("teamId");
            } else if (locationTypeName.equalsIgnoreCase("home")) {
                gd.homeTeam = team.getString("abbreviation").toUpperCase();
                gd.homeId = team.getString("teamId");
            }
        }
        gd.awayScore = 0;
        gd.homeScore = 0;

        return new ObjectMapper().writeValueAsString(gd);
    }

    /*
    "location": "Cincinnati",
    "nickname": "Reds",
    "abbreviation": "Cin",
    "teamLocationType": {
        "teamLocationTypeId": 2,
        "name": "away"
    },
     * */
    public static class GameDescription {
        public String homeTeam;
        public String awayTeam;
        public String venue;
        public String location;
    }

    public static class GameShortDescription {
        public String homeId;
        public String homeTeam;
        public String awayId;
        public String awayTeam;
        public Integer homeScore;
        public Integer awayScore;
    }

}
