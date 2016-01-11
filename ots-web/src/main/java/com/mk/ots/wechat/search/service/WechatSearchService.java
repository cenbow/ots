package com.mk.ots.wechat.search.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.restful.output.SearchPositiontypesRespEntity;

public interface WechatSearchService {

    /**
     * 获取 区域位置类型
     * @param citycode
     * @param positiontypeid
     */
    public List<SearchPositiontypesRespEntity> readonlyPositionTypes(String citycode, Long positiontypeid);

    /**
     * 查询位置区域
     * @param citycode
     * @param ptype
     */
    public Map<String, Object> readonlyPositions(String citycode, String ptype);

    /**
     * 查询用户附件的位置区域
     * @param citycode
     * @param ptype
     */
    public Map<String, Object> readonlyNearPositions(String citycode, String ptype, Double userlatitude, Double userlongitude);

    /**
     * 模糊查询位置区域
     * @param citycode
     * @param keyword
     */
    public Map<String, Object> readonlyPositionsFuzzy(String citycode, String keyword);
    
    /**
     * 酒店综合查询接口方法
     * @param params
     * @return
     */
    public Map<String, Object> readonlySearchHotels(HotelQuerylistReqEntity params);
    
    /**
     * 同步城市位置区域数据到ES.
     * @param citycode
     * 参数：城市编码
     * @param typeid
     * 参数：区域位置分类(0附近；1商圈；2机场车站；3地铁路线；4行政区；5景点；6医院；7高校；8酒店；9地址；-1不限)
     * @param forceUpate
     * 参数：是否强制更新
     * @return
     */
    public Map<String, Object> readonlySyncCityPOI(String citycode, String typeid, boolean forceUpate);
    
    /**
     * check if it is in current promotion period 
     * 
     * @return
     */
    public boolean isInPromoPeriod();
}
