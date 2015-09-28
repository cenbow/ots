-- 1. 定义券定义
INSERT INTO b_promotion (name, Createtime, Begintime, Endtime, type, isticket, num, info, classname, isota, otapre, activitiesid, note, pic, version, isinstance, weight, totalnum, plannum, protype, onlineprice, offlineprice, expiretype, expiredaynum, effectivetype, platformtype, sourcecdkey, channelid)
VALUES ('眯客开学季特惠抵用券', '2015-06-11 16:36:55', '2015-09-06 00:00:00', '2015-09-16 23:59:59', 8, 'T', -1, '<root>
<tickets>
	<ticket>
		<onlineprice>40</onlineprice>
		<offlineprice>0</offlineprice>
		<limit-daynum>30</limit-daynum><!--限制天数-->
		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->
	</ticket>
</tickets>
</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket', 'T', 1.00, 4, '', '', 2, 'T', NULL, NULL, NULL, NULL,
        40.00, 0.00, 2, 5, 1, 3, NULL, NULL);

-- 2. 添加渠道信息
INSERT INTO b_active_channel (activeid, channelid, channelname, promotionid, expiration)
VALUES (4, 17, '上海市场部(刘志强)', -1, '2015-09-16 23:59:59');

-- 3. 关联渠道与券
UPDATE b_active_channel
SET promotionid = (SELECT id
                   FROM b_promotion
                   WHERE name = '眯客开学季特惠抵用券' AND isinstance = 'T')
WHERE channelid = 17;

-- 4. 添加活动入口
DELETE FROM b_activity
WHERE id = 4;
INSERT INTO ots.b_activity (id, title, detail, description, type, begintime, endtime, limitget, hotel, banner, Iscollect, actState, expectNum, actualNum, expectMerchantNum, actualMerchantNum, isticket, gentype, promotionmethodtype, activeurl, activepic, isvisible, activityCarrier, userGroup)
VALUES (4, '眯客优惠券兑换', NULL, NULL, NULL, '2015-06-16 00:00:00', '2015-12-03 00:00:00', 0, NULL,
        'http://7xip11.com2.z0.glb.qiniucdn.com/HD_EXCHANGE.jpg', 'T', '5', NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL,
        NULL, 'T', NULL, NULL);
