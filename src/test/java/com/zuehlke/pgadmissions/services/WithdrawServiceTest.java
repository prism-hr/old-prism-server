package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.jms.PorticoQueueService;

public class WithdrawServiceTest {

	private ApplicationsService applicationServiceMock;
	
	private MailService mailServiceMock;
	
	private RefereeService refereeServiceMock;
	
	private WithdrawService service;
	
	private PorticoQueueService porticoQueueServiceMock;
	
	@Test
	public void shouldSaveFormAndSendEmails() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		applicationServiceMock.save(applicationForm);
		Referee referee1 = new RefereeBuilder().id(1).build();
		Referee referee2 = new RefereeBuilder().id(2).build();
		List<Referee> referees = Arrays.asList(referee1, referee2 );
		EasyMock.expect(refereeServiceMock.getRefereesWhoHaveNotProvidedReference(applicationForm)).andReturn(referees);
	
		mailServiceMock.sendWithdrawMailToAdminsReviewersInterviewersSupervisors(referees, applicationForm);
		EasyMock.replay(applicationServiceMock, refereeServiceMock, mailServiceMock);
		service.saveApplicationFormAndSendMailNotifications(applicationForm);
		EasyMock.verify(applicationServiceMock, refereeServiceMock, mailServiceMock);
	}
	
	@Before
	public void setup(){
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		mailServiceMock = EasyMock.createMock(MailService.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		porticoQueueServiceMock = EasyMock.createMock(PorticoQueueService.class);
		service = new WithdrawService(applicationServiceMock, mailServiceMock, refereeServiceMock, porticoQueueServiceMock);
	}
}
