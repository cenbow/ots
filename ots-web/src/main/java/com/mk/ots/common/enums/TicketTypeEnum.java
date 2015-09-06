package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月29日
 */
public enum TicketTypeEnum {
	simplesub(1,"立减"),
	;
	private final Integer id;
	private final String name;
	
	private TicketTypeEnum(Integer id,String name){
		this.id=id;
		this.name=name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
