package com.mk.ots.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author JGroup
 * @date   2015年1月8日下午3:09:28
 */
public class CheckTools {
	
	public static void main(String[] args) {
//		System.out.println(CheckTools.checkPhoneNum("16333333333"));
	}
	
	/**
	 * 验证手机号
	 * @param phoneNum
	 * @return
	 */
	public static boolean checkPhoneNum(String phoneNum) {
//		Pattern p = Pattern.compile("^(1[^46\\D][0-9])\\d{8}$");  
		Pattern p = Pattern.compile("^1\\d{10}$");  
		Matcher m = p.matcher(phoneNum);  
		return m.matches();
	}
	
	/**
	 * 验证登录密码
	 * @param phoneNum
	 * @return
	 */
	public static boolean checkLoginPsd(String loginPsd) {
		Pattern p = Pattern.compile("\\S{6,20}");  //6位，支持空格，若不支持空白符：^[\w\W]{6,}$
		Matcher m = p.matcher(loginPsd);  
		return m.matches();
	}

	public static boolean checkPayPsd(String payPsd) {
		Pattern p = Pattern.compile("\\S{6,20}");  //6位，支持空格，若不支持空白符：^[\w\W]{6,}$
		Matcher m = p.matcher(payPsd);  
		return m.matches();
	}
	
	public static boolean checkAppVersion(String appversion) {
		if(appversion == null){
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*"); 
		return pattern.matcher(appversion).matches();  
	}
}
