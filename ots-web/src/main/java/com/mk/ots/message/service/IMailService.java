package com.mk.ots.message.service;

public interface IMailService {

	public boolean send(String subject, String content, String[] toMails);
	
	public boolean sendAttachment(String subject, String content, String[] attachment, String[] toMails);
}
