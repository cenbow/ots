package com.mk.ots.manager;

public class RedisCacheName {
	// jfianl的缓存
	public static final String IMIKE_JFINAL_DB = "IMIKE:JFINAL";
	// job任务的缓存
	public static final String IMIKE_JOB = "IMIKE:JOB";
	//同步房间锁
	public static final String IMIKE_OTS_SYNCHOTELINFO_KEY = "IMIKE:OTS:LOCKDATA:SYNCHOTELINFO:";
	//同步房间锁PMS2.0
	public static final String IMIKE_OTS_SYNCHOTELINFO_PMS20_KEY = "IMIKE:OTS:LOCKDATA:SYNCHOTELINFOPMS20:";
	//同步订单锁
	public static final String IMIKE_OTS_SYNORDER_KEY = "IMIKE:OTS:LOCKDATA:SYNORDER:";
	//取消订单锁
	public static final String IMIKE_OTS_CANCELORDER_KEY = "IMIKE:OTS:LOCKDATA:CANCELORDER:";
	//房费清单
	public static final String IMIKE_OTS_ROOMCHARGE_KEY = "IMIKE:OTS:LOCKDATA:ROOMCHARGE:";
	//同步客单锁
	public static final String IMIKE_OTS_SAVECUSTOMERNO_KEY = "IMIKE:OTS:LOCKDATA:SAVECUSTOMERNO:";
	//领取优惠券锁
	public static final String IMIKE_OTS_GETTICKET_KEY = "IMIKE:OTS:LOCKDATA:GETTICKET:";
	/**
	 * 抽奖活动领取优惠券
	 */
	public static final String IMIKE_OTS_TRYLUCK_KEY = "IMIKE:OTS:LOCKDATA:TRYLUCK:";
	/**
	 * 兑换优惠券
	 */
	public static final String IMIKE_OTS_GETBYCODE_KEY = "IMIKE:OTS:LOCKDATA:GETTICKET:";
	/**
	 * worker发放优惠券
	 */
	public static final String IMIKE_OTS_SEND_COUPONS_JOB = "IMIKE:OTS:LOCKDATA:SENDCOUPONSJOB:";

	public static final String DYNAMIC_PRICE_CRITERION_PRICE_CODE = "DYNAMIC:PRICE:CRITERION:PRICE_CODE:";
	
	
	
}
