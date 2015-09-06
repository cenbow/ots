package com.mk.framework.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nolan
 *
 */
public class ThreadUtil {
	@SuppressWarnings("unchecked")
	private static ThreadLocal tl = new ThreadLocal();

	private static Map threadBean() {
		Map iBean = (Map) tl.get();
		if (iBean == null) {
			iBean = new HashMap();
			tl.set(iBean);
		}
		return iBean;
	}

	public static Object threadVar(String key) {
		return threadBean().get(key);
	}

	public static void setThreadVar(String key, Object obj) {
		threadBean().put(key, obj);
	}
}