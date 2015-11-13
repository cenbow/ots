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

    public BigDecimal queryOnlineGiveHotel(String cityCode) {
        TPromoteConfig config = this.queryGiveHotel(cityCode);
        if (null == config) {
            return BigDecimal.ZERO;
        } else {
            return config.getOnlineGiveHotel();
        }
    }
    public BigDecimal queryOfflineGiveHotel(String cityCode) {
        TPromoteConfig config = this.queryGiveHotel(cityCode);
        if (null == config) {
            return BigDecimal.ZERO;
        } else {
            return config.getOfflineGiveHotel();
        }
    }

    public BigDecimal queryGiveNewMemberGeneral(String cityCode) {
        TPromoteConfig config = this.queryGiveHotel(cityCode);
        if (null == config) {
            return BigDecimal.ZERO;
        } else {
            return config.getGiveNewMemberGeneral();
        }
    }
    public BigDecimal queryGiveNewMemberAppOnly(String cityCode) {
        TPromoteConfig config = this.queryGiveHotel(cityCode);
        if (null == config) {
            return BigDecimal.ZERO;
        } else {
            return config.getGiveNewMemberAppOnly();
        }
    }
}
