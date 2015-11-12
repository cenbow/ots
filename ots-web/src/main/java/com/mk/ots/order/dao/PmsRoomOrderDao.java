package com.mk.ots.order.dao;

import com.mk.ots.order.bean.PmsRoomOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PmsRoomOrderDao {

	public PmsRoomOrder getPmsRoomOrder(String pmsRoomOrderNo, Long hotelId){
		StringBuffer sql = new StringBuffer("select * from b_pmsroomorder ox where ox.PmsRoomOrderNo = ? and ox.Hotelid = ? ");
		return PmsRoomOrder.dao.find(sql.toString(), pmsRoomOrderNo, hotelId).get(0);
	}

	public PmsRoomOrder getCheckInTime(Long otaorderid){
		return PmsRoomOrder.dao.findFirst("select bp.* from b_pmsroomorder bp, b_otaorder bo, b_otaroomorder bor where bo.id = ? and bo.id = bor.otaorderid and bor.hotelid = bp.hotelid and bor.pmsroomorderno = bp.pmsroomorderno", otaorderid);
	}

	public List<PmsRoomOrder> getPmsRoomOrderByCheckInTime(String checkInBeginTime, String checkInEndTime, Long limtBegin, Long limtEnd){
		return PmsRoomOrder.dao.find("select bo.id as orderId,bp.* from b_pmsroomorder bp, b_otaorder bo, b_otaroomorder bor where bp.checkintime >= ? and bp.checkintime < ? and bo.id = bor.otaorderid and bor.hotelid = bp.hotelid and bor.pmsroomorderno = bp.pmsroomorderno and bo.orderstatus in (180, 190, 200) order by bp.checkintime, bp.id limit ?, ?",
				checkInBeginTime, checkInEndTime, limtBegin, limtEnd);
	}
}
