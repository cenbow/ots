package com.mk.ots.common.enums;
/**
 *
 *
 */
public enum FrontPageEnum {
	page(1,"首页分页"),
	 limit(3,"首页显示酒店条数")
	;

	private final Integer id;
	private final String name;

	private FrontPageEnum(Integer id, String name){
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
