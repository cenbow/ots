package com.mk.framework.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.SysConfig;

/**
 *
 * @author zhouhuangling
 * @date   2015年1月23日上午11:00:27
 */
public class PayUtil {
	
	/**
	 * 获取小枕头转储值的比例
	 * @return
	 */
	public static BigDecimal getXiaozhentouRate() {
/*		return new BigDecimal(SysConfig.getInstance()
				.getSysValueByKey(Constant.xiaozhentouRateKey).trim());*/
		return new BigDecimal(1);
	}

	/**
	 * 获取积分转储值的比例
	 * @return
	 */
	public static BigDecimal getJifenRate() {
		return new BigDecimal(SysConfig.getInstance()
				.getSysValueByKey(Constant.jifenRateKey).trim());
	}
	
	/**
	 * BigDecimal null转换成zero
	 * @param num
	 * @return
	 */
	public static BigDecimal bigDecimalNullToZero(BigDecimal num){
		return num==null?BigDecimal.ZERO:num;
	}
	
	/**
	 * String[]转换List
	 * @param strs
	 * @return
	 */
	public static List<Long> stringsToList(String[] strs){
		List<Long> list = new ArrayList<>();
		for (String str : strs) {
			if(isNotEmpty(str)){
				try{
					list.add(Long.parseLong(str));
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	/**
	 * 校验字符串非空，非NULL
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		return StringUtils.isNotBlank(str) &&!"NULL".equals(str.toUpperCase()) ;
	}
	
	/**
	 * 校验字符串为空，为NULL
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		return StringUtils.isBlank(str)||"NULL".equals(str.toUpperCase());
	}
	/**
	 * 获取pms失败日志时间点
	 * @return
	 */
	public static Long getPmsErrorDateTime(){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-Constant.pmsLogDays);
		return cal.getTimeInMillis();
	}
}
