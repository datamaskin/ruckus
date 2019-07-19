# --- !Ups

ALTER TABLE contest add column display_name varchar(255) not null default "";

# --- !Downs

ALTER TABLE contest drop column display_name;
