# --- !Ups

create index ix_stats_live_feed_data_data_hash on stats_live_feed_data(data_hash);

# --- !Downs

drop index ix_stats_live_feed_data_data_hash;
