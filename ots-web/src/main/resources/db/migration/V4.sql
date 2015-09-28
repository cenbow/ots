--1. 短信回执状态 
ALTER TABLE `l_message_log` ADD COLUMN `success`    char(50)     DEFAULT 'F' COMMENT '是否成功';
ALTER TABLE `l_message_log` ADD COLUMN `reporttime` varchar(255) COMMENT '回执时间'；

--2. 系统邮箱配置
insert into sy_config (skey, svalue, stype) values ('mail.smtp.host', 'smtp.exmail.sina.com', 'mikeweb');
insert into sy_config (skey, svalue, stype) values ('mail.smtp.port', '25', 'mikeweb');
insert into sy_config (skey, svalue, stype) values ('mail.user.name', 'service@imike.com', 'mikeweb');
insert into sy_config (skey, svalue, stype) values ('mail.user.pwd', 'imike@2014', 'mikeweb');

--3. 活动定义
INSERT INTO `b_activity` VALUES ('4', '眯客优惠券兑换', null, null, null, '2015-06-16 00:00:00', '2015-06-30 23:59:59', '-1', null, 'http://7xip11.com2.z0.glb.qiniucdn.com/HD_EXCHANGE.jpg', 'T', null, null, null, null, null);

INSERT INTO `b_active_channel` VALUES 
('14', '4', '1', '渠道一', '15', '2015-07-31 00:00:00'), 
('15', '4', '2', '渠道二', '15', '2015-07-31 00:00:00'), 
('16', '4', '3', '渠道三', '15', '2015-07-31 00:00:00'), 
('17', '4', '4', '渠道四', '15', '2015-07-31 00:00:00'), 
('18', '4', '5', '渠道五', '15', '2015-07-31 00:00:00'), 
('19', '4', '6', '渠道六', '15', '2015-07-31 00:00:00'), 
('20', '4', '7', '渠道七', '15', '2015-07-31 00:00:00'), 
('21', '4', '8', '渠道八', '15', '2015-07-31 00:00:00'), 
('22', '4', '9', '渠道九', '16', '2015-07-31 00:00:00'), 
('23', '4', '10', '渠道十', '16', '2015-07-31 00:00:00'), 
('24', '4', '11', '渠道十一', '17', '2015-07-31 00:00:00'), 
('25', '4', '12', '渠道十二', '17', '2015-07-31 00:00:00');

INSERT INTO `b_promotion` VALUES 
('15', '兑换优惠券', '2015-06-16 00:00:00', '2015-06-16 00:00:00', '2015-07-16 00:00:00', '1', 'T', '-1', '<root>\n<tickets>\n	<ticket>\n		<onlineprice>30</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>30</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket', 'T', '1.00', '4', '', '', '2', 'T', null), 
('16', '兑换优惠券', '2015-06-16 00:00:00', '2015-06-16 00:00:00', '2015-07-16 00:00:00', '1', 'T', '-1', '<root>\n<tickets>\n	<ticket>\n		<onlineprice>30</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>30</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n	<ticket>\n		<onlineprice>30</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>30</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket', 'T', '1.00', '4', '', '', '2', 'T', null), 
('17', '兑换优惠券', '2015-06-16 00:00:00', '2015-06-16 00:00:00', '2015-07-16 00:00:00', '1', 'T', '-1', '<root>\n<tickets>\n	<ticket>\n		<onlineprice>30</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>30</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n	<ticket>\n		<onlineprice>30</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>30</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n	<ticket>\n		<onlineprice>30</onlineprice>\n		<offlineprice>0</offlineprice>\n		<limit-daynum>30</limit-daynum><!--限制天数-->\n		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->\n	</ticket>\n</tickets>\n</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket', 'T', '1.00', '4', '', '', '2', 'T',null);

truncate table b_active_cdkey;
