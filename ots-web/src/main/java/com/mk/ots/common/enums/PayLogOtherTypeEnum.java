package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public enum PayLogOtherTypeEnum {
	pay(1,"交易流水"),
	;
	private final Integer id;
	private final String name;
	
	private PayLogOtherTypeEnum(Integer id,String name){
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
