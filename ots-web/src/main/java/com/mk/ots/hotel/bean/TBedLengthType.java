package com.mk.ots.hotel.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/** 
 * 
 * @author shellingford
 * @version 2014年11月5日
 */
//@DbClassMapping("t_bedlengthtype")
public class TBedLengthType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2409212244005943737L;
	//@DbFieldMapping(dbColName = "id", isPrimaryKey = true, isAutoPrimaryKey=true)
	private Long id;
	//@DbFieldMapping(dbColName = "bedtypeid" ,ORMappingType = ORMappingType.MANY_TO_ONE, relateClass = TBedType.class)
	private TBedType bedType;
	//@DbFieldMapping(dbColName = "name")
	private BigDecimal name;
	//@DbFieldMapping(dbColName = "visible")
	private Boolean visible;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TBedType getBedType() {
		return bedType;
	}
	public void setBedType(TBedType bedType) {
		this.bedType = bedType;
	}
	public BigDecimal getName() {
		return name;
	}
	public void setName(BigDecimal name) {
		this.name = name;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
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
		TBedLengthType other = (TBedLengthType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
