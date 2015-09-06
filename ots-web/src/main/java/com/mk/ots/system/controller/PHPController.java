//package com.mk.ots.system.controller;
//
//import java.util.UUID;
//
//import javax.servlet.http.HttpServletRequest;
//
//import net.sf.json.JSONObject;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import cn.com.winhoo.mikeweb.service.IRegService;
//
//import com.google.common.base.Optional;
//import com.mk.framework.exception.MyErrorEnum;
//import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
//import com.mk.ots.common.enums.TokenTypeEnum;
//import com.mk.ots.common.utils.CalculateMd5;
//import com.mk.ots.common.utils.CheckTools;
//import com.mk.ots.common.utils.Constant;
//import com.mk.ots.member.model.UMember;
//import com.mk.ots.member.service.IMemberService;
//import com.mk.ots.pay.model.OtherPayResult;
//import com.mk.ots.pay.service.IPayService;
//import com.mk.ots.system.model.UToken;
//import com.mk.ots.system.service.ITokenService;
//
///**
// * 手机相关的
// */
//
//public class PHPController {
//
//	private static final Logger logger = LoggerFactory.getLogger(PHPController.class);
//
//	@Autowired
//	private IMemberService memberService;
//	@Autowired
//	private ITokenService tokenService;
//	@Autowired
//	private IPayService payService;
//	@Autowired
//	private IRegService regService;
//
//	@RequestMapping(value = "/sendMessageByPhone", method = RequestMethod.POST)
//	public ResponseEntity<String> sendMessageByPhone(HttpServletRequest request) {
//		String phone = request.getParameter("phone");
//		String message = request.getParameter("message");
//		String type = request.getParameter("type");
//		PHPController.logger.error("PHP-sendMessageByPhone-----------------------" + "\n phone:" + phone + "\n message:" + message + "\n type:"
//				+ type);
//		if ((phone == null) || (message == null)) {
//			throw MyErrorEnum.errorParm.getMyException("必填项为空");
//		}
//		if (!CheckTools.checkPhoneNum(phone)) {
//			throw MyErrorEnum.PhoneNumFormatError.getMyException();
//		}
//		if ("2".equals(type)) {
//			// HmHessianManager.getInstance().getService().sendAudioMessage(phone,
//			// message,"php",IPUtils.getIpAddr(request));
//		} else {
//			// HmHessianManager.getInstance().getService().sendMessage(phone,
//			// message,"php",IPUtils.getIpAddr(request));
//		}
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.element("success", true);
//		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/checkOpenIdAndPhone", method = RequestMethod.POST)
//	public ResponseEntity<String> checkOpenIdAndPhone(HttpServletRequest request) {
//		String openid = request.getParameter("openid");
//		PHPController.logger.error("PHP-checkOpenIdAndPhone-----------------------" + "\n openid:" + openid);
//		if (StringUtils.isEmpty(openid)) {
//			throw MyErrorEnum.errorParm.getMyException("必填项为空");
//		}
//		Optional<UMember> optionalMember = this.memberService.findMemberByOpenid(openid);
//		UMember member = optionalMember.get();
//		JSONObject jsonObj = new JSONObject();
//		if (member == null) {
//			jsonObj.element("success", true);
//			jsonObj.element("check", false);
//		} else {
//			jsonObj.element("success", true);
//			jsonObj.element("check", true);
//			UToken token = this.tokenService.findTokenByMId(member.getMid(), TokenTypeEnum.WX);
//			if (token != null) {
//				jsonObj.element("token", token.getAccesstoken());
//			} else {
//				UToken uToken = new UToken();
//				uToken.setMid(member.getMid());
//				uToken.setAccesstoken(UUID.randomUUID().toString());
//				uToken.setType(TokenTypeEnum.WX);
//				token = this.tokenService.savaOrUpdate(uToken);
//				jsonObj.element("token", token.getAccesstoken());
//			}
//		}
//		jsonObj.element("success", true);
//		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/bindingOpenIdAndPhone", method = RequestMethod.POST)
//	public ResponseEntity<String> bindingOpenIdAndPhone(HttpServletRequest request) {
//		String openid = request.getParameter("openid");
//		String phone = request.getParameter("phone");
//		String nickname = request.getParameter("nickname");
//		PHPController.logger.error("PHP-bindingOpenIdAndPhone-----------------------" + "\n openid:" + openid + "\n phone:" + phone + "\n nickname:"
//				+ nickname);
//		if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(phone)) {
//			throw MyErrorEnum.errorParm.getMyException("必填项为空");
//		}
//		Optional<UMember> findMemberByOpenid = this.memberService.findMemberByOpenid(openid);
//		Optional<UMember> findMemberByLoginName = this.memberService.findMemberByLoginName(phone);
//		JSONObject jsonObj = new JSONObject();
//		if (findMemberByOpenid.isPresent() && findMemberByLoginName.isPresent()) {
//			UMember mOpenid = findMemberByOpenid.get();
//			UMember mphone = findMemberByLoginName.get();
//			if (mOpenid.equals(mphone)) {
//				jsonObj.element("check", "true");
//				UToken token = this.tokenService.findTokenByMId(mphone.getMid(), TokenTypeEnum.WX);
//				if (token != null) {
//					jsonObj.element("token", token.getAccesstoken());
//				} else {
//					UToken uToken = new UToken();
//					uToken.setMid(mphone.getMid());
//					uToken.setAccesstoken(UUID.randomUUID().toString());
//					uToken.setType(TokenTypeEnum.WX);
//					token = this.tokenService.savaOrUpdate(uToken);
//					jsonObj.element("token", token.getAccesstoken());
//				}
//			} else {
//				if ((mphone.getOpenid() != null) && !mphone.getOpenid().equals(openid)) {
//					throw MyErrorEnum.phoneByOpenid.getMyException();
//				} else {
//					throw MyErrorEnum.openidByPhone.getMyException();
//				}
//			}
//		} else if (findMemberByOpenid.isPresent()) {
//			throw MyErrorEnum.openidByPhone.getMyException();
//		} else if (findMemberByLoginName.isPresent()) {
//			UMember mphone = findMemberByLoginName.get();
//			if ((mphone.getOpenid() != null) && !mphone.getOpenid().equals(openid)) {
//				// 手机号已被其他openid绑定
//				throw MyErrorEnum.phoneByOpenid.getMyException();
//			} else {
//				mphone.setOpenid(openid);
//				if (StringUtils.isBlank(mphone.getName()) && StringUtils.isNotBlank(nickname)) {
//					mphone.setName(nickname);
//				}
//				this.memberService.updateBaseInfo(mphone.getMid(), nickname, openid);
//				jsonObj.element("check", "true");
//				UToken token = this.tokenService.findTokenByMId(mphone.getMid(), TokenTypeEnum.WX);
//				if (token != null) {
//					jsonObj.element("token", token.getAccesstoken());
//				} else {
//					UToken uToken = new UToken();
//					uToken.setMid(mphone.getMid());
//					uToken.setAccesstoken(UUID.randomUUID().toString());
//					uToken.setType(TokenTypeEnum.WX);
//					token = this.tokenService.savaOrUpdate(uToken);
//					jsonObj.element("token", token.getAccesstoken());
//				}
//			}
//		} else {
//			String pass = CalculateMd5.caculateCF(openid, Constant.defaulCharset);
//			String accessToken = UUID.randomUUID().toString();
//			// regService.regMember(phone, pass, accessToken, "W", -1, nickname,
//			// openid);
//			jsonObj.element("check", "true");
//			jsonObj.element("token", accessToken);
//		}
//		jsonObj.element("success", true);
//		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
//	}
//
//	/**
//	 * 检验微信支付通知
//	 *
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(value = "/weixinNotify", method = RequestMethod.POST)
//	public ResponseEntity<String> weixinNotify(HttpServletRequest request) {
//		String orderid = request.getParameter("orderid");
//		String payno = request.getParameter("payno");
//		String price = request.getParameter("price");
//		OtherPayResult result = new OtherPayResult();
//		result.setPayType(PPayInfoOtherTypeEnum.tenpay);
//		if (orderid.toUpperCase().startsWith(Constant.testStr)) {
//			orderid = orderid.substring(Constant.testStr.length(), orderid.length());
//		}
//		result.setOutTradeNo(orderid);
//		result.setTradeNo(payno);
//		result.setTotalFee(price);
//		// TODO 待处理 payService.alipayReturnNotify(result);
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.element("success", true);
//		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
//	}
//
//}
