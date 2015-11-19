package com.mk.framework.component.message.webservice;

public class RespInfo {
	private int httpStatus;
	private String respVal;

	public RespInfo(int httpStatus, String respVal) {
		this.httpStatus = httpStatus;
		this.respVal = respVal;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getRespVal() {
		return respVal;
	}

	public void setRespVal(String respVal) {
		this.respVal = respVal;
	}

}
