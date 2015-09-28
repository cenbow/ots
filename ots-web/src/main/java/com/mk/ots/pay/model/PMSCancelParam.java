package com.mk.ots.pay.model;

import java.math.BigDecimal;

public class PMSCancelParam {
	
	private BigDecimal price;
	
	private Long payId;

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public Long getPayId() {
		return payId;
	}

	public void setPayId(Long payId) {
		this.payId = payId;
	}

	public String toString() {
		
		return "PMSCancelParam:[price:" + price + ", payId:" + payId + "]";
		
	}

}
