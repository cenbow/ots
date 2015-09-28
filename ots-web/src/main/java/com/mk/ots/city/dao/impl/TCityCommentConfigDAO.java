package com.mk.ots.city.dao.impl;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.city.dao.ITCityCommentConfigDAO;
import com.mk.ots.city.model.TCityCommentConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by nolan on 15/9/12.
 */
@Component
public class TCityCommentConfigDAO extends MyBatisDaoImpl<TCityCommentConfig, Long> implements ITCityCommentConfigDAO {

    @Override
    public TCityCommentConfig findCityConfigByCitycode(Long citycode) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("citycode", citycode);
        return super.findOne("findCityConfigByCitycode", param);
    }
}
