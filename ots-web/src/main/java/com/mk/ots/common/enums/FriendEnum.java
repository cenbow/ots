package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月6日
 */
public enum FriendEnum {
	friend(10,"好友"),
	blacklist(0,"黑名单"),
	;
	
	private final Integer id;
	private final String name;
	
	private FriendEnum(Integer id,String name){
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
