drop table t_backmoney_rule;
drop table t_order_promo_pay_rule;

alter table b_otaorder drop column callVersion;
alter table b_otaroomorder drop column promoid;