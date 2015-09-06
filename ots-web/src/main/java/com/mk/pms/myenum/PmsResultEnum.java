package com.mk.pms.myenum;


/**
 *
 * @author shellingford
 * @version 2015年1月7日
 */
public enum PmsResultEnum {
	success("T","成功"),
	faild("F","失败"),
	online("O","处理中"),
	;
	private final String id;
	private final String name;
	private PmsResultEnum(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return id;
	}
}
