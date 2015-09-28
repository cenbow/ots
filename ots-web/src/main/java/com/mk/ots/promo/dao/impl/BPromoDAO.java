package com.mk.ots.promo.dao.impl;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.promo.model.BPromotion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BPromoDAO extends MyBatisDaoImpl<BPromotion, Long> implements IBPromotionDao {
	
	@Override
	public void saveOrUpdate(BPromotion bPromotion) {
		if(bPromotion.getId()!=null){
			this.update(bPromotion);
		}else{
			this.insert(bPromotion);
		}
	}

	@Override
	public List<BPromotion> findByPromotionType(
			PromotionTypeEnum promotionTypeEnum) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("promotiontype", promotionTypeEnum.getId());
		return this.find("findByPromotionType", param);
	}
	
	@Override
	public List<BPromotion> findByActiveId(Long activeid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("activeid", activeid);
		return this.find("findByActiveId", param);
	}
	
	@Override
	public List<BPromotion> queryAllOrderQikePromotions(Long mid, Long otaorderid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("otaorderid", otaorderid);
		return this.find("queryAllOrderQikePromotions", param);
	}
	
	@Override
	public List<BPromotion> queryYiJiaAndQiKePromotionByOrderId(Long otaorderid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("otaorderid", otaorderid);
		return this.find("queryYiJiaAndQiKeAndLiJianPromotionByOrderId", param);
	}

	@Override
	public List<BPromotion> queryPromotionByOrderId(Long otaorderid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("otaorderid", otaorderid);
		return this.find("queryPromotionByOrderId", param);
	}
	
	/**
	 * 根据订单id查询用户所有的优惠信息：切客券，议价券，其他优惠券
	 */
	@Override
	public List<BPromotion> queryBPromotionByOrderId(Long otaorderid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("otaorderid", otaorderid);
		return this.find("queryBPromotionByOrderId", param);
	}
	

    @Override
    public List<BPromotion> findFirstOrderPromotionByHardwarecode(String hardwarecode) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("hardwarecode", hardwarecode);
        return find("findFirstOrderPromotionByHardwarecode", param);
    }
}
