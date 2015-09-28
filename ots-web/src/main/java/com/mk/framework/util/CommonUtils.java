package com.mk.framework.util;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;


/**
 * 常用工具类
 * 
 * @author zhangliang
 * @version $Revision: 1474 $
 */
public class CommonUtils {
	
    /**
     * 判断集合是否为NULL或空
     * 
     * @param collection
     *            集合
     * @return 如果此collection为null或不包含元素，则返回 true
     */
    public static boolean isNullOrEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断MAP是否为NULL或空
     * 
     * @param map
     *            MAP
     * @return 如果此map为null或不包含元素，则返回 true
     */
    public static boolean isNullOrEmpty(Map map) {
        return map == null || map.isEmpty();
    }
    
    /**
     * 判断map中数据是否存在
     * @param map map对象
     * @param key 键值
     * @param defaultValue 默认值
     * @return 空为键值不存在
     */
    public static String getMapValue(Map map, String key, String defaultValue) {
    	return (String) (isNullOrEmpty(map) ? defaultValue 
    			: isNullOrEmpty((String)map.get(key)) ? defaultValue : map.get(key));
    }
    
    public static String getMapValue(Map map, String key) {
    	return getMapValue(map, key, "");
    }

    /**
     * 判断字符串是否为NULL或空
     * 
     * @param s
     *            字符串
     * @return 如果此map为null或空，则返回 true
     */
    public static boolean isNullOrEmpty(String s) {
        return Strings.isNullOrEmpty(s);
    }


    /**
     * 判断Object是否为NULL
     * 
     * @param obj
     *            Object
     * @return true/false
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断字符串数组是否为NULL或空
     * 
     * @param s
     *            字符串数组
     * @return 如果此map为null或空，则返回 true
     */
    public static boolean isNullOrEmptys(Object[] s) {
        return s == null || s.length == 0;
    }

    /**
     * 值空处理
     * 
     * @param value
     *            原始值
     * @return 处理值
     */
    public static String fixNull(String value) {
        return value == null || value.toUpperCase().equals("NULL") ? "" : value;
    }

    /**
     * 是否数字
     * 
     * @param str
     *            字符串
     * @return true/false
     */
    public static boolean isDigit(String str) {
        Pattern pattern = Pattern.compile("[0-9]+(.[0-9]+)?");
        Matcher isNum = pattern.matcher(str.trim());
        return isNum.matches();
    }
}