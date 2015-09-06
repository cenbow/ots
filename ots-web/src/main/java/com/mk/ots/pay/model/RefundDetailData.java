package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 单笔数据集
 * 
 * “退款理由”长度不能大于256字节，“退款理由”中不能有“^”、“|”、“$”、“#”等影响detail_data格式的特殊字符；
 * detail_data中退款总金额不能大于交易总金额；
 * 一笔交易可以多次退款，退款次数最多不能超过99次，需要遵守多次退款的总金额不超过该笔交易付款金额的原则。
 * 
 */
public class RefundDetailData implements Serializable {

	private static final long serialVersionUID = 7910510812938528569L;

	private String traceId;
	private BigDecimal amount;
	private String reason;

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
