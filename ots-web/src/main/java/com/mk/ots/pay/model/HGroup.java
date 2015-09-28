package com.mk.ots.pay.model;

import java.io.Serializable;
 
public class HGroup implements Serializable{
	private static final long serialVersionUID = 1L;
 
	private Long id;

    private String regphone;

    private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegphone() {
		return regphone;
	}

	public void setRegphone(String regphone) {
		this.regphone = regphone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
 
}