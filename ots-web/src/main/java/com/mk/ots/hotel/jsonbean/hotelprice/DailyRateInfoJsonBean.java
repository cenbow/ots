package com.mk.ots.hotel.jsonbean.hotelprice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yub 特殊价详细jsonbean
 */
public class DailyRateInfoJsonBean {
	private String id;// 房型pms
	private String name;// 房型名称
	private List<RoomTypeData> data = new ArrayList<RoomTypeData>();// 门市价

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	public List<RoomTypeData> getData() {
		return data;
	}

	public void setData(List<RoomTypeData> data) {
		this.data = data;
	}



	/**
	 * 
	 * @author yub
	 *
	 */
	public class RoomTypeData {
		
		private String day;  // 日期  yyyymmdd 
		private String price; //门市价
		
		
		public String getDay() {
			return day;
		}
		public void setDay(String day) {
			this.day = day;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		
		
		
	}
}
