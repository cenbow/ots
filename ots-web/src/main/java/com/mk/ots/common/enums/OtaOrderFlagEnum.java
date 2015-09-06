package com.mk.ots.common.enums;

/** 
 * 
 * @author  lisw
 * @version 2015年05月29日
 */

public enum OtaOrderFlagEnum {
	
	CREATEQKORDER("01","创建切客订单"),
	CREATEORDER("02","创建直客订单"),
	ADDPMSORDERERR("03","创建pms客单失败"),
//	PMSORDERCANCELPMSORDER("04","创建pms客单失败后,删除此客单"),
	MODIFYPMSORDERAFTERPAY("05","预付支付完成后修改订单的状态"),
	AFERPTPAY("06","在到付后,查看状态已确认"),
//	CANCELSTART("07","取消开始"),
	CANCELPMSPAY("08","取消PMS支付"),
//	CANCELORDERPMSOK("09","PMS取消订单成功"),
	CANCELORDERPMSOKCANCELORDERPAY("10","PMS取消订单成功后,取消订单支付"),
	CANCELORDERPMSERROR("11","PMS取消订单出错"),
	CANCELBYSYSTEM("12","系统取消订单"),
	CANCELBYUSERWAITPAY("13","用户取消待支付订单"),
	CANCELBYUSER("14","用户取消订单"),
	AFTERCANCELUNLOCKROOM("15","用户取消订单解开房间"),
	CHANGESTATUS_SAVECUSTORNO("16","收到pms回调消息"),
	AFTERXIAFALUZHUBI("17","下发乐住币"),
	UPDATEORDERSTATUS("18","pms回调后改变订单状态"),
	UNLOCKROOM("19","pms回调后房间解锁"),
	KF_CANCELORDER("20","客服取消订单"),
	KF_MODIFYORDER("21","客服修改订单"),
	KF_MODIFYORDERSTATUSAFTERCANCELPAY("22","客服取消第三方支付，取消了订单"),
	ORDER_START_PAY("23","选择支付方式"),
	BANK_PAY_SUCCESS("24","用户支付成功"),
	BANK_REFUND_SUCCESS("25","银行退款成功");
	private final String id;
	private final String name;
	
	private OtaOrderFlagEnum(String id,String name){
		this.id=id;
		this.name=name;
	}

	public static OtaOrderFlagEnum getEnumById(String id){
		for (OtaOrderFlagEnum enu : OtaOrderFlagEnum.values()) {
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
