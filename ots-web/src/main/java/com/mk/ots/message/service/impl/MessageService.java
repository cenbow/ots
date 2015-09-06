package com.mk.ots.message.service.impl;

import com.ctc.wstx.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mk.framework.component.message.*;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.enums.MessageTypeEnum;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.member.dao.IMemberDao;
import com.mk.ots.member.model.UMember;
import com.mk.ots.message.dao.ILMessageLogDao;
import com.mk.ots.message.dao.ILPushLogDao;
import com.mk.ots.message.model.LMessageLog;
import com.mk.ots.message.model.LPushLog;
import com.mk.ots.message.model.MessageType;
import com.mk.ots.message.service.IMessageService;

import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author nolan
 */
@Service
public class MessageService implements IMessageService {
	final Logger logger = LoggerFactory.getLogger(MessageService.class);
	@Autowired
	private ILMessageLogDao iLMessageLogDao;
	@Autowired
	private ILPushLogDao ilPushLogDao;
	@Autowired
	private IMemberDao iMemberDao;

	@Autowired
	private OtsCacheManager cacheManager;
	
	/**
	 * 发送短信消息
	 * @param phone
	 * @param msgContent
	 * @param messageTypeEnum
	 * @return
	 */
	@Override
	public boolean sendMsg(Long msgid, String phone, String msgContent, MessageTypeEnum messageTypeEnum) {
		logger.info("send message: {}, {}, {}", phone, msgContent, messageTypeEnum);
		if (messageTypeEnum == null) {
			logger.error("短信类型错误. ");
			throw MyErrorEnum.customError.getMyException("短信类型错误.");
		}

		boolean rtnstatus = false;
		try {
			ITips message = null;
			if (messageTypeEnum == MessageTypeEnum.normal) {
				message = new SmsMessage();
			} else if (messageTypeEnum == MessageTypeEnum.audioMessage) {
				message = new VoiceMessage();
			}

			ITips setContent = message.setTitle("--").setReceivers(phone).setContent(msgContent).setMsgId(msgid);
			for (int i = 0, n = 2; i < n; i++) {
				rtnstatus = setContent.send();
				logger.info("发送短信响应结果:{}", rtnstatus);
				if (rtnstatus) {
					logger.info("发送短信成功.....{}", phone);
					break;
				} else {
					logger.info("第{}次重新发送短信....", i + 1);
				}
			}

			if (!rtnstatus) {
				logger.error("send message occur error. info: {}, {}, {}. ", phone, msgContent, messageTypeEnum);
			}
		} catch (Exception e) {
			logger.error("send message occur error. info: {}, {}, {}.", phone, msgContent, messageTypeEnum);
			e.printStackTrace();
		}

		return rtnstatus;
	}

	@Override
	public Long logsms(String phone, String msgContent, MessageTypeEnum messageTypeEnum, String source, String ip) {
		LMessageLog log = new LMessageLog();
		log.setMessage(msgContent);
		log.setPhone(phone);
		log.setSource(source);
		log.setIp(ip);
		log.setTime(new Date());
		log.setType(Integer.parseInt(messageTypeEnum.getId()));
		iLMessageLogDao.insert(log);
		return log.getId();
	}

	@Override
	public boolean pushMsg(String phone, String title, String msgContent, String msgtype) {
		return pushMsg(phone, title, msgContent, msgtype, null);
	}


	@Override
	public boolean PushMsg(String phone, String title, String msgContent, String msgtype, String url, Long activeid) {
		logger.info(">>>推送消息----------------------//开始");
		logger.info(">>> param: {}, {}, {}, {}, {}.", phone, title, msgContent, msgtype, url);

		boolean result = false;
		Optional<List<UMember>> of = Optional.absent();
		if (MessageType.USER.getId().equals(msgtype)) {
			of = iMemberDao.findPushMember(phone);
		} else if (MessageType.SYSTEM.getId().equals(msgtype)) {
			of = iMemberDao.findPushMember(null);
		}
		if (of.isPresent()) {
			try {
				ITips pushMessage = null;
				logger.info("推送用户:{}", of.get());
				for (UMember umember : of.get()) {
					if (OSTypeEnum.ANDROID.getId().equals(umember.getOstype())) {
						pushMessage = new AndroidPushMessage();
					} else if (OSTypeEnum.IOS.getId().equals(umember.getOstype())) {
						pushMessage = new IosPushMessage();
					}
					boolean sendflag = false;
					if (pushMessage != null && !Strings.isNullOrEmpty(umember.getChannelid())) {
						sendflag = pushMessage.setMsgtype(msgtype).setTitle(title).setReceivers(umember.getChannelid()).setUrl(url).setContent(msgContent).send();
					}

					LPushLog pushlog = new LPushLog();
					pushlog.setContent(msgContent);
					pushlog.setDeviceno(umember.getChannelid());
					pushlog.setDevicetype(umember.getDevicetype());
					pushlog.setFromip("");
					pushlog.setFromsource("");
					pushlog.setMid(umember.getId());
					pushlog.setPhone(umember.getPhone());
					pushlog.setReadstatus(false);
					pushlog.setTime(new Date());
					pushlog.setTitle(title);
					pushlog.setType(MessageType.getById(msgtype));
					pushlog.setUrl(url);
					pushlog.setActiveid(activeid);
					pushlog.setSuccess(sendflag);
					this.ilPushLogDao.insert(pushlog);
				}
				result = true;
			} catch (Exception e) {
				logger.error("push message occur error. info: {}, title, {}, {}.", phone, title, msgContent, msgtype);
				e.printStackTrace();
			}
		} else {
			logger.info("无可以push的用户.phone: {}, msgtype(1用户,2广播):{}", phone, msgtype);
		}
		logger.info(">>>推送消息----------------------//结束");
		return result;

	}
	

	@Override
	public boolean pushMsg(String phone, String title, String msgContent, String msgtype, String url) {
		return PushMsg(phone, title, msgContent, msgtype, url, null);
	}

	@Override
	public boolean PushMsgToSingle(String phone, String title, String msgContent, String msgtype, String url, String pushid) {
		logger.info(">>>推送单个用户开始title：{}， msgContent：{}， msgtype：{}， url：{}， pushid：{}",title,msgContent,msgtype,url,pushid);
		boolean result=false;
		Optional<List<UMember>> of = Optional.absent();
		of = iMemberDao.findPushMember(phone);
		if (of.isPresent()) {
			try {
				ITips pushMessage = null;
				logger.info("推送用户:{}", of.get());
				for (UMember umember : of.get()) {
					if (OSTypeEnum.ANDROID.getId().equals(umember.getOstype())) {
						pushMessage = new AndroidPushMessage();
					} else if (OSTypeEnum.IOS.getId().equals(umember.getOstype())) {
						pushMessage = new IosPushMessage();
					}
					boolean sendflag = false;
					if (pushMessage != null && !Strings.isNullOrEmpty(umember.getChannelid())) {
						sendflag = pushMessage.setMsgtype(msgtype).setTitle(title).setReceivers(umember.getChannelid()).setUrl(url).setContent(msgContent).send();
					}
					//记录pushlog
					LPushLog pushlog = new LPushLog();
					pushlog.setContent(msgContent);
					pushlog.setPushid(pushid);
					pushlog.setDeviceno(umember.getChannelid());
					pushlog.setDevicetype(umember.getDevicetype());
					pushlog.setFromip("");
					pushlog.setFromsource("");
					pushlog.setMid(umember.getId());
					pushlog.setPhone(umember.getPhone());
					pushlog.setReadstatus(false);
					pushlog.setTime(new Date());
					pushlog.setTitle(title);
					pushlog.setType(MessageType.getById(msgtype));
					pushlog.setUrl(url);
					//pushlog.setActiveid(activeid);
					pushlog.setSuccess(sendflag);
					this.ilPushLogDao.insert(pushlog);
				}
				result = true;
			} catch (Exception e) {
				logger.error("push message occur error. info: {}, title, {}, {}.", phone, title, msgContent, msgtype);
				e.printStackTrace();
			}
		}else {
			logger.info("根据phone获取用户失败");
		}
		logger.info(">>>推送消息----------------------//结束");
		return result;
	}



	@Override
	public boolean PushMsgToAll(String title, String msgContent,
			String msgtype, String url, String pushid) {
		boolean result=false;
		logger.info(">>>开始广播 msgtype(1用户,2广播):{}",msgtype);
		//分别向android与ios设备发送广播
		ITips androidPushMessage = new AndroidPushMessage();
		ITips iosPushMessage = new IosPushMessage();
		boolean sendflag = false;
		if (androidPushMessage != null && iosPushMessage != null) {
			try {
				sendflag = androidPushMessage.setMsgtype(msgtype)
						.setTitle(title).setUrl(url).setContent(msgContent)
						.send()
						&& iosPushMessage.setMsgtype(msgtype).setTitle(title)
								.setUrl(url).setContent(msgContent).send();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Optional<List<UMember>> of = Optional.absent();
			of = iMemberDao.findPushMember(null);
			if (of.isPresent()) {
				for (UMember uMember : of.get()) {
					//记录push消息发送记录
					LPushLog pushlog = new LPushLog();
					pushlog.setContent(msgContent);
					pushlog.setPushid(pushid);
					pushlog.setDeviceno(uMember.getChannelid());
					pushlog.setDevicetype(uMember.getDevicetype());
					pushlog.setFromip("");
					pushlog.setFromsource("");
					pushlog.setMid(uMember.getId());
					pushlog.setPhone(uMember.getPhone());
					pushlog.setReadstatus(false);
					pushlog.setTime(new Date());
					pushlog.setTitle(title);
					pushlog.setType(MessageType.getById(msgtype));
					pushlog.setUrl(url);
					pushlog.setSuccess(sendflag);
					this.ilPushLogDao.insert(pushlog);
				}
			}
			result = true;
			logger.info("推送广播结束！");
		}
		logger.info(">>>推送广播消息----------------------//结束");
		return result;
	}

	@Override
	public boolean PushMsgToGroup(String usergroupid, String title, String msgContent, String msgtype, String url, String pushid) {
		boolean result=false;
		logger.info(">>>开始组播消息 usergroupid：{}， title：{}， msgContent：{}， msgtype：{}， url：{}， pushid：{}",usergroupid,title,msgContent,msgtype,url,pushid);
		Optional<List<UMember>> of = Optional.absent();
		of=iMemberDao.findBindMemberByGroupid(usergroupid.toString());
		logger.info("推送成员列表："+of.toString());
		//根据组成员系统区别，分别生成android、ios对应的channelid列表
		StringBuffer androidString=new StringBuffer();
		StringBuffer iosString=new StringBuffer();
		if (of.isPresent()) {
			for (UMember umember : of.get()) {
				if (OSTypeEnum.ANDROID.getId().equals(umember.getOstype())) {
					androidString.append(umember.getChannelid()).append(",");
				}else if (OSTypeEnum.IOS.getId().equals(umember.getOstype())) {
					iosString.append(umember.getChannelid()).append(",");
				}
			}
			logger.info("androidChannelids:{}, iosChannelids:{}",androidString,iosString);
			boolean androidSendflag = false;
			boolean iosSendflag = false;
			try {
				if (androidString.length() > 0) {
					androidString.deleteCharAt(androidString.length() - 1);
					ITips androidPushMessage = new AndroidPushMessage();

					androidSendflag = androidPushMessage.setMsgtype(msgtype)
							.setTitle(title)
							.setReceivers(androidString.toString())
							.setUrl(url).setContent(msgContent).send();
				}
				if (iosString.length() > 0) {
					iosString.deleteCharAt(iosString.length() - 1);
					ITips iosPushMessage = new IosPushMessage();
					iosSendflag = iosPushMessage.setMsgtype(msgtype)
							.setTitle(title)
							.setReceivers(iosString.toString()).setUrl(url)
							.setContent(msgContent).send();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//批量记录log
			for (UMember umember : of.get()) {
				LPushLog pushlog = new LPushLog();
				pushlog.setPushid(pushid);
				pushlog.setGroupid(usergroupid);
				pushlog.setContent(msgContent);
				pushlog.setDeviceno(umember.getChannelid());
				pushlog.setDevicetype(umember.getDevicetype());
				pushlog.setFromip("");
				pushlog.setFromsource("");
				pushlog.setMid(umember.getId());
				pushlog.setPhone(umember.getPhone());
				pushlog.setReadstatus(false);
				pushlog.setTime(new Date());
				pushlog.setTitle(title);
				pushlog.setType(MessageType.getById(msgtype));
				pushlog.setUrl(url);
				if (OSTypeEnum.ANDROID.getId().equals(umember.getOstype())) {
					pushlog.setSuccess(androidSendflag);
				}else if (OSTypeEnum.IOS.getId().equals(umember.getOstype())) {
					pushlog.setSuccess(iosSendflag);
				}
				this.ilPushLogDao.insert(pushlog);
			}
		}else {
			logger.info("根据组id获取成员失败");
		}
		logger.info(">>>推送组播消息----------------------//结束");
		return result;
	}


	@Override
	public boolean modifyAlreadyRead(Long mid, List<Long> ids, String readstatus) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("ids", ids);
		param.put("readstatus", readstatus);
		return ilPushLogDao.update("modifyAlreadyRead", param) > 0;
	}

	@Override
	public boolean modifyAlreadyReadAll(Long mid, String readstatus) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("readstatus", readstatus);
		return ilPushLogDao.update("modifyAlreadyReadAll", param) > 0;
	}

	@Override
	public List<LPushLog> query(LPushLog pushlog) {
		return ilPushLogDao.find(pushlog);
	}

	@Override
	public void rewriteReport(Long msgid, boolean reportstatus, String reporttime) {
		logger.info("RewriteReport(回执信息) msgid:{}, reportstatus:{}, reporttime:{}", msgid, reportstatus, reporttime);
		LMessageLog message = new LMessageLog();
		message.setId(msgid);
		message.setSuccess(reportstatus);
		message.setReporttime(reporttime);
		this.iLMessageLogDao.update(message);
	}

	@Override
	public long findUnreadcount(LPushLog pushlog) {
		return ilPushLogDao.findCount(pushlog);
	}

	@Override
	public Long findCountByPhoneAndMsg(String phone, String message, Date time, long timeSpace) {
		LMessageLog msg = new LMessageLog();
		msg.setPhone(phone);
		msg.setMessage(message);
		msg.setTime(time);
		//id 只是将时间间隔临时的放在这个属性中，此时的ID不是主键的意思
		msg.setId(timeSpace);
		return this.iLMessageLogDao.findCount("selectCountByPhoneAndMsg", msg);
	}

	public Long findActiveCount(LPushLog lPushLog) {
		return ilPushLogDao.findActiveCount(lPushLog);
	}
	
	@Override
	public String generateVerifyCode(String phone){
		logger.info("开始生成验证码");
		String codeNumString="";
		String charSequence="";
		//缓存过期时间，默认值为60秒
		Integer expiresTimeInteger=60;
		Properties pro;
		try {
			pro = PropertiesLoaderUtils.loadAllProperties("/message.properties");
			codeNumString=pro.getProperty("message.code_num");
			charSequence=pro.getProperty("message.char_sequence");
			//如果读取内容为空，则设置默认值
			codeNumString=Strings.isNullOrEmpty(codeNumString)?"4":codeNumString;
			charSequence=Strings.isNullOrEmpty(charSequence)?"0123456789":charSequence;
			expiresTimeInteger=Integer.parseInt(pro.getProperty("message.expires_time"));
		} catch (Exception e) {
			throw MyErrorEnum.customError.getMyException("资源文件属性message.properties读取异常");
		}
		if (Strings.isNullOrEmpty(codeNumString)) {
			throw MyErrorEnum.customError
					.getMyException("资源文件属性message.code_num值失败");
		}
		if (Strings.isNullOrEmpty(charSequence)) {
			throw MyErrorEnum.customError
					.getMyException("资源文件属性message.char_sequence值失败");
		}
		logger.info("生成验证码长度为{}，所有验证码字符集为：{}，过期时间：{}",codeNumString,charSequence,expiresTimeInteger);
		String message=genCode(Long.parseLong(codeNumString), charSequence);
		
		 //将验证码存入缓存
		 String cacheName="ots";
		 cacheManager.setExpires(cacheName, phone, message, expiresTimeInteger);
		return message;
	}
	
	/**
	 * 随机生成验证码
	 * @param codeNum
	 * @param charSequence
	 * @return
	 */
	private String genCode(Long codeNum,String charSequence){
		StringBuffer verifyCode=new StringBuffer();
		char[] chars=charSequence.toCharArray();
		if (chars!=null && chars.length>0&&codeNum>0) {
			Random random = new Random();
			for (int i = 0; i < codeNum; i++) {
				String randomCharString=String.valueOf(chars[random.nextInt(chars.length)]);
				verifyCode.append(randomCharString);
			}
		}
		return verifyCode.toString();
	}

	@Override
	public boolean checkVerifyCode(String phone, String code) {
		 //2.验证验证码是否有效
        String cacheName="ots";
        String key=phone;
        String cacheCodeString="";
        try {
        	cacheCodeString=(String)cacheManager.getExpires(cacheName, key);
        	logger.info("缓存中验证码为：{}",cacheCodeString);
		} catch (Exception e) {
			logger.info("获取验证码失败，该key对应的验证码不存在或已过期");
		}
        boolean checkResult=false;
        if (Strings.isNullOrEmpty(cacheCodeString)) {
			logger.info("获取redis中验证码为空，key为{}",key);
		}else {
			if (cacheCodeString.equals(code)) {
	        	checkResult=true;
	        	//验证完成后删除缓存
	        	cacheManager.remove(cacheName, key);
	        	logger.info("验证码验证成功");
			}else {
				logger.info("缓存中验证码与传递过来的验证码不匹配，redis code：{}，phone code：{}",cacheCodeString,code);
			}
		}
        return checkResult;
	}
}
