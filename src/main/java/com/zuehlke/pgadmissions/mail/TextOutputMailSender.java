package com.zuehlke.pgadmissions.mail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class TextOutputMailSender extends JavaMailSenderImpl {

    private final Logger log = LoggerFactory.getLogger(TextOutputMailSender.class);

    private String getFilename() {
        return String.format("%s/prism_email_%s.txt", System.getProperty("user.home"), new DateTime().toString("yyyy-MM-dd HH-mm-ss-SSS"));
    }
    
    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(getFilename())));
            for (MimeMessage mimeMessage : mimeMessages) {
                writer.write(String.format("Sender: %s\n", mimeMessage.getSender()));
                for (Address address : mimeMessage.getAllRecipients()) {
                    writer.write(String.format("Recipient: %s\n", address));
                }
                writer.write(String.format("Subject: %s\n", mimeMessage.getSubject()));
                if (mimeMessage.getContent() instanceof MimeMultipart) {
                    MimeMultipart multiPart = (MimeMultipart) mimeMessage.getContent();
                    for (int idx = 0; idx < multiPart.getCount(); idx++) {
                        BodyPart bodyPart = multiPart.getBodyPart(idx);
                        String bodyAsString = IOUtils.toString(bodyPart.getInputStream());
                        writer.write(bodyAsString);
                    }
                } else {
                    Object contentAsObject = mimeMessage.getContent();
                    writer.write(contentAsObject.toString());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
