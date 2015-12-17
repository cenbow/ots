package com.mk.ots.kafka.message;

import com.mk.care.kafka.model.AppMessage;
import com.mk.care.kafka.model.Message;
import com.mk.care.kafka.model.SmsMessage;
import com.mk.care.kafka.model.WeixinMessage;
import com.mk.framework.util.Config;
import com.mk.kafka.client.stereotype.MkTopicProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 消息生产者
 * @author
 *
 */
public class MessageProducer {

	private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

	/**
	 * 发送微信消息
	 * @param message
	 */
	@MkTopicProducer(topic = "RemindWeixinMsg", serializerClass = "com.mk.kafka.client.serializer.SerializerEncoder",replicationFactor = 1)
	public void pushWeixinMsg(WeixinMessage message) {
		logger.info("发送微信消息成功，"+message.toString());
	}

	/**
	 * 发送APP消息
	 * @param appMessage
	 */
	@MkTopicProducer(topic = "RemindAppMsg", serializerClass = "com.mk.kafka.client.serializer.SerializerEncoder",replicationFactor = 1)
	public void pushAppMsg(AppMessage appMessage){
		logger.info("发送App消息成功，"+appMessage.toString());
	}

	/**
	 * 发送短信消息
	 * @param smsMessage
	 */
	@MkTopicProducer(topic = "RemindSmsMsg", serializerClass = "com.mk.kafka.client.serializer.SerializerEncoder",replicationFactor = 1)
	public void pushSmsMsg(SmsMessage smsMessage) {
		logger.info("发送sms消息：" + smsMessage.getMessage());
	}

	/**
	 * 发送短信的topic
	 * @param message
	 */
	public void sendSmsMsg(SmsMessage message) {
		try {
			if(isOpen()){
				this.pushSmsMsg(message);
			}else{
				logger.info("kafka消息关闭");
			}
		} catch (Exception e) {
			logger.error("发送kafka消息异常",e);
		}
	}
	/**
	 * push app message
	 * @param message
	 */
	public void sendAppMsg(AppMessage message) {
		try {
			if(isOpen()){
				this.pushAppMsg(message);

			}else{
				logger.info("kafka消息关闭");
			}
		} catch (Exception e) {
			logger.error("发送kafka消息异常",e);
		}
	}


	/**
	 * 发送微信消息
	 * @param message
	 */
	public void sendWeixinMsg(WeixinMessage message) {
		try {
			if(isOpen()){
				this.pushWeixinMsg(message);
			}else{
				logger.info("kafka消息关闭");
			}
		} catch (Exception e) {
			logger.error("发送kafka消息异常",e);
		}
	}


	private boolean isOpen(){
		if(Config.getValue("kafka.switch") == null || Config.getValue("kafka.switch").equals("0")){
			return true;
		}
		return false;
	}
}
