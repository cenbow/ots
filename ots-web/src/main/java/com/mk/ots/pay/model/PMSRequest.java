package com.mk.ots.pay.model;

/**
 * 发送给PMS2.0的数据,序列化后的格式:
 * {
 *	hotelid:'',   //酒店id
 *	customerid:'' //pms客单id
 *	payid:'',     // ota本次支付id
 *	cost:'',      //支付费用 
 *	memo:''       //备注
 * }
 * @author lindi
 *
 */
public class PMSRequest {
	
	private String hotelid;
	
	private String uuid;
	
	private String function;
	
	private String timestamp;
	
	private String customerid;
	
	private String payid;
	
	private String cost;
	
	private String memo;

	public String getHotelid() {
		return hotelid;
	}

	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
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

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getPayid() {
		return payid;
	}

	public void setPayid(String payid) {
		this.payid = payid;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}
