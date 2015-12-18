package com.mk.ots.inner.service;

import java.util.Map;

/**
 * OTS Administrator服务接口类.
 * 
 * @author chuaiqing.
 *
 */
public interface IOtsAdminService {
    /**
     * 导入城市位置区域信息：3地铁，5地标。
     * 目前该数据是从网络抓取的：310000上海市、500000重庆市、420100武汉市。
     * @param citycode
     * 参数：城市编码
     * @param typeid
     * 参数：数据分类，3地铁；5地标
     * @return
     * @throws Exception
     */
    Map<String, Object> readonlyImportPoiDatas(String citycode, Integer typeid) throws Exception;
    
    /**
     * 删除城市位置区域信息：3地铁，5地标。
     * @param citycode
     * 参数：城市编码
     * @param typeid
     * 参数：数据分类，3地铁；5地标
     * @return
     * @throws Exception
     */
    Map<String, Object> readonlyDeletePoiDatas(String citycode, Integer typeid, String indexName, String indexType) throws Exception;

}
