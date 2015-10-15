package com.mk.ots.ticket.dao.impl;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.BPromoStatuEnum;
import com.mk.ots.ticket.dao.IBPromoDao;
import com.mk.ots.ticket.model.BPromo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BPromoDaoImp extends MyBatisDaoImpl<BPromo, Long> implements
		IBPromoDao {

	/**
	 *  根据卷号和城市id查询已经被激活的卷
	 * @param promoPwd  卷密码
	 * @return  已经被激活的卷
	 */
	@Override
	public  BPromo  findBPromoByPromo(String  promoPwd) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("promoPwd", promoPwd);
		return findOne("findBPromoByPromo", param);
	}

	/**
	 * 查询卷号
	 * @param promoid   城市id
	 * @return  卷
	 */
	@Override
	public  BPromo  findBPromoById(long  promoid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("promoid", promoid);
		return findOne("findBPromoById", param);
	}

	@Override
	public void saveOrUpdate(BPromo bpromo) {
		if(bpromo.getId()!=null){
			 this.update(bpromo);
		 }else{
			 this.insert(bpromo);
		 }
	}

	@Override
	public void updateBpromoForUse(String promoPwd,int promoStatus,String  updateTime,String  updateBy) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("promoPwd", promoPwd);
		param.put("promoStatus", promoStatus);
		param.put("updateTime", updateTime);
		param.put("updateBy", updateBy);
		param.put("promoOldStatus", BPromoStatuEnum.activite.getType());

		super.update("updateBpromoForUse", param);
	}


}
