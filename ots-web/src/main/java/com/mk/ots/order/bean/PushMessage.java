package com.mk.ots.order.bean;

import com.mk.care.kafka.model.Message;
import com.mk.ots.message.model.MessageType;

/**
 * 推送信息组装类
 * @author zhiwei
 *
 */
public class PushMessage {
	//电话
	private String phone;
	//标题
	private String title;
	//内容
	private String msgContent;
	//类型
	private String msgtype=MessageType.USER.getId();
	
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public Boolean getIsSms() {
		return isSms;
	}
	public void setIsSms(Boolean isSms) {
		this.isSms = isSms;
	}
	private Message message;
	
	private Boolean isSms=false; 
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	

}
