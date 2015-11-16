package com.mk.ots.mapper;

import com.mk.ots.order.model.OtaOrderMac;

import java.util.List;
import java.util.Map;

public interface OtaOrderMacMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OtaOrderMac record);

    int insertSelective(OtaOrderMac record);

    OtaOrderMac selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OtaOrderMac record);

    int updateByPrimaryKey(OtaOrderMac record);

    OtaOrderMac selectByOrderId(Long orderId);

    List<OtaOrderMac> selectByUuid(Map<String,Object> map);
    List<OtaOrderMac> selectByDeviceimei(Map<String,Object> map);
}