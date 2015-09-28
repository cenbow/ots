package com.mk.ots.pay.module.query;

import java.math.BigDecimal;

import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;


public class QueryPayPram {
 
	private boolean success=false;
	private PPayInfoOtherTypeEnum banktype;       //付款银行类型
	private BankPayStatusEnum  paystatus;   //付款状态
	private BigDecimal price;
	private String bankno;     //银行返回单号
	private String orderid;     
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getBankno() {
		return bankno;
	}
	public void setBankno(String bankno) {
		this.bankno = bankno;
	}
	public PPayInfoOtherTypeEnum getBanktype() {
		return banktype;
	}
	public void setBanktype(PPayInfoOtherTypeEnum banktype) {
		this.banktype = banktype;
	}
	public BankPayStatusEnum getPaystatus() {
		return paystatus;
	}
	public void setPaystatus(BankPayStatusEnum paystatus) {
		this.paystatus = paystatus;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	

}