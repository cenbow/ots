package com.mk.pms.myenum;
/**
 *
 * @author shellingford
 * @version 2015年1月7日
 */
public enum PmsLogTypeEnum {
	send("S","发送"),
	receive("R","接收"),
	;
	
	private final String id;
	private final String name;
	private PmsLogTypeEnum(String id, String name) {
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
