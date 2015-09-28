package com.mk.ots.pay.module.query;

import com.mk.framework.exception.MyErrorEnum;

public enum  BankPayStatusEnum {
	notPay(1,"未支付"),
	success(2,"已支付"),
	refund(3,"已退款"),
//	successNotRefund(4,"已支付不能退款"),
	;
	
	private final Integer id;
	private final String name;
	
	private BankPayStatusEnum(Integer id,String name){
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
	
	public static BankPayStatusEnum getByID(Integer id){
		for (BankPayStatusEnum temp : BankPayStatusEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
