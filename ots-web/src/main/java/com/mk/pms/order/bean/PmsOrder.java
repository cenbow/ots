package com.mk.pms.order.bean;

import java.io.Serializable;
import java.util.Date;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */
//@DbClassMapping("b_pmsorder")
public class PmsOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8751598892103491908L;
	
	private Long id;
	private Date createTime;
	private Date updateTime;
	private String pmsOrderNo;
	private Long hotelId;
	private String hotelPms;
	private String pmsRoomTypeOrderNo;
	private Long roomTypeId;
	private String roomTypePms;
	private Date beginTime;
	private Date endTime;
	private Integer orderNum;
	private Integer planNum;
	private Integer checkInNum;
	private Boolean cancel;
	private String opuser;
	private Boolean visible;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getPmsOrderNo() {
		return pmsOrderNo;
	}
	public void setPmsOrderNo(String pmsOrderNo) {
		this.pmsOrderNo = pmsOrderNo;
	}
	public Long getHotelId() {
		return hotelId;
	}
	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}
	public String getHotelPms() {
		return hotelPms;
	}
	public void setHotelPms(String hotelPms) {
		this.hotelPms = hotelPms;
	}
	public String getPmsRoomTypeOrderNo() {
		return pmsRoomTypeOrderNo;
	}
	public void setPmsRoomTypeOrderNo(String pmsRoomTypeOrderNo) {
		this.pmsRoomTypeOrderNo = pmsRoomTypeOrderNo;
	}
	public Long getRoomTypeId() {
		return roomTypeId;
	}
	public void setRoomTypeId(Long roomTypeId) {
		this.roomTypeId = roomTypeId;
	}
	public String getRoomTypePms() {
		return roomTypePms;
	}
	public void setRoomTypePms(String roomTypePms) {
		this.roomTypePms = roomTypePms;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	public Integer getPlanNum() {
		return planNum;
	}
	public void setPlanNum(Integer planNum) {
		this.planNum = planNum;
	}
	public Integer getCheckInNum() {
		return checkInNum;
	}
	public void setCheckInNum(Integer checkInNum) {
		this.checkInNum = checkInNum;
	}
	public Boolean getCancel() {
		return cancel;
	}
	public void setCancel(Boolean cancel) {
		this.cancel = cancel;
	}
	public String getOpuser() {
		return opuser;
	}
	public void setOpuser(String opuser) {
		this.opuser = opuser;
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
		PmsOrder other = (PmsOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

}
