package com.zuehlke.pgadmissions.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsReceiver implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(JmsReceiver.class);
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                log.info(((TextMessage) message).getText());
            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
