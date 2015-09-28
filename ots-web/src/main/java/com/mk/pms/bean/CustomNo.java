package com.mk.pms.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mk.ots.common.enums.PmsRoomOrderStatusEnum;
import com.mk.ots.common.enums.PriceTypeEnum;

/**
 *
 * @author shellingford
 * @version 2015年1月12日
 */
public class CustomNo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5657403134016677457L;
	
	private Long roomTypeId;
	private String roomTypePms;
	private Long roomId;
	private String roomPms;
	private boolean delete=false;
	private String orderid;
	private String customerno;
	private PmsRoomOrderStatusEnum recstatus;
	private String rectype;
	private BigDecimal price;
	private Date ArrivalTime;
	private Date excheckoutTime;
	private BigDecimal payment;
	private BigDecimal cost;
	private BigDecimal balance;
	private String roominguser;
	private PriceTypeEnum priceType;
	
	public Long getRoomTypeId() {
		return roomTypeId;
	}
	public void setRoomTypeId(Long roomTypeId) {
		this.roomTypeId = roomTypeId;
	}
	public String getRoomTypePms() {
		return roomTypePms;
	}
	public void setRoomTypePms(String roomTypePms) {
		this.roomTypePms = roomTypePms;
	}
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	public String getRoomPms() {
		return roomPms;
	}
	public void setRoomPms(String roomPms) {
		this.roomPms = roomPms;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getCustomerno() {
		return customerno;
	}
	public void setCustomerno(String customerno) {
		this.customerno = customerno;
	}
	public PmsRoomOrderStatusEnum getRecstatus() {
		return recstatus;
	}
	public void setRecstatus(PmsRoomOrderStatusEnum recstatus) {
		this.recstatus = recstatus;
	}
	public String getRectype() {
		return rectype;
	}
	public void setRectype(String rectype) {
		this.rectype = rectype;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Date getArrivalTime() {
		return ArrivalTime;
	}
	public void setArrivalTime(Date arrivalTime) {
		ArrivalTime = arrivalTime;
	}
	public Date getExcheckoutTime() {
		return excheckoutTime;
	}
	public void setExcheckoutTime(Date excheckoutTime) {
		this.excheckoutTime = excheckoutTime;
	}
	public BigDecimal getPayment() {
		return payment;
	}
	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getRoominguser() {
		return roominguser;
	}
	public void setRoominguser(String roominguser) {
		this.roominguser = roominguser;
	}
	public PriceTypeEnum getPriceType() {
		return priceType;
	}
	public void setPriceType(PriceTypeEnum priceType) {
		this.priceType = priceType;
	}
	

}
