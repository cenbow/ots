package com.mk.ots.test.controller;

import com.google.common.collect.Maps;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.ots.bill.service.BillOrderService;
import com.mk.ots.job.PushMessageJob;
import com.mk.ots.job.SendCouponsJob;
import com.mk.ots.job.SendLiveThreeCouponsJob;
import com.mk.ots.job.SendTicketJob;
import com.mk.ots.message.model.LPushLog;
import com.mk.ots.message.model.MessageType;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.pay.model.CouponParam;
import com.mk.ots.rpc.IPmsSoapService;
import com.mk.ots.test.service.TestService;
import com.mk.ots.ticket.service.ITicketService;
import org.apache.commons.dbcp.BasicDataSource;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/test", method = RequestMethod.POST)
public class TestController {
	final Logger logger = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private IMessageService iMessageService;

	@Autowired
	private TestService testService = null;

	@Resource
	private BasicDataSource masterDataSource;

	@Resource
	private BasicDataSource slaveDataSource;

    @Autowired
    private IPmsSoapService pmsSoapService;
    @Autowired
	private ITicketService iTicketService;
	@Autowired
	private BillOrderService billOrderService;

    // @Autowired
    // private KafkaProducer kafkaProducer = null;

	/**
	 * 意见反馈
	 *
	 * @param token
	 *            ：用户
	 * @param suggest
	 *            ：意见
	 * @return jeson
	 */
	@RequestMapping(value = "/pushjob")
	public ResponseEntity<Map<String, Object>> pushjob(HttpServletRequest request) {
		PushMessageJob job = new PushMessageJob();
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			job.testjob();
		} catch (Exception e) {
			this.logger.error("message job出错了", e);
			rtnMap.put("success", false);
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 意见反馈
	 *
	 * @param token
	 *            ：用户
	 * @param suggest
	 *            ：意见
	 * @return jeson
	 */
	@RequestMapping(value = "/sendcoupons")
	public ResponseEntity<Map<String, Object>> sendcoupons(HttpServletRequest request) {
		SendCouponsJob job = new SendCouponsJob();
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			job.testjob();
		} catch (Exception e) {
			this.logger.error("message job出错了", e);
			rtnMap.put("success", false);
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/sendLiveThreeCouponsJob")
	public ResponseEntity<Map<String, Object>> SendLiveThreeCoupons(HttpServletRequest request) {
		SendLiveThreeCouponsJob job = new SendLiveThreeCouponsJob();
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			job.testjob();
		} catch (Exception e) {
			this.logger.error("message job出错了", e);
			rtnMap.put("success", false);
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/SendTicketJob")
	public ResponseEntity<Map<String, Object>> SendTicketJob(HttpServletRequest request) {
		SendTicketJob job = new SendTicketJob();
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			job.testjob();
		} catch (Exception e) {
			this.logger.error("message job出错了", e);
			rtnMap.put("success", false);
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/sendmessage")
	public ResponseEntity<Map<String, Object>> sendmessage(HttpServletRequest request, String mid, String promotionid, String phone) {
		SendCouponsJob job = new SendCouponsJob();
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			/**
			 * 标题：领取30元红包 内容：感谢您入住眯客酒店，送你30元红包，赶快领取！
			 * URL：http://weixin.imike.com/
			 * event/getvalidticket?token=**&phone=**&systype=1 systype： 1 -
			 * android 2 - IOS TODO:systype能否反过来， 参数promotionid要加？？？？
			 */
			String title = "领取30元红包 ";
			String msgContent = "感谢您入住眯客酒店，送你30元红包，赶快领取！";
			String url = "www.imikeshareMessage-url-scheme://inneractivityurl/" + "http://weixin.imike.com/event/getvalidticket" + "?token=" + request.getParameter("token") + "&phone=" + phone
					+ "&promotionid=" + promotionid + "&systype=" + 1;
			LPushLog lPushLog = new LPushLog();
			lPushLog.setMid(Long.valueOf(mid));
			lPushLog.setActiveid(15L);

			// push message
			this.iMessageService.pushMsg(phone, title, msgContent, MessageType.USER.getId(), url, 15L);
		} catch (Exception e) {
			this.logger.error("message job出错了", e);
			rtnMap.put("success", false);
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@Autowired
	private ElasticsearchProxy proxy = null;

	@RequestMapping("/test")
	@ResponseBody
	public String test() {
		List<GeoPoint> gpList = new ArrayList<GeoPoint>();
		GeoPoint gp1 = new GeoPoint(31, 121);
		gpList.add(gp1);
		GeoPoint gp2 = new GeoPoint(31, 122);
		gpList.add(gp2);
		GeoPoint gp3 = new GeoPoint(32, 121);
		gpList.add(gp3);

		GeoPoint myLoc = new GeoPoint(31.229538, 121.395839);

		List<String> mkPriceDateList = new ArrayList<String>();
		mkPriceDateList.add("$mike_price_20150901");
		mkPriceDateList.add("$mike_price_20150902");
		mkPriceDateList.add("$mike_price_20150903");

		SearchHit[] hits = this.proxy.searchHotelByBsiCycleWithNoScore("ots", "hotel", gpList, myLoc, mkPriceDateList);
		for (SearchHit hit : hits) {
			System.out.println(hit.getSource().get("hotelid"));
		}

		return "success";
	}

	@RequestMapping("/showdb")
	public ResponseEntity<Map<String, Object>> showdb() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("active_number", this.masterDataSource.getNumActive());
		map.put("idle_number", this.masterDataSource.getNumIdle());

		map.put("slave_active_number", this.slaveDataSource.getNumActive());
		map.put("slave_idle_number", this.slaveDataSource.getNumIdle());
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

    @RequestMapping("/testoffline")
    public ResponseEntity<Map<String, Object>> testoffline() {
        Map<String, Object> map = new HashMap<String, Object>();
        pmsSoapService.pmsHotelOffline("1430");
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }
    @RequestMapping("/queryCouponParam")
    public ResponseEntity<Map<String, Object>> queryCouponParam(OtaOrder order) {
    	CouponParam couponParam  = iTicketService.queryCouponParam(order);
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("couponParam", couponParam);
    	return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }


    public TestService getTestService() {
        return this.testService;
	}

}
