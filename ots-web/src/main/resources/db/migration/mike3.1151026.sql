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

