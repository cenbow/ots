package com.mk.ots.inner.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.ots.common.enums.HotelSearchEnum;
import com.mk.ots.inner.service.IOtsAdminService;
import com.mk.ots.web.ServiceOutput;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OTS Administrator服务实现类.
 * 
 * @author chuaiqing.
 *
 */
@Service
public class OtsAdminService implements IOtsAdminService {

    final Logger logger = LoggerFactory.getLogger(OtsAdminService.class);
    
    @Autowired
    private ElasticsearchProxy esProxy;
    
    /**
     * 导入城市位置区域信息：3地铁，5地标。
     * 目前该数据是从网络抓取的：310000上海市、500000重庆市、420100武汉市。
     * @param citycode
     * 参数：城市编码
     * @param typeid
     * 参数：数据分类id
     * @return
     * 返回值
     * @throws Exception
     */
    @Override
    public Map<String, Object> readonlyImportPoiDatas(String citycode, Integer typeid) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String fileName = citycode + typeid + "_POI.json";
            InputStream in = this.getClass().getResourceAsStream("/datas/" + fileName);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> inpputMap = objectMapper.readValue(in, new HashMap<String, Object>().getClass());
            if (HotelSearchEnum.SUBWAY.getId() == typeid) {
                // 地铁线路数据
                List<Map<String, Object>> datas = (List<Map<String, Object>>) inpputMap.get("data");
                boolean islist = datas instanceof List;
                if (islist) {
                    logger.info("准备输出城市{}地铁线路数据SQL脚本...", citycode);
                    result.put("datas", this.outputSubwaySQL(citycode, datas));
                    logger.info("输出城市{}地铁线路数据SQL脚本成功.", citycode);
                } else {
                    logger.error("城市{}地铁线路数据格式不正确.", citycode);
                }
            } else if (HotelSearchEnum.SAREA.getId() == typeid) {
                Map<String, Object> datas = (Map<String, Object>) inpputMap.get("data");
                // 地标数据
                boolean ismap = inpputMap.get("data") instanceof Map;
                if (ismap) {
                    logger.info("准备输出城市{}位置区域数据SQL脚本...", citycode);
                    List<String> sqls = this.outputAreainfoSQL(citycode, datas);
                    result.put("datas", sqls);
                    logger.info("输出城市{}位置区域数据SQL脚本成功.", citycode);
                } else {
                    logger.error("城市{}位置区域数据格式不正确.", citycode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 输出城市地铁线路数据SQL脚本.
     * @return
     */
    private List<String> outputSubwaySQL(String citycode, List<Map<String, Object>> datas) {
        List<String> sqls = Lists.newArrayList();
        try {
            StringBuffer bfsql = new StringBuffer();
            StringBuffer bfsql_stations = new StringBuffer();
            for (Map<String, Object> data : datas) {
                //
                bfsql.setLength(0);
                sqls.add("/******************************************************************************************/");
                // 地铁线路
                String lineid = String.valueOf(data.get("lineId"));
                String linename = String.valueOf(data.get("name"));
                bfsql.append("insert into s_subway (lineid, linename, citycode) values (")
                .append(lineid).append(",").append("'").append(linename).append("'")
                .append(",").append("'").append(citycode).append("'")
                .append(");");
                // 添加到脚本列表
                sqls.add(bfsql.toString());
                
                // 地铁站点
                List<Map<String, Object>> stations = (List<Map<String, Object>>) data.get("stations");
                for (Map<String, Object> station : stations) {
                    //
                    bfsql_stations.setLength(0);
                    //
                    String stationid = String.valueOf(station.get("id"));
                    String stationname = String.valueOf(station.get("name"));
                    double lat = Double.valueOf(String.valueOf(station.get("latitude"))).doubleValue() / 1000000;
                    double lon = Double.valueOf(String.valueOf(station.get("longitude"))).doubleValue() / 1000000;
                    String pinyin = String.valueOf(station.get("pinyin"));
                    bfsql_stations.append("insert into s_subway_stations (lineid, stationid, stationname, lat, lng, pinyin, citycode) values (")
                    .append(lineid).append(",").append(stationid).append(",").append("'").append(stationname).append("'")
                    .append(",").append(lat).append(",").append(lon).append(",").append("'").append(pinyin).append("'")
                    .append(",").append("'").append(citycode).append("'")
                    .append(");");
                    // 添加到脚本列表
                    sqls.add(bfsql_stations.toString());
                }
            }
        } catch (Exception e) {
            logger.error("outputSubwaySQL:: method error: {}", e.getLocalizedMessage());
            throw e;
        }
        return sqls;
    }
    
    /**
     * 
     * @param citycode
     * @param datas
     * @return
     */
    private List<String> outputAreainfoSQL(String citycode, Map<String, Object> datas) {
        List<String> sqls = Lists.newArrayList();
        try {
            // 行政区
            List<Map<String, Object>> areasinfo = Lists.newArrayList();
            if (datas.containsKey("areasinfo")) {
                areasinfo = (List<Map<String, Object>>) datas.get("areasinfo");
            }
            sqls.addAll(this.outputAreaSQL(citycode, areasinfo));
            
            // 子行政区
            List<Map<String, Object>> subareasinfo = Lists.newArrayList();
            if (datas.containsKey("subareasinfo")) {
                subareasinfo = (List<Map<String, Object>>) datas.get("subareasinfo");
            }
            
            // 地标
            List<Map<String, Object>> landmarks = Lists.newArrayList();
            if (datas.containsKey("landmarks")) {
                landmarks = (List<Map<String, Object>>) datas.get("landmarks");
            }
            sqls.addAll(this.outputLandmarkSQL(citycode, landmarks));
        } catch (Exception e) {
            logger.error("outputAreainfoSQL:: method error: {}", e.getLocalizedMessage());
            throw e;
        }
        return sqls;
    }
    
    /**
     * 
     * @param citycode
     * @param areainfos
     * @return
     */
    private List<String> outputAreaSQL(String citycode, List<Map<String, Object>> areainfos) {
        List<String> sqls = Lists.newArrayList();
        try {
            StringBuffer bfsql = new StringBuffer();
            sqls.add("/****************************** 输出行政区SQL脚本: 开始 ***********************************/");
            for (Map<String, Object> areainfo : areainfos) {
                bfsql.setLength(0);
                String areaid = String.valueOf(areainfo.get("id"));
                String areaname = String.valueOf(areainfo.get("name"));
                String pinyin = String.valueOf(areainfo.get("slug"));
                List<Map<String, Object>> pois = Lists.newArrayList();
                double lat = 0;
                double lon = 0;
                String discode = "";
                if (areainfo.containsKey("poi")) {
                    pois = (List<Map<String, Object>>) areainfo.get("poi");
                    Map<String, Object> poi = pois.get(0);
                    Map<String, Object> location = (Map<String, Object>) poi.get("location");
                    discode = poi.get("adcode") == null ? "" : String.valueOf(poi.get("adcode"));
                    lat = Double.valueOf(String.valueOf(location.get("lat")));
                    lon = Double.valueOf(String.valueOf(location.get("lng")));
                } else {
                    lat = 0;
                    lon = 0;
                }
                bfsql.append("insert into s_areainfo (areaid, areaname, pinyin, lat, lng, citycode, discode) values (")
                    .append(areaid).append(",").append("'").append(areaname).append("'")
                    .append(",").append("'").append(pinyin).append("'")
                    .append(",").append(lat).append(",").append(lon)
                    .append(",").append("'").append(citycode).append("'")
                    .append(",").append("'").append(discode).append("'")
                    .append(");");
                // 添加到SQL脚本
                sqls.add(bfsql.toString());
            }
            sqls.add("/****************************** 输出行政区SQL脚本: 结束 ***********************************/");
        } catch (Exception e) {
            logger.error("outputAreaSQL:: method error: {}", e.getLocalizedMessage());
        }
        return sqls;
    }
    
    /**
     * 
     * @param citycode
     * @param ids
     * @return
     */
    private List<String> outputHotAreasSQL(String citycode, List ids) {
        return Lists.newArrayList();
    }
    
    /**
     * 
     * @param citycode
     * @param landmarks
     * @return
     */
    private List<String> outputLandmarkSQL(String citycode, List<Map<String, Object>> landmarks) {
        List<String> sqls = Lists.newArrayList();
        try {
            sqls.add("/****************************** 输出景点SQL脚本: 开始 ***********************************/");
            StringBuffer bfsql = new StringBuffer();
            for (Map<String, Object> landmark : landmarks) {
                //
                bfsql.setLength(0);
                String landmarkid = String.valueOf(landmark.get("id"));
                String landmarkname = String.valueOf(landmark.get("name"));
                String pinyin = String.valueOf(landmark.get("slug"));
                // 地标数据分类:
                // 1大学，2火车站，3景点，4机场，5医院，6商场商圈，7高校学院，8汽车站
                Integer ltype = Integer.valueOf(String.valueOf(landmark.get("type")));
                // OTS搜索分类:
                // 0附近；1商圈；2机场车站；3地铁路线；4行政区；5景点；6医院；7高校；8酒店；9地址；-1不限
                Integer otstype = HotelSearchEnum.ALL.getId();
                // 转换类型
                switch (ltype) {
                case 1:
                case 7:
                    // 高校
                    otstype = HotelSearchEnum.COLLEGE.getId();
                    break;
                case 2:
                case 4:
                case 8:
                    // 机场车站
                    otstype = HotelSearchEnum.AIRPORT.getId();
                    break;
                case 3:
                    // 景点
                    otstype = HotelSearchEnum.SAREA.getId();
                    break;
                case 5:
                    // 医院
                    otstype = HotelSearchEnum.HOSPITAL.getId();
                    break;
                case 6:
                    // 商圈
                    otstype = HotelSearchEnum.BZONE.getId();
                    break;
                default:
                    otstype = HotelSearchEnum.ALL.getId();
                    break;
                }
                
                Double lat = null;
                Double lon = null;
                String discode = "";
                List<Map<String, Object>> pois = Lists.newArrayList();
                if (landmark.containsKey("poi")) {
                    pois = (List<Map<String, Object>>) landmark.get("poi");
                    Map<String, Object> poi = pois.get(0);
                    discode = poi.get("adcode") == null ? "" : String.valueOf(poi.get("adcode"));
                    Map<String, Object> location = (Map<String, Object>) poi.get("location");
                    lat = Double.valueOf(String.valueOf(location.get("lat")));
                    lon = Double.valueOf(String.valueOf(location.get("lng")));
                } else {
                    logger.error("地标数据id: {}, name: {}没有坐标数据.", landmarkid, landmarkname);
                    continue;
                }
                bfsql.append("insert into s_landmark (landmarkid, landmarkname, pinyin, ltype, lat, lng, citycode, discode) values (")
                    .append(landmarkid).append(",").append("'").append(landmarkname).append("'")
                    .append(",").append("'").append(pinyin).append("'").append(",").append(otstype)
                    .append(",").append(lat).append(",").append(lon)
                    .append(",").append("'").append(citycode).append("'")
                    .append(",").append("'").append(discode).append("'")
                    .append(");");
                // 添加到SQL脚本
                sqls.add(bfsql.toString());
            }
            sqls.add("/****************************** 输出景点SQL脚本: 结束 ***********************************/");
        } catch (Exception e) {
            logger.error("outputLandmarkSQL:: method error: {}", e.getLocalizedMessage());
        }
        return sqls;
    }
    
    /**
     * 
     * @param citycode
     * @param hotlandmarks
     * @return
     */
    private List<String> outputHotLandmarkSQL(String citycode, List<Map<String, Object>> hotlandmarks) {
        return Lists.newArrayList();
    }

    /**
     * 删除城市位置区域信息：3地铁，5地标。
     * @param citycode
     * 参数：城市编码
     * @param typeid
     * 参数：数据分类，3地铁；5地标
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> readonlyDeletePoiDatas(String citycode, Integer typeid) throws Exception {
        Map<String, Object> result = Maps.newHashMap();
        try {
            int limit = 10000;
            SearchRequestBuilder searchBuilder = esProxy.prepareSearch(ElasticsearchProxy.OTS_INDEX_DEFAULT, ElasticsearchProxy.POSITION_TYPE_DEFAULT);

            List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
            QueryFilterBuilder citycodeFilter = FilterBuilders.queryFilter(QueryBuilders.termQuery("citycode",citycode));
            QueryFilterBuilder typeidFilter = FilterBuilders.queryFilter(QueryBuilders.termQuery("ptype", typeid));
            filterBuilders.add(citycodeFilter);
            filterBuilders.add(typeidFilter);
            
            FilterBuilder[] builders = new FilterBuilder[] {};
            BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
//            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
//                .must(QueryBuilders.matchQuery("visible", Constant.STR_TRUE))
//                .must(QueryBuilders.matchQuery("online", Constant.STR_TRUE));
//            boolFilter.must(FilterBuilders.queryFilter(boolQueryBuilder));
            searchBuilder.setFrom(0).setSize(limit).setExplain(true);
            searchBuilder.setPostFilter(boolFilter);
            SearchResponse searchResponse = searchBuilder.execute().actionGet();
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchResponse.getHits().totalHits();
            logger.info("search hotel by citycode:{}, typeid:{}, success: total {} found.", citycode, typeid, totalHits);
            SearchHit[] hits = searchHits.getHits();
            for (int i = 0; i < hits.length; i++) {
                SearchHit hit = hits[i];
                esProxy.deleteDocument(ElasticsearchProxy.OTS_INDEX_DEFAULT, ElasticsearchProxy.POSITION_TYPE_DEFAULT, hit.getId());
            }
            result.put(ServiceOutput.STR_MSG_SUCCESS, true);
            result.put("citycode:{}, typeid:{}, total delete poidatas count: ", hits.length);
        } catch (Exception e) {
            result.put(ServiceOutput.STR_MSG_SUCCESS, false);
            result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            result.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
            e.printStackTrace();
        }
        return result;
    }

}
