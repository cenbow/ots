ALTER TABLE `b_promotion` ADD COLUMN `expiretype` int COMMENT '时间限制类型';
ALTER TABLE `b_promotion` ADD COLUMN `expiredaynum` int COMMENT '有效天数';
ALTER TABLE `b_promotion` ADD COLUMN `effectivetype` int COMMENT '是否当天有效';


ALTER TABLE `b_activity` ADD COLUMN `gentype` int COMMENT '券生成类型';

select distinct activitiesid from b_promotion where   name ='新用户礼包'
select count(id) from b_promotion where activitiesid=0 and name ='新用户礼包'
update b_promotion set activitiesid = 1 where activitiesid = 0 and name = '新用户礼包'