package com.zuehlke.pgadmissions.jms;

import java.util.Date;

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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.PorticoExportServiceException;
import com.zuehlke.pgadmissions.mail.refactor.MailSendingService;
import com.zuehlke.pgadmissions.services.ThrottleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;
import com.zuehlke.pgadmissions.services.exporters.PorticoExportService;

@Service
public class PorticoQueueListener implements MessageListener {

    public static class TriggerJmsRetryException extends RuntimeException {
        private static final long serialVersionUID = 79819935845687782L;
        public TriggerJmsRetryException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
    
    private final Logger log = LoggerFactory.getLogger(PorticoQueueListener.class);
    
    private PorticoExportService exportService;

    private final ApplicationFormDAO formDAO;

    private final ApplicationFormTransferService applicationFormTransferService;
    
    private final ThrottleService throttleService;
    
    private final MailSendingService mailService;
    
    private final UserService userService;
    
    public PorticoQueueListener() {
        this(null, null, null, null, null, null);
    }
    
    @Autowired
    public PorticoQueueListener(
            final PorticoExportService exportService, 
            final ApplicationFormDAO formDAO, 
            final ApplicationFormTransferService applicationFormTransferService,
            final ThrottleService throttleService, 
            final MailSendingService mailService, 
            final UserService userService) {
        this.exportService = exportService;
        this.formDAO = formDAO;
        this.applicationFormTransferService = applicationFormTransferService;
        this.throttleService = throttleService;
		this.mailService = mailService;
		this.userService = userService;
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
        
        try {
            exportService.sendToPortico(form, transfer);
        } catch (PorticoExportServiceException e) {
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
    
    private ApplicationFormTransfer getApplicationFormTransfer(final ApplicationForm form) {
        return applicationFormTransferService.createOrReturnExistingApplicationFormTransfer(form);
    }
    
    private void sendEmailWithErrorMessage(final Exception e) {
        try {
        	mailService.sendExportErrorMessage(userService.getUsersInRole(Authority.SUPERADMINISTRATOR), e.getMessage(), new Date());
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
    
    private void errorHandlingStrategyResolver(PorticoExportServiceException e) {
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
        String messageCode = "There was an issue with the PORTICO interfaces which needs attention by an administrator. " +
        		"PRISM is now not sending any more applications to PORTICO until this issue has been resolved";
        mailService.sendExportErrorMessage(userService.getUsersInRole(Authority.SUPERADMINISTRATOR), messageCode, new Date());
    }
}
