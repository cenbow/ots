package com.mk.ots.pay.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.base.Strings;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.XMLUtils;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.order.model.BTicketOrder;
import com.mk.ots.order.service.OrderBusinessLogService;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.order.service.OrderUtil;
import com.mk.ots.order.service.TicketOrderService;
import com.mk.ots.pay.module.ali.AliPay;
import com.mk.ots.pay.module.weixin.AppPay;
import com.mk.ots.pay.module.weixin.WeChat;
import com.mk.ots.pay.module.weixin.pay.common.Tools;
import com.mk.ots.pay.service.IPayService;
import com.mk.ots.pay.service.IPriceService;
import com.mk.ots.promo.model.BPromotionProduct;
import com.mk.ots.promo.service.IPromotionProductService;

@Controller
@RequestMapping(value = "/pay/ticket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class PayTicketController {
 
	private Logger logger = LoggerFactory.getLogger(PayTicketController.class);

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
	@Autowired
	private OrderUtil orderUtil;
	
	@Autowired
	private TicketOrderService payTicketService;

	@Autowired
	private IPromotionProductService iPromotionProductService;
	/**
	 * 购买优惠券
		productId	优惠券产品id	必填
		onlinepaytype	预付支付类型	非必填(若预付，则必填)
		1、微信
		2、支付宝
		3、网银
		4、其他
	 * @param request
	 * @return
	 */
	@RequestMapping("/createorder")
	public ResponseEntity<JSONObject> create(HttpServletRequest request,String productid,String onlinepaytype) {
		// 提取orderbean
		if (orderUtil.checkNotNulls(request, new String[] {"productid","onlinepaytype" })) {
			throw MyErrorEnum.errorParm.getMyException("必填项为空");
		}
		
		String accessToken = request.getParameter("token");
		UMember uMember =  MyTokenUtils.getMemberByToken(accessToken);
		if(uMember==null){
			throw MyErrorEnum.errorParm.getMyException("token错误");
		}
		Long mid = uMember.getMid();
		
		
		//根据优惠券产品id获取优惠券金额和优惠券id
		BPromotionProduct bPromotionProduct = iPromotionProductService.queryBPromotionProduct(Long.valueOf(productid));
		
		if(bPromotionProduct==null){
			throw MyErrorEnum.errorParm.getMyException("没有优惠券");
		}
		
		BTicketOrder bTicketOrder = new BTicketOrder();
		bTicketOrder.setCreatetime(new Date());
		bTicketOrder.setUpdatetime(new Date());
		bTicketOrder.setMid(mid);
		bTicketOrder.setPaystatus(PayStatusEnum.waitPay.getId());
		bTicketOrder.setPaytype(Integer.valueOf(onlinepaytype));
		bTicketOrder.setProductid(bPromotionProduct.getProductid());
		//保存数据到b_ticket_buyinfo  状态为等待支付
		Long orderId = payTicketService.savePayTicketInfo(bTicketOrder);
		// 订单转换为json
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("success", true);
		jsonObj.put("price", bPromotionProduct.getPrice());
		jsonObj.put("orderId", "ticket_"+orderId);
		
		String orderIdStr = "ticket_"+orderId;
//		String notify = "pay/ticket/check";
		BigDecimal price = bPromotionProduct.getPrice();
		String bodySB="优惠券";
		
		
		//orderIdStr callbackurl price onlinepaytype
		
		
		return new ResponseEntity<JSONObject>(jsonObj,  org.springframework.http.HttpStatus.OK); 
	}

	// 支付宝退款回调地址 ***不用本地系统调用***【支付宝专用】
	@RequestMapping("/alicancel")
	public String alicancel(HttpServletRequest request) {
		String result_details = request.getParameter("result_details");
		this.logger.info("支付宝退款回调： " + result_details);
		if (this.payService.alipayCancelRes(result_details)) {
			this.logger.info("支付宝退款回调处理成功. ");
			return "success";
		} else {
			this.logger.info("支付宝退款回调处理失败. ");
			return "";
		}
	}

	// 支付宝回调地址 ***不用本地系统调用***【支付宝专用】
	@RequestMapping("/alires")
	// 成功直接返回 success
	public String alires(HttpServletRequest request) {
		String res = "";
		String orderid = request.getParameter("out_trade_no");
		this.logger.info("支付宝回调，订单号：" + orderid);
		long orderIdNum =  getLongOrderId(orderid);

		String trade_status = request.getParameter("trade_status");
		// 支付成功 //WAIT_BUYER_PAY等待付款， TRADE_CLOSED 未付款交易关闭，
		// TRADE_FINISHED交易成功，不能改变的
		if (trade_status.equals("TRADE_SUCCESS")) {
			this.logger.info("支付宝回调，订单号：" + orderid + "   trade_status=" + trade_status);
			// 不能多次请求改状态
			if (this.payTicketService.payIsWaitPay(orderid)) {
				String payid = request.getParameter("trade_no");
				String price = request.getParameter("price");
				
				this.logger.info("支付宝回调，订单号：" + orderid + "  支付宝支付成功，第一次回调");
				
				
				BTicketOrder bTicketOrder = new BTicketOrder();
				bTicketOrder.setId(orderIdNum);
				bTicketOrder.setUpdatetime(new Date());
				bTicketOrder.setPaymentid(payid);
				bTicketOrder.setPaystatus(PayStatusEnum.alreadyPay.getId());
				bTicketOrder.setPaytime(new Date());
				bTicketOrder.setPrice(new BigDecimal(price));
				bTicketOrder.setPaytype(PPayInfoOtherTypeEnum.alipay.getId());
				if (this.payTicketService.updateBTicketOrderStatus(bTicketOrder)>0){
					// 处理成功后给支付宝返回 success将不会再请求
					res = "success";
				}
				this.logger.info("支付宝回调，订单号：" + orderid + "  支付宝支付成功，第一次回调结束");
			} else {
				if (this.payTicketService.payOkByOrderid(orderIdNum)) {
					res = "success";
				}
			}
		} else {
			this.logger.info("支付宝回调，订单号：" + orderid + "  支付宝支付失败，状态是：" + trade_status);
		}
		return res;
	}

	// 微信支付专用【App】
	@RequestMapping("/wxres")
	// http://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=9_7&index=3
	public String wxres(HttpServletRequest request) {
		Element elroot = null;
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
		String out_trade_no = elroot.getChildText("out_trade_no");
		String result_code = elroot.getChildText("result_code");
		String payid = elroot.getChildText("transaction_id");
		String total_fee = elroot.getChildText("total_fee");
		this.logger.info("return_code={},return_msg={},out_trade_no={}", return_code, return_msg, out_trade_no);
		this.logger.info("result_code={},payid={},total_fee={}", result_code, payid, total_fee);
		String res = null;
		String orderid = out_trade_no;
		
		Long orderIdNum=getLongOrderId(orderid);
		
		if (return_code.equalsIgnoreCase("SUCCESS")) {
			if (result_code.equalsIgnoreCase("SUCCESS")) {
				// 去支付的out_trade_no 是orderid经过处理的
				if (this.payTicketService.payIsWaitPay(orderid)) {
					try {
						// 微信返回的价格是以 【分为单位】
						BigDecimal b = new BigDecimal(total_fee);
						total_fee = b.divide(new BigDecimal("100")).doubleValue() + "";
					} catch (Exception e) {
						throw MyErrorEnum.errorParm.getMyException("获取支付金额失败");
					}
					BTicketOrder bTicketOrder = new BTicketOrder();
					bTicketOrder.setId(orderIdNum);
					bTicketOrder.setUpdatetime(new Date());
					bTicketOrder.setPaymentid(payid);
					bTicketOrder.setPaystatus(PayStatusEnum.alreadyPay.getId());
					bTicketOrder.setPaytime(new Date());
					bTicketOrder.setPaytype(PPayInfoOtherTypeEnum.wxpay.getId());
					bTicketOrder.setPrice(new BigDecimal(total_fee));
					if (this.payTicketService.updateBTicketOrderStatus(bTicketOrder)>0){
						res = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
					}
				}else{
					if (this.payTicketService.payOkByOrderid(orderIdNum)) {
						// 返回此信息，后面就不会再有此订单的回调
						res = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
					}
				}
			}
		} else {
			this.logger.info("微信回调,支付错误，错误信息码：" + return_msg);
		}
		this.logger.info("微信回调结束");
		return res;
	}

	/**
	 * 此方法只是查询本地有没接收到支付宝或者微信返回信息的情况， 如果没有修改本地状态，微信需要去银行查询
	 * @param orderid 订单号，必填
	 * @param onlinepaytype 预付支付类型，必填 1 微信 2 支付宝 3、网银
	 * @param payno 第三方交易号，非必填（若预付类型是微信，则必填）
	 * @param paytime 支付时间 非必填（若预付类型是微信，则必填）待确认
	 * @param price 支付的价格 非必填（若预付类型是微信，则必填）待确认
	 * @param prepayid 微信去支付时候定的返回的字段
	 * @return
	 */
	@RequestMapping("/check")
	public ResponseEntity<Map<String, Object>> checkPay(String orderid) {
		this.logger.info("支付检测，订单号：" + orderid);
		Map<String, Object> map = new HashMap<String, Object>();
		if (Strings.isNullOrEmpty(orderid)) { // 订单号 必填
			this.logger.error("支付检测，订单号orderid为空：");
			throw MyErrorEnum.errorParm.getMyException("[订单号] 不允许为空.");
		}
		BTicketOrder bTicketOrder = payTicketService.findTicketOrder(Long.valueOf(orderid));
		
		if (bTicketOrder==null) { // 订单号 必填
			this.logger.error("支付检测，订单不存在：");
			throw MyErrorEnum.errorParm.getMyException("[订单号] 不存在.");
		}
		
		Integer onlinepaytype = bTicketOrder.getPaytype();
		String payno = bTicketOrder.getPaymentid();
		BigDecimal price = bTicketOrder.getPrice();
		this.logger.info("支付检测，订单号：" + orderid + ",onlinepaytype:" + onlinepaytype + ",payno:" + payno  + ",price:" + price);
		if (onlinepaytype==null) { // 预付支付类型 必填, 微信 支付宝 网银。
			this.logger.error("支付检测， [预付支付类型] 为空：");
			throw MyErrorEnum.errorParm.getMyException("[预付支付类型] 不允许为空.");
		}
		// 没有接收到银行信息并且Order 支付状态没有改为支付的
		boolean b = this.payTicketService.payOkByOrderid(Long.valueOf(orderid));
		this.logger.info("付款完成后回调，先去本地查询，orderid=" + orderid + "   boolean=====" + b);
		if (!b) {
			// 去银行判断
			String payid = null;
			// 微信
			if (onlinepaytype.equals("1")) {
				this.logger.info("微信ID" + payno + " 去微信查询！orderid::" + orderid);
				int priceint = Tools.get100price(price);
				payid = AppPay.query(orderid, payid, priceint);
				bTicketOrder.setUpdatetime(new Date());
				bTicketOrder.setPaystatus(PayStatusEnum.alreadyPay.getId());
				bTicketOrder.setPaytime(new Date());
				bTicketOrder.setPaytype(PPayInfoOtherTypeEnum.wxpay.getId());
				if (this.payTicketService.updateBTicketOrderStatus(bTicketOrder)>0){
					map.put("success", true);
				} else {
					map.put("success", false);
					map.put("errcode", MyErrorEnum.requestWXError.getErrorCode());
					map.put("errmsg", MyErrorEnum.requestWXError.getErrorMsg());
				}
			} else if (onlinepaytype.equals("2")) {
				payid= AliPay.query(orderid, payno);
				if(payid!=null && payid!="" ){
					bTicketOrder.setUpdatetime(new Date());
					bTicketOrder.setPaystatus(PayStatusEnum.alreadyPay.getId());
					bTicketOrder.setPaytime(new Date());
					bTicketOrder.setPaytype(PPayInfoOtherTypeEnum.alipay.getId());
					if (this.payTicketService.updateBTicketOrderStatus(bTicketOrder)>0){
						map.put("success", true);
					} else {
						map.put("success", false);
						map.put("errcode", MyErrorEnum.requestAliError.getErrorCode());
						map.put("errmsg", MyErrorEnum.requestAliError.getErrorMsg());
					}
				}
				// 支付宝 还没有签约单笔订单查询接口，故没法去支付宝查询，先返回没支付成功
			} else {
				throw MyErrorEnum.errorParm.getMyException("当前只支持 支付宝和微信.");
			}
		} else {
			this.logger.info("订单已经是支付状态，success＝true   orderid==" + orderid);
			map.put("success", true);
		}
		this.logger.info("支付检测 ,处理完成，orderid=" + orderid);
		return new ResponseEntity<Map<String, Object>>(map, org.springframework.http.HttpStatus.OK);
	}

	/** 得到Long类型orderId */
	private Long getLongOrderId(String orderid) {
		try {
			return Long.parseLong(orderid.replace("ticket_", ""));
		} catch (NumberFormatException e1) {
			throw MyErrorEnum.errorParm.getMyException("订单号只能是数字");
		}
	}
	
	
}
