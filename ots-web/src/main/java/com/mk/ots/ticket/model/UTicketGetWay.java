package com.mk.ots.ticket.model;

import javax.validation.constraints.NotNull;

/**
 * 用户优惠券获取记录
 * 
 * @author nolan
 *
 */
public class UTicketGetWay {
	/**
	 * 主键
	 */
	@NotNull
	private String id;
	
	/**
	 * 获取时间
	 */
	@NotNull
	private String actiontime;
	
	/**
	 * 用户券id
	 */
	private String tid;
	
	/**
	 * 活动id
	 */
	private String activityid;
	
	public UTicketGetWay() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActiontime() {
		return actiontime;
	}

	public void setActiontime(String actiontime) {
		this.actiontime = actiontime;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getActivityid() {
		return activityid;
	}

	public void setActivityid(String activityid) {
		this.activityid = activityid;
	}
}
