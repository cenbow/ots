DROP TABLE IF EXISTS `t_backmoney_rule`;
CREATE TABLE `t_backmoney_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bussiness_type` int(10) DEFAULT NULL COMMENT '业务类型',
  `hotel_city_code` varchar(20) DEFAULT '-1' COMMENT '酒店城市code',
  `member_city_code` varchar(20) DEFAULT '-1' COMMENT '会员城市code',
  `per_money` decimal(8,2) DEFAULT NULL COMMENT '返现金额',
  `max_money` decimal(8,2) DEFAULT NULL COMMENT '限制最大返现金额',
  `max_count` int(8) DEFAULT NULL COMMENT '限制返现最大次数',
  `rule_type` int(11) DEFAULT '0' COMMENT '规则限制类型；0：不限制；1：最大次数;2:最大金额',
  `status` varchar(30) DEFAULT '1' COMMENT '是否有效',
  `type` varchar(30) DEFAULT NULL COMMENT '规则类型：1入住返现',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_backmony_rule_bussiness_type` (`bussiness_type`) USING BTREE,
  KEY `idx_backmony_rule_hotel_city_code` (`hotel_city_code`),
  KEY `idx_backmony_member_city_code` (`member_city_code`),
  KEY `idx_backmony_rule_type` (`rule_type`),
  KEY `idx_backmony_rule_status` (`status`),
  KEY `idx_backmony_type` (`type`)
);

INSERT INTO `t_backmoney_rule` VALUES ('1', '1', '-1', '-1', '5.00', null, null, '0', '1', '1', now());