package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.jms.Queue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormTransferBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;
import com.zuehlke.pgadmissions.services.exporters.PorticoExportService;

public class PorticoQueueServiceTest {
    
    private Queue queueMock;
    
    private JmsTemplate templateMock;
    
    private ThrottleService throttleServiceMock;
    
    private PorticoExportService exportServiceMock;
    
    private ApplicationFormTransferService formTransferServiceMock;

    private PorticoQueueService porticoQueueService;
    
    private ApplicationForm form;
    
    private int numberOfSentApplications = 0;
    
    @Before
    public void prepare() {
        form = new ValidApplicationFormBuilder().build();
        queueMock = EasyMock.createMock(Queue.class);
        templateMock = EasyMock.createMock(JmsTemplate.class);
        throttleServiceMock = EasyMock.createMock(ThrottleService.class);
        exportServiceMock = EasyMock.createMock(PorticoExportService.class);
        formTransferServiceMock = EasyMock.createMock(ApplicationFormTransferService.class);
        porticoQueueService = new PorticoQueueService();
        porticoQueueService.setExportService(exportServiceMock);
        porticoQueueService.setFormTransferService(formTransferServiceMock);
        porticoQueueService.setThrottleService(throttleServiceMock);
        porticoQueueService.setTemplate(templateMock);
        porticoQueueService.setQueue(queueMock);
    }
    
    @Test
    public void shouldNotSendApplicationsToTheQueueIfTheInterfaceHasBeenDisabled() {
        EasyMock.expect(formTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationFormTransfer());
        EasyMock.expect(throttleServiceMock.isPorticoInterfaceEnabled()).andReturn(false);
        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
        porticoQueueService.sendToPortico(form);
        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
    }
    
    @Test
    public void shouldSendApplicationsToTheQueueIfTheInterfaceHasBeenDisabled() {
        EasyMock.expect(formTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationFormTransfer());
        EasyMock.expect(throttleServiceMock.isPorticoInterfaceEnabled()).andReturn(true);
        templateMock.convertAndSend(EasyMock.eq(queueMock), EasyMock.eq(form.getApplicationNumber()), EasyMock.isA(MessagePostProcessor.class));
        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
        porticoQueueService.sendToPortico(form);
        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
    }
    
    @Test
    public void shouldSendApprovedApplicationsToTheQueueWhichPreviouslyHaveNotBeenSent() {
        porticoQueueService = new PorticoQueueService() {
            @Override
            public void sendToPortico(final ApplicationForm form) {
                assertEquals(ApplicationFormStatus.APPROVED, form.getStatus());
            }
        };
        porticoQueueService.setExportService(exportServiceMock);
        porticoQueueService.setFormTransferService(formTransferServiceMock);
        porticoQueueService.setThrottleService(throttleServiceMock);
        porticoQueueService.setTemplate(templateMock);
        porticoQueueService.setQueue(queueMock);
        
        ApplicationForm approved = new ValidApplicationFormBuilder().build();
        ApplicationForm rejected = new ValidApplicationFormBuilder().build();
        approved.setStatus(ApplicationFormStatus.APPROVED);
        rejected.setStatus(ApplicationFormStatus.REJECTED);
        
        ApplicationFormTransfer transferApproved = new ApplicationFormTransferBuilder().applicationForm(approved).build();
        ApplicationFormTransfer transferRejected = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        
        EasyMock.expect(formTransferServiceMock.getAllTransfersWaitingToBeSentToPorticoOldestFirst()).andReturn(Arrays.asList(transferApproved, transferRejected));
        
        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
        
        porticoQueueService.sendQueuedApprovedApplicationsToPortico();
        
        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
    }
    
    @Test
    public void shouldSendRejectedOrWithdrawnApplicationsToTheQueueWhichPreviouslyHaveNotBeenSent() {
        porticoQueueService = new PorticoQueueService() {
            @Override
            public void sendToPortico(final ApplicationForm form) {
                assertEquals(ApplicationFormStatus.REJECTED, form.getStatus());
            }
        };
        porticoQueueService.setExportService(exportServiceMock);
        porticoQueueService.setFormTransferService(formTransferServiceMock);
        porticoQueueService.setThrottleService(throttleServiceMock);
        porticoQueueService.setTemplate(templateMock);
        porticoQueueService.setQueue(queueMock);
        
        ApplicationForm approved = new ValidApplicationFormBuilder().build();
        ApplicationForm rejected = new ValidApplicationFormBuilder().build();
        approved.setStatus(ApplicationFormStatus.APPROVED);
        rejected.setStatus(ApplicationFormStatus.REJECTED);
        
        ApplicationFormTransfer transferApproved = new ApplicationFormTransferBuilder().applicationForm(approved).build();
        ApplicationFormTransfer transferRejected = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        
        EasyMock.expect(formTransferServiceMock.getAllTransfersWaitingToBeSentToPorticoOldestFirst()).andReturn(Arrays.asList(transferApproved, transferRejected));
        
        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
        
        porticoQueueService.sendQueuedWithdrawnOrRejectedApplicationsToPortico(50);
        
        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
    }
    
    @Test
    public void shouldStopSendingApplicationsIfTheMaximumNumberOfApplicationsHaveBeenReached() {
        porticoQueueService = new PorticoQueueService() {
            @Override
            public void sendToPortico(final ApplicationForm form) {
                numberOfSentApplications++;
            }
        };
        porticoQueueService.setExportService(exportServiceMock);
        porticoQueueService.setFormTransferService(formTransferServiceMock);
        porticoQueueService.setThrottleService(throttleServiceMock);
        porticoQueueService.setTemplate(templateMock);
        porticoQueueService.setQueue(queueMock);
        
        ApplicationForm rejected = new ValidApplicationFormBuilder().build();
        rejected.setStatus(ApplicationFormStatus.REJECTED);
        
        ApplicationFormTransfer transferRejected1 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        ApplicationFormTransfer transferRejected2 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        ApplicationFormTransfer transferRejected3 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        ApplicationFormTransfer transferRejected4 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        ApplicationFormTransfer transferRejected5 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        ApplicationFormTransfer transferRejected6 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        ApplicationFormTransfer transferRejected7 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
        
        EasyMock.expect(formTransferServiceMock.getAllTransfersWaitingToBeSentToPorticoOldestFirst()).andReturn(
                Arrays.asList(transferRejected1, transferRejected2, transferRejected3, transferRejected4, transferRejected5, transferRejected6, transferRejected7));
        
        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
        
        porticoQueueService.sendQueuedWithdrawnOrRejectedApplicationsToPortico(2);
        
        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
        
        assertEquals(2, numberOfSentApplications);
    }
    
    @Test
    public void shouldSendAllRejectedOrWithdrawnApplicationsToTheQueueWithoutBatching() {
    	porticoQueueService = new PorticoQueueService() {
    		@Override
    		public void sendToPortico(final ApplicationForm form) {
    			numberOfSentApplications++;
    		}
    	};
    	porticoQueueService.setExportService(exportServiceMock);
    	porticoQueueService.setFormTransferService(formTransferServiceMock);
    	porticoQueueService.setThrottleService(throttleServiceMock);
    	porticoQueueService.setTemplate(templateMock);
    	porticoQueueService.setQueue(queueMock);
    	
    	ApplicationForm rejected = new ValidApplicationFormBuilder().build();
    	rejected.setStatus(ApplicationFormStatus.REJECTED);
    	
    	ApplicationFormTransfer transferRejected1 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
    	ApplicationFormTransfer transferRejected2 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
    	ApplicationFormTransfer transferRejected3 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
    	ApplicationFormTransfer transferRejected4 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
    	ApplicationFormTransfer transferRejected5 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
    	ApplicationFormTransfer transferRejected6 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
    	ApplicationFormTransfer transferRejected7 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
    	
    	EasyMock.expect(formTransferServiceMock.getAllTransfersWaitingToBeSentToPorticoOldestFirst()).andReturn(
    			Arrays.asList(transferRejected1, transferRejected2, transferRejected3, transferRejected4, transferRejected5, transferRejected6, transferRejected7));
    	
    	EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
    	
    	porticoQueueService.sendQueuedWithdrawnOrRejectedApplicationsToPortico(0);
    	
    	EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
    	
    	assertEquals(7, numberOfSentApplications);
    }

}
