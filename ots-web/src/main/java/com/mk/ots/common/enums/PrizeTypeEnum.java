package com.mk.ots.common.enums;

public enum PrizeTypeEnum {
	thirdparty(1,"第三方券"),
	material(2,"实物"),
	mike(3,"眯客券"),
	;
	private final Integer id;
	private final String name;
	
	private PrizeTypeEnum(Integer id,String name){
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
