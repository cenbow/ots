package com.mk.ots.kafka.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mk.care.kafka.model.Message;
import com.mk.framework.util.Config;


/**
 * ots发送关怀消息的生产者
 * @author tankai
 *
 */
public class OtsCareProducer {
    @Autowired
    private CareProducer careProducer;
    private static final Logger logger = LoggerFactory.getLogger(OtsCareProducer.class);

	/**
	 * 发送短信的topic
	 * @param value
	 */
    public void sendSmsMsg(Message message) {
    	try {
    		if(isOpen()){
    			careProducer.sendSmsMsg(message);
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
    public void sendAppMsg(Message message) {
    	try {
    		if(isOpen()){
    			careProducer.sendAppMsg(message);
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
    public void sendWeixinMsg(Message message) {
    	try {
    		if(isOpen()){
    			careProducer.sendWeixinMsg(message);
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
