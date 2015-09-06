package com.mk.ots.order.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Db;
import com.mk.ots.order.bean.OtaCheckInUser;
@Repository
public class CheckInUserDAO {

	public int delectOtaCheckInUserByRoomOrderId(Long roomOrderId) {
		return Db.update("delete from b_CheckinUser where OtaRoomOrderId = ?", roomOrderId);
	}
	
	public OtaCheckInUser findOtaCheckInUser(Long roomOrderId){
		return OtaCheckInUser.dao.findFirst("select * from b_CheckinUser where OtaRoomOrderId = ?", roomOrderId);
	}
	public List<OtaCheckInUser> findOtaCheckInUsers(Long otaOrderId){
		String sql = "select cu.* from b_checkinuser cu, b_otaorder o, b_otaroomorder ro where cu.otaroomorderid = ro.id and ro.otaorderid=o.id and o.id=?";
		return OtaCheckInUser.dao.find(sql, otaOrderId);
	}
	
	public List<OtaCheckInUser> findOtaCheckInUserList(Long roomOrderId){
		return OtaCheckInUser.dao.find("select * from b_CheckinUser where OtaRoomOrderId = ?", roomOrderId);
	}
}
