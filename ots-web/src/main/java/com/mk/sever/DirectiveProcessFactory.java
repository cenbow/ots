package com.mk.sever;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import com.mk.server.processer.BookHotelFailedProcessor;
import com.mk.server.processer.IProcessor;
import com.mk.server.processer.RequestIdentiferProcessor;
import com.mk.synserver.DirectiveData;

/**
 * 指令处理工厂.
 *
 * @author zhaoshengbo
 *
 */
public class DirectiveProcessFactory {

	private static Map<Integer, IProcessor> processorMap = null;

	static {
		DirectiveProcessFactory.processorMap = new HashMap<Integer, IProcessor>();
		DirectiveProcessFactory.processorMap.put(DirectiveSet.DIRECTIVE_REQUEST_IDENTIER, new RequestIdentiferProcessor());
		DirectiveProcessFactory.processorMap.put(DirectiveSet.DIRECTIVE_BOOK_HOTEL_FAILED, new BookHotelFailedProcessor());

	}

	public static IProcessor getProcess(int directive) {
		return DirectiveProcessFactory.processorMap.get(Integer.valueOf(directive));
	}

	public static void process(int directive, Channel channel) {
		DirectiveProcessFactory.getProcess(directive).process(channel, null);
	}

	public static void process(DirectiveData directiveData, Channel channel) {
		DirectiveProcessFactory.getProcess(directiveData.getDirective()).process(channel, directiveData);
	}

	public static void pushProcessor(IDirectiveProcessor directiveProcessor) {
		DirectiveProcessFactory.processorMap.put(directiveProcessor.getDirectiveKey(), directiveProcessor.getProcessor());
	}
}
