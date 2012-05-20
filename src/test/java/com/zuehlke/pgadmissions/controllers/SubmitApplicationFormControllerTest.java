package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

public class SubmitApplicationFormControllerTest {

	private SubmitApplicationFormController applicationController;

	private ApplicationsService applicationsServiceMock;

	private RegisteredUser student;

	private UsernamePasswordAuthenticationToken authenticationToken;

	private ApplicationFormValidator applicationFormValidatorMock;
	

	private StageDurationDAO stageDurationDAOMock;

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
		EasyMock.replay(errorsMock);
		String view = applicationController.submitApplication(applicationForm, errorsMock);
		assertEquals("/private/pgStudents/form/main_application_page", view);
	}


	@Test
	public void shouldChangeStatusToValidateAndSaveIfNoErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).toApplicationForm();		
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);		
		StageDuration stageDuration = new StageDuration();
		stageDuration.setDuration(8);
		stageDuration.setUnit(DurationUnitEnum.HOURS);
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDuration);
		applicationsServiceMock.save(applicationForm);
		
		EasyMock.replay(applicationsServiceMock, errorsMock, stageDurationDAOMock);
		
		
		applicationController.submitApplication(applicationForm, errorsMock);

		EasyMock.verify(applicationsServiceMock);
		assertEquals(ApplicationFormStatus.VALIDATION, applicationForm.getStatus());	
		assertEquals(DateUtils.truncate(DateUtils.addHours(new Date(), 8), Calendar.HOUR), DateUtils.truncate(applicationForm.getDueDate(), Calendar.HOUR));
		assertEquals(2, applicationForm.getEvents().size());
		assertEquals(ApplicationFormStatus.VALIDATION, applicationForm.getEvents().get(1).getNewStatus());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(applicationForm.getEvents().get(1).getDate(), Calendar.DATE));
		
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(),Calendar.DATE), DateUtils.truncate(applicationForm.getSubmittedDate(), Calendar.DATE));
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(),Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));		
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));

	}

	@Test
	public void shouldRedirectToAppsViewWithMessageIfNoErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).toApplicationForm();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		StageDuration stageDuration = new StageDuration();
		stageDuration.setDuration(1);
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDuration);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock, errorsMock,stageDurationDAOMock);
		String view = applicationController.submitApplication(applicationForm, errorsMock);
		assertEquals("redirect:/applications?submissionSuccess=true", view);
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

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.UNSUBMITTED).applicant(student)
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
	public void shouldThrowSubmitExceptionIfApplicationIsDecided() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).status(ApplicationFormStatus.APPROVED).toApplicationForm();
		applicationController.submitApplication(applicationForm, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowSubmitExceptionIfUserCannotSeeApplicationForm() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(3)).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationsServiceMock, userMock);

		applicationController.getApplicationForm(3);
	}

	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		

		applicationFormValidatorMock = EasyMock.createMock(ApplicationFormValidator.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		applicationController = new SubmitApplicationFormController(applicationsServiceMock, applicationFormValidatorMock, stageDurationDAOMock);

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
