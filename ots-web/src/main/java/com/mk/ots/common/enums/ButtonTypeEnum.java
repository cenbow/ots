package com.mk.ots.common.enums;

public enum ButtonTypeEnum {
	cancel("cancel","取消"),
	pay("pay","付款"),
	edit("edit","修改"),
	checkin("checkin","快速入住"),
	meet("meet","约会"),
	refund("refund","退款"),
	evaluation("evaluation","评价"),
	continuedToLive ("continuedToLive","续住"),
	delete ("delete","删除"),
	;
	private String name;
	private String info;

	private ButtonTypeEnum(String name, String info) {
		this.name = name;
		this.info = info;
		
	}
	
	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
