/**
 * @author he
 * 周末门市价json
 */
package com.mk.ots.hotel.jsonbean.hotelprice;

import java.util.ArrayList;
import java.util.List;

public class WeekendRateJsonBean {
	private String pmstypeid; // pms厂商编码 详见代码定义 1 2 3 4等
	private String hotelid;// 酒店pms
	private List<WeekendRateInfoJsonBean> roomtype = new ArrayList<WeekendRateInfoJsonBean>();
	public String getPmstypeid() {
		return pmstypeid;
	}
	public void setPmstypeid(String pmstypeid) {
		this.pmstypeid = pmstypeid;
	}
	public String getHotelid() {
		return hotelid;
	}
	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
	}
	public List<WeekendRateInfoJsonBean> getRoomtype() {
		return roomtype;
	}
	public void setRoomtype(List<WeekendRateInfoJsonBean> roomtype) {
		this.roomtype = roomtype;
	}

	

}