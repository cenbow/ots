package com.mk.ots.common.enums;

/**
 * 渠道来源
 * @author tankai
 *
 */
public enum ComefromtypeEnum {
	qieke("QK","切客"),
	qudao("QD","渠道"),
	b_qieke("BQK","B规则切客"),
    huodong("HD","活动"),
	;
	private String type;
	private String name;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private ComefromtypeEnum(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
}
