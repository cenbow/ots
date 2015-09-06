/**
 * 
 */
package com.mk.pms.room.bean;

import java.math.BigDecimal;

/**
 * @author he
 * 房间json转化bean
 */
public class RoomLockJsonBean {
	private String time;
	private String roomid;
	private BigDecimal price;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "RoomLockJsonBean [time=" + time + ", roomid=" + roomid + ", price=" + price + "]";
	}
}
