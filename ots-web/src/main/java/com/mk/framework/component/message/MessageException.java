package com.mk.framework.component.message;

import java.io.Serializable;

/**
 * 
 * @author chuaiqing.
 *
 */
public class MessageException extends RuntimeException implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 312460104045869878L;
    
    private String errorCode;
    private String errorKey;
    
    public MessageException(String errorCode,String errorKey,String errorMsg) {
        super("errorCode:"+errorCode+"    errorMsg:"+errorMsg);
        this.errorCode=errorCode;
        this.errorKey=errorKey;
    }
    
    public MessageException(String errorCode,String errorKey){
        super("errorCode:"+errorCode);
        this.errorCode=errorCode;
        this.errorKey=errorKey;
    }
    
    public MessageErrorEnum getMyErrorEnum(){
        return MessageErrorEnum.findByCode(errorCode);
    }
    
    public final String getErrorKey() {
        return errorKey;
    }
    
    public final void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }
}
