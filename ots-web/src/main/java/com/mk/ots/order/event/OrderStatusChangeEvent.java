package com.mk.ots.order.event;

import com.mk.ots.order.bean.OtaOrder;

/**
 * 监控订单状态变化
 * @author zzy
 */
public class OrderStatusChangeEvent {
	private OtaOrder order;
	private String note;

	public OrderStatusChangeEvent(OtaOrder order, String note) {
		this.order = order;
		this.note = note;
	}
	
	public OtaOrder getMessage() {
		return order;
	}
	
	public String getNote(){
		return note;
	}

}