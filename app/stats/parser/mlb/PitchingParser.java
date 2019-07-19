package stats.parser.mlb;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.stats.mlb.StatsMlbPitching;
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
public class PitchingParser implements IStatsParser<StatsMlbPitching> {

    private FantasyPointTranslator fantasyPointTranslator;

    public PitchingParser(FantasyPointTranslator fantasyPointTranslator) {
        this.fantasyPointTranslator = fantasyPointTranslator;
    }

    public List<StatsMlbPitching> parse(String results) {
        try {
            List<StatsMlbPitching> statsList = new ArrayList<>();

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

                if (eventItem.getJSONObject("playerStats").has("pitchingStats")) {
                    JSONObject ps = eventItem.getJSONObject("playerStats").getJSONObject("pitchingStats");
                    StatsMlbPitching stats = Ebean.find(StatsMlbPitching.class).where().eq("statProviderId", statProviderId)
                            .eq("eventId", eventItem.optInt("eventId")).findUnique();
                    if (stats == null) {
                        stats = new StatsMlbPitching();
                        stats.setEventId(eventItem.optInt("eventId"));
                        stats.setStatProviderId(statProviderId);
                        stats.setBalks(ps.optInt("balks"));
                        stats.setBallsHitAllowedFlyballs(ps.getJSONObject("ballsHitAllowed").optInt("flyballs"));
                        stats.setBallsHitAllowedGroundBalls(ps.getJSONObject("ballsHitAllowed").optInt("groundballs"));
                        stats.setBallsHitAllowedLineDrives(ps.getJSONObject("ballsHitAllowed").optInt("lineDrives"));
                        stats.setBaseRunnersAllowedRatePerNineInnings(ps.getJSONObject("baserunnersAllowed").has("ratePerNineInnings")
                                ? Double.parseDouble(ps.getJSONObject("baserunnersAllowed").getString("ratePerNineInnings"))
                                : null);
                        stats.setBaseRunnersAllowedTotal(ps.getJSONObject("baserunnersAllowed").optInt("total"));
                        stats.setEarnedRunAverage(ps.has("earnedRunAverage")
                                ? Double.parseDouble(ps.getString("earnedRunAverage"))
                                : null);
                        stats.setGamesComplete(ps.getJSONObject("games").optInt("complete"));
                        stats.setGamesQualityStarts(ps.getJSONObject("games").optInt("qualityStarts"));
                        stats.setGamesShutouts(ps.getJSONObject("games").optInt("shutouts"));
                        stats.setGamesStarts(ps.getJSONObject("games").optInt("starts"));
                        stats.setGamesTotal(ps.getJSONObject("games").optInt("total"));
                        stats.setGroundIntoDoublePlaysPercentage(ps.getJSONObject("groundIntoDoublePlays").has("percentage")
                                ? Double.parseDouble(ps.getJSONObject("groundIntoDoublePlays").getString("percentage"))
                                : null);
                        stats.setGroundIntoDoublePlaysRatePerNineInnings(ps.getJSONObject("groundIntoDoublePlays").has("ratePerNineInnings")
                                ? Double.parseDouble(ps.getJSONObject("groundIntoDoublePlays").getString("ratePerNineInnings"))
                                : null);
                        stats.setGroundIntoDoublePlaysTotal(ps.getJSONObject("groundIntoDoublePlays").optInt("total"));
                        stats.setHitBatsmen(ps.optInt("hitBatsmen"));
                        stats.setHitsAllowedDoubles(ps.getJSONObject("hitsAllowed").optInt("doubles"));
                        stats.setHitsAllowedHomerunsTotal(ps.getJSONObject("hitsAllowed").getJSONObject("homeRuns").optInt("total"));
                        stats.setHitsAllowedHomerunsRatePerNineInnings(ps.getJSONObject("hitsAllowed").getJSONObject("homeRuns").has("ratePerNineInnings")
                                ? Double.parseDouble(ps.getJSONObject("hitsAllowed").getJSONObject("homeRuns").getString("ratePerNineInnings"))
                                : null);
                        stats.setHitsAllowedRatePerNineInnings(ps.getJSONObject("hitsAllowed").has("ratePerNineInnings")
                                ? Double.parseDouble(ps.getJSONObject("hitsAllowed").getString("ratePerNineInnings"))
                                : null);
                        stats.setHitsAllowedTotal(ps.getJSONObject("hitsAllowed").optInt("total"));
                        stats.setHitsAllowedTriples(ps.getJSONObject("hitsAllowed").optInt("triples"));
                        stats.setWildPitches(ps.optInt("wildPitches"));
                        stats.setTotalBattersFaced(ps.optInt("totalBattersFaced"));
                        stats.setWalksIntentional(ps.getJSONObject("walks").optInt("intentional"));
                        stats.setWalksTotal(ps.getJSONObject("walks").optInt("total"));
                        stats.setWalksPlusHitsRatePerInning(ps.getJSONObject("walksPlusHits").has("ratePerInning")
                                ? Double.parseDouble(ps.getJSONObject("walksPlusHits").getString("ratePerInning"))
                                : null);
                        stats.setWalksPlusHitsTotal(Double.parseDouble(ps.getJSONObject("walksPlusHits").getString("total")));
                        if (stats.getWalksTotal() > 0) {
                            stats.setStrikeoutWalkRatio(Double.parseDouble(ps.getString("strikeoutWalkRatio")));
                        }
                        stats.setTotalBasesAgainst(ps.optInt("totalBasesAgainst"));
                        stats.setStrikeoutsRatePerNineInnings(ps.getJSONObject("strikeouts").has("ratePerNineInnings")
                                ? Double.parseDouble(ps.getJSONObject("strikeouts").getString("ratePerNineInnings"))
                                : null);
                        stats.setStrikeoutsTotal(ps.getJSONObject("strikeouts").optInt("total"));
                        stats.setStolenBasesAgainstAttempts(ps.getJSONObject("stolenBasesAgainst").optInt("attempts"));
                        stats.setStolenBasesAgainstCaughtStealing(ps.getJSONObject("stolenBasesAgainst").optInt("caughtStealing"));
                        stats.setStolenBasesAgainstTotal(ps.getJSONObject("stolenBasesAgainst").optInt("total"));
                        stats.setSavesBlown(ps.getJSONObject("saves").optInt("blown"));
                        stats.setSavesOpportunities(ps.getJSONObject("saves").optInt("opportunities"));
                        stats.setSavesTotal(ps.getJSONObject("saves").optInt("total"));
                        stats.setSacrificesFlies(ps.getJSONObject("sacrifices").optInt("flies"));
                        stats.setSacrificesHits(ps.getJSONObject("sacrifices").optInt("hits"));
                        stats.setRunSupportRatePerNineInnings(ps.getJSONObject("runSupport").has("ratePerNineInnings")
                                ? Double.parseDouble(ps.getJSONObject("runSupport").getString("ratePerNineInnings"))
                                : null);
                        stats.setRunSupportTotal(ps.getJSONObject("runSupport").optInt("total"));
                        stats.setRunsAllowedEarnedRuns(ps.getJSONObject("runsAllowed").optInt("earnedRuns"));
                        stats.setRunsAllowedRunsBattedIn(ps.getJSONObject("runsAllowed").optInt("runsBattedIn"));
                        stats.setRunsAllowedTotal(ps.getJSONObject("runsAllowed").optInt("total"));
                        stats.setPitchesPerInning(ps.getJSONObject("pitches").has("pitchesPerInning")
                                ? Double.parseDouble(ps.getJSONObject("pitches").getString("pitchesPerInning"))
                                : null);
                        stats.setPitchesTotal(ps.getJSONObject("pitches").optInt("total"));
                        stats.setHolds(ps.optInt("holds"));
                        stats.setInheritedRunnersStranded(ps.getJSONObject("inheritedRunners").optInt("stranded"));
                        stats.setInheritedRunnersTotal(ps.getJSONObject("inheritedRunners").optInt("total"));
                        stats.setInningsPitched(Double.parseDouble(ps.getString("inningsPitched")));
                        stats.setOpponentAtBats(ps.optInt("opponentAtBats"));
                        stats.setOpponentBattingAverage(ps.has("opponentBattingAverage")
                                ? Double.parseDouble(ps.getString("opponentBattingAverage"))
                                : null);
                        stats.setOpponentOnBasePercentage(ps.has("opponentOnBasePercentage")
                                ? Double.parseDouble(ps.getString("opponentOnBasePercentage"))
                                : null);
                        stats.setOpponentSluggingPercentage(ps.has("opponentSluggingPercentage")
                                ? Double.parseDouble(ps.getString("opponentSluggingPercentage"))
                                : null);
                        stats.setPickoffsPlusPitcherCaughtStealing(ps.getJSONObject("pickoffs").optInt("pickoffsPlusPitcherCaughtStealing"));
                        stats.setPickoffsThrows(ps.getJSONObject("pickoffs").optInt("throws"));
                        stats.setPickoffsThrowsPerBaseRunner(ps.getJSONObject("pickoffs").has("throwsPerBaserunner")
                                ? Double.parseDouble(ps.getJSONObject("pickoffs").getString("throwsPerBaserunner"))
                                : null);
                        stats.setPickoffsTotal(ps.getJSONObject("pickoffs").optInt("total"));
                        stats.setOpposition(eventItem.getJSONObject("opponentTeam").optInt("teamId"));

                        BigDecimal fpp = fantasyPointTranslator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbPitchingMap(stats));
                        stats.setFpp(fpp);
                        stack.add(fpp);
                        LinkedList<BigDecimal> averages = ListUtil.findAveragesAndReverse(stack, 15);
                        stats.setAverageFpp(new ObjectMapper().writeValueAsString(averages));
                        Ebean.save(stats);
                    } else {
                        BigDecimal fpp = fantasyPointTranslator.calculateFantasyPoints(DaoFactory.getStatsDao().generateMlbPitchingMap(stats));
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
