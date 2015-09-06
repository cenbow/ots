-- 房量统计表
﻿CREATE TABLE `t_room_census` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `hotelId` bigint(20) DEFAULT NULL COMMENT '酒店id',
  `hotelName` varchar(50) DEFAULT NULL COMMENT '酒店名称',
  `visible` char(1) DEFAULT NULL COMMENT '上线标识',
  `online` char(1) DEFAULT NULL COMMENT '在线标识',
  `roomCount` int(11) DEFAULT NULL COMMENT '总房量',
  `freeRoomCount` int(11) DEFAULT NULL COMMENT '可售房量',
  `year` char(4) DEFAULT NULL COMMENT '统计刻度点(年)',
  `month` char(2) DEFAULT NULL COMMENT '统计刻度点(月)',
  `day` char(2) DEFAULT NULL COMMENT '统计刻度点(日)',
  `date` char(10) DEFAULT NULL COMMENT '统计刻度点(时：分)',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `other1` int(11) DEFAULT NULL COMMENT '备用字段',
  `other2` int(11) DEFAULT NULL COMMENT '备用字段',
  `other3` varchar(20) DEFAULT NULL COMMENT '备用字段',
  `other4` varchar(20) DEFAULT NULL COMMENT '备用字段',
  PRIMARY KEY (`id`)
) COMMENT='房量统计表';
