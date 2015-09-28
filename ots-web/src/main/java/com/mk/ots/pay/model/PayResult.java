package com.mk.ots.pay.model;

import java.util.Map;

public class PayResult {

	private String payMarking;
	
	private Map<String, Object> result;

	public String getPayMarking() {
		return payMarking;
	}

	public void setPayMarking(String payMarking) {
		this.payMarking = payMarking;
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public void setResult(Map<String, Object> result) {
		this.result = result;
	}
}
