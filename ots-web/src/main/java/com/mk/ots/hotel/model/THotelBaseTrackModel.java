/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 * 2015年6月26日下午1:50:49
 * zhaochuanbin
 */
package com.mk.ots.hotel.model;

import java.util.Date;

/**
 * @author zhaochuanbin
 *
 */
public class THotelBaseTrackModel {
	
	private Long id;
	private Long hotelid;
	private String hotelname;
	private Integer roomcnt;
	private String content;
	private Date createtime;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the hotelid
	 */
	public Long getHotelid() {
		return hotelid;
	}
	/**
	 * @param hotelid the hotelid to set
	 */
	public void setHotelid(Long hotelid) {
		this.hotelid = hotelid;
	}
	/**
	 * @return the hotelname
	 */
	public String getHotelname() {
		return hotelname;
	}
	/**
	 * @param hotelname the hotelname to set
	 */
	public void setHotelname(String hotelname) {
		this.hotelname = hotelname;
	}
	/**
	 * @return the roomcnt
	 */
	public Integer getRoomcnt() {
		return roomcnt;
	}
	/**
	 * @param roomcnt the roomcnt to set
	 */
	public void setRoomcnt(Integer roomcnt) {
		this.roomcnt = roomcnt;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the createtime
	 */
	public Date getCreatetime() {
		return createtime;
	}
	/**
	 * @param createtime the createtime to set
	 */
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	
	
}
