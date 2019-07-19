# --- !Ups

ALTER TABLE stats_nfl_depth_chart ADD COLUMN depth_position varchar(255) DEFAULT NULL;
ALTER TABLE stats_nfl_projection ADD COLUMN depth_position varchar(255) DEFAULT NULL;

# --- !Downs

alter table stats_nfl_depth_chart drop column depth_position;
alter table stats_nfl_projection drop column depth_position;
