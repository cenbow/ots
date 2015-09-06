package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月6日
 */
public enum HuanXinChangeActionEnum {
	createUser(1,"创建用户"),
	changePassword(2,"修改密码"),
	createUserByH(3,"创建用户"),
	changePasswordByH(4,"修改密码"),
	;
	
	private final Integer id;
	private final String name;
	
	private HuanXinChangeActionEnum(Integer id,String name){
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
