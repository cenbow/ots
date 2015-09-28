#用户收藏酒店表
CREATE TABLE b_hotel_collection (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  hotelid bigint(20) NOT NULL COMMENT '酒店id',
  mid bigint(20) NOT NULL COMMENT '用户编号',
  collecttime datetime DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (id),
  KEY index_hotelid (hotelid) USING BTREE,
  KEY index_mid (mid) USING BTREE,
  KEY index_collecttime (collecttime) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户收藏酒店表';

# 添加本订单使用账户(金额)支付的金额
ALTER TABLE p_orderlog
ADD COLUMN accountcost
  decimal(10,2) NULL COMMENT '此订单使用账户(金额)支付的金额' AFTER qiekeIncome;

 
#评价添加是否系统默认评价字段(T/F)
ALTER TABLE t_hotel_score 
ADD COLUMN isdefault VARCHAR(1) NULL DEFAULT 'F' AFTER USER_NAME;

#评价 添加是否返现字段
ALTER TABLE t_hotel_score 
ADD COLUMN iscashbacked VARCHAR(1) NULL DEFAULT 'F' AFTER isdefault;

#评价 添加返现字段
ALTER TABLE t_hotel_score 
ADD COLUMN backcashcost DECIMAL(10,2) NULL DEFAULT 0 AFTER isbackcash;


#增加优惠券描述
alter table b_promotion add COLUMN description 
  varchar(1000) DEFAULT NULL COMMENT '优惠券描述' AFTER name ;
  
#添加 能用的返现，订单返现
ALTER TABLE b_otaorder
ADD COLUMN availablemoney  decimal(10,2) NULL DEFAULT 0 AFTER userlatitude,
ADD COLUMN cashback  decimal(10,2) NULL AFTER availablemoney;

#订单表添加城市编码字段
ALTER TABLE b_otaorder
ADD COLUMN cityCode  varchar(50) NULL AFTER cashback;


#位置区域搜索类型表
CREATE TABLE s_positiontype (
  id BIGINT NOT NULL AUTO_INCREMENT,
  typename VARCHAR(45) NOT NULL,
  visible VARCHAR(1) NULL DEFAULT 'T',
  PRIMARY KEY (id),
  UNIQUE INDEX id_UNIQUE (id ASC);  
#初始化位置区域类型
INSERT INTO s_positiontype (id, typename) VALUES ('1', '商圈');
INSERT INTO s_positiontype (id, typename) VALUES ('2', '机场车站');
INSERT INTO s_positiontype (id, typename) VALUES ('3', '地铁路线 ');
INSERT INTO s_positiontype (id, typename) VALUES ('4', '行政区');
INSERT INTO s_positiontype (id, typename) VALUES ('5', '景点');
INSERT INTO s_positiontype (id, typename) VALUES ('6', '医院');
INSERT INTO s_positiontype (id, typename) VALUES ('7', '高校');
#位置区域类型表添加字段
ALTER TABLE s_positiontype 
ADD COLUMN citycode VARCHAR(45) NULL AFTER visible,
ADD COLUMN pttxt VARCHAR(45) NULL AFTER citycode;
#位置区域表
CREATE TABLE s_position (
  id BIGINT NOT NULL AUTO_INCREMENT,
  pname VARCHAR(45) NULL,
  ptype VARCHAR(45) NULL,
  coordinates VARCHAR(500) NULL,
  ptxt VARCHAR(200) NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX id_UNIQUE (id ASC));
#位置区域表添加字段
ALTER TABLE s_position 
ADD COLUMN citycode VARCHAR(45) NULL AFTER ptxt;


#==============================================================================================
# 眯客3.0 钱包相关表sql author: zhangliang

CREATE TABLE `u_wallet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键编码',
  `mid` bigint(20) NOT NULL COMMENT '用户编码',
  `balance` decimal(20,2) NOT NULL COMMENT '收支金额',
  `lastmodify` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `mid` (`mid`) 
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '用户红包定义表';

CREATE TABLE `u_wallet_cashflow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mid` bigint(20) NOT NULL COMMENT '用户编码',
  `cashflowtype` int(20) NOT NULL COMMENT '业务类别(1: 订单返现入; 2: 酒店点评返现入; 3: 订单消费入; 4: 订单返现退; 5: 酒店点评返现退; 6: 订单消费退)',
  `price` decimal(20,2) NOT NULL COMMENT '乐住币金额',
  `sourceid` bigint(20) NOT NULL COMMENT '业务数据id',
  `createtime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `mid` (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '用户红包现金流记录表';

# 用户领券记录
ALTER TABLE `u_promotion_log` ADD COLUMN `hardwarecode` VARCHAR(30)
AFTER `createtime`;

# 用户兑券记录
ALTER TABLE `u_active_cdkey_log` ADD COLUMN `hardwarecode` VARCHAR(30)
AFTER `createtime`;
# 眯客3.0 钱包相关表sql author: zhangliang //END
#==============================================================================================

#钱包结算
ALTER TABLE b_bill_orders
ADD COLUMN availablemoney  decimal(10,2) NULL COMMENT '钱包金额' AFTER statusTime;
ALTER TABLE b_bill_confirm_everyday
ADD COLUMN availablemoney  decimal(10,2) NULL COMMENT '钱包金额' AFTER billCost;
ALTER TABLE b_bill_confirm_check
ADD COLUMN availablemoney  decimal(10,2) NULL COMMENT '钱包金额' AFTER financeStatus;





# 后置的用户账户的金额
ALTER TABLE p_orderlog
ADD COLUMN realaccountcost  decimal(10,2) NULL COMMENT '此订单使用账户(金额)支付的金额' AFTER accountcost;


# 后置的订单支付的金额
ALTER TABLE p_orderlog
ADD COLUMN realallcost  decimal(10,2) NULL COMMENT '此订单使用账户(金额)支付的金额' AFTER allcost;



#pms2.0平日门市价表
CREATE TABLE t_rack_rate (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  ehotelid bigint(20) NOT NULL COMMENT 'e_hotel id',
  roomtypeid bigint(20) NOT NULL COMMENT 't_roomtype id',
  price decimal(10,2) NOT NULL COMMENT '门市价',
  createtime datetime DEFAULT NULL COMMENT '创建时间',
  createuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  updatetime datetime DEFAULT NULL COMMENT '更新时间',
  updateuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  PRIMARY KEY (id),
  KEY index_ehotelid (ehotelid) USING BTREE,
  KEY index_roomtypeid (roomtypeid) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pms2.0平日门市价表';

#pms2.0周末门市价表
CREATE TABLE t_weekend_rate (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  ehotelid bigint(20) NOT NULL COMMENT 'e_hotel id',
  roomtypeid bigint(20) NOT NULL COMMENT 't_roomtype id',
  week int(2) NOT NULL COMMENT ' //星期  星期日0 星期一 1 星期二 2 星期三 3 星期四 4 星期五 5 星期六 6',
  price decimal(10,2) DEFAULT NULL COMMENT '周末价',
  createtime datetime DEFAULT NULL COMMENT '创建时间',
  createuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  updatetime datetime DEFAULT NULL COMMENT '更新时间',
  updateuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  PRIMARY KEY (id),
  KEY index_ehotelid (ehotelid) USING BTREE,
  KEY index_roomtypeid (roomtypeid) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pms2.0周末门市价表';

#pms2.0特殊门市价表
CREATE TABLE t_daily_rate (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  ehotelid bigint(20) NOT NULL COMMENT 'e_hotel id',
  roomtypeid bigint(20) NOT NULL COMMENT 't_roomtype id',
  day bigint(10) NOT NULL COMMENT '日期  20150101',
  price decimal(10,2) NOT NULL COMMENT '门市价',
  createtime datetime DEFAULT NULL COMMENT '创建时间',
  createuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  updatetime datetime DEFAULT NULL COMMENT '更新时间',
  updateuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  PRIMARY KEY (id),
  KEY index_ehotelid (ehotelid) USING BTREE,
  KEY index_roomtypeid (roomtypeid) USING BTREE,
  KEY index_day (day) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pms2.0特殊门市价表';

#pms2.0平日门市价表e表
CREATE TABLE e_rack_rate (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  ehotelid bigint(20) NOT NULL COMMENT 'e_hotel id',
  roomtypeid bigint(20) NOT NULL COMMENT 't_roomtype id',
  price decimal(10,2) NOT NULL COMMENT '门市价',
  createtime datetime DEFAULT NULL COMMENT '创建时间',
  createuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  updatetime datetime DEFAULT NULL COMMENT '更新时间',
  updateuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  PRIMARY KEY (id),
  KEY index_ehotelid (ehotelid) USING BTREE,
  KEY index_roomtypeid (roomtypeid) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pms2.0平日门市价表';

#pms2.0周末门市价表e表
CREATE TABLE e_weekend_rate (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  ehotelid bigint(20) NOT NULL COMMENT 'e_hotel id',
  roomtypeid bigint(20) NOT NULL COMMENT 't_roomtype id',
  week int(2) NOT NULL COMMENT ' //星期  星期日0 星期一 1 星期二 2 星期三 3 星期四 4 星期五 5 星期六 6',
  price decimal(10,2) DEFAULT NULL COMMENT '周末价',
  createtime datetime DEFAULT NULL COMMENT '创建时间',
  createuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  updatetime datetime DEFAULT NULL COMMENT '更新时间',
  updateuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  PRIMARY KEY (id),
  KEY index_ehotelid (ehotelid) USING BTREE,
  KEY index_roomtypeid (roomtypeid) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pms2.0周末门市价表';


#pms2.0特殊门市价表e表
CREATE TABLE e_daily_rate (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  ehotelid bigint(20) NOT NULL COMMENT 'e_hotel id',
  roomtypeid bigint(20) NOT NULL COMMENT 't_roomtype id',
  day bigint(10) NOT NULL COMMENT '日期  20150101',
  price decimal(10,2) NOT NULL COMMENT '门市价',
  createtime datetime DEFAULT NULL COMMENT '创建时间',
  createuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  updatetime datetime DEFAULT NULL COMMENT '更新时间',
  updateuser varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  PRIMARY KEY (id),
  KEY index_ehotelid (ehotelid) USING BTREE,
  KEY index_roomtypeid (roomtypeid) USING BTREE,
  KEY index_day (day) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pms2.0特殊门市价表';


#pms2.0设置mike价T表
CREATE TABLE `t_setting_mike_rate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ehotelid` bigint(20) NOT NULL COMMENT '酒店 id',
  `roomtypeid` bigint(20) NOT NULL COMMENT '房间 id',
  `settingtype` int(11) DEFAULT NULL COMMENT '设置类型 1-全局设置  2-指定日期设置',
  `settingtime` int(11) DEFAULT NULL COMMENT '创建时间',
  `subprice` decimal(10,2) DEFAULT NULL COMMENT '下浮比率',
  `subper` decimal(10,2) DEFAULT NULL COMMENT '下浮金额',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `createuser` varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `updateuser` varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  PRIMARY KEY (`id`),
  KEY `index_ehotelid` (`ehotelid`) USING BTREE,
  KEY `index_settingtype` (`settingtype`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='pms2.0设置mike价T表';

#pms2.0设置mike价E表
CREATE TABLE `e_setting_mike_rate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ehotelid` bigint(20) NOT NULL COMMENT '酒店 id',
  `roomtypeid` bigint(20) NOT NULL COMMENT '房间 id',
  `settingtype` int(11) DEFAULT NULL COMMENT '设置类型 1-全局设置  2-指定日期设置',
  `settingtime` int(11) DEFAULT NULL COMMENT '创建时间',
  `subprice` decimal(10,2) DEFAULT NULL COMMENT '下浮比率',
  `subper` decimal(10,2) DEFAULT NULL COMMENT '下浮金额',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `createuser` varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `updateuser` varchar(30) DEFAULT NULL COMMENT '创建人 (pms或者hms)',
  PRIMARY KEY (`id`),
  KEY `index_ehotelid` (`ehotelid`) USING BTREE,
  KEY `index_settingtype` (`settingtype`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='pms2.0设置mike价E表';

#订单是否已经返现
ALTER TABLE b_otaorder
ADD COLUMN isReceiveCashBack  int(5) NULL DEFAULT 0 COMMENT '是否已经领取返现' AFTER cityCode;
#数据初始化
update b_otaorder set availablemoney=0
where availablemoney is null;



UPDATE b_promotion SET DESCRIPTION='限App在线支付使用' WHERE onlineprice>0 AND platformtype=1;
UPDATE b_promotion SET DESCRIPTION='限在线支付使用' WHERE onlineprice>0 AND platformtype=3;
UPDATE b_promotion SET DESCRIPTION='限App到付使用' WHERE offlineprice>0 AND platformtype=1;
UPDATE b_promotion SET DESCRIPTION='限到付使用' WHERE offlineprice>0 AND platformtype=3;
UPDATE b_promotion SET DESCRIPTION=CONCAT(DESCRIPTION,'|满',150,'元可用') WHERE activitiesid IN (14) AND onlineprice=100; 
UPDATE b_promotion SET DESCRIPTION=CONCAT(DESCRIPTION,'|满',150,'元可用') WHERE activitiesid IN (18) AND onlineprice=77; 
UPDATE b_promotion SET DESCRIPTION='限在线支付使用' WHERE onlineprice is null and offlineprice is null	;


    ALTER TABLE `b_logininfo`   
  ADD COLUMN `security` VARCHAR(1024) NULL   COMMENT '安全信息' AFTER `createTime`;
  
UPDATE p_orderlog SET realallcost = allcost;  
   