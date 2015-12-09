package com.mk.pms.order.service;

import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.pms.room.bean.RoomRepairPo;

public interface PmsShiftService {
	public void shiftRoomForPromo(PmsRoomOrder pmsRoomOrder, boolean isChanged) throws Exception;
	public void shiftRoomForPromo(RoomRepairPo roomRepairPo) throws Exception;
}
