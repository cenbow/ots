package com.mk.pms.order.control;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.controller.OrderController;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.order.service.NewPmsOrderService;
import com.mk.pms.order.service.PmsOrderService;
import com.mk.pms.order.service.PmsService;

@RestController
@RequestMapping("/pmsutil")
public class PmsUtilController {

	private static Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OtsCacheManager manager;
	@Autowired
	PmsService pmsService;
	@Autowired
	PmsOrderService pmsOrderService;
	@Autowired
	NewPmsOrderService newPmsOrderService;
	@Autowired
	OtsCacheManager cacheManager;
	
	@Autowired
	OrderService orderService;

	private static ExecutorService pool = Executors.newFixedThreadPool(5);

	@RequestMapping(value = "/pmsroomorderok", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> createOrder(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		this.pmsService.genPmsRoomOrder();
		PmsUtilController.logger.info("ok处理完成");
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
		PmsUtilController.logger.info("ok处理完成");
		jsonObj.put("message", "ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	class MyThread extends Thread {
		@Override
		public void run() {

		}
	}

	@RequestMapping(value = "/checkweiiyi", method = RequestMethod.POST)
	public void checkorder() {
		try {
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
			prorder.set("Endtime", DateUtils.getDateFromString("2015-06-20 12:00:00", "yyyyMMddHHmmss"));
			prorder.set("Ordertype", Integer.parseInt("2"));
			prorder.set("visible", "T");
			prorder.save();
		} catch (Exception e) {

		}
	}

	@RequestMapping(value = "/lookRedisForJob", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> findAll(String cacheName, String keyStr) {

		if (this.manager == null) {
			this.manager = AppUtils.getBean(OtsCacheManager.class);
		}

		Jedis jedis = this.manager.getNewJedis();
		Map<String, String> orderMap = jedis.hgetAll("orderJobList");
		try {
			orderMap.put(ServiceOutput.STR_MSG_SUCCESS, "true");

		} catch (Exception e) {
			orderMap.put(ServiceOutput.STR_MSG_SUCCESS, "false");
		} finally {
			jedis.close();
		}
		return new ResponseEntity<Map<String, String>>(orderMap, HttpStatus.OK);
	}

	/**
	 * @param hotelId
	 * @param roomNos
	 *            多个用逗号分隔 全量更新，同步信息，处理应离未离问题 同步endtime是今天的
	 */
	@RequestMapping(value = "/synPmsOrder", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> synPmsOrder(String hotelId, final String roomNos) {
		PmsUtilController.logger.info("pmsutil::synPmsOrder::params{}  begin", hotelId);
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isEmpty(hotelId)) {
			throw MyErrorEnum.errorParm.getMyException("参数为空!");
		}
		// 加60秒锁
		Jedis jedis = this.cacheManager.getNewJedis();
		try {
			if (jedis.exists("synPmsOrder:" + hotelId)) {
				throw MyErrorEnum.errorParm.getMyException("反查进行中，请一分钟之后再执行反查操作!");
			}
			List<String> hotelIds = Arrays.asList(hotelId.split(","));
			for (String hid : hotelIds) {
				final Long hotelid = Long.parseLong(hid);
				PmsUtilController.pool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							PmsUtilController.this.pmsOrderService.synPmsOrder(hotelid, roomNos);
						} catch (Exception e) {
							PmsUtilController.logger.info("pmsutil::transPmsOrder:今日0点反查返回错误结果{}{}", hotelid, e.getMessage());
						}
					}
				});
			}
			jedis.set("synPmsOrder:" + hotelId, "1", "NX", "EX", 60);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
			result.put(ServiceOutput.STR_MSG_ERRMSG, "申请反查成功，反查任务执行中，请1分钟之后查看房态信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} catch (Exception e) {
			throw e;
		} finally {
			jedis.close();
		}
	}

	/**
	 * @param hotelId
	 *            多个用逗号分隔
	 * @param roomNos
	 *            多个用逗号分隔 全量更新，同步信息，处理应离未离问题 同步endtime是昨天之前的
	 */
	@RequestMapping(value = "/synPmsOrderBefore", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> synPmsOrderBefore(String hotelId, final String roomNos) {
		PmsUtilController.logger.info("pmsutil::synPmsOrder::params{}  begin", hotelId);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(hotelId)) {
				throw MyErrorEnum.errorParm.getMyException("参数为空!");
			}
			final Long hotelid = Long.parseLong(hotelId);
			PmsUtilController.pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String resultStr = PmsUtilController.this.pmsOrderService.synPmsOrderBefore(hotelid, roomNos);
						PmsUtilController.logger.info("pmsutil::transPmsOrder:昨天之前反查返回结果{}{}", hotelid, resultStr);
					} catch (Exception e) {
						PmsUtilController.logger.info("pmsutil::transPmsOrder:昨天之前反查返回错误结果{}{}", hotelid, e.getMessage());
					}
				}
			});
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
			result.put(ServiceOutput.STR_MSG_ERRMSG, "申请反查成功，反查任务执行中，请1分钟之后查看房态信息");
		} catch (Exception e) {
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			PmsUtilController.logger.info("pmsutil::transPmsOrder  end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/**
	 * @param hotelId
	 * @return 批量更新客单roomid和roomtypeid
	 */
	@RequestMapping(value = "/batchUpdateRoomInfos", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> batchUpdateCustomerNo(String hotelId) {
		PmsUtilController.logger.info("pmsutil::batchUpdateCustomerNo::params{}  begin", hotelId);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(hotelId)) {
				throw MyErrorEnum.errorParm.getMyException("参数为空!");
			}
			Long hotelid = Long.parseLong(hotelId);
			this.pmsOrderService.batchUpdateCustomerNo(hotelid);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			PmsUtilController.logger.info("pmsutil::batchUpdateCustomerNo  end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	// @RequestMapping(value="/testRoomCharge", method = RequestMethod.POST)
	// public ResponseEntity<Map<String, String>> testRoomCharge(){
	// List<PmsCost> ds = PmsCost.dao.find("SELECT \n" +
	// "    hotelid,\n" +
	// "    hotelpms,\n" +
	// "    roomcostno id,\n" +
	// "    costtime bizday,\n" +
	// "    costtype,\n" +
	// "    Source costsource,\n" +
	// "    roomcost,\n" +
	// "    Othercost price,\n" +
	// "    'zzytest' opuser,\n" +
	// "    customerno\n" +
	// "FROM\n" +
	// "    b_PmsCost\n" +
	// "WHERE\n" +
	// "    hotelid = 1449\n" +
	// "ORDER BY id DESC\n" +
	// "LIMIT 100");
	// Map param = new HashMap<>();
	// param.put("hotleid", 1449L);
	// List<Map> customerno = new ArrayList<Map>();
	// for (PmsCost pmsCost : ds) {
	// customerno.add(pmsCost.getAttrs());
	// }
	// param.put("customerno", customerno);
	// pmsOrderService.roomCharge(param);
	// Map<String, String> orderMap = new HashMap<>();
	// return new ResponseEntity<Map<String, String>>(orderMap,HttpStatus.OK);
	// }

	@RequestMapping(value = "/test12", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> test12() {
		
		String s = "[{\"leavetime\":\"20150709120000\",\"status\":\"IN\",\"totlepayment\":0,\"ordertype\":\"R\",\"type\":\"2\",\"totlecost\":0,\"arrivetime\":\"20150708180000\",\"checkintime\":\"20150708150244\",\"checkouttime\":\"\",\"roomno\":\"1Uiiz8cYRa1UyeXDDOMjmH\",\"roomtypeid\":\"11Vi7dd8mdbOU88hdbH01IPD\",\"day\":[{\"roomid\":\"1Uiiz8cYRa1UyeXDDOMjmH\",\"time\":\"20150708\",\"price\":120}],\"orderid\":\"0lTB4C8A0FbdGZs9gHZsGl7\",\"customeno\":\"0DOQFB9nyJbRGNFgD4MKEUX\",\"otacustomno\":\"\",\"user\":[{\"idtype\":\"13\",\"name\":\"老张\",\"idno\":\"222222222\"}]}]";
		JSONArray js = JSONArray.parseArray(s);
		JSONObject param = new JSONObject();
		param.put("hotelid", 1461L);
		param.put("customerno", js);
		
		newPmsOrderService.saveCustomerNo(param);

		Map<String, String> orderMap = new HashMap<>();
		return new ResponseEntity<Map<String, String>>(orderMap, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/loadallms", method = RequestMethod.POST)
	public ResponseEntity<ServiceOutput> getMonthlySales(String token) {
        ServiceOutput output = new ServiceOutput();
        if (StringUtils.isBlank(token) || !Constant.STR_INNER_TOKEN.equals(token)) {
            output.setFault("token is invalidate.");
            return new ResponseEntity<ServiceOutput>(output, HttpStatus.OK);
        }
		logger.info("LoadMonthlySalesJob::start");
		try {
			Long res = orderService.findMonthlySales(null);
			if(res <= 0)
				 output.setSuccess(true);
			else {
                output.setSuccess(false);
				output.setFault("findMonthlySales返回结果异常");
			}
		} catch (Exception e) {
			output.setFault(e.getMessage());
		}
		logger.info("LoadMonthlySalesJob::end");
		return new ResponseEntity<ServiceOutput>(output, HttpStatus.OK);
	}
	
	
}