package com.mk.ots.home.controller;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.base.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.service.HomeService;

@RestController
@RequestMapping("/home")
public class HomeController {

	@RequestMapping(value = "/genBillDetails")
	public ResponseEntity<String> genBillDetails(HttpServletRequest request) {
		JSONObject m = new JSONObject();
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		Object hotelId = request.getParameter("hotelId");
		endDate = endDate == null ? DateUtils.getDate() : endDate;
		if (beginDate == null) {
			m.put("err", "参数错误，beginDate为null");
		} else {

			m.put("start", "开始.计算生成数据到：t_bill_detail.<br>");
			int i = 1;
			HomeService homeService = AppUtils.getBean(HomeService.class);
			// 计算所有日期的跑批
			while (beginDate.compareToIgnoreCase(endDate) < 0) {
				homeService.genBillDetails((Long) hotelId, beginDate, DateUtils.getCertainDate(beginDate, 1));
				beginDate = DateUtils.getCertainDate(beginDate, 1);
				m.put("处理" + i, beginDate + "至" + endDate);
				if (endDate.equals("9999-01-01")) {
					break;
				}
				i++;
			}
		}

		return new ResponseEntity<String>(m.toJSONString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/genHomeDatas")
	public ResponseEntity<String> genHomeDatas(HttpServletRequest request) {
		JSONObject m = new JSONObject();
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		String hotelId = request.getParameter("hotelId");
		endDate = endDate == null ? "9999-01-01" : endDate;

		if (beginDate == null) {
			m.put("err", "参数错误，beginDate为null");
		} else {
			int i = 1;
			Long hotelId0 = Strings.isNullOrEmpty(hotelId) ? null : Long.parseLong(hotelId);
			HomeService homeService = AppUtils.getBean(HomeService.class);
			// 计算所有日期的跑批
			while (beginDate.compareToIgnoreCase(endDate) < 0) {
				homeService.genHomeDatas(hotelId0, beginDate, 0);
				m.put("处理" + i, beginDate + "至" + endDate);
				beginDate = DateUtils.getDateAdded(1, beginDate);
				if (endDate.equals("9999-01-01")) {
					break;
				}
				i++;
			}
		}

		return new ResponseEntity<String>(m.toJSONString(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/genCheckerBill")
	public ResponseEntity<String> genCheckerBill(HttpServletRequest request) {
		JSONObject m = new JSONObject();
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		Object hotelId = request.getParameter("hotelId");
		endDate = endDate == null ? "9999-01-01" : endDate;

		if (beginDate == null) {
			m.put("err", "参数错误，beginDate为null");
		} else {
			int i = 1;
			HomeService homeService = AppUtils.getBean(HomeService.class);
			homeService.genCheckerBill(beginDate, endDate);
		}
		m.put("message", "清空redis完成");
		return new ResponseEntity<String>(m.toJSONString(), HttpStatus.OK);
	}

}
