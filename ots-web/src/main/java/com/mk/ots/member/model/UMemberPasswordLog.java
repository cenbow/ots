package com.mk.ots.member.model;

import java.io.Serializable;
import java.util.Date;

import com.mk.ots.common.enums.MemberPasswordLogEnum;

/** 
 * 
 * @author shellingford
 * @version 2014年11月5日
 */
//("u_member_passwordlog")
public class UMemberPasswordLog implements Serializable{

	private static final long serialVersionUID = -4543979925223914056L;
	//(dbColName = "id", isPrimaryKey = true, isAutoPrimaryKey=true)
	private String id;
	//(dbColName = "mid")
	private String mid;
	//(dbColName = "type")
	private MemberPasswordLogEnum type;
	//(dbColName = "newpassword")
	private String newPassword;
	//(dbColName = "time",saveBigintByDate=true)
	private Date time;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public MemberPasswordLogEnum getType() {
		return type;
	}
	public void setType(MemberPasswordLogEnum type) {
		this.type = type;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

}
