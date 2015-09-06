package com.mk.ots.order.model;

import java.util.Date;

public class OtaOrderTasts {
	private Long id;

	private Long otaorderid;

	private Long hotelid;

	private Integer status;

	private Integer tasktype;

	private Integer count;

	private Date createtime;

	private Date updatetime;
	private Date executeTime;

	private String content;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOtaorderid() {
		return this.otaorderid;
	}

	public void setOtaorderid(Long otaorderid) {
		this.otaorderid = otaorderid;
	}

	public Long getHotelid() {
		return this.hotelid;
	}

	public void setHotelid(Long hotelid) {
		this.hotelid = hotelid;
	}

	public Integer getStatus() {
		return this.status;
	}

	/**
	 * 枚举：OrderTasksStatusEnum
	 * 
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTasktype() {
		return this.tasktype;
	}

	/**
	 * 枚举：OrderTasksTypeEnum
	 * 
	 * @param tasktype
	 */
	public void setTasktype(Integer tasktype) {
		this.tasktype = tasktype;
	}

	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public Date getExecuteTime() {
		return this.executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}
}