package com.mk.framework.kafka;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
public class KafkaConsumer {

	private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

	public void processMessage(Map<String, Map<Integer, List<byte[]>>> msgs) {
		for (Map.Entry<String, Map<Integer, List<byte[]>>> entry : msgs.entrySet()) {
			KafkaConsumer.log.debug("Topic:" + entry.getKey());
			ConcurrentHashMap<Integer, List<byte[]>> messages = (ConcurrentHashMap<Integer, List<byte[]>>) entry.getValue();
			KafkaConsumer.log.debug("\n**** Partition: \n");
			Set<Integer> keys = messages.keySet();
			for (Integer i : keys) {
				KafkaConsumer.log.debug("p:" + i);
			}
			KafkaConsumer.log.debug("\n**************\n");
			Collection<List<byte[]>> values = messages.values();
			for (Iterator<List<byte[]>> iterator = values.iterator(); iterator.hasNext();) {
				List<byte[]> list = iterator.next();
				for (byte[] object : list) {
					String message = new String(object);
					System.out.println(message);
					KafkaConsumer.log.debug("Message: " + message);
					try {
						// this.kafkaService.receiveMessage(message);
					} catch (Exception e) {
						KafkaConsumer.log.error(String.format("Failed to process message %s", message));
					}
				}
			}

		}
	}

	public void processMessage1(Map<String, Map<Integer, List<byte[]>>> msgs) {
		for (Map.Entry<String, Map<Integer, List<byte[]>>> entry : msgs.entrySet()) {
			KafkaConsumer.log.debug("Topic:" + entry.getKey());
			ConcurrentHashMap<Integer, List<byte[]>> messages = (ConcurrentHashMap<Integer, List<byte[]>>) entry.getValue();
			KafkaConsumer.log.debug("\n**** Partition: \n");
			Set<Integer> keys = messages.keySet();
			for (Integer i : keys) {
				KafkaConsumer.log.debug("p:" + i);
			}
			KafkaConsumer.log.debug("\n**************\n");
			Collection<List<byte[]>> values = messages.values();
			for (Iterator<List<byte[]>> iterator = values.iterator(); iterator.hasNext();) {
				List<byte[]> list = iterator.next();
				for (byte[] object : list) {
					String message = new String(object);
					System.out.println(message);
					KafkaConsumer.log.debug("Message: " + message);
					try {
						// this.kafkaService.receiveMessage(message);
					} catch (Exception e) {
						KafkaConsumer.log.error(String.format("Failed to process message %s", message));
					}
				}
			}

		}
	}

}