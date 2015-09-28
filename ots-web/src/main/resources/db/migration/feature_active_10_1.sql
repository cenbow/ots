alter table `l_push_log` add column `activeid` bigint(30) DEFAULT NULL COMMENT '活动id';

CREATE TABLE `b_promotion_product` (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `productid` bigint(30) NOT NULL COMMENT '优惠券产品ID',
  `name` varchar(100) NOT NULL COMMENT '产品名称',
  `promotionid` bigint(30) NOT NULL COMMENT '优惠券ID',
  `price` decimal(8,2) NOT NULL COMMENT '产品定价',
  `buytimelimit` int(11) DEFAULT NULL COMMENT '购买次数限制',
  `createby` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `updateby` varchar(50) DEFAULT NULL COMMENT '修改人ID',
  `createtime` datetime NOT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_prom_product_productid` (`productid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠券产品表';


CREATE TABLE `b_ticket_order` (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '购买ID',
  `mid` bigint(30) NOT NULL COMMENT '用户ID',
  `productid` bigint(30) NOT NULL COMMENT '产品ID',
  `paystatus` int(11) NOT NULL COMMENT '支付状态  50：无需支付,100：等待支付，120：已支付，130：已退款',
  `price` decimal(8,2) NOT NULL COMMENT '支付金额',
  `paytime` datetime DEFAULT NULL COMMENT '支付时间',
  `paytype` int(11) DEFAULT NULL COMMENT '支付类型	1-微信 2-支付宝 3-网银 4-其他',
  `paymentid` varchar(200) DEFAULT NULL COMMENT '第三方支付流水号',
  `opuserid` bigint(30) DEFAULT NULL COMMENT '操作人员ID',
  `createtime` datetime NOT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `index_ticket_buyinfo_mid` (`mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠券购买订单表';


ALTER TABLE `ots`.`l_push_log` ADD COLUMN `success` char(50) COMMENT '是否成功' AFTER `activeid`;
