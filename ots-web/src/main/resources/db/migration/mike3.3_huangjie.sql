

/*==============================================================*/
/* Table: T_REMIND                                              */
/*==============================================================*/
create table T_REMIND
(
   id                   bigint(20) not null auto_increment,
   mid                  bigint(20) comment 'mid',
   phone		varchar(100) comment '电话号码',
   content              text comment '提醒内容',
   title                varchar(100),
   url                  varchar(1000) comment 'url',
   type_id              bigint(20) comment '提醒类型id',
   remind_time          datetime comment '提醒时间',
   count                int comment '执行次数',
   execute_time         datetime comment '执行时间',
   status_code          varchar(2) comment '状态',
   create_time          datetime comment '创建时间',
   update_time          datetime comment '更新时间',
   primary key (id)
);

alter table T_REMIND comment '提醒记录remind';

/*==============================================================*/
/* Index: idx_remind_mid                                        */
/*==============================================================*/
create index idx_remind_mid on T_REMIND
(
   mid,
   type_id
);

/*==============================================================*/
/* Index: idx_remind_type                                       */
/*==============================================================*/
create index idx_remind_type on T_REMIND
(
   status_code,
   type_id
);


/*==============================================================*/
/* Table: T_REMIND_LOG                                          */
/*==============================================================*/
create table T_REMIND_LOG
(
   id                   bigint(20) not null auto_increment,
   remind_id            bigint(20),
   content              text,
   status_code           varchar(2) comment '状态 1、初始化 2、push 3、取消',
   create_time          datetime,
   descr                text,
   primary key (id)
);

alter table T_REMIND_LOG comment '提醒记录log';

/*==============================================================*/
/* Index: idx_remind_log_rid                                    */
/*==============================================================*/
create index idx_remind_log_rid on T_REMIND_LOG
(
   remind_id
);


/*==============================================================*/
/* Table: T_REMIND_TYPE                                         */
/*==============================================================*/
create table T_REMIND_TYPE
(
   id                   bigint(20) not null auto_increment,
   code                 varchar(2) comment '状态code',
   name                 varchar(100) comment '状态名称',
   content              text comment '提醒模板',
   url                  varchar(1000),
   title                varchar(100),
   remind_time          datetime comment '提醒时间',
   is_weixin             int(1) comment '是否发送微信',
   is_sms                int(1) comment '是否发送短信',
   is_push               int(1) comment '是否发送push',
   valid                int(1),
   create_time          datetime,
   create_by            varchar(50),
   update_time          datetime,
   update_by            varchar(50),
   primary key (id)
);

alter table T_REMIND_TYPE comment '提醒记录类型';


INSERT INTO T_REMIND_TYPE
(code,name,content,url,title,is_weixin,is_sms,is_push,
valid,create_time,create_by,update_time,update_by,remind_time)
VALUE
('10','特价开抢提醒','特价开抢提醒','www.imikeshareMessage-url-scheme://hoteldetail/','亲，距离您预约抢购的房间时间只剩5分钟，请打开眯客APP开抢哦！',0,0,1,1,SYSDATE(),'',SYSDATE(),'','2015-12-20 19:55:00');




update b_message_copywriter set copywriter = '#if($!cityCode==\'500000\')
    #if($!order.promoType==\'0\')
    预订成功
    恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。本次订单支付 ¥$!{order.totalprice}。眯客为您节省$!{roomCostSavePrice}元。查看订单每日20:00点后更多酒店4折起，入住后评论还有返现哦~
    #else
    预订成功
    恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。本次订单支付 ¥$!{order.totalprice}。眯客又为您优惠$!{mikeSavePrice}元。查看订单每日20:00点后推出更多超低特价房，入住后评论还有返现哦~
    #end
#else
预订成功
恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。本次订单支付 ¥ $!{order.totalprice}。#if($!onlineprcie)已使用$!{onlineprcie}元优惠券。#end #if($!count>0)您还有${count}张住房优惠券，下次APP在线支付可用。#end
#end' where id = 10;


update b_message_copywriter set copywriter = '#if($cityCode==\'500000\')
预订成功
恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。请于$!{begintimehour}点前尽快去酒店办理入住哦~需到店支付 ¥$!{order.totalprice}。地址：$!{detailAddr}电话：$!{hotelPhone}。每日20:00点后眯客酒店4折起~
#else
预订成功
恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。请于$!{begintimehour}点前尽快去酒店办理入住哦~需到店支付 ¥$!{order.totalprice}。地址：$!{detailAddr}电话：$!{hotelPhone}。
    #if($!count>0)您还有${count}张住房优惠券，下次APP在线支付可用。#end #if($!activeCount>0)参与活动获得更多优惠。#end
#end' where id = 14;

update b_message_copywriter set copywriter = '#if($cityCode==\'500000\')
预订成功
恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。请于$!{begintimehour}点前尽快去酒店办理入住哦~需到店支付 ¥$!{order.totalprice}。地址：$!{detailAddr}电话：$!{hotelPhone}。每日20:00点后眯客酒店4折起~
#else
预订成功
恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。请于$!{begintimehour}点前尽快去酒店办理入住哦~需到店支付 ¥$!{order.totalprice}。地址：$!{detailAddr}电话：$!{hotelPhone}。
    #if($!count>0)您还有${count}张住房优惠券，下次APP在线支付可用。#end #if($!activeCount>0)参与活动获得更多优惠。#end
#end' where id = 15;

update b_message_copywriter set copywriter = '#if($cityCode==\'500000\')
预订成功
恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。请于$!{begintimehour}点前尽快去酒店办理入住哦~需到店支付 ¥$!{order.totalprice}。地址：$!{detailAddr}电话：$!{hotelPhone}。每日20:00点后眯客酒店4折起~
#else
预订成功
恭喜您成功预定$!{order.hotelName}$!{order.roomName}房间，入住时间$!{begintime},离店时间$!{endtime}，共$!{order.daynumber}晚。请于$!{begintimehour}点前尽快去酒店办理入住哦~需到店支付 ¥$!{order.totalprice}。地址：$!{detailAddr}电话：$!{hotelPhone}。
    #if($!count>0)您还有${count}张住房优惠券，下次APP在线支付可用。#end
#end' where id = 16;