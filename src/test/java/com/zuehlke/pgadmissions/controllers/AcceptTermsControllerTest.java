package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationFormService;

public class AcceptTermsControllerTest {

	private AcceptTermsController acceptTermsController;
	private ApplicationFormService applicationsServiceMock;
	private RegisteredUser student;

	@Test
	public void shouldUpdateApplicationWithAcceptedTermsAndReturnApplicationPage() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.UNSUBMITTED).applicationNumber("ABC")
				.applicant(student).id(2).build();
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		String view = acceptTermsController.acceptTermsAndGetApplicationPage(applicationForm);
		EasyMock.verify(applicationsServiceMock);
		assertEquals("redirect:/application?view=view&applicationId=" + applicationForm.getApplicationNumber(), view);
	}

	@Test
	public void shouldGetApplicationForm() {
		String applicationNumber = "abc";
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getByApplicationNumber("abc")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.isApplicant(applicationForm)).andReturn(true);
		ApplicationForm returnedForm = acceptTermsController.getApplicationForm(applicationNumber);
		assertEquals(applicationForm, returnedForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfNotApplciationFoud() {
		String applicationNumber = "abc";		
		EasyMock.expect(applicationsServiceMock.getByApplicationNumber("abc")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		acceptTermsController.getApplicationForm(applicationNumber);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserIsNotApplicant() {
		String applicationNumber = "abc";
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getByApplicationNumber("abc")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.isApplicant(applicationForm)).andReturn(false);
		acceptTermsController.getApplicationForm(applicationNumber);
		
	}
	@Test
	public void shouldGetAcceptedTermsView() {
		assertEquals("/private/pgStudents/form/components/terms_and_conditions", acceptTermsController.getAcceptedTermsView());
	}

	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationFormService.class);
		acceptTermsController = new AcceptTermsController(applicationsServiceMock);

		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
	}
	
}
