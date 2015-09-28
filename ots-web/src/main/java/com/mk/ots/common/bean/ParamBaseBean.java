package com.mk.ots.common.bean;

import java.io.Serializable;

/**
 * @author he
 * C端传参公共基类
 */
public class ParamBaseBean implements Serializable{
	
	/**
     * 序列化UID
     */
    private static final long serialVersionUID = 7708311749011307605L;
    
    /**
	 * 调用来源
	 * 1-crs客服；2-web；3-wechat；4-app(ios)；5-app(Android) 
	 */
	private String callmethod;
	
	/**
	 * 调用版本
	 */
	private String callversion;
	
	/**
	 * ip地址
	 */
	private String ip;
	
	/**
	 * 硬件编码
	 */
	private String hardwarecode;
	
	/** OTS版本 */
	private String otsversion;
	
	
	public String getHardwarecode() {
		return hardwarecode;
	}
	public void setHardwarecode(String hardwarecode) {
		this.hardwarecode = hardwarecode;
	}
	public String getCallmethod() {
		return callmethod;
	}
	public void setCallmethod(String callmethod) {
		this.callmethod = callmethod;
	}
	public String getCallversion() {
		return callversion;
	}
	public void setCallversion(String callversion) {
		this.callversion = callversion;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getOtsversion() {
        return otsversion;
    }
    public void setOtsversion(String otsversion) {
        this.otsversion = otsversion;
    }
    
    @Override
	public String toString() {
		return "ParamBaseBean [callmethod=" + callmethod 
		        + ", callversion=" + callversion
		        + ", ip=" + ip
		        + ", otsversion=" + otsversion
		        + "]";
	}
	
	
}
