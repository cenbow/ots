package com.mk.ots.pay.model;

import java.io.Serializable;
import com.mk.ots.pay.module.weixin.pay.common.Tools;
 
public class SelectPms implements Serializable{
	private static final long serialVersionUID = 1L;
 
	private String hotelid;
	private String uuid;
	private String function;
	private String timestamp;
	private String customerid;
	
	public SelectPms() {
		super();
	}

	
	public SelectPms(String hotelid,String customerid) {
		super();
		this.uuid = Tools.getUuid();
		this.function = "selectcustomerpay";
		this.timestamp = Tools.getTimestamp();
		this.hotelid = hotelid;
		this.customerid = customerid;
	}

	/**
	 * @return the hotelid
	 */
	public String getHotelid() {
		return hotelid;
	}

	/**
	 * @param hotelid the hotelid to set
	 */
	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction(String function) {
		this.function = function;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the customerid
	 */
	public String getCustomerid() {
		return customerid;
	}

	/**
	 * @param customerid the customerid to set
	 */
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	
	
	
	
 
}