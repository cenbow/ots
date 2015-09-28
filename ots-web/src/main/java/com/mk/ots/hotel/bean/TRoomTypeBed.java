package com.mk.ots.hotel.bean;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="t_roomtype_bed", pkey="id")
public class TRoomTypeBed extends BizModel<TRoomTypeBed>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3382066991811056983L;

	public static final String TABLE_NAME="t_roomtype_bed";
	public static TRoomTypeBed dao = new TRoomTypeBed();
	
	private Long id;
	private TRoomType roomType;
	private Integer num;
	private TBedType bedtype;
	private String otherinfo;
	private List<TRoomTypeBedLength> roomTypeBedLengthList;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TRoomType getRoomType() {
		return roomType;
	}
	public void setRoomType(TRoomType roomType) {
		this.roomType = roomType;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public TBedType getBedtype() {
		return bedtype;
	}
	public void setBedtype(TBedType bedtype) {
		this.bedtype = bedtype;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	public List<TRoomTypeBedLength> getRoomTypeBedLengthList() {
		return roomTypeBedLengthList;
	}
	public void setRoomTypeBedLengthList(
			List<TRoomTypeBedLength> roomTypeBedLengthList) {
		this.roomTypeBedLengthList = roomTypeBedLengthList;
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
		TRoomTypeBed other = (TRoomTypeBed) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
