package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum RuleEnum {
	CHONG_QIN (1002,"重庆"),
	SHANG_HAI(1001,"上海");
	
	private final Integer id;
	private final String cityName;

	private RuleEnum(Integer id,String cityName) {
		this.cityName = cityName;
		this.id = id;
	}
	public String getCityName() {
		return cityName;
	}
	public Integer getId() {
		return id;
	}
	

	@Override
	public String toString() {
		return id.toString();
	}
	public static OrderTypeEnum getByID(Integer id){
		for (OrderTypeEnum temp : OrderTypeEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}

}
