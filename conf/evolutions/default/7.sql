# --- !Ups
create unique index ix_stats_nfl_defense_by_event_athlete_sport_event on stats_nfl_defense_by_event(athlete_id, sport_event_id);
create unique index ix_stats_nfl_projection_defense_athlete_sport_event on stats_nfl_projection_defense(athlete_id, sport_event_id);

# --- !Downs
alter table stats_nfl_defense_by_event drop index ix_stats_nfl_defense_by_event_athlete_sport_event;
alter table stats_nfl_projection_defense drop index ix_stats_nfl_projection_defense_athlete_sport_event;
