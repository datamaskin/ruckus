# --- !Ups

CREATE TABLE affiliate_plan (
  id      INTEGER      NOT NULL AUTO_INCREMENT,
  uuid    CHAR(36)     NOT NULL,
  name    VARCHAR(255) NOT NULL,
  start   DATETIME     NOT NULL,
  expires DATETIME     NOT NULL,
  CONSTRAINT uq_affiliate_plan$uuid UNIQUE (uuid),
  CONSTRAINT uq_affiliate_plan$name UNIQUE (name),
  CONSTRAINT pk_affiliate_plan PRIMARY KEY (id)
);

CREATE TABLE affiliate_code (
  id      INTEGER  NOT NULL AUTO_INCREMENT,
  uuid    CHAR(36) NOT NULL,
  user_id BIGINT   NOT NULL,
  plan_id INTEGER  NOT NULL,
  CONSTRAINT uq_affiliate_code$uuid UNIQUE (uuid),
  CONSTRAINT pk_affiliate_code PRIMARY KEY (id)
);

ALTER TABLE affiliate_code ADD CONSTRAINT fk_affiliate_code$plan_id FOREIGN KEY (plan_id) REFERENCES affiliate_plan (id);
ALTER TABLE affiliate_code ADD CONSTRAINT fk_affiliate_code$user_id FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user ADD COLUMN affiliate_code_id INTEGER DEFAULT NULL;
ALTER TABLE user ADD CONSTRAINT fk_user$affiliate_code_id FOREIGN KEY (affiliate_code_id) REFERENCES affiliate_code (id);

# --- !Downs
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE affiliate_plan;
DROP TABLE affiliate_code;
ALTER TABLE user DROP FOREIGN KEY fk_user$affiliate_code_id;
ALTER TABLE USER DROP COLUMN affiliate_code_id;
SET FOREIGN_KEY_CHECKS = 1;


