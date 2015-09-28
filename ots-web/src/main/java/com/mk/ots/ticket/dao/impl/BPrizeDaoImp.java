package com.mk.ots.ticket.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.BPrizeDao;
import com.mk.ots.ticket.model.BPrize;
import com.mk.ots.ticket.model.BPrizeInfo;
@Component
public class BPrizeDaoImp extends MyBatisDaoImpl<BPrize, Long> implements BPrizeDao {

	@Override
	public List<BPrize> findBPrizeByActiveid(Long activeid, boolean flag) {
		Map<String,Object> of = Maps.newHashMap();
		of.put("activeid", activeid);
		of.put("flag", flag);
		return find("findBPrizeByActiveid", of);
	}

	@Override
	public void saveOrUpdate(BPrize bPrize) {
		if (bPrize.getId()==null) {
			insert(bPrize);
		}else {
			update(bPrize);
		}
		
	}
	public BPrize findPrizeById(long prizeId) {
		// TODO Auto-generated method stub
		Map<String,Object> param = Maps.newHashMap();
		param.put("id", prizeId);
		return findOne("findBPrizeById", param);
	}

}
