package com.zuehlke.pgadmissions.mail;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class FakeLoggingMailSender extends JavaMailSenderImpl {

    private final Logger log = LoggerFactory.getLogger(MailSenderMock.class);
    
    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        try {
            for (MimeMessage mimeMessage : mimeMessages) {                
                log.info(String.format("Sender: %s", mimeMessage.getSender()));
                
                for (Address address : mimeMessage.getAllRecipients()) {
                    log.info(String.format("Recipient: %s", address));
                }
                
                log.info(String.format("Subject: %s", mimeMessage.getSubject()));
                
                if (mimeMessage.getContent() instanceof MimeMultipart) {
                    MimeMultipart multiPart = (MimeMultipart) mimeMessage.getContent();
                    for (int idx = 0; idx < multiPart.getCount(); idx++) {
                        BodyPart bodyPart = multiPart.getBodyPart(idx);
                        String bodyAsString = IOUtils.toString(bodyPart.getInputStream());
                        log.info(String.format("Body: %s", bodyAsString));
                    }
                } else {
                    log.info(String.format("Body: %s", mimeMessage.getContent()));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
