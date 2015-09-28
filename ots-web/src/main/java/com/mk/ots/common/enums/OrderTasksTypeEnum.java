package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum OrderTasksTypeEnum {

	/**
	 * pms1.0
	 */
	ORDERCREATETIMEGT15(100, "tasktype初始化"),
	/**
	 * 手机
	 */
	ORDERPUSH(101, "订单推送消息"),
	/**
	 * pms2.0重庆规则推送
	 */
	CHONG_QING_RULE_PMS2(102,"pms2.0重庆规则推送");
	
	private final Integer id;
	private final String name;
	
	private OrderTasksTypeEnum(Integer id, String name) {
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
	
	public static OrderTasksTypeEnum getByID(Integer id){
		for (OrderTasksTypeEnum temp : OrderTasksTypeEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
	
}
