package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PayCallbackLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long orderId;
	
	private String callbackFrom;
	
	private String payResult;
	
	private Date callbackTime;
	
	private String payNo;
	
	private BigDecimal price;
	
	private String errorCode;
	
	private String errorMsg;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getCallbackFrom() {
		return callbackFrom;
	}

	public void setCallbackFrom(String callbackFrom) {
		this.callbackFrom = callbackFrom;
	}

	public String isPayResult() {
		return payResult;
	}

	public void setPayResult(String payResult) {
		this.payResult = payResult;
	}

	public Date getCallbackTime() {
		return callbackTime;
	}

	public void setCallbackTime(Date callbackTime) {
		this.callbackTime = callbackTime;
	}

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
