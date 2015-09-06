--  下发乐住币日志
DROP TABLE IF EXISTS `p_pmspay_log`;
CREATE TABLE `p_pmspay_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `orderid` bigint(20) NOT NULL COMMENT '订单id',
  `createtime` datetime NOT NULL COMMENT '下发乐住币时间',
  `lezhu` decimal(10,0) NOT NULL COMMENT '乐住币',
  `reason` varchar(255) DEFAULT NULL COMMENT '下发原因',
  `operator` bigint(20) NOT NULL COMMENT '操作人员',
  PRIMARY KEY (`id`)
)