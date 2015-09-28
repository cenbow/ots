package com.mk.ots.mapper;

import java.util.List;

import com.mk.ots.order.model.BOrderBusinessLog;

public interface BOrderBusinessLogMapper {
    int deleteByPrimaryKey(Long id);

	int insert(BOrderBusinessLog record);

	int insertSelective(BOrderBusinessLog record);

	BOrderBusinessLog selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(BOrderBusinessLog record);

	int updateByPrimaryKey(BOrderBusinessLog record);
	
	List<BOrderBusinessLog> selectByOrderId(Long orderId);

}