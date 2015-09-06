package com.mk.ots.common.enums;

/** 
 * 
 * @author wbj
 * @version 2015年8月7日
 */

public enum PrizeStatusEnum {
	unused("0", "未使用"),
	received("1", "已领取"),
	used("2", "已使用");

	
	private final String id;
	private final String name;
	
	private PrizeStatusEnum(String id,String name){
		this.id=id;
		this.name=name;
	}

	public static PrizeStatusEnum getEnumById(String id){
		for (PrizeStatusEnum enu : PrizeStatusEnum.values()) {
			if(enu.getId().equals(id)){
				return enu;
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
