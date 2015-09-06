package com.mk.pms.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author shellingford
 * @version 2015年1月12日
 */
public class OrderCharge implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1386880487467612417L;

	private String custmoerPms;
	private BigDecimal allcost;
	private BigDecimal roomcost;
	private BigDecimal othercost;
	private BigDecimal mikepay;
	private BigDecimal nootapay;
	public String getCustmoerPms() {
		return custmoerPms;
	}
	public void setCustmoerPms(String custmoerPms) {
		this.custmoerPms = custmoerPms;
	}
	public BigDecimal getAllcost() {
		return allcost;
	}
	public void setAllcost(BigDecimal allcost) {
		this.allcost = allcost;
	}
	public BigDecimal getRoomcost() {
		return roomcost;
	}
	public void setRoomcost(BigDecimal roomcost) {
		this.roomcost = roomcost;
	}
	public BigDecimal getOthercost() {
		return othercost;
	}
	public void setOthercost(BigDecimal othercost) {
		this.othercost = othercost;
	}
	public BigDecimal getMikepay() {
		return mikepay;
	}
	public void setMikepay(BigDecimal mikepay) {
		this.mikepay = mikepay;
	}
	public BigDecimal getNootapay() {
		return nootapay;
	}
	public void setNootapay(BigDecimal nootapay) {
		this.nootapay = nootapay;
	}
}
