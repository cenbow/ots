package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月29日
 */
public enum TicketStatusEnum {
	used(1, "已使用"),
	unused(2, "未使用"),
	unactive(3, "未激活"),
	overdue(4, "已过期"),//虚拟状态，数据库中没有这个状态
	limit(5, "还剩{0}天过期"),//虚拟状态，数据库中没有这个状态
	invalid(6, "已失效"),
	
	;
	
	private final Integer id;
	private final String name;
	
	private TicketStatusEnum(Integer id,String name){
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
