package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum RemindTypeEnum {

	SPECIAL_ROOM("10","特价房提醒");

	private final String code;
	private final String name;

	private RemindTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.code;
	}
	
	public static RemindTypeEnum getByCode(String code){
		for (RemindTypeEnum temp : RemindTypeEnum.values()) {
			if(temp.code.equals(code)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
	
}
