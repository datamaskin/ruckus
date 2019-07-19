# --- !Ups

alter table contest add column reconciled_time datetime;


# --- !Downs


alter table contest drop column reconciled_time;
