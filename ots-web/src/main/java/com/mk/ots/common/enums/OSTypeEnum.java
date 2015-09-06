package com.mk.ots.common.enums;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum OSTypeEnum {
	IOS("1", "IOS"),
	ANDROID("2", "ANDROID"),
	WX("3", "微信"),
	OTHER("4","other"),
	H("5","H5");

	
	private final String id;
	private final String name;
	
	private OSTypeEnum(String id,String name){
		this.id=id;
		this.name=name;
	}

	public static OSTypeEnum getEnumById(String id){
		for (OSTypeEnum enu : OSTypeEnum.values()) {
			if(enu.getId().equals(id)){
				return enu;
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
