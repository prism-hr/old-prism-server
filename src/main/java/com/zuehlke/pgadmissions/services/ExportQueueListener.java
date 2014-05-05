package com.zuehlke.pgadmissions.services;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationTransferService;
import com.zuehlke.pgadmissions.services.exporters.ExportService;

public class ExportQueueListener implements MessageListener {

    public static class TriggerJmsRetryException extends RuntimeException {
        private static final long serialVersionUID = 79819935845687782L;

        public TriggerJmsRetryException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    private final Logger log = LoggerFactory.getLogger(ExportQueueListener.class);

    @Autowired
    private ExportService exportService;

    @Autowired
    private ApplicationFormDAO applicationFormDAO;

    @Autowired
    private ApplicationTransferService applicationFormTransferService;

    @Autowired
    private ApplicationExportConfigurationService exportConfigurationService;

    @Autowired
    private MailSendingService mailSendingService;

    @Autowired
    private RoleService roleService;

    @Override
    public void onMessage(final Message message) {
        if (message instanceof TextMessage) {
            try {

                String applicationNumber = ((TextMessage) message).getText();

                log.info(String.format("Received JMS message [messageId=%s, applicationNumber=%s, status=%s, dateAdded=%s]", message.getJMSMessageID(),
                        applicationNumber, message.getStringProperty("Status"), message.getStringProperty("Added")));

                sendApplicationToPortico(applicationNumber);

            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void sendApplicationToPortico(final String applicationNumber) {
        ApplicationForm form = getApplicationForm(applicationNumber);
        ApplicationTransfer transfer = getApplicationFormTransfer(form);

        try {
            exportService.sendToPortico(form, transfer);
        } catch (ExportServiceException e) {

            // TODO add APPLICATION_CORRECT_REQUEST task and send email
            
            errorHandlingStrategyResolver(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public ApplicationForm getApplicationForm(final String applicationNumber) {
        return applicationFormDAO.getByApplicationNumber(applicationNumber);
    }

    private ApplicationTransfer getApplicationFormTransfer(final ApplicationForm form) {
        return applicationFormTransferService.createOrReturnExistingApplicationFormTransfer(form);
    }

    private void errorHandlingStrategyResolver(ExportServiceException e) {
        switch (e.getErrorHandlingStrategy()) {
        case RETRY:
            throw new TriggerJmsRetryException(e.getMessage(), e);

        case STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION:
            exportConfigurationService.disablePorticoInterface();
            break;

        default:
            break;
        }
    }

}
