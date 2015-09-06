package com.mk.ots.hotel.bean;

import java.io.Serializable;

/** 
 * 
 * @author shellingford
 * @version 2014年11月5日
 */
//@DbClassMapping("t_roomtyle_length")
public class TRoomTypeBedLength implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3970038948906338754L;
	//@DbFieldMapping(dbColName = "id", isPrimaryKey = true, isAutoPrimaryKey=true)
	private Long id;
	//@DbFieldMapping(dbColName = "bedlengthid" ,ORMappingType = ORMappingType.MANY_TO_ONE, relateClass = TBedLengthType.class)
	private TBedLengthType bedLengType;
	//@DbFieldMapping(dbColName = "roomtypebedid" ,ORMappingType = ORMappingType.MANY_TO_ONE, relateClass = TRoomTypeBed.class)
	private TRoomTypeBed roomTypeBed;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TBedLengthType getBedLengType() {
		return bedLengType;
	}
	public void setBedLengType(TBedLengthType bedLengType) {
		this.bedLengType = bedLengType;
	}
	public TRoomTypeBed getRoomTypeBed() {
		return roomTypeBed;
	}
	public void setRoomTypeBed(TRoomTypeBed roomTypeBed) {
		this.roomTypeBed = roomTypeBed;
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
		TRoomTypeBedLength other = (TRoomTypeBedLength) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
