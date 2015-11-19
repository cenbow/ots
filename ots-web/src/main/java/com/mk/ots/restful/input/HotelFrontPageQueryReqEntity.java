package com.mk.ots.restful.input;

import com.mk.ots.common.bean.ParamBaseBean;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * hotel/querylist酒店综合查询接口入参实体类.
 * 
 * @author chuaiqing.
 *
 */
public class HotelFrontPageQueryReqEntity extends ParamBaseBean implements Serializable {

	/**
	 * 序列化UID
	 */
	private static final long serialVersionUID = 8392856964959038420L;


	/** 入参: 城市code,必填 */
	@NotNull(message = "缺少参数-城市编码: cityid.")
	@NotEmpty(message = "接口参数cityid不能为空.")
	private String cityid;


	// 经度
	private Double userlongitude;
	// 纬度
	private Double userlatitude;

	/** 地图地理位置坐标：根据搜索范围查询周边酒店 */
	// 经度
	private Double pillowlongitude;
	// 纬度
	private Double pillowlatitude;



	// 调用来源: 1-crs 客服；2-web；3-wechat 微信；4-app(ios) iOS App；5-app(Android)
	// android App
	private String callmethod;
	// app版本
	private String callversion;
	// 发起请求的ip
	private String ip;
	// 硬件编码
	private String hardwarecode;
	// OTS接口版本
	private String otsversion;

	// 搜索方式(0关键字；1商圈；2机场车站；3地铁路线；4行政区；5景点；6医院；7高校)
	private Integer searchtype;

	public String getCityid() {
		return cityid;
	}

	public void setCityid(String cityid) {
		this.cityid = cityid;
	}

	public Double getUserlongitude() {
		return userlongitude;
	}

	public void setUserlongitude(Double userlongitude) {
		this.userlongitude = userlongitude;
	}

	public Double getUserlatitude() {
		return userlatitude;
	}

	public void setUserlatitude(Double userlatitude) {
		this.userlatitude = userlatitude;
	}

	@Override
	public String getCallmethod() {
		return callmethod;
	}

	@Override
	public void setCallmethod(String callmethod) {
		this.callmethod = callmethod;
	}

	@Override
	public String getCallversion() {
		return callversion;
	}

	@Override
	public void setCallversion(String callversion) {
		this.callversion = callversion;
	}

	@Override
	public String getIp() {
		return ip;
	}

	@Override
	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String getHardwarecode() {
		return hardwarecode;
	}

	@Override
	public void setHardwarecode(String hardwarecode) {
		this.hardwarecode = hardwarecode;
	}

	@Override
	public String getOtsversion() {
		return otsversion;
	}

	@Override
	public void setOtsversion(String otsversion) {
		this.otsversion = otsversion;
	}

	public Integer getSearchtype() {
		return searchtype;
	}

	public void setSearchtype(Integer searchtype) {
		this.searchtype = searchtype;
	}

	public Double getPillowlatitude() {
		return pillowlatitude;
	}

	public void setPillowlatitude(Double pillowlatitude) {
		this.pillowlatitude = pillowlatitude;
	}

	public Double getPillowlongitude() {
		return pillowlongitude;
	}

	public void setPillowlongitude(Double pillowlongitude) {
		this.pillowlongitude = pillowlongitude;
	}
}
