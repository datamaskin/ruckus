package common;

import play.Play;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dan on 4/14/14.
 */
public class GlobalConstants {

    /**
     * environments
     */
    public static final String ENVIRONMENT_LOCAL = "local";
    public static final String ENVIRONMENT_DEV = "dev";
    public static final String ENVIRONMENT_LIVE = "live";
    public static final String ENVIRONMENT_QA = "qa";
    public static final String ENVIRONMENT_RECORDING = "recording";
    public static final String ENVIRONMENT_STAGING = "staging";

    public static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    public static final String AWS_SECRET_KEY = "AWS_SECRET_KEY";
    public static final String AWS_ENVIRONMENT_NAME = "ENVIRONMENT_NAME";

    public static final String ADMIN_EMAIL = "admin@victiv.com";
    public static final String DEFAULT_DATABASE = "default";

    public static int DEFAULT_SALARY_CAP = 5000000;

    public static final String SPORT_NFL = "nfl";
    public static final String SPORT_MLB = "mlb";
    public static final String TOPIC_REALTIME_PREFIX = "topic.realtime.";
    public static final String TOPIC_FANTASY_POINTS = "fantasy_points_topic";

    public static final String CONFIG_STATS_SOCKET_URL = "stats.inc.socket.url";
    public static final String CONFIG_STATS_SOCKET_PORT = "stats.inc.socket.port.";
    public static final String CONFIG_STATS_SOCKET_USERNAME = "stats.inc.socket.username";
    public static final String CONFIG_STATS_SOCKET_PASSWORD = "stats.inc.socket.password";
    public static final String CONFIG_SIMULATOR_BASE_URL = "contestsimulator.base.url";
    public static final String CONFIG_SIMULATOR_SOCKET_URL = "contestsimulator.socket.url";
    public static final String CONFIG_SIMULATOR_SOCKET_PORT = "contestsimulator.socket.port";
    public static final String CONFIG_DISTRIBUTED_CONFIG = "distributed.configfile";
    public static final String CONFIG_PUBLIC_HOST_IP = "public.host.ip";
    public static final String ENV_HOST_IP = "HOSTIP";
    public static final String SIMULATOR_JVM_ENV_FLAG = "simulator";

    public static final String TIMELINE_TIMESTAMP_FORMAT = "MM/dd/yyyy hh:mm a z";

    /**
     * Constants for historical Stats related stuff
     */
    public static final int YEARS_BACK = 3;

    /*
     * Constants for Hazelcast topics
     */
    public static final String CONTEST_UPDATE_TOPIC = "CONTEST_UPDATE_TOPIC";
    public static final String CONTEST_UPDATE = "CONTEST_UPDATE";
    public static final String CONTEST_ADD = "CONTEST_ADD";
    public static final String CONTEST_REMOVE = "CONTEST_REMOVE";
    public static final String FANTASY_POINT_UPDATE_CONTEST = "FANTASY_POINT_UPDATE_CONTEST_";
    public static final String FANTASY_POINT_UPDATE_ENTRY = "FANTASY_POINT_UPDATE_ENTRY_";
    public static final String FANTASY_POINT_UPDATE_ATHLETE = "FANTASY_POINT_UPDATE_ATHLETE_";
    public static final String FANTASY_POINT_UPDATE_ATHLETE_DRILLIN = "FANTASY_POINT_UPDATE_ATHLETE_DRILLIN_";
    public static final String FANTASY_POINT_UPDATE_LINEUP = "FANTASY_POINT_UPDATE_LINEUP_";
    public static final String FANTASY_POINT_UPDATE_SPORT_EVENT = "FANTASY_POINT_UPDATE_SPORT_EVENT_";
    public static final String ATHLETE_GENERAL_UPDATE_TOPIC = "ATHLETE_GENERAL_UPDATE_";
    public static final String CONTEST_STATE_UPDATE_TOPIC = "CONTEST_STATE_UPDATE_TOPIC";

    /*
     * Constants for Hazelcast maps
     */
    public static final String ATHLETE_SPORT_EVENT_INFO_MAP = "ATHLETE_SPORT_EVENT_INFO_MAP";
    public static final String ATHLETE_PERCENT_OWNED_MAP = "ATHLETE_PERCENT_OWNED_MAP";
    public static final String ATHLETE_FPPG_MAP = "ATHLETE_FPPG_MAP";
    public static final String ATHLETE_DOLLARS_PER_POINT_MAP = "ATHLETE_DOLLARS_PER_POINT_MAP";
    public static final String MLB_DEFENSE_VS_POSITION_MAP = "MLB_DEFENSE_VS_POSITION_MAP";
    public static final String NFL_DEFENSE_VS_POSITION_MAP = "NFL_DEFENSE_VS_POSITION_MAP";
    public static final String NFL_ATHLETE_RANK_MAP = "NFL_ATHLETE_RANK_MAP";
    public static final String NFL_ATHLETE_RANK_CALCULATIONS_MAP = "NFL_ATHLETE_RANK_CALCULATIONS_MAP";     // This should only be used to help making calculations.
    public static final String PROBABLE_PITCHERS_MAP = "PROBABLE_PITCHERS_MAP";
    public static final String STATS_KEY_ACTIVE_PROVIDER = "stats.activeProvider";
    public static final String DISTRIBUTED_SESSION_MAP = "distributed_session";
    public static final String SECURESOCIAL_SESSION_MAP = "session"; // must match the setting in distributed-config.xml
    public static final String STATS_UPDATE_TIMELINE_TIMESTAMPS = "STATS_UPDATE_TIMELINE_TIMESTAMPS";

    /*
     * Contest Entry status codes
     */
    public static final int CONTEST_ENTRY_SUCCESS = 0;
    public static final int CONTEST_ENTRY_ERROR_CONTEST_FULL = 1;
    public static final int CONTEST_ENTRY_ERROR_CONTEST_STARTED = 2;
    public static final int CONTEST_ENTRY_ERROR_NOT_OPEN = 3;
    public static final int CONTEST_ENTRY_ERROR_INVALID_ID = 4;
    public static final int CONTEST_ENTRY_ERROR_SINGLE_ENTRY_DUPE = 5;
    public static final int CONTEST_ENTRY_ERROR_INCOMPATIBLE_LINEUP = 6;
    public static final int CONTEST_ENTRY_ERROR_SESSION_EXPIRED = 7;
    public static final int CONTEST_ENTRY_ERROR_NOT_ENOUGH_SPORT_EVENTS = 8;
    public static final int CONTEST_ENTRY_ERROR_OVER_SALARY_CAP = 9;
    public static final int CONTEST_ENTRY_ERROR_OTHER = 10;
    public static final int CONTEST_ENTRY_ERROR_INSUFFICIENT_FUNDS = 11;
    public static final int CONTEST_ENTRY_ERROR_DUPLICATE_ATHLETES = 12;
    public static final int CONTEST_ENTRY_ERROR_INVALID_LINEUP_SIZE = 13;

    public static final String MINIMUM_SPORT_EVENTS_ERROR = "There must be at least two Sport Events represented by a lineup.";
    public static final String SALARY_CAP_EXCEEDED_ERROR = "This lineup exceeds the salary cap.";
    public static final String LINEUP_SIZE_INVALID_ERROR = "This lineup does not contain the correct number of lineup spots";

    /*
     * Indicator light codes
     */
    public static final int INDICATOR_TEAM_OFF_FIELD = 0;
    public static final int INDICATOR_TEAM_ON_FIELD = 1;
    public static final int INDICATOR_SCORING_OPPORTUNITY = 2;

    /*
 * STATS Inc Constants
 */
    public static final String STATS_INC_KEY_METHOD = "method";
    public static final String STATS_INC_KEY_RESOURCE = "resource";
    public static final String STATS_INC_GAME_STATUS_FINAL = "Final";
    public static final int STATS_INC_GAME_STATUS_FINAL_CODE = 4;
    public static final String STATS_INC_MLB_SOCKET_ROOT_NODE_NAME = "MLB-event";
    public static final String STATS_INC_NFL_SOCKET_ROOT_NODE_NAME = "nfl-event";
    public static final String STATS_INC_S3_CACHE_BUCKET = "stats-inc-cache";

    /*
     * Event Types
     */
    public static final int EVENT_TYPE_NFL_PRE_SEASON = 0;
    public static final int EVENT_TYPE_NFL_REGULAR_SEASON = 1;
    public static final int EVENT_TYPE_NFL_POST_SEASON = 2;

    /*
     * NFL Scoring System
     */
    public static final String SCORING_NFL_PASSING_YARDS_ID = "NFL_1";
    public static final String SCORING_NFL_RUSHING_YARDS_ID = "NFL_2";
    public static final String SCORING_NFL_RECEIVING_YARDS_ID = "NFL_3";
    public static final String SCORING_NFL_PASSING_TOUCHDOWN_ID = "NFL_4";
    public static final String SCORING_NFL_RUSHING_TOUCHDOWN_ID = "NFL_5";
    public static final String SCORING_NFL_RECEIVING_TOUCHDOWN_ID = "NFL_6";
    public static final String SCORING_NFL_GENERAL_TOUCHDOWN_ID = "NFL_7";
    public static final String SCORING_NFL_RECEPTION_ID = "NFL_8";
    public static final String SCORING_NFL_LOST_FUMBLE_ID = "NFL_9";
    public static final String SCORING_NFL_INTERCEPTION_ID = "NFL_10";
    public static final String SCORING_NFL_PUNT_RETURN_TOUCHDOWN_ID = "11";
    public static final String SCORING_NFL_KICK_RETURN_TOUCHDOWN_ID = "NFL_12";
    public static final String SCORING_NFL_FIELD_GOAL_0_39_ID = "NFL_13";
    public static final String SCORING_NFL_FIELD_GOAL_40_49_ID = "NFL_14";
    public static final String SCORING_NFL_FIELD_GOAL_50_PLUS_ID = "NFL_15";
    public static final String SCORING_NFL_PAT_ID = "NFL_16";
    public static final String SCORING_NFL_TWO_POINT_CONVERSION_ID = "NFL_17";
    public static final String SCORING_NFL_SACK_ID = "NFL_18";
    public static final String SCORING_NFL_DEF_INTERCEPTION_ID = "NFL_19";
    public static final String SCORING_NFL_FUMBLE_RECOVERY_ID = "NFL_20";
    public static final String SCORING_NFL_INTERCEPTION_RETURN_TD_ID = "NFL_21";
    public static final String SCORING_NFL_FUMBLE_RECOVERY_TD_ID = "NFL_22";
    public static final String SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_ID = "NFL_23";
    public static final String SCORING_NFL_SAFETY_ID = "NFL_24";
    public static final String SCORING_NFL_BLOCKED_KICK_ID = "NFL_25";
    public static final String SCORING_NFL_POINTS_ALLOWED_ID = "NFL_26";

    public static final String SCORING_NFL_PASSING_YARDS_LABEL = "Passing Yards";
    public static final String SCORING_NFL_RUSHING_YARDS_LABEL = "Rushing Yards";
    public static final String SCORING_NFL_RECEIVING_YARDS_LABEL = "Receiving Yards";
    public static final String SCORING_NFL_PASSING_TOUCHDOWN_LABEL = "Passing TD";
    public static final String SCORING_NFL_RUSHING_TOUCHDOWN_LABEL = "Rushing TD";
    public static final String SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL = "Receiving TD";
    public static final String SCORING_NFL_GENERAL_TOUCHDOWN_LABEL = "Touchdown";
    public static final String SCORING_NFL_RECEPTION_LABEL = "Reception";
    public static final String SCORING_NFL_LOST_FUMBLE_LABEL = "Lost Fumble";
    public static final String SCORING_NFL_INTERCEPTION_LABEL = "Interception";
    public static final String SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL = "Punt Return TD";
    public static final String SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL = "Kick Return TD";
    public static final String SCORING_NFL_FIELD_GOAL_0_39_LABEL = "30 Yard Field Goal Made";
    public static final String SCORING_NFL_FIELD_GOAL_40_49_LABEL = "40 Yard Field Goal Made";
    public static final String SCORING_NFL_FIELD_GOAL_50_PLUS_LABEL = "50 Yard Field Goal Made";
    public static final String SCORING_NFL_PAT_LABEL = "Pat";
    public static final String SCORING_NFL_TWO_POINT_CONVERSION_LABEL = "Two Point Conversion";
    public static final String SCORING_NFL_SACK_LABEL = "Sack";
    public static final String SCORING_NFL_DEF_INTERCEPTION_LABEL = "Def. Interception";
    public static final String SCORING_NFL_FUMBLE_RECOVERY_LABEL = "Fumble Recovery";
    public static final String SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL = "Interception Return TD";
    public static final String SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL = "Fumble Recovery TD";
    public static final String SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL = "Blocked Punt or FG Return TD";
    public static final String SCORING_NFL_SAFETY_LABEL = "Safety";
    public static final String SCORING_NFL_BLOCKED_KICK_LABEL = "Blocked Kick";
    public static final String SCORING_NFL_POINTS_ALLOWED_LABEL = "Points Allowed";

    public static final String SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR = "TDs";
    public static final String SCORING_NFL_PASSING_TOUCHDOWN_ABBR = "PTDs";
    public static final String SCORING_NFL_TWO_POINT_CONVERSION_ABBR = "2PT";
    public static final String SCORING_NFL_PASSING_YARDS_ABBR = "PYDs";
    public static final String SCORING_NFL_RECEPTIONS_ABBR = "REC";
    public static final String SCORING_NFL_RECEIVING_YARDS_ABBR = "REYDs";
    public static final String SCORING_NFL_RUSHING_YARDS_ABBR = "RUYDs";
    public static final String SCORING_NFL_INTERCEPTION_ABBR = "INT";
    public static final String SCORING_NFL_LOST_FUMBLE_ABBR = "FUM";
    public static final String SCORING_NFL_FIELD_GOAL_0_39_ABBR = "FG39";
    public static final String SCORING_NFL_FIELD_GOAL_40_49_ABBR = "FG49";
    public static final String SCORING_NFL_FIELD_GOAL_50_PLUS_ABBR = "FG50+";
    public static final String SCORING_NFL_PAT_ABBR = "PAT";
    public static final String SCORING_NFL_POINTS_ALLOWED_ABBR = "PA";
    public static final String SCORING_NFL_SAFETY_ABBR = "SAF";
    public static final String SCORING_NFL_FUMBLE_RECOVERY_ABBR = "FUM";
    public static final String SCORING_NFL_SACK_ABBR = "SK";
    public static final String SCORING_NFL_BLOCKED_KICK_ABBR = "BLK";

    public static final BigDecimal SCORING_NFL_PASSING_YARDS_FACTOR = new BigDecimal("0.04");
    public static final BigDecimal SCORING_NFL_PASSING_TOUCHDOWN_FACTOR = new BigDecimal("4");
    public static final BigDecimal SCORING_NFL_RECEIVING_YARDS_FACTOR = new BigDecimal("0.1");
    public static final BigDecimal SCORING_NFL_RECEIVING_TOUCHDOWN_FACTOR = new BigDecimal("6");
    public static final BigDecimal SCORING_NFL_RUSHING_YARDS_FACTOR = new BigDecimal("0.1");
    public static final BigDecimal SCORING_NFL_RUSHING_TOUCHDOWN_FACTOR = new BigDecimal("6");
    public static final BigDecimal SCORING_NFL_RECEPTION_FACTOR = new BigDecimal("0.5");
    public static final BigDecimal SCORING_NFL_INTERCEPTION_FACTOR = new BigDecimal("-1");
    public static final BigDecimal SCORING_NFL_LOST_FUMBLE_FACTOR = new BigDecimal("-1");
    public static final BigDecimal SCORING_NFL_PUNT_RETURN_TOUCHDOWN_FACTOR = new BigDecimal("6");
    public static final BigDecimal SCORING_NFL_KICK_RETURN_TOUCHDOWN_FACTOR = new BigDecimal("6");
    public static final BigDecimal SCORING_NFL_FIELD_GOAL_MADE_30_39_FACTOR = new BigDecimal("3");
    public static final BigDecimal SCORING_NFL_FIELD_GOAL_MADE_40_49_FACTOR = new BigDecimal("4");
    public static final BigDecimal SCORING_NFL_FIELD_GOAL_MADE_50_PLUS_FACTOR = new BigDecimal("5");
    public static final BigDecimal SCORING_NFL_PAT_FACTOR = new BigDecimal("1");
    public static final BigDecimal SCORING_NFL_TWO_POINT_CONVERSION_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_NFL_SACK_FACTOR = new BigDecimal("1");
    public static final BigDecimal SCORING_NFL_DEF_INTERCEPTION_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_NFL_FUMBLE_RECOVERY_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_NFL_INTERCEPTION_RETURN_TD_FACTOR = new BigDecimal("6");
    public static final BigDecimal SCORING_NFL_FUMBLE_RECOVERY_TD_FACTOR = new BigDecimal("6");
    public static final BigDecimal SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_FACTOR = new BigDecimal("6");
    public static final BigDecimal SCORING_NFL_SAFETY_FACTOR = new BigDecimal("4");
    public static final BigDecimal SCORING_NFL_BLOCKED_KICK_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_NFL_POINTS_ALLOWED_FACTOR = new BigDecimal("-0.5");
    public static final BigDecimal SCORING_NFL_DEFENSE_INITIAL_POINTS = new BigDecimal("12");

    public static final Map<String, String> SCORING_NFL_NAME_TO_ABBR_MAP = new HashMap<>();
    public static final Map<String, String> SCORING_NFL_NAME_TO_ID_MAP = new HashMap<>();


    /*
     * MLB Scoring System
     */
    public static final String SCORING_MLB_SINGLE_LABEL = "Single";
    public static final String SCORING_MLB_DOUBLE_LABEL = "Double";
    public static final String SCORING_MLB_TRIPLE_LABEL = "Triple";
    public static final String SCORING_MLB_HOMERUN_LABEL = "Home Run";
    public static final String SCORING_MLB_INNING_PITCHED_LABEL = "Innings Pitched";
    public static final String SCORING_MLB_STRIKEOUT_LABEL = "Strikeout";
    public static final String SCORING_MLB_EARNED_RUN_LABEL = "Earned Run";
    public static final String SCORING_MLB_PITCHER_HIT_LABEL = "Pitcher Hit";
    public static final String SCORING_MLB_HIT_LABEL = "Hit";
    public static final String SCORING_MLB_PITCHER_WALK_LABEL = "Pitcher Walk";
    public static final String SCORING_MLB_WALK_LABEL = "Walk";
    public static final String SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL = "Pitcher Hit By Pitch";
    public static final String SCORING_MLB_HIT_BY_PITCH_LABEL = "Hit By Pitch";
    public static final String SCORING_MLB_RUN_BATTED_IN_LABEL = "Run Batted In";
    public static final String SCORING_MLB_RUN_LABEL = "Run";
    public static final String SCORING_MLB_STOLEN_BASE_LABEL = "Stolen Base";
    public static final String SCORING_MLB_CAUGHT_STEALING_LABEL = "Caught Stealing";
    public static final String SCORING_MLB_WIN_LABEL = "Win";
    public static final String SCORING_MLB_COMPLETE_GAME_LABEL = "Complete Game";
    public static final String SCORING_MLB_COMPLETE_GAME_SHUTOUT_LABEL = "Complete Game Shutout";

    public static final String SCORING_MLB_WIN_ABBR = "W";
    public static final String SCORING_MLB_COMPLETE_GAME_ABBR = "CG";
    public static final String SCORING_MLB_STRIKEOUT_ABBR = "SO";
    public static final String SCORING_MLB_EARNED_RUN_ABBR = "ER";
    public static final String SCORING_MLB_HITS_ALLOWED_ABBR = "H";
    public static final String SCORING_MLB_INNINGS_PITCHED_ABBR = "IP";
    public static final String SCORING_MLB_HIT_BATSMEN_ABBR = "HB";

    public static final String SCORING_MLB_HOME_RUN_ABBR = "HR";
    public static final String SCORING_MLB_RUN_ABBR = "R";
    public static final String SCORING_MLB_RBI_ABBR = "RBI";
    public static final String SCORING_MLB_TRIPLE_ABBR = "3B";
    public static final String SCORING_MLB_DOUBLE_ABBR = "2B";
    public static final String SCORING_MLB_SINGLE_ABBR = "1B";
    public static final String SCORING_MLB_STOLEN_BASE_ABBR = "SB";
    public static final String SCORING_MLB_WALK_ABBR = "BB";
    public static final String SCORING_MLB_HIT_BY_PITCH_ABBR = "HBP";
    public static final String SCORING_MLB_CAUGHT_STEALING_ABBR = "CS";

    public static final Map<String, String> SCORING_MLB_NAME_TO_ABBR_MAP = new HashMap<>();

    public static final BigDecimal SCORING_MLB_SINGLE_FACTOR = new BigDecimal("3");
    public static final BigDecimal SCORING_MLB_DOUBLE_FACTOR = new BigDecimal("5");
    public static final BigDecimal SCORING_MLB_TRIPLE_FACTOR = new BigDecimal("8");
    public static final BigDecimal SCORING_MLB_HOMERUN_FACTOR = new BigDecimal("10");
    public static final BigDecimal SCORING_MLB_RUN_BATTED_IN_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_MLB_RUN_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_MLB_WALK_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_MLB_STOLEN_BASE_FACTOR = new BigDecimal("5");
    public static final BigDecimal SCORING_MLB_CAUGHT_STEALING_FACTOR = new BigDecimal("-2");
    public static final BigDecimal SCORING_MLB_HIT_BY_PITCH_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_MLB_INNING_PITCHED_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_MLB_STRIKEOUT_FACTOR = new BigDecimal("2");
    public static final BigDecimal SCORING_MLB_EARNED_RUN_FACTOR = new BigDecimal("-2");
    public static final BigDecimal SCORING_MLB_PITCHER_HIT_FACTOR = new BigDecimal("-0.6");
    public static final BigDecimal SCORING_MLB_PITCHER_WALK_FACTOR = new BigDecimal("-0.6");
    public static final BigDecimal SCORING_MLB_PITCHER_HIT_BY_PITCH_FACTOR = new BigDecimal("-0.6");
    public static final BigDecimal SCORING_MLB_WIN_FACTOR = new BigDecimal("4");
    public static final BigDecimal SCORING_MLB_COMPLETE_GAME_FACTOR = new BigDecimal("2.5");
    public static final BigDecimal SCORING_MLB_COMPLETE_GAME_SHUTOUT_FACTOR = new BigDecimal("2.5");

    /*
     * Labels for stat averages in athlete compare.
     */
    public static final String STATS_MLB_EXTRA_BASE_HITS = "Extra Base Hits";
    public static final String STATS_MLB_AT_BATS = "At Bats";
    public static final String STATS_MLB_ON_BASE_PLUS_SLUGGING = "OPS";
    public static final String STATS_MLB_RBIS = "RBIs";
    public static final String STATS_MLB_STRIKEOUTS = "Strikeouts";
    public static final String STATS_MLB_WALKS = "Walks";

    public static final String STATS_MLB_INNINGS_PITCHED = "Innings Pitched";
    public static final String STATS_MLB_STRIKEOUT_TO_WALK_RATIO = "Strikeout-to-Walk Ratio";
    public static final String STATS_MLB_OPP_BATTING_AVG = "Opponent Batting Average";
    public static final String STATS_MLB_OPPONENT_OBA = "Opponent On-Base Average";

    public static final String STATS_NFL_RUSHING_TOUCHDOWNS = "Rushing Touchdowns";
    public static final String STATS_NFL_RUSHING_YARDS = "Rushing Yards";
    public static final String STATS_NFL_RUSHING_ATTEMPTS = "Rushing Attempts";
    public static final String STATS_NFL_RECEPTIONS = "Receptions";
    public static final String STATS_NFL_RECEIVING_TARGETS = "Targets";
    public static final String STATS_NFL_RECEIVING_TOUCHDOWNS = "Receiving Touchdowns";
    public static final String STATS_NFL_RECEIVING_YARDS = "Receiving Yards";
    public static final String STATS_NFL_FUMBLES = "Fumbles";
    public static final String STATS_NFL_PASSING_YARDS = "Passing Yards";
    public static final String STATS_NFL_PASSING_TOUCHDOWNS = "Passing Touchdowns";
    public static final String STATS_NFL_PASSING_ATTEMPTS = "Passing Attempts";
    public static final String STATS_NFL_SACKS = "Sacks";
    public static final String STATS_NFL_INTERCEPTIONS = "Interceptions";
    public static final String STATS_NFL_POINTS_ALLOWED = "Points Allowed";
    public static final String STATS_NFL_DEFENSIVE_TOUCHDOWNS = "Defensive Touchdowns";
    public static final String STATS_NFL_SAFETIES = "Safeties";
    public static final String STATS_NFL_FUMBLE_RECOVERIES = "Fumble Recoveries";
    public static final String STATS_NFL_BLOCKED_KICKS = "Blocked Kicks";


    public static final String[] STATS_ARRAY_FOR_MLB_PITCHER = {
            GlobalConstants.SCORING_MLB_WIN_LABEL,
            GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL,
            GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL,
            GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL,
            GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL,
            GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL,
            GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL,
            GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL
    };

    public static final String[] STATS_ARRAY_FOR_MLB_BATTER = {
            GlobalConstants.SCORING_MLB_SINGLE_LABEL,
            GlobalConstants.SCORING_MLB_DOUBLE_LABEL,
            GlobalConstants.SCORING_MLB_TRIPLE_LABEL,
            GlobalConstants.SCORING_MLB_HOMERUN_LABEL,
            GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL,
            GlobalConstants.SCORING_MLB_RUN_LABEL,
            GlobalConstants.SCORING_MLB_WALK_LABEL,
            GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL,
            GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL,
            GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL
    };

    public static final String[] STATS_ARRAY_FOR_NFL_DEFENSE = {
            GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL,
            GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL,
            GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL,
            GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL,
            GlobalConstants.SCORING_NFL_SAFETY_LABEL,
            GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL,
            GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL,
            GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL,
            GlobalConstants.SCORING_NFL_SACK_LABEL
    };

    public static final String[] STATS_ARRAY_FOR_NFL_OFFENSE = {
            GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL,
            GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL,
            GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL,
            GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL,
            GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL,
            GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL,
            GlobalConstants.SCORING_NFL_RECEPTION_LABEL,
            GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL,
            GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL,
            GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL,
            GlobalConstants.SCORING_NFL_INTERCEPTION_LABEL
    };

    static {
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_WIN_LABEL, SCORING_MLB_WIN_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_COMPLETE_GAME_LABEL, SCORING_MLB_COMPLETE_GAME_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_STRIKEOUT_LABEL, SCORING_MLB_STRIKEOUT_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_EARNED_RUN_LABEL, SCORING_MLB_EARNED_RUN_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_PITCHER_HIT_LABEL, SCORING_MLB_HITS_ALLOWED_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_INNING_PITCHED_LABEL, SCORING_MLB_INNINGS_PITCHED_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_PITCHER_WALK_LABEL, SCORING_MLB_WALK_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, SCORING_MLB_HIT_BATSMEN_ABBR);

        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_HOMERUN_LABEL, SCORING_MLB_HOME_RUN_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_RUN_LABEL, SCORING_MLB_RUN_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_RUN_BATTED_IN_LABEL, SCORING_MLB_RBI_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_TRIPLE_LABEL, SCORING_MLB_TRIPLE_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_DOUBLE_LABEL, SCORING_MLB_DOUBLE_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_SINGLE_LABEL, SCORING_MLB_SINGLE_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_STOLEN_BASE_LABEL, SCORING_MLB_STOLEN_BASE_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_HIT_BY_PITCH_LABEL, SCORING_MLB_HIT_BY_PITCH_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_WALK_LABEL, SCORING_MLB_WALK_ABBR);
        SCORING_MLB_NAME_TO_ABBR_MAP.put(SCORING_MLB_CAUGHT_STEALING_LABEL, SCORING_MLB_CAUGHT_STEALING_ABBR);

        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL, SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_RUSHING_TOUCHDOWN_LABEL, SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_PASSING_TOUCHDOWN_LABEL, SCORING_NFL_PASSING_TOUCHDOWN_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_TWO_POINT_CONVERSION_LABEL, SCORING_NFL_TWO_POINT_CONVERSION_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_PASSING_YARDS_LABEL, SCORING_NFL_PASSING_YARDS_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_RECEIVING_YARDS_LABEL, SCORING_NFL_RECEIVING_YARDS_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_RUSHING_YARDS_LABEL, SCORING_NFL_RUSHING_YARDS_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_RECEPTION_LABEL, SCORING_NFL_RECEPTIONS_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_LOST_FUMBLE_LABEL, SCORING_NFL_LOST_FUMBLE_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_INTERCEPTION_LABEL, SCORING_NFL_INTERCEPTION_ABBR);

        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_POINTS_ALLOWED_LABEL, SCORING_NFL_POINTS_ALLOWED_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_SAFETY_LABEL, SCORING_NFL_SAFETY_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_FUMBLE_RECOVERY_LABEL, SCORING_NFL_FUMBLE_RECOVERY_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_DEF_INTERCEPTION_LABEL, SCORING_NFL_INTERCEPTION_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_SACK_LABEL, SCORING_NFL_SACK_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL, SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL, SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL, SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
        SCORING_NFL_NAME_TO_ABBR_MAP.put(SCORING_NFL_BLOCKED_KICK_LABEL, SCORING_NFL_BLOCKED_KICK_ABBR);

        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_PASSING_YARDS_LABEL, SCORING_NFL_PASSING_YARDS_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_RUSHING_YARDS_LABEL, SCORING_NFL_RUSHING_YARDS_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_RECEIVING_YARDS_LABEL, SCORING_NFL_RECEIVING_YARDS_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_PASSING_TOUCHDOWN_LABEL, SCORING_NFL_PASSING_TOUCHDOWN_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_RUSHING_TOUCHDOWN_LABEL, SCORING_NFL_RUSHING_TOUCHDOWN_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL, SCORING_NFL_RECEIVING_TOUCHDOWN_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_GENERAL_TOUCHDOWN_LABEL, SCORING_NFL_GENERAL_TOUCHDOWN_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_RECEPTION_LABEL, SCORING_NFL_RECEPTION_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_LOST_FUMBLE_LABEL, SCORING_NFL_LOST_FUMBLE_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_INTERCEPTION_LABEL, SCORING_NFL_INTERCEPTION_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, SCORING_NFL_PUNT_RETURN_TOUCHDOWN_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, SCORING_NFL_KICK_RETURN_TOUCHDOWN_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_FIELD_GOAL_0_39_LABEL, SCORING_NFL_FIELD_GOAL_0_39_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_FIELD_GOAL_40_49_LABEL, SCORING_NFL_FIELD_GOAL_40_49_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_FIELD_GOAL_50_PLUS_LABEL, SCORING_NFL_FIELD_GOAL_50_PLUS_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_PAT_LABEL, SCORING_NFL_PAT_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_TWO_POINT_CONVERSION_LABEL, SCORING_NFL_TWO_POINT_CONVERSION_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_SACK_LABEL, SCORING_NFL_SACK_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_DEF_INTERCEPTION_LABEL, SCORING_NFL_DEF_INTERCEPTION_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_FUMBLE_RECOVERY_LABEL, SCORING_NFL_FUMBLE_RECOVERY_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL, SCORING_NFL_INTERCEPTION_RETURN_TD_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL, SCORING_NFL_FUMBLE_RECOVERY_TD_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL, SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_SAFETY_LABEL, SCORING_NFL_SAFETY_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_BLOCKED_KICK_LABEL, SCORING_NFL_BLOCKED_KICK_ID);
        SCORING_NFL_NAME_TO_ID_MAP.put(SCORING_NFL_POINTS_ALLOWED_LABEL, SCORING_NFL_POINTS_ALLOWED_ID);

    }
}
