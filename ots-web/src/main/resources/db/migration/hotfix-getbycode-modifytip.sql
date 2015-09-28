#1. 添加活动定义
INSERT INTO b_activity (id, title, detail, description, type, begintime, endtime, limitget, hotel, banner, Iscollect, actState, expectNum, actualNum, expectMerchantNum, actualMerchantNum, isticket, gentype, promotionmethodtype, activeurl, activepic, isvisible, activityCarrier, userGroup)
VALUES (30, '兑换活动', NULL, NULL, NULL, '2015-09-14 00:00:00', '2015-10-30 23:59:59', 0, NULL,
        'http://7xip11.com2.z0.glb.qiniucdn.com/HD_EXCHANGE.jpg', 'T', '5', NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL,
        NULL, 'T', NULL, NULL);

#2. 添加优惠券定义
INSERT INTO ots.b_promotion (name, description, Createtime, Begintime, Endtime, type, isticket, num, info, classname, isota, otapre, activitiesid, note, pic, version, isinstance, weight, totalnum, plannum, protype, onlineprice, offlineprice, expiretype, expiredaynum, effectivetype, platformtype, sourcecdkey, channelid)
VALUES ('眯客首单优惠券', NULL, '2015-09-14 00:00:00', '2015-09-14 00:00:00', '2015-10-30 23:59:59', 8, 'T', -1, '<root>
<tickets>
	<ticket>
		<onlineprice>30</onlineprice>
		<offlineprice>0</offlineprice>
		<limit-daynum>5</limit-daynum><!--限制天数-->
		<effective>0</effective><!--生效时间,0为当日生效,1为隔日生效,以此类推.-->
	</ticket>
</tickets>
</root>', 'com.mk.ots.ticket.service.parse.SimplesubTicket', 'T', 1.00, 30, '', '', 2, 'T', NULL, NULL, NULL, NULL,
        30.00, 0.00, 2, 5, 1, 3, NULL, NULL);


#3. 添加渠道定义
INSERT INTO b_active_channel (activeid, channelid, channelname, promotionid, expiration)
VALUES (30, 1, '市场部', -1, '2015-10-30 23:59:59');

#4. 更新渠道优惠券
UPDATE b_active_channel
SET promotionid = (SELECT id
                   FROM b_promotion
                   WHERE name = '眯客首单优惠券' AND isinstance = 'T')
WHERE channelid = 1 AND activeid = 30;