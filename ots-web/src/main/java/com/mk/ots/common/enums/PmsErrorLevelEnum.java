package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月22日
 */
public enum PmsErrorLevelEnum {
	lgnore(10,"可忽略的"),
	error(500,"需要人工介入的"),
	;
	
	private final Integer id;
	private final String name;
	
	private PmsErrorLevelEnum(Integer id,String name){
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
}
