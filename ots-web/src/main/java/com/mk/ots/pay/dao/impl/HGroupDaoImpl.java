package com.mk.ots.pay.dao.impl;

import java.util.Map;
import org.springframework.stereotype.Component;
import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.pay.dao.HGroupDao;
import com.mk.ots.pay.model.HGroup;

@Component
public class HGroupDaoImpl extends MyBatisDaoImpl<HGroup, Long> implements HGroupDao {
 
	@Override
	public HGroup getByHotelId(Long hotelid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("hotelid", hotelid);
		return this.findOne("selectByPrimaryKey", param);
	}
	
 
 
}