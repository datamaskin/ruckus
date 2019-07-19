# --- !Ups
DROP TABLE wallet_transaction;
DROP TABLE wallet_transaction_result;

CREATE TABLE user_wallet_txn (
  id                INTEGER  NOT NULL AUTO_INCREMENT,
  uuid              CHAR(36) NOT NULL,
  time_stamp        DATETIME NOT NULL,
  user_wallet_id    INTEGER  NOT NULL,
  cams_detail_id    INTEGER,
  paypal_detail_id  INTEGER,
  bitcoin_detail_id INTEGER,
  victiv_detail_id  INTEGER,
  balance_after     BIGINT   NOT NULL,
  amount            BIGINT   NOT NULL,
  CONSTRAINT uq_user_wallet_txn$uuid UNIQUE (uuid),
  CONSTRAINT pk_user_wallet_txn PRIMARY KEY (id)
);

CREATE TABLE cams_txn_detail (
  id               INTEGER      NOT NULL AUTO_INCREMENT,
  uuid             CHAR(36)     NOT NULL,
  time_stamp       DATETIME     NOT NULL,
  description      VARCHAR(255) NOT NULL,
  cams_result_code INTEGER      NOT NULL,
  cams_authcode    VARCHAR(255),
  CONSTRAINT uq_cams_txn_detail$uuid UNIQUE (uuid),
  CONSTRAINT pk_cams_txn_detail PRIMARY KEY (id)
);

CREATE TABLE paypal_txn_detail (
  id          INTEGER      NOT NULL AUTO_INCREMENT,
  uuid        CHAR(36)     NOT NULL,
  time_stamp  DATETIME     NOT NULL,
  description VARCHAR(255) NOT NULL,
  CONSTRAINT uq_paypal_txn_detail$uuid UNIQUE (uuid),
  CONSTRAINT pk_paypal_txn_detail PRIMARY KEY (id)
);

CREATE TABLE bitcoin_txn_detail (
  id          INTEGER      NOT NULL AUTO_INCREMENT,
  uuid        CHAR(36)     NOT NULL,
  time_stamp  DATETIME     NOT NULL,
  description VARCHAR(255) NOT NULL,
  CONSTRAINT uq_bitcoin_txn_detail$uuid UNIQUE (uuid),
  CONSTRAINT pk_bitcoin_txn_detail PRIMARY KEY (id)
);

CREATE TABLE victiv_txn_detail (
  id              INTEGER      NOT NULL AUTO_INCREMENT,
  uuid            CHAR(36)     NOT NULL,
  time_stamp      DATETIME     NOT NULL,
  description     VARCHAR(255) NOT NULL,
  victiv_txn_type VARCHAR(30)  NOT NULL,
  contest_id      INTEGER,
  CONSTRAINT uq_victiv_txn_detail$user_wallet_txn$uuid UNIQUE (uuid),
  CONSTRAINT pk_victiv_txn_detail PRIMARY KEY (id)
);

CREATE TABLE bonus_wallet (
  id              INTEGER     NOT NULL AUTO_INCREMENT,
  uuid            CHAR(36)    NOT NULL,
  user_id         BIGINT      NOT NULL,
  original_amount BIGINT      NOT NULL,
  granted_on      DATETIME    NOT NULL,
  cleared_amount  BIGINT      NOT NULL,
  payout_plan     VARCHAR(30) NOT NULL,
  award_type      VARCHAR(30) NOT NULL,
  CONSTRAINT uq_bonus_wallet$uuid UNIQUE (uuid),
  CONSTRAINT pk_bonus_wallet PRIMARY KEY (id)
);

CREATE TABLE bonus_wallet_txn (
  id              INTEGER  NOT NULL AUTO_INCREMENT,
  uuid            CHAR(36) NOT NULL,
  bonus_wallet_id INTEGER  NOT NULL,
  clear_date      DATETIME NOT NULL,
  amount          BIGINT   NOT NULL,
  CONSTRAINT uq_bonus_wallet_txn$uuid UNIQUE (uuid),
  CONSTRAINT pk_victiv_txn_detail PRIMARY KEY (id)
);

ALTER TABLE user_wallet_txn ADD CONSTRAINT fk_user_wallet_txn$user_wallet_id FOREIGN KEY (user_wallet_id) REFERENCES user_wallet (id);
ALTER TABLE user_wallet_txn ADD CONSTRAINT fk_user_wallet_txn$cams_detail_id FOREIGN KEY (cams_detail_id) REFERENCES cams_txn_detail (id);
ALTER TABLE user_wallet_txn ADD CONSTRAINT fk_user_wallet_txn$paypal_detail_id FOREIGN KEY (paypal_detail_id) REFERENCES paypal_txn_detail (id);
ALTER TABLE user_wallet_txn ADD CONSTRAINT fk_user_wallet_txn$bitcoin_detail_id FOREIGN KEY (bitcoin_detail_id) REFERENCES bitcoin_txn_detail (id);
ALTER TABLE user_wallet_txn ADD CONSTRAINT fk_user_wallet_txn$victiv_detail_id FOREIGN KEY (victiv_detail_id) REFERENCES victiv_txn_detail (id);

ALTER TABLE victiv_txn_detail ADD CONSTRAINT fk_victiv_txn_detail$contest_id FOREIGN KEY (contest_id) REFERENCES contest (id);

ALTER TABLE bonus_wallet ADD CONSTRAINT fk_bonus_wallet$user_id FOREIGN KEY (user_id) REFERENCES USER (id);

ALTER TABLE bonus_wallet_txn ADD CONSTRAINT fk_bonus_wallet_txn$bonus_wallet_id FOREIGN KEY (bonus_wallet_id) REFERENCES bonus_wallet (id);

# --- !Downs
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE user_wallet_txn;
DROP TABLE cams_txn_detail;
DROP TABLE paypal_txn_detail;
DROP TABLE bitcoin_txn_detail;
DROP TABLE victiv_txn_detail;
DROP TABLE bonus_wallet;
DROP TABLE bonus_wallet_txn;

CREATE TABLE wallet_transaction (
  id                             BIGINT AUTO_INCREMENT NOT NULL,
  type                           VARCHAR(1),
  amount                         INTEGER,
  ts_begin                       DATETIME,
  ts_cams_response               DATETIME,
  cams_response_text             TEXT,
  wallet_transaction_result_code INTEGER,
  ts_amount_applied              DATETIME,
  CONSTRAINT ck_wallet_transaction_type CHECK (type IN ('D', 'W')),
  CONSTRAINT pk_wallet_transaction PRIMARY KEY (id)
);

CREATE TABLE wallet_transaction_result (
  code        INTEGER AUTO_INCREMENT NOT NULL,
  description VARCHAR(255),
  CONSTRAINT pk_wallet_transaction_result PRIMARY KEY (code)
);

SET FOREIGN_KEY_CHECKS = 1;