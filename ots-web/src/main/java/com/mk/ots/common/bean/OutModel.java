package com.mk.ots.common.bean;

import java.util.List;

/**
 * 返回页面所用对象
 * @author hdy
 *
 */
public class OutModel {

	private Boolean success; //成功标志
	private String context; //返回文本内容
	private Integer errorCode; //错误编码
	private String errorMsg; //错误信息
	private Boolean system; // 是否是系统提示
	private Object attribute; //自定义属性值
	private List<Object> dataList; //数据列表

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	public Object getAttribute() {
		return attribute;
	}

	public void setAttribute(Object attribute) {
		this.attribute = attribute;
	}

	public List<Object> getDataList() {
		return dataList;
	}

	public void setDataList(List<Object> dataList) {
		this.dataList = dataList;
	}
}
