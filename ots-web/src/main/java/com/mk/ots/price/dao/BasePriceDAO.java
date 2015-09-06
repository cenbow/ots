package com.mk.ots.price.dao;

import org.springframework.stereotype.Repository;

import com.mk.ots.order.dao.BaseDAO;
import com.mk.ots.price.bean.TBasePrice;

@Repository
public class BasePriceDAO extends BaseDAO {

	public TBasePrice findBasePrice(Long roomTypeId) {
		return TBasePrice.dao.findFirst("select * from t_baseprice where roomTypeId =?", roomTypeId);
	}

}