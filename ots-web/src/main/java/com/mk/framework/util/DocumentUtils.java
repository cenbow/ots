package com.mk.framework.util;

import org.elasticsearch.common.base.Strings;
import org.jdom.Element;

public class DocumentUtils {
	/**
	 * @param root
	 * @param eleName
	 * @return
	 */
	public static String getEleValueByName(Element root, String eleName) {
		Element ele = root.getChild(eleName);
		if (ele == null) {
			return null;
		}
		return ele.getAttributeValue("value");
	}
}
