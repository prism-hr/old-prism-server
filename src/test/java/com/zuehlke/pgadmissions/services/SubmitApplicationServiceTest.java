package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;

public class SubmitApplicationServiceTest {

	private ApplicationsService applicationsServiceMock;
	private SubmitApplicationService submitApplicationService;
	private MailService mailServiceMock;

	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		mailServiceMock = EasyMock.createMock(MailService.class);		
		submitApplicationService = new SubmitApplicationService(applicationsServiceMock, mailServiceMock);

	}


	@Test
	public void shouldSaveApplicationFormAndSendEmailsToAdminsAndApplicant() throws UnsupportedEncodingException {

		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		applicationsServiceMock.save(form);
		mailServiceMock.sendSubmissionMailToAdmins(form);
		mailServiceMock.sendSubmissionMailToApplicant(form);
		EasyMock.replay(applicationsServiceMock, mailServiceMock);
		submitApplicationService.saveApplicationFormAndSendMailNotifications(form);
		EasyMock.verify(applicationsServiceMock, mailServiceMock);
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(form.getLastUpdated(), Calendar.DATE));

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
