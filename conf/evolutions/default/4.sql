
# --- !Ups
alter table user_profile drop index uq_user_profile_1;
alter table user_profile drop column name;

# --- !Downs
alter table user_profile add column name varchar(255);
alter table user_profile add constraint uq_user_profile_1 unique (user_id,name);
