package com.zuehlke.pgadmissions.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;

@Service
public class PorticoDeadLetterQueueListener implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(PorticoDeadLetterQueueListener.class);
    
    private final ApplicationFormDAO formDAO;

    private final ApplicationFormTransferDAO formTransferDAO;
    
    public PorticoDeadLetterQueueListener() {
        this(null, null);
    }
    
    @Autowired
    public PorticoDeadLetterQueueListener(final ApplicationFormDAO formDAO, final ApplicationFormTransferDAO formTransferDAO) {
        this.formDAO = formDAO;
        this.formTransferDAO = formTransferDAO;
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
                handleNonDeliverableApplication(applicationNumber);
            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    @Transactional
    private void handleNonDeliverableApplication(final String applicationNumber) {
        ApplicationFormTransfer transfer = formTransferDAO.getByApplicationForm(getApplicationForm(applicationNumber));
        transfer.setStatus(ApplicationTransferStatus.CANCELLED);
    }
    
    @Transactional(readOnly = true)
    public ApplicationForm getApplicationForm(final String applicationNumber) {
        return formDAO.getApplicationByApplicationNumber(applicationNumber);
    }
    
    @Transactional(readOnly = true)
    public ApplicationFormTransfer getApplicationFormTransfer(final ApplicationForm form) {
        return formTransferDAO.getByApplicationForm(form);
    } 
}
