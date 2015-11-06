package com.mk.ots.bill.model;

import java.math.BigDecimal;
import java.util.Date;

public class BillRPPromoDetails {
	private Long id;
	private Long hotelId;
	private Long billId;
	private Long orderId;
	private Date checkinTime;
	private Date checkoutTime;
	private Date beginTime;
	private Date endTime;
	private Date orderTime;
	private Long orderType;
	private Long promoType;
	private Long dayNumber;
	private String roomTypeName;
	private Long roomTypeId;
	private String roomNo;
	private Long roomId;
	private String payMethod;
	private Long financeStatus;
	private BigDecimal orderPrice;
	private BigDecimal userCost;
	private BigDecimal availableMoney;
	private BigDecimal mikePrice;
	private BigDecimal lezhuCoins;
	private BigDecimal onlinePaied;
	private BigDecimal aliPaied;
	private BigDecimal wechatPaied;
	private BigDecimal billCost;
	private BigDecimal changeCost;
	private BigDecimal finalCost;
	private BigDecimal income;
	private Date createTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getHotelId() {
		return hotelId;
	}
	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}
	public Long getBillId() {
		return billId;
	}
	public void setBillId(Long billId) {
		this.billId = billId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Date getCheckinTime() {
		return checkinTime;
	}
	public void setCheckinTime(Date checkinTime) {
		this.checkinTime = checkinTime;
	}
	public Date getCheckoutTime() {
		return checkoutTime;
	}
	public void setCheckoutTime(Date checkoutTime) {
		this.checkoutTime = checkoutTime;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	public Long getOrderType() {
		return orderType;
	}
	public void setOrderType(Long orderType) {
		this.orderType = orderType;
	}
	public Long getDayNumber() {
		return dayNumber;
	}
	public void setDayNumber(Long dayNumber) {
		this.dayNumber = dayNumber;
	}
	public Long getPromoType() {
		return promoType;
	}
	public void setPromoType(Long promoType) {
		this.promoType = promoType;
	}
	public String getRoomTypeName() {
		return roomTypeName;
	}
	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}
	public Long getRoomTypeId() {
		return roomTypeId;
	}
	public void setRoomTypeId(Long roomTypeId) {
		this.roomTypeId = roomTypeId;
	}
	public String getRoomNo() {
		return roomNo;
	}
	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public Long getFinanceStatus() {
		return financeStatus;
	}
	public void setFinanceStatus(Long financeStatus) {
		this.financeStatus = financeStatus;
	}
	public BigDecimal getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}
	public BigDecimal getUserCost() {
		return userCost;
	}
	public void setUserCost(BigDecimal userCost) {
		this.userCost = userCost;
	}
	public BigDecimal getAvailableMoney() {
		return availableMoney;
	}
	public void setAvailableMoney(BigDecimal availableMoney) {
		this.availableMoney = availableMoney;
	}
	public BigDecimal getMikePrice() {
		return mikePrice;
	}
	public void setMikePrice(BigDecimal mikePrice) {
		this.mikePrice = mikePrice;
	}
	public BigDecimal getLezhuCoins() {
		return lezhuCoins;
	}
	public void setLezhuCoins(BigDecimal lezhuCoins) {
		this.lezhuCoins = lezhuCoins;
	}
	public BigDecimal getOnlinePaied() {
		return onlinePaied;
	}
	public void setOnlinePaied(BigDecimal onlinePaied) {
		this.onlinePaied = onlinePaied;
	}
	public BigDecimal getAliPaied() {
		return aliPaied;
	}
	public void setAliPaied(BigDecimal aliPaied) {
		this.aliPaied = aliPaied;
	}
	public BigDecimal getWechatPaied() {
		return wechatPaied;
	}
	public void setWechatPaied(BigDecimal wechatPaied) {
		this.wechatPaied = wechatPaied;
	}
	public BigDecimal getBillCost() {
		return billCost;
	}
	public void setBillCost(BigDecimal billCost) {
		this.billCost = billCost;
	}
	public BigDecimal getChangeCost() {
		return changeCost;
	}
	public void setChangeCost(BigDecimal changeCost) {
		this.changeCost = changeCost;
	}
	public BigDecimal getFinalCost() {
		return finalCost;
	}
	public void setFinalCost(BigDecimal finalCost) {
		this.finalCost = finalCost;
	}
	public BigDecimal getIncome() {
		return income;
	}
	public void setIncome(BigDecimal income) {
		this.income = income;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	
}
