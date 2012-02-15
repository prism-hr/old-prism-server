package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.ReviewerService;


public class AssignReviewerControllerTest {

	private RegisteredUser user;
	private AssignReviewerController controller;
	private ReviewerService reviewerService;
	private ApplicationFormDAO applicationFormDAOMock;
	private UserDAO userDAOMock;
	
	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		userDAOMock = EasyMock.createMock(UserDAO.class);
		reviewerService = new ReviewerService(applicationFormDAOMock, userDAOMock);
		controller = new AssignReviewerController(reviewerService);
	}
	
	@Test
	public void shouldReturnReviwersViewName() {
		assertEquals("assignReviewer", controller.assignReviewerView(new ModelMap()));
	}
	
	@Test
	public void shouldAssignReviewerToApplication() {
		ApplicationForm app = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().username("bob").toUser();
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(app);
		applicationFormDAOMock.save(app);
		EasyMock.replay(applicationFormDAOMock);
		
		EasyMock.expect(userDAOMock.getUserByUsername("bob")).andReturn(reviewer);
		EasyMock.replay(userDAOMock);
		
		ModelMap modelMap = new ModelMap();
		controller.submitReviewer("bob", 1, modelMap);
		ApplicationForm application = (ApplicationForm) modelMap.get("application");
		
		assertEquals("bob", application.getReviewer().getUsername());
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
	
}
