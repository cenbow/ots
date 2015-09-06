package com.mk.ots.member.model;

/**
 * 用户等级表
 * @author nolan
 *
 */
public class ULevel {
	
	/**
	 * 等级id 
	 */
	private String id;
	
	/**
	 * 等级名
	 */
	private String name;
	
	public ULevel() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
