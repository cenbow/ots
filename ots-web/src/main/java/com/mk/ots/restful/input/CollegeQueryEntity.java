package com.mk.ots.restful.input;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.mk.ots.common.bean.ParamBaseBean;

public class CollegeQueryEntity extends ParamBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4898364844592106948L;
	private String token;
	@NotNull(message = "缺少参数-城市编码: cityid.")
	@NotEmpty(message = "接口参数cityid不能为空.")	
	private String cityid;
	private String callmethod;
	private String callversion;
	private String ip;
	private String hardwarecode;
	private String otsversion;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCityid() {
		return cityid;
	}

	public void setCityid(String cityid) {
		this.cityid = cityid;
	}

	public String getCallmethod() {
		return callmethod;
	}

	public void setCallmethod(String callmethod) {
		this.callmethod = callmethod;
	}

	public String getCallversion() {
		return callversion;
	}

	public void setCallversion(String callversion) {
		this.callversion = callversion;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHardwarecode() {
		return hardwarecode;
	}

	public void setHardwarecode(String hardwarecode) {
		this.hardwarecode = hardwarecode;
	}

	public String getOtsversion() {
		return otsversion;
	}

	public void setOtsversion(String otsversion) {
		this.otsversion = otsversion;
	}

}
