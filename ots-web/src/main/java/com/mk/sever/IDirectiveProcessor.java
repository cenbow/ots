package com.mk.sever;

import com.mk.server.processer.IProcessor;

public interface IDirectiveProcessor {

	public int getDirectiveKey();

	public IProcessor getProcessor();
}
