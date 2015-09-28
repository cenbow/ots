package com.mk.framework.component.eventbus;

import java.util.concurrent.Executors;

import org.slf4j.Logger;

import com.google.common.eventbus.AsyncEventBus;
import com.mk.framework.AppUtils;
import com.mk.ots.order.event.OrderStatusChangeListener;
import com.mk.pms.order.event.PmsCalCacheEventListener;
import com.mk.pms.order.event.PmsQueryAgainEventListener;

public class EventBusHelper {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(EventBusHelper.class);

	private AsyncEventBus eventBus = null;

	private static volatile EventBusHelper instance = null;

	private EventBusHelper() {
		this.init();
	}

	public static EventBusHelper getEventBus() {
		if (EventBusHelper.instance == null) {
			synchronized (EventBusHelper.class) {
				if (EventBusHelper.instance == null) {
					EventBusHelper.instance = new EventBusHelper();
				}
			}
		}
		return EventBusHelper.instance;
	}

	private void init() {
		EventBusHelper.logger.info("OTSMessage::EventBusHelper---init--start");
		AsyncEventBus eventBus = new AsyncEventBus("test", Executors.newCachedThreadPool());
		eventBus.register(AppUtils.getBean(OrderStatusChangeListener.class));
		// 全量更新、初始化的事件
		eventBus.register(AppUtils.getBean(PmsQueryAgainEventListener.class));
		// 订单状态变化的监听类
		eventBus.register(AppUtils.getBean(PmsCalCacheEventListener.class));
		this.eventBus = eventBus;
		EventBusHelper.logger.info("OTSMessage::EventBusHelper---init--end");
	}

	public void post(Object event) {
		this.eventBus.post(event);
	}

}
