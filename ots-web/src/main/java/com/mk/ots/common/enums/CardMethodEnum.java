package com.mk.ots.common.enums;

import cn.com.winhoo.mikeweb.exception.MyErrorEnum;
import org.apache.commons.lang3.StringUtils;

/** 
 * 
 * @author jeashi
 * @version 2015年12月22日
 */

public enum CardMethodEnum {
	CRS("1","crs"),
	WEB("2","web"),
	WECHAT("3","wechat"),
	IOS("4","ios"),
	ANDROID("5","android"),
	UNKNOW("6","UNKNOW");

	private final String id;
	private final String name;

	private CardMethodEnum(String id, String name){
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
	
	public static CardMethodEnum getByName(String name){
		if(StringUtils.isEmpty(name)){
			return  UNKNOW;
		}
		for (CardMethodEnum temp : CardMethodEnum.values()) {
			if(temp.getName().equals(name.toLowerCase())){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举名称错误");
	}
}
