package com.mk.ots.restful.input;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.mk.ots.common.bean.ParamBaseBean;

public class HotelHomePageReqEntity extends ParamBaseBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7146109486113573345L;

	@NotNull(message = "缺少参数-城市编码: cityid.")
	@NotEmpty(message = "接口参数cityid不能为空.")
	private String cityid;

	@NotNull(message = "缺少参数-userlatitude: userlatitude.")
	@NotEmpty(message = "接口参数userlatitude不能为空.")
	private Double userlatitude;
	
	@NotNull(message = "缺少参数-userlongitude: userlongitude.")
	@NotEmpty(message = "接口参数userlongitude不能为空.")
	private Double userlongitude;

	private String callmethod;

	@NotNull(message = "缺少参数-接口版本: callversion.")
	@NotEmpty(message = "接口参数callversion不能为空.")
	private String callversion;

	private String ip;

	private String hardwarecode;

	private String otsversion;

	public Double getUserlatitude() {
		return userlatitude;
	}

	public void setUserlatitude(Double userlatitude) {
		this.userlatitude = userlatitude;
	}

	public Double getUserlongitude() {
		return userlongitude;
	}

	public void setUserlongitude(Double userlongitude) {
		this.userlongitude = userlongitude;
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
