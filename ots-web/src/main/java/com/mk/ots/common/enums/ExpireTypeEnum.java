package com.mk.ots.common.enums;

public enum ExpireTypeEnum {
	LOCK(1),
	DYNAMIC(2);
	private int id;
	
	ExpireTypeEnum(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
