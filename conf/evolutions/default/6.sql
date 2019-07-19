# --- !Ups

alter table stats_nfl_athlete_by_event add column pred_fpp_allowed_qb_avg_range TEXT;
alter table stats_nfl_athlete_by_event add column pred_fpp_allowed_wr_avg_range TEXT;

# --- !Downs


alter table stats_nfl_athlete_by_event drop column pred_fpp_allowed_qb_avg_range;
alter table stats_nfl_athlete_by_event drop column pred_fpp_allowed_qb_avg_range;
