package com.mk.framework.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

//@Component
public class KafkaProducer {

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

	@Autowired
	private MessageChannel inputToKafka = null;

	public KafkaProducer() {
		super();
	}

	public void sendMessage(String topic, String message) {
		try {
			this.inputToKafka.send(MessageBuilder.withPayload(message).setHeader(KafkaHeaders.TOPIC, topic).build());
		} catch (Exception e) {
			KafkaProducer.logger.error(String.format("Failed to send [ %s ] to topic %s ", message, topic), e);
		}
	}
}