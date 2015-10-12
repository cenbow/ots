package com.mk.ots.common.enums;

public enum ClearingTypeEnum {
	generalOrder("普通订单",2001),
	priceDrop("直减订单",2002);
	private String name;
	private int id;
	private ClearingTypeEnum(String name, int id) {
		this.name = name;
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
}
