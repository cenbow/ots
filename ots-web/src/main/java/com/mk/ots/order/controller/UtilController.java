package com.mk.ots.order.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.service.OrderServiceImpl;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.order.service.PmsOrderServiceImpl;


@RestController
@RequestMapping("/util")
public class UtilController {
	
	private static Logger logger = LoggerFactory.getLogger(OrderController.class);
	
    @Autowired
    private OtsCacheManager manager;
    
	@RequestMapping(value = "/pmsroomorderok", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> createOrder(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		List<Bean> beans = Db.find("SELECT t.Hotelid, t.PmsroomOrderNo\n" +
									"FROM\n" +
									"    b_pmsroomorder t\n" +
									"WHERE\n" +
									"    t.Begintime > '2015-06-14'\n" +
									"GROUP BY t.Hotelid , t.PmsroomOrderNo\n" +
									"HAVING COUNT(1) > 1");
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
						} else if (!key.equals("id") 
								&& tempOrder.get(key) instanceof BigDecimal 
								&& order.get(key) instanceof BigDecimal 
								&& tempOrder.getBigDecimal(key).longValue() == 0l
								&& order.getBigDecimal(key).longValue() != 0l) {
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
			logger.info("delete from b_pmsroomorder where id in(" + ids.toString() + ")");
			Db.update("delete from b_pmsroomorder where id in(" + ids.toString() + ")");
		}
		logger.info("ok处理完成");
		jsonObj.put("message", "ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/cancelOrderJob", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> cancelOrderJob(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		// 10个线程 同时插入订单
		for (int i = 0; i < 10; i++) {
			MyThread mt = new MyThread();
			mt.start();
		}
		logger.info("ok处理完成");
		jsonObj.put("message", "ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	class MyThread extends Thread {
		public void run() {
			
		}
	}
	
	@RequestMapping(value = "/checkweiiyi", method = RequestMethod.POST)
	public void checkorder(){
		try{
			PmsRoomOrder prorder = new PmsRoomOrder();
			prorder.set("Hotelid", Long.parseLong("1441"));
			prorder.set("PmsRoomOrderNo", "K1001150619013");
			prorder.set("RoomTypePms", "3");
			prorder.set("RoomTypeId", Long.parseLong("2915"));
			prorder.set("RoomId", Long.parseLong("21537"));
			prorder.set("Roomno", "305");
			prorder.set("RoomTypeName", "标准间");
			prorder.set("RoomPms", "59");
			prorder.set("Begintime", DateUtils.getDateFromString("2015-06-19 18:00:00", "yyyyMMddHHmmss"));
			prorder.set("Endtime", DateUtils.getDateFromString("2015-06-20 12:00:00","yyyyMMddHHmmss"));
			prorder.set("Ordertype", Integer.parseInt("2"));
			prorder.set("visible", "T");
			prorder.save();
		} catch(Exception e) {
			
		}
	}

	
	@RequestMapping(value="/lookRedisForJob", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> findAll(String cacheName,String keyStr){
	    
		if (manager == null) {
            manager = AppUtils.getBean(OtsCacheManager.class);
        }
		
		Jedis jedis = manager.getNewJedis();
		Map<String, String> orderMap =  jedis.hgetAll("orderJobList");
		try {
			orderMap.put(ServiceOutput.STR_MSG_SUCCESS, "true");
	    
        } catch (Exception e) {
        	orderMap.put(ServiceOutput.STR_MSG_SUCCESS, "false");
        } finally {
        	jedis.close();
        }
		return new ResponseEntity<Map<String, String>>(orderMap,HttpStatus.OK);
	}
	
	@RequestMapping(value="/test", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> test(String cacheName,String keyStr){
	    JSONObject s = new JSONObject();
	    
	    OrderServiceImpl serv = AppUtils.getBean(OrderServiceImpl.class);
	    PmsRoomOrder order =  serv.findPmsRoomOrderById("K1001150602007", 1432l);
	    System.out.println(order);
		s.put("sucess", true);
		return new ResponseEntity<JSONObject>(s,HttpStatus.OK);
	}
}
