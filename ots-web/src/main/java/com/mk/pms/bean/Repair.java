package com.mk.pms.bean;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author shellingford
 * @version 2015年1月9日
 */
public class Repair implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6489864610199832424L;
	
	private String id;
	private String roomPmsNo;
	private Date beginTime;
	private Date endTime;
	private boolean delete=false;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRoomPmsNo() {
		return roomPmsNo;
	}
	public void setRoomPmsNo(String roomPmsNo) {
		this.roomPmsNo = roomPmsNo;
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
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
}
