alter table b_otaorder add column callVersion varchar(20) COMMENT '创建订单的客户端版本';

alter table b_otaroomorder add column promoid int(5) COMMENT '特价活动 id （1：今夜特价 2.今日特价 3 主题酒店 6.一元秒杀）';


-- ----------------------------
-- Table structure for `t_backmoney_rule`
-- ----------------------------
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

-- ----------------------------
-- Records of t_backmoney_rule
-- ----------------------------
INSERT INTO `t_backmoney_rule` VALUES ('1', '1', '-1', '-1', '5.00', null, null, '0', '1', '1', '2015-12-10 18:00:47');



-- ----------------------------
-- Table structure for `t_order_promo_pay_rule`
-- ----------------------------
DROP TABLE IF EXISTS `t_order_promo_pay_rule`;
CREATE TABLE `t_order_promo_pay_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `promo_type` int(10) DEFAULT NULL COMMENT '促销类型',
  `is_ticket_pay` int(1) DEFAULT NULL COMMENT '是否可以使用优惠券付款：1：支持优惠券支付 0：不支持优惠券支付',
  `is_wallet_pay` int(1) DEFAULT NULL COMMENT '是否可以使用钱包付款：1：支持钱包支付 0：不支持钱包支付',
  `is_online_pay` int(1) DEFAULT NULL COMMENT '是否可以使用在线支付付款：1：支持在线支付支付 0：不支持在线支付支付',
  `is_real_pay` int(1) DEFAULT NULL COMMENT '是否可以使用线下付款：1：支持线下支付 0：不支持线下支付',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(20) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idex_t_order_promo_pay_rule_promo_type` (`promo_type`)
);

-- ----------------------------
-- Records of t_order_promo_pay_rule
-- ----------------------------
INSERT INTO `t_order_promo_pay_rule` VALUES ('2', '0', '1', '1', '1', '1', '2015-12-16 21:01:28', '2015-12-16 21:01:30', '1', '1');
INSERT INTO `t_order_promo_pay_rule` VALUES ('3', '1', '0', '1', '1', '1', '2015-12-16 21:01:42', '2015-12-16 21:01:45', '1', '1');
INSERT INTO `t_order_promo_pay_rule` VALUES ('4', '6', '0', '1', '1', '0', '2015-12-16 21:02:01', '2015-12-16 21:02:03', '1', '1');
INSERT INTO `t_order_promo_pay_rule` VALUES ('5', '3', '1', '1', '1', '1', '2015-12-18 19:46:55', '2015-12-18 19:46:58', '1', '1');
INSERT INTO `t_order_promo_pay_rule` VALUES ('6', '2', '0', '1', '1', '1', '2015-12-18 19:48:24', '2015-12-18 19:48:25', '1', '1');
