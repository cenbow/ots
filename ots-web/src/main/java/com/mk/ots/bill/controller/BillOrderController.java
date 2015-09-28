package com.mk.ots.bill.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.bill.dao.BillOrderDAO;
import com.mk.ots.bill.service.BillOrderService;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.util.HomeConst;
import com.mk.ots.order.controller.OrderController;

@RestController
@RequestMapping("/bill")
public class BillOrderController {
	
	private static final Logger logger = LoggerFactory.getLogger(BillOrderDAO.class);

	@Autowired
	BillOrderService billOrderService;
	
	/**
	 * 账单明细表-每天跑 凌晨2点1分
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/genBillOrders")
	public ResponseEntity<JSONObject> genBillDetails(HttpServletRequest request) {
		JSONObject m = new JSONObject();
		String orderDate = request.getParameter("orderDate");
		String hotelid = request.getParameter("hotelid");
		String orderidId = request.getParameter("orderid");
		if (orderDate.length() < 10) {
			throw MyErrorEnum.customError.getMyException("日期错误，只需要某月的某日就可以。实例：2015-05-10,2015-06-10");
		}
		Set<String> dates = new HashSet<>();
		if (orderDate.indexOf(",") > 0) {
			String[] strs = orderDate.split(",");
			for (String s : strs) {
				if (s.length() >= 10) {
					dates.add(s.substring(0, 10));
				}
			}
		}
		dates.add(orderDate);
		for (String date : dates) {
			Date beginTime = DateUtils.getDateFromString(date.replaceAll("-", "") + "000000");
			billOrderService.genBillOrders(beginTime, hotelid, orderidId);
		}
		m.put("sucess", true);
		return new ResponseEntity<JSONObject>(m, HttpStatus.OK);
	}

	/**
	 * 账单汇总 月表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/genBillConfirmChecks")
	public ResponseEntity<JSONObject> genBillConfirmChecks(HttpServletRequest request) {
		
		// 加redis锁
		String hotelid = request.getParameter("hotelid");
		String isThreshold = request.getParameter("isThreshold");
		String lockValue = null;
		if(StringUtils.isNotBlank(hotelid)){
			lockValue = DistributedLockUtil.tryLock("orderBillLock_" + hotelid, 40);
			if (lockValue == null) {
				logger.info("酒店：" + hotelid + "正在执行结算任务，无法进行二次结算");
				throw MyErrorEnum.customError.getMyException("酒店：" + hotelid + "正在执行结算任务，无法进行二次结算");
			}
		}
		JSONObject m = new JSONObject();
		String theMonthDay = request.getParameter("theMonthDay");
		try {
			if(theMonthDay != null){
				Date begintime = DateUtils.getDateFromString(theMonthDay);
				billOrderService.genBillConfirmChecks(begintime, hotelid, isThreshold);
			}
		} catch (Exception e) {
			throw e;
		} finally{
			// 释放 redis锁
			logger.info("释放分布锁orderBillLock_, hotelid= " + hotelid);
			DistributedLockUtil.releaseLock("orderBillLock_" + hotelid, lockValue);
		}
		
		m.put("sucess", true);
		return new ResponseEntity<JSONObject>(m, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/test")
	public ResponseEntity<String> test(HttpServletRequest request) {
		JSONObject m = new JSONObject();
		String theMonth = DateUtils.getYearMonth(-1);
		BillOrderDAO billOrderDAO = AppUtils.getBean(BillOrderDAO.class);
		String beginTime = DateUtils.getDateAdded(-1, DateUtils.getDate());
		Set<String> times = new HashSet<>();
		times.add(beginTime);
		Map his = billOrderDAO.getJobHistory(HomeConst.BILL_ORDERS_JOB_HIS);
		if (his != null && his.containsKey("job")) {
			// 2015-04-01 12:21:22
			String lastRunTime = (String) his.get("last_run_time");
			while (lastRunTime.compareTo(beginTime) < 0) {
				times.add(lastRunTime.substring(0, 10));
				lastRunTime = DateUtils.getDateAdded(1, lastRunTime);
			}
		}
		for (String time : times) {
			//billOrderDAO.genBillOrders(DateUtils.getDateFromString(time));
		}
		m.put("sucess", true);
		return new ResponseEntity<String>(m.toJSONString(), HttpStatus.OK);
	}
	
}
