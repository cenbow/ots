package com.mk.ots.common.enums;

/**
 * 是否当日生效
 * 
 * @author nolan
 * 
 */
public enum EffectiveTypeEnum {
	TODAY(1), TOMORROW(2);

	private int id;

	EffectiveTypeEnum(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
