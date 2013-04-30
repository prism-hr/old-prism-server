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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;
import com.zuehlke.pgadmissions.services.exporters.PorticoExportService;

@Service
public class PorticoQueueService {

    private final Logger log = LoggerFactory.getLogger(PorticoQueueService.class);
    
    @Resource(name = "porticoQueue")
    private Queue queue;
    
    @Resource(name = "jmsTemplate")
    private JmsTemplate template;
    
    @Autowired
    private ThrottleService throttleService;
    
    @Autowired
    private PorticoExportService exportService;
    
    @Autowired
    private ApplicationFormTransferService formTransferService;       
    
    @Transactional
    public void sendToPortico(final ApplicationForm form) {
        createOrReturnExistingApplicationFormTransfer(form);
        
        if (throttleService.isPorticoInterfaceEnabled()) {
            log.info(String.format("Sending JMS message to the portico queue [applicationNumber=%s, status=%s]", form.getApplicationNumber(), form.getStatus()));            
            
            template.convertAndSend(queue, form.getApplicationNumber(), new MessagePostProcessor() {
                public Message postProcessMessage(Message message) throws JMSException {
                    message.setStringProperty("Status", form.getStatus().toString());
                    message.setStringProperty("Added", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                    return message;
                }
            });
        } 
    }
    
    @Transactional(readOnly = true)
    public void sendQueuedApprovedApplicationsToPortico() {
        List<ApplicationFormTransfer> applications = formTransferService.getAllTransfersWaitingToBeSentToPorticoOldestFirst();
        for (ApplicationFormTransfer transfer : applications) {
            ApplicationForm form = transfer.getApplicationForm();
            if (form.getStatus().equals(ApplicationFormStatus.APPROVED)) {
                sendToPortico(form);
            }
        }       
    }

    @Transactional(readOnly = true)
    public void sendQueuedWithdrawnOrRejectedApplicationsToPortico(final int batchSize) {
        List<ApplicationFormTransfer> applications = formTransferService.getAllTransfersWaitingToBeSentToPorticoOldestFirst();
        int numberOfApplicationsSent = 1;
        for (ApplicationFormTransfer transfer : applications) {
            ApplicationForm form = transfer.getApplicationForm();
            if (form.getStatus().equals(ApplicationFormStatus.WITHDRAWN) || form.getStatus().equals(ApplicationFormStatus.REJECTED)) {
                if ((batchSize == 0) || numberOfApplicationsSent <= batchSize) {
                    numberOfApplicationsSent++;
                    sendToPortico(form);
                }
            }
        }
    }
    
    @Transactional
    public ApplicationFormTransfer createOrReturnExistingApplicationFormTransfer(final ApplicationForm form) {
        return formTransferService.createOrReturnExistingApplicationFormTransfer(form);
    }
    
    public void setThrottleService(ThrottleService throttleService) {
        this.throttleService = throttleService;
    }

    public void setExportService(PorticoExportService exportService) {
        this.exportService = exportService;
    }

    public void setFormTransferService(ApplicationFormTransferService formTransferService) {
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

    public ThrottleService getThrottleService() {
        return throttleService;
    }
    
    public PorticoExportService getExportService() {
        return exportService;
    }

    public ApplicationFormTransferService getFormTransferService() {
        return formTransferService;
    }
}
