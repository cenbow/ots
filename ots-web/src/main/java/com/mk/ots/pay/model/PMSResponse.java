package com.mk.ots.pay.model;

public class PMSResponse {
	
	private boolean success;
	
	private String errorcode;
	
	private String errormsg;
	
	private String repayid;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getRepayid() {
		return repayid;
	}

	public void setRepayid(String repayid) {
		this.repayid = repayid;
	}

}
