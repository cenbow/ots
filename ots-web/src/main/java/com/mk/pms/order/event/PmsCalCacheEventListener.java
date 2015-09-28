package com.mk.pms.order.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.mk.framework.AppUtils;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.order.service.OrderServiceImpl;

/**
 * @author zzy com.mk.ots.order.event.EventListener
 */
@Component
public class PmsCalCacheEventListener {
	private Logger logger = LoggerFactory.getLogger(PmsCalCacheEventListener.class);

	public Long hotelid = null;
	public Long roomTypeId = null;

	@Autowired
	private RoomService roomService;

	@Subscribe
	public void listen(PmsCalCacheEvent event) {
		hotelid = event.getHotel();
		try {
			logger.info("OTSMessage::PmsCalCacheEventListener---start--hotelid:" + hotelid);
			roomTypeId = event.getRoomTypeId();

			if (hotelid == null) {
				return;
			}
			if (roomTypeId == null) {
				return;
			}

			if (roomService == null) {
				roomService = AppUtils.getBean(RoomService.class);
			}
			// OrderServiceImpl orderServ =
			// AppUtils.getBean(OrderServiceImpl.class);
			// 调用全量更新，覆盖ots的房态数据
			long s = System.currentTimeMillis();
			logger.info("OTSMessage::PmsCalCacheEventListener::resetRoomStateFromPMS");
//			roomService.resetRoomStateFromPMS(hotelid, roomTypeId);
			/*roomService.calCacheByHotelIdAndRoomType(hotelid, roomTypeId);*/
			logger.info("OTSMessage::PmsCalCacheEventListener:calCacheByHotelIdAndRoomType::ok::耗时{}", System.currentTimeMillis() - s);

		} catch (Exception e) {
			logger.info("OTSMessage::PmsCalCacheEventListener:calCacheByHotelIdAndRoomType--error"+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
