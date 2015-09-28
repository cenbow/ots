package com.mk.pms.order.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.BizModel;

import cn.com.winhoo.mikeweb.myenum.PmsRoomOrderStatusEnum;
import cn.com.winhoo.mikeweb.myenum.PriceTypeEnum;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */
//@DbClassMapping("b_pmsroomorder")
public class PmsRoomOrder extends BizModel<PmsRoomOrder> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -684469568316741710L;
	private Long id;
	private String pmsRoomOrderNo;
	private Long hotelId;
	private String hotelPms;
	private PmsOrder pmsOrder;
	private PmsRoomOrderStatusEnum status;
	private String roomTypePms;
	private String pmsOrderNo;
	private Long roomTypeId;
	private String roomTypeName;
	private Long roomId;
	private String roomNo;
	private String roomPms;
	private Date beginTime;
	private Date endTime;
	private Date checkInTime;
	private Date checkOutTime;
	private PriceTypeEnum orderType;
	private BigDecimal roomCost;
	private BigDecimal otherCost;
	private BigDecimal mikePay;
	private BigDecimal otherPay;
	private String opuser;
	private Boolean visible;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPmsRoomOrderNo() {
		return pmsRoomOrderNo;
	}
	public void setPmsRoomOrderNo(String pmsRoomOrderNo) {
		this.pmsRoomOrderNo = pmsRoomOrderNo;
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
	public PmsOrder getPmsOrder() {
		return pmsOrder;
	}
	public void setPmsOrder(PmsOrder pmsOrder) {
		this.pmsOrder = pmsOrder;
	}
	public PmsRoomOrderStatusEnum getStatus() {
		return status;
	}
	public void setStatus(PmsRoomOrderStatusEnum status) {
		this.status = status;
	}
	public String getRoomTypePms() {
		return roomTypePms;
	}
	public void setRoomTypePms(String roomTypePms) {
		this.roomTypePms = roomTypePms;
	}
	public String getPmsOrderNo() {
		return pmsOrderNo;
	}
	public void setPmsOrderNo(String pmsOrderNo) {
		this.pmsOrderNo = pmsOrderNo;
	}
	public Long getRoomTypeId() {
		return roomTypeId;
	}
	public void setRoomTypeId(Long roomTypeId) {
		this.roomTypeId = roomTypeId;
	}
	public String getRoomTypeName() {
		return roomTypeName;
	}
	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}
	public String getRoomNo() {
		return roomNo;
	}
	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}
	public String getRoomPms() {
		return roomPms;
	}
	public void setRoomPms(String roomPms) {
		this.roomPms = roomPms;
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
	public Date getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(Date checkInTime) {
		this.checkInTime = checkInTime;
	}
	public Date getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(Date checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
	public PriceTypeEnum getOrderType() {
		return orderType;
	}
	public void setOrderType(PriceTypeEnum orderType) {
		this.orderType = orderType;
	}
	public BigDecimal getRoomCost() {
		return roomCost;
	}
	public void setRoomCost(BigDecimal roomCost) {
		this.roomCost = roomCost;
	}
	public BigDecimal getOtherCost() {
		return otherCost;
	}
	public void setOtherCost(BigDecimal otherCost) {
		this.otherCost = otherCost;
	}
	public BigDecimal getMikePay() {
		return mikePay;
	}
	public void setMikePay(BigDecimal mikePay) {
		this.mikePay = mikePay;
	}
	public BigDecimal getOtherPay() {
		return otherPay;
	}
	public void setOtherPay(BigDecimal otherPay) {
		this.otherPay = otherPay;
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
		PmsRoomOrder other = (PmsRoomOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
}
