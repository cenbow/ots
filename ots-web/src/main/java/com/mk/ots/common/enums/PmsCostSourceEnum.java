package com.mk.ots.common.enums;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum PmsCostSourceEnum {
	Y("Y","夜审房租"),
	C("C","退房加收"),
	P("P","手工"),
	;
	private final String id;
	private final String name;
	
	private PmsCostSourceEnum(String id,String name){
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
	
	public static PmsCostSourceEnum findPmsCostSourceEnumById(String id){
		for (PmsCostSourceEnum one : PmsCostSourceEnum.values()) {
			if(one.id.equalsIgnoreCase(id)){
				return one;
			}
		}
		return null;
	}
}
