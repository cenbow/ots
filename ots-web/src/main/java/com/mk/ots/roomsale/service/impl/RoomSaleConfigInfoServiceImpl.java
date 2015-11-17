package com.mk.ots.roomsale.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.mapper.RoomSaleConfigInfoMapper;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;


@Service
public class RoomSaleConfigInfoServiceImpl implements RoomSaleConfigInfoService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(RoomSaleConfigInfoServiceImpl.class);

	@Autowired
	private RoomSaleConfigInfoMapper roomSaleConfigInfoMapper;


    public List<TRoomSaleConfigInfo> queryListBySaleTypeId(String cityid,int saleTypeId,int start,int limit){
        return roomSaleConfigInfoMapper.queryListBySaleTypeId(saleTypeId);
    }
}
