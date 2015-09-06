CREATE TABLE `t_room_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pmsorderid` bigint(20) DEFAULT NULL COMMENT '外键b_pmsroomorder   id',
  `roomjson` varchar(2000) DEFAULT NULL COMMENT '入住信息json',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pmsorderid` (`pmsorderid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pms2.0锁房信息表';	