package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum OrderTasksStatusEnum {

	INITIALIZE(0 ,"初始化"),
	CHANGE(1 ,"执行成功"),
	FAILURE(2, "取消执行");
	
	private final Integer id;
	private final String name;
	
	private OrderTasksStatusEnum(Integer id, String name) {
		this.id = id;
		this.name = name;
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
	
	public static OrderTasksStatusEnum getByID(Integer id){
		for (OrderTasksStatusEnum temp : OrderTasksStatusEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
	
}
