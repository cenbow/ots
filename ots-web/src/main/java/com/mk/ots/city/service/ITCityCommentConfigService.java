package com.mk.ots.city.service;

import com.mk.ots.city.model.TCityCommentConfig;

import java.math.BigDecimal;

/**
 * Created by nolan on 15/9/12.
 */
public interface ITCityCommentConfigService {
    /**
     * 根据城市编码查询返现金额
     *
     * @param citycode 城市编码
     * @return TCityCommentConfig
     */
    BigDecimal findCashbackByCitycode(Long citycode);

    TCityCommentConfig findCashbackEntityByCitycode(Long citycode);

}
