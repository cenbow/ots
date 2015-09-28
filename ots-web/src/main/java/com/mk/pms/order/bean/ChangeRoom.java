package com.mk.pms.order.bean;

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.PmsRoomOrder;

/** 
 * 
 * @author lisw
 * @version 2015年05月13日
 */
//@DbClassMapping("o_changeroom")
/**
 * b_otaorder
 */
@Component
@DbTable(name="o_changeroom", pkey="id")
public class ChangeRoom extends BizModel<ChangeRoom> {

	private static final long serialVersionUID = -7160547857240730829L;
	public static final ChangeRoom dao = new ChangeRoom();
	
	public ChangeRoom saveOrUpdate(){
		if (get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}
//	private Long id;
//	private Long hotelId;
//	private String hotelName;
////	private PmsRoomOrder pmsRoomOrder;
//	private Long oldRoomId;
//	private Long newRoomId;
//	private String oldRoomNo;
//	private String newRoomNo;
//	private Long oldRoomTypeId;
//	private String oldRoomTypeName;
//	private String newRoomTypeId;
//	private String newRoomTypeName;
//	private Date changeTime;
//	
//	
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public Long getHotelId() {
//		return hotelId;
//	}
//	public void setHotelId(Long hotelId) {
//		this.hotelId = hotelId;
//	}
//	public String getHotelName() {
//		return hotelName;
//	}
//	public void setHotelName(String hotelName) {
//		this.hotelName = hotelName;
//	}
////	public String getHotelPms() {
////		return hotelPms;
////	}
////	public void setHotelPms(String hotelPms) {
////		this.hotelPms = hotelPms;
////	}
//	public PmsRoomOrder getPmsRoomOrder() {
//		return pmsRoomOrder;
//	}
//	public void setPmsRoomOrder(PmsRoomOrder pmsRoomOrder) {
//		this.pmsRoomOrder = pmsRoomOrder;
//	}
//	public String getOldRoomNo() {
//		return oldRoomNo;
//	}
//	public void setOldRoomNo(String oldRoomNo) {
//		this.oldRoomNo = oldRoomNo;
//	}
//	public String getNewRoomNo() {
//		return newRoomNo;
//	}
//	public void setNewRoomNo(String newRoomNo) {
//		this.newRoomNo = newRoomNo;
//	}
//	public Long getOldRoomTypeId() {
//		return oldRoomTypeId;
//	}
//	public void setOldRoomTypeId(Long oldRoomTypeId) {
//		this.oldRoomTypeId = oldRoomTypeId;
//	}
//	public String getOldRoomTypeName() {
//		return oldRoomTypeName;
//	}
//	public void setOldRoomTypeName(String oldRoomTypeName) {
//		this.oldRoomTypeName = oldRoomTypeName;
//	}
//	public String getNewRoomTypeId() {
//		return newRoomTypeId;
//	}
//	public void setNewRoomTypeId(String newRoomTypeId) {
//		this.newRoomTypeId = newRoomTypeId;
//	}
//	public String getNewRoomTypeName() {
//		return newRoomTypeName;
//	}
//	public void setNewRoomTypeName(String newRoomTypeName) {
//		this.newRoomTypeName = newRoomTypeName;
//	}
//	public Date getChangeTime() {
//		return changeTime;
//	}
//	public void setChangeTime(Date changeTime) {
//		this.changeTime = changeTime;
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
//		ChangeRoom other = (ChangeRoom) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//	public Long getOldRoomId() {
//		return oldRoomId;
//	}
//	public void setOldRoomId(Long oldRoomId) {
//		this.oldRoomId = oldRoomId;
//	}
//	public Long getNewRoomId() {
//		return newRoomId;
//	}
//	public void setNewRoomId(Long newRoomId) {
//		this.newRoomId = newRoomId;
//	}

}
