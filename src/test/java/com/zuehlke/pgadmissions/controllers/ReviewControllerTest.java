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
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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
	private UserPropertyEditor userPropertyEditorMock;
	private UsernamePasswordAuthenticationToken authenticationToken;

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnReviwersViewName() {

		assertEquals("private/staff/admin/assign_reviewers_page",
				controller.getReviewerPage(new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm()).getViewName());
	}

	@Test
	public void shouldgetListOfReviewersAndAddToApplication() {
		EasyMock.expect(userServiceMock.getReviewersForApplication(form)).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userServiceMock);

		ReviewersListModel model = (ReviewersListModel) controller
				.getReviewerPage(new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm()).getModel().get("model");
		ApplicationForm reviewedApplication = model.getApplicationForm();
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
				.getReviewerPage(new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm()).getModel().get("model");
		assertEquals(currentUser, model.getUser());
	}

	@Test
	public void shouldGetApplicationFromFromService() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(userMock, applicationsServiceMock);
		assertEquals(applicationForm, controller.getApplicationForm(5));
	}

	@Test
	public void shouldSaveApplicationForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView redirectModel = controller.updateReviewers(applicationForm);
		Assert.assertEquals("redirect:/reviewer/assign", redirectModel.getViewName());
		EasyMock.verify(applicationsServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionApplicationFormDoesNotExist() {

		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserCannotSeeApplication() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(userMock, applicationsServiceMock);
		controller.getApplicationForm(5);
	}

	@Test(expected = CannotReviewApplicationException.class)
	public void shouldThrowCannotReviewApprovedApplicationExceptionIfSubmittedApplicationnNotReviewable() {
		ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormMock.isModifiable()).andReturn(false);
		EasyMock.replay(applicationFormMock);
		controller.updateReviewers(applicationFormMock);
	}

	@Before
	public void setUp() {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		reviewer = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		authenticationToken.setDetails(reviewer);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);
		controller = new ReviewController(applicationsServiceMock, userServiceMock, userPropertyEditorMock);

		form = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
