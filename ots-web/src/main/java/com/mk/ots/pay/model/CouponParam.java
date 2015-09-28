package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;
 
public class CouponParam implements Serializable{
	private static final long serialVersionUID = 1L;
 
	private BigDecimal totalPrice;  //没优惠之前的总价
	private BigDecimal userCost;    //除过优惠券、红包之后要支付的金额
	private BigDecimal hotelCost;   //酒店补贴，用于写 p_orderlog
	private BigDecimal coupon;      //优惠券需要写的流水（p_payinfo）
	 
	
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public BigDecimal getUserCost() {
		return userCost;
	}
	public void setUserCost(BigDecimal userCost) {
		this.userCost = userCost;
	}
	public BigDecimal getHotelCost() {
		return hotelCost;
	}
	public void setHotelCost(BigDecimal hotelCost) {
		this.hotelCost = hotelCost;
	}
	public BigDecimal getCoupon() {
		return coupon;
	}
	public void setCoupon(BigDecimal coupon) {
		this.coupon = coupon;
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder sf = new StringBuilder();
		sf.append("totalPrice=").append(totalPrice).append(",");
		sf.append("userCost=").append(userCost).append(",");
		sf.append("hotelCost=").append(hotelCost).append(",");
		sf.append("coupon=").append(coupon);
		return sf.toString();
	}
	
}