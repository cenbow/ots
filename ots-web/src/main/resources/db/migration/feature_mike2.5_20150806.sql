#历史订单表字段变更
ALTER TABLE `b_hotel_stat`
ADD COLUMN `isDelete`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除（c端操作）',
ADD COLUMN `roomTypeId`  bigint(20) NULL COMMENT '房型id',
ADD COLUMN `roomno`  varchar(50) NULL COMMENT '房间号' ,
ADD COLUMN `roomTypeName`  varchar(50) NULL COMMENT '房型名称';

ALTER TABLE `b_hotel_stat`
ADD INDEX `index_createtime` (`createTime`) USING BTREE ,
ADD INDEX `index_isdel` (`isDelete`) USING BTREE ;

#初始化历史数据
INSERT INTO b_hotel_stat (
	mid,
	hotelId,
	statisticInvalid,
	otaorderid,
	createTime,
	updateTime,
	isDelete,
	roomTypeId,
	roomno,
	roomTypeName
) SELECT
	a.Mid,
	a.HotelId,
	2,
	a.id,
	a.Createtime,
	a.Updatetime,
	'F',
  b.roomTypeId,b.roomno,b.roomTypeName
FROM
	b_otaorder a,
	b_pmsroomorder b,
	b_otaroomorder c
WHERE
	b.PmsRoomOrderNo = c.PMSRoomOrderNo AND a.id = c.OtaOrderId
AND a.HotelId=b.Hotelid AND a.OrderStatus in(180,190,200)
AND a.id NOT IN (
	SELECT
		otaorderid
	FROM
		b_hotel_stat
);

#更新老数据
UPDATE b_hotel_stat SET roomTypeId = 
(SELECT
  b.roomTypeId
FROM
	b_otaorder a,
	b_pmsroomorder b,
	b_otaroomorder c
WHERE
	b.PmsRoomOrderNo = c.PMSRoomOrderNo
AND a.id = c.OtaOrderId AND a.HotelId=b.Hotelid AND a.HotelId=c.Hotelid
AND a.id=b_hotel_stat.otaorderid),
roomno=(SELECT
  b.roomno
FROM
	b_otaorder a,
	b_pmsroomorder b,
	b_otaroomorder c
WHERE
	b.PmsRoomOrderNo = c.PMSRoomOrderNo
AND a.id = c.OtaOrderId AND a.HotelId=b.Hotelid AND a.HotelId=c.Hotelid
AND a.id=b_hotel_stat.otaorderid),
roomTypeName=(SELECT
  b.roomTypeName
FROM
	b_otaorder a,
	b_pmsroomorder b,
	b_otaroomorder c
WHERE
	b.PmsRoomOrderNo = c.PMSRoomOrderNo
AND a.id = c.OtaOrderId AND a.HotelId=b.Hotelid AND a.HotelId=c.Hotelid
AND a.id=b_hotel_stat.otaorderid)
where b_hotel_stat.RoomTypeId IS NULL;

#更新创建时间为预离时间
UPDATE b_hotel_stat SET createTime = 
(SELECT
  b.Endtime
FROM
	b_otaorder a,
	b_pmsroomorder b,
	b_otaroomorder c
WHERE
	b.PmsRoomOrderNo = c.PMSRoomOrderNo
AND a.id = c.OtaOrderId AND a.HotelId=b.Hotelid AND a.HotelId=c.Hotelid
AND a.id=b_hotel_stat.otaorderid);


#C端调用房态相关接口日志表
CREATE TABLE `t_interface_roomstate_log` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`callmethod`  char(1) NULL COMMENT '调用来源 1-crs；2-web；3-wechat；4-app(ios)；5-app(Android) ' ,
`callversion`  varchar(20) NULL COMMENT '调用版本' ,
`ip`  varchar(20) NULL COMMENT 'ip' ,
`methodurl`  varchar(50) NULL COMMENT '接口url 例如：/history/querylist' ,
`methodparams`  varchar(1000) NULL COMMENT '方法参数' ,
`createtime`  datetime NULL COMMENT '创建时间' ,
PRIMARY KEY (`id`),
INDEX `index_methodurl` (`methodurl`) USING BTREE COMMENT '接口url 索引'
)
COMMENT='C端调用房态相关接口日志表';

ALTER TABLE `t_interface_roomstate_log`
ADD COLUMN `optuser`  varchar(20) NULL COMMENT '操作人' AFTER `methodparams`,
ADD COLUMN `other1`  varchar(50) NULL COMMENT '预留字段' AFTER `createtime`,
ADD COLUMN `other2`  varchar(200) NULL COMMENT '预留字段' AFTER `other1`;

