/**
 * 2015年9月15日下午6:44:24
 * zhaochuanbin
 */
package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class PRealOrderLog implements Serializable{
	
	private static final long serialVersionUID = -1794970832831835366L;

	private Long orderid;
	
	private Long payid;
	
	private BigDecimal realcost;

    private BigDecimal realotagive;
    
    private BigDecimal  realaccountcost;
    
    private BigDecimal  realallcost;
    
    private BigDecimal  hotelgive;
    
    private BigDecimal qiekeIncome;

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public Long getPayid() {
		return payid;
	}

	public void setPayid(Long payid) {
		this.payid = payid;
	}

	public BigDecimal getRealcost() {
		return realcost;
	}

	public void setRealcost(BigDecimal realcost) {
		this.realcost = realcost;
	}

	public BigDecimal getRealotagive() {
		return realotagive;
	}

	public void setRealotagive(BigDecimal realotagive) {
		this.realotagive = realotagive;
	}

	public BigDecimal getRealaccountcost() {
		return realaccountcost;
	}

	public void setRealaccountcost(BigDecimal realaccountcost) {
		this.realaccountcost = realaccountcost;
	}

	public BigDecimal getRealallcost() {
		return realallcost;
	}

	public void setRealallcost(BigDecimal realallcost) {
		this.realallcost = realallcost;
	}

	public BigDecimal getHotelgive() {
		return hotelgive;
	}

	public void setHotelgive(BigDecimal hotelgive) {
		this.hotelgive = hotelgive;
	}

	public BigDecimal getQiekeIncome() {
		return qiekeIncome;
	}

	public void setQiekeIncome(BigDecimal qiekeIncome) {
		this.qiekeIncome = qiekeIncome;
	}

	@Override
	public String toString() {
		return "PRealOrderLog [orderid=" + orderid + ", payid=" + payid
				+ ", realcost=" + realcost + ", realotagive=" + realotagive
				+ ", realaccountcost=" + realaccountcost + ", realallcost="
				+ realallcost + ", hotelgive=" + hotelgive + ", qiekeIncome="
				+ qiekeIncome + "]";
	}
    
	
}
