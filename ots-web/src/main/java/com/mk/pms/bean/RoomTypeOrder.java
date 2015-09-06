package com.mk.pms.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author shellingford
 * @version 2015年1月9日
 */
public class RoomTypeOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7369798891418839836L;

	private String orderid;
	private boolean delete=false;
	private String roomTypePms;
	private Integer bookingcnt;
	private Integer roomingcnt;
	private Integer checkincnt;
	private BigDecimal price;
	private Date begintime;
	private Date endtime;
	private String opuser;
	private String batchno;
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public String getRoomTypePms() {
		return roomTypePms;
	}
	public void setRoomTypePms(String roomTypePms) {
		this.roomTypePms = roomTypePms;
	}
	public Integer getBookingcnt() {
		return bookingcnt;
	}
	public void setBookingcnt(Integer bookingcnt) {
		this.bookingcnt = bookingcnt;
	}
	public Integer getRoomingcnt() {
		return roomingcnt;
	}
	public void setRoomingcnt(Integer roomingcnt) {
		this.roomingcnt = roomingcnt;
	}
	public Integer getCheckincnt() {
		return checkincnt;
	}
	public void setCheckincnt(Integer checkincnt) {
		this.checkincnt = checkincnt;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Date getBegintime() {
		return begintime;
	}
	public void setBegintime(Date begintime) {
		this.begintime = begintime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public String getOpuser() {
		return opuser;
	}
	public void setOpuser(String opuser) {
		this.opuser = opuser;
	}
	public String getBatchno() {
		return batchno;
	}
	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}
	
}
