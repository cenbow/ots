package com.mk.pms.hotel.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * PMS2.0传递参数Bean
 * 
 * @author LYN
 *
 */
public class PMSRoomTypeBean {

	private String id;
	private String name;
	private BigDecimal price;
	private List<PMSRoomBean> room;
	
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		if( null == price){
			price = BigDecimal.ZERO;
		}
		this.price = price;
	}
	public List<PMSRoomBean> getRoom() {
		return room;
	}
	public void setRoom(List<PMSRoomBean> room) {
		if(null == room){
			room = new ArrayList<PMSRoomBean>();
		}
		this.room = room;
	}
	
	
}
