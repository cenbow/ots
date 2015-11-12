package com.mk.ots.promoteconfig.service;

import com.mk.ots.promoteconfig.model.TPromoteConfig;

import java.math.BigDecimal;

public interface IPromoteConfigService {
    public TPromoteConfig queryByCityLevel(Integer cityLevel);
    public TPromoteConfig queryGiveHotel(String cityCode);
    public BigDecimal queryOnlineGiveHotel(String cityCode);
    public BigDecimal queryOfflineGiveHotel(String cityCode);
}
