package com.mk.ots.hotel.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;


@Component
@DbTable(name="t_city", pkey="cityid")
public class TCity extends BizModel<TCity> implements Serializable{
	public static final TCity dao = new TCity();
	

	private static final long serialVersionUID = 2676085472018498591L;
	private Integer cityid;
	private String code;
	private String cityname;
	private Integer proid;  //20150729 add
	private Integer citysort;
	private TProvince province;
	private BigDecimal longitude;	
	private BigDecimal latitude;
	
	private String simplename; //20150729 add
	private String ishotcity; //20150729 add
	private Integer range; //20150729 add
	private String isSelect; //20150729 add
	private String querycityname; //20150729 add
	private String pinyin;//拼音 全小写
	private String py;//拼音首字母 全小写
	
	
	public Integer getCityid() {
		return cityid;
	}
	public void setCityid(Integer cityid) {
		this.cityid = cityid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) {
		this.cityname = cityname;
	}
	public Integer getCitysort() {
		return citysort;
	}
	public void setCitysort(Integer citysort) {
		this.citysort = citysort;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityid == null) ? 0 : cityid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TCity other = (TCity) obj;
		if (cityid == null) {
			if (other.cityid != null)
				return false;
		} else if (!cityid.equals(other.cityid))
			return false;
		return true;
	}
	public TProvince getProvince() {
		return province;
	}
	public void setProvince(TProvince province) {
		this.province = province;
	}
	
	public BigDecimal getLongitude() {
		return longitude;
	}
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	public BigDecimal getLatitude() {
		return latitude;
	}
	
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getPy() {
		return py;
	}
	public void setPy(String py) {
		this.py = py;
	}
	public Integer getProid() {
		return proid;
	}
	public void setProid(Integer proid) {
		this.proid = proid;
	}
	public String getSimplename() {
		return simplename;
	}
	public void setSimplename(String simplename) {
		this.simplename = simplename;
	}
	public String getIshotcity() {
		return ishotcity;
	}
	public void setIshotcity(String ishotcity) {
		this.ishotcity = ishotcity;
	}
	public Integer getRange() {
		return range;
	}
	public void setRange(Integer range) {
		this.range = range;
	}
	public String getIsSelect() {
		return isSelect;
	}
	public void setIsSelect(String isSelect) {
		this.isSelect = isSelect;
	}
	public String getQuerycityname() {
		return querycityname;
	}
	public void setQuerycityname(String querycityname) {
		this.querycityname = querycityname;
	}
	
	
}
