
# --- !Ups

create table athlete (
  id                        integer auto_increment not null,
  stat_provider_id          integer not null,
  first_name                varchar(255) not null,
  last_name                 varchar(255) not null,
  team_id                   integer,
  uniform                   varchar(255) not null,
  injury_status             varchar(255) not null,
  active                    tinyint(1) default 0 not null,
  fpp_season                float,
  fpp_career                float,
  fpp_current_game          float,
  constraint uq_athlete_stat_provider_id unique (stat_provider_id),
  constraint pk_athlete primary key (id))
;

create table athlete_salary (
  id                        integer auto_increment not null,
  athlete_id                integer,
  sport_event_grouping_id   integer,
  salary                    integer,
  constraint uq_athlete_salary_1 unique (athlete_id,sport_event_grouping_id),
  constraint pk_athlete_salary primary key (id))
;

create table athlete_sport_event_info (
  id                        integer auto_increment not null,
  sport_event_id            integer,
  athlete_id                integer,
  fantasy_points            Decimal(10,2) not null,
  stats                     text,
  timeline                  text,
  indicator                 integer,
  constraint uq_athlete_sport_event_info_1 unique (sport_event_id,athlete_id),
  constraint pk_athlete_sport_event_info primary key (id))
;

create table avb_lineup (
  id                        integer auto_increment not null,
  avb_matchup_id            integer not null,
  constraint pk_avb_lineup primary key (id))
;

create table avb_matchup (
  id                        integer auto_increment not null,
  constraint pk_avb_matchup primary key (id))
;

create table contest (
  id                        integer auto_increment not null,
  url_id                    varchar(255) not null,
  contest_type_id           integer,
  league_id                 integer,
  current_entries           integer not null default 0,
  capacity                  integer not null default 0,
  is_public                 tinyint(1) default 0 not null,
  entry_fee                 integer not null default 0,
  guaranteed                tinyint(1) default 0 not null,
  allowed_entries           integer not null,
  sport_event_grouping_id   integer,
  salary_cap                integer not null,
  start_time                datetime not null,
  created_from_id           integer,
  contest_state_id          integer,
  constraint uq_contest_url_id unique (url_id),
  constraint pk_contest primary key (id))
;

create table contest_entry_fee (
  id                        integer auto_increment not null,
  league_id                 integer,
  entry_fee                 integer not null,
  constraint pk_contest_entry_fee primary key (id))
;

create table contest_grouping (
  id                        integer auto_increment not null,
  league_id                 integer,
  name                      varchar(255) not null,
  constraint pk_contest_grouping primary key (id))
;

create table contest_number_of_users (
  id                        integer auto_increment not null,
  league_id                 integer,
  minimum                   integer not null,
  maximum                   integer not null,
  constraint pk_contest_number_of_users primary key (id))
;

create table contest_payout (
  id                        integer auto_increment not null,
  contest_id                integer not null,
  leading_position          integer,
  trailing_position         integer,
  payout_amount             integer,
  constraint pk_contest_payout primary key (id))
;

create table contest_results (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  contest_id                integer,
  entry_id                  integer,
  payout                    integer,
  constraint pk_contest_results primary key (id))
;

create table contest_salary (
  id                        integer auto_increment not null,
  league_id                 integer,
  salary                    integer not null,
  constraint pk_contest_salary primary key (id))
;

create table contest_state (
  dtype                     varchar(10) not null,
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_contest_state primary key (id))
;

create table contest_suggestion (
  id                        integer auto_increment not null,
  contest_type_id           integer,
  capacity                  integer,
  suggestion_contest_type_id integer,
  suggestion_capacity       integer,
  constraint pk_contest_suggestion primary key (id))
;

create table contest_template (
  id                        integer auto_increment not null,
  contest_type_id           integer,
  capacity                  integer not null,
  is_public                 tinyint(1) default 0 not null,
  entry_fee                 integer not null,
  allowed_entries           integer not null,
  salary_cap                integer not null,
  rake_percentage           float not null,
  payout_rounding           integer not null,
  auto_populate             tinyint(1) default 0 not null,
  constraint pk_contest_template primary key (id))
;

create table contest_template_payout (
  id                        integer auto_increment not null,
  contest_template_id       integer not null,
  leading_position          integer not null,
  trailing_position         integer not null,
  payout_percentage         float not null,
  rounding_mode             integer not null,
  constraint ck_contest_template_payout_rounding_mode check (rounding_mode in (0,1,2,3,4,5,6,7)),
  constraint pk_contest_template_payout primary key (id))
;

create table contest_type (
  id                        integer auto_increment not null,
  name                      varchar(255),
  abbr                      varchar(255),
  constraint pk_contest_type primary key (id))
;

create table entry (
  id                        integer auto_increment not null,
  user_id                   bigint,
  contest_id                integer,
  points                    double not null,
  lineup_id                 integer,
  constraint pk_entry primary key (id))
;

create table league (
  id                        integer auto_increment not null,
  sport_id                  integer,
  name                      varchar(255) not null,
  abbreviation              varchar(255) not null,
  display_name              varchar(255) not null,
  is_active                 tinyint(1) default 0 not null,
  constraint pk_league primary key (id))
;

create table lineup (
  id                        integer auto_increment not null,
  name                      varchar(255) not null,
  user_id                   bigint,
  league_id                 integer,
  performance_data          varchar(255),
  projected_performance_data varchar(255),
  sport_event_grouping_id   integer,
  constraint pk_lineup primary key (id))
;

create table lineup_spot (
  id                        integer auto_increment not null,
  lineup_id                 integer not null,
  athlete_id                integer,
  position_id               integer,
  athlete_sport_event_info_id integer,
  constraint uq_lineup_spot_1 unique (athlete_id,lineup_id),
  constraint pk_lineup_spot primary key (id))
;

create table lineup_template (
  id                        integer auto_increment not null,
  league_id                 integer,
  position_id               integer,
  number_of_athletes        integer not null,
  constraint pk_lineup_template primary key (id))
;

create table position (
  id                        integer auto_increment not null,
  name                      varchar(255) not null,
  abbreviation              varchar(255) not null,
  sport_id                  integer,
  constraint pk_position primary key (id))
;

create table scoring_rule (
  id                        integer auto_increment not null,
  rule_name                 varchar(255) not null,
  league_id                 integer,
  scoring_factor            Decimal(10,2) not null,
  constraint pk_scoring_rule primary key (id))
;

create table secure_social_token (
  uuid                      varchar(255) not null,
  creation_time             datetime,
  email                     varchar(255),
  expiration_time           datetime,
  is_sign_up                tinyint(1) default 0,
  constraint pk_secure_social_token primary key (uuid))
;

create table sport (
  id                        integer auto_increment not null,
  name                      varchar(255) not null,
  constraint pk_sport primary key (id))
;

create table sport_event (
  id                        integer auto_increment not null,
  stat_provider_id          integer not null,
  league_id                 integer,
  start_time                datetime not null,
  short_description         varchar(255) not null,
  description               varchar(255) not null,
  units_remaining           integer not null,
  complete                  tinyint(1) default 0,
  week                      integer,
  season                    integer,
  event_type_id             integer,
  constraint uq_sport_event_stat_provider_id unique (stat_provider_id),
  constraint pk_sport_event primary key (id))
;

create table sport_event_date_range_selector (
  id                        integer auto_increment not null,
  sport_event_grouping_type_id integer not null,
  start_day_of_week         integer,
  start_hour_of_day         integer,
  start_minute_of_hour      integer,
  end_day_of_week           integer,
  end_hour_of_day           integer,
  end_minute_of_hour        integer,
  constraint ck_sport_event_date_range_selector_start_day_of_week check (start_day_of_week in (0,1,2,3,4,5,6)),
  constraint ck_sport_event_date_range_selector_end_day_of_week check (end_day_of_week in (0,1,2,3,4,5,6)),
  constraint pk_sport_event_date_range_selector primary key (id))
;

create table sport_event_grouping (
  id                        integer auto_increment not null,
  sport_event_grouping_type_id integer,
  event_date                datetime,
  constraint pk_sport_event_grouping primary key (id))
;

create table sport_event_grouping_type (
  id                        integer auto_increment not null,
  league_id                 integer,
  name                      varchar(255),
  constraint pk_sport_event_grouping_type primary key (id))
;

create table stats_athlete_by_season_raw (
  id                        integer auto_increment not null,
  last_fetched              datetime,
  raw_data                  LONGTEXT,
  stats_athlete_id          integer,
  unique_key                varchar(255),
  previously_failed         tinyint(1) default 0,
  season                    integer,
  event_type_id             integer,
  last_update               datetime not null,
  constraint uq_stats_athlete_by_season_raw_unique_key unique (unique_key),
  constraint pk_stats_athlete_by_season_raw primary key (id))
;

create table stats_live_feed_data (
  id                        integer auto_increment not null,
  sport_event_id            integer,
  data                      longtext not null,
  data_hash                 varchar(32) not null,
  constraint pk_stats_live_feed_data primary key (id))
;

create table stats_mlb_batting (
  id                        integer auto_increment not null,
  stat_provider_id          integer,
  event_id                  integer,
  at_bats                   integer,
  flyballs                  integer,
  groundballs               integer,
  line_drives               integer,
  ground_into_double_plays_opportunities integer,
  ground_into_double_plays_total integer,
  hit_by_pitch              integer,
  hits_doubles              integer,
  hits_extra_base_hits      integer,
  hits_home_runs            integer,
  hits_singles              integer,
  hits_total                integer,
  hits_triples              integer,
  on_base_percentage        float,
  slugging_percentage       float,
  on_base_plus_slugging_percentage float,
  pitches_seen_rate_per_plate_appearance integer,
  pitches_seen_total        integer,
  plate_appearances         integer,
  runs_batted_in_game_winning integer,
  runs_batted_in_total      integer,
  runs_scored               integer,
  sacrifices_hits           integer,
  sacrifices_flies          integer,
  stolen_bases_attempts     integer,
  stolen_bases_caught_stealing integer,
  stolen_bases_total        integer,
  strike_outs               integer,
  times_on_base             integer,
  total_bases               integer,
  walks_intentional         integer,
  walks_total               integer,
  fpp                       Decimal(10,2),
  average_fpp               varchar(255),
  opposition                integer,
  last_update               datetime not null,
  constraint pk_stats_mlb_batting primary key (id))
;

create table stats_mlb_fielding (
  id                        integer auto_increment not null,
  stat_provider_id          integer,
  position                  varchar(255),
  assists                   integer,
  balls_hit_in_zone         integer,
  double_plays              integer,
  errors                    integer,
  fielding_outs             integer,
  hits_allowed              integer,
  innings                   float,
  opportunities             integer,
  put_outs                  integer,
  triple_plays              integer,
  constraint pk_stats_mlb_fielding primary key (id))
;

create table stats_mlb_pitching (
  id                        integer auto_increment not null,
  stat_provider_id          integer,
  event_id                  integer,
  balks                     integer,
  balls_hit_allowed_flyballs integer,
  balls_hit_allowed_ground_balls integer,
  balls_hit_allowed_line_drives integer,
  base_runners_allowed_rate_per_nine_innings double,
  base_runners_allowed_total integer,
  earned_run_average        double,
  games_complete            integer,
  games_quality_starts      integer,
  games_shutouts            integer,
  games_starts              integer,
  games_total               integer,
  ground_into_double_plays_percentage double,
  ground_into_double_plays_rate_per_nine_innings double,
  ground_into_double_plays_total integer,
  hit_batsmen               integer,
  hits_allowed_doubles      integer,
  hits_allowed_homeruns_total integer,
  hits_allowed_homeruns_rate_per_nine_innings double,
  hits_allowed_rate_per_nine_innings double,
  hits_allowed_total        integer,
  hits_allowed_triples      integer,
  wild_pitches              integer,
  total_batters_faced       integer,
  walks_intentional         integer,
  walks_total               integer,
  walks_plus_hits_rate_per_inning double,
  walks_plus_hits_total     double,
  strikeout_walk_ratio      double,
  total_bases_against       integer,
  strikeouts_rate_per_nine_innings double,
  strikeouts_total          integer,
  stolen_bases_against_attempts integer,
  stolen_bases_against_caught_stealing integer,
  stolen_bases_against_total integer,
  saves_blown               integer,
  saves_opportunities       integer,
  saves_total               integer,
  sacrifices_flies          integer,
  sacrifices_hits           integer,
  run_support_rate_per_nine_innings double,
  run_support_total         integer,
  runs_allowed_earned_runs  integer,
  runs_allowed_runs_batted_in integer,
  runs_allowed_total        integer,
  pitches_per_inning        double,
  pitches_total             integer,
  holds                     integer,
  inherited_runners_stranded integer,
  inherited_runners_total   integer,
  innings_pitched           double,
  opponent_at_bats          integer,
  opponent_batting_average  double,
  opponent_on_base_percentage double,
  opponent_slugging_percentage double,
  pickoffs_plus_pitcher_caught_stealing integer,
  pickoffs_throws           integer,
  pickoffs_throws_per_base_runner double,
  pickoffs_total            integer,
  fpp                       Decimal(10,2),
  average_fpp               varchar(255),
  opposition                integer,
  last_update               datetime not null,
  constraint pk_stats_mlb_pitching primary key (id))
;

create table stats_nfl_athlete_by_event (
  id                        integer auto_increment not null,
  athlete_id                integer,
  sport_event_id            integer,
  team_id                   integer,
  position                  varchar(255),
  season                    integer,
  week                      integer,
  event_type_id             integer,
  opponent_id               integer,
  location_id               integer,
  start_time                datetime,
  participation_offense     integer,
  participation_defense     integer,
  participation_special_teams integer,
  unique_key                varchar(255),
  fpp_in_this_event         Decimal(10,2),
  fantasy_points_per_game_range TEXT,
  fantasy_points_avg_range  TEXT,
  passing_completions       integer,
  passing_completion_percentage float,
  passing_interceptions     integer,
  passing_yards_per_attempt float,
  passing_sacked            integer,
  passing_sacked_yards_lost integer,
  passing_long              integer,
  passing_is_long_touchdown tinyint(1) default 0,
  passing_rating            float,
  passing_yards_at_catch    integer,
  passing_yards_at_catch_average float,
  passing_yards_after_catch integer,
  passing_yards_after_catch_average float,
  passing_attempts          integer,
  passing_attempts_per_game_range TEXT,
  passing_attempts_percent_of_max_per_game_range TEXT,
  passing_attempts_avg_range TEXT,
  passing_attempts_percent_of_max_avg_range TEXT,
  passing_yards             integer,
  passing_yards_per_game_range TEXT,
  passing_yards_avg_range   TEXT,
  receiving_receptions      integer,
  receiving_average         float,
  receiving_long            integer,
  receiving_is_long_touchdown tinyint(1) default 0,
  receiving_yards_at_catch  integer,
  receiving_yards_at_catch_average float,
  receiving_yards_after_catch integer,
  receiving_yards_after_catch_average float,
  receiving_yards           integer,
  receiving_yards_per_game_range TEXT,
  receiving_yards_avg_range TEXT,
  receiving_targets         integer,
  receiving_targets_per_game_range TEXT,
  receiving_targets_percent_of_max_per_game_range TEXT,
  receiving_targets_avg_range TEXT,
  receiving_targets_percent_of_max_avg_range TEXT,
  rushing_average           float,
  rushing_long              integer,
  rushing_is_long_touchdown tinyint(1) default 0,
  rushing_stuffed           integer,
  rushing_stuffed_yards_lost integer,
  rushing_stuffed_percentage float,
  rushing_attempts          integer,
  rushing_attempts_per_game_range TEXT,
  rushing_attempts_percent_of_max_per_game_range TEXT,
  rushing_attempts_avg_range TEXT,
  rushing_attempts_percent_of_max_avg_range TEXT,
  rushing_yards             integer,
  rushing_yards_per_game_range TEXT,
  rushing_yards_avg_range   TEXT,
  passing_touchdowns        integer,
  rushing_touchdowns        integer,
  receiving_touchdowns      integer,
  punt_returning_touchdowns integer,
  kickoff_returning_touchdowns integer,
  touch_downs_per_game_range TEXT,
  touch_downs_avg_range     TEXT,
  opponent_points_allowed_at_position_per_game_range TEXT,
  opponent_points_allowed_at_position_avg_range TEXT,
  first_downs_total         integer,
  first_downs_rushing       integer,
  first_downs_passing       integer,
  first_downs_receiving     integer,
  first_downs_penalty       integer,
  fumbles_total             integer,
  fumbles_pass              integer,
  fumbles_rush              integer,
  fumbles_special_teams     integer,
  fumbles_receiving         integer,
  fumbles_defense           integer,
  fumbles_misc              integer,
  fumbles_lost_total        integer,
  fumbles_lost_pass         integer,
  fumbles_lost_rush         integer,
  fumbles_lost_special_teams integer,
  fumbles_lost_receiving    integer,
  fumbles_lost_defense      integer,
  fumbles_lost_misc         integer,
  two_point_conversions_made integer,
  two_point_conversions_passes integer,
  two_point_conversions_attempts integer,
  penalties_number          integer,
  penalties_yards           integer,
  penalties_false_start     integer,
  penalties_holding         integer,
  kickoffs_number           integer,
  kickoffs_end_zone         integer,
  kickoffs_touchback_percentage float,
  kickoffs_yards            integer,
  kickoffs_average          float,
  kickoffs_returns          integer,
  kickoffs_return_yards     integer,
  kickoffs_return_average   float,
  kickoffs_touchbacks       integer,
  kicking_extra_points_made integer,
  kicking_extra_points_attempts integer,
  kicking_extra_points_blocked integer,
  kicking_extra_points_percentage float,
  kicking_long              integer,
  kicking_points            integer,
  kicking_field_goals_made0to19 integer,
  kicking_field_goals_attempts0to19 integer,
  kicking_field_goals_blocked0to19 integer,
  kicking_field_goals_percentage0to19 float,
  kicking_field_goals_made20to29 integer,
  kicking_field_goals_attempts20to29 integer,
  kicking_field_goals_blocked20to29 integer,
  kicking_field_goals_percentage20to29 float,
  kicking_field_goals_made30to39 integer,
  kicking_field_goals_attempts30to39 integer,
  kicking_field_goals_blocked30to39 integer,
  kicking_field_goals_percentage30to39 float,
  kicking_field_goals_made40to49 integer,
  kicking_field_goals_attempts40to49 integer,
  kicking_field_goals_blocked40to49 integer,
  kicking_field_goals_percentage40to49 float,
  kicking_field_goals_made50plus integer,
  kicking_field_goals_attempts50plus integer,
  kicking_field_goals_blocked50plus integer,
  kicking_field_goals_percentage50plus float,
  punt_returning_returns    integer,
  punt_returning_yards      integer,
  punt_returning_average    float,
  punt_returning_fair_catches integer,
  punt_returning_long       integer,
  punt_returning_is_long_touchdown tinyint(1) default 0,
  kickoff_returning_returns integer,
  kickoff_returning_yards   integer,
  kickoff_returning_average float,
  kickoff_returning_fair_catches integer,
  kickoff_returning_long    integer,
  kickoff_returning_is_long_touchdown tinyint(1) default 0,
  last_update               datetime not null,
  constraint uq_stats_nfl_athlete_by_event_unique_key unique (unique_key),
  constraint pk_stats_nfl_athlete_by_event primary key (id))
;

create table stats_nfl_defense_by_event (
  id                        integer auto_increment not null,
  athlete_id                integer,
  sport_event_id            integer,
  team_id                   integer,
  season                    integer,
  week                      integer,
  opponent_id               integer,
  location_id               integer,
  start_time                datetime,
  event_type_id             integer,
  fpp_in_this_event         Decimal(10,2),
  fantasy_points_per_game_range TEXT,
  fantasy_points_avg_range  TEXT,
  interceptions             integer,
  fumble_recoveries         integer,
  interception_return_touchdowns integer,
  fumble_recovery_touchdowns integer,
  kick_return_touchdowns    integer,
  punt_return_touchdowns    integer,
  blocked_punt_or_field_goal_return_touchdowns integer,
  safeties                  integer,
  sacks                     integer,
  blocked_kicks             integer,
  points_allowed            integer,
  last_update               datetime not null,
  constraint pk_stats_nfl_defense_by_event primary key (id))
;

create table stats_nfl_game_odds (
  id                        integer auto_increment not null,
  stats_event_id            integer,
  lines_change_date         datetime,
  opening_favorite_team_id  integer,
  current_favorite_team_id  integer,
  opening_favorite_money    integer,
  current_favorite_money    integer,
  opening_favorite_points   float,
  current_favorite_points   float,
  opening_away_money        integer,
  current_away_money        integer,
  opening_home_money        integer,
  current_home_money        integer,
  opening_over_money        integer,
  current_over_money        integer,
  opening_total             float,
  current_total             float,
  opening_under_money       integer,
  current_under_money       integer,
  opening_underdog_money    integer,
  current_underdog_money    integer,
  constraint uq_stats_nfl_game_odds_stats_event_id unique (stats_event_id),
  constraint pk_stats_nfl_game_odds primary key (id))
;

create table stats_nfl_projection (
  id                        integer auto_increment not null,
  athlete_id                integer,
  stats_athlete_id          integer,
  sport_event_id            integer,
  position                  varchar(255),
  season                    integer,
  week                      integer,
  start_time                datetime,
  actual_fpp                float,
  projected_fpp             float,
  projected_fpp_mod         float,
  over_under                float,
  point_spread              float,
  unique_key                varchar(255),
  pred_fpp_allowed_qb_avg_range TEXT,
  pred_fpp_allowed_wr_avg_range TEXT,
  hist_fpp_avg_range        TEXT,
  hist_pass_attempts_percent_max_avg_range TEXT,
  hist_rec_targets_avg_range TEXT,
  hist_rec_targets_percent_max_avg_range TEXT,
  hist_rush_attempts_percent_max_avg_range TEXT,
  last_update               datetime not null,
  constraint uq_stats_nfl_projection_unique_key unique (unique_key),
  constraint pk_stats_nfl_projection primary key (id))
;

create table stats_nfl_projection_defense (
  id                        integer auto_increment not null,
  athlete_id                integer,
  sport_event_id            integer,
  season                    integer,
  week                      integer,
  start_time                datetime,
  team_name                 varchar(255),
  opponent_team_name        varchar(255),
  is_home                   tinyint(1) default 0,
  hist_fpp_avg_range        TEXT,
  hist_opponent_offense_fpp_avg TEXT,
  actual_fpp                float,
  projected_fpp             float,
  projected_fpp_mod         float,
  last_update               datetime not null,
  constraint pk_stats_nfl_projection_defense primary key (id))
;

create table stats_projection (
  id                        integer auto_increment not null,
  athlete_sport_event_info_id integer,
  projection                double,
  constraint pk_stats_projection primary key (id))
;

create table team (
  id                        integer auto_increment not null,
  stat_provider_id          integer not null,
  league_id                 integer,
  location                  varchar(255) not null,
  name                      varchar(255) not null,
  abbreviation              varchar(255) not null,
  constraint uq_team_stat_provider_id unique (stat_provider_id),
  constraint pk_team primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  user_name                 varchar(255),
  provider_id               varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  version                   datetime not null,
  constraint uq_user_email unique (email),
  constraint uq_user_user_name unique (user_name),
  constraint pk_user primary key (id))
;

create table user_action_transaction (
  id                        bigint auto_increment not null,
  start_timestamp           datetime,
  type                      integer,
  data                      varchar(255),
  constraint ck_user_action_transaction_type check (type in (0)),
  constraint pk_user_action_transaction primary key (id))
;

create table user_bonus (
  id                        integer auto_increment not null,
  user_wallet_id            integer not null,
  created_date              datetime,
  user_bonus_type_id        integer,
  amount                    bigint,
  constraint pk_user_bonus primary key (id))
;

create table user_bonus_type (
  id                        integer auto_increment not null,
  name                      varchar(255),
  parameters                varchar(255),
  constraint pk_user_bonus_type primary key (id))
;

create table user_profile (
  id                        integer auto_increment not null,
  user_id                   bigint,
  name                      varchar(255),
  address1                  varchar(255),
  address2                  varchar(255),
  city                      varchar(255),
  state_province            varchar(2),
  postal_code               varchar(255),
  country                   varchar(2),
  active                    tinyint(1) default 0,
  cams_token_id             varchar(255),
  cc_type                   integer,
  cc_number                 varchar(255),
  cc_exp_month              integer,
  cc_exp_year               integer,
  constraint ck_user_profile_state_province check (state_province in ('MA','NU','ID','NT','MI','NS','MD','IA','ME','BC','TX','DC','DE','HI','SK','NC','ND','AR','NJ','VT','NE','RI','AZ','NH','ON','IL','IN','MS','UT','MN','MO','AL','VA','PE','MT','AK','SC','SD','WI','OK','CA','OH','GA','FL','YT','NM','NY','MB','QC','NV','WA','LA','CT','PA','WY','TN','OR','NL','WV','CO','NB','KY','KS')),
  constraint ck_user_profile_country check (country in ('US','CA')),
  constraint ck_user_profile_cc_type check (cc_type in (0,1,2,3,4,5)),
  constraint uq_user_profile_1 unique (user_id,name),
  constraint pk_user_profile primary key (id))
;

create table user_role (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_user_role primary key (id))
;

create table user_session (
  id                        varchar(255) not null,
  user_id                   bigint,
  expiration_date           datetime,
  last_used                 datetime,
  creation_date             datetime,
  constraint pk_user_session primary key (id))
;

create table user_wallet (
  id                        integer auto_increment not null,
  user_id                   bigint,
  usd_currency              bigint,
  loyalty_points            bigint,
  constraint uq_user_wallet_1 unique (user_id),
  constraint pk_user_wallet primary key (id))
;

create table wallet_transaction (
  id                        bigint auto_increment not null,
  type                      varchar(1),
  amount                    integer,
  ts_begin                  datetime,
  ts_cams_response          datetime,
  cams_response_text        TEXT,
  wallet_transaction_result_code integer,
  ts_amount_applied         datetime,
  constraint ck_wallet_transaction_type check (type in ('D','W')),
  constraint pk_wallet_transaction primary key (id))
;

create table wallet_transaction_result (
  code                      integer auto_increment not null,
  description               varchar(255),
  constraint pk_wallet_transaction_result primary key (code))
;


create table athlete_x_position (
  athlete_id                     integer not null,
  position_id                    integer not null,
  constraint pk_athlete_x_position primary key (athlete_id, position_id))
;

create table avblineup_x_athlete_sport_event_info (
  avb_lineup_id                  integer not null,
  athlete_sport_event_info_id    integer not null,
  constraint pk_avblineup_x_athlete_sport_event_info primary key (avb_lineup_id, athlete_sport_event_info_id))
;

create table sport_event_x_team (
  sport_event_id                 integer not null,
  team_id                        integer not null,
  constraint pk_sport_event_x_team primary key (sport_event_id, team_id))
;

create table sport_event_grouping_x_sport_event (
  sport_event_grouping_id        integer not null,
  sport_event_id                 integer not null,
  constraint pk_sport_event_grouping_x_sport_event primary key (sport_event_grouping_id, sport_event_id))
;

create table user_x_role (
  user_id                        bigint not null,
  role_id                        integer not null,
  constraint pk_user_x_role primary key (user_id, role_id))
;
alter table athlete add constraint fk_athlete_team_1 foreign key (team_id) references team (id) on delete restrict on update restrict;
create index ix_athlete_team_1 on athlete (team_id);
alter table athlete_salary add constraint fk_athlete_salary_athlete_2 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;
create index ix_athlete_salary_athlete_2 on athlete_salary (athlete_id);
alter table athlete_salary add constraint fk_athlete_salary_sportEventGrouping_3 foreign key (sport_event_grouping_id) references sport_event_grouping (id) on delete restrict on update restrict;
create index ix_athlete_salary_sportEventGrouping_3 on athlete_salary (sport_event_grouping_id);
alter table athlete_sport_event_info add constraint fk_athlete_sport_event_info_sportEvent_4 foreign key (sport_event_id) references sport_event (id) on delete restrict on update restrict;
create index ix_athlete_sport_event_info_sportEvent_4 on athlete_sport_event_info (sport_event_id);
alter table athlete_sport_event_info add constraint fk_athlete_sport_event_info_athlete_5 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;
create index ix_athlete_sport_event_info_athlete_5 on athlete_sport_event_info (athlete_id);
alter table avb_lineup add constraint fk_avb_lineup_avb_matchup_6 foreign key (avb_matchup_id) references avb_matchup (id) on delete restrict on update restrict;
create index ix_avb_lineup_avb_matchup_6 on avb_lineup (avb_matchup_id);
alter table contest add constraint fk_contest_contestType_7 foreign key (contest_type_id) references contest_type (id) on delete restrict on update restrict;
create index ix_contest_contestType_7 on contest (contest_type_id);
alter table contest add constraint fk_contest_league_8 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_contest_league_8 on contest (league_id);
alter table contest add constraint fk_contest_sportEventGrouping_9 foreign key (sport_event_grouping_id) references sport_event_grouping (id) on delete restrict on update restrict;
create index ix_contest_sportEventGrouping_9 on contest (sport_event_grouping_id);
alter table contest add constraint fk_contest_createdFrom_10 foreign key (created_from_id) references contest_template (id) on delete restrict on update restrict;
create index ix_contest_createdFrom_10 on contest (created_from_id);
alter table contest add constraint fk_contest_contestState_11 foreign key (contest_state_id) references contest_state (id) on delete restrict on update restrict;
create index ix_contest_contestState_11 on contest (contest_state_id);
alter table contest_entry_fee add constraint fk_contest_entry_fee_league_12 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_contest_entry_fee_league_12 on contest_entry_fee (league_id);
alter table contest_grouping add constraint fk_contest_grouping_league_13 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_contest_grouping_league_13 on contest_grouping (league_id);
alter table contest_number_of_users add constraint fk_contest_number_of_users_league_14 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_contest_number_of_users_league_14 on contest_number_of_users (league_id);
alter table contest_payout add constraint fk_contest_payout_contest_15 foreign key (contest_id) references contest (id) on delete restrict on update restrict;
create index ix_contest_payout_contest_15 on contest_payout (contest_id);
alter table contest_results add constraint fk_contest_results_user_16 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_contest_results_user_16 on contest_results (user_id);
alter table contest_results add constraint fk_contest_results_contest_17 foreign key (contest_id) references contest (id) on delete restrict on update restrict;
create index ix_contest_results_contest_17 on contest_results (contest_id);
alter table contest_results add constraint fk_contest_results_entry_18 foreign key (entry_id) references entry (id) on delete restrict on update restrict;
create index ix_contest_results_entry_18 on contest_results (entry_id);
alter table contest_salary add constraint fk_contest_salary_league_19 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_contest_salary_league_19 on contest_salary (league_id);
alter table contest_suggestion add constraint fk_contest_suggestion_contestType_20 foreign key (contest_type_id) references contest_type (id) on delete restrict on update restrict;
create index ix_contest_suggestion_contestType_20 on contest_suggestion (contest_type_id);
alter table contest_suggestion add constraint fk_contest_suggestion_suggestionContestType_21 foreign key (suggestion_contest_type_id) references contest_type (id) on delete restrict on update restrict;
create index ix_contest_suggestion_suggestionContestType_21 on contest_suggestion (suggestion_contest_type_id);
alter table contest_template add constraint fk_contest_template_contestType_22 foreign key (contest_type_id) references contest_type (id) on delete restrict on update restrict;
create index ix_contest_template_contestType_22 on contest_template (contest_type_id);
alter table contest_template_payout add constraint fk_contest_template_payout_contest_template_23 foreign key (contest_template_id) references contest_template (id) on delete restrict on update restrict;
create index ix_contest_template_payout_contest_template_23 on contest_template_payout (contest_template_id);
alter table entry add constraint fk_entry_user_24 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_entry_user_24 on entry (user_id);
alter table entry add constraint fk_entry_contest_25 foreign key (contest_id) references contest (id) on delete restrict on update restrict;
create index ix_entry_contest_25 on entry (contest_id);
alter table entry add constraint fk_entry_lineup_26 foreign key (lineup_id) references lineup (id) on delete restrict on update restrict;
create index ix_entry_lineup_26 on entry (lineup_id);
alter table league add constraint fk_league_sport_27 foreign key (sport_id) references sport (id) on delete restrict on update restrict;
create index ix_league_sport_27 on league (sport_id);
alter table lineup add constraint fk_lineup_user_28 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_lineup_user_28 on lineup (user_id);
alter table lineup add constraint fk_lineup_league_29 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_lineup_league_29 on lineup (league_id);
alter table lineup add constraint fk_lineup_sportEventGrouping_30 foreign key (sport_event_grouping_id) references sport_event_grouping (id) on delete restrict on update restrict;
create index ix_lineup_sportEventGrouping_30 on lineup (sport_event_grouping_id);
alter table lineup_spot add constraint fk_lineup_spot_lineup_31 foreign key (lineup_id) references lineup (id) on delete restrict on update restrict;
create index ix_lineup_spot_lineup_31 on lineup_spot (lineup_id);
alter table lineup_spot add constraint fk_lineup_spot_athlete_32 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;
create index ix_lineup_spot_athlete_32 on lineup_spot (athlete_id);
alter table lineup_spot add constraint fk_lineup_spot_position_33 foreign key (position_id) references position (id) on delete restrict on update restrict;
create index ix_lineup_spot_position_33 on lineup_spot (position_id);
alter table lineup_spot add constraint fk_lineup_spot_athleteSportEventInfo_34 foreign key (athlete_sport_event_info_id) references athlete_sport_event_info (id) on delete restrict on update restrict;
create index ix_lineup_spot_athleteSportEventInfo_34 on lineup_spot (athlete_sport_event_info_id);
alter table lineup_template add constraint fk_lineup_template_league_35 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_lineup_template_league_35 on lineup_template (league_id);
alter table lineup_template add constraint fk_lineup_template_position_36 foreign key (position_id) references position (id) on delete restrict on update restrict;
create index ix_lineup_template_position_36 on lineup_template (position_id);
alter table position add constraint fk_position_sport_37 foreign key (sport_id) references sport (id) on delete restrict on update restrict;
create index ix_position_sport_37 on position (sport_id);
alter table scoring_rule add constraint fk_scoring_rule_league_38 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_scoring_rule_league_38 on scoring_rule (league_id);
alter table sport_event add constraint fk_sport_event_league_39 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_sport_event_league_39 on sport_event (league_id);
alter table sport_event_date_range_selector add constraint fk_sport_event_date_range_selector_sport_event_grouping_type_40 foreign key (sport_event_grouping_type_id) references sport_event_grouping_type (id) on delete restrict on update restrict;
create index ix_sport_event_date_range_selector_sport_event_grouping_type_40 on sport_event_date_range_selector (sport_event_grouping_type_id);
alter table sport_event_grouping add constraint fk_sport_event_grouping_sportEventGroupingType_41 foreign key (sport_event_grouping_type_id) references sport_event_grouping_type (id) on delete restrict on update restrict;
create index ix_sport_event_grouping_sportEventGroupingType_41 on sport_event_grouping (sport_event_grouping_type_id);
alter table sport_event_grouping_type add constraint fk_sport_event_grouping_type_league_42 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_sport_event_grouping_type_league_42 on sport_event_grouping_type (league_id);
alter table stats_live_feed_data add constraint fk_stats_live_feed_data_sportEvent_43 foreign key (sport_event_id) references sport_event (id) on delete restrict on update restrict;
create index ix_stats_live_feed_data_sportEvent_43 on stats_live_feed_data (sport_event_id);
alter table stats_nfl_athlete_by_event add constraint fk_stats_nfl_athlete_by_event_athlete_44 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;
create index ix_stats_nfl_athlete_by_event_athlete_44 on stats_nfl_athlete_by_event (athlete_id);
alter table stats_nfl_athlete_by_event add constraint fk_stats_nfl_athlete_by_event_sportEvent_45 foreign key (sport_event_id) references sport_event (id) on delete restrict on update restrict;
create index ix_stats_nfl_athlete_by_event_sportEvent_45 on stats_nfl_athlete_by_event (sport_event_id);
alter table stats_nfl_athlete_by_event add constraint fk_stats_nfl_athlete_by_event_team_46 foreign key (team_id) references team (id) on delete restrict on update restrict;
create index ix_stats_nfl_athlete_by_event_team_46 on stats_nfl_athlete_by_event (team_id);
alter table stats_nfl_defense_by_event add constraint fk_stats_nfl_defense_by_event_athlete_47 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;
create index ix_stats_nfl_defense_by_event_athlete_47 on stats_nfl_defense_by_event (athlete_id);
alter table stats_nfl_defense_by_event add constraint fk_stats_nfl_defense_by_event_sportEvent_48 foreign key (sport_event_id) references sport_event (id) on delete restrict on update restrict;
create index ix_stats_nfl_defense_by_event_sportEvent_48 on stats_nfl_defense_by_event (sport_event_id);
alter table stats_nfl_defense_by_event add constraint fk_stats_nfl_defense_by_event_team_49 foreign key (team_id) references team (id) on delete restrict on update restrict;
create index ix_stats_nfl_defense_by_event_team_49 on stats_nfl_defense_by_event (team_id);
alter table stats_nfl_defense_by_event add constraint fk_stats_nfl_defense_by_event_opponent_50 foreign key (opponent_id) references team (id) on delete restrict on update restrict;
create index ix_stats_nfl_defense_by_event_opponent_50 on stats_nfl_defense_by_event (opponent_id);
alter table stats_nfl_projection add constraint fk_stats_nfl_projection_athlete_51 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;
create index ix_stats_nfl_projection_athlete_51 on stats_nfl_projection (athlete_id);
alter table stats_nfl_projection add constraint fk_stats_nfl_projection_sportEvent_52 foreign key (sport_event_id) references sport_event (id) on delete restrict on update restrict;
create index ix_stats_nfl_projection_sportEvent_52 on stats_nfl_projection (sport_event_id);
alter table stats_nfl_projection_defense add constraint fk_stats_nfl_projection_defense_athlete_53 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;
create index ix_stats_nfl_projection_defense_athlete_53 on stats_nfl_projection_defense (athlete_id);
alter table stats_nfl_projection_defense add constraint fk_stats_nfl_projection_defense_sportEvent_54 foreign key (sport_event_id) references sport_event (id) on delete restrict on update restrict;
create index ix_stats_nfl_projection_defense_sportEvent_54 on stats_nfl_projection_defense (sport_event_id);
alter table stats_projection add constraint fk_stats_projection_athleteSportEventInfo_55 foreign key (athlete_sport_event_info_id) references athlete_sport_event_info (id) on delete restrict on update restrict;
create index ix_stats_projection_athleteSportEventInfo_55 on stats_projection (athlete_sport_event_info_id);
alter table team add constraint fk_team_league_56 foreign key (league_id) references league (id) on delete restrict on update restrict;
create index ix_team_league_56 on team (league_id);
alter table user_bonus add constraint fk_user_bonus_user_wallet_57 foreign key (user_wallet_id) references user_wallet (id) on delete restrict on update restrict;
create index ix_user_bonus_user_wallet_57 on user_bonus (user_wallet_id);
alter table user_bonus add constraint fk_user_bonus_userBonusType_58 foreign key (user_bonus_type_id) references user_bonus_type (id) on delete restrict on update restrict;
create index ix_user_bonus_userBonusType_58 on user_bonus (user_bonus_type_id);
alter table user_profile add constraint fk_user_profile_user_59 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_profile_user_59 on user_profile (user_id);
alter table user_session add constraint fk_user_session_user_60 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_session_user_60 on user_session (user_id);
alter table user_wallet add constraint fk_user_wallet_user_61 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_wallet_user_61 on user_wallet (user_id);
alter table wallet_transaction add constraint fk_wallet_transaction_walletTransactionResult_62 foreign key (wallet_transaction_result_code) references wallet_transaction_result (code) on delete restrict on update restrict;
create index ix_wallet_transaction_walletTransactionResult_62 on wallet_transaction (wallet_transaction_result_code);



alter table athlete_x_position add constraint fk_athlete_x_position_athlete_01 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;

alter table athlete_x_position add constraint fk_athlete_x_position_position_02 foreign key (position_id) references position (id) on delete restrict on update restrict;

alter table avblineup_x_athlete_sport_event_info add constraint fk_avblineup_x_athlete_sport_event_info_avb_lineup_01 foreign key (avb_lineup_id) references avb_lineup (id) on delete restrict on update restrict;

alter table avblineup_x_athlete_sport_event_info add constraint fk_avblineup_x_athlete_sport_event_info_athlete_sport_event_i_02 foreign key (athlete_sport_event_info_id) references athlete_sport_event_info (id) on delete restrict on update restrict;

alter table sport_event_x_team add constraint fk_sport_event_x_team_sport_event_01 foreign key (sport_event_id) references sport_event (id) on delete restrict on update restrict;

alter table sport_event_x_team add constraint fk_sport_event_x_team_team_02 foreign key (team_id) references team (id) on delete restrict on update restrict;

alter table sport_event_grouping_x_sport_event add constraint fk_sport_event_grouping_x_sport_event_sport_event_grouping_01 foreign key (sport_event_grouping_id) references sport_event_grouping (id) on delete restrict on update restrict;

alter table sport_event_grouping_x_sport_event add constraint fk_sport_event_grouping_x_sport_event_sport_event_02 foreign key (sport_event_id) references sport_event (id) on delete restrict on update restrict;

alter table user_x_role add constraint fk_user_x_role_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_x_role add constraint fk_user_x_role_user_role_02 foreign key (role_id) references user_role (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table athlete;

drop table athlete_x_position;

drop table athlete_salary;

drop table athlete_sport_event_info;

drop table avb_lineup;

drop table avblineup_x_athlete_sport_event_info;

drop table avb_matchup;

drop table contest;

drop table contest_entry_fee;

drop table contest_grouping;

drop table contest_number_of_users;

drop table contest_payout;

drop table contest_results;

drop table contest_salary;

drop table contest_state;

drop table contest_suggestion;

drop table contest_template;

drop table contest_template_payout;

drop table contest_type;

drop table entry;

drop table league;

drop table lineup;

drop table lineup_spot;

drop table lineup_template;

drop table position;

drop table scoring_rule;

drop table secure_social_token;

drop table sport;

drop table sport_event;

drop table sport_event_x_team;

drop table sport_event_date_range_selector;

drop table sport_event_grouping;

drop table sport_event_grouping_x_sport_event;

drop table sport_event_grouping_type;

drop table stats_athlete_by_season_raw;

drop table stats_live_feed_data;

drop table stats_mlb_batting;

drop table stats_mlb_fielding;

drop table stats_mlb_pitching;

drop table stats_nfl_athlete_by_event;

drop table stats_nfl_defense_by_event;

drop table stats_nfl_game_odds;

drop table stats_nfl_projection;

drop table stats_nfl_projection_defense;

drop table stats_projection;

drop table team;

drop table user;

drop table user_x_role;

drop table user_action_transaction;

drop table user_bonus;

drop table user_bonus_type;

drop table user_profile;

drop table user_role;

drop table user_session;

drop table user_wallet;

drop table wallet_transaction;

drop table wallet_transaction_result;

SET FOREIGN_KEY_CHECKS=1;

