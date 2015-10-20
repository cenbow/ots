package com.mk.ots.roomsale.service.impl;

import com.mk.ots.mapper.RoomSaleConfigInfoMapper;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class RoomSaleConfigInfoServiceImpl implements RoomSaleConfigInfoService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(RoomSaleConfigInfoServiceImpl.class);

	@Autowired
	private RoomSaleConfigInfoMapper roomSaleConfigInfoMapper;


    public List<TRoomSaleConfigInfo> queryListBySaleTypeId(String cityid,int saleTypeId,int start,int limit){
        Map<String,Object> map=new HashMap<>();
        map.put("citycode",cityid);
        map.put("saleTypeId",saleTypeId);
        map.put("start",start);
        map.put("limit",limit);
        return roomSaleConfigInfoMapper.queryListBySaleTypeId(map);
    }
}
