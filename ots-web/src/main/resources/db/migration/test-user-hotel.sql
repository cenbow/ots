CREATE TABLE `t_test_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `backupfield1` varchar(100) DEFAULT NULL COMMENT '保留字段--设备号',
  `backupfield2` varchar(20) DEFAULT NULL COMMENT '保留字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测试账号表';

CREATE TABLE `t_test_hotel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `hotelid` bigint(20) NOT NULL COMMENT '酒店id',
  `backupfield` varchar(20) DEFAULT NULL COMMENT '保留字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='测试酒店白名单';

