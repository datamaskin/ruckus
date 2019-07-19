# --- !Ups

alter table user_action_transaction rename to user_action;
alter table user_action modify column type varchar(255) not null;
alter table user_action add column user_id bigint;
alter table user_action add constraint fk_user_action_user_24 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_action_user_24 on user_action (user_id);


# --- !Downs

alter table user_action rename to user_action_transaction;
alter table user_action drop column user_id;
