package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum PayStatusEnum {
	doNotPay(50,"无需支付"),
	waitPay(100,"等待支付"),
	paying(110,"支付中"),
	payFail(111,"支付失败"),
	alreadyPay(120,"已支付"),
	refundPay(130,"已退款"),
	;
	
	private final Integer id;
	private final String name;
	
	private PayStatusEnum(Integer id,String name){
		this.id=id;
		this.name=name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return id.toString();
	}
	
	public static PayStatusEnum getByID(Integer id){
		for (PayStatusEnum temp : PayStatusEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
