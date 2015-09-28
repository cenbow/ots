package com.mk.ots.pay.model;

import java.io.Serializable;
import java.util.Date;

public class PPayStatusErrorOrder implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long orderId;
	
	private Date orderCreateTime;
	
	private Date recordCreateTime;

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

	public Date getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public Date getRecordCreateTime() {
		return recordCreateTime;
	}

	public void setRecordCreateTime(Date recordCreateTime) {
		this.recordCreateTime = recordCreateTime;
	}
}
