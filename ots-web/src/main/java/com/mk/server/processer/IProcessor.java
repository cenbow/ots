package com.mk.server.processer;

import io.netty.channel.Channel;

import com.mk.synserver.DirectiveData;

public interface IProcessor {

	public void process(Channel channel, DirectiveData dd);
}
