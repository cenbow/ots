package com.mk.ots.order.event;

import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.mk.ots.order.bean.OtaOrder;

/**
 * @author zzy com.mk.ots.order.event.EventListener
 */
@Component
public class OrderStatusChangeListener {
	public OtaOrder order;;

	@Subscribe
	public void listen(OrderStatusChangeEvent event) {
		this.order = event.getMessage();
		
		
	}

}
