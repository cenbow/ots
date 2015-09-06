package com.mk.ots.promo.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.promo.dao.IBPromotionProductDao;
import com.mk.ots.promo.model.BPromotionProduct;
import com.mk.ots.promo.service.IPromotionProductService;

@Service
public class PromotionProductService implements IPromotionProductService{
	final Logger logger = LoggerFactory.getLogger(PromotionProductService.class);

	@Autowired
	private IBPromotionProductDao iBPromotionProductDao;
	@Override
	public BPromotionProduct queryBPromotionProduct(Long productid) {
		return iBPromotionProductDao.queryBPromotionProduct(productid);
	}
}
