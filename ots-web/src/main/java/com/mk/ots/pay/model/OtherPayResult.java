package com.mk.ots.pay.model;

import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;


public class OtherPayResult {
	//订单号
	private String outTradeNo;
	//第三方交易号
	private String tradeNo;
	//交易金额
	private String totalFee;
	//第三方支付类型
	private PPayInfoOtherTypeEnum payType;
	//买家账号
	private String buyId;
	public String getOutTradeNo() {
//		if(outTradeNo == null){
//			return null;
//		}
//		if(outTradeNo.startsWith("TEST")){
//			return outTradeNo.substring(4);
//		}
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public String getBuyId() {
		return buyId;
	}
	public void setBuyId(String buyId) {
		this.buyId = buyId;
	}
	public PPayInfoOtherTypeEnum getPayType() {
		return payType;
	}
	public void setPayType(PPayInfoOtherTypeEnum payType) {
		this.payType = payType;
	}
	@Override
	public String toString() {
		return "OtherPayResult [outTradeNo=" + outTradeNo + ", tradeNo="
				+ tradeNo + ", totalFee=" + totalFee + ", payType=" + payType
				+ ", buyId=" + buyId + "]";
	}
}
