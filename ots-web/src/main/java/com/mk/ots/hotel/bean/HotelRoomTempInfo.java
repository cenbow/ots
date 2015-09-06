package com.mk.ots.hotel.bean;

import java.math.BigDecimal;

public class HotelRoomTempInfo {
	private Long hotelID;
	private Long roomID;
	private String roomName;
	private Long roomTypeId;
	private String roomTypeName;
	private BigDecimal price;;
	private Long bedId;
	private String bedName;
	private Long daynum ;
	private String obj;	// findRoomByHotelAndRoomType方法里是天数Long
	public Long getHotelID() {
		return hotelID;
	}
	public void setHotelID(Long hotelID) {
		this.hotelID = hotelID;
	}
	public Long getRoomID() {
		return roomID;
	}
	public void setRoomID(Long roomID) {
		this.roomID = roomID;
	}
	public Long getRoomTypeId() {
		return roomTypeId;
	}
	public void setRoomTypeId(Long roomTypeId) {
		this.roomTypeId = roomTypeId;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomTypeName() {
		return roomTypeName;
	}
	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Long getBedId() {
		return bedId;
	}
	public void setBedId(Long bedId) {
		this.bedId = bedId;
	}
	public String getBedName() {
		return bedName;
	}
	public void setBedName(String bedName) {
		this.bedName = bedName;
	}
	public String getObj() {
		return obj;
	}
	public void setObj(String obj) {
		this.obj = obj;
	}
	public Long getDaynum() {
		return daynum;
	}
	public void setDaynum(Long daynum) {
		this.daynum = daynum;
	}

	
}
