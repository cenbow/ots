package com.mk.ots.common.enums;

public enum OtaFreqTrvEnum {
	IN_FREQUSER("1", "FREQUSER"),
	OK_LESS4("2", "LESS4"),
	MONTHE_UP4("3", "MONTHE_UP4"), // 每月超过4单
	ONEDAY_UP1("4", "ONEDAY_UP1"),// 每天超过一单
	L1("-1", "LESS4");
	
	private final String id;
	private final String name;
	
	private OtaFreqTrvEnum(String id,String name){
		this.id=id;
		this.name=name;
	}

	public static OtaFreqTrvEnum getEnumById(String id){
		for (OtaFreqTrvEnum enu : OtaFreqTrvEnum.values()) {
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