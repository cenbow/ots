package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.mk.ots.pay.module.weixin.pay.common.Tools;
 
public class CancelPms implements Serializable{
	private static final long serialVersionUID = 1L;
 
	private String hotelid;
	private String uuid;
	private String function;
	private String timestamp;
	private String customerid;
	private String payid;
	private BigDecimal cost;
	private String memo;
	
	public CancelPms(Long orderid, BigDecimal cost ) {
		super();
		this.uuid = Tools.getUuid();
		this.function = "cancelpaybyerror";
		this.timestamp = Tools.getTimestamp();
		this.cost = cost;
		this.memo = "订单号："+orderid+"取消乐住币，金额是："+cost;
	}
	
 
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
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
	
	
	
	
	
	
 
}