
CREATE INDEX index_otaorder_mid ON b_otaorder (`Mid`) ;
CREATE INDEX inx_otaorder_begintime ON b_otaorder (`Begintime`) ;
CREATE INDEX inx_otaorder_endtime ON b_otaorder (`Endtime`) ;
CREATE INDEX inx_otaorder_spreadUser ON b_otaorder (`spreadUser`) ;
CREATE INDEX inx_otaorder_orderStatus ON b_otaorder (`HotelId`,`OrderStatus`) ;

CREATE INDEX IX_b_otaroomorder_hotelid ON b_otaroomorder (`Hotelid`) ;
CREATE INDEX IX_b_otaroomorder_hotelpms ON b_otaroomorder (`HotelPms`) ;
CREATE INDEX IX_b_otaroomorder_mid ON b_otaroomorder (`Mid`) ;
CREATE INDEX IX_b_otaroomorder_otaorderid ON b_otaroomorder (`OtaOrderId`) ;
CREATE INDEX IX_b_otaroomorder_PMSRoomOrderNo ON b_otaroomorder (`PMSRoomOrderNo`) ;

CREATE INDEX IX_b_pmsorder_hotelid ON b_pmsorder (`Hotelid`) ;
CREATE INDEX IX_b_pmsorder_hotelpms ON b_pmsorder (`Hotelpms`) ;
CREATE INDEX IX_b_pmsorder_pmsorderno ON b_pmsorder (`Pmsorderno`) ;
CREATE INDEX IX_b_pmsorder_pmsroomtypeorderno ON b_pmsorder (`PmsRoomTypeOrderNo`) ;
CREATE INDEX IX_b_pmsorder_roomtypeid ON b_pmsorder (`RoomTypeId`) ;
CREATE INDEX IX_b_pmsorder_roomtypepms ON b_pmsorder (`RoomTypePms`) ;
 
CREATE INDEX ix_oro_status ON b_pmsroomorder (`Status`);
CREATE INDEX ix_oro_ordertype ON b_pmsroomorder (`Ordertype`);
CREATE INDEX ix_oro_endtime ON b_pmsroomorder (`Endtime`);
CREATE INDEX ix_oro_begintime ON b_pmsroomorder (`Begintime`);
CREATE INDEX ix_oro_roomtypeid ON b_pmsroomorder (`RoomTypeId`);
CREATE INDEX ix_pmsroomorder_hotelid ON b_pmsroomorder (`Hotelid`);
CREATE INDEX ix_pmsroomorder_id ON b_pmsroomorder (`id`);
CREATE INDEX ix_pmsroomorder_PmsRoomOrderNo ON b_pmsroomorder (`PmsRoomOrderNo`);

create index IX_b_otaroomprice_otaroomorderid on b_otaroomprice (`OtaRoomOrderId`);

create index ix_h_hotel_orders_daily_hotelid on h_hotel_orders_daily (`hotelId`);
create index ix_h_hotel_orders_daily_date on h_hotel_orders_daily (`date`);

create index inx_billdetail_hotelId on t_bill_detail (`hotelId`);
create index inx_billdetail_mid on t_bill_detail (`mId`);
create index inx_billdetail_otaorderId on t_bill_detail (`hotelId`);

create index inx_cb_checkdate on t_checker_bill (`checkdate`);
create index inx_cb_checkerid on t_checker_bill (`checkerId`);
create index inx_cb_hotelId on t_checker_bill (`hotelId`);
create index inx_cb_otaorderid on t_checker_bill (`OtaOrderId`);



------LYN 索引
ALTER  TABLE  b_costtemp_310000  ADD  INDEX index_costtemp3100000_roomtype ( roomtypeid,time);
ALTER  TABLE  b_costtemp_310000  ADD  INDEX index_costtemp3100000_hotelid ( hotelid,time);
alter table t_hotel_score add index index_score(hotelid,grade);
alter table t_hotel_subject add index index_subject(hotelid);
alter table t_hotel_subject_his add index index_subject_his(hotelid,createtime);
alter table t_hotel_subject_mx add index index_subject_mx(hotelid,roomtypeid,Createtime);
alter table t_subject_c_his add index index_subject_c_his(subjectid,createdate);



-----支付
create index inx_pay_orderid  on p_pay (`orderid`);
create index inx_payinfo_payid  on p_payinfo (`payid`);
create index inx_p_orderlog_payid  on p_orderlog (`payid`);








