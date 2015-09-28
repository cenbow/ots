/**
 * 
 */
package com.mk.pms.order.bean;

import java.io.Serializable;

/**
 * @author he
 * 记录已同步的客单，放到redis队列中
 */
public class SynedCustomerBean implements Serializable{
	private static final long serialVersionUID = 1221503639109622672L;
	
	private String customernos;//已同步的客单号  以逗号分隔
	private Long hotelId;//酒店id
	public String getCustomernos() {
		return customernos;
	}
	public void setCustomernos(String customernos) {
		this.customernos = customernos;
	}
	public Long getHotelId() {
		return hotelId;
	}
	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}
	
}
