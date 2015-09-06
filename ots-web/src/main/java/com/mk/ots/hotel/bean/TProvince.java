package com.mk.ots.hotel.bean;

import java.io.Serializable;

/** 
 * 
 * @author shellingford
 * @version 2014年10月23日
 */
//@DbClassMapping("t_province")
public class TProvince implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5570504720209899678L;
	private Integer proid;
	private String code;
	private String proname;
	private Integer prosort;
	private String proRemark;	
	private String longitude;	
	private String latitude;
	public Integer getProid() {
		return proid;
	}
	public void setProid(Integer proid) {
		this.proid = proid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getProname() {
		return proname;
	}
	public void setProname(String proname) {
		this.proname = proname;
	}
	public Integer getProsort() {
		return prosort;
	}
	public void setProsort(Integer prosort) {
		this.prosort = prosort;
	}
	public String getProRemark() {
		return proRemark;
	}
	public void setProRemark(String proRemark) {
		this.proRemark = proRemark;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((proid == null) ? 0 : proid.hashCode());
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
		TProvince other = (TProvince) obj;
		if (proid == null) {
			if (other.proid != null)
				return false;
		} else if (!proid.equals(other.proid))
			return false;
		return true;
	}
}
