package com.mk.framework.es;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;

/**
 * 
 * @author chuaiqing.
 *
 */
public final class ElasticSearchUtil {

    /**
     * 索引库是否存在
     * @param client
     * 参数：客户端
     * @param index
     * 参数：索引库名
     * @return 存在则返回true，不存在则返回false
     */
    public static Boolean indexExist(Client client, String index) {
        IndicesExistsRequest request = new IndicesExistsRequestBuilder(client.admin().indices(), index).request();
        IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
        return response.isExists();
    }
}
