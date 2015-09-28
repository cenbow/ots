package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月6日
 */
public enum FootPrintShareEnum {
	onlySelf(0,"仅自己可见"),
	friend(10,"朋友可见"),
	all(20,"所有人可见")
	;
	
	private final Integer id;
	private final String name;
	
	private FootPrintShareEnum(Integer id,String name){
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