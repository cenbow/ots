package com.mk.ots.member.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.common.enums.MessageTypeEnum;
import com.mk.ots.common.enums.VerifyEnum;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.system.service.impl.VerifyCodeService;

/**
 * 会员管理接口
 * 
 * @author nolan
 */
@Controller
@RequestMapping(value="/member", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {
	Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@Autowired
	private IMemberService iMemberService;
	
	@Autowired
	private VerifyCodeService verifyCodeService;
	
	@Autowired
	private IMessageService messageService;
	
	/**
	 * 发送验证码或语音信息到手机
	 * @return
	 */
	@RequestMapping("/verifycode/send")
	public ResponseEntity<String> genRegisterVerifyCode(String phonenum, String messagetype){
		//1. 参数校验及缺省参数处理
		if(Strings.isNullOrEmpty(phonenum)){
			throw MyErrorEnum.errorParm.getMyException("手机号为空.");
		}
		MessageTypeEnum msgType =  MessageTypeEnum.getEnumById(messagetype);
		if(msgType == null){
			msgType = MessageTypeEnum.normal;
		}
		
		//2. 验证码生成
		String verifyCode = verifyCodeService.generatePhoneVerifyCode(phonenum, VerifyEnum.REBIND);
		String msgcontent = verifyCodeService.generateMsgContent(verifyCode, VerifyEnum.REBIND);
		
		//3. 调用消息接口发送数据
		messageService.sendMsg(null, phonenum,msgcontent, msgType, null);

		//4. 组织参数返回
		return new ResponseEntity<String>("验证码已发送", HttpStatus.OK);
	}
	
	/**
	 * 修正用户基本信息
	 * @param token 由系统统一生成
	 * @param name  用户姓名
	 * @param sex	性别
	 * @param birthday	出生年月
	 * @return
	 */
	@RequestMapping("/detail/update")
	public ResponseEntity<String> modifyDetail(String accesstoken, String name, String sex, String birthday){
		//1. 根据token获取相关会员信息
		Long mid = MyTokenUtils.getMidByToken(accesstoken);
		if(mid == null){
			throw MyErrorEnum.errorParm.getMyException("获取用户信息失败.");
		}

		//2. 更新用户信息
		iMemberService.updateBaseInfo(mid, name, sex, birthday);
		
		//3. 返回数据
		return new ResponseEntity<String>("信息修改成功.",HttpStatus.OK);
	}	
	
	/**
	 * 重置支付密码
	 * @param token 由系统统一生成
	 * @param newPassword 新密码
	 * @return
	 */
	@RequestMapping("/paypwd/reset")
	public ResponseEntity<String> resetPayPwd(String accesstoken, String oldpwd, String newpwd){
		//1. 根据token获取相关会员信息 
		UMember um = MyTokenUtils.getMemberByToken(accesstoken);
		
		//2. 修改密码
		if(um == null){
			throw MyErrorEnum.errorParm.getMyException("获取用户信息失败.");
		}
		if(!iMemberService.checkPayPwd(um.getLoginname(), oldpwd)){
			throw MyErrorEnum.errorParm.getMyException("原密码不正确.");
		}
		iMemberService.resetPayPwd(um.getLoginname(), newpwd);
		
		//3. 返回参数
		return new ResponseEntity<String>("重置密码成功.", HttpStatus.OK);
	}
	
	/**
	 * 更换手机号
	 * @param token
	 * @param mobile
	 * @return
	 */
	@RequestMapping("/phone/rebind")
	public ResponseEntity<String> modifyPhone(String token, String phonenum , String verifycode){
		//1. 参数校验
		if(!verifyCodeService.checkPhoneVerifyCode(phonenum, verifycode, VerifyEnum.REBIND)){
			throw MyErrorEnum.PhoneVerifyCodeError.getMyException();
		}
		
		//2. 获取token信息
		UMember um = MyTokenUtils.getMemberByToken(token);
		if(um == null){
			throw MyErrorEnum.errorParm.getMyException("获取用户信息失败.");
		}
		
		//3. 重置手机号
		iMemberService.resetPhoneNum(um.getLoginname(), phonenum);
		
		//4. 返回数据
		return new ResponseEntity<String>("更换手机号成功.", HttpStatus.OK);
	}
	
	/**
	 * 检测Unionid
	 * @param weixinUnionid
	 * @return
	 */
	@RequestMapping("/weixin/checkunionid")
	public ResponseEntity<Map> checkWeixinOpenId(String unionid){
		Map rtnMap  = ImmutableMap.of("success", true, "check", iMemberService.isExistUnionid(unionid), "token","");
		return new ResponseEntity<Map>(rtnMap, HttpStatus.OK);
	} 
	
	/**
	 * 绑定unionid和手机号
	 * @param token
	 * @param unionid
	 * @return
	 */
	@RequestMapping("/weixin/bindunionid")
	public ResponseEntity<Map> bindWeixinUnionid(String accesstoken, String unionid){
		//1. 参数校验
		UMember um = MyTokenUtils.getMemberByToken(accesstoken);
		if(um == null){
			throw MyErrorEnum.errorParm.getMyException("获取用户信息失败.");
		}
		if(Strings.isNullOrEmpty(unionid)){
			throw MyErrorEnum.errorParm.getMyException("unionid不允许为空.");
		}

		//2. 业务逻辑判断
		Map<String,Object> rtnMap = Maps.newHashMap();
		boolean  check = true;
		if(!Strings.isNullOrEmpty(um.getOpenid()) && !um.getUnionid().equals(unionid)){
			rtnMap.put("checkerrortype", 2); //绑定失败的原因，1、openid已被其他手机号绑定，2、手机号已被其他openid绑定
			check = false;
		}
		Optional<UMember> oMember = iMemberService.findMemberByUnionid(unionid);
		if(oMember.isPresent() && !um.getMid().equals(oMember.get().getMid())){
			rtnMap.put("checkerrortype", 1);
			check = false;
		}
		
		//3.更新unionid
		if(check){
			iMemberService.updateBaseInfo(um.getMid(), um.getName(), unionid);
		}
		
		//4. 组织返回数据 
		rtnMap.put("success", check);
		rtnMap.put("check", check); 	//true表示已绑定成功，false表示绑定失败
		rtnMap.put("token", "");		//成功绑定后的token
		return new ResponseEntity<Map>(rtnMap, HttpStatus.OK);
	}
	
	@RequestMapping("/upDelivery")
	public ResponseEntity<Map> upDelivery(String token, String name,
			String deliveryAddress) {
		// 1. 参数校验
		if(Strings.isNullOrEmpty(token)){
			throw MyErrorEnum.errorParm.getMyException("token不允许为空.");
		}
		if(Strings.isNullOrEmpty(name)){
			throw MyErrorEnum.errorParm.getMyException("name不允许为空.");
		}
		if(Strings.isNullOrEmpty(deliveryAddress)){
			throw MyErrorEnum.errorParm.getMyException("deliveryaddress不允许为空.");
		}
		UMember um = MyTokenUtils.getMemberByToken(token);
		if (um == null) {
			throw MyErrorEnum.errorParm.getMyException("获取用户信息失败.");
		}
		um.setAddress(deliveryAddress);
		um.setName(name);
		iMemberService.saveOrUpdate(um);
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map>(rtnMap, HttpStatus.OK);
	}
}
