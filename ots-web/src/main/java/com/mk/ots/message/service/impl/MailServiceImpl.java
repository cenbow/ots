package com.mk.ots.message.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mk.ots.activity.controller.BActivityController;
import com.mk.ots.message.service.IMailService;
import com.mk.ots.system.dao.impl.ISyConfigService;

@Service
public class MailServiceImpl implements IMailService{
	final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
	
//	private String smtp_host = "smtp.exmail.sina.com";
//	private String smtp_port = "25";
//	private String sy_mail_user = "service@imike.com";
//	private String sy_mail_pwd = "imike@2014";
	
	@Autowired
	private ISyConfigService iSyConfigService;
	
	public MailServiceImpl() {
	}

	@Override
	public boolean send(String subject, String content, String[] toMails) {
		return doSend(subject, content, null, toMails);
	}

	@Override
	public boolean sendAttachment(String subject, String content, String[] attachment, String[] toMails) {
		logger.info("sendAttachment(subject:{}, content:{}, attachment:{}, tomails:{})	>>Begin......", subject, content, attachment, toMails);
		boolean isRtn = true;
		try {
			List<BodyPart> parts = Lists.newArrayList();
			//2. 添加附件
			for(String att: attachment){
				File file = new File(att);
				logger.info("attachment: {}, status:{}", att, file.exists());
				if(file.exists()){
					BodyPart attachmentPart = new MimeBodyPart();
					DataSource source = new FileDataSource(file);
					attachmentPart.setDataHandler(new DataHandler(source));
					attachmentPart.setFileName(file.getName());
					parts.add(attachmentPart);
				}
			}
			isRtn = doSend(subject, content, parts, toMails);
		} catch (MessagingException e) {
			logger.error("sendAttachment error.",e);
			isRtn = false;
			e.printStackTrace();
		}
		logger.info("sendAttachment(subject:{}, content:{}, attachment:{}, tomails:{}), sendstatus:{}	>>End.", subject, content, attachment, toMails, isRtn);
		return isRtn;
	}

	private boolean doSend(String subject, String content, List<BodyPart> bodyParts, String[] toMails ){
		boolean isRtn = true;
		//1. 设置session属性
		Properties props = new Properties();
	    props.put("mail.smtp.host", this.iSyConfigService.findValue("mail.smtp.host", "mikeweb"));
	    props.put("mail.smtp.port", this.iSyConfigService.findValue("mail.smtp.port", "mikeweb"));
	    props.put("mail.smtp.auth", "true");
	    Session session = Session.getInstance(props,
	    		  new javax.mail.Authenticator() {
	    			protected PasswordAuthentication getPasswordAuthentication() {
	    				String sy_mail_user2 = iSyConfigService.findValue("mail.user.name", "mikeweb");
						String sy_mail_pwd2 = iSyConfigService.findValue("mail.user.pwd", "mikeweb");
						return new PasswordAuthentication(sy_mail_user2, sy_mail_pwd2);
	    			}
	    		  });
	    
		//2. 构造邮件内容
	    MimeMessage msg = new MimeMessage(session);
		try {
			//2.1 邮件标题及内容
			//2.1.1 标题
			msg.setSubject(subject, "UTF-8");
			//2.1.2 正文内容
			Multipart multipart = new MimeMultipart();
			BodyPart contentPart = new MimeBodyPart(); 
			if(content!=null){
				contentPart.setText(content+"\n\n 此邮件是系统发出,请勿回复.");
			} 
			multipart.addBodyPart(contentPart);
			if(bodyParts!=null && bodyParts.size()>0){
				for(BodyPart tmppart : bodyParts){
					multipart.addBodyPart(tmppart);
				}
			}
			msg.setContent(multipart);
			//2.1.3 时间
			msg.setSentDate(new Date());
			
			//2.2 设置发件人
			msg.setFrom(new InternetAddress(iSyConfigService.findValue("mail.user.name", "mikeweb")));
			
			//2.3 设置收件人
			List<InternetAddress> adList = Lists.newArrayList();
			for(String tmpto: toMails){
				if(!Strings.isNullOrEmpty(tmpto)){
					adList.add(new InternetAddress(tmpto));
				}
			}
			InternetAddress[] toAddress = new InternetAddress[adList.size()];
			adList.toArray(toAddress);
	        msg.setRecipients(Message.RecipientType.TO, toAddress);
	        msg.saveChanges();
	        
	        //3. 发送邮件
	        Transport.send(msg);
		} catch (MessagingException e) {
			logger.error("doSend error.",e);
			isRtn = false;
			e.printStackTrace();
		}
		return isRtn;
	}
}
