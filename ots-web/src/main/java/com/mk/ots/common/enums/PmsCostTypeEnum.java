package com.mk.ots.common.enums;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum PmsCostTypeEnum {
	Q("Q","全日租"),
	B("B","半日租"),
	H("H","钟点房费"),
	P("P","手工房费"),
	;
	private final String id;
	private final String name;
	
	private PmsCostTypeEnum(String id,String name){
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
	
	public static PmsCostTypeEnum findPmsCostTypeEnumById(String id){
		for (PmsCostTypeEnum one : PmsCostTypeEnum.values()) {
			if(one.id.equalsIgnoreCase(id)){
				return one;
			}
		}
		return null;
	}

}
