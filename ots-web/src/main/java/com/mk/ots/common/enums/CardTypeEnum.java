package com.mk.ots.common.enums;

import cn.com.winhoo.mikeweb.exception.MyErrorEnum;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum CardTypeEnum {
	shenfenzheng("11","身份证"),
	hukouben("13","户口薄"),
	junguanzheng("90","军官证"),
	jingguanzheng("91","警官证"),
	shibingzheng("91","士兵证"),
	huzhao("91","护照"),
	other("91","其他证件"),
	;
	private final String id;
	private final String name;
	
	private CardTypeEnum(String id,String name){
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
	
	public static CardTypeEnum getByName(String name){
		for (CardTypeEnum temp : CardTypeEnum.values()) {
			if(temp.getName().equals(name)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
