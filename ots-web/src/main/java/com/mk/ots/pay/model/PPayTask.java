package com.mk.ots.pay.model;

import java.io.Serializable;
import java.util.Date;

import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;

public class PPayTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long orderId;
	
	private String content;
	
	private PayTaskStatusEnum status;
	
	private PayTaskTypeEnum taskType;
	
	private Date createTime;
	
	private Date executeTime;
	
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public PayTaskStatusEnum getStatus() {
		return status;
	}

	public void setStatus(PayTaskStatusEnum status) {
		this.status = status;
	}

	public PayTaskTypeEnum getTaskType() {
		return taskType;
	}

	public void setTaskType(PayTaskTypeEnum taskType) {
		this.taskType = taskType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
