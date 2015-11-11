package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.TRoomSaleCity;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;

import java.util.List;

public interface TRoomSaleShowConfigService {
    /**
     * @param cityid
     * @return
     */

    public List<TRoomSaleShowConfig> queryTRoomSaleShowConfig(String cityid,String showArea);

    public  List<TRoomSaleCity> queryTRoomSaleCity(String cityid);
}
