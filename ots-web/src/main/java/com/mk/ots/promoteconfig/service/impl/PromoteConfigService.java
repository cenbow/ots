package com.mk.ots.promoteconfig.service.impl;

import com.mk.ots.hotel.model.TCityModel;
import com.mk.ots.hotel.service.CityService;
import com.mk.ots.mapper.TPromoteConfigMapper;
import com.mk.ots.promoteconfig.model.TPromoteConfig;
import com.mk.ots.promoteconfig.service.IPromoteConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoteConfigService implements IPromoteConfigService {
    @Autowired
    private TPromoteConfigMapper promoteConfigMapper;
    @Autowired
    private CityService cityService;

    /**
     * 根据几线城市查询配置信息
     * @param cityLevel 1：一线城市 2：2线城市 3：3线城市
     * @return
     */
    public TPromoteConfig queryByCityLevel(Integer cityLevel) {
        return this.promoteConfigMapper.queryByCityLevel(cityLevel);
    }


    public TPromoteConfig queryGiveHotel(String cityCode) {

        TCityModel cityModel = this.cityService.findCityByCode(cityCode);
        if (null == cityModel) {
            return null;
        }

        Integer level = cityModel.getLevel();
        if (null == level) {
            return null;
        }
        TPromoteConfig config = this.queryByCityLevel(level);
        if (null == config) {
            return null;
        }
        return config;
    }

    /**
     * 在线支付返酒店老板金额
     * @param cityCode
     * @return
     */
    public BigDecimal queryOnlineGiveHotel(String cityCode) {
        TPromoteConfig config = this.queryGiveHotel(cityCode);
        if (null == config) {
            return BigDecimal.ZERO;
        } else {
            return config.getOnlineGiveHotel();
        }
    }

    /**
     * 到付返酒店老板金额
     * @param cityCode
     * @return
     */
    public BigDecimal queryOfflineGiveHotel(String cityCode) {
        TPromoteConfig config = this.queryGiveHotel(cityCode);
        if (null == config) {
            return BigDecimal.ZERO;
        } else {
            return config.getOfflineGiveHotel();
        }
    }
}
