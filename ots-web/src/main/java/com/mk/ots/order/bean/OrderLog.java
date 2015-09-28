package com.mk.ots.order.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="b_orderlog", pkey="id")
public class OrderLog extends BizModel<OrderLog> {

	private static final long serialVersionUID = 239026048428991143L;
	public static final OrderLog dao = new OrderLog();
	
	public OrderLog saveOrUpdate() {
		if (this.get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}
}
