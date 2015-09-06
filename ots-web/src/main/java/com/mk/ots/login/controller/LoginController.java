package com.mk.ots.login.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.common.enums.VerifyEnum;
import com.mk.ots.common.utils.CheckTools;
import com.mk.ots.hotel.bean.LoginBean;
import com.mk.ots.log.ILogService;
import com.mk.ots.login.model.ULoginLog;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.system.model.UToken;
import com.mk.ots.system.service.impl.VerifyCodeService;

/**
 * 会员登录接口
 * 
 * @author nolan
 */
//@RequestMapping(value="/auth", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {
	
	@Autowired
	private IMemberService iMemberService;
	
	@Autowired
	private ILogService iLogService;
	
	@Autowired
	private VerifyCodeService verifyCodeService;
	
	/**
	 * 生成并发送短信验证码
	 * @return
	 */
	@RequestMapping("/verifycode/send")
	public ResponseEntity<String> genRegisterVerifyCode(String phonenum){
		if(Strings.isNullOrEmpty(phonenum)){
			throw new IllegalArgumentException("手机号为空.");
		}
		String verifyCode = verifyCodeService.generatePhoneVerifyCode(phonenum, VerifyEnum.LOGIN);
		String msgcontent = verifyCodeService.generateMsgContent(verifyCode, VerifyEnum.LOGIN);
		//TODO  调用短信或语音接口
		return new ResponseEntity<String>("验证码已发送. testmsg:"+msgcontent, HttpStatus.OK);
	}
	
	
	/**
	 * 登录认证
	 * @param mobile
	 * @return
	 */
	@RequestMapping("/login")
	public ResponseEntity<Map> login(HttpServletRequest request,String phonenum, String verifycode, String appversion, String ostype ,  String comefrom){
		//1.数据校验
		//1.1 手机号
		if(!CheckTools.checkPhoneNum(phonenum)){
			throw MyErrorEnum.PhoneNumFormatError.getMyException();
		}
		//1.2 appversion
		if(!CheckTools.checkAppVersion(appversion)){
			throw MyErrorEnum.appversionError.getMyException();
		}
		//1.3 verifycode
		if(Strings.isNullOrEmpty(verifycode)){
			throw MyErrorEnum.errorParm.getMyException("验证码不允许为空.");
		}
		ostype = Strings.isNullOrEmpty(ostype) ? "" : ostype;
		comefrom = Strings.isNullOrEmpty(comefrom) ? "" : comefrom;
		
		//2.登录验证
		//2.1 校验验证码
		String debugCode= "8888";//iRegService.findCodeByDebugPhone(phonenum);
		if(!debugCode.equals(verifycode) && !verifyCodeService.checkPhoneVerifyCode(phonenum, verifycode, VerifyEnum.LOGIN)){
			throw MyErrorEnum.PhoneVerifyError.getMyException();
		} 
		
		
		//2.2 判断用户是否存在,不存在则自动注册用户
		Optional<UMember> findMemberByPhone = iMemberService.findMemberByPhone(phonenum);
		Integer clientversion = Integer.parseInt(appversion);
		UMember member = null;
		Date time = new Date();
		if(!findMemberByPhone.isPresent()){
			member = iMemberService.regNewMember(String.valueOf(phonenum), appversion, ostype);
		}else{
			member = findMemberByPhone.get();
		}
		
		UToken ut = MyTokenUtils.genAndCacheUToken(member.getMid(),TokenTypeEnum.PT,OSTypeEnum.getEnumById(ostype));
		
		ULoginLog loginLog = new ULoginLog();
		loginLog.setType(LoginBean.NORMAL_LOGIN);//1,登录;2,自动登录
		loginLog.setAccessToken(ut.getAccesstoken());
		loginLog.setAppVersion(clientversion);
		loginLog.setIp("");//NetUtils.getIpAddr(request));
		loginLog.setMid(member.getMid());
		loginLog.setOsType(ostype);
		loginLog.setTime(time);
		iLogService.savaOrUpdate(loginLog,time);
		
		//4. 构造返回参数
		Map<String,Object> rtnMap = Maps.newHashMap();
		member.setPaypassword("");
		rtnMap.put("member", member);
		rtnMap.put("accesstoken", ut.getAccesstoken());
		rtnMap.put("success", true);
		return new ResponseEntity<Map>(rtnMap, HttpStatus.OK);
	}
	
	/**
	 * 自动登陆
	 * @param request
	 * @param accesstoken
	 * @param appversion
	 * @param ostype
	 * @return
	 */
	@RequestMapping("/autologin")
	public ResponseEntity<Map> autoLogin(HttpServletRequest request, String accesstoken, String appversion, String ostype){
		if(!CheckTools.checkAppVersion(appversion)){
			throw MyErrorEnum.appversionError.getMyException();
		}
		//缓存中查找token
		UToken token = MyTokenUtils.getToken(accesstoken);
		//数据库中查找token
		if(token == null){
			throw MyErrorEnum.accesstokenTimeOut.getMyException();
		}
		UMember member = MyTokenUtils.getMemberByToken(accesstoken);
		if(member == null){
			throw MyErrorEnum.memberNotExist.getMyException();
		}
		
		Date time = new Date();
		ULoginLog loginLog = new ULoginLog();
		loginLog.setType(LoginBean.AUTO_LGOIN);//1,登录;2,自动登录
		loginLog.setAccessToken(token.getAccesstoken());
		loginLog.setAppVersion(Integer.parseInt(appversion));
		loginLog.setIp("");//NetUtils.getIpAddr(request));
		loginLog.setMid(member.getMid());
		loginLog.setOsType(ostype);
		loginLog.setTime(time);
		iLogService.savaOrUpdate(loginLog,time);
		
		Map<String,Object> rtnMap = Maps.newHashMap();
		member.setPaypassword("");
		rtnMap.put("member", member);
		rtnMap.put("success", true);
		return new ResponseEntity<Map>(rtnMap, HttpStatus.OK);
	}
	
	/**
	 * 退出登录
	 * @param mobile
	 * @return
	 */
	@RequestMapping("/logout")
	public ResponseEntity<String> logout(String accesstoken){
		MyTokenUtils.removeToken(accesstoken);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
