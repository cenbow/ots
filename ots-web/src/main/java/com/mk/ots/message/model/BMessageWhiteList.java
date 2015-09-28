package com.mk.ots.message.model;

import java.util.Date;

/**
 * 中间类
 * 推送信息时如果是测试环境，传来的手机号要判断是否在该表中如果在就不会发送。如果正式不用判断就发送
 * @author zhangyajun
 *
 */
public class BMessageWhiteList {

	private Long id;
	private String phone;
	private String type;
	private Date createtime;
	private String createby;
	private Date updatetime;
	private String  updateby;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getCreateby() {
		return createby;
	}
	public void setCreateby(String createby) {
		this.createby = createby;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getUpdateby() {
		return updateby;
	}
	public void setUpdateby(String updateby) {
		this.updateby = updateby;
	}
	@Override
	public String toString() {
		return "BMessageWhiteList [id=" + id + ", phone=" + phone + ", type="
				+ type + ", createtime=" + createtime + ", createby="
				+ createby + ", updatetime=" + updatetime + ", updateby="
				+ updateby + "]";
	}
	
}
