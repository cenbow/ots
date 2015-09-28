--// 规则定义
drop table IF EXISTS `b_area_rule`;
CREATE TABLE `b_area_rule` (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '地域规则id',
  `rulecode` int(11) NOT NULL COMMENT '业务规则码1001,1002,……',
  `rulename` varchar(30) DEFAULT NULL COMMENT '规则名称',
  `description` varchar(200) NOT NULL COMMENT '规则描述',
  `createby` int(11) NOT NULL DEFAULT '0' COMMENT '创建人',
  `updateby` int(11) DEFAULT NULL COMMENT '修改人',
  `createtime` datetime NOT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_rulecode` (`rulecode`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='地域规则定义表';


drop table IF EXISTS `b_area_rule_detail`;
CREATE TABLE `b_area_rule_detail` (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '地域规则明细id',
  `rulekey` varchar(200) NOT NULL COMMENT '规则key',
  `rulevalue` varchar(200) NOT NULL COMMENT '规则value',
  `createby` int(11) NOT NULL default 0 COMMENT '创建人',
  `updateby` int(11) DEFAULT NULL COMMENT '修改人',
  `createtime` datetime Not NULL  COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL  COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `index_rulekey` (`rulekey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地域规则明细表';

drop table IF EXISTS `b_area_rule_detail_relation`;
CREATE TABLE `b_area_rule_detail_relation` (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '地域规则关系id',
  `rulecode` Bigint(30) NOT NULL COMMENT 'b_area_rule表的rulecode',
  `detailid` Bigint(30) NOT NULL COMMENT 'b_area_rule_detail主键id',
  PRIMARY KEY (`id`),
  KEY `index_rulecode` (`rulecode`),
  KEY `index_detailid` (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地域规则关系表';

ALTER TABLE e_hotel ADD COLUMN rulecode INT(20) NULL DEFAULT 1001 COMMENT '规则值';
ALTER TABLE t_hotel ADD COLUMN rulecode INT(20) NULL DEFAULT 1001 COMMENT '规则值';


INSERT INTO `b_area_rule` VALUES ('1', '1001', 'A规则', 'A规则,目前为上海使用的老规则', '0', null, now(), null);
INSERT INTO `b_area_rule` VALUES ('2', '1002', 'B规则', 'B规则,重庆使用的新规则', '0', null, now(), null);

-- //活动定义表
ALTER TABLE `b_activity` ADD COLUMN `promotionmethodtype` int COMMENT '领取方式,1:系统发放  2.手动领取 3.需手动领取且需激活';		-- 发放方式：1:系统发放  2.手动领取 3.需手动领取且需激活

-- //用户绑定券表
ALTER TABLE `u_ticket` ADD COLUMN `promotionmethod` int COMMENT '领取方式,1:系统发放  2.手动领取 3.需手动领取且需激活'; -- 发放方式：1:系统发放  2.手动领取 3.需手动领取且需激活





-- ----------------------------
-- Table structure for ordertasks
-- ----------------------------
DROP TABLE IF EXISTS `ordertasks`;
CREATE TABLE `ordertasks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `otaorderid` bigint(20) NOT NULL COMMENT 'ota订单号',
  `hotelid` bigint(20) DEFAULT NULL COMMENT '酒店id',
  `content` text COMMENT '任务内容',
  `status` int(10) DEFAULT NULL COMMENT '任务状态',
  `tasktype` int(10) DEFAULT NULL COMMENT '任务类型',
  `count` int(10) DEFAULT NULL COMMENT '执行次数',
  `executetime` datetime DEFAULT NULL COMMENT '任务执行时间',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `querytask` (`status`,`tasktype`,`count`,`executetime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;





INSERT INTO `ots`.`b_area_rule_detail` (`id`, `rulekey`, `rulevalue`, `createby`, `updateby`, `createtime`, `updatetime`) VALUES ('1', 'YF', '20', '0', NULL, '2015-07-22 19:56:20', NULL);
INSERT INTO `ots`.`b_area_rule_detail` (`id`, `rulekey`, `rulevalue`, `createby`, `updateby`, `createtime`, `updatetime`) VALUES ('2', 'PT', '10', '0', NULL, '2015-07-22 19:56:32', NULL);


INSERT INTO `ots`.`b_area_rule_detail_relation` (`id`, `rulecode`, `detailid`) VALUES ('1', '1002', '1');
INSERT INTO `ots`.`b_area_rule_detail_relation` (`id`, `rulecode`, `detailid`) VALUES ('2', '1002', '2');




ALTER TABLE b_otaorder add column rulecode int(11) null;
update b_otaorder set rulecode=1001;



ALTER TABLE p_orderlog add column  `qiekeIncome` decimal(10,2) DEFAULT '0.00' COMMENT '切客收益';


INSERT INTO `ots`.`b_activity` (`id`, `title`, `detail`, `description`, `type`, `begintime`, `endtime`, `limitget`, `hotel`, `banner`, `Iscollect`, `actState`, `expectNum`, `actualNum`, `expectMerchantNum`, `actualMerchantNum`, `isticket`, `gentype`, `promotionmethodtype`) VALUES ('15', '新手优惠', NULL, NULL, NULL, '2015-07-24 00:00:00', '2015-09-01 00:00:00', '1', NULL, '', 'T', '2', NULL, NULL, NULL, NULL, NULL, '1', '3');

INSERT INTO `ots`.`b_promotion` (`name`, `Createtime`, `Begintime`, `Endtime`, `type`, `isticket`, `num`, `info`, `classname`, `isota`, `otapre`, `activitiesid`, `note`, `pic`, `version`, `isinstance`, `weight`, `totalnum`, `plannum`, `protype`, `onlineprice`, `offlineprice`, `expiretype`, `expiredaynum`, `effectivetype`) VALUES ('新手优惠券', '2015-07-25 00:00:00', '2015-07-25 00:00:00', '2015-09-01 00:00:00', '4', 'T', '9983', '<root>\n<tickets>\n	<ticket>\n		<onlineprice>30</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>30</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', '', 'T', '1.00', '15', NULL, NULL, '2', 'T', '0', '1000', '20', NULL, '30', '0', '2', '30', '1');
