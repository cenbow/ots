--p_payinfo表添加字段
ALTER TABLE `p_payinfo` ADD COLUMN `realcost` decimal(10,2) DEFAULT NULL COMMENT '实际下发乐住币金额'；