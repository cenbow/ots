package com.mk.ots.common.utils;

/**
 * 搜索常量定义类.
 * @author chuaiqing.
 *
 */
public final class SearchConst {
    /** 酒店搜索默认页码 */
    public final static Integer SEARCH_PAGE_DEFAULT = 1;
    
    /** 酒店搜索默认条数 */
    public final static Integer SEARCH_LIMIT_DEFAULT = 10;
    
    /** 酒店搜索默认搜索半径（单位: 米） */
    public final static Integer SEARCH_RANGE_DEFAULT = 5000;
    
    /** 酒店搜索最大搜索半径（单位: 米） */
    public final static Integer SEARCH_RANGE_MAX = 3000000;
    
    /**
     * 酒店眯客价保存天数
     */
    public static final Integer MIKEPRICE_DAYS = 33;

    /**
     * 眯客价属性前缀名
     */
    public static final String MIKE_PRICE_PROP = "$mike_price_";
    /**
     * 眯客价属性
     */
    public static final String MIKE_PRICE_DATE = MIKE_PRICE_PROP.concat("yyyyMMdd");
    
    /**
     * 酒店搜索最大天数
     */
    public static final int SEARCH_DAYS_MAX = 30;
}
