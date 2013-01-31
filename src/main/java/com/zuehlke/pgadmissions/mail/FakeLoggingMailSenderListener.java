package com.zuehlke.pgadmissions.mail;

import javax.mail.internet.MimeMessage;

public interface FakeLoggingMailSenderListener {

    void onDoSend(String message);
    
    void onDoSend(MimeMessage message);
    
    void onSender(String sender);
    
    void onRecipient(String recipient);
    
    void onBody(String body);
    
    void onSubject(String subject);
}
