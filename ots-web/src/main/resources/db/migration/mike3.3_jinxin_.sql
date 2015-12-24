
-- ----------------------------
DROP TABLE IF EXISTS `sy_view_log`;
CREATE TABLE `sy_view_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `longitude` decimal(12,8) DEFAULT NULL,
  `latitude` decimal(12,8) DEFAULT NULL,
  `city_code` varchar(50) DEFAULT NULL,
  `ip` varchar(200) DEFAULT NULL,
  `call_method` int(5) DEFAULT '0' COMMENT '2-web；3-wechat；4-app(ios)；5-app(Android)',
  `version` varchar(200) DEFAULT NULL COMMENT '版本',
  `wifi_macaddr` varchar(200) DEFAULT NULL,
  `bi_macaddr` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `simsn` varchar(200) DEFAULT NULL,
  `from_url` varchar(255) DEFAULT NULL COMMENT '来源url',
  `to_url` varchar(200) DEFAULT NULL COMMENT '请求或事件地址目标url',
  `phone` varchar(50) DEFAULT NULL,
  `params` varchar(3000) DEFAULT NULL COMMENT '请求参数信息，json格式',
  `bussiness_id` varchar(200) DEFAULT NULL COMMENT '业务主键：(酒店id\\订单id)',
  `bussiness_type` int(11) DEFAULT NULL COMMENT '类型：1酒店；2：订单3：其它',
  `action_type` varchar(200) DEFAULT NULL,
  `view_code` varchar(200) DEFAULT NULL COMMENT '设备号',
  `imei` varchar(200) DEFAULT NULL COMMENT '手机串号',
  `hardwarecode` varchar(200) DEFAULT NULL,
  `call_time` datetime DEFAULT NULL COMMENT '请求时间',
  `model` varchar(200) DEFAULT NULL COMMENT '手机型号',
  `package_name` varchar(200) DEFAULT NULL,
  `data_name` varchar(200) DEFAULT NULL,
  `channel` varchar(200) DEFAULT NULL COMMENT '来源渠道',
  `city_name` varchar(200) DEFAULT NULL COMMENT '城市名称',
  PRIMARY KEY (`id`),
  KEY `index_id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `t_hotel_score_mark`;
CREATE TABLE `t_hotel_score_mark` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ord` int(11) DEFAULT NULL,
  `mark` varchar(500) DEFAULT NULL,
  `isdelete` char(1) DEFAULT 'F',
  `createtime` datetime DEFAULT NULL,
  `hotel_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_hotel_score_mark
-- ----------------------------
INSERT INTO `t_hotel_score_mark` VALUES ('1', '1', '值得推荐', 'F', '2015-12-08 15:03:36', null);
INSERT INTO `t_hotel_score_mark` VALUES ('2', '2', '采光好', 'F', '2015-12-08 15:03:50', null);
INSERT INTO `t_hotel_score_mark` VALUES ('3', '3', '宽敞', 'F', '2015-12-08 15:04:03', null);
INSERT INTO `t_hotel_score_mark` VALUES ('4', '4', '光线差', 'F', '2015-12-08 15:05:08', null);
INSERT INTO `t_hotel_score_mark` VALUES ('5', '5', '隔音差', 'F', '2015-12-08 15:05:22', null);
INSERT INTO `t_hotel_score_mark` VALUES ('6', '6', '面积小', 'F', '2015-12-08 15:05:32', null);
INSERT INTO `t_hotel_score_mark` VALUES ('7', '7', '信号差', 'F', '2015-12-08 15:05:42', null);
INSERT INTO `t_hotel_score_mark` VALUES ('8', '8', '安静', 'F', '2015-12-08 15:05:58', null);


DROP TABLE IF EXISTS `t_hotel_score_mark_member`;
CREATE TABLE `t_hotel_score_mark_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mid` bigint(20) DEFAULT NULL,
  `mark_id` bigint(20) DEFAULT NULL,
  `hotel_id` bigint(20) DEFAULT NULL,
  `room_id` bigint(20) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `score_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;