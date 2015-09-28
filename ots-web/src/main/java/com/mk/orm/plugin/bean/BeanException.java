/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

/**
 * BeanException
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class BeanException extends RuntimeException {
    
    
    /**
     * 
     */
    private static final long serialVersionUID = 342820722361408621L;

    public BeanException(String message) {
        super(message);
    }
    
    public BeanException(Throwable cause) {
        super(cause);
    }
    
    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
