package com.mk.framework.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.hotel.bean.VerifyRecord;


/**
 * @author shellingford
 * @version 创建时间：2012-3-19 下午5:44:00
 * 
 */
public class MySession implements Serializable{
	private static final long serialVersionUID = 1L;
	private String sessionId;
	private String language="zh";
	private String country="CN";
	private String ip;
	private String loginUserId;//已经登录用户 mid
	private Long loinTimeLong=null;
//	private Date loginTime=new Date();
	private Map<String,VerifyRecord> verifyRecordMap;
	private OSTypeEnum osType;
	private Integer version;//版本号
	/**
	 * 注销逻辑，需要清空seesion中的内容
	 */
	public void logout(){
		this.loginUserId=null;
		this.setVerifyRecordMap(null);
	}
	
	/**
	 * 登录逻辑 
	 * @param accesstoken 
	 */
	public void login(String mId, OSTypeEnum osType,Integer version) {
		this.loginUserId = mId;
		this.osType = osType;
		this.version = version;
		this.loinTimeLong=new Date().getTime();
//		this.loginTime=new Date();
	}
	
	public String displayDebug(){
		return "["+this.sessionId+"]["+isLogin()+"]";
	}
	
	public boolean isLogin() {
		if(loginUserId == null){
			return false;
		}
		return true;
	}
	
	public MySession(String id) {
		sessionId=id;
	}

	public MySession(){
		
	}
	
	public SimpleDateFormat createSimpleDateFormat(){
		if(country==null || "CN".equals(country)){
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else{
			return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		}
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public Map<String, VerifyRecord> getVerifyRecordMap() {
		if(verifyRecordMap == null){
			verifyRecordMap = new HashMap<String,VerifyRecord>();
		}
		return verifyRecordMap;
	}

	public void setVerifyRecordMap(Map<String, VerifyRecord> verifyRecordMap) {
		this.verifyRecordMap = verifyRecordMap;
	}

	public String getLoginUserId() {
		return loginUserId;
	}

//	public void setLoginUserId(Long loginUserId) {
//		this.loginUserId = loginUserId;
//	}

	public Date getLoginTime() {
		if(this.loinTimeLong==null){
			return null;
		}
		return new Date(this.loinTimeLong);
	}

//	public void setLoginTime(Date loginTime) {
//		this.loginTime = loginTime;
//	}

	public OSTypeEnum getOsType() {
		return osType;
	}

	public void setOsType(OSTypeEnum osType) {
		this.osType = osType;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
