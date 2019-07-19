# --- !Ups

create index ix_stats_nfl_projection_start_time_22 on stats_nfl_projection (start_time);
create index ix_stats_nfl_projection_last_update_22 on stats_nfl_projection (last_update);

create index ix_stats_nfl_athlete_by_event_position_22 on stats_nfl_athlete_by_event (position);
create index ix_stats_nfl_athlete_by_event_opponent_id_22 on stats_nfl_athlete_by_event (opponent_id);
create index ix_stats_nfl_athlete_by_event_season_22 on stats_nfl_athlete_by_event (season);
create index ix_stats_nfl_athlete_by_event_last_update_22 on stats_nfl_athlete_by_event (last_update);

create index ix_stats_athlete_by_season_season_id_22 on stats_athlete_by_season_raw (season);
create index ix_stats_athlete_by_season_last_update_22 on stats_athlete_by_season_raw (last_update);

create index ix_stats_nfl_defense_by_event_start_time on stats_nfl_defense_by_event(start_time);

create index ix_sport_event_start_time_22 on sport_event (start_time);

create index ix_stats_nfl_athlete_by_event_start_time_22 on stats_nfl_athlete_by_event (start_time);
create index ix_stats_nfl_athlete_by_event_week_22 on stats_nfl_athlete_by_event (week);
create index ix_stats_nfl_athlete_by_event_type_id_22 on stats_nfl_athlete_by_event (event_type_id);
create index ix_stats_nfl_projection_stats_athlete_id_22 on stats_nfl_projection (stats_athlete_id);

create unique index ix_stats_nfl_defense_by_event_athlete_sport_event on stats_nfl_defense_by_event(athlete_id, sport_event_id);
create unique index ix_stats_nfl_projection_defense_athlete_sport_event on stats_nfl_projection_defense(athlete_id, sport_event_id);

create index ix_week_season_position_eventtype_22 on stats_nfl_athlete_by_event(week, season, position, event_type_id);
create index ix_start_time_event_type_league_22 on sport_event(start_time, event_type_id, league_id);

create index ix_sportevent_team_position_22 on stats_nfl_athlete_by_event(sport_event_id, team_id, position);
create index ix_athlete_id_start_time_22 on stats_nfl_athlete_by_event(athlete_id, start_time);

create index ix_stats_live_feed_data_data_hash on stats_live_feed_data(data_hash);

# --- !Downs
