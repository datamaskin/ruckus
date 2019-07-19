package stats.parser.nfl;

import service.ScoringRulesService;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.sports.Athlete;
import models.sports.SportEvent;
import models.sports.Team;
import models.stats.nfl.StatsNflAthleteByEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;
import stats.translator.nfl.FantasyPointTranslator;
import utils.ListUtil;
import utils.ParserUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mgiles on 7/22/14.
 */
public class AthleteParser {
    private static final int PRE_SEASON_ID = 0;
    private static final int REG_SEASON_ID = 1;
    private static final int POST_SEASON_ID = 2;
    private static final int PRO_BOWL_ID = 3;

    private static final int MAX_EVENTS = 42;
    private static FantasyPointTranslator translator = new FantasyPointTranslator((ScoringRulesService)
            DistributedServices.getContext().getBean("ScoringRulesManager"));

    public void parse(String results, AthleteDataset dataset) {
        int playerId = 0;
        int eventId = 0;

        try {
            JSONObject jObj = new JSONObject(results);
            JSONObject jApiResults = jObj.getJSONArray("apiResults").getJSONObject(0);
            JSONObject jLeague = jApiResults.getJSONObject("league");
            JSONObject jPlayers = jLeague.getJSONArray("players").getJSONObject(0);
            JSONObject jSeasons = jPlayers.getJSONArray("seasons").getJSONObject(0);

            int season = jSeasons.getInt("season");
            playerId = jPlayers.getInt("playerId");
            Athlete athlete = DaoFactory.getSportsDao().findAthlete(playerId);
            JSONArray eventType = jSeasons.getJSONArray("eventType");

            for (int ei = 0; ei < eventType.length(); ei++) {

                int eventTypeId = eventType.getJSONObject(ei).getInt("eventTypeId");

                //TODO: Should skip Pro Bowl and pre-season
                //Skip the pro bowl and pre-season
                //if (eventTypeId == PRO_BOWL_ID || eventTypeId == PRE_SEASON_ID) {
                if (eventTypeId == PRO_BOWL_ID) {
                    continue;
                }
                //TODO: Should skip Pro Bowl and pre-season

                if (eventType.getJSONObject(ei)
                        .getJSONArray("splits").length() > 1) {
                    throw new ArrayIndexOutOfBoundsException("More than one split found, expecting only one!");
                }
                JSONArray jEvents = eventType.getJSONObject(ei)
                        .getJSONArray("splits")
                        .getJSONObject(0)
                        .getJSONArray("events");

                for (int index = 0; index < jEvents.length(); index++) {
                    JSONObject jEventItem = jEvents.getJSONObject(index);
                    eventId = jEventItem.getInt("eventId");
                    int teamId = jEventItem.getJSONObject("team").getInt("teamId");
                    SportEvent sportEvent = DaoFactory.getSportsDao().findSportEvent(eventId);
                    Team team = DaoFactory.getSportsDao().findTeam(teamId);
                    JSONObject jPlayerStats = jEventItem.getJSONObject("playerStats");
                    StatsNflAthleteByEvent stats = DaoFactory.getStatsDao().findStatsNflAthleteByEvent(athlete, sportEvent);

                    JSONObject jOpponentTeam = jEventItem.getJSONObject("opponentTeam");
                    JSONObject jTeamLocationType = jOpponentTeam.getJSONObject("teamLocationType");
                    int teamLocationTypeId = jTeamLocationType.getInt("teamLocationTypeId");

                    int opponentId = jOpponentTeam.getInt("teamId");
                    if (stats == null) {
                        stats = new StatsNflAthleteByEvent();
                        stats.setEventTypeId(eventTypeId);
                        stats.setSeason(season);
                        stats.setWeek(sportEvent.getWeek());
                        stats.setUniqueKey(athlete.getStatProviderId() + "_" + eventId);
                        stats.setAthlete(athlete);
                        stats.setPosition(athlete.getPositions().get(0).getAbbreviation());
                        stats.setSportEvent(sportEvent);
                        stats.setOpponentId(opponentId);
                        stats.setTeam(team);
                        stats.setLocationId(teamLocationTypeId);
                        Date startTime = ParserUtil.getDate(jEventItem, "startDate");
                        stats.setStartTime(startTime);

                        // Participation
                        if (jPlayerStats.has("participation")) {
                            JSONObject participation = jPlayerStats.getJSONObject("participation");
                            stats.setParticipationOffense(participation.optInt("offense"));
                            stats.setParticipationDefense(participation.optInt("defense"));
                            stats.setParticipationSpecialTeams(participation.optInt("specialTeams"));
                        }

                        // First Downs
                        if (jPlayerStats.has("firstDowns")) {
                            JSONObject firstDowns = jPlayerStats.getJSONObject("firstDowns");
                            stats.setFirstDownsTotal(firstDowns.optInt("total"));
                            stats.setFirstDownsRushing(firstDowns.optInt("rushing"));
                            stats.setFirstDownsPassing(firstDowns.optInt("passing"));
                            stats.setFirstDownsReceiving(firstDowns.optInt("receiving"));
                            stats.setFirstDownsPenalty(firstDowns.optInt("penalty"));
                        }

                        // Rushing
                        if (jPlayerStats.has("rushing")) {
                            JSONObject rushing = jPlayerStats.getJSONObject("rushing");
                            stats.setRushingAttempts(rushing.optInt("attempts"));
                            stats.setRushingYards(rushing.optInt("yards"));
                            stats.setRushingAverage(rushing.has("average")
                                    ? parseFloat(rushing.optString("average")) : 0);
                            stats.setRushingLong(rushing.optInt("long"));
                            stats.setRushingIsLongTouchdown(rushing.getBoolean("isLongTouchdown"));
                            stats.setRushingTouchdowns(rushing.optInt("touchdowns"));
                            stats.setRushingStuffed(rushing.optInt("stuffed"));
                            stats.setRushingStuffedYardsLost(rushing.optInt("stuffedYardsLost"));
                            stats.setRushingStuffedPercentage(rushing.has("stuffedPercentage")
                                    ? parseFloat(rushing.optString("stuffedPercentage")) : 0);
                        }

                        // Passing
                        if (jPlayerStats.has("passing")) {
                            JSONObject passing = jPlayerStats.getJSONObject("passing");
                            stats.setPassingCompletions(passing.optInt("completions"));
                            stats.setPassingAttempts(passing.optInt("attempts"));
                            stats.setPassingCompletionPercentage(passing.has("completionPercentage")
                                    ? parseFloat(passing.optString("completionPercentage")) : 0);
                            stats.setPassingInterceptions(passing.optInt("interceptions"));
                            stats.setPassingYards(passing.optInt("yards"));
                            stats.setPassingYardsPerAttempt(passing.has("yardsPerAttempt")
                                    ? parseFloat(passing.optString("yardsPerAttempt")) : 0);
                            stats.setPassingSacked(passing.optInt("sacked"));
                            stats.setPassingSackedYardsLost(passing.optInt("sackedYardsLost"));
                            stats.setPassingLong(passing.optInt("long"));
                            stats.setPassingIsLongTouchdown(passing.getBoolean("isLongTouchdown"));
                            stats.setPassingTouchdowns(passing.optInt("touchdowns"));
                            stats.setPassingRating(passing.has("rating")
                                    ? parseFloat(passing.optString("rating")) : 0);
                            stats.setPassingYardsAtCatch(passing.optInt("yardsAtCatch"));
                            stats.setPassingYardsAtCatchAverage(passing.has("yardsAtCatchAverage")
                                    ? parseFloat(passing.optString("yardsAtCatchAverage")) : 0);
                            stats.setPassingYardsAfterCatch(passing.optInt("yardsAfterCatch"));
                            stats.setPassingYardsAfterCatchAverage(passing.has("yardsAfterCatchAverage")
                                    ? parseFloat(passing.optString("yardsAfterCatchAverage")) : 0);
                        }

                        // Fumbles
                        if (jPlayerStats.has("fumbles")) {
                            JSONObject fumbles = jPlayerStats.getJSONObject("fumbles");
                            stats.setFumblesTotal(fumbles.optInt("total"));
                            stats.setFumblesPass(fumbles.optInt("pass"));
                            stats.setFumblesRush(fumbles.optInt("rush"));
                            stats.setFumblesSpecialTeams(fumbles.optInt("specialTeams"));
                            stats.setFumblesReceiving(fumbles.optInt("receiving"));
                            stats.setFumblesDefense(fumbles.optInt("defense"));
                            stats.setFumblesMisc(fumbles.optInt("misc"));
                        }

                        // Fumbles Lost
                        if (jPlayerStats.has("fumblesLost")) {
                            JSONObject fumblesLost = jPlayerStats.getJSONObject("fumblesLost");
                            stats.setFumblesLostTotal(fumblesLost.optInt("total"));
                            stats.setFumblesLostPass(fumblesLost.optInt("pass"));
                            stats.setFumblesLostRush(fumblesLost.optInt("rush"));
                            stats.setFumblesLostSpecialTeams(fumblesLost.optInt("specialTeams"));
                            stats.setFumblesLostReceiving(fumblesLost.optInt("receiving"));
                            stats.setFumblesLostDefense(fumblesLost.optInt("defense"));
                            stats.setFumblesLostMisc(fumblesLost.optInt("misc"));
                        }

                        // Receiving
                        if (jPlayerStats.has("receiving")) {
                            JSONObject receiving = jPlayerStats.getJSONObject("receiving");
                            stats.setReceivingReceptions(receiving.optInt("receptions"));
                            stats.setReceivingYards(receiving.optInt("yards"));
                            stats.setReceivingAverage(receiving.has("average")
                                    ? parseFloat(receiving.optString("average")) : 0);
                            stats.setReceivingLong(receiving.optInt("long"));
                            stats.setReceivingIsLongTouchdown(receiving.getBoolean("isLongTouchdown"));
                            stats.setReceivingTouchdowns(receiving.optInt("touchdowns"));
                            stats.setReceivingTargets(receiving.optInt("targets"));
                            stats.setReceivingYardsAtCatch(receiving.optInt("yardsAtCatch"));
                            stats.setReceivingYardsAtCatchAverage(receiving.has("yardsAtCatchAverage")
                                    ? parseFloat(receiving.optString("yardsAtCatchAverage")) : 0);
                            stats.setReceivingYardsAfterCatch(receiving.optInt("yardsAfterCatch"));
                            stats.setReceivingYardsAfterCatchAverage(receiving.has("yardsAfterCatchAverage")
                                    ? parseFloat(receiving.optString("yardsAfterCatchAverage")) : 0);
                        }

                        // Two Point Conversions
                        if (jPlayerStats.has("twoPointConversions")) {
                            JSONObject twoPointConversions = jPlayerStats.getJSONObject("twoPointConversions");
                            stats.setTwoPointConversionsAttempts(twoPointConversions.optInt("attempts"));
                            stats.setTwoPointConversionsPasses(twoPointConversions.optInt("passes"));
                            stats.setTwoPointConversionsMade(twoPointConversions.optInt("made"));
                        }

                        // Penalties
                        if (jPlayerStats.has("penalties")) {
                            JSONObject penalties = jPlayerStats.getJSONObject("penalties");
                            stats.setPenaltiesNumber(penalties.optInt("number"));
                            stats.setPenaltiesYards(penalties.optInt("yards"));
                            stats.setPenaltiesFalseStart(penalties.optInt("falseStart"));
                            stats.setPenaltiesHolding(penalties.optInt("holding"));
                        }

                        // Kickoffs
                        if (jPlayerStats.has("kickoffs")) {
                            JSONObject kickoffs = jPlayerStats.getJSONObject("kickoffs");
                            stats.setKickoffsNumber(kickoffs.optInt("number"));
                            stats.setKickoffsEndZone(kickoffs.optInt("endZone"));
                            stats.setKickoffsTouchbackPercentage(kickoffs.has("touchbackPercentage")
                                    ? parseFloat(kickoffs.optString("touchbackPercentage")) : 0);
                            stats.setKickoffsYards(kickoffs.optInt("yards"));
                            stats.setKickoffsAverage(kickoffs.has("average")
                                    ? parseFloat(kickoffs.optString("average")) : 0);
                            stats.setKickoffsReturns(kickoffs.optInt("returns"));
                            stats.setKickoffsReturnYards(kickoffs.optInt("returnYards"));
                            stats.setKickoffsReturnAverage(kickoffs.has("returnAverage")
                                    ? parseFloat(kickoffs.optString("returnAverage")) : 0);
                            stats.setKickoffsTouchbacks(kickoffs.optInt("touchbacks"));
                        }
                        // Kickoff returning
                        if (jPlayerStats.has("kickoffReturning")) {
                            JSONObject kickoffReturning = jPlayerStats.getJSONObject("kickoffReturning");
                            stats.setKickoffReturningReturns(kickoffReturning.optInt("returns"));
                            stats.setKickoffReturningYards(kickoffReturning.optInt("yards"));
                            stats.setKickoffReturningAverage(kickoffReturning.has("average")
                                    ? parseFloat(kickoffReturning.optString("average")) : 0);
                            stats.setKickoffReturningFairCatches(kickoffReturning.optInt("fairCatches"));
                            stats.setKickoffReturningTouchdowns(kickoffReturning.optInt("touchdowns"));
                            stats.setKickoffReturningLong(kickoffReturning.optInt("long"));
                            stats.setKickoffReturningIsLongTouchdown(kickoffReturning.getBoolean("isLongTouchdown"));
                        }

                        // Punt returning
                        if (jPlayerStats.has("puntReturning")) {
                            JSONObject puntReturning = jPlayerStats.getJSONObject("puntReturning");
                            stats.setPuntReturningReturns(puntReturning.optInt("returns"));
                            stats.setPuntReturningYards(puntReturning.optInt("yards"));
                            stats.setPuntReturningAverage(puntReturning.has("average")
                                    ? parseFloat(puntReturning.optString("average")) : 0);
                            stats.setPuntReturningFairCatches(puntReturning.optInt("fairCatches"));
                            stats.setPuntReturningTouchdowns(puntReturning.optInt("touchdowns"));
                            stats.setPuntReturningLong(puntReturning.optInt("long"));
                            stats.setPuntReturningIsLongTouchdown(puntReturning.getBoolean("isLongTouchdown"));
                        }

                        // Kicking
                        if (jPlayerStats.has("kicking")) {
                            JSONObject kicking = jPlayerStats.getJSONObject("kicking");
                            if (kicking.has("extraPoints")) {
                                JSONObject extraPoints = kicking.getJSONObject("extraPoints");
                                stats.setKickingExtraPointsMade(extraPoints.optInt("made"));
                                stats.setKickingExtraPointsAttempts(extraPoints.optInt("attempts"));
                                stats.setKickingExtraPointsBlocked(extraPoints.optInt("blocked"));
                                stats.setKickingExtraPointsPercentage(extraPoints.has("percentage")
                                        ? parseFloat(extraPoints.optString("percentage")) : 0);
                            }
                            if (kicking.has("fieldGoals")) {
                                JSONArray fieldGoals = kicking.getJSONArray("fieldGoals");
                                for (int j = 0; j < fieldGoals.length(); j++) {
                                    JSONObject fieldGoal = fieldGoals.getJSONObject(j);
                                    if (fieldGoal.has("range")) {
                                        String range = fieldGoal.optString("range");
                                        if (range.equals("0-19")) {
                                            stats.setKickingFieldGoalsMade0to19(fieldGoal.optInt("made"));
                                            stats.setKickingFieldGoalsAttempts0to19(fieldGoal.optInt("attempts"));
                                            stats.setKickingFieldGoalsBlocked0to19(fieldGoal.optInt("blocked"));
                                            stats.setKickingFieldGoalsPercentage0to19(fieldGoal.has("percentage")
                                                    ? parseFloat(fieldGoal.optString("percentage")) : 0);
                                        } else if (range.equals("20-29")) {
                                            stats.setKickingFieldGoalsMade20to29(fieldGoal.optInt("made"));
                                            stats.setKickingFieldGoalsAttempts20to29(fieldGoal.optInt("attempts"));
                                            stats.setKickingFieldGoalsBlocked20to29(fieldGoal.optInt("blocked"));
                                            stats.setKickingFieldGoalsPercentage20to29(fieldGoal.has("percentage")
                                                    ? parseFloat(fieldGoal.optString("percentage")) : 0);
                                        } else if (range.equals("30-39")) {
                                            stats.setKickingFieldGoalsMade30to39(fieldGoal.optInt("made"));
                                            stats.setKickingFieldGoalsAttempts30to39(fieldGoal.optInt("attempts"));
                                            stats.setKickingFieldGoalsBlocked30to39(fieldGoal.optInt("blocked"));
                                            stats.setKickingFieldGoalsPercentage30to39(fieldGoal.has("percentage")
                                                    ? parseFloat(fieldGoal.optString("percentage")) : 0);
                                        } else if (range.equals("40-49")) {
                                            stats.setKickingFieldGoalsMade40to49(fieldGoal.optInt("made"));
                                            stats.setKickingFieldGoalsAttempts40to49(fieldGoal.optInt("attempts"));
                                            stats.setKickingFieldGoalsBlocked40to49(fieldGoal.optInt("blocked"));
                                            stats.setKickingFieldGoalsPercentage40to49(fieldGoal.has("percentage")
                                                    ? parseFloat(fieldGoal.optString("percentage")) : 0);
                                        } else if (range.equals("50+")) {
                                            stats.setKickingFieldGoalsMade50Plus(fieldGoal.optInt("made"));
                                            stats.setKickingFieldGoalsAttempts50Plus(fieldGoal.optInt("attempts"));
                                            stats.setKickingFieldGoalsBlocked50Plus(fieldGoal.optInt("blocked"));
                                            stats.setKickingFieldGoalsPercentage50Plus(fieldGoal.has("percentage")
                                                    ? parseFloat(fieldGoal.optString("percentage")) : 0);
                                        }
                                    }
                                }
                            }
                            stats.setKickingLong(kicking.optInt("long"));
                            stats.setKickingPoints(kicking.optInt("points"));
                        }
                    }
                    BigDecimal fpp = translator.calculateFantasyPoints(DaoFactory.getStatsDao().generateNflOffenseMap(stats));
                    stats.setFppInThisEvent(fpp);
                    if (stats.getEventTypeId() == REG_SEASON_ID || stats.getEventTypeId() == POST_SEASON_ID) {
                        dataset.getFppStack().add(fpp);
                        dataset.getTdStack().add(getTouchDowns(stats));
                        dataset.getPaStack().add(new BigDecimal(stats.getPassingAttempts()));// Passing attempts
                        dataset.getPyStack().add(new BigDecimal(stats.getPassingYards()));// Passing yards
                        dataset.getRuaStack().add(new BigDecimal(stats.getRushingAttempts())); //Rushing attempts
                        dataset.getRuyStack().add(new BigDecimal(stats.getRushingYards())); //Rushing yards
                        dataset.getRetStack().add(new BigDecimal(stats.getReceivingTargets()));//Receiving targets
                        dataset.getReyStack().add(new BigDecimal(stats.getReceivingYards()));//Receiving yards
                        dataset.getParStack().add(new BigDecimal(stats.getPassingRating()));//Passing rating
                    }
                    stats.setPassingRatingPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getParStack(), MAX_EVENTS)));//Passing rating
                    stats.setPassingRatingAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getParStack(), MAX_EVENTS)));//Passing rating

                    stats.setFantasyPointsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getFppStack(), MAX_EVENTS)));
                    stats.setFantasyPointsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getFppStack(), MAX_EVENTS)));

                    stats.setTouchDownsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getTdStack(), MAX_EVENTS)));
                    stats.setTouchDownsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getTdStack(), MAX_EVENTS)));

                    stats.setPassingAttemptsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getPaStack(), MAX_EVENTS)));// Passing attempts
                    stats.setPassingAttemptsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getPaStack(), MAX_EVENTS)));// Passing attempts
                    stats.setPassingYardsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getPyStack(), MAX_EVENTS)));// Passing yards
                    stats.setPassingYardsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getPyStack(), MAX_EVENTS)));// Passing yards

                    stats.setRushingAttemptsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getRuaStack(), MAX_EVENTS))); //Rushing attempts
                    stats.setRushingAttemptsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getRuaStack(), MAX_EVENTS))); //Rushing attempts
                    stats.setRushingYardsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getRuyStack(), MAX_EVENTS))); //Rushing yards
                    stats.setRushingYardsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getRuyStack(), MAX_EVENTS))); //Rushing yards

                    stats.setReceivingTargetsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getRetStack(), MAX_EVENTS)));
                    stats.setReceivingTargetsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getRetStack(), MAX_EVENTS)));
                    stats.setReceivingYardsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(dataset.getReyStack(), MAX_EVENTS)));
                    stats.setReceivingYardsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(dataset.getReyStack(), MAX_EVENTS)));

                    try {
                        Ebean.save(stats);
                    } catch (Exception e) {
                        Logger.debug(e.getMessage());
                    }

                }
            }
        } catch (Exception e) {
            Logger.error("Error parsing for Player: " + playerId + " in event: " + eventId);
            e.printStackTrace();
        }

    }

    private BigDecimal getTouchDowns(StatsNflAthleteByEvent stats) {
        int tds = 0;
        tds += stats.getPassingTouchdowns();
        tds += stats.getReceivingTouchdowns();
        tds += stats.getRushingTouchdowns();
        tds += stats.getKickoffReturningTouchdowns();
        tds += stats.getPuntReturningTouchdowns();
        return new BigDecimal(tds);
    }

    private float parseFloat(String floatString) {
        try {
            return Float.parseFloat(floatString);
        } catch (Exception e) {
            Logger.debug(e.getMessage());
            return 0f;
        }
    }
}
