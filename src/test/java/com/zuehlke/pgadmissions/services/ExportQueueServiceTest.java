package com.zuehlke.pgadmissions.services;

import javax.jms.Queue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.services.exporters.ApplicationTransferService;
import com.zuehlke.pgadmissions.services.exporters.ExportService;

public class ExportQueueServiceTest {
    
    private Queue queueMock;
    
    private JmsTemplate templateMock;
    
    private ApplicationExportConfigurationService throttleServiceMock;
    
    private ExportService exportServiceMock;
    
    private ApplicationTransferService formTransferServiceMock;

    private ExportQueueService porticoQueueService;
    
    private Application form;
    
    @Before
    public void prepare() {
        form = new ValidApplicationFormBuilder().build();
        queueMock = EasyMock.createMock(Queue.class);
        templateMock = EasyMock.createMock(JmsTemplate.class);
        throttleServiceMock = EasyMock.createMock(ApplicationExportConfigurationService.class);
        exportServiceMock = EasyMock.createMock(ExportService.class);
        formTransferServiceMock = EasyMock.createMock(ApplicationTransferService.class);
        porticoQueueService = new ExportQueueService();
        porticoQueueService.setExportService(exportServiceMock);
        porticoQueueService.setFormTransferService(formTransferServiceMock);
        porticoQueueService.setThrottleService(throttleServiceMock);
        porticoQueueService.setTemplate(templateMock);
        porticoQueueService.setQueue(queueMock);
    }
    
    @Test
    public void shouldNotSendApplicationsToTheQueueIfTheInterfaceHasBeenDisabled() {
        EasyMock.expect(formTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationTransfer());
        EasyMock.expect(throttleServiceMock.isPorticoInterfaceEnabled()).andReturn(false);
        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
        porticoQueueService.sendToPortico(form);
        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
    }
    
    @Test
    public void shouldSendApplicationsToTheQueueIfTheInterfaceHasBeenDisabled() {
        EasyMock.expect(formTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationTransfer());
        EasyMock.expect(throttleServiceMock.isPorticoInterfaceEnabled()).andReturn(true);
        templateMock.convertAndSend(EasyMock.eq(queueMock), EasyMock.eq(form.getApplicationNumber()), EasyMock.isA(MessagePostProcessor.class));
        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
        porticoQueueService.sendToPortico(form);
        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
    }
    
//    @Test
//    public void shouldSendApprovedApplicationsToTheQueueWhichPreviouslyHaveNotBeenSent() {
//        porticoQueueService = new ExportQueueService() {
//            @Override
//            public void sendToPortico(final ApplicationForm form) {
//                assertEquals(ApplicationFormStatus.APPROVED, form.getStatus());
//            }
//        };
//        porticoQueueService.setExportService(exportServiceMock);
//        porticoQueueService.setFormTransferService(formTransferServiceMock);
//        porticoQueueService.setThrottleService(throttleServiceMock);
//        porticoQueueService.setTemplate(templateMock);
//        porticoQueueService.setQueue(queueMock);
//        
//        ApplicationForm approved = new ValidApplicationFormBuilder().build();
//        ApplicationForm rejected = new ValidApplicationFormBuilder().build();
//        approved.setStatus(ApplicationFormStatus.APPROVED);
//        rejected.setStatus(ApplicationFormStatus.REJECTED);
//        
//        ApplicationFormTransfer transferApproved = new ApplicationFormTransferBuilder().applicationForm(approved).build();
//        ApplicationFormTransfer transferRejected = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        
//        EasyMock.expect(formTransferServiceMock.getAllTransfersWaitingToBeSentToPorticoOldestFirst()).andReturn(Arrays.asList(transferApproved, transferRejected));
//        
//        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
//        
//        porticoQueueService.sendQueuedApprovedApplicationsToPortico();
//        
//        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
//    }
//    
//    @Test
//    public void shouldSendRejectedOrWithdrawnApplicationsToTheQueueWhichPreviouslyHaveNotBeenSent() {
//        final List<ApplicationForm> applications = Lists.newArrayList();
//        porticoQueueService = new ExportQueueService() {
//            @Override
//            public void sendToPortico(final ApplicationForm form) {
//                applications.add(form);
//            }
//        };
//        porticoQueueService.setExportService(exportServiceMock);
//        porticoQueueService.setFormTransferService(formTransferServiceMock);
//        porticoQueueService.setThrottleService(throttleServiceMock);
//        porticoQueueService.setTemplate(templateMock);
//        porticoQueueService.setQueue(queueMock);
//        
//        ApplicationForm approved = new ValidApplicationFormBuilder().build();
//        ApplicationForm rejected = new ValidApplicationFormBuilder().build();
//        approved.setStatus(ApplicationFormStatus.APPROVED);
//        rejected.setStatus(ApplicationFormStatus.REJECTED);
//        
//        ApplicationFormTransfer transferApproved = new ApplicationFormTransferBuilder().applicationForm(approved).build();
//        ApplicationFormTransfer transferRejected = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        
//        EasyMock.expect(formTransferServiceMock.getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds()).andReturn(Arrays.asList(777L, 888L));
//        EasyMock.expect(formTransferServiceMock.getById(777L)).andReturn(transferApproved);
//        EasyMock.expect(formTransferServiceMock.getById(888L)).andReturn(transferRejected);
//        
//        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
//        
//        porticoQueueService.sendApplicationsToBeSentToPortico(50);
//        
//        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
//        
//        Assert.assertThat(applications, Matchers.contains(approved, rejected));
//    }
//    
//    @Test
//    public void shouldStopSendingApplicationsIfTheMaximumNumberOfApplicationsHaveBeenReached() {
//        porticoQueueService = new ExportQueueService() {
//            @Override
//            public void sendToPortico(final ApplicationForm form) {
//                numberOfSentApplications++;
//            }
//        };
//        porticoQueueService.setExportService(exportServiceMock);
//        porticoQueueService.setFormTransferService(formTransferServiceMock);
//        porticoQueueService.setThrottleService(throttleServiceMock);
//        porticoQueueService.setTemplate(templateMock);
//        porticoQueueService.setQueue(queueMock);
//        
//        ApplicationForm rejected = new ValidApplicationFormBuilder().build();
//        rejected.setStatus(ApplicationFormStatus.REJECTED);
//        
//        ApplicationFormTransfer transferRejected1 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        ApplicationFormTransfer transferRejected2 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        ApplicationFormTransfer transferRejected3 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        ApplicationFormTransfer transferRejected4 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        ApplicationFormTransfer transferRejected5 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        ApplicationFormTransfer transferRejected6 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        ApplicationFormTransfer transferRejected7 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//        
//        EasyMock.expect(formTransferServiceMock.getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds()).andReturn(Arrays.asList(111L, 222L, 333L, 444L, 555L, 666L, 777L));
//        EasyMock.expect(formTransferServiceMock.getById(111L)).andReturn(transferRejected1);
//        EasyMock.expect(formTransferServiceMock.getById(222L)).andReturn(transferRejected2);
//        EasyMock.expect(formTransferServiceMock.getById(333L)).andReturn(transferRejected3);
//        EasyMock.expect(formTransferServiceMock.getById(444L)).andReturn(transferRejected4);
//        EasyMock.expect(formTransferServiceMock.getById(555L)).andReturn(transferRejected5);
//        EasyMock.expect(formTransferServiceMock.getById(666L)).andReturn(transferRejected6);
//        EasyMock.expect(formTransferServiceMock.getById(777L)).andReturn(transferRejected7);
//        
//        EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
//        
//        porticoQueueService.sendApplicationsToBeSentToPortico(2);
//        
//        EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
//        
//        assertEquals(2, numberOfSentApplications);
//    }
//    
//    @Test
//    public void shouldSendAllRejectedOrWithdrawnApplicationsToTheQueueWithoutBatching() {
//    	porticoQueueService = new ExportQueueService() {
//    		@Override
//    		public void sendToPortico(final ApplicationForm form) {
//    			numberOfSentApplications++;
//    		}
//    	};
//    	porticoQueueService.setExportService(exportServiceMock);
//    	porticoQueueService.setFormTransferService(formTransferServiceMock);
//    	porticoQueueService.setThrottleService(throttleServiceMock);
//    	porticoQueueService.setTemplate(templateMock);
//    	porticoQueueService.setQueue(queueMock);
//    	
//    	ApplicationForm rejected = new ValidApplicationFormBuilder().build();
//    	rejected.setStatus(ApplicationFormStatus.REJECTED);
//    	
//    	ApplicationFormTransfer transferRejected1 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//    	ApplicationFormTransfer transferRejected2 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//    	ApplicationFormTransfer transferRejected3 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//    	ApplicationFormTransfer transferRejected4 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//    	ApplicationFormTransfer transferRejected5 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//    	ApplicationFormTransfer transferRejected6 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//    	ApplicationFormTransfer transferRejected7 = new ApplicationFormTransferBuilder().applicationForm(rejected).build();
//    	
//    	EasyMock.expect(formTransferServiceMock.getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds()).andReturn(Arrays.asList(111L, 222L, 333L, 444L, 555L, 666L, 777L));
//        EasyMock.expect(formTransferServiceMock.getById(111L)).andReturn(transferRejected1);
//        EasyMock.expect(formTransferServiceMock.getById(222L)).andReturn(transferRejected2);
//        EasyMock.expect(formTransferServiceMock.getById(333L)).andReturn(transferRejected3);
//        EasyMock.expect(formTransferServiceMock.getById(444L)).andReturn(transferRejected4);
//        EasyMock.expect(formTransferServiceMock.getById(555L)).andReturn(transferRejected5);
//        EasyMock.expect(formTransferServiceMock.getById(666L)).andReturn(transferRejected6);
//        EasyMock.expect(formTransferServiceMock.getById(777L)).andReturn(transferRejected7);
//    	
//    	EasyMock.replay(formTransferServiceMock, throttleServiceMock, templateMock);
//    	
//    	porticoQueueService.sendApplicationsToBeSentToPortico(0);
//    	
//    	EasyMock.verify(formTransferServiceMock, throttleServiceMock, templateMock);
//    	
//    	assertEquals(7, numberOfSentApplications);
//    }

}
