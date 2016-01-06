package com.mk.ots.common.enums;

public enum OtaFreqTrvEnum {
	IN_FREQUSER("1", "入住人为常旅客"),
	OK_LESS4("2", "入住时间未满4小时"),
	MONTHE_UP4("3", "酒店本月超过4次切单"), // 一个酒店每月超过4单
	ONEDAY_UP1("4", "酒店今日不是第一次切单"),// 每天超过一单
	MONTHE_UP5("5", "酒店全网每月超过4单"), // 每月超过4单
	DEVICE_NUM_OVER("6","该订单用户的手机设备号对应手机号超过三个"),

	NON_USE_APP("9","该订单用户不是通过App下单"),
	PHONE_NOT_FIRST("10","该订单用户的电话号码不是第一次使用"),

	DEVICE_NUM_IS_NULL("20","该订单用户的手机设备号为空"),
	DEVICE_NUM_NOT_FIRST("30","该订单用户的手机设备号不是第一次使用"),

	CARD_ID_IS_NULL("40","该订单的身份证号码为空"),
	CARD_ID_IS_NOT_PMS_SCAN("41","该订单不是通过PMS扫描二代身份证办理"),
	CARD_ID_NOT_FIRST("50","该订单用户的身份证号不是第一次使用"),
	TICKET_NOT_FIRST("60","该订单用户使用过优惠券"),
	ZHIFU_NOT_FIRST("70","该订单用户的支付账号不是第一次使用"),
//	LOCATION_NULL("80","用户未允许眯客获取其下单时坐标，无法判断用户位置"),
	OUT_OF_RANG("90","该订单用户下单时位置不在酒店周边1公里内"),
	CHECKIN_LESS4("100","该订单的入住时间不满30分钟"),
	OVER_RANG("110","酒店日拉新限额已满"),
	NOT_CHECKIN("120","该订单未入住"),
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