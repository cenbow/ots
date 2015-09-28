package com.mk.ots.pay.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.pay.model.HGroup;

public interface HGroupDao extends BaseDao<HGroup, Long>{

	public HGroup getByHotelId(Long hotelid);
	
	
}