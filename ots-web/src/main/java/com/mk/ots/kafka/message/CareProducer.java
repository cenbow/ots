package com.mk.ots.kafka.message;

import com.mk.care.kafka.model.AppMessage;
import com.mk.care.kafka.model.SmsMessage;
import com.mk.care.kafka.model.WeixinMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.care.kafka.model.Message;
import com.mk.kafka.client.stereotype.MkTopicProducer;


/**
 * 关怀系统的生产者
 * @author tankai
 *
 */
public class CareProducer {

	private static final Logger logger = LoggerFactory.getLogger(CareProducer.class);

	/**
	 * 发送短信的topic
	 * @param value
	 */
	@MkTopicProducer(topic = "CareSmsMsg", serializerClass = "com.mk.kafka.client.serializer.SerializerEncoder",replicationFactor = 1)
	public void sendSmsMsg(Message message) {
		logger.info("发送短信成功，"+message.toString());
    }
	/**
	 * push app message
	 * @param message
	 */
	@MkTopicProducer(topic = "CareAppMsg", serializerClass = "com.mk.kafka.client.serializer.SerializerEncoder",replicationFactor = 1)
	public void sendAppMsg(Message message) {
		logger.info("push app消息成功，"+message.toString());
	}
	

	/**
	 * 发送微信消息
	 * @param message
	 */
	@MkTopicProducer(topic = "CareWeixinMsg", serializerClass = "com.mk.kafka.client.serializer.SerializerEncoder",replicationFactor = 1)
	public void sendWeixinMsg(Message message) {
		logger.info("发送微信消息成功，"+message.toString());
	}

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

}
