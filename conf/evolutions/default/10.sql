# --- !Ups
create index ix_week_season_position_eventtype_22 on stats_nfl_athlete_by_event(week, season, position, event_type_id);
create index ix_start_time_event_type_league_22 on sport_event(start_time, event_type_id, league_id);

# --- !Downs
alter table stats_nfl_athlete_by_event drop index ix_week_season_position_eventtype_22;
alter table sport_event drop index ix_start_time_event_type_league_22;
