package com.mk.framework.component.message.webservice;

public enum EncryptionType {
	/**
	 * 无加密
	 */
	NONE(null),
	/**
	 * base64-utf8加密
	 */
	BASE64_UTF8("BASE64_UTF8"),
	/**
	 * url-utf8编码
	 */
	URL_UTF8("URL_UTF8");

	private String desc;

	private EncryptionType(String iDesc) {
		this.desc = iDesc;
	}

	public String getDesc() {
		return desc;
	}

}
