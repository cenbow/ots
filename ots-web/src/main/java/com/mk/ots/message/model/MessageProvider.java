package com.mk.ots.message.model;

public class MessageProvider {
	private Long id;
	private String providername;
	private Long weight;
	private String providerclass;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProvidename() {
		return providername;
	}
	public void setProvidename(String providename) {
		this.providername = providename;
	}
	public Long getWeight() {
		return weight;
	}
	public void setWeight(Long weight) {
		this.weight = weight;
	}
	public String getProviderclass() {
		return providerclass;
	}
	public void setProviderclass(String providerclass) {
		this.providerclass = providerclass;
	}
	
}
