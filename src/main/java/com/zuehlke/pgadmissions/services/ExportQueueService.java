package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferState;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.services.exporters.ApplicationTransferService;
import com.zuehlke.pgadmissions.services.exporters.ExportService;

@Service
public class ExportQueueService {

    private final Logger log = LoggerFactory.getLogger(ExportQueueService.class);

    @Resource
    private ApplicationFormDAO formDAO;

    @Resource(name = "porticoQueue")
    private Queue queue;

    @Resource(name = "jmsTemplate")
    private JmsTemplate template;

    @Autowired
    private ApplicationExportConfigurationService throttleService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ApplicationTransferService formTransferService;

    @Transactional
    public void sendToPortico(final Application form) {
        createOrReturnExistingApplicationFormTransfer(form);

        if (throttleService.isPorticoInterfaceEnabled()) {
            log.info(String.format("Sending JMS message to the portico queue [applicationNumber=%s, status=%s]", form.getApplicationNumber(), form.getState()));

            template.convertAndSend(queue, form.getApplicationNumber(), new MessagePostProcessor() {
                public Message postProcessMessage(Message message) throws JMSException {
                    message.setStringProperty("Status", form.getState().toString());
                    message.setStringProperty("Added", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                    return message;
                }
            });
        }
    }

    @Transactional
    public void sendQueuedApprovedApplicationsToPortico() {
        List<ApplicationTransfer> applications = formTransferService.getAllTransfersWaitingToBeSentToPorticoOldestFirst();
        for (ApplicationTransfer transfer : applications) {
            Application form = transfer.getApplicationForm();
            if (form.getState().equals(PrismState.APPLICATION_APPROVED)) {
                sendToPortico(form);
            }
        }
    }

    @Transactional
    public void sendApplicationsToBeSentToPortico(final int batchSize) {
        List<Long> transferIds = formTransferService.getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds();
        int numberOfApplicationsSent = 1;
        for (Long transferId : transferIds) {
            Application form = formTransferService.getById(transferId).getApplicationForm();
            if ((batchSize == 0) || numberOfApplicationsSent <= batchSize) {
                numberOfApplicationsSent++;
                sendToPortico(form);
            }
        }
    }

    @Transactional
    public ApplicationTransfer createOrReturnExistingApplicationFormTransfer(final Application form) {
        return formTransferService.createOrReturnExistingApplicationFormTransfer(form);
    }

    @Transactional
    public void handleNonDeliverableApplication(final String applicationNumber) {
        Application form = formDAO.getByApplicationNumber(applicationNumber);
        ApplicationTransfer transfer = form.getTransfer();
        transfer.setState(ApplicationTransferState.CANCELLED);
    }

    public void setThrottleService(ApplicationExportConfigurationService throttleService) {
        this.throttleService = throttleService;
    }

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }

    public void setFormTransferService(ApplicationTransferService formTransferService) {
        this.formTransferService = formTransferService;
    }

    public Queue getQueue() {
        return queue;
    }

    public JmsTemplate getTemplate() {
        return template;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public void setTemplate(JmsTemplate template) {
        this.template = template;
    }

    public ApplicationExportConfigurationService getThrottleService() {
        return throttleService;
    }

    public ExportService getExportService() {
        return exportService;
    }

    public ApplicationTransferService getFormTransferService() {
        return formTransferService;
    }
}
