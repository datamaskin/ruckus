# --- !Ups

alter table stats_nfl_athlete_by_event add column passing_rating_per_game_range TEXT;
alter table stats_nfl_athlete_by_event add column passing_rating_avg_range TEXT;
alter table stats_nfl_projection add column hist_passing_rating_avg_range TEXT;
alter table stats_nfl_projection add column hist_rec_yards_avg_range TEXT;

create index ix_stats_nfl_athlete_by_event_start_time_22 on stats_nfl_athlete_by_event (start_time);
create index ix_stats_nfl_athlete_by_event_week_22 on stats_nfl_athlete_by_event (week);
create index ix_stats_nfl_athlete_by_event_type_id_22 on stats_nfl_athlete_by_event (event_type_id);
create index ix_stats_nfl_projection_stats_athlete_id_22 on stats_nfl_projection (stats_athlete_id);

# --- !Downs


alter table stats_nfl_athlete_by_event drop column passing_rating_per_game_range;
alter table stats_nfl_athlete_by_event drop column passing_rating_avg_range;
alter table stats_nfl_projection drop column hist_passing_rating_avg_range;
alter table stats_nfl_projection drop column hist_rec_yards_avg_range;