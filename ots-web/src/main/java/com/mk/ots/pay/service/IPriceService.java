package com.mk.ots.pay.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.mk.ots.hotel.bean.RoomTypePriceBean;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.OtaRoomPrice;

/**
 *
 */
public interface IPriceService {
	void saveOtaRoomPriceByOtaRoomOrder(OtaRoomOrder roomOrder, Map<String, BigDecimal> map);
	
	void saveOtaRoomPriceByOtaRoomOrder(OtaRoomOrder roomOrder, List<RoomTypePriceBean> roomtypeList, boolean flag);

	void updateOtaRoomPriceByOtaRoomOrder(OtaRoomOrder roomOrder, Map<String, BigDecimal> map);

	List<OtaRoomPrice> findOtaRoomPriceByOrder(OtaOrder order);

}
