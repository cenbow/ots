package com.mk.pms.order.service;

import com.mk.ots.order.bean.PmsRoomOrder;

public interface PmsShiftService {
	public void shiftRoomForPromo(PmsRoomOrder pmsRoomOrder, boolean isChanged) throws Exception;
}
