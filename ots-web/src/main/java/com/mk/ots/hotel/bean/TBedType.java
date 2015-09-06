package com.mk.ots.hotel.bean;

import java.io.Serializable;
import java.util.List;

/** 
 * 
 * @author shellingford
 * @version 2014年11月5日
 */
//@DbClassMapping("t_bedtype")
public class TBedType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6391125312732844409L;
	
	public static final String TABLE_NAME="t_bedtype";
	
	//@DbFieldMapping(dbColName = "id", isPrimaryKey = true, isAutoPrimaryKey=true)
	private Long id;
	//@DbFieldMapping(dbColName = "name")
	private String name;
	//@DbFieldMapping(dbColName = "visible")
	private Boolean visible;
	//@DbFieldMapping(ORMappingType =ORMappingType.ONE_TO_MANY,relateClass=TBedLengthType.class)
	private List<TBedLengthType> bedLengthType;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public List<TBedLengthType> getBedLengthType() {
		return bedLengthType;
	}
	public void setBedLengthType(List<TBedLengthType> bedLengthType) {
		this.bedLengthType = bedLengthType;
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
		TBedType other = (TBedType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
