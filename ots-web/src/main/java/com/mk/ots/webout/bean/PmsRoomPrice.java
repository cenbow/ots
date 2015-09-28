package com.mk.ots.webout.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author shellingford
 * @version 2015年1月13日
 */
public class PmsRoomPrice implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 186970874879990895L;
	private String roomTypePms;
	private BigDecimal price;
	public String getRoomTypePms() {
		return roomTypePms;
	}
	public void setRoomTypePms(String roomTypePms) {
		this.roomTypePms = roomTypePms;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
