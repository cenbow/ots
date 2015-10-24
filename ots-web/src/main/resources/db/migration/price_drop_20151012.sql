#订单实体字段添加
ALTER TABLE `b_otaorder` ADD COLUMN `clearingtype` INT(10) NULL DEFAULT 2001
COMMENT '结算类型'
AFTER `isReceiveCashBack`, ADD COLUMN `clearingmoney` DECIMAL(10, 2) NULL
COMMENT '结算金额'
AFTER `clearingtype`;

#直减订单明细
CREATE TABLE `b_bill_price_drop_orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `hotelId` bigint(20) DEFAULT NULL COMMENT '酒店id',
  `orderId` bigint(20) DEFAULT NULL COMMENT '订单号',
  `orderType` bigint(1) DEFAULT NULL COMMENT '支付类型',
  `roomTypeName` varchar(50) NOT NULL COMMENT '房型',
  `roomNo` varchar(50) DEFAULT NULL COMMENT '房间',
  `beginTime` datetime DEFAULT NULL COMMENT '入住时间',
  `endTime` datetime DEFAULT NULL COMMENT '离店时间',
  `dayNumber` int(11) DEFAULT NULL COMMENT '间夜数',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `pId` bigint(20) DEFAULT NULL COMMENT '账单的id',
  `payStatus` int(11) DEFAULT NULL,
  `spreadUser` bigint(20) DEFAULT NULL,
  `checkinTime` datetime DEFAULT NULL COMMENT '入住时间',
  `rulecode` int(11) DEFAULT NULL,
  `orderCreatetime` datetime NOT NULL,
  `statusTime` datetime DEFAULT NULL COMMENT '每天是否汇总标记',
  `clearingMoney` decimal(10,2) DEFAULT NULL COMMENT '结算金额',
  PRIMARY KEY (`id`)
)

#策略配置表
CREATE TABLE b_strategy_price
(
  id            BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name          VARCHAR(200)       NOT NULL,
  type          INT                NOT NULL,
  value         DECIMAL(10, 2)     NOT NULL,
  stprice       DECIMAL(10, 2)     NOT NULL,
  rulearea      BIGINT             NOT NULL,
  rulehotel     BIGINT             NOT NULL,
  ruleroomtype  BIGINT             NOT NULL,
  rulebegintime VARCHAR(10)        NOT NULL,
  ruleendtime   VARCHAR(10)        NOT NULL,
  ruleroom      VARCHAR(200)       NOT NULL,
  contracttype  INT                NOT NULL,
  contractvalue INT                NOT NULL,
  createtime    DATETIME           NOT NULL,
  enable        CHAR(1)            NOT NULL
);

# 酒店时段空房记录表
CREATE TABLE b_roomstock
(
  id            BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  rulearea      BIGINT             NOT NULL,
  rulehotel     BIGINT             NOT NULL,
  ruleroomtype  BIGINT             NOT NULL,
  rulebegintime DATETIME           NOT NULL,
  ruleendtime   DATETIME           NOT NULL,
  totalroom     INT                NOT NULL,
  mikeroomnum   INT                NOT NULL,
  ruleid        BIGINT             NOT NULL,
  createtime    DATETIME           NOT NULL
);
CREATE INDEX rulehotel ON b_roomstock (rulehotel, ruleroomtype, rulebegintime, ruleendtime);

# 酒店时段锁房记录表
CREATE TABLE b_roomstock_order
(
  id          BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  roomstockid BIGINT             NOT NULL,
  orderid     BIGINT             NOT NULL,
  createtime  DATETIME           NOT NULL
);
CREATE INDEX roomstockid ON b_roomstock_order (roomstockid);
CREATE INDEX roomstockid_2 ON b_roomstock_order (roomstockid, orderid);
)

# B_promotion表增加规则表id字段ruleid
ALTER TABLE `ots`.`b_promotion`
ADD COLUMN `strategy_id` BIGINT NULL
COMMENT '优惠券策略id'
AFTER `channelid`;

# 活动表
INSERT INTO `b_activity` (`id`, `title`, `detail`, `description`, `type`, `begintime`, `endtime`, `limitget`, `hotel`, `banner`, `Iscollect`, `actState`, `expectNum`, `actualNum`, `expectMerchantNum`, `actualMerchantNum`, `isticket`, `gentype`, `promotionmethodtype`, `activeurl`, `activepic`, `isvisible`, `activityCarrier`, `userGroup`)
VALUES
  ('34', '眯客0元住房券', '眯客0元住房券', '限制在线支付使用', NULL, '2015-10-12 00:00:00', '2016-01-12 00:00:00', '1', NULL, '', 'T', '5',
   NULL, NULL, NULL, NULL, 'T', '1', '2', NULL, NULL, 'F', NULL, NULL);

# 优惠券策略表
INSERT INTO `b_strategy_price` (`name`, `type`, `value`, `stprice`, `rulearea`, `rulehotel`, `ruleroomtype`, `rulebegintime`, `ruleendtime`, `ruleroom`, `enable`)
VALUES ('住房券优惠策略', '3', '100.00', '0.00', '0', '0', '0', '2015-10-12 16:01:24', '2016-10-12 16:01:28', '', 'T');

# 优惠券
INSERT INTO `b_promotion` (`name`, `description`, `Createtime`, `Begintime`, `Endtime`, `type`, `isticket`, `num`, `info`, `classname`, `isota`, `otapre`, `activitiesid`, `note`, `pic`, `version`, `isinstance`, `weight`, `totalnum`, `plannum`, `protype`, `onlineprice`, `offlineprice`, `expiretype`, `expiredaynum`, `effectivetype`, `platformtype`, `sourcecdkey`, `channelid`, `strategy_id`)
VALUES
  ('眯客0元住房优惠券', '限在线支付使用', '2015-10-12 00:00:00', '2015-10-12 00:00:00', '2016-01-12 00:00:00', '9', 'T', '-1', '<root> <tickets> 	<ticket> 		<onlineprice>0</onlineprice> 		<offlineprice>0</offlineprice> 		<limit-daynum>90</limit-daynum><!--限制天数--> 		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.--> 	</ticket> </tickets> </root>', '',
   'T', '1.00', '34', NULL, NULL, NULL, 'T', NULL, NULL, NULL, NULL, '0.00', '0.00', '2', '90', '1', '3', NULL, NULL,
   NULL);

# 渠道表
INSERT INTO `ots`.`b_active_channel` (`activeid`, `channelid`, `channelname`, `promotionid`, `expiration`)
VALUES ('34', '1', '渠道一', '123', '2016-10-30 23:59:59');
INSERT INTO `ots`.`b_active_channel` (`activeid`, `channelid`, `channelname`, `promotionid`, `expiration`)
VALUES ('34', '1', '渠道二', '123', '2016-10-30 23:59:59');