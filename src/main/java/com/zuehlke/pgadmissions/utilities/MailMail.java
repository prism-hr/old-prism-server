package com.zuehlke.pgadmissions.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
 
public class MailMail
{
	private final JavaMailSenderImpl mailSender;
 
	@Autowired
	public MailMail(JavaMailSenderImpl mailSender){
		this.mailSender = mailSender;
	}
	
	public void sendMail(String from, String to, String subject, String msg) {
 
		SimpleMailMessage message = new SimpleMailMessage();
 
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(msg);
		mailSender.send(message);	
	}
}