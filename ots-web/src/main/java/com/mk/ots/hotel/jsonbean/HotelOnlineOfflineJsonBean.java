package com.mk.ots.hotel.jsonbean;

import java.util.ArrayList;
import java.util.List;

public class HotelOnlineOfflineJsonBean {
	/*
	/*
	data:[{   //数组，批量给出
		hotelid:’’， //酒店id
		action : ‘’ ,  //操作类型（online在线/offline离线）
		time:’’, //yyyyMMddHHmmss  该状态有效时间}]
	*
	*
	*
	*/
	List<HotelOnlineOfflineInfoJsonBean> data=new ArrayList<HotelOnlineOfflineInfoJsonBean>();

	public List<HotelOnlineOfflineInfoJsonBean> getData() {
		return data;
	}

	public void setData(List<HotelOnlineOfflineInfoJsonBean> data) {
		this.data = data;
	}
	
	
}
