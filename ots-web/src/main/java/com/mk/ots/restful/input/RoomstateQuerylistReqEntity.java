package com.mk.ots.restful.input;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * roomstate/querylist房态查询接口入参实体类.
 * 
 * @author chuaiqing.
 *
 */
public class RoomstateQuerylistReqEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3720687190019288222L;

	@NotNull(message = "缺少参数-酒店id: hotelid")
	@Range(min = 1, message = "无效的酒店id,最小值为1")
	private Long hotelid;

	@Range(min = 1, message = "无效的房型id")
	private Long roomtypeid;

	@NotNull(message = "缺少参数-查询开始日期: startdateday")
	private String startdateday;

	@NotNull(message = "缺少参数-查询结束日期: enddateday")
	private String enddateday;

	private String startdate;

	private String enddate;

	private String roomstatus;

	private Integer bednum;

	private String orderby;

	/**
	 * the entry identifier which represents the search entrance
	 * 
	 * 1-摇一摇 2-房态搜索入口 3-qieke
	 */
	private Integer callentry;
	private String callmethod;
	private String callversion;
	//是否显示全部房间 T:是 F:否
	private String isShowAllRoom;

	public Integer getCallentry() {
		return callentry;
	}

	public void setCallentry(Integer callentry) {
		this.callentry = callentry;
	}

	public Long getHotelid() {
		return hotelid;
	}

	public void setHotelid(Long hotelid) {
		this.hotelid = hotelid;
	}

	public Long getRoomtypeid() {
		return roomtypeid;
	}

	public void setRoomtypeid(Long roomtypeid) {
		this.roomtypeid = roomtypeid;
	}

	public String getStartdateday() {
		return startdateday;
	}

	public void setStartdateday(String startdateday) {
		this.startdateday = startdateday;
	}

	public String getEnddateday() {
		return enddateday;
	}

	public void setEnddateday(String enddateday) {
		this.enddateday = enddateday;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getRoomstatus() {
		return roomstatus;
	}

	public void setRoomstatus(String roomstatus) {
		this.roomstatus = roomstatus;
	}

	public Integer getBednum() {
		return bednum;
	}

	public void setBednum(Integer bednum) {
		this.bednum = bednum;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}


	public String getCallmethod() {
		return callmethod;
	}

	public void setCallmethod(String callmethod) {
		this.callmethod = callmethod;
	}

	public String getCallversion() {
		return callversion;
	}

	public void setCallversion(String callversion) {
		this.callversion = callversion;
	}

	public String getIsShowAllRoom() {
		return isShowAllRoom;
	}

	public void setIsShowAllRoom(String isShowAllRoom) {
		this.isShowAllRoom = isShowAllRoom;
	}

	@Override
	public String toString() {
		return "RoomstateQuerylistReqEntity{" +
				"hotelid=" + hotelid +
				", roomtypeid=" + roomtypeid +
				", startdateday='" + startdateday + '\'' +
				", enddateday='" + enddateday + '\'' +
				", startdate='" + startdate + '\'' +
				", enddate='" + enddate + '\'' +
				", roomstatus='" + roomstatus + '\'' +
				", bednum=" + bednum +
				", orderby='" + orderby + '\'' +
				", callentry=" + callentry +
				", callmethod='" + callmethod + '\'' +
				", callversion='" + callversion + '\'' +
				", isShowAllRoom='" + isShowAllRoom + '\'' +
				'}';
	}
}
