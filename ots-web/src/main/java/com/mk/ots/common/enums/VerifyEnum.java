package com.mk.ots.common.enums;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum VerifyEnum {
	REG("0","REG"),
	REBIND("1","REBIND"),//
//	LOGIN_PSD("2","LOGIN_PSD"),
//	LOGIN_PSD_QUESTION("3","LOGIN_PSD_QUESTION"),
	PAY_PSD("4","PAY_PSD"),
//	OTHER_VERIFY_CODE("9","OTHER_VERIFY_CODE"),
	LOGIN("10","LOGIN")
	;
	
	private final String id;
	private final String name;
	
	private VerifyEnum(String id,String name){
		this.id=id;
		this.name=name;
	}

	public static VerifyEnum getEnumById(String id){
		for (VerifyEnum enu : VerifyEnum.values()) {
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
