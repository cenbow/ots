package com.mk.ots.order.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name = "b_orderbusiness_log", pkey = "id")
public class OrderBusinessLog extends BizModel<OrderBusinessLog> {
	
	private static final long serialVersionUID = -5003710734636333018L;
	public static final OrderBusinessLog dao = new OrderBusinessLog();

}
