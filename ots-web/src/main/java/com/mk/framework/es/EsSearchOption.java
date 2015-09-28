package com.mk.framework.es;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * ES SearchOption
 * 
 * @author chuaiqing.
 *
 */
public class EsSearchOption implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 7670165608293109545L;
    private SearchLogic searchLogic = SearchLogic.must;
    private SearchType searchType = SearchType.querystring;
    private DataFilter dataFilter = DataFilter.exists;
    /* querystring精度，取值[1-100]的整数 */
    private String queryStringPrecision = "100";
    /* 排名权重 */
    private float boost = 1.0f;
    private boolean highlight = false;
    
    /**
     * 
     * @author chuaiqing.
     *
     */
    public enum SearchType {
        /* 按照quert_string搜索，搜索非词组时候使用 */
        querystring
        /* 按照区间搜索 */
        , range
        /* 按照词组搜索，搜索一个词时候使用 */
        , term
    }
    
    /**
     * 
     * @author chuaiqing.
     *
     */
    public enum SearchLogic {
        /* 逻辑must关系 */
        must
        /* 逻辑should关系 */
        , should
    }
    
    /**
     * 
     * @author chuaiqing.
     *
     */
    public enum DataFilter {
        /* 只显示有值的 */
        exists
        /* 显示没有值的 */
        , notExists
        /* 显示全部 */
        , all
    }
    
    /**
     * 
     * @param searchType
     * @param searchLogic
     * @param queryStringPrecision
     * @param dataFilter
     * @param boost
     * @param highlight
     */
    public EsSearchOption(SearchType searchType, SearchLogic searchLogic,
            String queryStringPrecision, DataFilter dataFilter, float boost,
            int highlight) {
        this.setSearchLogic(searchLogic);
        this.setSearchType(searchType);
        this.setQueryStringPrecision(queryStringPrecision);
        this.setDataFilter(dataFilter);
        this.setBoost(boost);
        this.setHighlight(highlight > 0 ? true : false);
    }
    
    /**
     * 
     */
    public EsSearchOption() {
    }
    
    /**
     * 
     * @return
     */
    public DataFilter getDataFilter() {
        return this.dataFilter;
    }
    
    /**
     * 
     * @param dataFilter
     */
    public void setDataFilter(DataFilter dataFilter) {
        this.dataFilter = dataFilter;
    }
    
    /**
     * 
     * @return
     */
    public boolean isHighlight() {
        return this.highlight;
    }
    
    /**
     * 
     * @param highlight
     */
    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
    
    /**
     * 
     * @return
     */
    public float getBoost() {
        return this.boost;
    }
    
    /**
     * 
     * @param boost
     */
    public void setBoost(float boost) {
        this.boost = boost;
    }
    
    /**
     * 
     * @return
     */
    public SearchLogic getSearchLogic() {
        return this.searchLogic;
    }
    
    /**
     * 
     * @param searchLogic
     */
    public void setSearchLogic(SearchLogic searchLogic) {
        this.searchLogic = searchLogic;
    }
    
    /**
     * 
     * @return
     */
    public SearchType getSearchType() {
        return this.searchType;
    }
    
    /**
     * 
     * @param searchType
     */
    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }
    
    /**
     * 
     * @return
     */
    public String getQueryStringPrecision() {
        return this.queryStringPrecision;
    }
    
    /**
     * 
     * @param queryStringPrecision
     */
    public void setQueryStringPrecision(String queryStringPrecision) {
        this.queryStringPrecision = queryStringPrecision;
    }
    
    /**
     * 
     * @return
     */
    public static long getSerialversionuid() {
        return EsSearchOption.serialVersionUID;
    }
    
    /**
     * 
     * @param object
     * @return
     */
    public static String formatDate(Object object) {
        if (object instanceof java.util.Date) {
            return EsSearchOption.formatDateFromDate((java.util.Date) object);
        }
        return EsSearchOption.formatDateFromString(object.toString());
    }
    
    /**
     * 
     * @param object
     * @return
     */
    public static boolean isDate(Object object) {
        return object instanceof java.util.Date
                || Pattern.matches("[1-2][0-9][0-9][0-9]-[0-9][0-9].*",
                        object.toString());
    }
    
    /**
     * 
     * @param date
     * @return
     */
    public static String formatDateFromDate(Date date) {
        SimpleDateFormat dateFormat_hms = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String result = dateFormat_hms.format(date);
            return result;
        } catch (Exception e) {
        }
        try {
            String result = dateFormat.format(date) + "00:00:00";
            return result;
        } catch (Exception e) {
        }
        return dateFormat_hms.format(new Date());
    }
    
    /**
     * 
     * @param date
     * @return
     */
    public static String formatDateFromString(String date) {
        SimpleDateFormat dateFormat_hms = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date value = dateFormat_hms.parse(date);
            return EsSearchOption.formatDateFromDate(value);
        } catch (Exception e) {
        }
        try {
            Date value = dateFormat.parse(date);
            return EsSearchOption.formatDateFromDate(value);
        } catch (Exception e) {
        }
        return dateFormat_hms.format(new Date());
    }
}
