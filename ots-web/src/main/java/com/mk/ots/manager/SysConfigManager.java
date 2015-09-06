package com.mk.ots.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.framework.AppUtils;
import com.mk.ots.system.service.impl.SysConfigService;

public class SysConfigManager {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final SysConfigManager instance = new SysConfigManager();

	/**
     *
     */
	private SysConfigManager() {
	}

	/**
	 *
	 * @return
	 */
	public static SysConfigManager getInstance() {
		return SysConfigManager.instance;
	}

	/**
	 *
	 * @param stype
	 * @param skey
	 * @return
	 */
	public String readOne(String stype, String skey) {
		SysConfigService configService = AppUtils.getBean(SysConfigService.class);
		return configService.getSysConfig(stype, skey);
	}

	/**
	 *
	 */
	public void loadSysConfig() {
		SysConfigService configService = AppUtils.getBean(SysConfigService.class);
		if (configService != null) {
			configService.loadAll();
		} else {
			this.logger.error("load system config cache data error.");
		}
	}

}
