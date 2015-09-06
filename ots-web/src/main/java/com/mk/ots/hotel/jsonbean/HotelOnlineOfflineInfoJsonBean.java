/**
 * 
 */
package com.mk.ots.hotel.jsonbean;

/**
 * @author he
 * 房间json转化bean
 */
public class HotelOnlineOfflineInfoJsonBean {
	private String hotelid;
	private Long time;//yyyyMMddHHmmssSSS
	private String action;
	public String getHotelid() {
		return hotelid;
	}
	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	@Override
	public String toString() {
		return "HotelOnlineOfflineInfoJsonBean [hotelid=" + hotelid + ", time=" + time + ", action=" + action + "]";
	}
}
