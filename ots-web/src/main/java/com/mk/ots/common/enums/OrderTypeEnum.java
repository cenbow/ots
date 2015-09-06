package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;


/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum OrderTypeEnum {
	YF(1,"预付","D"), //
	PT(2,"普通","T"), //临时性预订--到付
//	DB(3,"担保","D"), //担保性预订--预付
	;
	
	private final Integer id;
	private final String name;
	private final String key;
	
	private OrderTypeEnum(Integer id,String name ,String key){
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
	public static OrderTypeEnum getByID(Integer id){
		for (OrderTypeEnum temp : OrderTypeEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
