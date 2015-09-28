package com.mk.ots.mapper;

import com.mk.ots.order.model.OtaOrderMac;

public interface OtaOrderMacMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OtaOrderMac record);

    int insertSelective(OtaOrderMac record);

    OtaOrderMac selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OtaOrderMac record);

    int updateByPrimaryKey(OtaOrderMac record);
}