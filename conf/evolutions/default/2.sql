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


# --- !Downs
