package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotReviewApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ReviewersListModel;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ReviewControllerTest {

	private RegisteredUser reviewer;
	private ReviewController controller;
	private ApplicationForm form;
	private UserService userServiceMock;
	private ApplicationsService applicationsServiceMock;
	private UserPropertyEditor userPropertyEditorMoc;


	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMoc);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnReviwersViewName() {

		assertEquals("reviewer/reviewer",
				controller.getReviewerPage(new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm()).getViewName());
	}

	@Test
	public void shouldgetListOfReviewersAndAddToApplication() {
		EasyMock.expect(userServiceMock.getReviewersForApplication(form)).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userServiceMock);

		ReviewersListModel model = (ReviewersListModel) controller
				.getReviewerPage(new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm()).getModel().get("model");
		ApplicationForm reviewedApplication = model.getApplication();
		assertEquals(form, reviewedApplication);
		assertNotNull(model.getReviewers());
	}

	
	@Test
	public void shouldAddCurrentUserToAModel() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).toUser();
		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		

		ReviewersListModel model = (ReviewersListModel) controller
				.getReviewerPage(new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm()).getModel().get("model");
		assertEquals(currentUser, model.getUser());


	}
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfAPplicationDoesNotExist() {

		controller.getReviewerPage(null);
	}

	@Test(expected = CannotReviewApplicationException.class)
	public void shouldThrowCannotReviewApprovedApplicationExceptionIfApplicationnNotReviewable() {
		ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormMock.isReviewable()).andReturn(false);
		EasyMock.replay(applicationFormMock);
		controller.getReviewerPage(applicationFormMock);
	}

	@Test
	public void shouldGetApplicationFromFromService() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		assertEquals(applicationForm, controller.getApplicationForm(5));
	}

	@Test
	public void shouldSaveApplicationForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView redirectModel = controller.updateReviewers(applicationForm);
		Assert.assertEquals("redirect:/reviewer/assign", redirectModel.getViewName());
		EasyMock.verify(applicationsServiceMock);
			
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationDoesNotExist() {

		controller.updateReviewers(null);
	}

	@Test(expected = CannotReviewApplicationException.class)
	public void shouldThrowCannotReviewApprovedApplicationExceptionIfSubmittedApplicationnNotReviewable() {
		ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormMock.isReviewable()).andReturn(false);
		EasyMock.replay(applicationFormMock);
		controller.updateReviewers(applicationFormMock);
	}
	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		reviewer = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		authenticationToken.setDetails(reviewer);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMoc = EasyMock.createMock(UserPropertyEditor.class);		
		controller = new ReviewController(applicationsServiceMock, userServiceMock, userPropertyEditorMoc);

		form = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
