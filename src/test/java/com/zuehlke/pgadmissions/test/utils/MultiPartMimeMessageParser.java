package com.zuehlke.pgadmissions.test.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

public class MultiPartMimeMessageParser {

    private MultiPartMimeMessageParser() {
    }
    
    public static List<String> parseMessage(Message message) throws IOException, MessagingException {
        List<String> emailContents = new ArrayList<String>();
        Object content = message.getContent();  
        if (content instanceof String) {  
            emailContents.add((String)content);
        } else if (content instanceof Multipart) {  
            Multipart mp = (Multipart)content;  
            emailContents.addAll(handleMultipart(mp));  
        } 
        return emailContents;
    }  
      
    private static List<String> handleMultipart(Multipart mp) throws IOException, MessagingException {
        List<String> emailContents = new ArrayList<String>();
        
        int count = mp.getCount();  
        for (int i = 0; i < count; i++) {  
            BodyPart bp = mp.getBodyPart(i);  
            Object content = bp.getContent();  
            if (content instanceof String) {  
                emailContents.add((String)content);
            } else if (content instanceof InputStream) {  
                // handle input stream  
            } else if (content instanceof Message) {  
                Message message = (Message)content;  
                emailContents.addAll(parseMessage(message));  
            } else if (content instanceof Multipart) {  
                Multipart mp2 = (Multipart)content;  
                emailContents.addAll(handleMultipart(mp2));  
            }  
        }  
        return emailContents;
    }  
}
