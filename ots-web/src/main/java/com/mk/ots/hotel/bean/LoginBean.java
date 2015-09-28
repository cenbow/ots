package com.mk.ots.hotel.bean;

import com.mk.ots.member.model.UMember;
import com.mk.ots.system.model.UToken;


public class LoginBean {
	public static final Integer NORMAL_LOGIN = 1;
	public static final Integer AUTO_LGOIN = 2;
	private UToken token;
	private String loginType;//登录类型
	private UMember member;
	
	
	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public UMember getMember() {
		return member;
	}

	public void setMember(UMember member) {
		this.member = member;
	}

	public UToken getToken() {
		return token;
	}

	public void setToken(UToken token) {
		this.token = token;
	}
}
