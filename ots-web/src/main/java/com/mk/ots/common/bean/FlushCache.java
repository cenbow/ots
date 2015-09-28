package com.mk.ots.common.bean;

import java.util.Set;

/**
 *
 * @author shellingford
 * @version 2015年1月12日
 */
public class FlushCache {
	private Long hotelId;
	private Set<Long> roomTypeId;
	public Long getHotelId() {
		return hotelId;
	}
	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}
	public Set<Long> getRoomTypeId() {
		return roomTypeId;
	}
	public void setRoomTypeId(Set<Long> roomTypeId) {
		this.roomTypeId = roomTypeId;
	}
	
}
