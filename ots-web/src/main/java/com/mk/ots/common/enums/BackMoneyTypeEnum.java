package com.mk.ots.common.enums;

import cn.com.winhoo.mikeweb.exception.MyErrorEnum;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum BackMoneyTypeEnum {
	defineMaxNum(1,"数量限制"),
	defineMaxMoney(2,"金额限制"),
	;

	private final Integer id;
	private final String name;

	private BackMoneyTypeEnum(Integer id, String name){
		this.id=id;
		this.name=name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}


	public static BackMoneyTypeEnum getByName(String name){
		for (BackMoneyTypeEnum temp : BackMoneyTypeEnum.values()) {
			if(temp.getName().equals(name)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}


	public static BackMoneyTypeEnum getById(Integer id){
		for (BackMoneyTypeEnum temp : BackMoneyTypeEnum.values()) {
			if(temp.getId()==(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
