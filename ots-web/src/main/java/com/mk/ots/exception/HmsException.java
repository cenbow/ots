package com.mk.ots.exception;

public class HmsException extends Exception {

	private static final long serialVersionUID = 2626908725247678135L;

	private int errorCode = -1;

	public HmsException(int errorCode, Throwable cause) {
		super(cause);
		this.setErrorCode(errorCode);
	}

	public HmsException(String message){
		super(message);
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	private void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
