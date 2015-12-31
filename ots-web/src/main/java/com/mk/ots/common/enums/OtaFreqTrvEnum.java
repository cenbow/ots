package com.mk.ots.common.enums;

public enum OtaFreqTrvEnum {
	IN_FREQUSER("1", "常旅客订单"),
	OK_LESS4("2", "LESS4"),
	MONTHE_UP4("3", "MONTHE_UP4"), // 一个酒店每月超过4单
	ONEDAY_UP1("4", "ONEDAY_UP1"),// 每天超过一单
	MONTHE_UP5("5", "MONTHE_UP5"), // 每月超过4单
	DEVICE_NUM_OVER("6","设备号对应手机号超过三个手机号码"),

	PHONE_NOT_FIRST("10","电话号码不是第一次使用"),

	DEVICE_NUM_IS_NULL("20","设备号为空"),
	DEVICE_NUM_NOT_FIRST("30","设备号不是第一次使用"),


	CARD_ID_IS_NULL("40","身份证号为空"),
	CARD_ID_IS_NOT_PMS_SCAN("41","身份证号不为PMS扫描"),
	CARD_ID_NOT_FIRST("50","身份证号不是第一次使用"),
	ZHIFU_NOT_FIRST("70","支付账号不是第一次使用"),
//	LOCATION_NULL("80","用户未允许眯客获取其下单时坐标，无法判断用户位置"),
	OUT_OF_RANG("90","下单位置不在酒店1公里范围内"),
	CHECKIN_LESS4("100","入住30分钟内"),
	OVER_RANG("110","结算超出定额"),
	NOT_CHECKIN("120","未入住"),
	L1("-1", "无原因");
	
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