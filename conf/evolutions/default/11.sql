# --- !Ups

alter table stats_nfl_projection add column participated_last_two tinyint(1) default 0;
alter table stats_nfl_projection add column pred_fpp_allowed_rb_avg_range TEXT;
alter table stats_nfl_projection add column pred_fpp_allowed_te_avg_range TEXT;
create index ix_sportevent_team_position_22 on stats_nfl_athlete_by_event(sport_event_id, team_id, position);
create index ix_athlete_id_start_time_22 on stats_nfl_athlete_by_event(athlete_id, start_time);


# --- !Downs

alter table stats_nfl_projection drop column participated_last_two;
alter table stats_nfl_projection drop column pred_fpp_allowed_rb_avg_range;
alter table stats_nfl_projection drop column pred_fpp_allowed_te_avg_range;
alter table stats_nfl_athlete_by_event drop index ix_sportevent_team_position_22;
alter table stats_nfl_athlete_by_event drop index ix_athlete_id_start_time_22;


