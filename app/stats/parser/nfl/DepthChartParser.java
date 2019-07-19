package stats.parser.nfl;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import dao.DaoFactory;
import models.sports.Athlete;
import models.sports.SportEvent;
import models.sports.Team;
import models.stats.nfl.StatsNflDepthChart;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mgiles on 8/20/14.
 */
public class DepthChartParser {

    public Map<String, String> parse(String results) {
        Map<String, String> cache = new HashMap<>();
        int season = -1;
        int week = -1;
        int eventTypeId = -1;
        try {
            JSONObject jObj = new JSONObject(results);
            JSONObject jApiResults = jObj.getJSONArray("apiResults").getJSONObject(0);
            JSONObject jLeague = jApiResults.getJSONObject("league");
            JSONObject jSeason = jLeague.getJSONObject("season");
            JSONArray jTeams = jSeason.getJSONArray("teams");

            season = jSeason.getInt("season");
            for (int i = 0; i < jTeams.length(); i++) {
                JSONObject jTeam = jTeams.getJSONObject(i);
                int teamId = jTeam.getInt("teamId");
                Team team = DaoFactory.getSportsDao().findTeam(teamId);
                JSONArray jPositions = jTeam.getJSONArray("positions");
                for (int j = 0; j < jPositions.length(); j++) {
                    JSONObject jPosition = jPositions.getJSONObject(j);
                    String abbr = jPosition.getString("abbreviation");
                    JSONArray jDepthCharts = jPosition.getJSONArray("depthChart");
                    for (int k = 0; k < jDepthCharts.length(); k++) {
                        JSONObject jDepthChart = jDepthCharts.getJSONObject(k);
                        int depth = jDepthChart.getInt("depth");
                        JSONObject jPlayer = jDepthChart.getJSONObject("player");
                        int playerId = jPlayer.getInt("playerId");
                        Athlete athlete = DaoFactory.getSportsDao().findAthlete(playerId);
                        if (athlete == null) {
                            continue;
                        }
                        athlete.setDepth(depth);
                        DaoFactory.getSportsDao().saveAthlete(athlete);
                        Integer[] types = {GlobalConstants.EVENT_TYPE_NFL_PRE_SEASON, GlobalConstants.EVENT_TYPE_NFL_REGULAR_SEASON, GlobalConstants.EVENT_TYPE_NFL_POST_SEASON};

                        SportEvent nextEvent = DaoFactory.getSportsDao().findNextFutureSportEvent(athlete, Arrays.asList(types));
                        week = nextEvent.getWeek();
                        season = nextEvent.getSeason();
                        eventTypeId = nextEvent.getEventTypeId();

                        StatsNflDepthChart dbChart = DaoFactory.getStatsDao()
                                .findStatsNflDepthChart(athlete, season, week, eventTypeId);
                        if (dbChart == null) {
                            dbChart = new StatsNflDepthChart();
                        }
                        dbChart.setAthlete(athlete);
                        dbChart.setTeam(team);
                        dbChart.setDepth(depth);
                        dbChart.setEventTypeId(eventTypeId);
                        dbChart.setSeason(season);
                        dbChart.setWeek(week);
                        dbChart.setDepthPosition(abbr);
                        try {
                            Ebean.save(dbChart);
                        } catch (Exception e) {
                            Logger.warn(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cache.put("id", season + "_" + week + "_" + eventTypeId);
        cache.put("results", results);
        return cache;
    }
}
