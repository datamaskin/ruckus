package stats.parser.nfl;

import common.GlobalConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import stats.parser.IStatsParser;
import stats.translator.IFantasyPointTranslator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 8/19/14.
 */
public class BoxscoreParser implements IStatsParser<Map<Integer, BigDecimal>> {

    private IFantasyPointTranslator translator;

    public BoxscoreParser(IFantasyPointTranslator translator) {
        this.translator = translator;
    }

    @Override
    public List<Map<Integer, BigDecimal>> parse(String results) {
        List<Map<Integer, BigDecimal>> result = new ArrayList<>();
        Map<Integer, BigDecimal> resultMap = new HashMap<>();

        try {
            JSONObject jObj = new JSONObject(results);
            JSONObject jApiResults = jObj.getJSONArray("apiResults").getJSONObject(0);
            JSONObject jLeague = jApiResults.getJSONObject("league");
            JSONObject jSeason = jLeague.getJSONObject("season");
            JSONObject jEventType = jSeason.getJSONArray("eventType").getJSONObject(0);
            JSONObject jEvent = jEventType.getJSONArray("events").getJSONObject(0);
            JSONObject team1Box = jEvent.getJSONArray("boxscores").getJSONObject(0);
            JSONObject team2Box = jEvent.getJSONArray("boxscores").getJSONObject(1);

            /*
             * Check event status - bail if we're not in Final status.
             */
            int eventStatusId = jEvent.getJSONObject("eventStatus").optInt("eventStatusId");
            if(eventStatusId != GlobalConstants.STATS_INC_GAME_STATUS_FINAL_CODE) {
                return result;
            }

            // Handle defenses.
            JSONObject team1Stats = team1Box.getJSONObject("teamStats");
            JSONObject team2Stats = team2Box.getJSONObject("teamStats");
            int team1Id = team1Box.getInt("teamId");
            int team2Id = team2Box.getInt("teamId");

            BigDecimal team1FantasyPoints = processStatsForTeam(team1Stats, team2Stats);
            BigDecimal team2FantasyPoints = processStatsForTeam(team2Stats, team1Stats);

            resultMap.put(team1Id, team1FantasyPoints);
            resultMap.put(team2Id, team2FantasyPoints);

            /*
             * Handle Team 1 athletes
             */
            JSONObject team1Athletes = team1Box.getJSONObject("playerStats");
            Map<Integer, BigDecimal> team1AthletesMap = processStatsForAthletes(team1Athletes);
            resultMap.putAll(team1AthletesMap);

            /*
             * Handle Team 2 athletes.
             */
            JSONObject team2Athletes = team2Box.getJSONObject("playerStats");
            Map<Integer, BigDecimal> team2AthletesMap = processStatsForAthletes(team2Athletes);
            resultMap.putAll(team2AthletesMap);

            result.add(resultMap);
        }
        catch(JSONException e) {
            Logger.error(e.getMessage());
        }

        return result;
    }

    private Map<Integer, BigDecimal> processStatsForAthletes(JSONObject athleteStats) throws JSONException {
        Map<Integer, BigDecimal> athleteStatsMap = new HashMap<>();

        // Rushing stats
        JSONArray rushingArray = athleteStats.getJSONArray("rushingStats");
        for(int i=0; i<rushingArray.length(); i++) {
            JSONObject rushingElement = rushingArray.getJSONObject(i);
            int playerId = rushingElement.getJSONObject("player").getInt("playerId");
            int yards = rushingElement.getInt("yards");
            int touchdowns = rushingElement.getInt("touchdowns");

            Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, new BigDecimal(yards));
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL, new BigDecimal(touchdowns));
            BigDecimal fps = translator.calculateFantasyPoints(fpTranslationMap);

            BigDecimal fpsFromMap = athleteStatsMap.get(playerId);
            if(fpsFromMap == null)
                athleteStatsMap.put(playerId, fps);
            else
                athleteStatsMap.put(playerId, fpsFromMap.add(fps));
        }
        
        // Passing stats
        JSONArray passingArray = athleteStats.getJSONArray("passingStats");
        for(int i=0; i<passingArray.length(); i++) {
            JSONObject passingElement = passingArray.getJSONObject(i);
            int playerId = passingElement.getJSONObject("player").getInt("playerId");
            int yards = passingElement.getInt("yards");
            int touchdowns = passingElement.getInt("touchdowns");
            int interceptions = passingElement.getInt("interceptions");

            Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, new BigDecimal(yards));
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, new BigDecimal(touchdowns));
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_INTERCEPTION_LABEL, new BigDecimal(interceptions));
            BigDecimal fps = translator.calculateFantasyPoints(fpTranslationMap);

            BigDecimal fpsFromMap = athleteStatsMap.get(playerId);
            if(fpsFromMap == null)
                athleteStatsMap.put(playerId, fps);
            else
                athleteStatsMap.put(playerId, fpsFromMap.add(fps));
        }

        // Receiving stats
        JSONArray receivingArray = athleteStats.getJSONArray("receivingStats");
        for(int i=0; i<receivingArray.length(); i++) {
            JSONObject receivingElement = receivingArray.getJSONObject(i);
            int playerId = receivingElement.getJSONObject("player").getInt("playerId");
            int yards = receivingElement.getInt("yards");
            int touchdowns = receivingElement.getInt("touchdowns");
            int receptions = receivingElement.getInt("receptions");

            Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, new BigDecimal(yards));
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL, new BigDecimal(touchdowns));
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, new BigDecimal(receptions));
            BigDecimal fps = translator.calculateFantasyPoints(fpTranslationMap);

            BigDecimal fpsFromMap = athleteStatsMap.get(playerId);
            if(fpsFromMap == null)
                athleteStatsMap.put(playerId, fps);
            else
                athleteStatsMap.put(playerId, fpsFromMap.add(fps));
        }

        // Kick return stats
        JSONArray kickReturnArray = athleteStats.getJSONArray("kickReturnStats");
        for(int i=0; i<kickReturnArray.length(); i++) {
            JSONObject kickReturnElement = kickReturnArray.getJSONObject(i);
            int playerId = kickReturnElement.getJSONObject("player").getInt("playerId");
            int touchdowns = kickReturnElement.getInt("touchdowns");

            Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, new BigDecimal(touchdowns));
            BigDecimal fps = translator.calculateFantasyPoints(fpTranslationMap);

            BigDecimal fpsFromMap = athleteStatsMap.get(playerId);
            if(fpsFromMap == null)
                athleteStatsMap.put(playerId, fps);
            else
                athleteStatsMap.put(playerId, fpsFromMap.add(fps));
        }

        // Punt return stats
        JSONArray puntReturnArray = athleteStats.getJSONArray("puntReturnStats");
        for(int i=0; i<puntReturnArray.length(); i++) {
            JSONObject puntReturnElement = puntReturnArray.getJSONObject(i);
            int playerId = puntReturnElement.getJSONObject("player").getInt("playerId");
            int touchdowns = puntReturnElement.getInt("touchdowns");

            Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, new BigDecimal(touchdowns));
            BigDecimal fps = translator.calculateFantasyPoints(fpTranslationMap);

            BigDecimal fpsFromMap = athleteStatsMap.get(playerId);
            if(fpsFromMap == null)
                athleteStatsMap.put(playerId, fps);
            else
                athleteStatsMap.put(playerId, fpsFromMap.add(fps));
        }

        // Two-point conversion
        JSONArray twoPointConversionArray = athleteStats.getJSONArray("twoPointConversionStats");
        for(int i=0; i<twoPointConversionArray.length(); i++) {
            JSONObject twoPointConversionElement = twoPointConversionArray.getJSONObject(i);
            int playerId = twoPointConversionElement.getJSONObject("player").getInt("playerId");
            int scores = twoPointConversionElement.getInt("scores");

            Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, new BigDecimal(scores));
            BigDecimal fps = translator.calculateFantasyPoints(fpTranslationMap);

            BigDecimal fpsFromMap = athleteStatsMap.get(playerId);
            if(fpsFromMap == null)
                athleteStatsMap.put(playerId, fps);
            else
                athleteStatsMap.put(playerId, fpsFromMap.add(fps));
        }

        // Fumbles
        JSONArray ownFumbleArray = athleteStats.getJSONArray("ownFumbleStats");
        for(int i=0; i<ownFumbleArray.length(); i++) {
            JSONObject ownFumbleElement = ownFumbleArray.getJSONObject(i);
            int playerId = ownFumbleElement.getJSONObject("player").getInt("playerId");
            int totalLost = ownFumbleElement.getInt("totalLost");

            Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
            fpTranslationMap.put(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, new BigDecimal(totalLost));
            BigDecimal fps = translator.calculateFantasyPoints(fpTranslationMap);

            BigDecimal fpsFromMap = athleteStatsMap.get(playerId);
            if(fpsFromMap == null)
                athleteStatsMap.put(playerId, fps);
            else
                athleteStatsMap.put(playerId, fpsFromMap.add(fps));
        }

        return athleteStatsMap;
    }

    /**
     * Calculates the fantasy points for the provided team, considering their opponent.
     *
     * @param teamStats             The JSONObject representing stats for the team we're interested in.
     * @param opponentStats         The JSONObject representing stats for their opponent.
     * @return
     * @throws JSONException
     */
    private BigDecimal processStatsForTeam(JSONObject teamStats, JSONObject opponentStats) throws JSONException {
        int interceptions = 0;
        int interceptionReturnTouchdowns = 0;
        int fumbleRecoveries = 0;
        int fumbleRecoveryTouchdowns = 0;
        int kickReturnTouchdowns= 0;
        int puntReturnTouchdowns = 0;
        int blockedPuntOrFieldGoalReturnTouchdowns = 0;
        int safeties = 0;
        int sacks = 0;
        int blockedKicks = 0;
        int pointsAllowed = 0;

        // Interceptions and interception return touchdowns
        if(teamStats.has("interceptions")) {
            interceptions = teamStats.getJSONObject("interceptions").optInt("number");
            interceptionReturnTouchdowns = teamStats.getJSONObject("interceptions").optInt("touchdowns");
        }

        // Fumble recoveries and recovery touchdowns
        if(teamStats.has("opponentFumbles")) {
            JSONObject opponentFumbles = teamStats.getJSONObject("opponentFumbles");
            fumbleRecoveries = opponentFumbles.optInt("recovered");
            fumbleRecoveryTouchdowns = opponentFumbles.optInt("touchdowns");
        }

        // Touchdowns
        kickReturnTouchdowns = teamStats.getJSONObject("kickReturning").optInt("touchdowns");

        // Punt return touchdowns
        puntReturnTouchdowns = teamStats.getJSONObject("puntReturning").optInt("touchdowns");

        /*
         * Blocked punt or field goal return touchdowns
         *
         * As far as I can tell, this needs to be deduced by taking the total touchdowns for the team and
         * subtracting all touchdowns that are called out explicitly.
         */
        int rushingTouchdowns = teamStats.getJSONObject("rushing").getInt("touchdowns");
        int passingTouchdowns = teamStats.getJSONObject("passing").getInt("touchdowns");
        int ownFumbleTouchdowns = teamStats.getJSONObject("ownFumbles").getInt("touchdowns");
        int totalTouchdowns = teamStats.getJSONObject("gameTotals").getInt("touchdowns");
        blockedPuntOrFieldGoalReturnTouchdowns = totalTouchdowns - (interceptionReturnTouchdowns + fumbleRecoveryTouchdowns + kickReturnTouchdowns +
                puntReturnTouchdowns + rushingTouchdowns + passingTouchdowns + ownFumbleTouchdowns);

        // Safeties and sacks
        if(teamStats.has("defense")) {
            safeties = teamStats.optInt("safeties");
            sacks = teamStats.getJSONObject("defense").optInt("sacks");
        }

        // Blocked kicks
        if(opponentStats.has("fieldGoals")) {
            blockedKicks = opponentStats.getJSONObject("fieldGoals").optInt("blocked");
        }

        int oppPassingTouchdowns = opponentStats.getJSONObject("passing").optInt("touchdowns");
        int oppRushingTouchdowns = opponentStats.getJSONObject("rushing").optInt("touchdowns");
        int oppOwnFumbleTouchdowns = opponentStats.getJSONObject("ownFumbles").getInt("touchdowns");
        int oppKickReturnTouchdowns = opponentStats.getJSONObject("kickReturning").optInt("touchdowns");
        int oppPuntReturnTouchdowns = opponentStats.getJSONObject("puntReturning").optInt("touchdowns");

        int twoPointConversions = opponentStats.getJSONObject("twoPointConversions").optInt("made");
        int extraPoints = opponentStats.getJSONObject("extraPoints").optInt("made");
        int fieldGoals = opponentStats.getJSONObject("fieldGoals").optInt("made");

        int totalPoints = (oppPassingTouchdowns + oppRushingTouchdowns + oppOwnFumbleTouchdowns + oppKickReturnTouchdowns + oppPuntReturnTouchdowns) * 6;
        totalPoints += twoPointConversions * 2;
        totalPoints += extraPoints;
        totalPoints += fieldGoals * 3;

        pointsAllowed = totalPoints;

        Map<String, BigDecimal> fpTranslationMap = new HashMap<>();
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, new BigDecimal(puntReturnTouchdowns));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, new BigDecimal(kickReturnTouchdowns));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_SACK_LABEL, new BigDecimal(sacks));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL, new BigDecimal(interceptions));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL, new BigDecimal(fumbleRecoveries));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL, new BigDecimal(interceptionReturnTouchdowns));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL, new BigDecimal(fumbleRecoveryTouchdowns));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL, new BigDecimal(blockedPuntOrFieldGoalReturnTouchdowns));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_SAFETY_LABEL, new BigDecimal(safeties));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL, new BigDecimal(blockedKicks));
        fpTranslationMap.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, new BigDecimal(pointsAllowed));

        return translator.calculateFantasyPoints(fpTranslationMap);
    }

    public IFantasyPointTranslator getTranslator() {
        return translator;
    }

    public void setTranslator(IFantasyPointTranslator translator) {
        this.translator = translator;
    }
}
