package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.SubmitApplicationService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

public class SubmitApplicationFormControllerTest {

	private SubmitApplicationFormController applicationController;

	private ApplicationsService applicationsServiceMock;

	private RegisteredUser student;

	private SubmitApplicationService submitApplicationServiceMock;
	private RefereeService refereeServiceMock;
	private UsernamePasswordAuthenticationToken authenticationToken;

	private ApplicationFormValidator applicationFormValidatorMock;

	@Test
	public void shouldReturnCurrentUser() {
		assertEquals(student, applicationController.getUser());
	}

	@Test
	public void shouldReturnStudenApplicationViewOnGetForApplicant() {
		String view = applicationController.getApplicationView();
		assertEquals("/private/pgStudents/form/main_application_page", view);
	}
	@Test
	public void shouldReturnStudenApplicationViewOnGetForNonApplicant() {
		RegisteredUser otherUser = new RegisteredUserBuilder().id(6).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		authenticationToken.setDetails(otherUser);
		String view = applicationController.getApplicationView();
		assertEquals("/private/staff/application/main_application_page", view);
	}
	@Test
	public void shouldReturnToApplicationViewIfErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).toApplicationForm();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(submitApplicationServiceMock, errorsMock);
		String view = applicationController.submitApplication(applicationForm, errorsMock);
		assertEquals("/private/pgStudents/form/main_application_page", view);
		EasyMock.verify(submitApplicationServiceMock);
	}

	@Test
	public void shouldChangeStatusToSubmittedAndSaveIfNoErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).toApplicationForm();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		submitApplicationServiceMock.saveApplicationFormAndSendMailNotifications(applicationForm);
		EasyMock.replay(submitApplicationServiceMock, errorsMock);
		applicationController.submitApplication(applicationForm, errorsMock);

		EasyMock.verify(submitApplicationServiceMock);
		assertEquals(SubmissionStatus.SUBMITTED, applicationForm.getSubmissionStatus());
		assertNotNull(applicationForm.getSubmittedDate());
	}

	@Test
	public void shouldRedirectToAppsViewWithMessageIfNoErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).toApplicationForm();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		submitApplicationServiceMock.saveApplicationFormAndSendMailNotifications(applicationForm);
		EasyMock.replay(submitApplicationServiceMock, errorsMock);
		String view = applicationController.submitApplication(applicationForm, errorsMock);
		assertEquals("redirect:/applications?submissionSuccess=true", view);
	}
	
	@Test
	public void shouldProcessRefereesRoleIfNoErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).toApplicationForm();
		List<Referee> referees = Arrays.asList(new RefereeBuilder().id(1).toReferee(), new RefereeBuilder().id(2).toReferee());
		applicationForm.setReferees(referees);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		refereeServiceMock.processRefereesRoles(referees);
		EasyMock.replay(refereeServiceMock, errorsMock);
		applicationController.submitApplication(applicationForm, errorsMock);
		EasyMock.verify(refereeServiceMock);

	}

	@Test
	public void shouldRegisterValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(applicationFormValidatorMock);
		EasyMock.replay(binderMock);
		applicationController.registerValidator(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetApplicationFormFromService() {

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(student)
				.toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		ApplicationForm returnedApplicationForm = applicationController.getApplicationForm(2);
		assertEquals(applicationForm, returnedApplicationForm);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmitterNotFormApplicant() {
		RegisteredUser otherApplicant = new RegisteredUserBuilder().id(6).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(otherApplicant);
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).toApplicationForm();
		applicationController.submitApplication(applicationForm, null);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		applicationController.getApplicationForm(2);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowSubmitExceptionIfApplicationIsAlreadySubmitted() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		applicationController.submitApplication(applicationForm, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowSubmitExceptionIfUserCannotSeeApplicationForm() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(3)).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationsServiceMock, userMock);

		applicationController.getApplicationForm(3);
	}

	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);

		submitApplicationServiceMock = EasyMock.createMock(SubmitApplicationService.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		applicationFormValidatorMock = EasyMock.createMock(ApplicationFormValidator.class);
		applicationController = new SubmitApplicationFormController(applicationsServiceMock, submitApplicationServiceMock, refereeServiceMock,
				applicationFormValidatorMock);

		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
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
