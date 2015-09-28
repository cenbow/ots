package com.mk.ots.common.enums;


/**
 * 平台类型枚举
 * @author tankai
 *
 */
public enum PlatformTypeEnum {

	APP(1,"APP"),
	WEIXIN(2,"微信"),//
	ALL(3,"全部"),
	;
	
	private final Integer id;
	private final String name;
	
	private PlatformTypeEnum(Integer id,String name){
		this.id=id;
		this.name=name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}


}
