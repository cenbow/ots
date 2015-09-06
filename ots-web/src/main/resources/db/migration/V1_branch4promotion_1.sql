-- 1. 订单表
ALTER TABLE `b_otaorder` ADD COLUMN `activeid` bigint COMMENT '活动id';   

-- 2. 消息推送
alter table l_push_log  add column `url` varchar(255) NOT NULL COMMENT '消息url';


-- 3. 用户优惠券 
alter table u_ticket add column  `activityid` bigint(20) DEFAULT NULL COMMENT '活动id';
alter table u_ticket add column  `otaorderid` bigint(20) DEFAULT NULL COMMENT 'ota订单id';
alter table u_ticket add column  `promotiontime` datetime DEFAULT NULL COMMENT '优惠券使用时间';

-- 4. 券实例表
alter table b_promotion add column  `totalnum` bigint(20) DEFAULT NULL;
alter table b_promotion add column  `plannum` bigint(20) DEFAULT NULL;
alter table b_promotion add column  `protype` int DEFAULT NULL;
alter table b_promotion add column  `onlineprice` DECIMAL DEFAULT NULL;
alter table b_promotion add column  `offlineprice` DECIMAL DEFAULT NULL;

-- 5. 活动券规则定义表
CREATE TABLE `b_promotion_rule` (
  `Id` varchar(40) NOT NULL,
  `promotionId` varchar(40) DEFAULT NULL,
  `ruleCode` varchar(255) DEFAULT NULL COMMENT '规则代码',
  `ruleFormula` varchar(255) DEFAULT NULL COMMENT '规则表达式',
  PRIMARY KEY (`Id`)
);
insert into `b_promotion_rule`(`Id`,`promotionId`,`ruleCode`,`ruleFormula`) values ('1','18','10001','1');
insert into `b_promotion_rule`(`Id`,`promotionId`,`ruleCode`,`ruleFormula`) values ('2','19','10001','2');
insert into `b_promotion_rule`(`Id`,`promotionId`,`ruleCode`,`ruleFormula`) values ('3','20','10001','3');

CREATE TABLE `b_promotion_rule_code` (
  `Id` varchar(40) NOT NULL,
  `ruletype` varchar(10) DEFAULT NULL,
  `ruleCode` varchar(10) DEFAULT NULL COMMENT '规则代码',
  `ruleFormula` varchar(255) DEFAULT NULL COMMENT '规则表达式',
  `ruleDetail` varchar(255) DEFAULT NULL COMMENT '规则描述',
  PRIMARY KEY (`Id`)
);
insert into `b_promotion_rule_code`(`Id`,`ruletype`,`ruleCode`,`ruleFormula`,`ruleDetail`) values ('421abbcd-10a4-11e5-bcb8-525400bdfcfa','2','20001',null,'提前立免');
insert into `b_promotion_rule_code`(`Id`,`ruletype`,`ruleCode`,`ruleFormula`,`ruleDetail`) values ('feab9433-10a3-11e5-bcb8-525400bdfcfa','1','10001',null,'提前立减');

INSERT INTO `b_promotion` VALUES  
('18', '立减10', '2015-06-27 00:00:00', '2014-12-12 08:00:00', '2015-07-02 08:00:00', '7', 'T', '-1', '<root><subprice value=\"10\" /><offlinesubprice value=\"0\"/></root>', 'com.mk.ots.ticket.service.parse.MinusTicket', 'T', '1.00', '5', '', '', '1', 'T', null, null, null, null, null, '10.00'), 
('19', '立减30', '2015-06-27 00:00:00', '2014-12-12 08:00:00', '2015-07-02 08:00:00', '7', 'T', '-1', '<root><subprice value=\"30\" /><offlinesubprice value=\"0\"/></root>', 'com.mk.ots.ticket.service.parse.MinusTicket', 'T', '1.00', '5', '', '', '1', 'T', null, null, null, null, null, '30.00'), 
('20', '立减60', '2015-06-27 00:00:00', '2014-12-12 08:00:00', '2015-07-02 08:00:00', '7', 'T', '-1', '<root><subprice value=\"60\" /><offlinesubprice value=\"0\"/></root>', 'com.mk.ots.ticket.service.parse.MinusTicket', 'T', '1.00', '5', '', '', '1', 'T', null, null, null, null, null, '60.00');

