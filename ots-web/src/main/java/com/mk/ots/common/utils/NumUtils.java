package com.mk.ots.common.utils;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.mk.framework.exception.MyErrorEnum;

/**
 * 数字工具类
 * @author LYN
 *
 */

public class NumUtils {
	
	/*
	 * 返回bigDecimal
	 */
	public static BigDecimal getBigDec(String num,String str) {
		BigDecimal temp=null;
		if(StringUtils.isNotBlank(num))
		{
			temp=new BigDecimal(num);
			if(temp.compareTo(BigDecimal.ZERO)<0){
				throw MyErrorEnum.errorParm.getMyException(str);
			}
			return temp;
		}else{
			return null;
		}
	}
}
