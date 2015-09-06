package com.mk.framework.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;



/**
 * 提供基础的工具核心类
 * @author birkhoff
 *
 */
public class ComUtils {	
	
	/**
	 * 
	 * @desc 将POJO对象转成Map
	 * @date 2013-10-16 上午11:38:50
	 *
	 * @param POJO对象
	 * @return Map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map pojoToMap(Object obj) {
		Map hashMap = new HashMap();
		try {
			Class c = obj.getClass();
			Method m[] = c.getDeclaredMethods();
			
			for (int i = 0; i < m.length; i++) {
				if (m[i].getName().indexOf("get") == 0) {
					hashMap
							.put(m[i].getName().substring(3,4).toLowerCase()+m[i].getName().substring(4), m[i]
									.invoke(obj, new Object[0]));
				}
			}
			if(!"java.lang.Object".equals(c.getSuperclass().getName())){
				Method sm[] = c.getSuperclass().getDeclaredMethods();
				
				for (int i = 0; i < sm.length; i++) {
					if (sm[i].getName().indexOf("get") == 0) {
						hashMap
								.put(sm[i].getName().substring(3,4).toLowerCase()+sm[i].getName().substring(4), sm[i]
										.invoke(obj, new Object[0]));
					}
				}
			}
			
		} catch (Exception e) {
			System.err.println(e);
		}
		return hashMap;
	}
	
	/**
	 * 利用反射实现对象之间属性复制 
	 * @param from 源对象
	 * @param to 目标对象
	 * @throws Exception
	 */
	public static void copyProperties(Object from, Object to) throws Exception {  
        copyPropertiesExclude(from, to, null);  
    }
	
	/**
	 * 复制对象属性
	 * @param from 源对象
	 * @param to 目标对象
	 * @param excludsArray 排除对象列表
	 * @throws Exception
	 */
	public static void copyPropertiesExclude(Object from, Object to, String[] excludsArray) throws Exception {  
        List<String> excludesList = null;  
        if(excludsArray != null && excludsArray.length > 0) {  
            excludesList = Arrays.asList(excludsArray); //构造列表对象  
        }  
        Method[] fromMethods = from.getClass().getDeclaredMethods();  
        Method[] toMethods = to.getClass().getDeclaredMethods();  
        Method fromMethod = null, toMethod = null;  
        String fromMethodName = null, toMethodName = null;  
        for (int i = 0; i < fromMethods.length; i++) {  
            fromMethod = fromMethods[i];  
            fromMethodName = fromMethod.getName();  
            if (!fromMethodName.contains("get")){
            	continue;  
            }
            //排除列表检测  
            if(excludesList != null && excludesList.contains(fromMethodName.substring(3).toLowerCase())) {  
                continue;  
            }  
            toMethodName = "set" + fromMethodName.substring(3);  
            toMethod = findMethodByName(toMethods, toMethodName);  
            if (toMethod == null) {
            	continue;  
            }
            Object value = fromMethod.invoke(from, new Object[0]);  
            if(value == null)  
                continue;  
            //集合类判空处理  
            if(value instanceof Collection) {  
                Collection newValue = (Collection)value;  
                if(newValue.size() <= 0){
                	continue;  
                }  
            }  
            toMethod.invoke(to, new Object[] {value});  
        }  
    }  
	/** 
     * 对象属性值复制，仅复制指定名称的属性值 
     * @param from  源对象
     * @param to 目标对象
     * @param includsArray 包含对象列表 
     * @throws Exception 
     */  
    @SuppressWarnings("unchecked")  
    public static void copyPropertiesInclude(Object from, Object to, String[] includsArray) throws Exception {  
        List<String> includesList = null;  
        if(includsArray != null && includsArray.length > 0) {  
            includesList = Arrays.asList(includsArray); //构造列表对象  
        } else {  
            return;  
        }  
        Method[] fromMethods = from.getClass().getDeclaredMethods();  
        Method[] toMethods = to.getClass().getDeclaredMethods();  
        Method fromMethod = null, toMethod = null;  
        String fromMethodName = null, toMethodName = null;  
        for (int i = 0; i < fromMethods.length; i++) {  
            fromMethod = fromMethods[i];  
            fromMethodName = fromMethod.getName();  
            if (!fromMethodName.contains("get")){
            	continue;  
            }  
            //排除列表检测  
            String str = fromMethodName.substring(3);  
            if(!includesList.contains(str.substring(0,1).toLowerCase() + str.substring(1))) {  
                continue;  
            }  
            toMethodName = "set" + fromMethodName.substring(3);  
            toMethod = findMethodByName(toMethods, toMethodName);  
            if (toMethod == null){
            	continue;  
            }  
            Object value = fromMethod.invoke(from, new Object[0]);  
            if(value == null){
            	continue;  
            }  
            //集合类判空处理  
            if(value instanceof Collection) {  
                Collection newValue = (Collection)value;  
                if(newValue.size() <= 0){
                	continue;  
                }  
            }  
            toMethod.invoke(to, new Object[] {value});  
        }  
    }  
      
      
  
    /** 
     * 从方法数组中获取指定名称的方法 
     *  
     * @param methods 
     * @param name 
     * @return 
     */  
    public static Method findMethodByName(Method[] methods, String name) {  
        for (int j = 0; j < methods.length; j++) {  
            if (methods[j].getName().equals(name))  
                return methods[j];  
        }  
        return null;  
    }  
    /**
	 * Converts a database name (table or column) to a java name (first letter
	 * capitalised). employee_name -> EmployeeName.
	 * 
	 * Derived from middlegen's dbnameconverter.
	 * 
	 * @param s
	 *            The database name to convert.
	 * 
	 * @return The converted database name.
	 */
    public static String getJavaNameFromDBColumnName(String s) {
		if ("".equals(s)) {
			return s;
		}
		StringBuffer result = new StringBuffer();

		boolean capitalize = true;
		boolean lastCapital = false;
		boolean lastDecapitalized = false;
		String p = null;
		for (int i = 0; i < s.length(); i++) {
			String c = s.substring(i, i + 1);
			if ("_".equals(c) || " ".equals(c) || "-".equals(c)) {
				capitalize = true;
				continue;
			}

			if (c.toUpperCase().equals(c)) {
				if (lastDecapitalized && !lastCapital) {
					capitalize = true;
				}
				lastCapital = true;
			} else {
				lastCapital = false;
			}

			// if(forceFirstLetter && result.length()==0) capitalize = false;

			if (capitalize) {
				if (p == null || !p.equals("_")) {
					result.append(c.toUpperCase());
					capitalize = false;
					p = c;
				} else {
					result.append(c.toLowerCase());
					capitalize = false;
					p = c;
				}
			} else {
				result.append(c.toLowerCase());
				lastDecapitalized = true;
				p = c;
			}

		}
		String r = result.toString();
		return r;
	}
    /**
     * model properties change to db column Name
     * @param str
     * @return
     */
    public static String getDBColumnNameFromJavaName(String str){
		StringBuffer newStr = new StringBuffer();
		char[] str1 = str.toCharArray();
		for(int i =  0 ;i < str1.length ; i++ ){	
			if('A' <= str1[i] && str1[i] <= 'Z'){
				newStr.append("_" + String.valueOf(str1[i]).toLowerCase());
			} else {
				newStr.append(String.valueOf(str1[i]));
			}
		 }
		return newStr.toString().toUpperCase();
	}
    
}
