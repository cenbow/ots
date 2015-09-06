package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public enum TokenTypeEnum {
	PT(1,"普通TOKEN"),
	WX(2,"微信TOKEN"),
	APP(3,"APPTOKEN");
	
	private final Integer id;
	private final String name;
	
	private TokenTypeEnum(Integer id,String name){
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
	
	public static TokenTypeEnum getByID(Integer id){
		for (TokenTypeEnum temp : TokenTypeEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
