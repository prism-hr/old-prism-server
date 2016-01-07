package uk.co.alumeni.prism.utils;

import static java.nio.ByteBuffer.wrap;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

public class PrismEmailUtils {

    public static ByteBuffer getMessageData(MimeMessage message) {
        try {
            ByteArrayOutputStream messageData = new ByteArrayOutputStream();
            message.writeTo(messageData);
            return wrap(messageData.toByteArray());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static MimeBodyPart getMessagePart(String content, String contentType) {
        try {
            MimeBodyPart part = new MimeBodyPart();
            part.setContent(content, contentType);
            return part;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static MimeBodyPart getMessagePart(byte[] content, String contentType, String fileName) {
        try {
            MimeBodyPart part = new MimeBodyPart();
            part.setDataHandler(new DataHandler(new ByteArrayDataSource(content, contentType)));
            part.setFileName(fileName);
            return part;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
