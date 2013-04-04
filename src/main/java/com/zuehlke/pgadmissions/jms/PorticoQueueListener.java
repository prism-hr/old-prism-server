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
import com.zuehlke.pgadmissions.exceptions.UclExportServiceException;
import com.zuehlke.pgadmissions.mail.DataExportMailSender;
import com.zuehlke.pgadmissions.services.ThrottleService;
import com.zuehlke.pgadmissions.services.exporters.UclExportService;

@Service
public class PorticoQueueListener implements MessageListener {

    public static class TriggerJmsRetryException extends RuntimeException {
        private static final long serialVersionUID = 79819935845687782L;
        public TriggerJmsRetryException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
    
    private final Logger log = LoggerFactory.getLogger(PorticoQueueListener.class);
    
    private UclExportService exportService;

    private final ApplicationFormDAO formDAO;

    private final ApplicationFormTransferDAO formTransferDAO;
    
    private final DataExportMailSender exportMailSender;
    
    private final ThrottleService throttleService;
    
    public PorticoQueueListener() {
        this(null, null, null, null, null);
    }
    
    @Autowired
    public PorticoQueueListener(final UclExportService exportService, final ApplicationFormDAO formDAO,
            final ApplicationFormTransferDAO formTransferDAO, final DataExportMailSender exportMailSender,
            ThrottleService throttleService) {
        this.exportService = exportService;
        this.formDAO = formDAO;
        this.formTransferDAO = formTransferDAO;
        this.exportMailSender = exportMailSender;
        this.throttleService = throttleService;
    }
    
    @Override
    public void onMessage(final Message message) {
        if (message instanceof TextMessage) {
            try {
                
                String applicationNumber = ((TextMessage) message).getText();
        
                log.info(String.format(
                        "Received JMS message [messageId=%s, applicationNumber=%s, status=%s, dateAdded=%s]",
                        message.getJMSMessageID(), applicationNumber, message.getStringProperty("Status"),
                        message.getStringProperty("Added")));
                
                sendApplicationToPortico(applicationNumber);
                
            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    private void sendApplicationToPortico(final String applicationNumber) {
        ApplicationForm form = getApplicationForm(applicationNumber);
        ApplicationFormTransfer transfer = getApplicationFormTransfer(form);
        
        if (form == null || transfer == null) {
            log.warn("The applicationForm or the applicationTransfer object is NULL! applicationForm: {} applicationTransfer: {}", form, transfer);
            return;
        }
        
        try {
            exportService.sendToPortico(form, transfer);
        } catch (UclExportServiceException e) {
            sendEmailWithErrorMessage(e);
            errorHandlingStrategyResolver(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public ApplicationForm getApplicationForm(final String applicationNumber) {
        return formDAO.getApplicationByApplicationNumber(applicationNumber);
    }
    
    @Transactional(readOnly = true)
    public ApplicationFormTransfer getApplicationFormTransfer(final ApplicationForm form) {
        return formTransferDAO.getByApplicationForm(form);
    }
    
    private void sendEmailWithErrorMessage(final Exception e) {
        try {
            exportMailSender.sendErrorMessage(e.getMessage(), e);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
    
    private void errorHandlingStrategyResolver(UclExportServiceException e) {
        switch (e.getErrorHandlingStrategy()) {
        case RETRY:
            throw new TriggerJmsRetryException(e.getMessage(), e);
            
        case STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION:
            disablePorticoInterface();
            break;
            
        default:
            break;
        }
    }
    
    private void disablePorticoInterface() {
        throttleService.disablePorticoInterface();
        exportMailSender.sendErrorMessage("There was an issue with the PORTICO interfaces which needs attention by an administrator. " +
        		"PRISM is now not sending any more applications to PORTICO until this issue has been resolved");
    }
}
