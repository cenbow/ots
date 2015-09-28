package com.mk.ots.rpc.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.mk.server.processer.IProcessor;
import com.mk.sever.DirectiveSet;
import com.mk.sever.IDirectiveProcessor;

@Component
public class HmsDirectiveProcessor implements IDirectiveProcessor {

	@Autowired
	@Qualifier(value = "hmsHotelProcessor")
	private IProcessor processor;

	@Override
	public int getDirectiveKey() {
		return DirectiveSet.DIRECTIVE_RECEIVED_KEYWORD;
	}

	@Override
	public IProcessor getProcessor() {
		return processor;
	}
}
