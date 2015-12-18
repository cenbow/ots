package com.mk.ots.remind.model;

import java.util.Date;

public class RemindLog {
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRemindId() {
		return remindId;
	}

	public void setRemindId(Long remindId) {
		this.remindId = remindId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String stateCode) {
		this.statusCode = stateCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	private Long id;
	private Long remindId;
	private String content;
	private String statusCode;
	private Date createTime;
	private String descr;
}
