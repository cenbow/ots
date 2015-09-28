package com.mk.ots.message.model;


public enum MessageType {
	USER("1"), SYSTEM("2"),GROUP("3");

	private String id;

	private MessageType(String id) {
		this.id = id;
	}

	public String getId(){
		return this.id;
	}

	public static MessageType getById(String id) {
		for (MessageType temp : MessageType.values()) {
			if (temp.getId().equals(id)) {
				return temp;
			}
		}
		return null;
	}
}
