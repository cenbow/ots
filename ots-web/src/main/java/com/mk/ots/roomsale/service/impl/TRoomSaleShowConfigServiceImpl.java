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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TRoomSaleShowConfigServiceImpl implements TRoomSaleShowConfigService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(TRoomSaleShowConfigServiceImpl.class);

	@Autowired
	private RoomSaleShowConfigMapper roomSaleShowConfigMapper;


    public List<TRoomSaleShowConfig> queryTRoomSaleShowConfig(String cityid,String showArea){
        logger.info(" method queryTRoomSaleShowConfig  parame   cityid:" + cityid);
        List<TRoomSaleCity>   tRoomSaleCityList   =  this.queryTRoomSaleCity(cityid);
        if(CollectionUtils.isEmpty(tRoomSaleCityList)){
            logger.info(" method queryTRoomSaleShowConfig  querytRoomSaleCityList is  null ");
            return null;
        }
        HashMap  map=new HashMap<>();
        if(!StringUtils.isEmpty(showArea)){
            map.put("showArea",showArea);
        }
        List<TRoomSaleShowConfig>  resultList =  new ArrayList();
        for(TRoomSaleCity  tRoomSaleCity:tRoomSaleCityList){
            if(0==tRoomSaleCity.getSaleTypeId()){
                continue;
            }
            map.put("saleTypeId",tRoomSaleCity.getSaleTypeId());
            List<TRoomSaleShowConfig>  tRoomSaleShowConfigList = roomSaleShowConfigMapper.queryTRoomSaleShowConfig(map);
            resultList.addAll(tRoomSaleShowConfigList);
        }

        return  resultList;
    }


    public  List<TRoomSaleCity> queryTRoomSaleCity(String cityid) {
        logger.info(" method queryTRoomSaleCity   parame  cityid   " +  cityid);
        HashMap  map=new HashMap<>();
        map.put("citycode", cityid);
        return roomSaleShowConfigMapper.queryTRoomSaleCity(map);
    }
}
