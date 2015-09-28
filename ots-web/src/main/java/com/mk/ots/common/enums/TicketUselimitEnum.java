package com.mk.ots.common.enums;


public enum TicketUselimitEnum {

	YF("1","预付"), //
	PT("2","到付"), //临时性预订--到付
	ALL("","全部"), //临时性预订--到付
//	DB(3,"担保","D"), //担保性预订--预付
	;
	
	private final String type;
	private final String name;
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	private TicketUselimitEnum(String type, String name) {
		this.type = type;
		this.name = name;
	}

}
