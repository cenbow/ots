package com.mk.ots.roomsale.service.impl;

import com.mk.ots.mapper.RoomSaleConfigInfoMapper;
import com.mk.ots.mapper.RoomSaleShowConfigMapper;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TRoomSaleShowConfigServiceImpl implements TRoomSaleShowConfigService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(TRoomSaleShowConfigServiceImpl.class);

	@Autowired
	private RoomSaleShowConfigMapper roomSaleShowConfigMapper;


    public List<TRoomSaleShowConfig> queryTRoomSaleShowConfig(String cityid,String  showArea){
       HashMap  map=new HashMap<>();
        map.put("citycode", cityid);
        if (!StringUtils.isEmpty(showArea)){
            map.put("showArea", showArea);
        }
        return roomSaleShowConfigMapper.queryTRoomSaleShowConfig(map);
    }
}
