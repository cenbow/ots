package com.mk.pms.manager;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.ots.common.utils.SysConfig;

public class DebugManager {

	private static final Logger logger = LoggerFactory.getLogger(DebugManager.class);

	private static Boolean isDebug;
	private static Boolean isSaveXmlFile;

	public static boolean isSaveXmlFile() {
		if (DebugManager.isSaveXmlFile == null) {
			DebugManager.load();
		}
		return DebugManager.isSaveXmlFile;
	}

	public static boolean isDebug() {
		if (DebugManager.isDebug == null) {
			DebugManager.load();
		}
		return DebugManager.isDebug;
	}

	private static void load() {
		try {
			File file = new File(SysConfig.getInstance().getClasspath() + "/debug.properties");
			PropertiesUtil pro = new PropertiesUtil(file);
			DebugManager.isDebug = Boolean.parseBoolean(pro.getValue("debug_module"));
			DebugManager.isSaveXmlFile = Boolean.parseBoolean(pro.getValue("saveXmlFile"));
		} catch (Exception e) {
			DebugManager.isDebug = false;
			DebugManager.logger.error("", e);
		}

	}

}
