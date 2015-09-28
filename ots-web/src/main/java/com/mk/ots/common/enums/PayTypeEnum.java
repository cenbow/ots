package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public enum PayTypeEnum {
	effective(1,"有效"),
	userCancel(2,"用户撤销"),
	sysCanccel(3,"系统撤销"),
	;
	private final Integer id;
	private final String name;
	
	private PayTypeEnum(Integer id,String name){
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
