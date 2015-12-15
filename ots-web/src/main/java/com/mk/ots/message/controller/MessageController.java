package com.mk.ots.message.controller;

import cn.jpush.api.utils.StringUtils;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.component.message.ITips;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.NetUtils;
import com.mk.framework.util.ValidateUtils;
import com.mk.ots.common.enums.MessageTypeEnum;
import com.mk.ots.message.dao.IWhiteListDao;
import com.mk.ots.message.model.LPushLog;
import com.mk.ots.message.model.MessageType;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.system.dao.impl.ISyConfigService;
import org.apache.commons.collections.CollectionUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author nolan
 */
@Controller
@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {
	final Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Autowired
	private IMessageService iMessageService;

	@Autowired
	private ISyConfigService iSyConfigService;

	
	private Integer EXPIRESTIME=60;
	
	@Autowired
	private IWhiteListDao iWhiteDao;
	
	@RequestMapping(value="/message/report", produces = MediaType.TEXT_PLAIN_VALUE)

	@ResponseBody
	public String report(String args) throws Exception {
		logger.info("短信回执: {}", args);
		if (!Strings.isNullOrEmpty(args)) {
			List<String> itemList = Splitter.on(';').trimResults().omitEmptyStrings().splitToList(args);
			for (String tmp : itemList) {
				List<String> itemFieldList = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(tmp);
				if (itemFieldList != null && itemFieldList.size() == 6) {
					Long msgid = Long.parseLong(itemFieldList.get(3));
					boolean flag = "0".equals(itemFieldList.get(4)) || "DELIVRD".equals(itemFieldList.get(4)); //状态:（0或DELIVRD为成功，其它均为失败）
					String reporttime = itemFieldList.get(5);
					this.iMessageService.rewriteReport(msgid, flag, reporttime,null,itemFieldList.get(4).toString());
					/*//如果发送失败则需重发
					if (!flag) {
						LMessageLog log = iMessageService.findMsgById(msgid);

						String phone = itemFieldList.get(2);
						String provider = "";
						if (MessageTypeEnum.normal.getId()
								.equals(log.getType())) {
							provider = "com.mk.framework.component.message.SmsMessage";
							iMessageService.reSendMsg(msgid,phone, log.getMessage(),
									MessageTypeEnum.normal, log.getIp(),
									log.getTime(), provider);
						} else {
							provider = "com.mk.framework.component.message.VoiceMessage";
							iMessageService.reSendMsg(msgid,phone, log.getMessage(),
									MessageTypeEnum.audioMessage, log.getIp(),
									log.getTime(), provider);
						}
					}*/
				} else {
					logger.error("回执信息格式不正确. str:{}", itemFieldList);
				}
			}
		}
		return "0";
	}

	/**
	 * @param phone   手机号码,必填
	 * @param message 发送内容,必填
	 * @param type    非必填，默认为普通短信. 1——普通短信 , 2——语音消息
	 * @return
	 */
	@RequestMapping("/verifycode/send")
	public ResponseEntity<Map<String,Object>> send(HttpServletRequest request,String phone, String message, String type) throws Exception {
		//1. 校验参数
		if (Strings.isNullOrEmpty(phone)) {
			throw MyErrorEnum.errorParm.getMyException("[手机号码] 不允许为空.");
        }
		if (!ValidateUtils.isPhoneNumber(phone)) {
			throw MyErrorEnum.errorParm.getMyException("[手机号码] 格式不正确.");
		}
		if (Strings.isNullOrEmpty(message)) {
			throw MyErrorEnum.errorParm.getMyException("[发送内容] 不允许为空.");
        }
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.normal;
		if (!Strings.isNullOrEmpty(type)) {
			messageTypeEnum = MessageTypeEnum.getEnumById(type);
        }
		if (messageTypeEnum == null) {
			throw MyErrorEnum.customError.getMyException("无此消息类型.");
		}

        String source = "";
        String ip = "";
        //2. 发送短信
        //判断同一个手机号，在某固定时间段内只允许发x次
        String limitTimeLengthConf = this.iSyConfigService.findValue("msg_limit_length", "mikeweb");
        String limitNumConf = this.iSyConfigService.findValue("msg_limit_num", "mikeweb");
        logger.info("发送短信限制：在[{}]秒内只允许发送[{}]次.", limitTimeLengthConf, limitNumConf);
		int limitTimeLength = !Strings.isNullOrEmpty(limitTimeLengthConf) ? Integer.parseInt(limitTimeLengthConf) : 50;
		int limitNum = !Strings.isNullOrEmpty(limitNumConf) ? Integer.parseInt(limitNumConf) : 1;
		Map<String, Object> rtnMap = Maps.newHashMap();
		Long msgCount = iMessageService.findCountByPhoneAndMsg(phone, message.trim(), new java.util.Date(), limitTimeLength);
		logger.info("状态：此手机[{}]秒内发送短信[{}]次.", phone, msgCount);
        if (msgCount >= limitNum) {
			//3. 组织参数
			throw MyErrorEnum.customError.getMyException("在[60]秒内只允许发送[" + limitNumConf + "]次.");
		} else {
			Long msgid = iMessageService.logsms(phone, message, messageTypeEnum, source, ip, null, null);
			boolean sendMsg = iMessageService.sendMsg(msgid, phone, message.trim(), messageTypeEnum, ip);
			logger.info("发送短信: phone:{}, message:{}, messagetype:{}, success:{}", phone, message.trim(), messageTypeEnum.getName(), sendMsg);
			//3. 组织参数
			rtnMap.put("success", sendMsg);
		}

        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	
	
	@RequestMapping("/verifycode/sendcode")
	public ResponseEntity<Map<String,Object>> sendVerifyCode(String phone, String type, String callmethod, String callversion, String ip,String citycode) throws Exception {
		//1. 校验参数
        if(Strings.isNullOrEmpty(phone)){
            throw MyErrorEnum.errorParm.getMyException("[手机号码] 不允许为空.");
        }
        if(!ValidateUtils.isPhoneNumber(phone)){
        	 throw MyErrorEnum.errorParm.getMyException("[手机号码] 格式不正确.");
        }
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.normal;
        if(!Strings.isNullOrEmpty(type)){
            messageTypeEnum = MessageTypeEnum.getEnumById(type);
        }
        if(messageTypeEnum == null){
        	throw MyErrorEnum.customError.getMyException("无此消息类型.");
        }
        logger.info("开始发送短信: phone:{},messagetype:{}, callmethod:{},callversion:{}, ip:{}", phone, messageTypeEnum.getName(),callmethod, callversion,ip);
        String source = "";

        //2. 发送短信
       
        //判断同一个手机号，在某固定时间段内只允许发x次
        String limitTimeLengthConf = this.iSyConfigService.findValue("msg_limit_length", "mikeweb");
        String limitNumConf = this.iSyConfigService.findValue("msg_limit_num", "mikeweb");
        logger.info("发送短信限制：在{}秒内只允许获取{}次.", limitTimeLengthConf, limitNumConf);
		int limitTimeLength = !Strings.isNullOrEmpty(limitTimeLengthConf) ? Integer.parseInt(limitTimeLengthConf) : 50;
		int limitNum = !Strings.isNullOrEmpty(limitNumConf) ? Integer.parseInt(limitNumConf) : 1;
		Map<String, Object> rtnMap = Maps.newHashMap();
		Long msgCount = iMessageService.findCountByPhoneAndMsg(phone, null,new java.util.Date(),limitTimeLength);
        logger.info("状态：此手机[{}]秒内发送短信[{}]次.", phone, msgCount);
        if (msgCount >= limitNum) {
        	//3. 组织参数
        	throw MyErrorEnum.customError.getMyException("在60秒内只允许获取["+limitNumConf+"]次验证码.");
		}else {
			//生成验证码
			String message=iMessageService.generateVerifyCode(phone);
			
			 //写log
			 Long msgid = iMessageService.logsms(phone, message, messageTypeEnum, source, ip,null,null);
			 //message="您的眯客验证码是："+message+"（眯客弹指间有房间，保证低价、快速入住)";
		     boolean sendMsg = iMessageService.sendCode(msgid, phone, message.trim(), messageTypeEnum,ip,citycode);
		     logger.info("发送短信: phone:{}, message:{}, messagetype:{}, success:{}", phone, message.trim(), messageTypeEnum.getName(), sendMsg);
		     //3. 组织参数
			rtnMap.put("success", sendMsg);
		}

        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	
	@RequestMapping("/verifycode/sendwithip")
    public ResponseEntity<Map<String, Object>> sendWithIp(HttpServletRequest request, String phone, String message, String callmethod, String callversion) throws Exception {
        //1. 校验参数
        if (Strings.isNullOrEmpty(phone)) {
            throw MyErrorEnum.errorParm.getMyException("[手机号码] 不允许为空.");
        }
        if (!ValidateUtils.isPhoneNumber(phone)) {
            throw MyErrorEnum.errorParm.getMyException("[手机号码] 格式不正确.");
        }
        if (Strings.isNullOrEmpty(message)) {
            throw MyErrorEnum.errorParm.getMyException("[发送内容] 不允许为空.");
        }
        String ip = NetUtils.getIpAddr(request);
        logger.info("开始发送短信: phone:{}, callmethod:{},callversion:{}, ip:{}", phone, callmethod, callversion, ip);
        //默认按短信发送
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.normal;
        String source = "";
        //2.判断该ip是否具备发送短信资格
        String ipListString = this.iSyConfigService.findValue("msg_white_iplist", "sys");
        List ipsList = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(ipListString);
        if (ipsList == null || ipsList.size() == 0 || !ipsList.contains(ip)) {
            logger.info("IP地址({})不在可允许的发送列表({})中.", ip, ipsList);
            throw MyErrorEnum.customError.getMyException("该ip不允许发送短信！");
        }
        //3. 发送短信
        //判断同一个手机号，在某固定时间段内只允许发x次
        String limitTimeLengthConf = this.iSyConfigService.findValue("msg_limit_length", "mikeweb");
        String limitNumConf = this.iSyConfigService.findValue("msg_limit_num", "mikeweb");
        logger.info("发送短信限制：在[{}]秒内只允许发送[{}]次.", limitTimeLengthConf, limitNumConf);
        int limitTimeLength = !Strings.isNullOrEmpty(limitTimeLengthConf) ? Integer.parseInt(limitTimeLengthConf) : 50;
        int limitNum = !Strings.isNullOrEmpty(limitNumConf) ? Integer.parseInt(limitNumConf) : 1;
        Map<String,Object> rtnMap = Maps.newHashMap();
        Long msgCount = iMessageService.findCountByPhoneAndMsg(phone, message.trim(), new java.util.Date(), limitTimeLength);
        logger.info("状态：此手机[{}]秒内发送短信[{}]次.", phone, msgCount);
        if (msgCount >= limitNum) {
            //3. 组织参数
            throw MyErrorEnum.customError.getMyException("在[60]秒内只允许发送[" + limitNumConf + "]次.");
        } else {
            Long msgid = iMessageService.logsms(phone, message, messageTypeEnum, source, ip, null, null);
            boolean sendMsg = iMessageService.sendMsg(msgid, phone, message.trim(), messageTypeEnum, ip);
            logger.info("发送短信: phone:{}, message:{}, messagetype:{}, success:{}", phone, message.trim(), messageTypeEnum.getName(), sendMsg);
            //3. 组织参数
            rtnMap.put("success", sendMsg);
        }

        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }
	
	
	
	@RequestMapping("/verifycode/verify")
	public ResponseEntity<Map<String,Object>> verifyCode(String phone, String code, String callmethod, String callversion, String ip) throws Exception {
		//1. 校验参数
        if(Strings.isNullOrEmpty(phone)){
            throw MyErrorEnum.errorParm.getMyException("[手机号码] 不允许为空.");
        }
        if(!ValidateUtils.isPhoneNumber(phone)){
        	 throw MyErrorEnum.errorParm.getMyException("[手机号码] 格式不正确.");
        }
        if(Strings.isNullOrEmpty(code)){
            throw MyErrorEnum.errorParm.getMyException("[验证码内容] 不允许为空.");
        }
       
        logger.info("验证码校验开始: code:{},phone:{}, callmethod:{},callversion:{}, ip:{}",code, phone,callmethod, callversion,ip);
        String phoneLimit=iSyConfigService.findValue("ios_phone_check", "sys");
        boolean checkResult=false;
        
        //为了使IOS审核通过添加特定手机号与特定验证码
        boolean result=StringUtils.isNotEmpty(phoneLimit) &&phoneLimit.trim().equals(phone.trim()) &&code.trim().equals("1111");
        logger.info("phoneLimit：{},code:{}校验开始,结果：{}",phoneLimit,code,result);
        if (result) {
        	checkResult=true;
        	logger.info("IOS验证通过！");
		}else {
			checkResult=iMessageService.checkVerifyCode(phone, code);
		}
        
        Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", checkResult);
       
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	

	
	/**
	 * 根据reques请求获取客户ip地址
	 * @param request
	 * @return
	 */
	public String getRemoteHost(HttpServletRequest request){
	    String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getRemoteAddr();
	    }
	    return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	}

	/**
	 * 消息查询
	 *
	 * @param token     用户token，必填
	 * @param msgid     消息id，非必填
	 * @param msgtype   消息分类，非必填（1-用户消息，2-广播消息，空-全部）
	 * @param msgstatus 消息状态，非必填（1-未读，2-已读，空-全部）
	 * @return 返回数据格式
	 */
	@RequestMapping("/info/querylist")
	public ResponseEntity<Map<String, Object>> queryList(String token, String msgid, String msgtype, String msgstatus) {
		//1. 组织查询参数
		LPushLog pushlog = new LPushLog();
		pushlog.setMid(MyTokenUtils.getMidByToken(token));
		if (!Strings.isNullOrEmpty(msgid)) {
			pushlog.setId(Long.parseLong(msgid));
		}
		if (!Strings.isNullOrEmpty(msgtype)) {
			pushlog.setType(MessageType.getById(msgtype));
		}
		if (!Strings.isNullOrEmpty(msgstatus)) {
			pushlog.setReadstatus(Boolean.parseBoolean(msgstatus));
		}

		//2. 查询消息列表
		List<LPushLog> list = iMessageService.query(pushlog);
		pushlog.setReadstatus(Boolean.FALSE);
		//需要查询sql，获取未读消息数量
		long unreadcount = iMessageService.findUnreadcount(pushlog);

		//3. 组织数据
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("messages", list);
		//未读消息数量

		rtnMap.put("unreadcount", unreadcount);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 消息状态修改
	 *
	 * @param msgstatus 消息状态，必填（1-未读；2-已读）
	 * @param msgids    消息id集合
	 * @return 返回数据格式
	 */
	@RequestMapping("/info/modify")
	public ResponseEntity<Map<String, Object>> modifyStatus(String token, String msgstatus, String msgids) {
		//1. validate the params.
		if (Strings.isNullOrEmpty(msgstatus)) {
			throw MyErrorEnum.customError.getMyException("消息状态不允许为空.");
		}
		if (!"1".equals(msgstatus) && !"2".equals(msgstatus)) {
			throw MyErrorEnum.customError.getMyException("消息状态不正确.");
		}
		String msgboolean = "";
		if ("1".equals(msgstatus)) {
			msgboolean = "F";
		} else if ("2".equals(msgstatus)) {
			msgboolean = "T";
		}

		//2. invoke the service to modify the status.
		logger.info("修改消息状态. info: readstatus:{}, ids:{}.", msgboolean, msgids);
		Long mid = MyTokenUtils.getMidByToken(token);
		if (!Strings.isNullOrEmpty(msgids)) {
			List idsList = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(msgids);
			this.iMessageService.modifyAlreadyRead(mid, idsList, msgboolean);
		} else {
			this.iMessageService.modifyAlreadyReadAll(mid, msgboolean);
		}

		//3. organize the data.
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 消息推送
	 *
	 * @param token       用户token，必填
	 * @param msgtype     消息类型，必填（1-用户消息，2-广播消息）
	 * @param phone       手机号，非必填
	 * @param title       消息标题，必填
	 * @param text        参数：消息内容，必填
	 * @param url         参数：消息URL，非必填
	 * @param usergroupid 用户组id
	 * @param grouppushid push消息id
	 * @return 返回数据格式
	 */
	@RequestMapping(value = "info/push")
	public ResponseEntity<Map<String, Object>> push(String token, String msgtype, String phone, String title, String text, String url, String usergroupid, String grouppushid) {
		//1. validate the paramters
		if (Strings.isNullOrEmpty(title)) {
			throw MyErrorEnum.customError.getMyException("消息标题不允许为空.");
		}
		if (Strings.isNullOrEmpty(text)) {
			throw MyErrorEnum.customError.getMyException("消息内容不允许为空.");
		}
		if (Strings.isNullOrEmpty(msgtype)) {
			throw MyErrorEnum.customError.getMyException("消息类型不允许为空.");
		}
		
		//2. push mid via token.
		Long mid = MyTokenUtils.getMidByToken(token);
		//3.find if current mid is able to push message
		String avaiableMidString=this.iSyConfigService.findValue("push_white_list", "sys");
		logger.info("可发送push消息的用户列表：{}",avaiableMidString);
		boolean avaiable=false;
		if (!Strings.isNullOrEmpty(avaiableMidString)) {
			List midsList = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(avaiableMidString);
			if (CollectionUtils.isNotEmpty(midsList)) {
				for (Object object : midsList) {
					if (object.equals(mid.toString())) {
						avaiable=true;
					}
				}
			}
		}
		if (!avaiable) {
			throw MyErrorEnum.customError.getMyException("该用户不具备发送push消息资格！");
		}
		//4. push message via the messageservice.
		logger.info("push msg: mid:{}, msgtype:{}, phone:{}, title:{}, text:{}, usergroupid:{}, grouppushid:{}.", mid, msgtype, phone, title, text, usergroupid, grouppushid);
		if (ITips.PUSH_TYPE_SINGLE.equals(msgtype)) {            //单用户
			if (Strings.isNullOrEmpty(phone)) {
				throw MyErrorEnum.customError.getMyException("手机号不允许为空.");
			}
			if (!ValidateUtils.isPhoneNumber(phone)) {
				throw MyErrorEnum.customError.getMyException("手机号格式不正确.");
			}
			this.iMessageService.pushMsg(phone, title, text, msgtype, url);
		} else if (ITips.PUSH_TYPE_MULTY.equals(msgtype)) {    //广播
			this.iMessageService.pushMsgToAll(title, text, msgtype, url, grouppushid);
		} else if (ITips.PUSH_TYPE_USERGROUP.equals(msgtype)) { //用户组
			if (usergroupid==null) {
				throw MyErrorEnum.customError.getMyException("用户组ID不允许为空.");
			}
			if (grouppushid==null) {
				throw MyErrorEnum.customError.getMyException("发送ID不允许为空.");
			}
			this.iMessageService.pushMsgToGroup(usergroupid, title, text, "1", url, grouppushid);
		} else {
			throw MyErrorEnum.customError.getMyException("消息类型无此类型.");
		}


		//5. organize the data.
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
}
