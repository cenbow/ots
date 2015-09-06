package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum OrderTasksEnum {

	INITIALIZE(0 ,"初始化"),
	CHANGE(1 ,"修改入住人后"),
	
	TASKTYPE(100, "tasktype初始化");
	
	private final Integer id;
	private final String name;
	private OrderTasksEnum(Integer id, String name) {
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
	public static OrderTasksEnum getByID(Integer id){
		for (OrderTasksEnum temp : OrderTasksEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
	
	
}
