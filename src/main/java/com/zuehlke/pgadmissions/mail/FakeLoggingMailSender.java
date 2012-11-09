package com.zuehlke.pgadmissions.mail;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
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
                LOG.trace(String.format("Sender: %s", mimeMessage.getSender()));
                for (Address address : mimeMessage.getAllRecipients()) {
                    LOG.trace(String.format("Recipient: %s", address.toString()));
                }
                LOG.trace(String.format("Subject: %s", mimeMessage.getSubject()));
                if (mimeMessage.getContent() instanceof MimeMultipart) {
                    MimeMultipart multiPart = (MimeMultipart) mimeMessage.getContent();
                    for (int idx = 0; idx < multiPart.getCount(); idx++) {
                        BodyPart bodyPart = multiPart.getBodyPart(idx);
                        LOG.trace(String.format("Body: %s", IOUtils.toString(bodyPart.getInputStream())));
                    }
                } else {
                    LOG.trace(String.format("Body: %s", mimeMessage.getContent()));
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
