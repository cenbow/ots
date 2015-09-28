package com.mk.framework.util;

import java.text.MessageFormat;
import java.util.UUID;

/**
 * 字符串工具类
 * @author nolan
 *
 */
public class StringUtils {

	/***/
    public String strSuper;

    /***/
    protected String lowcaseSuper;
    
	/**
	 * 格式化字符串
	 * @param str
	 * @param args
	 * @return
	 */
	public static String getMessage(String str, Object... args) {
        return  MessageFormat.format(str, args);
    }
	
	/**
	 * get UUID
	 * @param is32bit
	 * @return String
	 */
    public static String getUuidByJdk(boolean is32bit){
        String uuid = UUID.randomUUID().toString();
        if(is32bit){
            return uuid.toString().replace("-", ""); 
        }
        return uuid;
    }
	
}
