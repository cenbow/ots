package com.mk.ots.kafka.message;

import com.mk.care.kafka.model.AppMessage;
import com.mk.care.kafka.model.Message;
import com.mk.care.kafka.model.SmsMessage;
import com.mk.care.kafka.model.WeixinMessage;
import com.mk.framework.util.Config;
import com.mk.kafka.client.stereotype.MkTopicProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 消息生产者
 * @author
 *
 */
public class MessageProducer {

	private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

	@Autowired
	private CareProducer careProducer;

	/**
	 * 发送短信的topic
	 * @param message
	 */
	public void sendSmsMsg(SmsMessage message) {
		try {
			if(isOpen()){
				this.careProducer.pushSmsMsg(message);
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
				this.careProducer.pushAppMsg(message);

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
				this.careProducer.pushWeixinMsg(message);
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
