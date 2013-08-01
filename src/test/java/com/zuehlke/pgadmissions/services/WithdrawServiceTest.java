package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class WithdrawServiceTest {

	private ApplicationsService applicationServiceMock;
	
	private WithdrawService service;
	
	private PorticoQueueService porticoQueueServiceMock;
	
	@Test
	public void shouldSaveFormAndSendEmails() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock, porticoQueueServiceMock);
		service.saveApplicationFormAndSendMailNotifications(applicationForm);
		EasyMock.verify(applicationServiceMock, porticoQueueServiceMock);
	}
	
	@Test
	public void shouldSendFormToPortico() {
	    ApplicationForm form = new ApplicationFormBuilder().id(1).submittedDate(new Date()).status(ApplicationFormStatus.VALIDATION).build();
	    EasyMock.expect(porticoQueueServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationFormTransfer());
	    EasyMock.replay(applicationServiceMock, porticoQueueServiceMock);
	    service.sendToPortico(form);
        EasyMock.verify(applicationServiceMock, porticoQueueServiceMock);
	}
	
	@Before
	public void setup(){
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		porticoQueueServiceMock = EasyMock.createMock(PorticoQueueService.class);
		service = new WithdrawService(applicationServiceMock, porticoQueueServiceMock);
	}
}
