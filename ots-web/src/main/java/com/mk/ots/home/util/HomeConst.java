package com.mk.ots.home.util;
public class HomeConst {
	
//	public static final String HOTEL_MAX_PRICES_MAP = "HOTEL_MAX_PRICES_MAP";
	// 每个的60天的HOTEL_ORDER_DAILY数据缓存
	//public static final String HOTEL_ORDER_DAILY_LIST_60 = "HOTEL_ORDER_DAILY_LIST_60";
	// 每个人的checkerBill数据，key：CHERKER_BILL_hotelId_checkerId，value是map
//	public static final String CHERKER_BILL = "CHERKER_BILL";
//	public static final String CHERKER_BILL2 = "CHERKER_BILL_2";
	
	public static final String CHECKER_BILL_JOB_HIS = "CHERKER_BILL";
	public static final String BILL_DETAIL_JOB_HIS = "BILL_DETAIL";
	public static final String BILL_ORDERS_JOB_HIS = "BILLORDERS_DETAIL";
	public static final String BILL_CONFIRM_JOB_HIS = "BILLCONFIRM_DETAIL";
	
	/**酒店统计数据缓存*/
	public static final String HOTEL_STATISTICS_CACHE = "HotelStatistics";
	/**酒店统计数据账单缓存(单日)前缀*/
	public static final String CACHE_CHECKER_BILL_DAY_PREFIX = "cache_checker_bill_day_key_";
	/**酒店统计数据账单缓存(两周)前缀*/
	public static final String CACHE_CHECKER_BILL_2WEEKS_PREFIX = "cache_checker_bill_2weeks_key_";
	/**酒店统计数据最大房价缓存前缀*/
	public static final String CACHE_HOTEL_MAX_PRICE_PREFIX = "cache_hotel_max_price_key_";
	/**酒店统计数据60天日订单列表*/
	public static final String CACHE_HOTEL_ORDER_DAILY_LIST_60 = "cache_hotel_order_daily_list_60_key_";
	
}