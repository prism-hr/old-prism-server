package com.zuehlke.pgadmissions.mail;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class FakeLoggingMailSender extends JavaMailSenderImpl {

    private static final Logger LOG = Logger.getLogger(FakeLoggingMailSender.class);
    
    public FakeLoggingMailSender() {
    }
    
    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        try {
            for (MimeMessage mimeMessage : mimeMessages) {
                LOG.info(String.format("Sender: %s", mimeMessage.getSender()));
                for (Address address : mimeMessage.getAllRecipients()) {
                    LOG.info(String.format("Recipient: %s", address.toString()));
                }
                LOG.info(String.format("Subject: %s", mimeMessage.getSubject()));
                LOG.info(String.format("Body: %s", mimeMessage.getContent()));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
