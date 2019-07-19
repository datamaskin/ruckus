INSERT INTO `ruckus`.`contest_sport` (`name`, `active`) VALUES ('NFL', '0');
INSERT INTO `ruckus`.`contest_sport` (`name`, `active`) VALUES ('MLB', '1');
INSERT INTO `ruckus`.`contest_sport` (`name`, `active`) VALUES ('NBA', '1');
INSERT INTO `ruckus`.`contest_sport` (`name`, `active`) VALUES ('NHL', '1');
INSERT INTO `ruckus`.`contest_sport` (`name`, `active`) VALUES ('MLS', '0');

INSERT INTO `contest_type` VALUES (1,'H2H');
INSERT INTO `contest_type` VALUES (2,'GPP');
INSERT INTO `contest_type` VALUES (3,'50/50');

INSERT INTO `user` VALUES (1,'dan.maclean@ruckusgaming.com','dmaclean','Dan','MacLean','$2a$10$9BIUz1ZzFuCOgGsCAmHxnOeeqIz1I1xPCyEksNac2WFNxaFcyfBQC','2014-05-22 17:01:07');
INSERT INTO `user` VALUES (2,'walshms@gmail.com','mwalsh','Matt','Walsh','$2a$10$9BIUz1ZzFuCOgGsCAmHxnOeeqIz1I1xPCyEksNac2WFNxaFcyfBQC','2014-05-22 17:01:07');

INSERT INTO `ruckus`.`contest_athlete` (`stat_provider_id`, `name`, `points`, `game_date`, `salary`) VALUES ('502166', 'Matt Adams', '12', '2014-05-22 00:00:00', '5000');
INSERT INTO `ruckus`.`contest_athlete` (`stat_provider_id`, `name`, `points`, `game_date`, `salary`) VALUES ('298500', 'Peter Bourjos', '23', '2014-05-22 00:00:00', '2000');
INSERT INTO `ruckus`.`contest_athlete` (`stat_provider_id`, `name`, `points`, `game_date`, `salary`) VALUES ('501744', 'Matt Carpenter', '19', '2014-05-22 00:00:00', '10000');
INSERT INTO `ruckus`.`contest_athlete` (`stat_provider_id`, `name`, `points`, `game_date`, `salary`) VALUES ('8617', 'Randy Choate', '15', '2014-05-22 00:00:00', '7000');
INSERT INTO `ruckus`.`contest_athlete` (`stat_provider_id`, `name`, `points`, `game_date`, `salary`) VALUES ('327054', 'Allen Craig', '16', '2014-05-22 00:00:00', '8000');
INSERT INTO `ruckus`.`contest_athlete` (`stat_provider_id`, `name`, `points`, `game_date`, `salary`) VALUES ('389441', 'Tony Cruz', '11', '2014-05-22 00:00:00', '9000');
INSERT INTO `ruckus`.`contest_athlete` (`stat_provider_id`, `name`, `points`, `game_date`, `salary`) VALUES ('390375', 'Daniel Descalso', '20', '2014-05-22 00:00:00', '1000');

INSERT INTO `contest` (`type`,`sport`,`current_entries`,`capacity`,`is_public`,`payout`,`entry_fee`,`guaranteed`,`multi_entry`,`grouping`,`salary_cap`,`start_time`) 
VALUES ('H2H','MLB',0,1000,0,'100000',10,0,0,'ALL',50000,'2014-05-23 13:02:58');
INSERT INTO `contest` (`type`,`sport`,`current_entries`,`capacity`,`is_public`,`payout`,`entry_fee`,`guaranteed`,`multi_entry`,`grouping`,`salary_cap`,`start_time`) 
VALUES ('H2H','MLB',0,2000,0,'12000',10,0,0,'ALL',50000,'2014-05-23 13:02:58');
INSERT INTO `contest` (`type`,`sport`,`current_entries`,`capacity`,`is_public`,`payout`,`entry_fee`,`guaranteed`,`multi_entry`,`grouping`,`salary_cap`,`start_time`) 
VALUES ('GPP','MLB',0,3000,0,'14000',10,0,0,'ALL',50000,'2014-05-23 13:02:58');