package com.mk.ots.roomsale.service.impl;

import com.mk.ots.mapper.RoomSaleConfigInfoMapper;
import com.mk.ots.mapper.RoomSaleShowConfigMapper;
import com.mk.ots.roomsale.model.TRoomSaleCity;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TRoomSaleShowConfigServiceImpl implements TRoomSaleShowConfigService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(TRoomSaleShowConfigServiceImpl.class);

	@Autowired
	private RoomSaleShowConfigMapper roomSaleShowConfigMapper;


    public List<TRoomSaleShowConfig> queryTRoomSaleShowConfig(String cityid){
        logger.info(" method queryTRoomSaleShowConfig  parame   cityid:" + cityid);
        List<TRoomSaleCity>   tRoomSaleCityList   =  this.queryTRoomSaleCity(cityid);
        if(CollectionUtils.isEmpty(tRoomSaleCityList)){
            logger.info(" method queryTRoomSaleShowConfig  querytRoomSaleCityList is  null ");
            return null;
        }
        String  ids = "";
        for(TRoomSaleCity  tRoomSaleCity:tRoomSaleCityList){
            if(0==tRoomSaleCity.getSaleTypeId()){
                continue;
            }
            ids = ids + tRoomSaleCity.getSaleTypeId() + ",";
        }
        if(StringUtils.isEmpty(ids)){
            return null;
        }
        logger.info(" method queryTRoomSaleShowConfig   parame  ids   " +  ids);
        if(ids.endsWith(",")){
            ids = ids.substring(0,ids.length()-1);
        }
        return  roomSaleShowConfigMapper.queryTRoomSaleShowConfigByIds(ids);
    }


    public  List<TRoomSaleCity> queryTRoomSaleCity(String cityid) {
        logger.info(" method queryTRoomSaleCity   parame  cityid   " +  cityid);
        HashMap  map=new HashMap<>();
        map.put("citycode", cityid);
        return roomSaleShowConfigMapper.queryTRoomSaleCity(map);
    }
}
