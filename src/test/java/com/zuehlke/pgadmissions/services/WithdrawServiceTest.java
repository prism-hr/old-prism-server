package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.jms.PorticoQueueService;
import com.zuehlke.pgadmissions.mail.refactor.MailSendingService;

public class WithdrawServiceTest {

	private ApplicationsService applicationServiceMock;
	
	private MailSendingService mailServiceMock;
	
	private WithdrawService service;
	
	private PorticoQueueService porticoQueueServiceMock;
	
	@Test
	public void shouldSaveFormAndSendEmails() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		applicationServiceMock.save(applicationForm);
		mailServiceMock.scheduleWithdrawalConfirmation(applicationForm);
		EasyMock.replay(applicationServiceMock, mailServiceMock, porticoQueueServiceMock);
		service.saveApplicationFormAndSendMailNotifications(applicationForm);
		EasyMock.verify(applicationServiceMock, mailServiceMock, porticoQueueServiceMock);
	}
	
	@Test
	public void shouldSendFormToPortico() {
	    ApplicationForm form = new ApplicationFormBuilder().id(1).submittedDate(new Date()).status(ApplicationFormStatus.VALIDATION).build();
	    EasyMock.expect(porticoQueueServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationFormTransfer());
	    EasyMock.replay(applicationServiceMock, mailServiceMock, porticoQueueServiceMock);
	    service.sendToPortico(form);
        EasyMock.verify(applicationServiceMock, mailServiceMock, porticoQueueServiceMock);
	}
	
	@Before
	public void setup(){
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		mailServiceMock = EasyMock.createMock(MailSendingService.class);
		porticoQueueServiceMock = EasyMock.createMock(PorticoQueueService.class);
		service = new WithdrawService(applicationServiceMock, mailServiceMock, porticoQueueServiceMock);
	}
}
