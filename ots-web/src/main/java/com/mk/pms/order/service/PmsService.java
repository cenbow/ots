package com.mk.pms.order.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.order.bean.PmsRoomOrder;

@Service
public class PmsService {
	private static Logger logger = LoggerFactory.getLogger(PmsService.class);

	public void genPmsRoomOrder() {
		List<Bean> beans = Db.find("SELECT t.Hotelid, t.PmsroomOrderNo FROM b_pmsroomorder t GROUP BY t.Hotelid , t.PmsroomOrderNo HAVING COUNT(1) > 1");
		String status = "RE,RX,IN,PM,OK";
		for (Bean bean : beans) {
			List<PmsRoomOrder> orders = PmsRoomOrder.dao.find("select * from b_pmsroomorder where hotelid=? and pmsroomorderno=?",
					bean.getLong("Hotelid"), bean.getStr("PmsroomOrderNo"));
			PmsRoomOrder tempOrder = null;
			// 找到status状态最新的order
			for (PmsRoomOrder order : orders) {
				if (tempOrder == null) {
					tempOrder = order;
				} else {
					if (status.indexOf(order.getStr("Status")) > status.indexOf(tempOrder.getStr("Status"))) {
						tempOrder = order;
					}
				}
			}
			logger.info("tempOrder:{}", tempOrder);
			StringBuffer ids = new StringBuffer();
			for (PmsRoomOrder order : orders) {
				// id不一样
				if (!tempOrder.getLong("id").equals(order.getLong("id"))) {
					for (String key : tempOrder.getAttrNames()) {
						// 排除id
						if (!key.equals("id") && tempOrder.get(key) == null && order.get(key) != null) {
							// 字符串、日期的情况
							tempOrder.set(key, order.get(key));
						} else if (!key.equals("id") && tempOrder.get(key) instanceof BigDecimal && order.get(key) instanceof BigDecimal
								&& tempOrder.getBigDecimal(key).longValue() == 0l && order.getBigDecimal(key).longValue() != 0l) {
							// 数字的情况
							tempOrder.set(key, order.get(key));
						}
					}
					ids.append(order.getLong("id").intValue()).append(",");
				}
			}
			tempOrder.set("visible", "T");
			tempOrder.saveOrUpdate();

			ids.setLength(ids.length() - 1);
			logger.info("留下订单id{}", tempOrder.get("id"));
			logger.info("delete from b_pmsroomorder where id in(" + ids.toString() + ")");
			Db.update("delete from b_pmsroomorder where id in(" + ids.toString() + ")");
		}

	}

}
