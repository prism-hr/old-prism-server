package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewerAssignedModel;
import com.zuehlke.pgadmissions.domain.ReviewersListModel;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotReviewApprovedApplicationException;
import com.zuehlke.pgadmissions.services.UserService;

public class ReviewControllerTest {

	private RegisteredUser reviewer;
	private ApplicationFormDAO applicationFormDAOMock;
	private ReviewController controller;
	private ApplicationForm form;
	private UserDAO userDAOMock;
	private UserService userService;

	@Test
	public void shouldReturnReviwersViewName() {
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(form);
		EasyMock.replay(applicationFormDAOMock);
		
		assertEquals("reviewer/reviewer", controller.getReviewerPage(1).getViewName());
	}
	
	@Test
	public void shouldgetListOfReviewersToApplication(){
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(form);
		EasyMock.replay(applicationFormDAOMock);
		
		EasyMock.expect(userService.getReviewersForApplication(form)).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userService);

		
		ReviewersListModel model = (ReviewersListModel) controller.getReviewerPage(1).getModel().get("model");
		ApplicationForm reviewedApplication = model.getApplication();
		assertEquals(form, reviewedApplication);
		assertNotNull(model.getReviewers());
	}
	
	@Test
	public void shouldAssignReviewerToApplication() {

		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(form);
		applicationFormDAOMock.save(form);
		EasyMock.replay(applicationFormDAOMock);
		
		EasyMock.expect(userService.getReviewersForApplication(form)).andReturn(Arrays.asList(reviewer));
		EasyMock.expect(userService.getUser(1)).andReturn(reviewer);
		EasyMock.replay(userService);
		
		userDAOMock.save(reviewer);
		EasyMock.expect(userDAOMock.get(1)).andReturn(reviewer);
		EasyMock.replay(userDAOMock);
		ReviewerAssignedModel model = (ReviewerAssignedModel) controller.addReviewer(1, 1).getModel().get("model");
		ApplicationForm application = model.getApplication();
		assertEquals(1, application.getReviewers().size());
	}
	
	@Test(expected=CannotReviewApprovedApplicationException.class)
	public void shouldThrowExceptionWhenAssigningReviewerToApprovedApplication() {
		ApplicationForm approvedForm = new ApplicationFormBuilder().id(1).approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(approvedForm);
		applicationFormDAOMock.save(approvedForm);
		EasyMock.replay(applicationFormDAOMock);
		
		EasyMock.expect(userService.getReviewersForApplication(approvedForm)).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userService);
		
		userDAOMock.save(reviewer);
		EasyMock.expect(userDAOMock.get(1)).andReturn(reviewer);
		EasyMock.replay(userDAOMock);
		
		controller.getReviewerPage(1);
	}
	
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		reviewer = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		authenticationToken.setDetails(reviewer);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		userDAOMock = EasyMock.createMock(UserDAO.class);
		userService = EasyMock.createMock(UserService.class);
		controller = new ReviewController(applicationFormDAOMock, userService);
		
		form = new ApplicationFormBuilder().id(1).toApplicationForm();
		
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
