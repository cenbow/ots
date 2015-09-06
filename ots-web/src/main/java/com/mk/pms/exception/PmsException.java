package com.mk.pms.exception;

import com.mk.pms.myenum.PmsErrorEnum;

/** 
 * 
 * @author shellingford
 * @version 2014年10月30日
 */

public class PmsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4339970938334539998L;
	private final String errorCode;
	private final String errorMessage;
	
	public PmsException(String errorCode,String errorMessage){
		super("errorCode:"+errorCode+"  errorMsg:"+errorMessage);
		this.errorCode=errorCode;
		this.errorMessage=errorMessage;
	}
	
	public PmsErrorEnum findPmsErrorEnum(){
		return PmsErrorEnum.findPmsErrorEnumByCode(errorCode);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
