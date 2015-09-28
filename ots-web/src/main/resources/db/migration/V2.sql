ALTER TABLE `p_orderlog`   
ADD COLUMN `pmssend` bigint COMMENT '下发状态', 
ADD COLUMN `pmssendtime` datetime COMMENT '下发时间',
ADD COLUMN `pmsrefund` bigint COMMENT '退还状态', 
ADD COLUMN `pmsrefundtime` datetime COMMENT '退还时间';