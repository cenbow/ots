package com.mk.ots.common.enums;

public enum ReceiveCashBackEnum {

	/**
	 * 无需领取
	 */
	noNeedCashBack(0,"无需领取"),
	/**
	 * 还未领取
	 */
	notReceiveCashBack(1,"还未领取"),
	/**
	 * 已经领取
	 */
	ReceiveedCashBack(2,"已经领取");
	
	private int id;
	private String name;
	private ReceiveCashBackEnum(int id,String name){
		this.id=id;
		this.name=name;
	}
	public int getId(){
		return id;
	}

}
