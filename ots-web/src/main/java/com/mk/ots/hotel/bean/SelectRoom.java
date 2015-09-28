package com.mk.ots.hotel.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.mk.ots.hotel.model.THotel;

public class SelectRoom implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2854935373826527556L;
	private TRoom room;
	private TRoomType roomType;
	private THotel hotel;
	private BigDecimal price;
	private TDistrict dis;
	private TCity city;
	private TProvince pro;
	private TRoomTypeInfo roomTypeInfo;
	private List<TRoomTypeBed> roomTypeBed;
	private List<TRoom> otherRoom;
	private BigDecimal distance;

	public TRoom getRoom() {
		return room;
	}

	public void setRoom(TRoom room) {
		this.room = room;
	}

	public TRoomType getRoomType() {
		return roomType;
	}

	public void setRoomType(TRoomType roomType) {
		this.roomType = roomType;
	}

	public THotel getHotel() {
		return hotel;
	}

	public void setHotel(THotel hotel) {
		this.hotel = hotel;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public TDistrict getDis() {
		return dis;
	}

	public void setDis(TDistrict dis) {
		this.dis = dis;
	}

	public TCity getCity() {
		return city;
	}

	public void setCity(TCity city) {
		this.city = city;
	}

	public TProvince getPro() {
		return pro;
	}

	public void setPro(TProvince pro) {
		this.pro = pro;
	}

	public TRoomTypeInfo getRoomTypeInfo() {
		return roomTypeInfo;
	}

	public void setRoomTypeInfo(TRoomTypeInfo roomTypeInfo) {
		this.roomTypeInfo = roomTypeInfo;
	}

	public List<TRoomTypeBed> getRoomTypeBed() {
		return roomTypeBed;
	}

	public void setRoomTypeBed(List<TRoomTypeBed> roomTypeBed) {
		this.roomTypeBed = roomTypeBed;
	}

	public List<TRoom> getOtherRoom() {
		return otherRoom;
	}

	public void setOtherRoom(List<TRoom> otherRoom) {
		this.otherRoom = otherRoom;
	}

	public BigDecimal getDistance() {
		return distance;
	}

	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

}
