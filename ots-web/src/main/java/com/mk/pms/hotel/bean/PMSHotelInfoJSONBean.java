package com.mk.pms.hotel.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * PMS2.0参数数据格式 传递json数据格式
 *  { hotelid:’’, //酒店id 
 *  	roomtype:[{ 
 *  		id:’’, //房型id
 * 			name:’’, //房型名称 
 * 			price:’’, //门市价 
 * 			room:[{ //房间 id:’’, //房间id 
 * 				roomno:’’, //房间号 
 * 			 }] 
 * 		}] 
 * 	}
 * 
 * @author LYN
 *
 */
public class PMSHotelInfoJSONBean {
	private String hotelid;
	private String phone;
	
	private List<PMSRoomTypeBean> roomtype;
	 
	public String getHotelid() {
		return hotelid;
	}
	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public List<PMSRoomTypeBean> getRoomtype() {
		return roomtype;
	}
	public void setRoomtype(List<PMSRoomTypeBean> roomtype) {
		if( null ==roomtype){
			roomtype = new ArrayList<PMSRoomTypeBean>();
		}
		this.roomtype = roomtype;
	}
}
