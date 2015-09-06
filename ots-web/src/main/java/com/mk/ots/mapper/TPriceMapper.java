package com.mk.ots.mapper;

import com.mk.ots.hotel.model.TPrice;


public interface TPriceMapper {

    TPrice selectByPrimaryKey(Long id);
    
    TPrice getPriceOfTime(Long timeid);

}