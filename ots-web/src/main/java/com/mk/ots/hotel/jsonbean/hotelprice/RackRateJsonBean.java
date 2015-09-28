/**
 * @author he
 * 平日门市价json
 */
package com.mk.ots.hotel.jsonbean.hotelprice;

import java.util.ArrayList;
import java.util.List;

public class RackRateJsonBean {
	private String pmstypeid; // pms厂商编码 详见代码定义 1 2 3 4等
	private String hotelid;// 酒店pms
	private List<RackRateInfoJsonBean> roomtype = new ArrayList<RackRateInfoJsonBean>();

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

	public List<RackRateInfoJsonBean> getRoomtype() {
		return roomtype;
	}

	public void setRoomtype(List<RackRateInfoJsonBean> roomtype) {
		this.roomtype = roomtype;
	}

}