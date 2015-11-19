package com.mk.ots.pay.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.http.HTTPException;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import jodd.util.StringUtil;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.elasticsearch.common.base.Strings;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.XMLUtils;
import com.mk.ots.common.enums.ManualLuzhuRstEnum;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PayCallbackEnum;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.common.utils.SysConfig;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderBusinessLogService;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.pay.module.weixin.WeChat;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.pay.service.IPayService;
import com.mk.ots.pay.service.IPriceService;
import com.mk.ots.utils.PayLockKeyUtil;



@RestController
@RequestMapping("/pay")
//@Controller
//@RequestMapping(value = "/pay", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class PayController {
 
	private Logger logger = LoggerFactory.getLogger(PayController.class);

	@Autowired
	private IMemberService memberService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private IPayService payService;
	@Autowired
	private IPriceService priceService;
	@Autowired
	private HotelService hotelService;
	@Autowired
	private OrderBusinessLogService orderBusinessLogService;
	
	
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public  ResponseEntity<Map<String, Object>>   test1(HttpServletRequest request) {
//		String otherno="1004640043201505110123070779";
		Map<String, Object>  map=new HashMap<String, Object>();
//		PPayInfo pi=payService.getPayOk(otherno);
//		BigDecimal  bg=payService.getRuleValue(RuleEnum.CHONG_QIN);
//		System.out.println("bg1==="+bg);
//		BigDecimal  bg1=payService.getRuleValue(RuleEnum.SHANG_HAI);
//		System.out.println("bg1==="+bg1);
//		System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL==========="+pi.toString());
		return new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
	}
	
	// 支付宝退款回调地址 ***不用本地系统调用***【支付宝专用】
	@RequestMapping(value = "/alicancel", method = RequestMethod.POST)
	public String alicancel(HttpServletRequest request) {
		String result_details = request.getParameter("result_details");
		this.logger.info("支付宝退款回调： " + result_details);
		if (this.payService.alipayCancelRes(result_details)) {
			Cat.logEvent("pay", "支付宝退款回调", Event.SUCCESS, result_details);
			this.logger.info("支付宝退款回调处理成功. ");
			return "success";
		} else {
			Cat.logEvent("pay", "支付宝退款回调", "Error", result_details);
			this.logger.info("支付宝退款回调处理失败. ");
			return "";
		}
	}

	// 支付宝回调地址 ***不用本地系统调用***【支付宝专用】
	@RequestMapping(value = "/alires", method = RequestMethod.POST)
	// 成功直接返回 success
	public String alires(HttpServletRequest request) {
		String res = "";
		String orderid = request.getParameter("out_trade_no");
		this.logger.info("支付宝回调，订单号：" + orderid);

		String lockValue = DistributedLockUtil.tryLock(PayLockKeyUtil.genLockKey4PayCallBack(orderid), 40);
		if (StringUtils.isNotEmpty(lockValue)) {
			logger.info("订单：" + orderid + "获取分布锁成功,继续执行回调流程.");
		} else {
			logger.info("订单：" + orderid + "获取分布锁失败,返回.");
			return null;
		}
		try {
			this.logger.info("支付宝回调，订单号：" + orderid);
			String trade_status = request.getParameter("trade_status");
			String payid = request.getParameter("trade_no");
			String price = request.getParameter("price");
			// 支付成功 //WAIT_BUYER_PAY等待付款， TRADE_CLOSED 未付款交易关闭，
			// TRADE_FINISHED交易成功，不能改变的
			if (trade_status.equals("TRADE_SUCCESS")) {
				this.logger.info("支付宝回调，订单号：" + orderid + "   trade_status=" + trade_status);
				// 不能多次请求改状态
				Boolean boo = this.payService.payIsWaitPay(orderid);
				if (boo == null) {
					return null;
				} else if (boo) {

					long orderIdNum = getLongOrderId(orderid);
					
					this.logger.info("支付宝回调，订单号：" + orderid + "  支付宝支付成功，第一次回调");
					
					String userId = request.getParameter("buyer_id");
					logger.info("订单:" +orderid+ "用户标识为:" + userId);
					Boolean rb = this.payService.payresponse(orderIdNum, payid, price, PPayInfoOtherTypeEnum.alipay, userId);
					if (rb != null && rb) {
						// 处理成功后给支付宝返回 success将不会再请求
						res = "success";
					}
					Cat.logEvent("pay", "支付宝回调", Event.SUCCESS, orderid);
					this.logger.info("支付宝回调，订单号：" + orderid + "  支付宝支付成功，第一次回调结束");
				} else {
					res = "success";
				}

				payService.insertPayCallbackLog(Long.parseLong(orderid), PayCallbackEnum.Ali_Callback.name(), "Y", payid,
						new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP), null, null);
				
			} else {
				this.logger.info("支付宝回调，订单号：" + orderid + "  支付宝支付失败，状态是：" + trade_status);
				Cat.logEvent("pay", "支付宝回调", "ERROR", orderid);
				payService.insertPayCallbackLog(Long.parseLong(orderid), PayCallbackEnum.Ali_Callback.name(), "N", payid,
						new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP), trade_status, null);
			}
		} finally {

			logger.info("订单：" + orderid + "回调流程完毕,释放分布锁.");

			DistributedLockUtil.releaseLock(PayLockKeyUtil.genLockKey4PayCallBack(orderid), lockValue);
		}
		return res;
	}

	// 微信支付专用【App】
	@RequestMapping(value = "/wxres", method = RequestMethod.POST)
	// http://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=9_7&index=3
	public String wxres(HttpServletRequest request) {
		Element elroot = null;
		String res = null;
		try {
			String xml = XMLUtils.readXMLStringFromRequestBody(request);
			this.logger.info("微信回调：" + xml);
			Document doc = XMLUtils.StringtoXML(xml);
			elroot = doc.getRootElement();
			this.logger.info("请求微信返回的数据是:" + doc);
		} catch (JDOMException | IOException e1) {
			this.logger.error(e1.getMessage(), e1);
		}
		String return_code = elroot.getChildText("return_code");
		String return_msg = elroot.getChildText("return_msg");
		String orderid = elroot.getChildText("out_trade_no");
		String result_code = elroot.getChildText("result_code");
		String payid = elroot.getChildText("transaction_id");
		String total_fee = elroot.getChildText("total_fee");
		String userId = elroot.getChildText("openid");

		this.logger.info("return_code={},return_msg={},orderid={},result_code={},payid={},total_fee={},userId={}", 
				return_code, return_msg, orderid,result_code, payid, total_fee, userId);
		
		String lockValue = DistributedLockUtil.tryLock(PayLockKeyUtil.genLockKey4PayCallBack(orderid), 40);
		if(StringUtils.isNotEmpty(lockValue)) {
			logger.info("订单：" + orderid +"获取分布锁成功,继续执行回调流程.");
		}else{
			logger.info("订单：" + orderid +"获取分布锁失败,返回.");
			return null;
		}
		
		try {
			// 微信返回的价格是以 【分为单位】
			BigDecimal b = new BigDecimal(total_fee);
			total_fee = b.divide(new BigDecimal("100")).doubleValue() + "";
		} catch (Exception e) {
			throw MyErrorEnum.errorParm.getMyException("获取支付金额失败");
		}
		
		BigDecimal payPrice = new BigDecimal(total_fee).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		try {

		if (return_code.equalsIgnoreCase("SUCCESS")) {
			if (result_code.equalsIgnoreCase("SUCCESS")) {
				// 去支付的out_trade_no 是orderid经过处理的
				Boolean boo=this.payService.payIsWaitPay(orderid);
				if(boo==null){
					return null;
				}else if (boo)  {
					Long orderIdNum=getLongOrderId(orderid);
					
					Boolean  rb=this.payService.payresponse(orderIdNum, payid, total_fee, PPayInfoOtherTypeEnum.wxpay,
							userId);
					if (rb!=null && rb ) {
						res = "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
//						res = "SUCCESS";
					}
				}else{
						// 返回此信息，后面就不会再有此订单的回调
//					 res = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "</xml>";
					 res = "success";
				}
				
				payService.insertPayCallbackLog(Long.parseLong(orderid), PayCallbackEnum.WeChat_Callback.name(), "Y", 
						payid, payPrice, null, null);
				Cat.logEvent("pay", "微信回调", Event.SUCCESS, orderid);
			} else {
				
				String errCode = elroot.getChildText("err_code");
				String errMsg = elroot.getChildText("err_code_des");
				logger.info("订单:" + orderid + "微信回调,支付错误,错误信息码:" + errCode);
				
				payService.insertPayCallbackLog(Long.parseLong(orderid), PayCallbackEnum.WeChat_Callback.name(), "N", 
						payid, payPrice, errCode, errMsg);
				Cat.logEvent("pay", "微信回调", "ERROR", orderid);
			}
		} else {
			this.logger.info("订单:" + orderid + "微信回调,支付错误，错误信息码：" + return_msg);
			
			payService.insertPayCallbackLog(Long.parseLong(orderid), PayCallbackEnum.WeChat_Callback.name(), "N", 
					payid, payPrice, return_code, return_msg);
			Cat.logEvent("pay", "微信回调", "ERROR", orderid);
		}

		this.logger.info("订单:" + orderid + "微信回调结束");

		} finally {
        	logger.info("订单：" + orderid +"回调流程完毕,释放分布锁.");
			DistributedLockUtil.releaseLock(PayLockKeyUtil.genLockKey4PayCallBack(orderid), lockValue);
        }

		return res;
	}

	/**
	 * 此方法只是查询本地有没接收到支付宝或者微信返回信息的情况， 如果没有修改本地状态，微信需要去银行查询
	 * @param orderid 订单号，必填
	 * @param onlinepaytype 预付支付类型，必填 1 微信 2 支付宝 3 微信公共账号
	 * @param payno 第三方交易号，非必填（若预付类型是微信，则必填）
	 * @param paytime 支付时间 非必填（若预付类型是微信，则必填）待确认
	 * @param price 支付的价格 非必填（若预付类型是微信，则必填）待确认
	 * @param prepayid 微信去支付时候定的返回的字段
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> checkPay(String orderid, String onlinepaytype, String payno, String paytime, String price) {
		
		logger.info("Check回调,订单:" + orderid + ",onlinepaytype:" + onlinepaytype + ",payno:" + payno + ",paytime:" + paytime + ",price:" + price);
		
		if (Strings.isNullOrEmpty(orderid)) { // 订单号 必填
			this.logger.error("Check回调，订单号orderid为空：");
			throw MyErrorEnum.errorParm.getMyException("[订单号] 不允许为空.");
		}
		
		OtaOrder order = orderService.findOtaOrderById(Long.valueOf(orderid));
		
		if(order == null) {
			
			this.logger.error("订单:" + orderid + "不存在.");
			throw MyErrorEnum.findOrder.getMyException("订单不存在.");
		}
			
		orderService.modifyOrderStsAndPayStsOnCheck(order);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("success", true);
		ResponseEntity<Map<String, Object>> response = new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
		
		BigDecimal payPrice = null;
		if(StringUtil.isNotEmpty(price)) {
			
			try {
				payPrice = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
			} catch (Exception e) {
				
				logger.error("订单:" + orderid + "传入的price不能转换为BigDecimal", e);

			}
		}
		
		if (Strings.isNullOrEmpty(onlinepaytype)) { // 预付支付类型 必填, 微信 支付宝 网银。
			this.logger.error("Check回调， [预付支付类型] 为空：");
			
			payService.insertPayCallbackLog(Long.parseLong(orderid), null, "Y", payno, payPrice, null, "预付支付类型为空");
			
			return response;
		}
		
		PayCallbackEnum callbackEnum = PayCallbackEnum.getById(Integer.parseInt(onlinepaytype));
		
		if(callbackEnum == null) {
			
			this.logger.error("订单:" + orderid + "Check回调,支付类型错误:" + onlinepaytype);
			
			payService.insertPayCallbackLog(Long.parseLong(orderid), null, "Y", payno, payPrice, null, "预付支付类型错误");
			
			return response;
		}
		
		logger.info("Check回调,订单:" + orderid + "回调方是:" + callbackEnum.getDesc());
		
		payService.insertPayCallbackLog(Long.parseLong(orderid), callbackEnum.name(), "Y", payno, payPrice, null, null);
		
		return response;

	}

	/**
	 * 微信支付成功后的调用的接口，用于修改本地状态等操作
	 *
	 * @param orderid 订单号，必填
	 * @param payno 第三方交易号，非必填（若预付类型是微信，则必填）
	 * @param price 支付的价格 非必填（若预付类型是微信，则必填）待确认
	 * @return
	 */
	@RequestMapping(value = "/weixin", method = RequestMethod.POST)
	// 微信公共帐号专用支付成功回调地址
	public ResponseEntity<Map<String, Object>> wxcheck(HttpServletRequest request, String orderid, String payno, 
			String price, String openid) {
		this.logger.info("微信公共账号付款完成后回调,订单号:" + orderid + ",payno:" + payno 
				+ ",price:" + price + ",openid:" + openid);
		Map<String, Object> map = new HashMap<String, Object>();
		if (Strings.isNullOrEmpty(orderid)) { // 订单号 必填
			this.logger.error("支付完成后，回调订单号orderid为空：");
			throw MyErrorEnum.errorParm.getMyException("[订单号] 不允许为空.");
		}

		String lockValue = DistributedLockUtil.tryLock(PayLockKeyUtil.genLockKey4PayCallBack(orderid), 40);
		if (StringUtils.isNotEmpty(lockValue)) {
			logger.info("订单：" + orderid + "获取分布锁成功,继续执行回调流程.");
		} else {
			logger.info("订单：" + orderid + "获取分布锁失败,返回.");
			return null;
		}

		try {
			Boolean boo = this.payService.payIsWaitPay(orderid);
			this.logger.info("付款完成后回调，先去本地查询，orderid=" + orderid + "   boolean=====" + boo);
			if (boo == null) {
				return null;
			} else if (boo) {
				this.logger.info("付款完成后回调，第一次接收到信息，orderid=" + orderid);
				if (!Strings.isNullOrEmpty(request.getParameter("istest"))) {
					this.logger.info("付款完成后回调，第一次接收到信息，【测试公众帐号】orderid=" + orderid + "  payno=" + payno);
				}

				// 去银行判断
				String payid = WeChat.query(payno, PayTools.get100price(price));
				if (payid != null) {
					this.logger.info("微信订单" + orderid + "支付成功！支付金额为：" + price);
					long orderIdNum = -1l;
					try {
						orderIdNum = Long.parseLong(orderid);
						// 微信返回的价格是以 【分为单位】
					} catch (NumberFormatException e1) {
						throw MyErrorEnum.errorParm.getMyException("订单号只能是数字");
					}
					// 支付创建pms订单
					this.logger.info("支付完成，开始创建pms订单！orderid::" + orderid);
					Boolean rb = this.payService.payresponse(orderIdNum, payid, price, 
							PPayInfoOtherTypeEnum.wechatpay, openid);
					if (rb == null) {
						map.put("success", false);
						map.put("errcode", MyErrorEnum.OrderCancelBySystem.getErrorCode());
						map.put("errmsg", MyErrorEnum.OrderCancelBySystem.getErrorMsg());
					} else if (rb) {
						map.put("success", true);
					} else {
						map.put("success", false);
						map.put("errcode", MyErrorEnum.requestWXError.getErrorCode());
						map.put("errmsg", MyErrorEnum.requestWXError.getErrorMsg());
					}

					payService.insertPayCallbackLog(Long.parseLong(orderid), PayCallbackEnum.WeChat_Platform_Callback.name(), "Y", payno,
							new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP), null, null);
				} else {

					this.logger.info("微信公共账号订单:" + orderid + "去银行查询没有找到支付信息");
					map.put("success", false);
					map.put("errcode", MyErrorEnum.requestWXNoPay.getErrorCode());
					map.put("errmsg", MyErrorEnum.requestWXNoPay.getErrorMsg());

					payService.insertPayCallbackLog(Long.parseLong(orderid), PayCallbackEnum.WeChat_Platform_Callback.name(), "N", payno,
							new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP), null, "去银行查询没有找到支付信息");
					Cat.logEvent("pay", "微信公共账号付款完成后回调", "ERROR", orderid);

				}
			} else {
				this.logger.info("微信公共账号订单已经是支付状态");
				map.put("success", true);

				payService.insertPayCallbackLog(Long.parseLong(orderid), PayCallbackEnum.WeChat_Platform_Callback.name(), "Y", payno, new BigDecimal(
						price).setScale(2, BigDecimal.ROUND_HALF_UP), null, null);
			}
			this.logger.info("微信公共账号付款完成后回调,处理完成,订单:" + orderid);
			Cat.logEvent("pay", "微信公共账号付款完成后回调", Event.SUCCESS, orderid);
		} finally {
			logger.info("订单：" + orderid + "回调流程完毕,释放分布锁.");
			DistributedLockUtil.releaseLock(PayLockKeyUtil.genLockKey4PayCallBack(orderid), lockValue);
		}
		return new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
	}


	/** 得到Long类型orderId */
	private Long getLongOrderId(String orderid) {
		try {
			return Long.parseLong(orderid);
		} catch (NumberFormatException e1) {
			throw MyErrorEnum.errorParm.getMyException("订单号只能是数字");
		}
	}

	/**
	 * @author xiaofutao 在OTA系统中为一个已经创建的订单提交支付
	 * @param orderid订单id, 必填
	 * @param promotionno促销代码,非必填，若多个，则用“，”分割
	 * @param couponno优惠券代码, 非必填，若多个，则用“，”分割
	 * @param paytype必填 1、预付 2、到付
	 * @param onlinepaytype 预付支付类型 非必填(若预付，则必填) 1、微信 2、支付宝 3、网银 4、其他
	 * @return json data
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createPay(HttpServletRequest request, String orderid, String promotionno, String couponno, String paytype, String onlinepaytype) {
		this.outputInputParamLog(orderid, promotionno, couponno, paytype, onlinepaytype);
		
		logger.info("订单：" + orderid +"进入支付流程,尝试获取分布锁...");
		String lockValue = DistributedLockUtil.tryLock(PayLockKeyUtil.genLockKey4Pay(orderid), 40);
		
		if(StringUtils.isNotEmpty(lockValue)) {
			
			logger.info("订单：" + orderid +"获取分布锁成功,继续执行支付流程.");
		} else {
			
			logger.info("订单：" + orderid +"获取分布锁失败,中断执行支付流程.");
			
			Map<String, Object> resMap = new HashMap<String, Object>();
			
			resMap.put("success", false);
			resMap.put("errorcode", MyErrorEnum.exclusiveRequest.getErrorCode());
			resMap.put("errormsg", MyErrorEnum.exclusiveRequest.getErrorMsg());
			
			return new ResponseEntity<Map<String, Object>>(resMap, org.springframework.http.HttpStatus.OK);
		}
		
		long longorderId = this.getLongOrderId(orderid);
		Map<String, Object> map;
		Transaction t = Cat.newTransaction("URL", "/ots/pay/create");
		try {
			map = payService.createPay(request, longorderId, promotionno, couponno, paytype, onlinepaytype);
			try {
				if (paytype.trim().equals("2")) {
					payService.pushMsg(longorderId, "2");
				} else if (paytype.trim().equals("1")) {
					if (map != null) {

						BigDecimal cost = (BigDecimal) map.get("onlinepay");

						if (cost != null && cost.compareTo(BigDecimal.ZERO) == 0) {
							payService.pushMsg(longorderId, "1");
						}
					}
				} 
			} catch (Throwable e) {
				logger.error("订单:" + longorderId +"pushMsg异常!", e);
			}
			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			orderService.changeOrderStatusByPay(longorderId, OtaOrderStatusEnum.WaitPay, PayStatusEnum.waitPay, OrderTypeEnum.YF);
			logger.info("订单号：" + longorderId +"回滚，将其置为初始状态.异常:" + e.getMessage());
			Cat.logEvent("/pay/create", orderid, "Error", "订单:" + orderid + ",促销代码" + promotionno + ",优惠券" + couponno + ",付款类型(1:预付，2:到付):" + paytype + ",在线支付类型(1:微信,2:支付宝,3:网银,4:其它):" + onlinepaytype);
			Cat.logError("/pay/create error", e);
			t.setStatus(e);
			throw e;
		} finally {
			logger.info("订单：" + orderid +"支付流程完毕,释放分布锁.");
			DistributedLockUtil.releaseLock(PayLockKeyUtil.genLockKey4Pay(orderid), lockValue);
			t.complete();
		}
		Cat.logEvent("/pay/create", orderid, Event.SUCCESS, "订单:" + orderid + ",促销代码" + promotionno + ",优惠券" + couponno + ",付款类型(1:预付，2:到付):" + paytype + ",在线支付类型(1:微信,2:支付宝,3:网银,4:其它):" + onlinepaytype);
		return new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
	}

	private void outputInputParamLog(String orderid, String promotionno, String couponno, String paytype, String onlinepaytype) {
		this.logger.info("订单:" + orderid + "-------入参检查:");
		this.logger.info("订单:" + orderid + ",促销代码" + promotionno + ",优惠券" + couponno + ",付款类型(1:预付，2:到付):" + paytype + ",在线支付类型(1:微信,2:支付宝,3:网银,4:其它):" + onlinepaytype);
		this.logger.info("订单:" + orderid + "-------入参检查结束.");
	}

	public String dopost(String url, String send) throws HttpException, IOException {
		HttpClient theclient = new HttpClient();
		PostMethod method = new PostMethod(url);
		try {
			method.setRequestEntity(new StringRequestEntity(send, "text/xml", "UTF-8"));
			method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, SysConfig.timeout);
			theclient.getHttpConnectionManager().getParams().setConnectionTimeout(SysConfig.timeout);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(SysConfig.trytime, false));
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SysConfig.charset);
			int status = theclient.executeMethod(method);
			if (status == HttpStatus.SC_OK) {
				return method.getResponseBodyAsString();
			} else {
				throw new HTTPException(status);
			}
		} finally {
			method.releaseConnection();
		}
	}
	
	/**
	 * 手动下发乐住币
	 * 
	 * @param token 
	 * @param orderid 	订单号
	 * @param imikepay	乐住币
	 * @param reason	下发原因
	 * @return
	 * 
	 * 1. 如果imikepay为空,取消orderid对应的已经下发过的乐住币
	 * 2. 如果imikepay为负数,取消乐住币
	 * 3. 如果imikepay为正数有两种可能
	 *    3.1 之前正常流程下发乐住币失败,重新下发
	 *    3.2 增发乐住币
	 */
	@RequestMapping(value = "/pmspay", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> pmspay(String orderid, String imikepay, String operator) {
		String operateName = operator;
		logger.info("订单:" + orderid + "下发乐住币  参数:(imikepay:{}, operateName{})", imikepay,operateName);
	
		if (Strings.isNullOrEmpty(orderid)) {
			logger.error("订单号为空");
			throw MyErrorEnum.errorParm.getMyException("参数orderid不允许为空");
		}
		if (Strings.isNullOrEmpty(operateName)) {
			logger.error("订单:" + operateName + "参数操作人为空");
			throw MyErrorEnum.errorParm.getMyException("参数操作人不允许为空");
		}
		if (operateName.length()>50) {
			logger.error("订单:" + operateName + "应为50个字符以内");
			throw MyErrorEnum.errorParm.getMyException("参数操作人应为50个字符以内");
		}
		Long otaorderid = Long.parseLong(orderid);
		BigDecimal lezhupay = null;
		if (StringUtils.isNotEmpty(imikepay.trim())) {
			try { 
				lezhupay = new BigDecimal(imikepay);
			} catch (Exception e) {
				logger.error("订单:" + orderid + "参数imikepay格式不正确", e);
				throw MyErrorEnum.errorParm.getMyException("参数imikepay格式不正确.");
			}
		}

		ManualLuzhuRstEnum result;

		try {
			result = payService.pmspay(otaorderid, lezhupay,operateName);
		} catch (Exception e) {
			Cat.logError("手动下发乐住币 error", e);
			logger.error("订单:" + orderid + "下发/取消乐住币流程异常!", e);

			result = ManualLuzhuRstEnum.canReTry;
		}
		// 3. 组织信息返回
		Map<String, Object> rtnMap = Maps.newHashMap();

		rtnMap.put("code", result.getCode());
		rtnMap.put("success", result.getResult());
		rtnMap.put("desc", result.getDesc());
		Cat.logEvent("手动下发乐住币", orderid, Event.SUCCESS,
				String.format("params orderid[%s], imikepay[%s], operator[%s]",orderid, imikepay, operator));
		return new ResponseEntity<Map<String, Object>>(rtnMap, org.springframework.http.HttpStatus.OK);
	} 

//	/**
//	 * 异常情况下支付取消
//	 *  futao.xiao
//	 */
//	@RequestMapping("/cancelpaybyerror1")
//	public  ResponseEntity<Map<String, Object>>   cancelpaybyerror1(String orderid) {
//		logger.info("异常情况下支付取消:orderid="+ orderid  );
//		long otaOrderId=getLongOrderId(orderid);
//		OtaOrder order=orderService.findOtaOrderById(otaOrderId);
//		String repayid=payService.newCancelOrder(order);
//		Map<String, Object>  map=new HashMap<String, Object>();
//		if(repayid!=null ){
//			map.put("success", true);
//			map.put("repayid", repayid);
//		}else{
//			map.put("success", false);
//			map.put("errorcode", MyErrorEnum.kefuRefundOrderError.getErrorCode());
//			map.put("errormsg",  MyErrorEnum.kefuRefundOrderError.getErrorMsg());
//		}
//		return getResponseEntity(map);
//	}
	
	/**
	 * 异常情况下支付取消
	 *  futao.xiao
	 */
	@RequestMapping(value = "/cancelpaybyerror", method = RequestMethod.POST)
	public  ResponseEntity<Object>   cancelpaybyerror(String orderid) {
		logger.info("异常情况下支付取消:orderid="+ orderid  );
		long orderId=getLongOrderId(orderid);
		String responseString=this.payService.cancelpaybyerror(orderId);
		if(responseString!=null){
			logger.info("订单号："+ orderid +" 异常情况下支付取消  成功，返回数据是："+responseString  );
			Cat.logEvent("pay", "异常情况下支付取消", Event.SUCCESS, orderid);
			return new ResponseEntity<Object>(responseString, org.springframework.http.HttpStatus.OK);
		}else{
			logger.info("订单号："+ orderid +" 异常情况下支付取消  出现异常，数据返回 ：null "  );
			Map<String, Object>  map=new HashMap<String, Object>();
			map.put("success", false);
			map.put("errorcode", MyErrorEnum.cancelpaybyerrorError.getErrorCode());
			map.put("errormsg", MyErrorEnum.cancelpaybyerrorError.getErrorMsg());
			Cat.logEvent("pay", "异常情况下支付取消", "ERROR", orderid);
			return new ResponseEntity<Object>(map, org.springframework.http.HttpStatus.OK);
		}
	}
	
	/**
	 * 客单支付情况查询 
     *  futao.xiao
	 */
	@RequestMapping(value = "/selectcustomerpay", method = RequestMethod.POST)
	public  ResponseEntity<Object>  selectCustomerpay(String orderid) {
		logger.info("PMS2.0 客单支付情况查询  :orderid="+ orderid  );
		long otaOrderId=getLongOrderId(orderid);
		String rstr=this.payService.selectCustomerpay(otaOrderId);
		if(rstr!=null){
			logger.info("PMS2.0 客单支付情况查询成功，数据返回  :orderid="+ orderid +"  返回数据是："+rstr  );
			return new ResponseEntity<Object>(rstr, org.springframework.http.HttpStatus.OK);
		}else{
			logger.info("PMS2.0 客单支付情况查询失败，数据返回  :orderid="+ orderid +"  返回数据是 null" );
			Map<String, Object>  map=new HashMap<String, Object>();
			map.put("success", false);
			map.put("errorcode", MyErrorEnum.selselectCustomerpayError.getErrorCode());
			map.put("errormsg", MyErrorEnum.selselectCustomerpayError.getErrorMsg());
			return new ResponseEntity<Object>(map, org.springframework.http.HttpStatus.OK);
		}
	}

	
	/**
	 * @param orderid 	订单编码
	 */
	@RequestMapping(value = "/serviceRefund", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> superRefund(String orderid ){
		logger.info("订单号:"+ orderid +"客服执行退款操作" );
		Long lonOrderid=getLongOrderId(orderid);
		boolean b=payService.superRefund(lonOrderid);
		logger.info("订单号:"+ orderid +"客服执行退款操作结束，结果是:"+b );
		Map<String, Object>  map = Maps.newHashMap();
		map.put("success",  b);
		return new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
	}
	

	/**
	 * @param orderid 查询支付情况
	 */
	@RequestMapping(value = "/findpay", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> findpay(String orderid ){
		logger.info("订单号"+orderid+"准备去银行查询付款信息");
		Map<String, Object>  map = Maps.newHashMap();
       if(orderid==null || orderid.trim().length()<7 ||  orderid.trim().length()>10){
    	   map.put("success",  false);
    	   map.put("info",  "订单号有误");
		}else{
			 Long lonOrderid=getLongOrderId(orderid);
			 if(lonOrderid< 1151576){ //之前的订单号被加长，不能通过此方法查询
				 map.put("success",  false);
		    	 map.put("info",  "请查询2015-07-25开始的【正式环境】订单号");
			 }else{
				 String rs=payService.serviceFindPay(orderid);
				 logger.info("订单号"+orderid+"查询银行付款信息，返回结果是"+rs);
				 map.put("success",  true);
				 map.put("data",  rs);
			 }
		}
		return new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
	}
	
	/**
	 * @param orderid HMS 查询支付情况
	 */
	@RequestMapping(value = "/hmsfindpay", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> querypay(String orderid) {
		logger.info("【HMS】 订单号" + orderid + "准备去银行查询付款信息");
		
		String lockValue = DistributedLockUtil.tryLock(PayLockKeyUtil.genLockKey4PayCallBack(orderid), 40);
		if (StringUtils.isNotEmpty(lockValue)) {
			logger.info("订单：" + orderid + "获取分布锁成功,继续执行回调流程.");
		} else {
			logger.info("订单：" + orderid + "获取分布锁失败,返回.");
			return null;
		}
		
		Map<String, Object> map = Maps.newHashMap();
		try {
			if (orderid == null || orderid.trim().length() < 7 || orderid.trim().length() > 10) {
				map.put("success", false);
				map.put("info", "订单号有误");
			} else {
				Long lonOrderid = getLongOrderId(orderid);
				if (lonOrderid < 1151576) { // 之前的订单号被加长，不能通过此方法查询
					map.put("success", false);
					map.put("info", "请查询2015-07-25开始的订单号");
				} else {
					String rs = payService.hmsFindPay(lonOrderid);
					logger.info("【HMS】订单号" + orderid + "查询银行付款信息，返回结果是" + rs);
					map.put("success", true);
					map.put("data", rs);
				}
			} 
		} finally {
			
			logger.info("订单：" + orderid + "回调流程完毕,释放分布锁.");

			DistributedLockUtil.releaseLock(PayLockKeyUtil.genLockKey4PayCallBack(orderid), lockValue);
		}
		return new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/modifyPayStatus" , method = RequestMethod.POST)
	public  ResponseEntity<Object>   modifyPayStatus(String orderid,String paystatus,String operator) {
		
		this.logger.info("modifyPayStatus：orderid"+orderid+",payStatus:"+paystatus+",operator:"+operator+"，begin");
		if (Strings.isNullOrEmpty(orderid)) { // 订单号 必填

			this.logger.error("modifyPayStatus，订单号:"+orderid+"orderid为空：");
			throw MyErrorEnum.errorParm.getMyException("[订单号] 不允许为空.");
		}
		if (Strings.isNullOrEmpty(paystatus)) { // 支付状态 必填

			this.logger.error("modifyPayStatus，订单号:"+orderid+"，paystatus为空：");
			throw MyErrorEnum.errorParm.getMyException("[支付状态] 不允许为空.");
		}
		if (Strings.isNullOrEmpty(operator)) { // 操作人 必填

			this.logger.error("modifyPayStatus，订单号:"+orderid+"，operator为空：");
			throw MyErrorEnum.errorParm.getMyException("[操作人] 不允许为空.");
		}
		this.logger.info("PayController::modifyPayStatus：orderid"+orderid+"，begin");
		boolean flag = this.payService.modifyPayStatus(Long.parseLong(orderid), Integer.parseInt(paystatus), operator);
		this.logger.info("PayController::modifyPayStatus：orderid"+orderid+"，end");
		Map<String, Object>  map=new HashMap<String, Object>();
		if(flag){
			map.put("success", true);
			return new ResponseEntity<Object>(map, org.springframework.http.HttpStatus.OK);
		}else{
			map.put("success", false);
			map.put("errorcode", MyErrorEnum.modifypaystatusbyerrorError.getErrorCode());
			map.put("errormsg", MyErrorEnum.modifypaystatusbyerrorError.getErrorMsg());
			return new ResponseEntity<Object>(map, org.springframework.http.HttpStatus.OK);
		}
	}

	/**
	 * @param orderid 模拟环境批量退款情况
	 */
	@RequestMapping(value = "/refundpay", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> refundpay(String orderid ){
		logger.info("入参订单号："+orderid);
		Map<String, Object>  map = Maps.newHashMap();
    
		payService.refundBatchPay(orderid);
		map.put("success",  true);
		return new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
	}
	
}
