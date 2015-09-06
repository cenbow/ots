package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月6日
 */
public enum FavoriteEnum {
	hotel(1,"酒店"),
	roomType(2,"房型"),
	pic(3,"图片")
	;
	
	private final Integer id;
	private final String name;
	
	private FavoriteEnum(Integer id,String name){
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
