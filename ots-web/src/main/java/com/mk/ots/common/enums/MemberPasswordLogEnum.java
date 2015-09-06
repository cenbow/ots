package com.mk.ots.common.enums;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum MemberPasswordLogEnum {
	loginPassword("1","登录密码"),
	payPassword("2","支付密码"),
	;
	private final String id;
	private final String name;
	
	private MemberPasswordLogEnum(String id,String name){
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
