package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum RemindStatusEnum {

	INITIALIZE("00","初始化"),
	DONE("10","执行成功"),
	FAILD("20","取消执行");

	private final String code;
	private final String name;

	private RemindStatusEnum( String code, String name) {
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

	public static RemindStatusEnum getByCode(String code){
		for (RemindStatusEnum temp : RemindStatusEnum.values()) {
			if(temp.code.equals(code)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}

}
