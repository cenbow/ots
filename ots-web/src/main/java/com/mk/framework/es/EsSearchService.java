package com.mk.framework.es;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.Nullable;

import com.mk.framework.es.EsSearchOption.SearchLogic;

/*new Object[] { "TESTNAME1,TESTNAME2"}会识别为一个搜索条件*/
/*new Object[] { "TESTNAME1","TESTNAME2"}会识别为两个搜索条件*/
public interface EsSearchService {

    // 批量更新数据，先删除，再插入，只需要传递新数据的差异键值
    boolean autoBulkUpdateData(String indexName, HashMap<String, Object[]> oldContentMap, HashMap<String, Object[]> newContentMap);

    // 批量删除数据，危险
    boolean bulkDeleteData(String indexName, HashMap<String, Object[]> contentMap);

    // 批量插入数据
    boolean bulkInsertData(String indexName, HashMap<String, Object[]> contentMap);

    // 批量更新数据，先删除，再插入，需要传递新数据的完整数据
    boolean bulkUpdateData(String indexName
            , HashMap<String, Object[]> oldContentMap, HashMap<String, Object[]> newContentMap);

    /**
     * 搜索
     * indexNames    索引名称
     * mustSearchContentMap    must内容HashMap
     * shouldSearchContentMap    should内容HashMap
     * from    从第几条记录开始（必须大于等于0）
     * offset    一共显示多少条记录（必须大于0）
     * sortField    排序字段名称
     * sortType    排序方式（asc，desc）
     */
    List<Map<String, Object>> complexSearch(String[] indexNames
            ,@Nullable HashMap<String, Object[]> mustSearchContentMap
            ,@Nullable HashMap<String, Object[]> shouldSearchContentMap
            , int from, int offset, @Nullable String sortField, @Nullable String sortType);

    /**
     * 获得搜索结果总数
     * indexNames    索引名称
     * mustSearchContentMap    must内容HashMap
     * shouldSearchContentMap    should内容HashMap
     */
    long getComplexCount(String[] indexNames
            ,@Nullable HashMap<String, Object[]> mustSearchContentMap
            ,@Nullable HashMap<String, Object[]> shouldSearchContentMap);

    /**
     * 获得搜索结果总数，支持json版本
     */
    long getCount(String[] indexNames, byte[] queryString);

    /**
     * 获得搜索结果总数
     * indexNames    索引名称
     * searchContentMap    搜索内容HashMap
     * filterContentMap    过滤内容HashMap
     */
    long getCount(String[] indexNames
            , HashMap<String, Object[]> searchContentMap
            , @Nullable HashMap<String, Object[]> filterContentMap);

    /**
     * 获得搜索结果总数
     * indexNames    索引名称
     * searchContentMap    搜索内容HashMap
     * searchLogic    搜索条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
     * filterContentMap    过滤内容HashMap
     * filterLogic    过滤条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
     */
    long getCount(String[] indexNames
            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
            , @Nullable HashMap<String, Object[]> filterContentMap, @Nullable SearchLogic filterLogic);

    //获得推荐列表
    List<String> getSuggest(String[] indexNames, String fieldName, String value, int count);

    /**
     * 分组统计
     * indexName    索引名称
     * mustSearchContentMap    must内容HashMap
     * shouldSearchContentMap    should内容HashMap
     * groupFields    分组字段
     */
    Map<String, String> group(String indexName
            ,@Nullable HashMap<String, Object[]> mustSearchContentMap
            ,@Nullable HashMap<String, Object[]> shouldSearchContentMap
            , String[] groupFields);

    /**
     * 搜索，支持json版本
     */
    List<Map<String, Object>> simpleSearch(String[] indexNames, byte[] queryString
            , int from, int offset, @Nullable String sortField, @Nullable String sortType);

    /**
     * 搜索
     * indexNames    索引名称
     * searchContentMap    搜索内容HashMap
     * filterContentMap    过滤内容HashMap
     * from    从第几条记录开始（必须大于等于0）
     * offset    一共显示多少条记录（必须大于0）
     * sortField    排序字段名称
     * sortType    排序方式（asc，desc）
     */
    List<Map<String, Object>> simpleSearch(String[] indexNames
            , HashMap<String, Object[]> searchContentMap
            , @Nullable HashMap<String, Object[]> filterContentMap
            , int from, int offset, @Nullable String sortField, @Nullable String sortType);

    /**
     * 去掉排序参数的简化版本
     */
    List<Map<String, Object>> simpleSearch(String[] indexNames
            , HashMap<String, Object[]> searchContentMap
            , @Nullable HashMap<String, Object[]> filterContentMap
            , int from, int offset);

    /**
     * 搜索
     * indexNames    索引名称
     * searchContentMap    搜索内容HashMap
     * searchLogic    搜索条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
     * filterContentMap    过滤内容HashMap
     * filterLogic    过滤条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
     * from    从第几条记录开始（必须大于等于0）
     * offset    一共显示多少条记录（必须大于0）
     * sortField    排序字段名称
     * sortType    排序方式（asc，desc）
     * */
    List<Map<String, Object>> simpleSearch(String[] indexNames
            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
            , @Nullable HashMap<String, Object[]> filterContentMap, @Nullable SearchLogic filterLogic
            , int from, int offset, @Nullable String sortField, @Nullable String sortType);
}
