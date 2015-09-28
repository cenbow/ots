package com.mk.ots.common.enums;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum AffectiveStateEnum {
	Single("0","单身"),
	Loving("1","恋爱"),
	Married("2","已婚"),
	UNKNOW("9","未说明"),
	;
	
	private final String id;
	private final String name;
	
	private AffectiveStateEnum(String id,String name){
		this.id=id;
		this.name=name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return id;
	}
}
