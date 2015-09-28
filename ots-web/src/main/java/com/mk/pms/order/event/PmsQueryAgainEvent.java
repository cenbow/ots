package com.mk.pms.order.event;


/**
 * @author zzy
 * com.mk.ots.order.event.OrderEvent
 */
public class PmsQueryAgainEvent{
	private final Long message;

	public PmsQueryAgainEvent(Long message) {
		this.message = message;
	}

	public Long getMessage() {
		return message;
	}
}