package com.mk.ots.hotel.bean;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author JGroup
 * @date 2015年1月8日下午6:01:36
 */
public class VerifyRecord implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String phoneNum;
	private String phoneVerifyCode;
	private Date generateTime;
	private Long millseconds = 3600000L;// 有效时间 毫秒
	private boolean check = false;// 默认没有验证

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getPhoneVerifyCode() {
		return phoneVerifyCode;
	}

	public void setPhoneVerifyCode(String phoneVerifyCode) {
		this.phoneVerifyCode = phoneVerifyCode;
	}

	public Date getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(Date generateTime) {
		this.generateTime = generateTime;
	}

	public Long getMillseconds() {
		return millseconds;
	}

	public void setMillseconds(Long millseconds) {
		this.millseconds = millseconds;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

}
