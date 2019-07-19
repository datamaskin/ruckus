package stats.parser.mlb;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.stats.mlb.StatsMlbBatting;
import org.json.JSONArray;
import org.json.JSONObject;
import stats.parser.IStatsParser;
import stats.translator.mlb.FantasyPointTranslator;
import utils.ListUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mwalsh on 7/5/14.
 */
public class BattingParser implements IStatsParser<StatsMlbBatting> {

//    private static MLBFantasyPointTranslator translator = new MLBFantasyPointTranslator(
//            (ScoringRulesManager) DistributedServices.getContext().getBean("ScoringRulesManager"));

    private FantasyPointTranslator fantasyPointTranslator;

    public BattingParser(FantasyPointTranslator fantasyPointTranslator) {
        this.fantasyPointTranslator = fantasyPointTranslator;
    }

    public List<StatsMlbBatting> parse(String results) {
        try {
            List<StatsMlbBatting> statsList = new ArrayList<>();

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
                    .optInt("playerId");

            LinkedList<BigDecimal> stack = new LinkedList<>();

            for (int index = 0; index < eventTypes.length(); index++) {
                JSONObject eventItem = eventTypes.getJSONObject(index);

                if (eventItem.getJSONObject("playerStats").has("battingStats")) {
                    JSONObject battingStats = eventItem.getJSONObject("playerStats").getJSONObject("battingStats");
                    StatsMlbBatting stats = Ebean.find(StatsMlbBatting.class).where().eq("statProviderId", statProviderId)
                            .eq("eventId", eventItem.optInt("eventId")).findUnique();
                    if (stats == null) {
                        stats = new StatsMlbBatting();

                        stats.setEventId(eventItem.optInt("eventId"));
                        stats.setStatProviderId(statProviderId);
                        stats.setAtBats(battingStats.optInt("atBats"));
                        stats.setFlyballs(battingStats.getJSONObject("ballsHit").optInt("flyballs"));
                        stats.setGroundballs(battingStats.getJSONObject("ballsHit").optInt("groundballs"));
                        stats.setLineDrives(battingStats.getJSONObject("ballsHit").optInt("lineDrives"));
                        stats.setGroundIntoDoublePlaysOpportunities(battingStats.getJSONObject("groundIntoDoublePlays").optInt("opportunities"));
                        stats.setGroundIntoDoublePlaysTotal(battingStats.getJSONObject("groundIntoDoublePlays").optInt("total"));
                        stats.setHitByPitch(battingStats.optInt("hitByPitch"));
                        stats.setHitsDoubles(battingStats.getJSONObject("hits").optInt("doubles"));
                        stats.setHitsExtraBaseHits(battingStats.getJSONObject("hits").optInt("extraBaseHits"));
                        stats.setHitsHomeRuns(battingStats.getJSONObject("hits").optInt("homeRuns"));
                        stats.setHitsSingles(battingStats.getJSONObject("hits").optInt("singles"));
                        stats.setHitsTotal(battingStats.getJSONObject("hits").optInt("total"));
                        stats.setHitsTriples(battingStats.getJSONObject("hits").optInt("triples"));

                        stats.setOnBasePercentage(battingStats.has("onBasePercentage")
                                ? Float.parseFloat(battingStats.getString("onBasePercentage")) : null);

                        stats.setSluggingPercentage(battingStats.has("sluggingPercentage")
                                ? Float.parseFloat(battingStats.getString("sluggingPercentage")) : null);

                        stats.setOnBasePlusSluggingPercentage(battingStats.has("onBasePlusSluggingPercentage")
                                ? Float.parseFloat(battingStats.getString("onBasePlusSluggingPercentage")) : null);

                        stats.setPitchesSeenRatePerPlateAppearance(battingStats.getJSONObject("pitchesSeen").has("ratePerPlateAppearance")
                                ? battingStats.getJSONObject("pitchesSeen").optInt("ratePerPlateAppearance") : null);

                        stats.setPitchesSeenTotal(battingStats.getJSONObject("pitchesSeen").optInt("total"));
                        stats.setPlateAppearances(battingStats.optInt("plateAppearances"));
                        stats.setRunsBattedInGameWinning(battingStats.getJSONObject("runsBattedIn").optInt("gameWinning"));
                        stats.setRunsBattedInTotal(battingStats.getJSONObject("runsBattedIn").optInt("total"));
                        stats.setRunsScored(battingStats.optInt("runsScored"));
                        stats.setSacrificesHits(battingStats.getJSONObject("sacrifices").optInt("hits"));
                        stats.setSacrificesFlies(battingStats.getJSONObject("sacrifices").optInt("flies"));
                        stats.setStolenBasesAttempts(battingStats.getJSONObject("stolenBases").optInt("attempts"));
                        stats.setStolenBasesCaughtStealing(battingStats.getJSONObject("stolenBases").optInt("caughtStealing"));
                        stats.setStolenBasesTotal(battingStats.getJSONObject("stolenBases").optInt("total"));
                        stats.setStrikeOuts(battingStats.optInt("strikeouts"));
                        stats.setTimesOnBase(battingStats.optInt("timesOnBase"));
                        stats.setTotalBases(battingStats.optInt("totalBases"));
                        stats.setWalksIntentional(battingStats.getJSONObject("walks").optInt("intentional"));
                        stats.setWalksTotal(battingStats.getJSONObject("walks").optInt("total"));
                        stats.setOpposition(eventItem.getJSONObject("opponentTeam").optInt("teamId"));

                        BigDecimal fpp = fantasyPointTranslator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbBattingMap(stats));
                        stats.setFpp(fpp);
                        stack.add(fpp);
                        LinkedList<BigDecimal> averages = ListUtil.findAveragesAndReverse(stack, 15);
                        stats.setAverageFpp(new ObjectMapper().writeValueAsString(averages));
                        Ebean.save(stats);
                    } else {
                        BigDecimal fpp = fantasyPointTranslator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbBattingMap(stats));
                        stats.setFpp(fpp);
                        stack.add(fpp);
                        LinkedList<BigDecimal> averages = ListUtil.findAveragesAndReverse(stack, 15);
                        stats.setAverageFpp(new ObjectMapper().writeValueAsString(averages));
                        //Ebean.update(stats);
                    }
                    statsList.add(stats);
                }
            }
            return statsList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
