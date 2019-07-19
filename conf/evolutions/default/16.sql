# --- !Ups

ALTER TABLE stats_projection add column projection_mod double;

# --- !Downs

ALTER TABLE stats_projection drop column projection_mod;
