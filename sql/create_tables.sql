drop table if exists roster_athlete;
drop table if exists contest_roster;
drop table if exists athlete;
drop table if exists roster;
drop table if exists contest;
drop table if exists user_attributes;
drop table if exists user;

create table user_attributes (
    id int auto_increment primary key,
    um_user_id int not null,
    name varchar(100) not null,
    email varchar(100) not null,
    foreign key (um_user_id) references um_user(um_id)
) engine=InnoDB;

create table contest (
    id int auto_increment primary key,
    creator_id int not null,
    type varchar(50) not null,
    sport varchar(50) not null,
    size int not null default 0,
    capacity int not null,
    buyin int not null,
    is_public boolean not null,
    payout text not null,
    foreign key (creator_id) references um_user(um_id)
) engine=InnoDB;

create table roster (
    id int auto_increment primary key,
    user_id int not null,
    points double not null,
    sport varchar(50) not null,
    foreign key (user_id) references um_user(um_id)
) engine=InnoDB;

create table athlete (
    id int auto_increment primary key,
    stat_provider_id int not null,      /* for stats, inc. */
    name varchar(100) not null,
    stat_line varchar(500) not null,
    points double not null,
    game_date datetime not null,
    salary int not null,
    status varchar(50)
) engine=InnoDB;

create table contest_roster (
    id int auto_increment primary key,
    roster_id int not null,
    contest_id int not null,
    foreign key (roster_id) references roster(id),
    foreign key (contest_id) references contest(id)
) engine=InnoDB;

create table roster_athlete (
    id int auto_increment primary key,
    roster_id int not null,
    athlete_id int not null,
    foreign key (roster_id) references roster(id),
    foreign key (athlete_id) references athlete(id)
) engine=InnoDB;

create table security_config (
  environment               varchar(255) not null,
  issuer_id                 varchar(255),
  consumer_url              varchar(255),
  idp_url                   varchar(255),
  attribute_consuming_service_index varchar(255),
  keystore                  longblob,
  keystore_password         varchar(255),
  idp_certificate_alias     varchar(255),
  private_key_password      varchar(255),
  constraint pk_security_config primary key (environment)
) engine=InnoDB;
