-- 1.
CREATE TABLE `b_user_500` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`phone` varchar(255),
	`status` varchar(255),
	PRIMARY KEY (`id`)
);

-- 2.

CREATE TABLE `u_gift_record` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`mid` bigint NOT NULL,
	`activeid` bigint NOT NULL,
	`createtime` datetime NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `u_active_share` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`mid` bigint NOT NULL,
	`activeid` bigint NOT NULL,
	`createtime` datetime NOT NULL,
	PRIMARY KEY (`id`)
);

-- 3. 
-- 兑换码表
DROP TABLE IF EXISTS `b_active_cdkey`;
CREATE TABLE `b_active_cdkey` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batchno` varchar(255) NOT NULL COMMENT '批次编码',
  `activeid` bigint(20) NOT NULL COMMENT '活动编码',
  `channelid` bigint(20) NOT NULL COMMENT '渠道编码',
  `promotionid` bigint(20) NOT NULL COMMENT '优惠券定义',
  `code` varchar(255) NOT NULL COMMENT '生成兑换码',
  `expiration` datetime NOT NULL COMMENT '截止时间',
  `createtime` datetime NOT NULL COMMENT '创建时间',
  `used` char(50) DEFAULT NULL COMMENT '是否有效',
  `usetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) 
-- 渠道对应券编码
drop table `b_active_channel`;
CREATE TABLE `b_active_channel` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`activeid` bigint NOT NULL COMMENT '活动编码',
	`channelid` bigint NOT NULL COMMENT '渠道编码',
	`channelname` varchar(255) NOT NULL COMMENT '渠道名称',
	`promotionid` bigint NOT NULL COMMENT '优惠券编码',
	`expiration` datetime NOT NULL COMMENT '截止时间',
	PRIMARY KEY (`id`)
);
-- 兑换码日志
drop table `u_active_cdkey_log`;
CREATE TABLE `u_active_cdkey_log` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`mid` bigint NOT NULL,
	`code` varchar(255) NOT NULL,
	`activeid` bigint NOT NULL,
	`channelid` bigint NOT NULL,
	`promotionid` bigint NOT NULL,
	`success` char(50),
	`push` char(50),
	`createtime` datetime NOT NULL COMMENT '创建时间',
	PRIMARY KEY (`id`)
);

-- 4. 券定义表添加权重字段
ALTER TABLE `b_promotion` ADD COLUMN `weight` bigint;


-- 5. 初始化分享次数  15351
drop procedure if exists proc_u_active_share;
delimiter #
create procedure proc_u_active_share(in count integer, in mid bigint, in activeid bigint)
begin
declare v_max int unsigned default count;
declare v_counter int unsigned default 0;
  start transaction;
  while v_counter < v_max do
    insert into u_active_share (mid, activeid, createtime) values (mid, activeid, now());
    set v_counter=v_counter+1;
  end while;
  commit;
end #
-- call proc_u_active_share(15351, 0000, 3);

-- 6. 配置数据
-- 6.1 活动定义
INSERT INTO `b_activity` VALUES ('3', '砸十亿！请你住酒店', '“十亿愿望清单”\n给所有来魔都的人 一个歇脚的地方\n', '十亿愿望清单\n活动标题：\n“砸十亿！请你住酒店”\n\n活动副标题：\n“十亿愿望清单”\n给所有来魔都的人 一个歇脚的地方\n\n优惠券规则\n	●	优惠券分布\n	●	\n\n	●	发放领券规则\n用户每点击一次领取，就随机从60300张预先放入的“10元-100元”面值的优惠券里发放一张\n\n	●	优惠券权限\n活动期间，每个用户每天可领取一次\n有效期：6月11日—6月17日\n\n	●	活动对象\n活动期间，仅限在线支付所用\n\n实物奖品规则\n	●	实物奖品种类数量\n	●	\n\n	●	实物奖品抽奖规则\n抱枕：每天30个幸运用户\nIphone6：每天1个幸运用户（实际无）\n\n	●	活动对象\n活动期间，从所有“点击参加接力”的用户中抽奖\n\n	●	抽奖权限\n活动期间，每个用户每天可抽一次奖\n（所有用户限获一次奖）\n有效期：6月11日—6月17日\n\n	●	获奖公示\n6月18日在“眯客服务号”上统一公示活动期间所有获奖幸运用户', null, '2015-06-01 00:00:00', '2015-06-17 23:59:59', '7', null, 'http://7xip11.com2.z0.glb.qiniucdn.com/HD_611.jpg');
-- 6.2 优惠券定义
INSERT INTO `b_promotion` VALUES 
('8', '活动优惠券',  '2015-06-11 12:00:00', '2015-06-01 00:00:00', '2015-06-17 23:59:59', '1', 'T', '30000', '<root>\n<tickets>\n	<ticket>\n		<onlineprice>10</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>7</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket',     'T', '1.00', '3', '', '', '2', 'T', '20'), 
('9', '活动优惠券',  '2015-06-11 12:00:00', '2015-06-01 00:00:00', '2015-06-17 23:59:59', '1', 'T', '30000', '<root>\n<tickets>\n	<ticket>\n		<onlineprice>20</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>7</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket',     'T', '1.00', '3', '', '', '2', 'T', '20'), 
('10', '活动优惠券', '2015-06-11 12:00:00', '2015-06-01 00:00:00', '2015-06-17 23:59:59', '1', 'T', '30000', '<root>\n<tickets>\n	<ticket>\n		<onlineprice>30</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>7</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket',     'T', '1.00', '3', '', '', '2', 'T', '20'), 
('11', '活动优惠券', '2015-06-11 12:00:00', '2015-06-01 00:00:00', '2015-06-17 23:59:59', '1', 'T', '0',   '<root>\n<tickets>\n	<ticket>\n		<onlineprice>50</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>7</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket',     'T', '1.00', '3', '', '', '2', 'T', '0'), 
('12', '活动优惠券', '2015-06-11 12:00:00', '2015-06-01 00:00:00', '2015-06-17 23:59:59', '1', 'T', '0',   '<root>\n<tickets>\n	<ticket>\n		<onlineprice>100</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>7</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket', 'T', '1.00', '3', '', '', '2', 'T', '0')

