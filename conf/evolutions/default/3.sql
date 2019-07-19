# --- !Ups

alter table wallet_transaction add column user_id bigint;

alter table wallet_transaction add constraint fk_wallet_transaction_user_62 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_wallet_transaction_user_62 on wallet_transaction (user_id);

# --- !Downs

alter table wallet_transaction drop column user_id;