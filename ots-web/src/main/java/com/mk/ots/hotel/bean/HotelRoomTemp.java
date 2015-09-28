package com.mk.ots.hotel.bean;

import java.math.BigDecimal;
import java.util.Map;

public class HotelRoomTemp {
	private  Long hotelID  ;
	private  Map<String, HotelRoomTempInfo> roomInfo ;
	private  BigDecimal distance  ;
	public HotelRoomTemp() {
	}
	public Long getHotelID() {
		return hotelID;
	}
	public void setHotelID(Long hotelID) {
		this.hotelID = hotelID;
	}
	public Map<String, HotelRoomTempInfo> getRoomInfo() {
		return roomInfo;
	}
	public void setRoomInfo(Map<String, HotelRoomTempInfo> roomInfo) {
		this.roomInfo = roomInfo;
	}
	public BigDecimal getDistance() {
		return distance;
	}
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}
	
	

}
