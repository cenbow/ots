package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;



/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum OrderMethodEnum {
	CRS(1,"CRS"),
	WEB(2,"WEB"),
	WECHAT(3,"WECHAT"),
	IOS(4,"APP(IOS)"),
	ANDROID(5,"APP(ANDROID)")
	;
	
	private final Integer id;
	private final String name;
	
	private OrderMethodEnum(Integer id,String name){
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
	public static OrderMethodEnum getByID(Integer id){
		for (OrderMethodEnum temp : OrderMethodEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
