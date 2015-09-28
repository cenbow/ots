package com.mk.ots.promo.model;

import java.math.BigDecimal;

public class CommonXmlPromo {
	/**
	 * 线上价格
	 */
	private BigDecimal onlineprice;
	
	/**
	 * 线下价格
	 */
	private BigDecimal offlineprice;
	
	/**
	 * 限制天数
	 */
	private int limitdaynum;
	
	/**
	 * 生效时间,0为当日生效,1为隔日生效,以此类推.
	 */
	private int effective;
	
	public CommonXmlPromo(BigDecimal onlineprice, BigDecimal offlineprice, int limitdaynum, int effective) {
		this.onlineprice = onlineprice;
		this.offlineprice = offlineprice;
		this.limitdaynum = limitdaynum;
		this.effective = effective;
	}

	public BigDecimal getOnlineprice() {
		return onlineprice;
	}

	public void setOnlineprice(BigDecimal onlineprice) {
		this.onlineprice = onlineprice;
	}

	public BigDecimal getOfflineprice() {
		return offlineprice;
	}

	public void setOfflineprice(BigDecimal offlineprice) {
		this.offlineprice = offlineprice;
	}

	public int getLimitdaynum() {
		return limitdaynum;
	}

	public void setLimitdaynum(int limitdaynum) {
		this.limitdaynum = limitdaynum;
	}

	public int getEffective() {
		return effective;
	}

	public void setEffective(int effective) {
		this.effective = effective;
	}

	@Override
	public String toString() {
		return "CommonXmlPromo [onlineprice=" + onlineprice + ", offlineprice="
				+ offlineprice + ", limitdaynum=" + limitdaynum
				+ ", effective=" + effective + "]";
	}
	
}
