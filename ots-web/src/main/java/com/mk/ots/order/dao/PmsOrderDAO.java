package com.mk.ots.order.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.order.bean.PmsOrder;

@Repository(value="pmsOrderDAO4ots")
public class PmsOrderDAO {

	public List<PmsOrder> findNotOverPmsOrder(Integer days) {
		//查找未结束的酒店预订单（PmsOrder 条件 cancel是false  orderNum 和planNum不想等的 begintime和endtime 在3个月内 ）//days天内
		StringBuffer sql = new StringBuffer("select * from  b_pmsorder p where p.Cancel = 'F' and p.Ordernum <> p.Plannum ");
		List<Object> paras = new ArrayList<>();
		String begin = DateUtils.getDate() + " 00:00:00";
		String end = DateUtils.getDateAdded(days + 2, begin);
		sql.append(" and beginTime <= ?");
		paras.add(begin);
		sql.append(" and endTime >= ?");
		paras.add(end);
		sql.append("  and p.Begintime between ? and ?");
		paras.add(begin);
		paras.add(end);
		return PmsOrder.dao.find(sql.toString(), paras);
	}

}
