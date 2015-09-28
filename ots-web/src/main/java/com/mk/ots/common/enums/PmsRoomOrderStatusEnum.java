package com.mk.ots.common.enums;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum PmsRoomOrderStatusEnum {
//	RE("RE",""),
//	IN("IN",""),
//	RX("90",""),
//	OK("91","完成"),
//	IX("91",""),
//	PM("91","挂账"),
	RE("RE",""),
	IN("IN",""),
	RX("RX",""),
	OK("OK","完成"),
	IX("IX",""),
	PM("PM","挂账"),
	;
	private final String id;
	private final String name;
	
	private PmsRoomOrderStatusEnum(String id,String name){
		this.id=id;
		this.name=name;
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
