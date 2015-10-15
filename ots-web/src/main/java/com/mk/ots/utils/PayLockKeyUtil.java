package com.mk.ots.utils;

/**
 * 用来生成支付流程分布锁的key
 * @author lindi
 *
 */
public class PayLockKeyUtil {
	
	private static final String PREFIX = "PayLockKeyPrefix";
	private static final String CALL_BACK_PREFIX = "PayCallBackLockKeyPrefix";
	private static final String  PROPREFIX = "PromoLockKeyPrefix";

	
	public static String genLockKey4Pay(String sourceKey) {
		
		return PREFIX + sourceKey;
	}
	
	public static String genLockKeyPromo(String sourceKey) {

		return PROPREFIX + sourceKey;
	}
	
	public static String genLockKey4PayCallBack(String sourceKey) {
		
		return CALL_BACK_PREFIX + sourceKey;
	}

}
