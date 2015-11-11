package com.mk.ots.roomsale.service.impl;

import com.mk.ots.mapper.RoomSaleShowConfigMapper;
import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.model.TRoomSaleCity;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class TRoomSaleShowConfigServiceImpl implements TRoomSaleShowConfigService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(TRoomSaleShowConfigServiceImpl.class);

	@Autowired
	private RoomSaleShowConfigMapper roomSaleShowConfigMapper;


    public List<RoomSaleShowConfigDto> queryRoomSaleShowConfigByParams(RoomSaleShowConfigDto bean){
        List<TRoomSaleShowConfig>  tRoomSaleShowConfigList = roomSaleShowConfigMapper.queryRoomSaleShowConfigByParams(bean);
        List<RoomSaleShowConfigDto>  resultList =  new ArrayList();
        for(TRoomSaleShowConfig  showConfig:tRoomSaleShowConfigList){

            resultList.add(buildUMemberDto(showConfig));
        }
        return  resultList;
    }

    private RoomSaleShowConfigDto buildUMemberDto(TRoomSaleShowConfig bean) {
        if (bean==null){
            return new RoomSaleShowConfigDto();
        }
        RoomSaleShowConfigDto showDto=new RoomSaleShowConfigDto();
        showDto.setId(bean.getId());
        showDto.setPromotext(bean.getSaleName());
        showDto.setPromoid(bean.getSaleTypeId());
        showDto.setPromoicon(bean.getPicUrl());
        showDto.setBackPics(bean.getBackPicUrl());
        showDto.setBackColor(bean.getBackColor());
        showDto.setFontColor(bean.getFontColor());
        showDto.setFontFamily(bean.getFontFamily());
        showDto.setShowBeginTime(bean.getShowBeginTime());
        showDto.setShowEndTime(bean.getShowBeginTime());
        showDto.setPromoid(bean.getSaleTypeId());
        showDto.setPromoid(bean.getSaleTypeId());
        showDto.setPromoid(bean.getSaleTypeId());
        return showDto;
    }
    public  List<TRoomSaleCity> queryTRoomSaleCity(String cityid) {
        logger.info(" method queryTRoomSaleCity   parame  cityid   " +  cityid);
        HashMap  map=new HashMap<>();
        map.put("citycode", cityid);
        return roomSaleShowConfigMapper.queryTRoomSaleCity(map);
    }
}
