package com.mk.pms.myenum;

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
	Z("Z","杂费"),
	;
	private final String id;
	private final String name;
	
	private PmsCostTypeEnum(String id,String name){
		this.id=id;
		this.name=name;
	}
	
	public static PmsCostTypeEnum findPmsCostTypeEnumById(String id){
		for (PmsCostTypeEnum one : PmsCostTypeEnum.values()) {
			if(one.id.equalsIgnoreCase(id)){
				return one;
			}
		}
		return null;
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
