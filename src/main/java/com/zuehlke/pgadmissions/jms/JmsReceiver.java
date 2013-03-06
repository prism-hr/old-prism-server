package com.zuehlke.pgadmissions.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsReceiver implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(JmsReceiver.class);
    private static int messageReceived = 0;
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
            	String messageValue = ((TextMessage) message).getText();
            	if (messageValue.equals("Hello World")) {
            		log.info("Got message: {}; number of received times: {}", messageValue, messageReceived++);
                	log.info("Throwing RuntimeException");
                	throw new RuntimeException("This should be caught by the AS and message should be pushed back into the JMS queue..");
            	}
            	else {
            		log.info("Message successfully received: {}", messageValue);
            	}
            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
