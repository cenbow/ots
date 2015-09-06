package com.mk.ots.common.enums;

public enum ManualLuzhuRstEnum {

	success(1, true, "操作乐住币成功"),
	orderNoExist(2, false, "未找到订单"),
	payNoExist(3, false, "未找到对应支付单(pay)"),
	cantReTry(4, false, "操作乐住币失败(已离店/取消),账目已经留存,不要重试"),
	canReTry(5, false, "操作乐住币失败,可以重试"),
	dontReTry(6, false, "到付无乐住币订单,不需要取消乐住币"),
	cantCancel(7, false, "该订单已经回收过乐住币,不能再操作整单回收"),
	autoReTry(8, false, "该订单已经有下发任务,会自动下发不需要手动操作"),
	;

	private final int code;
	private final boolean result;
	private final String desc;

	private ManualLuzhuRstEnum(int code, boolean result, String desc) {
		this.code = code;
		this.result = result;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public boolean getResult() {

		return result;
	}
	
	public String getDesc() {
		return desc;
	}

}
