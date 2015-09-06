-- 优惠券表添加兑换码来源字段
ALTER TABLE `b_promotion` ADD COLUMN `sourcecdkey` VARCHAR(40) COMMENT '兑换码';
ALTER TABLE `b_promotion` ADD COLUMN `channelid` BIGINT COMMENT '兑换码渠道';

-- 设置允许发送push消息用户列表
INSERT INTO `ots`.`sy_config` (`skey`, `svalue`, `stype`, `id`) 
VALUES
  ('push_white_list', '41080,41082', 'sys', '124') ;
  
  CREATE TABLE `bms_active_user_bind` (
  `id` varchar(40) NOT NULL,
  `group_id` varchar(40) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `member_name` varchar(255) DEFAULT NULL,
  `member_phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_reference_22` (`group_id`),
  CONSTRAINT `fk_reference_22` FOREIGN KEY (`group_id`) REFERENCES `bms_user_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分组用户绑定';


 CREATE TABLE `bms_user_group` (
  `id` varchar(40) NOT NULL,
  `group_code` varchar(255) DEFAULT NULL COMMENT '分组编码',
  `group_name` datetime DEFAULT NULL,
  `s_flag` int(11) DEFAULT NULL,
  `user_code` varchar(40) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户分组';

-- 添加字段
 ALTER TABLE `ots`.`l_push_log`   
  ADD COLUMN `pushid` VARCHAR(50) NULL   COMMENT '推送id' AFTER `success`,
  ADD COLUMN `groupid` VARCHAR(50) NULL   COMMENT '组id' AFTER `pushid`;
  
  -- 插入组成员
INSERT INTO `ots`.`bms_active_user_bind` (
  `id`,
  `group_id`,
  `member_id`
) 
VALUES
  (
    '1',
    '1',
    '41081'
  ) ;

   -- 插入用户组 
INSERT INTO `ots`.`bms_user_group` (
  `id`
) 
VALUES
  (
    '1'
  ) ;

  

