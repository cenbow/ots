package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;


/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum PriceTypeEnum  {
	H(1,"时租","H"),
	R(2,"日租","R"),
	;
	
	private final Integer id;
	private final String name;
	private final String key;
	
	private PriceTypeEnum(Integer id,String name,String key ){
		this.id=id;
		this.name=name;
		this.key=key;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return id.toString();
	}
	public static PriceTypeEnum getByID(Integer id){
		for (PriceTypeEnum temp : PriceTypeEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
	public static PriceTypeEnum getByKey(String key){
		for (PriceTypeEnum temp : PriceTypeEnum.values()) {
			if(temp.getKey().equals(key)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
