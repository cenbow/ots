package com.mk.sever;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.server.processer.AbstractProcessor;
import com.mk.synserver.DirectiveData;

public class ServerChannel {

	private static Channel channel = null;
	
	private Logger logger = LoggerFactory.getLogger(ServerChannel.class);

	public static void setChannel(Channel channel) {
		ServerChannel.channel = channel;
	}

	public static Channel getChannel() {
		return ServerChannel.channel;
	}

	public static void sendKeyword(Long keywordId, String keyword) {
		DirectiveData dd = new DirectiveData();
		dd.setDirective(DirectiveSet.DIRECTIVE_SEND_KEYWORD);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(AbstractProcessor.SESSION_ID, AbstractProcessor.getSessionId());
		data.put(AbstractProcessor.KEYWORD_ID, keywordId);
		data.put(AbstractProcessor.KEYWORD, keyword);

		dd.setData(data);

		ServerChannel.getChannel().writeAndFlush(dd);
	}

	public static void batchSendKeyword(List<String> keywordList) {
		DirectiveData dd = new DirectiveData();
		dd.setDirective(DirectiveSet.DIRECTIVE_BATCH_SEND_KEYWORD);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(AbstractProcessor.SESSION_ID, AbstractProcessor.getSessionId());
		data.put(AbstractProcessor.KEYWORD_LIST, keywordList);

		dd.setData(data);

		ServerChannel.getChannel().writeAndFlush(dd);
	}

	/**
	 * 通知HMS议价
	 * 
	 * @param hotel
	 * @param orderId
	 */
	public static void bookHotel(Long orderId, Long userid) {
		DirectiveData dd = new DirectiveData();
		dd.setDirective(DirectiveSet.DIRECTIVE_BOOK_HOTEL);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(AbstractProcessor.SESSION_ID, AbstractProcessor.getSessionId());
		data.put("orderId", orderId);
		data.put("userId", userid);
		dd.setData(data);
		LoggerFactory.getLogger(ServerChannel.class).info("调用议价接口::"+data);
		ServerChannel.getChannel().writeAndFlush(dd);
	}
	
	/**
	 * 通知HMS
	 * 
	 * @param userIds
	 */
	public static void informHMS(List<Long> userIds, long orderId) {
		DirectiveData dd = new DirectiveData();
		dd.setDirective(DirectiveSet.DIRECTIVE_RECEIVED_ORDER);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(AbstractProcessor.SESSION_ID, AbstractProcessor.getSessionId());
		data.put(AbstractProcessor.USER_ID_LIST, userIds);
		data.put("orderId", orderId);
		dd.setData(data);
		LoggerFactory.getLogger(ServerChannel.class).info("向hms发送userIds::"+data);
		ServerChannel.getChannel().writeAndFlush(dd);
	}

}
