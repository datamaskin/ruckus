# --- !Ups

alter table user add column verified datetime;

# --- !Downs

alter table user drop column verified;