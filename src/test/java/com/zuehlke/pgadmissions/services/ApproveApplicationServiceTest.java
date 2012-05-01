package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;

public class ApproveApplicationServiceTest {

	private ApplicationsService applicationsServiceMock;
	private ApproveApplicationService submitApplicationService;
	private MailService mailServiceMock;

	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		mailServiceMock = EasyMock.createMock(MailService.class);		
		submitApplicationService = new ApproveApplicationService(applicationsServiceMock, mailServiceMock);

	}


	@Test
	public void shouldSaveApplicationFormAndSendEmailsToRefereesAdminsAndApplicant() throws UnsupportedEncodingException {

		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		applicationsServiceMock.save(form);
		mailServiceMock.sendSubmissionMailToReferees(form);
		EasyMock.replay(applicationsServiceMock, mailServiceMock);
		submitApplicationService.saveApplicationFormAndSendMailNotifications(form);
		EasyMock.verify(applicationsServiceMock, mailServiceMock);
	}

	

	@Test
	public void shouldNotSendEmailIfSaveFails() throws UnsupportedEncodingException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

		EasyMock.replay(applicationsServiceMock, mailServiceMock);
		try {
			submitApplicationService.saveApplicationFormAndSendMailNotifications(applicationForm);
		} catch (RuntimeException e) {
			// expected...ignore
		}

		EasyMock.verify(applicationsServiceMock, mailServiceMock);
	}
}
