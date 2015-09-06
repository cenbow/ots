package com.mk.ots.common.enums;

public enum PayCallbackEnum {

	APP_WeChat_Check(1,"微信支付Check"),
	APP_Ali_Check(2,"支付宝支付Check"),
	WeChat_Platform_Check(3,"微信公共账号支付Check"),
	WeChat_Callback(11,"微信回调"),
	Ali_Callback(12,"支付宝回调"),
	WeChat_Platform_Callback(13,"微信公共账号回调");
	
	public int id;
	public String desc;
	
	
	private PayCallbackEnum(int id, String desc) {
		this.id = id;
		this.desc = desc;
	}

	public int getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}

	public static PayCallbackEnum getById(int id) {
		
		for (PayCallbackEnum temp : PayCallbackEnum.values()) {
			if (temp.getId() == id) {
				return temp;
			}
		}
		
		return null;
	}
	
}
