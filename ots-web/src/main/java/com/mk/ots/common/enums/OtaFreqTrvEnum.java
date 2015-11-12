package com.mk.ots.common.enums;

public enum OtaFreqTrvEnum {
	IN_FREQUSER("1", "FREQUSER"),
	OK_LESS4("2", "LESS4"),
	MONTHE_UP4("3", "MONTHE_UP4"), // 一个酒店每月超过4单
	ONEDAY_UP1("4", "ONEDAY_UP1"),// 每天超过一单
	MONTHE_UP5("5", "MONTHE_UP5"), // 每月超过4单

	PHONE_NOT_FIRST("10","电话号码不是第一次使用"),

	DEVICE_NUM_IS_NULL("20","设备号为空"),
	DEVICE_NUM_NOT_FIRST("30","设备号不是第一次使用"),

	CARD_ID_IS_NULL("40","身份证号为空"),
	CARD_ID_NOT_FIRST("50","身份证号不是第一次使用"),
	OFFLINE_PAY("60","到付"),
	ZHIFUBAO_NOT_FIRST("70","支付宝账号不是第一次使用"),
	WEIXINZHIFU_NOT_FRIST("80","微信账号不是第一使用"),
	OUT_OF_RANG("90","下单位置不在酒店1公路范围内"),
	CHECKIN_LESS4("100","入住4小时内"),
	CHECKIN_THAN4("101","入住大于4小时"),
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