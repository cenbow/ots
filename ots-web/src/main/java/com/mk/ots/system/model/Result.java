package com.mk.ots.system.model;

import java.io.Serializable;

/**
 * 服务返回对象
 *
 * @author 须俊杰
 */
public class Result implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean success;
	/**
	 * 返回类型
	 */
	protected String code;
    
    /**
     * 描述
     */
    protected String reason;
    
    /**
     * 返回数据
     */
    protected String data;

	/**
	 * 获取返回码
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置返回码
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
     * 获取原因
     * @return
     */
	public String getReason() {
		return reason;
	}

	/**
	 * 设置原因
	 * @param reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * 获取数据
	 * @return
	 */
	public String getData() {
		return data;
	}

	/**
	 * 设置数据
	 * @param data
	 */
	public void setData(String data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
