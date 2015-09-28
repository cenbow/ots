CREATE TABLE `u_send_uticket` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `mid` bigint(20) NOT NULL COMMENT '用户编号',
  `msgtype` tinyint(4) NOT NULL default 1 COMMENT '通知消息类型 1短信 2app消息',
  `statisticinvalid` tinyint(4) NOT NULL  default 1 COMMENT '是否统计领取过了\n1：未统计领取\n2：统计领取',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_invalid_mid` (`statisticinvalid`,`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='发放优惠券用户表';




INSERT INTO `ots`.`b_activity` (`id`, `title`, `detail`,
 `description`, `type`, `begintime`, `endtime`, `limitget`, 
`hotel`, `banner`, `Iscollect`, `actState`, `expectNum`, `actualNum`, 
`expectMerchantNum`, `actualMerchantNum`, `isticket`, `gentype`, `promotionmethodtype`, 
`activeurl`, `activepic`, `activityCarrier`, `userGroup`, `isvisible`) 
VALUES ('27', '重庆新用户礼包活动详情(临时)', '发放一张30元优惠券', '限制在线支付使用', NULL, 
'2015-08-25 00:00:00', '2025-08-20 00:00:00', '1', NULL, '', 'T', '2', 
NULL, NULL, NULL, NULL, 'T', '1', '1', NULL, NULL, NULL, NULL, 'F');

INSERT INTO `ots`.`b_promotion` ( `name`, `Createtime`, `Begintime`, `Endtime`, `type`, `isticket`,
 `num`, `info`, `classname`, `isota`, `otapre`, `activitiesid`, `note`, `pic`,
 `version`, `isinstance`, `weight`, `totalnum`, `plannum`, `protype`, `onlineprice`, 
`offlineprice`, `expiretype`, `expiredaynum`, `effectivetype`, `platformtype`, `sourcecdkey`,
 `channelid`) VALUES ( '30元线新手券', '2015-04-29 18:08:43', '2015-04-29 18:08:49', 
'2016-07-31 23:59:59', '4', 'T', '-1', '', '', 'T', '1.00', '27', NULL, NULL, '2', 'T', NULL, '1000',
 '20', NULL, '30', '0', '2', '30', '1', '3', NULL, NULL);




