# --- !Ups

alter table athlete add column depth integer default 0;
alter table stats_nfl_projection add column depth integer default 0;
alter table stats_nfl_projection add column event_type_id integer;

create table stats_nfl_depth_chart (
  id                        integer auto_increment not null,
  week                      integer not null default -1,
  season                    integer not null default -1,
  event_type_id             integer not null default -1,
  athlete_id                integer,
  team_id                   integer,
  depth                     integer not null default 0,

  constraint uq_stats_nfl_depth_chart unique (athlete_id, season, week, event_type_id),
  constraint pk_stats_nfl_depth_chart primary key (id))
;

alter table stats_nfl_depth_chart add constraint fk_stats_nfl_depth_chart_athlete_id_22 foreign key (athlete_id) references athlete (id) on delete restrict on update restrict;
alter table stats_nfl_depth_chart add constraint fk_stats_nfl_depth_chart_team_id_22 foreign key (team_id) references team (id) on delete restrict on update restrict;

# --- !Downs

alter table athlete drop column depth;
alter table stats_nfl_projection drop column depth;
alter table stats_nfl_projection drop column event_type_id;

drop table stats_nfl_depth_chart;
