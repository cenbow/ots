package com.mk.pms.myenum;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum PmsRoomOrderStatusEnum {
	RE("RE","预定中"),
	IN("IN","在住"),
	RX("RX","预订取消"),
	OK("OK","完成"),
	IX("IX","入住取消"),
	PM("PM","挂账"),
	;
	private final String id;
	private final String name;
	
	private PmsRoomOrderStatusEnum(String id,String name){
		this.id=id;
		this.name=name;
	}
	
	public static PmsRoomOrderStatusEnum findPmsRoomOrderStatusEnumById(String id){
		for (PmsRoomOrderStatusEnum type : PmsRoomOrderStatusEnum.values()) {
			if(type.getId().equalsIgnoreCase(id)){
				return type;
			}
		}
		return null;
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
