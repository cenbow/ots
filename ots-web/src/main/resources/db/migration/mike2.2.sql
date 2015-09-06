ALTER TABLE `t_city`   
  CHANGE `ishotcity` `ishotcity` VARCHAR(10) CHARSET utf8 COLLATE utf8_general_ci DEFAULT 'F'   NULL  COMMENT '是否热门城市 T-是，F-否',
  CHANGE `isSelect` `isSelect` VARCHAR(10) CHARSET utf8 COLLATE utf8_general_ci DEFAULT 'F'   NULL  COMMENT '是否查询城市 F-是，F-否';
  
ALTER TABLE `t_city`
	MODIFY COLUMN `range`  double(10,2) NULL DEFAULT NULL COMMENT '城市半径长度（KM）' AFTER `ishotcity`;

UPDATE t_city SET ishotcity = 'F' , isSelect='F'
  
UPDATE t_city SET ishotcity='T',isSelect='T',simplename='沪',`range`=60000 where cityname LIKE '%上海%'

UPDATE t_city SET ishotcity='T',isSelect='T',CityName='C 重庆市',`range`=400000 where cityname LIKE '%重庆%'

alter table b_otaorder add userlongitude decimal(12,8);
alter table b_otaorder add userlatitude decimal(12,8);

-- 活动定义表字段添加
ALTER TABLE `b_activity` ADD COLUMN `activeurl` VARCHAR(255);
ALTER TABLE `b_activity` ADD COLUMN `activepic` VARCHAR(255);

-- 渠道注册用户对应券定义
DELETE FROM b_promotion
WHERE id IN (1, 24);
INSERT INTO ots.b_promotion (id, name, Createtime, Begintime, Endtime, type, isticket, num, info, classname, isota, otapre, activitiesid, note, pic, version, isinstance, weight, totalnum, plannum, protype, onlineprice, offlineprice, expiretype, expiredaynum, effectivetype, platformtype)
VALUES
  (1, '30元线上支付抵用券', '2015-04-29 18:08:43', '2015-04-29 18:08:49', '2016-07-31 23:59:59', 4, 'T', -1, '', '', 'T', 1.00,
   1, NULL, NULL, 2, 'T', NULL, 1000, 20, NULL, 30.00, 0.00, 2, 90, 1, 3);
INSERT INTO ots.b_promotion (id, name, Createtime, Begintime, Endtime, type, isticket, num, info, classname, isota, otapre, activitiesid, note, pic, version, isinstance, weight, totalnum, plannum, protype, onlineprice, offlineprice, expiretype, expiredaynum, effectivetype, platformtype)
VALUES
  (24, '10元线下支付抵用券', '2015-03-02 08:00:00', '2014-12-12 08:00:00', '2016-07-31 23:59:59', 4, 'T', -1, '', '', 'T', 1.00,
   1, '', '', 2, 'T', NULL, NULL, NULL, NULL, 0.00, 10.00, 2, 90, 1, 3);

-- 优惠券定义中添加渠道信息
ALTER TABLE `b_promotion` ADD COLUMN `sourcecdkey` VARCHAR(40) COMMENT '兑换码';
ALTER TABLE `b_promotion` ADD COLUMN `channelid` BIGINT COMMENT '兑换码渠道';