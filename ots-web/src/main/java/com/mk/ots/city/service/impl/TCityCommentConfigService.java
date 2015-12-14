package com.mk.ots.city.service.impl;

import com.mk.ots.city.dao.ITCityCommentConfigDAO;
import com.mk.ots.city.model.TCityCommentConfig;
import com.mk.ots.city.service.ITCityCommentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by nolan on 15/9/12.
 */
@Service
public class TCityCommentConfigService implements ITCityCommentConfigService {
    @Autowired
    private ITCityCommentConfigDAO itCityCommentConfigDAO;

    @Override
    public BigDecimal findCashbackByCitycode(Long citycode) {
        TCityCommentConfig tCityCommentConfig = itCityCommentConfigDAO.findCityConfigByCitycode(citycode);
        if (tCityCommentConfig != null && tCityCommentConfig.getValue().compareTo(BigDecimal.ZERO) > 0) {
            return tCityCommentConfig.getValue();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public TCityCommentConfig findCashbackEntityByCitycode(Long citycode) {
        TCityCommentConfig tCityCommentConfig = itCityCommentConfigDAO.findCityConfigByCitycode(citycode);
        if (tCityCommentConfig != null && tCityCommentConfig.getValue().compareTo(BigDecimal.ZERO) > 0) {
            return tCityCommentConfig;
        }
        return null;
    }
}
