package com.mk.pms.order.event;


/**
 * @author zzy
 * com.mk.ots.order.event.PmsCalCacheEvent
 */
public class PmsCalCacheEvent{
	private final Long _hotelId;
	private final Long _roomTypeId;

	public PmsCalCacheEvent(Long hotelId,Long roomTypeId) {
		this._hotelId = hotelId;
		this._roomTypeId = roomTypeId;
	}
	
	public Long getHotel() {
		return _hotelId;
	}
	public Long getRoomTypeId() {
		return _roomTypeId;
	}
}