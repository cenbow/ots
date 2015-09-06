package com.mk.ots.common.enums;

import cn.com.winhoo.mikeweb.exception.MyErrorEnum;
import cn.com.winhoo.mikeweb.myenum.OrderTypeEnum;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum SexEnum {
	UNKNOW("0","未知的性别"),
	MALE("1","男"),
	FEMALE("2","女"),
	UN("9","未说明"),
	;
	
	private final String id;
	private final String name;
	
	private SexEnum(String id,String name){
		this.id=id;
		this.name=name;
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
	public static SexEnum getByName(String name){
		for (SexEnum temp : SexEnum.values()) {
			if(temp.getName().equals(name)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
