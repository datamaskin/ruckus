package stats.parser;

import com.avaje.ebean.Ebean;
import dao.DaoFactory;
import models.sports.SportEvent;
import models.stats.nfl.StatsNflGameOdds;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;
import utils.ParserUtil;

import java.util.Date;

/**
 * Created by mwalsh on 7/4/14.
 */
public class GameOddsParser {
    private static final int GAME_SCOPE = 1;

    public void parse(String results) {
        try {

            JSONObject obj = new JSONObject(results);
            JSONArray eventTypes = obj.getJSONArray("apiResults").getJSONObject(0)
                    .getJSONObject("league")
                    .getJSONObject("season")
                    .getJSONArray("eventType");

            for (int index = 0; index < eventTypes.length(); index++) {
                JSONArray lineEvents = eventTypes.getJSONObject(index).getJSONArray("lineEvents");
                for (int eventIndex = 0; eventIndex < lineEvents.length(); eventIndex++) {
                    JSONObject lineEvent = lineEvents.getJSONObject(eventIndex);
                    int eventId = lineEvent.getInt("eventId");
                    SportEvent sportEvent = DaoFactory.getSportsDao().findSportEvent(eventId);
                    Date startDate = ParserUtil.getDate(lineEvent, "startDate");

                    if (sportEvent == null) {
                        continue;
                    }

                    StatsNflGameOdds odds = DaoFactory.getStatsDao().findStatsNflGameOdds(sportEvent);
                    odds = odds == null ? new StatsNflGameOdds() : odds;
                    odds.setStatsEventId(sportEvent.getStatProviderId());
                    odds.setLinesChangeDate(startDate);

                    JSONArray lineJson = lineEvent.getJSONArray("lines");
                    for (int lineJsonIndex = 0; lineJsonIndex < lineJson.length(); lineJsonIndex++) {
                        int scope = GAME_SCOPE;
                        if (lineJson.getJSONObject(lineJsonIndex).has("scope")) {
                            scope = lineJson.getJSONObject(lineJsonIndex).getJSONObject("scope").getInt("scopeId");
                        }
                        if (scope == GAME_SCOPE) {
                            JSONArray lines = lineJson.getJSONObject(lineJsonIndex).getJSONArray("line");
                            for (int l = 0; l < lines.length(); l++) {
                                JSONObject line = lines.getJSONObject(l);
                                if (line.getJSONObject("lineType").getString("name").equalsIgnoreCase("opening")) {
                                    odds.setOpeningFavoriteTeamId(line.getInt("favoriteTeamId"));
                                    odds.setOpeningFavoriteMoney(ParserUtil.parseSignedInt(line.getString("favoriteMoney")));
                                    odds.setOpeningFavoritePoints(new Float(line.getDouble("favoritePoints")));
                                    if (line.has("awayMoney") && !line.getString("awayMoney").equals("null")) {
                                        odds.setOpeningAwayMoney(ParserUtil.parseSignedInt(line.getString("awayMoney")));
                                    }
                                    if (line.has("homeMoney") && !line.getString("homeMoney").equals("null")) {
                                        odds.setOpeningHomeMoney(ParserUtil.parseSignedInt(line.getString("homeMoney")));
                                    }
                                    odds.setOpeningOverMoney(ParserUtil.parseSignedInt(line.getString("overMoney")));
                                    odds.setOpeningTotal(new Float(line.getDouble("total")));
                                    odds.setOpeningUnderMoney(ParserUtil.parseSignedInt(line.getString("underMoney")));
                                    odds.setOpeningUnderdogMoney(ParserUtil.parseSignedInt(line.getString("underdogMoney")));
                                } else if (line.getJSONObject("lineType").getString("name").equalsIgnoreCase("current")) {
                                    odds.setCurrentFavoriteTeamId(line.getInt("favoriteTeamId"));
                                    odds.setCurrentFavoriteMoney(ParserUtil.parseSignedInt(line.getString("favoriteMoney")));
                                    odds.setCurrentFavoritePoints(new Float(line.getDouble("favoritePoints")));
                                    if (line.has("awayMoney") && !line.getString("awayMoney").equals("null")) {
                                        odds.setCurrentAwayMoney(ParserUtil.parseSignedInt(line.getString("awayMoney")));
                                    }
                                    if (line.has("homeMoney") && !line.getString("homeMoney").equals("null")) {
                                        odds.setCurrentHomeMoney(ParserUtil.parseSignedInt(line.getString("homeMoney")));
                                    }
                                    odds.setCurrentOverMoney(ParserUtil.parseSignedInt(line.getString("overMoney")));
                                    try {
                                        odds.setCurrentTotal(new Float(line.getDouble("total")));
                                    } catch (Exception e) {
                                        odds.setCurrentTotal(0f);
                                    }
                                    odds.setCurrentUnderMoney(ParserUtil.parseSignedInt(line.getString("underMoney")));
                                    odds.setCurrentUnderdogMoney(ParserUtil.parseSignedInt(line.getString("underdogMoney")));
                                }
                            }
                        }
                    }
                    try {
                        StatsNflGameOdds dbOdds = DaoFactory.getStatsDao().findStatsNflGameOdds(sportEvent);
                        if (dbOdds == null) {
                            Ebean.save(odds);
                        }
                    } catch (Exception e) {
                        Logger.warn(e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
