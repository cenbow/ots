package com.mk.ots.hotel.bean;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="t_district", pkey="id")
public class TDistrict extends BizModel<TDistrict> implements Serializable {
	
	public static final TDistrict dao = new TDistrict();

	private static final long serialVersionUID = -2910571331107179702L;
	private Integer id;
	private String code;
	private String disname;
	private Integer dissort;
	private TCity city;
	private String longitude;	
	private String latitude;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDisname() {
		return disname;
	}
	public void setDisname(String disname) {
		this.disname = disname;
	}
	public Integer getDissort() {
		return dissort;
	}
	public void setDissort(Integer dissort) {
		this.dissort = dissort;
	}
	public TCity getCity() {
		return city;
	}
	public void setCity(TCity city) {
		this.city = city;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		TDistrict other = (TDistrict) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
