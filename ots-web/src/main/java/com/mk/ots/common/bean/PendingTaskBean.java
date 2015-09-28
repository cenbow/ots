/**
 * @author jianghe
 */
package com.mk.ots.common.bean;

/**
 * 待处理任务
 */
public class PendingTaskBean {
	/**
	 * 路径
	 */
	private String url;
	/**
	 * 参数json
	 */
	private String paramsJson;

	public String getParamsJson() {
		return paramsJson;
	}

	public void setParamsJson(String paramsJson) {
		this.paramsJson = paramsJson;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
