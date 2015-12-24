package com.mk.ots.restful.input;

import com.mk.ots.common.bean.ParamBaseBean;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class HotelHomePageReqEntity extends ParamBaseBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7146109486113573345L;

	@NotNull(message = "缺少参数-城市编码: cityid.")
	@NotEmpty(message = "接口参数cityid不能为空.")
	private String cityid;

	private Double userlatitude;
	
	private Double userlongitude;

	/** 地图地理位置坐标：根据搜索范围查询周边酒店 */
	// 经度
	private Double pillowlongitude;
	// 纬度
	private Double pillowlatitude;

	private String callmethod;

	@NotNull(message = "缺少参数-接口版本: callversion.")
	@NotEmpty(message = "接口参数callversion不能为空.")
	private String callversion;

	private String ip;

	private String hardwarecode;

	private String otsversion;

	/** 入参: 第几页,必填 */
	private Integer page;

	/** 入参: 每页多少条,必填 */
	private Integer limit;
	
	
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

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

	public Double getPillowlongitude() {
		return pillowlongitude;
	}

	public void setPillowlongitude(Double pillowlongitude) {
		this.pillowlongitude = pillowlongitude;
	}

	public Double getPillowlatitude() {
		return pillowlatitude;
	}

	public void setPillowlatitude(Double pillowlatitude) {
		this.pillowlatitude = pillowlatitude;
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
