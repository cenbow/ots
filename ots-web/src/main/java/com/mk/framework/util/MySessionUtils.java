package com.mk.framework.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.mk.ots.common.utils.Constant;


public class MySessionUtils {
	//判断本次线程是否需要通过线程对象获取mysession对象
	private static ThreadLocal<Boolean> flag=new ThreadLocal<Boolean>();
	//取消mysession与session关联的对象
	private static ThreadLocal<MySession> datasession=new ThreadLocal<MySession>();
	private static ThreadLocal<Exception> pmsexception=new ThreadLocal<Exception>();
	
	
	public static void clearPmsexception(){
		pmsexception.remove();
	}
	
	public static void setPmsexception(Exception e){
		pmsexception.set(e);
	}
	
	public static Exception getPmsException(){
		return pmsexception.get();
	}
	
	
	public static void putFlag(Boolean flag1){
		flag.set(flag1);
	}
	
	public static void removeFlag(){
		flag.remove();
		datasession.remove();
	}
	
	public static SimpleDateFormat createSimpleDateFormat(){
		MySession mysesseion=getMySession();
		if(mysesseion!=null){
			return mysesseion.createSimpleDateFormat();
		}else{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	}
	
	
	public static MySession getMySession(){
		HttpServletRequest request = null;//ServletActionContext.getRequest();
		Boolean needReadByLocal=flag.get();
		if(needReadByLocal==null || needReadByLocal==false){
			MySession mysession=(MySession) request.getSession().getAttribute(Constant.MySessionName);
			if(mysession==null){
				mysession=new MySession(request.getSession().getId());
				request.getSession().setAttribute(Constant.MySessionName, mysession);
			}
			return mysession;
		}else{
			MySession session= datasession.get();
			if(session==null){
				session=new MySession(UUID.randomUUID().toString().replaceAll("-", ""));
				datasession.set(session);
			}
			return session;
		}
	}
	
	public static String getInternationalMessage(String key, Object...args){
		MySession mysession=getMySession();
		if(mysession!=null){
			return getInternationalMessageByLanguage(mysession.getLanguage(), mysession.getCountry(), key, args);
		}else{
			return getInternationalMessageByLanguage("zh", "CN", key, args);
		}
	}
	
	public static String getInternationalMessageByLanguage(String language, String country, String key, Object...args) {
		Locale locale = null;
		if (language != null && !language.equals("")) {
			if (country != null && !country.equals(""))
				locale = new Locale(language, country);
			else
				locale = new Locale(language);
		} else
			locale = Locale.getDefault();
		ResourceBundle bundle = ResourceBundle.getBundle("cn/com/winhoo/hm/language/message", locale);
		String value = bundle.getString(key);
		if(value==null){
			return "";
		}
		if (args != null) 
			value = MessageFormat.format(value, args);
		return value;
	}

}
