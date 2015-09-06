package com.mk.ots.pay.model;

import java.io.Serializable;
import java.util.Date;

import com.mk.ots.common.enums.PmsErrorLevelEnum;
import com.mk.ots.common.enums.PmsErrorTypeEnum;

/**
 *
 * @author shellingford
 * @version 2015年1月22日
 */
public class PmsError implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private Date time;
	private PmsErrorTypeEnum type;
	private Long otherid;
	private PmsErrorLevelEnum level;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public PmsErrorTypeEnum getType() {
		return type;
	}
	public void setType(PmsErrorTypeEnum type) {
		this.type = type;
	}
	public Long getOtherid() {
		return otherid;
	}
	public void setOtherid(Long otherid) {
		this.otherid = otherid;
	}
	public PmsErrorLevelEnum getLevel() {
		return level;
	}
	public void setLevel(PmsErrorLevelEnum level) {
		this.level = level;
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
		PmsError other = (PmsError) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
