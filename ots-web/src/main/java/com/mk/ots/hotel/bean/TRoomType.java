package com.mk.ots.hotel.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="t_roomtype", pkey="id")
public class TRoomType extends BizModel<TRoomType> {

	private static final long serialVersionUID = -6068810551606312549L;
	
	public static final String TABLE_NAME="t_roomtype";
	public static final TRoomType dao = new TRoomType();
//	
//	private Long id;
//	private String thotelid;
//	private String ehotelid;
//	private String name;
//	private String pms;
//	private Long bedNum;
//	private Long roomNum;
//	private BigDecimal cost;
//	//仅页面action使用
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getPms() {
//		return pms;
//	}
//	public void setPms(String pms) {
//		this.pms = pms;
//	}
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		TRoomType other = (TRoomType) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//	public Long getBedNum() {
//		return bedNum;
//	}
//	public void setBedNum(Long bedNum) {
//		this.bedNum = bedNum;
//	}
//	public Long getRoomNum() {
//		return roomNum;
//	}
//	public void setRoomNum(Long roomNum) {
//		this.roomNum = roomNum;
//	}
//	public BigDecimal getCost() {
//		return cost;
//	}
//	public void setCost(BigDecimal cost) {
//		this.cost = cost;
//	}
//	public String getThotelid() {
//		return thotelid;
//	}
//	public void setThotelid(String thotelid) {
//		this.thotelid = thotelid;
//	}
//	public String getEhotelid() {
//		return ehotelid;
//	}
//	public void setEhotelid(String ehotelid) {
//		this.ehotelid = ehotelid;
//	}
	
}
