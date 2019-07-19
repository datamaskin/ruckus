package stats.parser.nfl;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.sports.Athlete;
import models.sports.SportEvent;
import models.sports.Team;
import models.stats.nfl.StatsNflDefenseByEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import stats.parser.IStatsParser;
import stats.translator.IFantasyPointTranslator;
import utils.ListUtil;
import utils.ParserUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by dmaclean on 8/6/14.
 */
public class DefenseParser implements IStatsParser<StatsNflDefenseByEvent> {
    private static final int PRE_SEASON_ID = 0;
    private static final int REG_SEASON_ID = 1;
    private static final int POST_SEASON_ID = 2;
    private static final int PRO_BOWL_ID = 3;

    private static final int MAX_EVENTS = 42;

    private static IFantasyPointTranslator translator = DistributedServices.getContext().getBean("NFLFantasyPointTranslator", IFantasyPointTranslator.class);

    private LinkedList<BigDecimal> fppStack = new LinkedList<>();

    @Override
    public List<StatsNflDefenseByEvent> parse(String results) {
        List<StatsNflDefenseByEvent> statsNflDefenseByEvents = new ArrayList<>();

        try {
            int eventId = 0;
            int playerId = 0;

            JSONObject obj = new JSONObject(results);
            JSONObject jSeason = obj.getJSONArray("apiResults").getJSONObject(0)
                    .getJSONObject("league")
                    .getJSONArray("teams").getJSONObject(0)
                    .getJSONArray("seasons").getJSONObject(0);
            int season = jSeason.getInt("season");
            JSONArray eventTypes = jSeason.getJSONArray("eventType");

            for (int ei = 0; ei < eventTypes.length(); ei++) {

                //Skip the pro bowl and pre-season
                int eventTypeId = eventTypes.getJSONObject(ei).optInt("eventTypeId");
                if (eventTypeId == PRO_BOWL_ID) {
                    continue;
                }

                if (eventTypes.getJSONObject(ei)
                        .getJSONArray("splits").length() > 1) {
                    throw new ArrayIndexOutOfBoundsException("More than one split found, expecting only one!");
                }
                JSONArray events = eventTypes.getJSONObject(ei)
                        .getJSONArray("splits")
                        .getJSONObject(0)
                        .getJSONArray("events");

                int statProviderId = obj.getJSONArray("apiResults").getJSONObject(0)
                        .getJSONObject("league")
                        .getJSONArray("teams").getJSONObject(0)
                        .optInt("teamId");
                playerId = statProviderId;

                Athlete athlete = DaoFactory.getSportsDao().findAthlete(statProviderId);

                for (int index = 0; index < events.length(); index++) {
                    JSONObject eventItem = events.getJSONObject(index);
                    eventId = eventItem.optInt("eventId");

                    SportEvent sportEvent = DaoFactory.getSportsDao().findSportEvent(eventId);
                    Team team = DaoFactory.getSportsDao().findTeam(eventItem.getJSONObject("team").optInt("teamId"));
                    Team opponent = DaoFactory.getSportsDao().findTeam(eventItem.getJSONObject("opponentTeam").optInt("teamId"));
                    JSONObject teamStats = eventItem.getJSONArray("teamStats").getJSONObject(0);
                    JSONObject opponentStats = eventItem.getJSONArray("teamStats").getJSONObject(1);
                    StatsNflDefenseByEvent stats = DaoFactory.getStatsDao().findStatsNflDefenseByEvent(athlete, sportEvent);

                    if(stats == null) {
                        stats = new StatsNflDefenseByEvent();
                        stats.setEventTypeId(eventTypeId);
                        stats.setAthlete(athlete);
                        stats.setTeam(team);
                        stats.setOpponent(opponent);
                        stats.setSportEvent(sportEvent);
                        stats.setSeason(season);
                        stats.setWeek(sportEvent.getWeek());
                        stats.setLocationId(eventItem.getJSONObject("opponentTeam")
                                .getJSONObject("teamLocationType")
                                .optInt("teamLocationTypeId"));
                        Date startTime = ParserUtil.getDate(eventItem, "startDate");
                        if (startTime == null) {
                            continue;
                        }
                        stats.setStartTime(startTime);

                        // Interceptions and interception return touchdowns
                        if(teamStats.has("interceptions")) {
                            stats.setInterceptions(teamStats.getJSONObject("interceptions").optInt("number"));
                            stats.setInterceptionReturnTouchdowns(teamStats.getJSONObject("interceptions").optInt("touchdowns"));
                        }


                        // Fumble recoveries and recovery touchdowns
                        if(teamStats.has("opponentFumbles")) {
                            JSONObject opponentFumbles = teamStats.getJSONObject("opponentFumbles");
                            stats.setFumbleRecoveries(opponentFumbles.optInt("recovered"));
                            stats.setFumbleRecoveryTouchdowns(opponentFumbles.optInt("touchdowns"));
                        }

                        // Touchdowns
                        if(teamStats.has("scoring") && teamStats.getJSONObject("scoring").has("touchdowns")) {
                            stats.setKickReturnTouchdowns(teamStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("kickoffReturn"));

                            // Punt return touchdowns
                            stats.setPuntReturnTouchdowns(teamStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("puntReturn"));

                            // Blocked punt or field goal return touchdowns
                            int missedFieldGoalReturnTouchdowns = teamStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("missedFieldGoalReturn");
                            int blockedPuntReturnTouchdowns = teamStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("blockedPuntReturn");
                            int blockedFieldGoalReturnTouchdowns = teamStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("blockedFieldGoalReturn");
                            stats.setBlockedPuntOrFieldGoalReturnTouchdowns(missedFieldGoalReturnTouchdowns + blockedPuntReturnTouchdowns + blockedFieldGoalReturnTouchdowns);
                        }

                        // Safeties and sacks
                        if(teamStats.has("defense")) {
                            stats.setSafeties(teamStats.getJSONObject("defense").optInt("safeties"));
                            stats.setSacks(teamStats.getJSONObject("defense").optInt("sacks"));
                        }

                        // Blocked kicks
                        if(opponentStats.has("kicking") && opponentStats.getJSONObject("kicking").has("fieldGoals") &&
                                opponentStats.getJSONObject("kicking").getJSONArray("fieldGoals").length() == 6) {
                            stats.setBlockedKicks(opponentStats.getJSONObject("kicking").getJSONArray("fieldGoals").getJSONObject(5).optInt("blocked"));
                        }

                        stats.setPointsAllowed(0);
                        if(opponentStats.has("scoring")) {
                            int passingTouchdowns = opponentStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("passing");
                            int rushingTouchdowns = opponentStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("rushing");
                            int ownFumbleTouchdowns = opponentStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("ownFumbleReturn");
                            int kickoffReturnTouchdowns = opponentStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("kickoffReturn");
                            int puntReturnTouchdowns = opponentStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("puntReturn");
                            int missedFieldGoalReturnTouchdowns = opponentStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("missedFieldGoalReturn");
                            int blockedPuntReturnTouchdowns = opponentStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("blockedPuntReturn");
                            int blockedFieldGoalReturnTouchdowns = opponentStats.getJSONObject("scoring").getJSONObject("touchdowns").optInt("blockedFieldGoalReturn");

                            int twoPointConversions = opponentStats.getJSONObject("scoring").optInt("twoPointConversions");
                            int extraPoints = opponentStats.getJSONObject("scoring").optInt("extraPoints");
                            int fieldGoals = opponentStats.getJSONObject("scoring").optInt("fieldGoals");

                            int totalPoints = (passingTouchdowns + rushingTouchdowns + ownFumbleTouchdowns + kickoffReturnTouchdowns +
                                    puntReturnTouchdowns + missedFieldGoalReturnTouchdowns + blockedPuntReturnTouchdowns + blockedFieldGoalReturnTouchdowns) * 6;
                            totalPoints += twoPointConversions * 2;
                            totalPoints += extraPoints;
                            totalPoints += fieldGoals * 3;

                            stats.setPointsAllowed(totalPoints);
                        }

                        Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, new BigDecimal(stats.getPuntReturnTouchdowns()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, new BigDecimal(stats.getKickReturnTouchdowns()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_SACK_LABEL, new BigDecimal(stats.getSacks()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL, new BigDecimal(stats.getInterceptions()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL, new BigDecimal(stats.getFumbleRecoveries()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL, new BigDecimal(stats.getInterceptionReturnTouchdowns()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL, new BigDecimal(stats.getFumbleRecoveryTouchdowns()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL, new BigDecimal(stats.getBlockedPuntOrFieldGoalReturnTouchdowns()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_SAFETY_LABEL, new BigDecimal(stats.getSafeties()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL, new BigDecimal(stats.getBlockedKicks()));
                        fpTranslationMap.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(stats.getPointsAllowed()));

                        BigDecimal fantasyPoints = translator.calculateFantasyPoints(fpTranslationMap);
                        stats.setFppInThisEvent(fantasyPoints);
                        fppStack.add(fantasyPoints);

                        stats.setFantasyPointsPerGameRange(new ObjectMapper().writeValueAsString(ListUtil.trimAndReverse(fppStack, MAX_EVENTS)));
                        stats.setFantasyPointsAvgRange(new ObjectMapper().writeValueAsString(ListUtil.findAveragesAndReverse(fppStack, MAX_EVENTS)));
                    }

                    /*
                     * Attempt to save the StatsNflDefenseByEvent object, but in the case of a failure, we don't want
                     * execution to fall through to the outer catch block and
                     */
                    try {
                        StatsNflDefenseByEvent statsNflDefenseByEvent = DaoFactory.getStatsDao().findStatsNflDefenseByEvent(athlete, sportEvent);
                        if (statsNflDefenseByEvent == null) {
                            Ebean.save(stats);
                        }
                    }
                    catch(Exception e) {
                        Logger.error("Unable to save StatsNflDefenseByEvent: " + e.getMessage());
                    }

                    statsNflDefenseByEvents.add(stats);
                }
            }
        } catch (JSONException | JsonProcessingException e) {
//            Logger.error("Error parsing for Player: " + playerId + " in event: " + eventId);
            e.printStackTrace();
            return statsNflDefenseByEvents;
        }

        return statsNflDefenseByEvents;
    }

    public void resetDataset() {
        fppStack.clear();
    }
}
