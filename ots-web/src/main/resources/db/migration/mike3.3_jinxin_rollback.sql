/*
Navicat MySQL Data Transfer

Source Server         : 10.10.187.189(北京服务器模拟)
Source Server Version : 50623
Source Host           : 10.10.144.135:3306
Source Database       : ots

Target Server Type    : MYSQL
Target Server Version : 50623
File Encoding         : 65001

Date: 2015-12-23 22:14:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sy_view_log
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
