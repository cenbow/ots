package com.mk.ots.inner.controller;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.google.common.base.Optional;
import com.mk.framework.AppUtils;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.ots.bill.dao.BillOrderDAO;
import com.mk.ots.bill.service.BillOrderService;
import com.mk.ots.common.enums.HotelSearchEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.OtsVersion;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.inner.service.IOtsAdminService;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.impl.MemberService;
import com.mk.ots.order.service.QiekeRuleService;
import com.mk.ots.search.service.impl.IndexerService;
import com.mk.ots.wallet.model.CashflowTypeEnum;
import com.mk.ots.wallet.service.impl.WalletCashflowService;
import com.mk.ots.wallet.service.impl.WalletService;
import com.mk.ots.web.ServiceOutput;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * OTS Administrator.
 * 
 * @author chuaiqing.
 *
 */
@Controller
@RequestMapping(value = "/admin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class OtsAdminController {

	final Logger logger = LoggerFactory.getLogger(OtsAdminController.class);

	@Autowired
	private IOtsAdminService otsAdminService;

	@Autowired
	private HotelService hotelService;

	@Autowired
	private BillOrderService billService;

	@Autowired
	private IndexerService indexerService;
	@Autowired
	private WalletCashflowService walletCashflowService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private MemberService memberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> ping() {
		return new ResponseEntity<Map<String, String>>(new OtsVersion().getVersionInfo(), HttpStatus.OK);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping("/indexer/init")
	@ResponseBody
	public ResponseEntity<ServiceOutput> indexerInit(String token) {
		ServiceOutput output = new ServiceOutput();
		if (StringUtils.isBlank(token) || !Constant.STR_INNER_TOKEN.equals(token)) {
			output.setFault("token is invalidate.");
			return new ResponseEntity<ServiceOutput>(output, HttpStatus.OK);
		}

		Date day = new Date();
		long starttime = day.getTime();
		try {

			String ret = indexerService.batchUpdateEsIndexer();
			output.setSuccess(true);
		} catch (Exception e) {
			output.setFault(e.getMessage());
		}
		if (AppUtils.DEBUG_MODE) {
			long endtime = new Date().getTime();
			output.setMsgAttr("$times$", endtime - starttime + " ms");
		}
		return new ResponseEntity<ServiceOutput>(output, HttpStatus.OK);
	}


	/**
	 * 
	 * @param citycode
	 * @param typeid
	 * @return
	 */
	@RequestMapping(value = "/imp", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> impCityPoiDatas(String citycode, Integer typeid, String token) {
		Map<String, Object> datas = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "token参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (StringUtils.isBlank(citycode)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "citycode参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (typeid == null) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "typeid参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (typeid != HotelSearchEnum.SAREA.getId() && typeid != HotelSearchEnum.SUBWAY.getId()) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "不支持的类型id: " + typeid);
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		try {
			datas = otsAdminService.readonlyImportPoiDatas(citycode, typeid);
			datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
		}
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	/**
	 * 
	 * @param citycode
	 * @param typeid
	 * @return
	 */
	@RequestMapping(value = "/position/delete", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> deleteCityPoiDatas(String citycode, Integer typeid, String token) {
		Map<String, Object> datas = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "token参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (StringUtils.isBlank(citycode)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "citycode参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (typeid == null) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "typeid参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (typeid != HotelSearchEnum.SAREA.getId() && typeid != HotelSearchEnum.SUBWAY.getId()) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "不支持的类型id: " + typeid);
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		try {
			datas = otsAdminService.readonlyDeletePoiDatas(citycode, typeid, ElasticsearchProxy.OTS_INDEX_DEFAULT, ElasticsearchProxy.POSITION_TYPE_DEFAULT);
			otsAdminService.readonlyDeletePoiDatas(citycode, typeid, ElasticsearchProxy.OTS_INDEX_LANDMARK, ElasticsearchProxy.POSITION_TYPE_DEFAULT);
		} catch (Exception e) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
		}
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	/**
	 * 
	 * @param citycode
	 * @param typeid
	 * @return
	 */
	@RequestMapping(value = "/batchupdate/hotelbedtype", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> batchupdateHotelbedtype(String citycode, String token) {
		Map<String, Object> datas = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "token参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		datas.putAll(hotelService.readonlyBatchUpdateHotelBedtype(citycode, token));
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	/**
	 * 
	 * @param citycode
	 * @param typeid
	 * @return
	 */
	@RequestMapping(value = "/batchupdate/hoteltype", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> batchupdateHoteltype(String citycode, String token) {
		Map<String, Object> datas = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "token参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		datas.putAll(hotelService.readonlyBatchUpdateHoteltype(citycode, token));
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	/**
	 * 特价房间跑财务对账数据
	 * @param startdateday
	 * @param enddateday
	 * @return
	 */
	@RequestMapping(value = "/report/launchPromo", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> launchBillRPPromotions(String startdateday, String enddateday) {
		logger.info(String.format("url /report/launchPromo params startdateday[%s],enddateday[%s]", startdateday, enddateday));
		Map<String, Object> datas = new HashMap<String, Object>();
		try {
			Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMATSHORTDATETIME);
			Date endTime = DateUtils.getDateFromString(enddateday, DateUtils.FORMATSHORTDATETIME);
			billService.createBillReport(beginTime, endTime);
		} catch (Exception ex) {
			logger.error("failed to createBillReportByHotelId", ex);
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "failed to createBillReportByHotelId");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/report/genBillOrders", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> genBillOrders(String startdateday) {
		logger.info(String.format("url /report/genBillOrders params startdateday[%s]", startdateday));
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		// 获取上个月
		BillOrderDAO billOrderDAO = AppUtils.getBean(BillOrderDAO.class);
		Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMATSHORTDATETIME);
		Cat.logEvent("OtsJob", "BillOrdersJob.executeInternal", Event.SUCCESS, "");
		logger.info("BillOrdersJob::genBillOrders::{}", beginTime);
		billOrderDAO.genBillOrders(beginTime, null, null);
		logger.info("BillOrdersJob::genBillOrders::end");
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}


	@RequestMapping(value = "/report/runtWeekClearing", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> runtWeekClearing(String startdateday) {
		logger.info(String.format("url /report/runtWeekClearing params startdateday[%s]", startdateday));
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		logger.info("周结算开始");
		Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMATSHORTDATETIME);
		BillOrderService billOrderDAO = AppUtils.getBean(BillOrderService.class);
		billOrderDAO.runtWeekClearing(beginTime, null);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/report/updateTopInvalidReason", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> updateTopInvalidReason(String startdateday) {
		logger.info(String.format("url /report/updateTopInvalidReason"));
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		QiekeRuleService qiekeRuleService = AppUtils.getBean(QiekeRuleService.class);
		Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMATSHORTDATETIME);
		qiekeRuleService.updateTopInvalidReason(beginTime);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/test/addWalletByTest", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> addWalletByTest(String phone, BigDecimal cash) {
		logger.info(String.format("url /test/addWalletByTest"));
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, "充值成功");
		if(StringUtils.isBlank(phone)){
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "phone参数为空");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if(cash == null){
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "cash参数为空");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if(cash.compareTo(new BigDecimal("10")) > 0){
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "冲错金额啦，充值金额请少于10元");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if(StringUtils.isBlank(phone) || !phone.startsWith("1000")){
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "冲错用户信息啦，手机号码不是测试用户");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		Optional<UMember> opMember = null;
		try {
			opMember = memberService.findMemberByLoginName(phone);
		}catch (Throwable e){
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "phone参数错误，没有用户信息");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		UMember member = opMember.get();
		if (member == null) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "phone参数错误，没有用户信息");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		try {
			BigDecimal balance = walletService.queryBalance(member.getMid());
			if(balance.compareTo(new BigDecimal("10")) > 0){
				datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
				datas.put(ServiceOutput.STR_MSG_ERRMSG, "所充值的用户的钱包金额> 10,已够用了");
				return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
			}
			walletCashflowService.saveCashflowAndSynWallet(member.getId(), cash, CashflowTypeEnum.MIKE_CHARGE_CARD, member.getId());
		}catch (Exception e){
			logger.info("addWalletByTest Exception", e);
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "addWalletByTest Exception");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		logger.info(String.format("url /test/addWalletByTest end"));
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}



}
