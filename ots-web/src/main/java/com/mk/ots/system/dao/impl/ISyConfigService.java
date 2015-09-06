package com.mk.ots.system.dao.impl;

public interface ISyConfigService {

	public abstract void update(String key, String type, String value);

	public abstract String findValue(String key, String type);
	
}
