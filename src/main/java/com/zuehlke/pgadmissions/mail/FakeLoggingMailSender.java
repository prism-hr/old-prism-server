package com.zuehlke.pgadmissions.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class FakeLoggingMailSender extends JavaMailSenderImpl implements FakeLoggingMailSenderListener {

    private static final Logger LOG = Logger.getLogger(FakeLoggingMailSender.class);
    
    private final List<FakeLoggingMailSenderListener> listeners;
    
    public FakeLoggingMailSender() {
        listeners = new ArrayList<FakeLoggingMailSenderListener>();
    }
    
    public void registerListeners(FakeLoggingMailSenderListener... observers) {
        this.listeners.addAll(Arrays.asList(observers));
    }
    
    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        try {
            for (MimeMessage mimeMessage : mimeMessages) {                
                onDoSend(mimeMessage);
                
                LOG.trace(String.format("Sender: %s", mimeMessage.getSender()));
                
                onSender(String.format("%s", mimeMessage.getSender()));
                
                for (Address address : mimeMessage.getAllRecipients()) {
                    LOG.trace(String.format("Recipient: %s", address));
                    onRecipient(String.format("%s", address));
                }
                
                LOG.trace(String.format("Subject: %s", mimeMessage.getSubject()));
                
                onSubject(mimeMessage.getSubject());
                
                if (mimeMessage.getContent() instanceof MimeMultipart) {
                    MimeMultipart multiPart = (MimeMultipart) mimeMessage.getContent();
                    for (int idx = 0; idx < multiPart.getCount(); idx++) {
                        BodyPart bodyPart = multiPart.getBodyPart(idx);
                        String bodyAsString = IOUtils.toString(bodyPart.getInputStream());
                        LOG.trace(String.format("Body: %s", bodyAsString));
                        onBody(bodyAsString);
                    }
                } else {
                    Object contentAsObject = mimeMessage.getContent();
                    LOG.trace(String.format("Body: %s", contentAsObject));
                    onBody(String.format("%s", contentAsObject));
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Override
    public void onDoSend(String message) {
        for (FakeLoggingMailSenderListener observer : listeners) {
            observer.onDoSend(message);
        }
    }

    @Override
    public void onDoSend(MimeMessage message) {
        for (FakeLoggingMailSenderListener observer : listeners) {
            observer.onDoSend(message);
        }
    }

    @Override
    public void onSender(String sender) {
        for (FakeLoggingMailSenderListener observer : listeners) {
            observer.onSender(sender);
        }
    }

    @Override
    public void onRecipient(String recipient) {
        for (FakeLoggingMailSenderListener observer : listeners) {
            observer.onRecipient(recipient);
        }
    }

    @Override
    public void onBody(String body) {
        for (FakeLoggingMailSenderListener observer : listeners) {
            observer.onBody(body);
        }
    }

    @Override
    public void onSubject(String subject) {
        for (FakeLoggingMailSenderListener observer : listeners) {
            observer.onSubject(subject);
        }
    }
}
