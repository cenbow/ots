insert into sy_config (id, skey, svalue, stype) values (108, 'SMS_SIGNATURE', '【眯客】', 'sys');
insert into sy_config (id, skey, svalue, stype) values (109, 'refundrule', '{"ONE_DAY":"当天订单不能退款", "MORE_DAY":"入住前一天18:00之前可以退款"}', 'mikeweb');
insert into sy_config (id, skey, svalue, stype) values (110, 'lastrefundtime', '180000', 'mikeweb');
insert into sy_config (skey, svalue, stype) VALUES ('QIKE_TODAY_LIMITNUM', '0', 'mikeweb');
insert into sy_config (skey, svalue, stype) VALUES ('QIKE_MONTH_LIMITNUM', '4', 'mikeweb');
insert into sy_config (skey, svalue, stype) VALUES ('WX_TEST_DEBUG', '', 'mikeweb');

--1. 用户表
	ALTER TABLE `u_member` ADD COLUMN `channelid` varchar(255) COMMENT '设备ID';
	ALTER TABLE `u_member` ADD COLUMN `devicetype` varchar(255) COMMENT 'app类型';
	ALTER TABLE `u_member` ADD COLUMN `marketsource` varchar(255) COMMENT '市场来源';
	ALTER TABLE `u_member` ADD COLUMN `appversion` varchar(255) COMMENT 'app版本';
	ALTER TABLE `u_member` ADD COLUMN `ostype` varchar(255) COMMENT '系统类型';
	ALTER TABLE `u_member` ADD COLUMN `osver` varchar(255) COMMENT '系统版本';
	ALTER TABLE `u_member` ADD COLUMN `weixinname` varchar(255) COMMENT ' 微信用户昵称';
	ALTER TABLE `u_member` ADD COLUMN `comefrom` varchar(255) COMMENT ' 来源人id';
	ALTER TABLE `u_member` ADD COLUMN `comefromtype` varchar(255) COMMENT '业务场景:QK 切客场景; SC	市场; HD	活动';
	ALTER TABLE `u_member` ADD COLUMN `hotelid` bigint(255) COMMENT '注册酒店';
	ALTER TABLE `u_member` ADD COLUMN `unionid` varchar(255) COMMENT '微信id';
--2. app状态推送表
	CREATE TABLE `u_appstatus` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	  `sysno` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '系统号\n',
	  `mid` bigint(20) DEFAULT NULL COMMENT '用户\n',
	  `phone` varchar(45) COLLATE utf8_bin DEFAULT NULL COMMENT '手机号\n',
	  `userlongitude` varchar(45) COLLATE utf8_bin DEFAULT NULL COMMENT '用户坐标(经度)\n',
	  `userlatitude` varchar(45) COLLATE utf8_bin DEFAULT NULL COMMENT '用户坐标(纬度)\n',
	  `runningstatus` int(11) DEFAULT NULL COMMENT 'app开启状态:必填1-前台；2-后台',
	  `runningpage` varchar(45) COLLATE utf8_bin DEFAULT NULL COMMENT '打开页表示, 用于记录用户当前打开的页面',
	  `createtime` datetime DEFAULT NULL,
	  PRIMARY KEY (`id`)
	) 

--3. token
	DELETE t1 FROM u_token t1 INNER JOIN (
	    SELECT
	    mid
	    FROM
	        u_token
	    GROUP BY
	        mid,
	        type
	    HAVING
	        count(id) > 1
	)  t2 ON t1.mid = t2.mid
	ALTER TABLE `u_token` DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD UNIQUE `uq_mid_type` (mid, type);

--4. 领券日志表
	CREATE TABLE `u_promotion_log` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	  `mid` bigint(20) DEFAULT NULL COMMENT '用户编码',
	  `activeid` bigint(20) DEFAULT NULL COMMENT '活动编码',
	  `promoinstanceid` bigint(20) DEFAULT NULL COMMENT '券定义实例编码',
	  `promotionid` bigint(20) DEFAULT NULL,
	  `noticetype` varchar(255) DEFAULT NULL COMMENT '通知方式',
	  `success` char(50) DEFAULT NULL COMMENT '成功状态',
	  `errormsg` varchar(255) DEFAULT NULL COMMENT '失败原因',
	  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
	  PRIMARY KEY (`id`)
	) 

--5. 优惠券活动
	CREATE TABLE `b_activity` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT,
	  `title` varchar(255) NOT NULL,
	  `detail` varchar(255) DEFAULT NULL,
	  `description` varchar(255) DEFAULT NULL,
	  `type` varchar(255) DEFAULT NULL,
	  `begintime` datetime NOT NULL,
	  `endtime` datetime NOT NULL,
	  `limitget` int(11) NOT NULL,
	  `hotel` varchar(255) DEFAULT NULL,
	  `banner` varchar(255) DEFAULT NULL,
	  PRIMARY KEY (`id`)
	) 
	ALTER TABLE `b_activity` DROP COLUMN `banner`, ADD COLUMN `banner` varchar(255) COMMENT 'banner图片' AFTER `hotel`;

--6. push消息记录表
	CREATE TABLE `l_push_log` (
	  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键编码',
	  `title` varchar(500) COLLATE utf8_bin NOT NULL COMMENT 'push标题',
	  `content` varchar(4000) COLLATE utf8_bin NOT NULL COMMENT 'push内容',
	  `type` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'push类型：1-用户消息；2-广播消息',
	  `time` datetime NOT NULL COMMENT 'push时间',
	  `readstatus` char(50) COLLATE utf8_bin DEFAULT NULL COMMENT '消息状态:1未读；2已读',
	  `mid` bigint(20) NOT NULL COMMENT '用户id',
	  `phone` varchar(255) COLLATE utf8_bin DEFAULT NULL,
	  `deviceno` varchar(45) COLLATE utf8_bin NOT NULL COMMENT ' 用户设备号',
	  `devicetype` varchar(40) COLLATE utf8_bin DEFAULT NULL COMMENT '用户设备类型',
	  `fromsource` varchar(60) COLLATE utf8_bin DEFAULT NULL COMMENT '发送来源：H端,PHP端',
	  `fromip` varchar(40) COLLATE utf8_bin DEFAULT NULL COMMENT '发送来源ip',
	  PRIMARY KEY (`id`)
	) 

--7. 调度表
	CREATE TABLE `sy_schedule` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT,
	  `name` varchar(255) NOT NULL,
	  `startdate` date NOT NULL,
	  `enddate` date NOT NULL,
	  `script` varchar(255) NOT NULL,
	  `expression` varchar(255) NOT NULL,
	  `type` varchar(255) DEFAULT NULL,
	  `active` char(50) DEFAULT NULL,
	  PRIMARY KEY (`id`)
	) 

--8. 系统调度表(quartz)
	drop table qrtz_fired_triggers;
	drop table qrtz_paused_trigger_grps;
	drop table qrtz_scheduler_state;
	drop table qrtz_locks;
	drop table qrtz_simple_triggers;
	drop table qrtz_simprop_triggers;
	drop table qrtz_cron_triggers;
	drop table qrtz_blob_triggers;
	drop table qrtz_triggers;
	drop table qrtz_job_details;
	drop table qrtz_calendars;

	create table qrtz_job_details (
		sched_name varchar(120) not null,
		job_name varchar(200) not null,
		job_group varchar(200) not null,
		description varchar(250) ,
		job_class_name varchar(250) not null,
		is_durable varchar(5) not null,
		is_nonconcurrent varchar(5) not null,
		is_update_data varchar(5) not null,
		requests_recovery varchar(5) not null,
		job_data blob,
		primary key (sched_name,job_name,job_group)
	);

	create table qrtz_triggers(
		sched_name varchar(120) not null,
		trigger_name varchar(200) not null,
		trigger_group varchar(200) not null,
		job_name varchar(200) not null,
		job_group varchar(200) not null,
		description varchar(250),
		next_fire_time bigint,
		prev_fire_time bigint,
		priority integer,
		trigger_state varchar(16) not null,
		trigger_type varchar(8) not null,
		start_time bigint not null,
		end_time bigint,
		calendar_name varchar(200),
		misfire_instr smallint,
		job_data blob,
		primary key (sched_name,trigger_name,trigger_group),
		foreign key (sched_name,job_name,job_group) references qrtz_job_details(sched_name,job_name,job_group)
	);

	create table qrtz_simple_triggers(
		sched_name varchar(120) not null,
		trigger_name varchar(200) not null,
		trigger_group varchar(200) not null,
		repeat_count bigint not null,
		repeat_interval bigint not null,
		times_triggered bigint not null,
		primary key (sched_name,trigger_name,trigger_group),
		foreign key (sched_name,trigger_name,trigger_group) references qrtz_triggers(sched_name,trigger_name,trigger_group)
	);

	create table qrtz_cron_triggers(
		sched_name varchar(120) not null,
		trigger_name varchar(200) not null,
		trigger_group varchar(200) not null,
		cron_expression varchar(120) not null,
		time_zone_id varchar(80),
		primary key (sched_name,trigger_name,trigger_group),
		foreign key (sched_name,trigger_name,trigger_group) references qrtz_triggers(sched_name,trigger_name,trigger_group)
	);

	create table qrtz_simprop_triggers (          
	    sched_name varchar(120) not null,
	    trigger_name varchar(200) not null,
	    trigger_group varchar(200) not null,
	    str_prop_1 varchar(512),
	    str_prop_2 varchar(512),
	    str_prop_3 varchar(512),
	    int_prop_1 int,
	    int_prop_2 int,
	    long_prop_1 bigint,
	    long_prop_2 bigint,
	    dec_prop_1 numeric(13,4),
	    dec_prop_2 numeric(13,4),
	    bool_prop_1 varchar(5),
	    bool_prop_2 varchar(5),
	    primary key (sched_name,trigger_name,trigger_group),
	    foreign key (sched_name,trigger_name,trigger_group) 
	    references qrtz_triggers(sched_name,trigger_name,trigger_group)
	);

	create table qrtz_blob_triggers(
		sched_name varchar(120) not null,
		trigger_name varchar(200) not null,
		trigger_group varchar(200) not null,
		blob_data blob,
		primary key (sched_name,trigger_name,trigger_group),
		foreign key (sched_name,trigger_name,trigger_group) references qrtz_triggers(sched_name,trigger_name,trigger_group)
	);

	create table qrtz_calendars(
		sched_name varchar(120) not null,
		calendar_name varchar(200) not null,
		calendar blob not null,
		primary key (sched_name,calendar_name)
	);

	create table qrtz_paused_trigger_grps(
	    sched_name varchar(120) not null,
	    trigger_group varchar(200) not null,
		primary key (sched_name,trigger_group)
	);

	create table qrtz_fired_triggers(
		sched_name varchar(120) not null,
		entry_id varchar(95) not null,
		trigger_name varchar(200) not null,
		trigger_group varchar(200) not null,
		instance_name varchar(200) not null,
		fired_time bigint not null,
		sched_time bigint not null,
		priority integer not null,
		state varchar(16) not null,
		job_name varchar(200),
		job_group varchar(200),
		is_nonconcurrent varchar(5),
		requests_recovery varchar(5),
		primary key (sched_name,entry_id)
	);

	create table qrtz_scheduler_state (
	    sched_name varchar(120) not null,
	    instance_name varchar(200) not null,
	    last_checkin_time bigint not null,
	    checkin_interval bigint not null,
		primary key (sched_name,instance_name)
	);

	create table qrtz_locks (
	    sched_name varchar(120) not null,
	    lock_name varchar(40) not null,
		primary key (sched_name,lock_name)
	);
	
	--8.  订单状态日志表
	drop TABLE `b_log_order` ;
	CREATE TABLE `b_log_order` (
		`id` bigint AUTO_INCREMENT,
		`logtime` datetime,
		`orderid` bigint ,
		`oldstatus` varchar(255) NOT NULL,
		`newstatus` varchar(255) NOT NULL,
		`note` varchar(255) NOT NULL,
		PRIMARY KEY (`id`)
	);
