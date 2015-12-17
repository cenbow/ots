package com.mk.ots.roomsale.service.impl;

import com.mk.ots.mapper.RoomSaleConfigInfoMapper;
import com.mk.ots.roomsale.model.TPriceScopeDto;

import com.mk.ots.roomsale.service.TPriceScopeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.support.Exceptions;


import java.util.HashMap;
import java.util.List;

/**
 * Created by jeashi on 2015/12/11.
 */
@Service
public class TPriceScoreSerivceImpl implements TPriceScopeService {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(TPriceScoreSerivceImpl.class);

    @Autowired
    private RoomSaleConfigInfoMapper roomSaleConfigInfoMapper;

    public List<TPriceScopeDto> queryTPriceScopeDto(String  promoId,String cityCode)throws Exception{
        logger.info(" begin method:[TPriceScoreSerivceImpl.queryTPriceScopeDto]");
        if(StringUtils.isEmpty(promoId)){
            logger.error(" \" promoId \"can not be  null");
            throw new Exception(" \" promoId \"can not be  null");
        }
        if(StringUtils.isEmpty(promoId)){
            cityCode = "-1" ;
            logger.info(" \" cityCode \" is  null set  value : \"-1\" ");
        }
        HashMap hm = new HashMap();
        hm.put("promoId",promoId);
        hm.put("cityCode", cityCode);

        return  roomSaleConfigInfoMapper.queryTPriceScopeDtoByPromoId(hm);
    }
}
