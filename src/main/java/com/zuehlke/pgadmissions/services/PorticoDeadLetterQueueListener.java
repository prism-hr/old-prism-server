package com.zuehlke.pgadmissions.services;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PorticoDeadLetterQueueListener implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(PorticoDeadLetterQueueListener.class);
    
    private final PorticoQueueService queueService;
    
    public PorticoDeadLetterQueueListener() {
        this(null);
    }
    
    @Autowired
    public PorticoDeadLetterQueueListener(final PorticoQueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public void onMessage(final Message message) {
        if (message instanceof TextMessage) {
            try {
                String applicationNumber = ((TextMessage) message).getText();
                log.info(String.format(
                        "Received undeliverable application notification [messageId=%s, applicationNumber=%s, status=%s, dateAdded=%s]",
                        message.getJMSMessageID(), applicationNumber, message.getStringProperty("Status"),
                        message.getStringProperty("Added")));
                queueService.handleNonDeliverableApplication(applicationNumber);
            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
