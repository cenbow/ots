package com.mk.ots.member.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.base.Strings;
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
import com.google.common.collect.Maps;
import com.mk.care.kafka.common.CopywriterTypeEnum;
import com.mk.care.kafka.model.Message;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.CharUtils;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.ValidateUtils;
import com.mk.ots.appstatus.dao.IAppStatusDao;
import com.mk.ots.appstatus.model.AppStatus;
import com.mk.ots.appstatus.service.IAppStatusService;
import com.mk.ots.common.enums.ComefromtypeEnum;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.common.utils.DESUtils;
import com.mk.ots.kafka.message.OtsCareProducer;
import com.mk.ots.logininfo.model.BLoginInfo;
import com.mk.ots.logininfo.service.IBloginInfoService;
import com.mk.ots.logininfo.service.impl.BloginInfoService;
import com.mk.ots.member.model.BAppUpdate;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IBAppUpdateService;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.promo.service.IPromoService;
import com.mk.ots.system.model.UToken;
import com.mk.ots.system.service.ITokenService;

/**
 * @author nolan
 * 
 */
@Controller
@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class AppController {
	final Logger logger = LoggerFactory.getLogger(AppController.class);

	@Autowired
	private IMemberService iMemberService;

	@Autowired
	private ITokenService iTokenService;

	@Autowired
	private IAppStatusDao iAppStatusDao;

	@Autowired
	private IPromoService iPromoService;
	
	@Autowired
	private IAppStatusService iAppStatusService;

	@Autowired
	private IBloginInfoService iBloginInfoService ;
	@Autowired
	private IBAppUpdateService iBAppUpdateService;
	
	@Autowired
	private BloginInfoService bloginInfoService;
	@Autowired
	private OtsCareProducer careProducer;
	/**
	 * @param unionid
	 *            微信unionid,非必填（两项至少有一项）
	 * @param phone
	 *            手机号,非必填（两项至少有一项）
	 * @return
	 */
	@RequestMapping("/unionidandphone/check")
	public ResponseEntity<Map<String, Object>> check(String unionid, String channelid, String phone, String ostype,String _s) {
		logger.info("check(unionid:{}, phone:{}, ostype:{})", unionid, phone,
				ostype);
		
		
		// 3. 判断用户并获取token
		boolean check;
		String token;
		try {
			// 1. 校验参数
			if (Strings.isNullOrEmpty(unionid) && Strings.isNullOrEmpty(phone)) {
				throw MyErrorEnum.errorParm
						.getMyException("[微信(unionid)|手机号(phone)],至少包含一项参数.");
			}
			if (Strings.isNullOrEmpty(ostype)) {
				throw MyErrorEnum.errorParm
						.getMyException("[客户端类型(ostype)] 不允许为空.");
			}

			// 2. 根据unionid和手机检索用户信息
			logger.info("检索用户：{},{}", unionid, phone);
			
			//黑名单验证
			this.iMemberService.checkPhoneIsBlack(phone,"Member.check","check：checkPhoneIsBlack","您的账号已被冻结，如有疑问请拨打客服电话4001-888-733");
			Optional<UMember> ofMember = Optional.absent();
			if (!Strings.isNullOrEmpty(unionid) && !Strings.isNullOrEmpty(phone)) {
				ofMember = iMemberService.findMember(phone, unionid);
				UMember um = null;
				if (ofMember.isPresent()) {
					um = ofMember.get();
				}
				logger.info("根据微信id,手机号检索用户: unionid:{}, phone:{}, info:{}",
						unionid, phone, um);
			} else if (!Strings.isNullOrEmpty(unionid)) {
				ofMember = iMemberService.findMemberByUnionid(unionid);
				UMember um = null;
				if (ofMember.isPresent()) {
					um = ofMember.get();
				}
				logger.info("根据微信id检索用户：unionid:{}, info:{}", unionid, um);
			} else if (!Strings.isNullOrEmpty(phone)) {
				ofMember = iMemberService.findMemberByLoginName(phone);
				UMember um = null;
				if (ofMember.isPresent()) {
					um = ofMember.get();
				}
				logger.info("根据手机号检索用户：phone:{}, info:{}", phone, um);
			}

			check = false;
			token = "";
			if (ofMember.isPresent()) {
				UMember member = ofMember.get();
				unionid = member.getUnionid();
				phone = member.getLoginname();
				check = !Strings.isNullOrEmpty(unionid);
				TokenTypeEnum tokenType = TokenTypeEnum.PT;
				if (!Strings.isNullOrEmpty(ostype)) {
					if (OSTypeEnum.IOS.getId().equals(ostype)
							|| OSTypeEnum.ANDROID.getId().equals(ostype)) {
						tokenType = TokenTypeEnum.APP; // 手机app
					} else if (OSTypeEnum.WX.getId().equals(ostype)) {
						tokenType = TokenTypeEnum.WX; // 微信客户端
					} else {
						tokenType = TokenTypeEnum.PT; // 网页
					}
				}
				logger.info("tokentype:{}", tokenType);

				UToken uToken = iTokenService.findTokenByMId(member.getMid()
						.longValue(), tokenType);
				if (uToken == null) {
					uToken = MyTokenUtils.genAndCacheUToken(member.getMid(),
							tokenType, null); // 生成并缓存token
				}
				if (uToken != null) {
					token = uToken.getAccesstoken();
				}
				logger.info("mid:{}, tokentype:{}, token:{}. ", member.getMid(),
						tokenType.getName(), token);
				
				if(!Strings.isNullOrEmpty(channelid)){
					member.setOstype(ostype);
					member.setChannelid(channelid);
					iMemberService.saveOrUpdate(member); //更新用户channelid及客户类型
				}
			} else {
				logger.info("未检索到用户. unionid:{}, phone:{}", unionid, phone);
				token = "";
				unionid = "";
				phone = "";
				check = false;
			}
		} finally{
			try {
				BLoginInfo bLoginInfo=new BLoginInfo();
				bLoginInfo.setCreatetime(new Date());
				bLoginInfo.setPhone(phone);
				bLoginInfo.setUuid(unionid);
				bLoginInfo.setSecurity(_s);
				bloginInfoService.save(bLoginInfo);
				logger.info("记录登陆/注册信息成功：{}",bLoginInfo);
			} catch (Exception e) {
				logger.info("记录登陆/注册信息失败，{}",e.getMessage());
			}
		}

		// 4. 组织数据返回
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("check", check ? "T" : "F");// T表示已绑定，F表示未绑定
		rtnMap.put("token", token);
		rtnMap.put("unionid", unionid);
		rtnMap.put("phone", phone);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	
	/**
	 * 重写comeFromType字段
	 * @param comeFromType
	 * @param comeFrom
	 * @return
	 */
	private String rewriteComeFromType(String comeFromType,String comeFrom){
		//1.comefromtype若为“BQK”，且comefrom为空，则不保存comefromtype到u_member表
		if(StringUtils.isNotEmpty(comeFromType)
				&&ComefromtypeEnum.b_qieke.getType().equals(comeFromType)
				&&StringUtils.isEmpty(comeFrom)){
			return null;
		}
		return comeFromType;
	}
	/**
	 * @param unionid
	 *            微信unionid 非必填
	 * @param phone
	 *            手机号 必填
	 * @param sysno
	 *            设备号 非必填
	 * @param devicetype
	 *            机型 非必填
	 * @param marketsource
	 *            市场来源 非必填
	 * @param appversion
	 *            app版本 非必填
	 * @param ostype
	 *            系统类型 非必填，1-ios;2-安卓;3-微信
	 * @param osver
	 *            系统版本 非必填
	 * @param weixinname
	 *            微信用户昵称 非必填
	 * @param comefrom
	 *            来源人id 非必填
	 * @return    2015年11月13日13:52:35 invitationcode新加邀请码
	 */
	@RequestMapping("/unionidandphone/binding")
	public ResponseEntity<Map<String, Object>> binding(String unionid,
			String phone, String sysno, String devicetype, String marketsource,
			String appversion, String ostype, String osver, String weixinname,
			String comefrom, String comefromtype, Long hotelid, String channelid, String citycode,String invitationcode) {
		logger.info("/unionidandphone/binding...  unionid:{} phone:{} sysno:{} devicetype:{} marketsource:{} appversion:{} ostype:{} osver:{} weixinname:{} comefrom:{} comefromtype:{} hotelid:{} channelid:{} citycode:{} invitationcod:{}",
				unionid, phone,  sysno,  devicetype,  marketsource,appversion,  ostype,  osver,  weixinname, comefrom,  comefromtype,  hotelid,  channelid ,citycode,invitationcode
				);
		
		String checkerrortype = ""; // 绑定失败的原因，1、unionid已被其他手机号绑定，2、手机号已被其他unionid绑定
		String token = "";
		boolean isCheck = true;
		//判断用户是否是注册
		boolean isRegister = false;
		logger.info(">>>>binding weixinname:{}",weixinname);
		
		//1.comefromtype若为“BQK”，且comefrom为空，则不保存comefromtype到u_member表
		comefromtype = rewriteComeFromType(comefromtype, comefrom);
		if(weixinname!= null){
			try {
				if (weixinname.equals(new String(weixinname.getBytes("iso8859-1"), "iso8859-1"))){
					weixinname = new String(weixinname.getBytes("iso8859-1"),"utf-8");
				}
				logger.info("weixinname = {}" , weixinname);
				weixinname = CharUtils.toValid3ByteUTF8String(weixinname);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage() ,e);
			}
		}
		
		// 1. 参数校验
		if (Strings.isNullOrEmpty(phone) && Strings.isNullOrEmpty(unionid)) {
			throw MyErrorEnum.errorParm.getMyException("[手机号|微信] 至少有一项不允许为空.");
		}
		if (!Strings.isNullOrEmpty(phone)
				&& !ValidateUtils.isPhoneNumber(phone)) {
			throw MyErrorEnum.customError.getMyException("手机格式不正确.");
		}

		// 2.判断用户是否注册
		Optional<UMember> ofMember = Optional.absent();
		if (!Strings.isNullOrEmpty(phone) && Strings.isNullOrEmpty(unionid)) { // 手机注册
			logger.info("手机注册入口. phone:{}, unionid:{}", phone, unionid);
			ofMember = iMemberService.findMemberByLoginName(phone);
			if (ofMember.isPresent()) {
				logger.info("手机号已被注册. phone:{}, unionid:{}", phone, unionid);
				throw MyErrorEnum.customError.getMyException("手机号已被注册.");
			}
			UMember regMem = new UMember();
			regMem.setLoginname(phone);
			regMem.setName(phone);
			regMem.setPhone(phone);
			regMem.setCreatetime(new Date());
			regMem.setEnable(UMember.NORMAL_STATE);
			regMem.setChannelid(channelid);
			regMem.setDevicetype(devicetype);
			regMem.setMarketsource(marketsource);
			regMem.setAppversion(appversion);
			regMem.setOstype(ostype);
			regMem.setRegostype(ostype);
			regMem.setOsver(osver);
            if (StringUtils.isEmpty(invitationcode)){
                regMem.setComefrom(comefrom);
            } else{
                regMem.setComefrom(invitationcode);
            }
			regMem.setComefromtype(comefromtype);
			regMem.setHotelid(hotelid);
			regMem.setCitycode(citycode);
			iMemberService.saveOrUpdate(regMem);
			logger.info("手机注册用户. member:{}", regMem.toString());
			ofMember = Optional.fromNullable(regMem);

			// 调用发放券接口：发放新用户礼包 暂时停掉
			//iPromoService.genTicketByAllRegNewMember(regMem.getMid());
			logger.info("手机注册发放新用户礼包. mid:{}", regMem.getMid());
			isRegister = true;
		} else if (Strings.isNullOrEmpty(phone) && !Strings.isNullOrEmpty(unionid)) { // 微信注册
			logger.info("微信注册入口. phone:{}, unionid:{}", phone, unionid);
			ofMember = iMemberService.findMemberByUnionid(unionid);
			if (ofMember.isPresent()) {
				logger.info("微信号已被注册. phone:{}, unionid:{}", phone, unionid);
				throw MyErrorEnum.customError.getMyException("微信号已被注册.");
			}
			UMember regMem = new UMember();
			regMem.setWeixinname(weixinname);
			regMem.setCreatetime(new Date());
			regMem.setEnable(UMember.NORMAL_STATE);
			regMem.setChannelid(channelid);
			regMem.setDevicetype(devicetype);
			regMem.setMarketsource(marketsource);
			regMem.setAppversion(appversion);
			regMem.setOstype(ostype);
			regMem.setRegostype(ostype);
			regMem.setOsver(osver);
			regMem.setComefrom(comefrom);
			regMem.setComefromtype(comefromtype);
			regMem.setHotelid(hotelid);
			regMem.setUnionid(unionid);
			regMem.setCitycode(citycode);
			iMemberService.saveOrUpdate(regMem);
			logger.info("微信注册用户. member:{}", regMem.toString());
			ofMember = Optional.fromNullable(regMem);

			// 调用发放券接口：发放新用户礼包  暂时停掉
			//iPromoService.genTicketByAllRegNewMember(regMem.getMid());
			logger.info("微信注册发放新用户礼包. mid:{}", regMem.getMid());
			isRegister = true;
		} else if (!Strings.isNullOrEmpty(phone) && !Strings.isNullOrEmpty(unionid)) {
			if (!Strings.isNullOrEmpty(ostype)
					&& ("3".equals(ostype) || "2".equals(ostype) || "1".equals(ostype))) {
				logger.info("微信/安卓注册入口. phone:{}, unionid:{}, ostype:{}",
						phone, unionid, ostype);
				// 1. 微信注册(微信号与手机号一起注册)
				// 1.1 微信是否注册
				ofMember = iMemberService.findMemberByUnionid(unionid);
				if (ofMember.isPresent()) {
					throw MyErrorEnum.customError.getMyException("微信号已被注册.");
				}
				// 1.2 绑定手机号是否注册
				ofMember = iMemberService.findMemberByLoginName(phone);
				if (ofMember.isPresent()) {
					if (Strings.isNullOrEmpty(ofMember.get().getUnionid())) {
						UMember upMem = new UMember();
						upMem.setMid(ofMember.get().getMid());
						upMem.setUnionid(unionid);
						upMem.setChannelid(channelid);
						upMem.setDevicetype(devicetype);
						upMem.setMarketsource(marketsource);
						upMem.setAppversion(appversion);
						upMem.setOstype(ostype);
						upMem.setOsver(osver);
						upMem.setCitycode(citycode);
						iMemberService.saveOrUpdate(upMem);
						ofMember = Optional.fromNullable(upMem);
						isCheck = true;
					} else if (ofMember.get().getUnionid().equals(unionid)) {
						isCheck = true;
					} else if (!ofMember.get().getUnionid().equals(unionid)) {
						checkerrortype = "2";
						isCheck = false;
					}
				} else {
					isCheck = true;
					UMember regMem = new UMember();
					regMem.setWeixinname(weixinname);
					regMem.setLoginname(phone);
					regMem.setPhone(phone);
					regMem.setCreatetime(new Date());
					regMem.setEnable(UMember.NORMAL_STATE);
					regMem.setChannelid(channelid);
					regMem.setDevicetype(devicetype);
					regMem.setMarketsource(marketsource);
					regMem.setAppversion(appversion);
					regMem.setOstype(ostype);
					regMem.setRegostype(ostype);
					regMem.setOsver(osver);
					regMem.setComefrom(comefrom);
					regMem.setComefromtype(comefromtype);
					regMem.setHotelid(hotelid);
					regMem.setUnionid(unionid);
					regMem.setCitycode(citycode);
					iMemberService.saveOrUpdate(regMem);
					ofMember = Optional.fromNullable(regMem);

                    //  暂时停掉
					// iPromoService.genTicketByAllRegNewMember(regMem.getMid());
					logger.info("微信注册发放新用户礼包. mid:{}", regMem.getMid());
					isRegister = true;
				}
			} else {
				// 2. App绑定微信(先注册后绑定)
				ofMember = iMemberService.findMemberByLoginName(phone);
				if (!ofMember.isPresent()) {
					throw MyErrorEnum.customError
							.getMyException("手机号用户不存在,无法绑定微信.");
				}
				UMember um = ofMember.get();
				if (!Strings.isNullOrEmpty(um.getUnionid())) {
					isCheck = true;
				} else {
					Optional<UMember> openMember = iMemberService
							.findMemberByUnionid(unionid);
					if (openMember.isPresent()) {
						checkerrortype = "1";
						isCheck = false;
					} else {
						isCheck = true;
						UMember upMem = new UMember();
						upMem.setMid(um.getMid());
						upMem.setUnionid(unionid);
						upMem.setChannelid(channelid);
						upMem.setDevicetype(devicetype);
						upMem.setMarketsource(marketsource);
						upMem.setAppversion(appversion);
						upMem.setOstype(ostype);
						upMem.setOsver(osver);
						upMem.setCitycode(citycode);
						iMemberService.saveOrUpdate(upMem);
						ofMember = Optional.fromNullable(upMem);
					}
				}
			}
		}
		if (!ofMember.isPresent()) {
			throw MyErrorEnum.customError.getMyException("注册用户失败.");
		}

		logger.info("注册用户时,开始获取token. ostype:{}", ostype);
		TokenTypeEnum tokenType = TokenTypeEnum.PT;
		if (!Strings.isNullOrEmpty(ostype)) {
			if (OSTypeEnum.IOS.getId().equals(ostype)
					|| OSTypeEnum.ANDROID.getId().equals(ostype)) {
				tokenType = TokenTypeEnum.APP; // 手机app
			} else if (OSTypeEnum.WX.getId().equals(ostype)) {
				tokenType = TokenTypeEnum.WX; // 微信客户端
			} else {
				tokenType = TokenTypeEnum.PT; // 网页
			}
		}
		logger.info("tokentype:{}", tokenType);

		UMember um = ofMember.get();
		if(isRegister){
			try {
				boolean isActivity = false;
				if(StringUtils.isNotEmpty(comefromtype)){
					if(ComefromtypeEnum.huodong.getType().equals(comefromtype)){
						isActivity = true;
					}
				}
				if(!isActivity){
					Message message = new Message();
					message.setMid(um.getMid());
					message.setCopywriterTypeEnum(CopywriterTypeEnum.register);
					logger.info("注册发送消息的Message对象："+message);
					careProducer.sendSmsMsg(message);
					careProducer.sendAppMsg(message);
					careProducer.sendWeixinMsg(message);
				}
			} catch (Exception e) {
				logger.error("发送消息异常",e);
			}
		}
		UToken uToken = iTokenService.findTokenByMId(um.getMid().longValue(),
				tokenType);
		if (uToken == null) {
			uToken = MyTokenUtils.genAndCacheUToken(um.getMid(), tokenType,
					null);
		}
		if (uToken != null && isCheck) {
			token = uToken.getAccesstoken();
		}

		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("check", isCheck ? "T" : "F");
		rtnMap.put("token", token);
		rtnMap.put("checkerrortype", checkerrortype);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 解绑微信id
	 * @param token 用户token
	 * @return
	 */
	@RequestMapping("/unionid/relive")
	public ResponseEntity<Map<String, Object>> relive(String token) {
		UMember member = new UMember();
		member.setMid(MyTokenUtils.getMidByToken(token));
		member.setUnionid("");
		this.iMemberService.saveOrUpdate(member);
		
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	/**
	 * @param sysno
	 *            系统号 必填
	 * @param token
	 *            用户 非必填
	 * @param phone
	 *            手机号 非必填
	 * @param userlongitude
	 *            用户坐标(经度) 非必填
	 * @param userlatitude
	 *            用户坐标(纬度) 非必填
	 * @param runningstatus
	 *            app开启状态 必填1-前台；2-后台
	 * @param runningpage
	 *            打开页表示 非必填，字符串，用于记录用户当前打开的页面
	 * @return
	 */
	@RequestMapping("/appstatus/push")
	public ResponseEntity<Map<String, Object>> collectAppStatus(HttpServletRequest request) {
		//获取参数
		String sysno = request.getParameter("sysno");
		String token = request.getParameter("token");
		String phone = request.getParameter("phone");
		String userlongitude = request.getParameter("userlongitude");
		String userlatitude = request.getParameter("userlatitude");
		String runningstatus = request.getParameter("runningstatus");
		String runningpage = request.getParameter("runningpage");
		
		String uuid = request.getParameter("uuid");
		String deviceimei = request.getParameter("deviceimei");
		String simsn = request.getParameter("simsn");
		String wifimacaddr = request.getParameter("wifimacaddr");
		String blmacaddr = request.getParameter("blmacaddr");
		logger.info("加密传来的参数 uuid:{},deviceimei:{},simsn:{},wifimacaddr:{},blmacaddr:{}", uuid,deviceimei,simsn,wifimacaddr,blmacaddr);
		if (Strings.isNullOrEmpty(sysno)) {
			throw MyErrorEnum.errorParm.getMyException("系统号[sysno] 不允许为空.");
		}
		if (Strings.isNullOrEmpty(runningstatus)) {
			throw MyErrorEnum.errorParm.getMyException("app开启状态[runningstatus] 不允许为空.");
		}
		Long mid = null;
		if (!Strings.isNullOrEmpty(token)) {
			mid = MyTokenUtils.getMidByToken(token);
		}

		AppStatus appStatus = new AppStatus();
		appStatus.setSysno(sysno);
		appStatus.setMid(mid);
		appStatus.setPhone(phone);
		appStatus.setUserlatitude(userlatitude);
		appStatus.setUserlongitude(userlongitude);
		appStatus.setRunningpage(runningpage);
		appStatus.setCreatetime(new Date());
		appStatus.setRunningstatus(Integer.valueOf(runningstatus));
		
		// add by zhangyajun 20130713 start
		//iAppStatusDao.insert(appStatus); //zyj 变为由service调用
		iAppStatusService.save(appStatus);
		logger.info("保存app推送状态id.{}", appStatus.getId());
		if (appStatus.getId()==null) {
			throw MyErrorEnum.errorParm.getMyException("保存app推送状态失败");
		}
		//手机号和用户mid必须有一个不为空
		if (!Strings.isNullOrEmpty(phone)||mid!= null) {
		BLoginInfo  bLoginInfo = new BLoginInfo();
		if (!Strings.isNullOrEmpty(phone)&&phone.startsWith("+86")) {
			phone = phone.substring(3);
		}
		bLoginInfo.setPhone(phone);
		bLoginInfo.setMid(mid);
		bLoginInfo.setSysno(sysno);
		if (StringUtils.isNotEmpty(uuid)) {
			bLoginInfo.setUuid(DESUtils.decryptDES(uuid));
		}
		if (StringUtils.isNotEmpty(deviceimei)) {
			bLoginInfo.setDeviceimei(DESUtils.decryptDES(deviceimei));
		}
		if (StringUtils.isNotEmpty(simsn)) {
			bLoginInfo.setSimsn(DESUtils.decryptDES(simsn));
		}
		if (StringUtils.isNotEmpty(wifimacaddr)) {
			bLoginInfo.setWifimacaddr(DESUtils.decryptDES(wifimacaddr));
		}
		if (StringUtils.isNotEmpty(blmacaddr)) {
			bLoginInfo.setBlmacaddr(DESUtils.decryptDES(blmacaddr));
		}
		
		bLoginInfo.setCreatetime(new Date());
		logger.info("插入登录日志表的对象信息.{}", bLoginInfo);
		iBloginInfoService.save(bLoginInfo);
		if (bLoginInfo.getId()==null) {
			throw MyErrorEnum.errorParm.getMyException("保存登录日志出错");
		}
		}
		// add by zhangyajun 20130713 end
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	
	/**
	 * 获取是app是否有新版更新
	 * @return
	 */
	@RequestMapping("/app/checkupdate")
	public ResponseEntity<Map<String, Object>> checkupdate() {
		List<BAppUpdate> bAppUpdates = iBAppUpdateService.findAllRecord();
		Map<String, Object> rtnMap = Maps.newHashMap();
		
		if (CollectionUtils.isNotEmpty(bAppUpdates)) {
			rtnMap.put("success", true);
			rtnMap.put("update", bAppUpdates.get(0).getAppupdate());
		}else {
			rtnMap.put("success", false);
			rtnMap.put("update", "");
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	
}
