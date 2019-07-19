package service.edge;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.GlobalConstants;
import models.sports.League;
import service.ScoringRulesService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmaclean on 7/23/14.
 */
public class TestScoringRulesService extends ScoringRulesService {
    public Map<String, Map<String, BigDecimal>> retrieveScoringRulesAsMaps() throws JsonProcessingException {
        Map<String, Map<String, BigDecimal>> allSports = new HashMap<>();
        Map<String, BigDecimal> nfl = new HashMap<>();

        nfl.put(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, GlobalConstants.SCORING_NFL_PASSING_YARDS_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, GlobalConstants.SCORING_NFL_RECEIVING_YARDS_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, GlobalConstants.SCORING_NFL_RECEPTION_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, GlobalConstants.SCORING_NFL_LOST_FUMBLE_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_INTERCEPTION_LABEL, GlobalConstants.SCORING_NFL_INTERCEPTION_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_FIELD_GOAL_0_39_LABEL, GlobalConstants.SCORING_NFL_FIELD_GOAL_MADE_30_39_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_FIELD_GOAL_40_49_LABEL, GlobalConstants.SCORING_NFL_FIELD_GOAL_MADE_40_49_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_FIELD_GOAL_50_PLUS_LABEL, GlobalConstants.SCORING_NFL_FIELD_GOAL_MADE_50_PLUS_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_PAT_LABEL, GlobalConstants.SCORING_NFL_PAT_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, GlobalConstants.SCORING_NFL_RUSHING_YARDS_FACTOR);

        nfl.put(GlobalConstants.SCORING_NFL_SACK_LABEL, GlobalConstants.SCORING_NFL_SACK_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL, GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL, GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL, GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL, GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL, GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_SAFETY_LABEL, GlobalConstants.SCORING_NFL_SAFETY_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL, GlobalConstants.SCORING_NFL_BLOCKED_KICK_FACTOR);
        nfl.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, GlobalConstants.SCORING_NFL_POINTS_ALLOWED_FACTOR);

        allSports.put(League.NFL.getAbbreviation().toLowerCase(), nfl);

        Map<String, BigDecimal> mlb = new HashMap<>();

        mlb.put(GlobalConstants.SCORING_MLB_SINGLE_LABEL, GlobalConstants.SCORING_MLB_SINGLE_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, GlobalConstants.SCORING_MLB_DOUBLE_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, GlobalConstants.SCORING_MLB_TRIPLE_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, GlobalConstants.SCORING_MLB_HOMERUN_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, GlobalConstants.SCORING_MLB_RUN_BATTED_IN_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_RUN_LABEL, GlobalConstants.SCORING_MLB_RUN_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_WALK_LABEL, GlobalConstants.SCORING_MLB_WALK_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, GlobalConstants.SCORING_MLB_HIT_BY_PITCH_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, GlobalConstants.SCORING_MLB_STOLEN_BASE_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL, GlobalConstants.SCORING_MLB_CAUGHT_STEALING_FACTOR);

        mlb.put(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, GlobalConstants.SCORING_MLB_INNING_PITCHED_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, GlobalConstants.SCORING_MLB_STRIKEOUT_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_WIN_LABEL, GlobalConstants.SCORING_MLB_WIN_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, GlobalConstants.SCORING_MLB_EARNED_RUN_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, GlobalConstants.SCORING_MLB_PITCHER_HIT_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, GlobalConstants.SCORING_MLB_PITCHER_WALK_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_FACTOR);
        mlb.put(GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL, GlobalConstants.SCORING_MLB_COMPLETE_GAME_FACTOR);

        allSports.put(League.MLB.getAbbreviation().toLowerCase(), mlb);

        return allSports;
    }
}
