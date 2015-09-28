package com.mk.ots.mapper;

import com.mk.ots.order.model.BOrderLog;

public interface BOrderLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BOrderLog record);

    int insertSelective(BOrderLog record);

    BOrderLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BOrderLog record);

    int updateByPrimaryKey(BOrderLog record);
    
    int updateByOrderId(BOrderLog record);
}