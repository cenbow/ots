DROP TABLE IF EXISTS `t_room_sale`;
CREATE TABLE `t_room_sale` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `roomtypeid` int(20) NOT NULL COMMENT '特价房型ID',
  `oldRoomtypeId` int(20) NOT NULL COMMENT '旧房型ID',
  `roomNo` varchar(10) DEFAULT NULL COMMENT '房间号',
  `pms` varchar(50) DEFAULT NULL COMMENT 'pmsRoomId',
  `createDate` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `salePrice` decimal(8,2) DEFAULT NULL COMMENT '活动价格',
  `costPrice` decimal(8,2) DEFAULT NULL COMMENT '市场价',
  `startTime` varchar(30) DEFAULT NULL COMMENT '活动开始时间',
  `endTime` varchar(30) DEFAULT NULL COMMENT '活动结束时间',
  `roomId` int(8) DEFAULT NULL COMMENT '活动roomId',
  `configId` int(8) DEFAULT NULL COMMENT '活动配置Id',
  `isBack` char(1) DEFAULT NULL COMMENT '是否回退',
  `saleName` varchar(20) DEFAULT NULL COMMENT '活动名称',
  `saleType` int(2) DEFAULT NULL COMMENT '活动类型',
  `hotelId` int(8) DEFAULT NULL COMMENT '酒店Id',
  `settleValue` decimal(8,2) DEFAULT NULL COMMENT '结算价格',
  PRIMARY KEY (`id`),
  KEY `hotelId` (`hotelId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2605 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_room_sale_city`;
CREATE TABLE `t_room_sale_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `saleTypeId` int(11) DEFAULT NULL COMMENT '活动类型Id',
  `provinceCode` varchar(20) DEFAULT NULL COMMENT '省份编码',
  `cityCode` varchar(20) DEFAULT NULL COMMENT '城市编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_room_sale_config`;
CREATE TABLE `t_room_sale_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `hotelId` int(11) NOT NULL COMMENT '酒店ID',
  `roomId` int(11) DEFAULT NULL COMMENT '房间ID',
  `roomTypeId` int(11) NOT NULL COMMENT '房型Id',
  `saleType` int(2) DEFAULT NULL COMMENT '活动类型',
  `saleValue` decimal(10,2) DEFAULT NULL COMMENT '活动价格',
  `costPrice` decimal(10,2) DEFAULT NULL COMMENT '市场价',
  `num` int(4) DEFAULT NULL COMMENT '活动数量',
  `saleName` varchar(20) DEFAULT NULL COMMENT '活动名称',
  `saleRoomTypeId` int(11) DEFAULT NULL COMMENT '活动房型Id',
  `settleValue` decimal(10,2) DEFAULT NULL COMMENT '结算值',
  `settleType` int(2) DEFAULT NULL COMMENT '结算类型',
  `valid` char(1) DEFAULT NULL COMMENT '是否有效',
  `styleType` int(2) DEFAULT NULL,
  `started` char(1) DEFAULT NULL COMMENT '活动开始标记',
  `saleConfigInfoId` int(11) DEFAULT NULL COMMENT '活动配置Id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `createBy` varchar(20) DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `updateBy` varchar(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_room_sale_config_info`;
CREATE TABLE `t_room_sale_config_info` (
  `id` int(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `saleTypeId` int(8) NOT NULL COMMENT '活动类型Id',
  `startDate` date DEFAULT NULL COMMENT '活动开始日期',
  `endDate` date DEFAULT NULL COMMENT '活动结束日期',
  `startTime` time DEFAULT NULL COMMENT '活动开始时间',
  `endTime` time DEFAULT NULL COMMENT '活动结束时间',
  `saleValue` varchar(10) DEFAULT NULL COMMENT '活动值',
  `saleLabel` varchar(20) DEFAULT NULL COMMENT '活动床型标签',
  `description` varchar(500) DEFAULT NULL COMMENT '活动描述',
  `fontColor` varchar(10) DEFAULT NULL COMMENT '活动字体颜色',
  `valid` char(1) DEFAULT NULL COMMENT '是否有效',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `createBy` varchar(20) DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `updateBy` varchar(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_room_sale_type`;
CREATE TABLE `t_room_sale_type` (
  `id` int(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `saleName` varchar(50) DEFAULT NULL COMMENT '活动类型名称',
  `fontColor` varchar(10) DEFAULT NULL COMMENT '活动字体颜色',
  `description` varchar(200) DEFAULT NULL COMMENT '活动描述',
  `valid` char(1) DEFAULT NULL COMMENT '是否有效',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `createBy` varchar(20) DEFAULT NULL COMMENT '创建人',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `updateBy` varchar(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


CREATE TABLE `b_card` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `batch_no` VARCHAR(50) NOT NULL COMMENT '批次号',
  `name` VARCHAR(100) DEFAULT NULL COMMENT '名称',
  `no` VARCHAR(50) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `status` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '状态：1-生成 2-入库3-激活4-使用5-注销',
  `price` DECIMAL(10,2) NOT NULL DEFAULT '0.00' COMMENT '面额',
  `cost` DECIMAL(10,2) NOT NULL DEFAULT '0.00' COMMENT '售价',
  `begin_date` DATETIME NOT NULL COMMENT '有效期开始日期',
  `end_date` DATETIME NOT NULL COMMENT '有效期截止日期',
  `description` TEXT COMMENT '描述',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `create_by` VARCHAR(50) DEFAULT NULL,
  `update_by` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `u_card_uselog` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `card_id` INT(11) NOT NULL COMMENT '卡id',
  `mid` BIGINT(20) NOT NULL COMMENT '用户id',
  `card_price` DECIMAL(10,2) NOT NULL DEFAULT '0.00' COMMENT '充值金额',
  `create_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


##20151012 脚本 begin--
alter table b_otaorder add promoType int(4) default 0 comment '特价类型, 0：非特价房间，1：今夜特价房间';
alter table b_otaorder add roomTicket varchar(100) comment '房券代码';
##20151012 脚本 end--

##
ALTER TABLE `u_member` ADD COLUMN `citycode`  varchar(50) NULL;

##更新u_member历史数据
update
(
select m.mid,min(o.id) oid from u_member m
INNER JOIN b_otaorder o on m.mid = o.Mid
where m.citycode is null and o.`OrderStatus` IN (180,190,200)
GROUP BY m.mid
) mm LEFT JOIN b_otaorder o on mm.mid = o.Mid and mm.oid = o.id
left join t_hotel h on h.id = o.HotelId
LEFT JOIN u_member m on m.mid = mm.mid
set m.citycode = h.citycode;