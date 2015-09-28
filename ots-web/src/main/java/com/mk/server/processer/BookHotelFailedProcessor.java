package com.mk.server.processer;

import io.netty.channel.Channel;

import com.mk.synserver.DirectiveData;

/**
 * 预定酒店失败处理.
 *
 * @author zhaoshb
 *
 */
public class BookHotelFailedProcessor extends AbstractProcessor {

	@Override
	protected void handle(Channel channel, DirectiveData dd) {
		Long hotelId = (Long) this.getValue(dd, AbstractProcessor.USER_ID);
		String message = (String) this.getValue(dd, AbstractProcessor.MESSAGE);
		System.out.println(hotelId);
		System.out.println(message);
	}

}
