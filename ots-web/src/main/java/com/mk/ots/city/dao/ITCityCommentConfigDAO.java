package com.mk.ots.city.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.city.model.TCityCommentConfig;

/**
 * 城市返现数据操作类
 * <p/>
 * Created by nolan on 15/9/12.
 */
public interface ITCityCommentConfigDAO extends BaseDao<TCityCommentConfig, Long> {
    /**
     * 根据城市编码查询返现金额
     *
     * @param citycode 城市编码
     * @return TCityCommentConfig
     */
    TCityCommentConfig findCityConfigByCitycode(Long citycode);
}
