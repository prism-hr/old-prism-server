package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class SubmitApplicationFormControllerTest {

	private SubmitApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;


	@Test
	public void shouldLoadApplicationFormByIdAndChangeSubmissionStatusToSubmitted() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		form.setApplicant(student);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		assertEquals(SubmissionStatus.UNSUBMITTED, form.getSubmissionStatus());
		assertEquals("redirect:/applications?submissionSuccess=true", applicationController.submitApplication(form).getViewName());
		assertEquals(SubmissionStatus.SUBMITTED, form.getSubmissionStatus());
		EasyMock.verify(applicationsServiceMock);

	}

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmitterNotFormAcpplicant() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(form);		
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);

		form.setApplicant(student);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser otherApplicant = new RegisteredUserBuilder().id(6).username("fred").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole())
		.toUser();
		authenticationToken.setDetails(otherApplicant);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);		

		applicationController.submitApplication(form);
	}


	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationFormDoesNotExist() {
		Integer id = 2;		
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(null);	
		EasyMock.replay(applicationsServiceMock);		
		applicationController.submitApplication(applicationsServiceMock.getApplicationById(id));
	}


	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowSubmitExceptionIfApplicationIsAlreadySubmitted() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(form);		
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);

		form.setApplicant(student);

		applicationController.submitApplication(form);
	}

	@Before
	public void setUp() {

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);

		applicationController = new SubmitApplicationFormController(applicationsServiceMock, userPropertyEditorMock) {
			ApplicationForm newApplicationForm() {
				return applicationForm;
			}
		};

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").address("london").firstName("mark").lastName("ham").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(student);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}

