package com.mk.ots.hotel.bean;

import java.io.Serializable;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */
//@DbClassMapping("b_roomtemp")
public class RoomTemp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2212237914961645635L;
	
	private Long id;
	private Long hotelId;
	private Long roomTypeId;
	private Long roomId;
//	@DbFieldMapping(dbColName = "roomNo")
//	private String roomNo;
	private String time;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getHotelId() {
		return hotelId;
	}
	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}
	public Long getRoomTypeId() {
		return roomTypeId;
	}
	public void setRoomTypeId(Long roomTypeId) {
		this.roomTypeId = roomTypeId;
	}
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
//	public String getRoomNo() {
//		return roomNo;
//	}
//	public void setRoomNo(String roomNo) {
//		this.roomNo = roomNo;
//	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoomTemp other = (RoomTemp) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
