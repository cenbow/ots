/**
 * @author yub
 *	特殊价格 json
 */
package com.mk.ots.hotel.jsonbean.hotelprice;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yub
 *
 */

public class DailyRateJsonBean {
	private String uuid; 	  //唯一标示    32位uuid
	private String function;   // syncdailyrate指令名称
	private String timestamp;	//yyymmddh24miss
	private String pmstypeid; // pms厂商编码 详见代码定义 1 2 3 4等
	private String hotelid;// 酒店pms
	private List<DailyRateInfoJsonBean> roomtype = new ArrayList<DailyRateInfoJsonBean>();

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

	public List<DailyRateInfoJsonBean> getRoomtype() {
		return roomtype;
	}

	public void setRoomtype(List<DailyRateInfoJsonBean> roomtype) {
		this.roomtype = roomtype;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
	

}