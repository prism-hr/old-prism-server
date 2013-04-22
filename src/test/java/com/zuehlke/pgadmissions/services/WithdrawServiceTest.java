package com.zuehlke.pgadmissions.services;

import static java.util.Arrays.asList;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.jms.PorticoQueueService;
import com.zuehlke.pgadmissions.mail.MailSendingService;

public class WithdrawServiceTest {

	private ApplicationsService applicationServiceMock;
	
	private MailSendingService mailServiceMock;
	
	private WithdrawService service;
	
	private RefereeService refereeServiceMock;
	
	private PorticoQueueService porticoQueueServiceMock;
	
	@Test
	public void shouldSaveFormAndSendEmails() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		Referee ref1 = new RefereeBuilder().id(2).application(applicationForm).build();
		Referee ref2 = new RefereeBuilder().id(3).application(applicationForm).build();
		applicationServiceMock.save(applicationForm);
		EasyMock.expect(refereeServiceMock.getRefereesWhoHaveNotProvidedReference(applicationForm)).
		    andReturn(asList(ref1, ref2));
		mailServiceMock.scheduleWithdrawalConfirmation(asList(ref1, ref2), applicationForm);
		EasyMock.replay(applicationServiceMock, refereeServiceMock, mailServiceMock, porticoQueueServiceMock);
		service.saveApplicationFormAndSendMailNotifications(applicationForm);
		EasyMock.verify(applicationServiceMock, refereeServiceMock, mailServiceMock, porticoQueueServiceMock);
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
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		mailServiceMock = EasyMock.createMock(MailSendingService.class);
		porticoQueueServiceMock = EasyMock.createMock(PorticoQueueService.class);
		service = new WithdrawService(applicationServiceMock, mailServiceMock, porticoQueueServiceMock, refereeServiceMock);
	}
}
