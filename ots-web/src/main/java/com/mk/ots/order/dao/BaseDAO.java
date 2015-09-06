package com.mk.ots.order.dao;

import java.util.List;

public abstract class BaseDAO {

	public String getIn(List lists) {
		StringBuffer in = new StringBuffer();
		for (Object e : lists) {
			in.append("'").append(e.toString()).append("',");
		}
		if (in.length() > 0) {
			in.setLength(in.length() - 1);
		}
		return in.toString();
	}

}
