package com.mk.framework.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.mk.framework.es.EsSearchOption.DataFilter;
import com.mk.framework.es.EsSearchOption.SearchLogic;

public class EsSearchServiceImp implements EsSearchService, InitializingBean, DisposableBean {
    private List<String> clusterList = null;
    private Logger logger = org.slf4j.LoggerFactory.getLogger(EsSearchServiceImp.class);
    private Client searchClient = null;
    private HashMap<String, String> searchClientConfigureMap = null;
    private String highlightCSS = "";

    public void setHighlightCSS(String highlightCSS) {
        this.highlightCSS = highlightCSS;
    }

    public void setSearchClientConfigureMap(HashMap<String, String> searchClientConfigureMap) {
        this.searchClientConfigureMap = searchClientConfigureMap;
    }

    private boolean _bulkInsertData(String indexName, XContentBuilder xContentBuilder) {
        try {
            BulkRequestBuilder bulkRequest = this.searchClient.prepareBulk();
            bulkRequest.add(this.searchClient.prepareIndex(indexName, indexName).setSource(xContentBuilder));
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (!bulkResponse.hasFailures()) {
                return true;
            }
            else {
                this.logger.error(bulkResponse.buildFailureMessage());
            }
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return false;
    }

    public void afterPropertiesSet() throws Exception {
        this.logger.info("连接搜索服务器");
        this.open();
    }

    public boolean bulkDeleteData(String indexName, HashMap<String, Object[]> contentMap) {
        try {
            QueryBuilder queryBuilder = null;
            queryBuilder = this.createQueryBuilder(contentMap, SearchLogic.must);
            this.logger.warn("[" + indexName + "]" + queryBuilder.toString());
            this.searchClient.prepareDeleteByQuery(indexName).setQuery(queryBuilder).execute().actionGet();
            return true;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return false;
    }

    public boolean bulkInsertData(String indexName, HashMap<String, Object[]> insertContentMap) {
        XContentBuilder xContentBuilder = null;
        try {
            xContentBuilder = XContentFactory.jsonBuilder().startObject();
        }
        catch (IOException e) {
            this.logger.error(e.getMessage());
            return false;
        }
        Iterator<Entry<String, Object[]>> iterator = insertContentMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Object[]> entry = iterator.next();
            String field = entry.getKey();
            Object[] values = entry.getValue();
            String formatValue = this.formatInsertData(values);
            try {
                xContentBuilder = xContentBuilder.field(field, formatValue);
            }
            catch (IOException e) {
                this.logger.error(e.getMessage());
                return false;
            }
        }
        try {
            xContentBuilder = xContentBuilder.endObject();
        }
        catch (IOException e) {
            this.logger.error(e.getMessage());
            return false;
        }
        try {
            this.logger.debug("[" + indexName + "]" + xContentBuilder.string());
        }
        catch (IOException e) {
            this.logger.error(e.getMessage());
        }
        return this._bulkInsertData(indexName, xContentBuilder);
    }

    public boolean bulkUpdateData(String indexName, HashMap<String, Object[]> oldContentMap, HashMap<String, Object[]> newContentMap) {
        if (this.bulkDeleteData(indexName, oldContentMap)) {
            return this.bulkInsertData(indexName, newContentMap);
        }
        this.logger.warn("删除数据失败");
        return false;
    }

    public boolean autoBulkUpdateData(String indexName, HashMap<String, Object[]> oldContentMap, HashMap<String, Object[]> newContentMap) {
        try {
            List<Map<String, Object>> searchResult = this.simpleSearch(new String[] { indexName }, oldContentMap, null, 0, 1, null, null);
            if (searchResult == null || searchResult.size() == 0) {
                this.logger.warn("未找到需要更新的数据");
                return false;
            }
            if (!this.bulkDeleteData(indexName, oldContentMap)) {
                this.logger.warn("删除数据失败");
                return false;
            }
            HashMap<String, Object[]> insertContentMap = new HashMap<String, Object[]>();
            for (Map<String, Object> contentMap : searchResult) {
                Iterator<Entry<String, Object>> oldContentIterator = contentMap.entrySet().iterator();
                while (oldContentIterator.hasNext()) {
                    Entry<String, Object> entry = oldContentIterator.next();
                    insertContentMap.put(entry.getKey(), new Object[] { entry.getValue() });
                }
            }
            Iterator<Entry<String, Object[]>> newContentIterator = newContentMap.entrySet().iterator();
            while (newContentIterator.hasNext()) {
                Entry<String, Object[]> entry = newContentIterator.next();
                insertContentMap.put(entry.getKey(), entry.getValue());
            }
            return this.bulkInsertData(indexName, insertContentMap);
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * 简单的值校验
     */
    private boolean checkValue(Object[] values) {
        if (values == null) {
            return false;
        }
        else if (values.length == 0) {
            return false;
        }
        else if (values[0] == null) {
            return false;
        }
        else if (values[0].toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void close() {
        if (this.searchClient == null) {
            return;
        }
        this.searchClient.close();
        this.searchClient = null;
    }

    private RangeQueryBuilder createRangeQueryBuilder(String field, Object[] values) {
        if (values.length == 1 || values[1] == null || values[1].toString().trim().isEmpty()) {
            this.logger.warn("[区间搜索]必须传递两个值，但是只传递了一个值，所以返回null");
            return null;
        }
        boolean timeType = false;
        if (EsSearchOption.isDate(values[0])) {
            if (EsSearchOption.isDate(values[1])) {
                timeType = true;
            }
        }
        String begin = "", end = "";
        if (timeType) {
            /*
             * 如果时间类型的区间搜索出现问题，有可能是数据类型导致的：
             *     （1）在监控页面（elasticsearch-head）中进行range搜索，看看什么结果，如果也搜索不出来，则：
             *     （2）请确定mapping中是date类型，格式化格式是yyyy-MM-dd HH:mm:ss
             *    （3）请确定索引里的值是类似2012-01-01 00:00:00的格式
             *    （4）如果是从数据库导出的数据，请确定数据库字段是char或者varchar类型，而不是date类型（此类型可能会有问题）
             * */
            begin = EsSearchOption.formatDate(values[0]);
            end = EsSearchOption.formatDate(values[1]);
        }
        else {
            begin = values[0].toString();
            end = values[1].toString();
        }
        return QueryBuilders.rangeQuery(field).from(begin).to(end);
    }

    /**
     * 创建过滤条件
     */
    private QueryBuilder createFilterBuilder(SearchLogic searchLogic, QueryBuilder queryBuilder, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap) throws Exception
    {
        try {
            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
            AndFilterBuilder andFilterBuilder = null;
            while (iterator.hasNext()) {
                Entry<String, Object[]> entry = iterator.next();
                Object[] values = entry.getValue();
                /*排除非法的搜索值*/
                if (!this.checkValue(values)) {
                    continue;
                }
                EsSearchOption mySearchOption = this.getSearchOption(values);
                if (mySearchOption.getDataFilter() == DataFilter.exists) {
                    /*被搜索的条件必须有值*/
                    ExistsFilterBuilder existsFilterBuilder = FilterBuilders.existsFilter(entry.getKey());
                    if (andFilterBuilder == null) {
                        andFilterBuilder = FilterBuilders.andFilter(existsFilterBuilder);
                    }
                    else {
                        andFilterBuilder = andFilterBuilder.add(existsFilterBuilder);
                    }
                }
            }
            if (filterContentMap == null || filterContentMap.isEmpty()) {
                /*如果没有其它过滤条件，返回*/
                return QueryBuilders.filteredQuery(queryBuilder, andFilterBuilder);
            }
            /*构造过滤条件*/
            QueryFilterBuilder queryFilterBuilder = FilterBuilders.queryFilter(this.createQueryBuilder(filterContentMap, searchLogic));
            /*构造not过滤条件，表示搜索结果不包含这些内容，而不是不过滤*/
            NotFilterBuilder notFilterBuilder = FilterBuilders.notFilter(queryFilterBuilder);
            return QueryBuilders.filteredQuery(queryBuilder, FilterBuilders.andFilter(andFilterBuilder, notFilterBuilder));
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    private QueryBuilder createSingleFieldQueryBuilder(String field, Object[] values, EsSearchOption mySearchOption) {
        try {
            if (mySearchOption.getSearchType() == EsSearchOption.SearchType.range) {
                /*区间搜索*/
                return this.createRangeQueryBuilder(field, values);
            }
            // String[] fieldArray = field.split(",");/*暂时不处理多字段[field1,field2,......]搜索情况*/
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (Object valueItem : values) {
                if (valueItem instanceof EsSearchOption) {
                    continue;
                }
                QueryBuilder queryBuilder = null;
                String formatValue = valueItem.toString().trim().replace("*", "");//格式化搜索数据
                if (mySearchOption.getSearchType() == EsSearchOption.SearchType.term) {
                    queryBuilder = QueryBuilders.termQuery(field, formatValue).boost(mySearchOption.getBoost());
                }
                else if (mySearchOption.getSearchType() == EsSearchOption.SearchType.querystring) {
                    if (formatValue.length() == 1) {
                        /*如果搜索长度为1的非数字的字符串，格式化为通配符搜索，暂时这样，以后有时间改成multifield搜索，就不需要通配符了*/
                        if (!Pattern.matches("[0-9]", formatValue)) {
                            formatValue = "*"+formatValue+"*";
                        }
                    }
                    QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryString(formatValue).minimumShouldMatch(mySearchOption.getQueryStringPrecision());
                    queryBuilder = queryStringQueryBuilder.field(field).boost(mySearchOption.getBoost());
                }
                if (mySearchOption.getSearchLogic() == SearchLogic.should) {
                    boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
                }
                else {
                    boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
                }
            }
            return boolQueryBuilder;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 创建搜索条件
     */
    private QueryBuilder createQueryBuilder(HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic) {
        try {
            if (searchContentMap == null || searchContentMap.size() ==0) {
                return null;
            }
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
            /*循环每一个需要搜索的字段和值*/
            while (iterator.hasNext()) {
                Entry<String, Object[]> entry = iterator.next();
                String field = entry.getKey();
                Object[] values = entry.getValue();
                /*排除非法的搜索值*/
                if (!this.checkValue(values)) {
                    continue;
                }
                /*获得搜索类型*/
                EsSearchOption mySearchOption = this.getSearchOption(values);
                QueryBuilder queryBuilder = this.createSingleFieldQueryBuilder(field, values, mySearchOption);
                if (queryBuilder != null) {
                    if (searchLogic == SearchLogic.should) {
                        /*should关系，也就是说，在A索引里有或者在B索引里有都可以*/
                        boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
                    }
                    else {
                        /*must关系，也就是说，在A索引里有，在B索引里也必须有*/
                        boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
                    }
                }
            }
            return boolQueryBuilder;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public void destroy() throws Exception {
        this.logger.info("关闭搜索客户端");
        this.close();
    }

    private String formatInsertData(Object[] values) {
        if (!this.checkValue(values)) {
            return "";
        }
        if (EsSearchOption.isDate(values[0])) {
            this.logger.warn("[" + values[0].toString() + "] formatDate");
            return EsSearchOption.formatDate(values[0]);
        }
        String formatValue = values[0].toString();
        for (int index = 1; index < values.length; ++index) {
            formatValue += "," + values[index].toString();
        }
        return formatValue.trim();
    }

    public long getCount(String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap) {
        SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
        return this.getCount(indexNames, searchContentMap, searchLogic, filterContentMap, searchLogic);
    }

    private SearchResponse searchCountRequest(String[] indexNames, Object queryBuilder) {
        try {
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.COUNT);
            if (queryBuilder instanceof QueryBuilder) {
                searchRequestBuilder = searchRequestBuilder.setQuery((QueryBuilder)queryBuilder);
                this.logger.debug(searchRequestBuilder.toString());
            }
            if (queryBuilder instanceof byte[]) {
                String query = new String((byte[])queryBuilder);
                searchRequestBuilder = searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));
                this.logger.debug(query);
            }
            return searchRequestBuilder.execute().actionGet();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public long getCount(String[] indexNames, byte[] queryString) {
        try {
            SearchResponse searchResponse = this.searchCountRequest(indexNames, queryString);
            return searchResponse.getHits().totalHits();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return 0;
    }

    /*获得搜索结果*/
    private List<Map<String, Object>> getSearchResult(SearchResponse searchResponse) {
        try {
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (SearchHit searchHit : searchResponse.getHits()) {
                Iterator<Entry<String, Object>> iterator = searchHit.getSource().entrySet().iterator();
                HashMap<String, Object> resultMap = new HashMap<String, Object>();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    resultMap.put(entry.getKey(), entry.getValue());
                }
                Map<String, HighlightField> highlightMap = searchHit.highlightFields();
                Iterator<Entry<String, HighlightField>> highlightIterator = highlightMap.entrySet().iterator();
                while (highlightIterator.hasNext()) {
                    Entry<String, HighlightField> entry = highlightIterator.next();
                    Object[] contents = entry.getValue().fragments();
                    if (contents.length == 1) {
                        resultMap.put(entry.getKey(), contents[0].toString());
                        System.out.println(contents[0].toString());
                    }
                    else {
                        this.logger.warn("搜索结果中的高亮结果出现多数据contents.length = " + contents.length);
                    }
                }
                resultList.add(resultMap);
            }
            return resultList;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    /*获得搜索选项*/
    private EsSearchOption getSearchOption(Object[] values) {
        try {
            for (Object item : values) {
                if (item instanceof EsSearchOption) {
                    return (EsSearchOption) item;
                }
            }
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return new EsSearchOption();
    }

    /**
     * 获得搜索建议
     * 服务器端安装elasticsearch-plugin-suggest
     * 客户端加入elasticsearch-plugin-suggest的jar包
     * https://github.com/spinscale/elasticsearch-suggest-plugin
     */
    public List<String> getSuggest(String[] indexNames, String fieldName, String value, int count) {
        try {
            SuggestRequestBuilder suggestRequestBuilder = new SuggestRequestBuilder(this.searchClient);
            TermSuggestionBuilder suggestionBuilder = new TermSuggestionBuilder(fieldName);
            suggestionBuilder.field(fieldName);
            suggestionBuilder.text(value);
            suggestionBuilder.size(count);
            ////suggestRequestBuilder = suggestRequestBuilder.setIndices(indexNames).field(fieldName).term(value).size(count);//.similarity(0.5f);
            suggestRequestBuilder = suggestRequestBuilder.setIndices(indexNames).addSuggestion(suggestionBuilder);
            SuggestResponse suggestResponse = suggestRequestBuilder.execute().actionGet();
            suggestResponse.getSuggest().iterator();
            ////return suggestResponse.suggestions();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 创建搜索客户端
     * tcp连接搜索服务器
     * 创建索引
     * 创建mapping
     */
    private void open() {
        try {
            /*如果10秒没有连接上搜索服务器，则超时*/
            Settings settings = ImmutableSettings.settingsBuilder()
                    .put(this.searchClientConfigureMap)
//                    .put("client.transport.ping_timeout", "10s")
//                    .put("client.transport.sniff", "true")
//                    .put("client.transport.ignore_cluster_name", "true")
                    .build();
            /*创建搜索客户端*/
            this.searchClient = new TransportClient(settings);
            if (CollectionUtils.isEmpty(this.clusterList)) {
                String cluster = "ots-elasticsearch-test";  //PropertiesConfigUtils.getProperty("search.clusterList");
                if (cluster != null) {
                    this.clusterList = Arrays.asList(cluster.split(","));
                }
            }
            for (String item : this.clusterList) {
                String address = item.split(":")[0];
                int port = Integer.parseInt(item.split(":")[1]);
                /*通过tcp连接搜索服务器，如果连接不上，有一种可能是服务器端与客户端的jar包版本不匹配*/
                this.searchClient = ((TransportClient) this.searchClient).addTransportAddress(new InetSocketTransportAddress(address, port));
            }
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }

    public void setClusterList(List<String> clusterList) {
        this.clusterList = clusterList;
    }

    public List<Map<String, Object>> simpleSearch(String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap, int from, int offset) {
        return this.simpleSearch(indexNames, searchContentMap, filterContentMap, from, offset, null, null);
    }

    public List<Map<String, Object>> simpleSearch(String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap, int from, int offset, String sortField, String sortType)
    {
        SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
        return this.simpleSearch(indexNames, searchContentMap, searchLogic, filterContentMap, searchLogic, from, offset, sortField, sortType);
    }

    public long getComplexCount(String[] indexNames
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap) {
        /*创建must搜索条件*/
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
        /*创建should搜索条件*/
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return 0;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
        }
        try {
            SearchResponse searchResponse = this.searchCountRequest(indexNames, boolQueryBuilder);
            return searchResponse.getHits().totalHits();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return 0;
    }

    public List<Map<String, Object>> complexSearch(String[] indexNames
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap
            , int from, int offset, @Nullable String sortField, @Nullable String sortType) {
        if (offset <= 0) {
            return null;
        }
        /*创建must搜索条件*/
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
        /*创建should搜索条件*/
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
        }
        try {
            SearchRequestBuilder searchRequestBuilder = null;
            searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setQuery(boolQueryBuilder).setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }
            else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, mustSearchContentMap);
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, shouldSearchContentMap);
            this.logger.debug(searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return this.getSearchResult(searchResponse);
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public List<Map<String, Object>> simpleSearch(String[] indexNames, byte[] queryString, int from, int offset, String sortField, String sortType) {
        if (offset <= 0) {
            return null;
        }
        try {
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }
            else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }

            String query = new String(queryString);
            searchRequestBuilder = searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));
            this.logger.debug(query);

            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return this.getSearchResult(searchResponse);
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    private Map<String, String> _group(String indexName, QueryBuilder queryBuilder, String[] groupFields) {
        try {
            TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("group").fields(groupFields).size(9999);
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexName).setSearchType(SearchType.DEFAULT)
                    .addFacet(termsFacetBuilder).setQuery(queryBuilder).setFrom(0).setSize(1).setExplain(true);
            this.logger.debug(searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            TermsFacet termsFacet = searchResponse.getFacets().facet("group");
            HashMap<String, String> result = new HashMap<String, String>();
            for (org.elasticsearch.search.facet.terms.TermsFacet.Entry entry : termsFacet.getEntries()) {
                result.put(entry.getTerm().toString(), entry.getCount() + "");
            }
            return result;
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public Map<String, String> group(String indexName
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap
            , String[] groupFields) {
        /*创建must搜索条件*/
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
        /*创建should搜索条件*/
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
        }
        try {
            return this._group(indexName, boolQueryBuilder, groupFields);
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    public List<Map<String, Object>> simpleSearch(String[] indexNames
            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
            , HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic
            , int from, int offset, String sortField, String sortType)
    {
        if (offset <= 0) {
            return null;
        }
        try {
            QueryBuilder queryBuilder = null;
            queryBuilder = this.createQueryBuilder(searchContentMap, searchLogic);
            queryBuilder = this.createFilterBuilder(filterLogic, queryBuilder, searchContentMap, filterContentMap);
            SearchRequestBuilder searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setQuery(queryBuilder).setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }
            else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, searchContentMap);
            this.logger.debug(searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return this.getSearchResult(searchResponse);
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    private SearchRequestBuilder createHighlight(SearchRequestBuilder searchRequestBuilder, HashMap<String, Object[]> searchContentMap) {
        Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
        /*循环每一个需要搜索的字段和值*/
        while (iterator.hasNext()) {
            Entry<String, Object[]> entry = iterator.next();
            String field = entry.getKey();
            Object[] values = entry.getValue();
            /*排除非法的搜索值*/
            if (!this.checkValue(values)) {
                continue;
            }
            /*获得搜索类型*/
            EsSearchOption mySearchOption = this.getSearchOption(values);
            if (mySearchOption.isHighlight()) {
                /*
                 * http://www.elasticsearch.org/guide/reference/api/search/highlighting.html
                 *
                 * fragment_size设置成1000，默认值会造成返回的数据被截断
                 * */
                searchRequestBuilder = searchRequestBuilder.addHighlightedField(field, 1000)
                        .setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                        .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">");
            }
        }
        return searchRequestBuilder;
    }

    public long getCount(String[] indexNames
            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
            , @Nullable HashMap<String, Object[]> filterContentMap, @Nullable SearchLogic filterLogic)
    {
        QueryBuilder queryBuilder = null;
        try {
            queryBuilder = this.createQueryBuilder(searchContentMap, searchLogic);
            queryBuilder = this.createFilterBuilder(searchLogic, queryBuilder, searchContentMap, filterContentMap);
            SearchResponse searchResponse = this.searchCountRequest(indexNames, queryBuilder);
            return searchResponse.getHits().totalHits();
        }
        catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return 0;
    }
}
