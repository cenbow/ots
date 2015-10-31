package com.mk.ots.message.service;

import com.mk.ots.common.enums.MessageTypeEnum;
import com.mk.ots.message.model.LMessageLog;
import com.mk.ots.message.model.LPushLog;

import java.util.Date;
import java.util.List;

/**
 * 消息通知业务类
 * <p/>
 * 主要针对短信及push业务进行实现.
 */
public interface IMessageService {

	boolean pushMsg(String phone, String title, String msgContent, String msgtype);

	boolean pushMsg(String phone, String title, String msgContent, String msgtype, String url);

	boolean pushMsg(String phone, String title, String msgContent, String msgtype, String url, Long activeid);

	boolean sendMsg(Long msgid, String phone, String msgContent, MessageTypeEnum messageTypeEnum, String ip);

	List<LPushLog> query(LPushLog pushlog);

	long findUnreadcount(LPushLog pushlog);

	/**
	 * 推送消息给单个用户
	 *
	 * @param phone      手机号
	 * @param title      标题
	 * @param msgContent 内容
	 * @param msgtype    推送类型
	 * @param url        url
	 * @param pushid     消息id
	 * @return 成功与否
	 */
	boolean PushMsgToSingle(String phone, String title, String msgContent, String msgtype, String url, String pushid);

	/**
	 * 推送消息给所有用户
	 *
	 * @param title      标题
	 * @param msgContent 内容
	 * @param msgtype    消息类型
	 * @param url        url
	 * @param pushid     推送id
	 * @return 成功与否?
	 */
	boolean pushMsgToAll(String title, String msgContent, String msgtype, String url, String pushid);

	/**
	 * 推送消息给指定用户组
	 *
	 * @param usergroupid 用户组id
	 * @param title       标题
	 * @param msgContent  内容
	 * @param msgtype     消息类型
	 * @param url         url
	 * @param pushid      消息id
	 * @return 成功与否?
	 */
	boolean pushMsgToGroup(String usergroupid, String title, String msgContent, String msgtype, String url, String pushid);

	/**
	 * 修改阅读状态
	 *
	 * @param mid        会员不id
	 * @param ids        键列表
	 * @param readstatus 状态
	 * @return 成功与否
	 */
	boolean modifyAlreadyRead(Long mid, List<Long> ids, String readstatus);

	/**
	 * 修改所有消息状态
	 *
	 * @param mid        用户id
	 * @param readstatus 消息状态
	 * @return 成功与否？
	 */
	boolean modifyAlreadyReadAll(Long mid, String readstatus);

	/**
	 * 记录短信日志
	 *
	 * @param phone           手机号
	 * @param msgContent      内容
	 * @param messageTypeEnum 消息类型
	 * @param source          来源
	 * @param ip              ip
	 * @return 日志id
	 */
	Long logsms(String phone, String msgContent, MessageTypeEnum messageTypeEnum,
				String source, String ip, String provider,Boolean result);

	/**
	 * 回写短信状态
	 *
	 * @param msgid        消息id
	 * @param reportstatus 回执状态
	 * @param reporttime   回执时间
	 */
	void rewriteReport(Long msgid, boolean reportstatus, String reporttime,String provider);

	/**
	 * 根据以下参数条件查询记录数
	 *
	 * @param phone     手机号
	 * @param message   消息
	 * @param time      时间
	 * @param timeSpace timeSpace
	 * @return 记录条数
	 */
	Long findCountByPhoneAndMsg(String phone, String message, Date time, long timeSpace);

	Long findActiveCount(LPushLog lPushLog);
	
	/**
	 * 生成验证码
	 * @return 验证码字符串
	 */
	public String generateVerifyCode(String phone);
	
	/**
	 * 验证验证码是否正确
	 * @param phone
	 * @param type
	 * @param message
	 * @return 验证码匹配则返回：true
	 */
	public boolean checkVerifyCode(String phone,String code);
	
	LMessageLog findMsgById(Long id);
	
	boolean reSendMsg(Long msgid,String phone, String msgContent, MessageTypeEnum messageTypeEnum, String ip,Date sendDate,String ExceptProvider);
	
	public boolean sendCode(Long msgid, String phone, String msgContent, MessageTypeEnum messageTypeEnum, String ip, String citycode);
}
