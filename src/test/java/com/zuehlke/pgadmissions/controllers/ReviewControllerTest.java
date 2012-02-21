package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationReviewDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;


public class ReviewControllerTest {

	private RegisteredUser user;
	private MockHttpServletRequest request;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationReviewDAO applicationReviewDAOMock;
	private ReviewController controller;
	private ApplicationForm form;

	@Test
	public void shouldReturnReviwersViewName() {
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(form);
		applicationFormDAOMock.save(form);
		EasyMock.replay(applicationFormDAOMock);
		assertEquals("reviewApplication", controller.assignReviewer(request, new ModelMap()));
	}
	
	@Test
	public void shouldAssignReviewerToApplication(){
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(form);
		applicationFormDAOMock.save(form);
		EasyMock.replay(applicationFormDAOMock);
		ModelMap modelMap = new ModelMap();
		controller.assignReviewer(request, modelMap);
		ApplicationForm reviewedApplication = (ApplicationForm) modelMap.get("application");
		assertEquals(form, reviewedApplication);
		assertNotNull(reviewedApplication.getReviewer());
	}
	
	@Test
	public void shouldReturnSubmittedReviewPageViewName(){
		assertEquals("reviewSuccess", controller.getSubmittedReviewPage(request, new ModelMap()));
	}
	
	@Test
	public void shouldReturnSubmittedReviewPage(){
		ModelMap modelMap = new ModelMap();
		controller.getSubmittedReviewPage(request, modelMap);
		ApplicationReview reviewApp = (ApplicationReview) modelMap.get("review");
		assertNotNull(reviewApp.getComment());
	}
	
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		request = new MockHttpServletRequest();
		request.setParameter("id", "1");
		request.setParameter("comment", "excellent application!");
		
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		applicationReviewDAOMock = EasyMock.createMock(ApplicationReviewDAO.class);
		controller = new ReviewController(applicationFormDAOMock, applicationReviewDAOMock);
		
		form = new ApplicationFormBuilder().id(1).toApplicationForm();
		
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
