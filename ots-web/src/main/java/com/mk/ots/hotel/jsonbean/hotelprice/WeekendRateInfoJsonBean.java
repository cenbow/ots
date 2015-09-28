package com.mk.ots.hotel.jsonbean.hotelprice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author he 周末门市价详细jsonbean
 */
public class WeekendRateInfoJsonBean {
	private String id;// 房型pms
	private String name;// 房型名称
	private List<WeekendRateInfoDataJsonBean> data = new ArrayList<WeekendRateInfoDataJsonBean>();// 门市价及星期
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
	public List<WeekendRateInfoDataJsonBean> getData() {
		return data;
	}
	public void setData(List<WeekendRateInfoDataJsonBean> data) {
		this.data = data;
	}
}
