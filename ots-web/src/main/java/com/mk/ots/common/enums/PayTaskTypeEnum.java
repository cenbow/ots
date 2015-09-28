package com.mk.ots.common.enums;

public enum PayTaskTypeEnum {
	
	SENDMSG2LANDLORD(1,"预付订单支付成功通知酒店老板", 15),
	AUTORETRYSENDLEZHU(2,"自动重试下发乐住币", 0),
	PROCESSPAYINGCONFIRMEDORDER(3,"处理已确认支付中订单", 30),
	PROCESSPAYSTATUSERROR(4,"处理支付状态异常订单", 30),
	;

	
	private final Integer id;
	private final String desc;
	private final int interval;
	
	private PayTaskTypeEnum(Integer id, String desc, int interval) {
		this.id = id;
		this.desc = desc;
		this.interval = interval;
	}
	

	public Integer getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}
	
	public int getInterval() {
		return interval;
	}

	@Override
	public String toString() {
		return id.toString();
	}
	
}
