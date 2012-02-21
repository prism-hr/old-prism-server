package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

//private ApproverController approverController;

public class ApproverControllerTest {
	
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationForm form;
	private MockHttpServletRequest request;
	private RegisteredUser user;
	private ApproverController approverController;
	
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("mark").toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		
	}


	private void setRequest(String id, String feedback) {
		request = new MockHttpServletRequest();
		request.setParameter("id", id);
		request.setParameter("feedback", feedback);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		approverController = new ApproverController(applicationFormDAOMock);
		form = new ApplicationFormBuilder().id(1).toApplicationForm();
	}
	

	@Test
	public void shouldSaveApproversFeedbackToApplicationAndReturnFeedbackSuccessView(){
		setRequest("1", "approve");
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(form);
		applicationFormDAOMock.save(form);
		EasyMock.replay(applicationFormDAOMock);
		ModelMap modelMap = new ModelMap();
		String feedbackPage = approverController.getSubmittedFeedbackPage(request, modelMap);
		ApplicationForm approvedApplication = (ApplicationForm) modelMap.get("application");
		assertNotNull(approvedApplication.getApprover());
		assertEquals("mark", approvedApplication.getApprover().getUsername());
		assertEquals("1", approvedApplication.getApproved());
		assertEquals("approverFeedbackSubmitted", feedbackPage);
	}
	
	@Test
	public void shouldReturnFeedbackEmptyErrorView(){
		setRequest("1", null);
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(form);
		applicationFormDAOMock.save(form);
		EasyMock.replay(applicationFormDAOMock);
		ModelMap modelMap = new ModelMap();
		String feedbackPage = approverController.getSubmittedFeedbackPage(request, modelMap);
		ApplicationForm approvedApplication = (ApplicationForm) modelMap.get("application");
		assertNull(approvedApplication.getApprover());
		assertEquals("applicationFeedbackError", feedbackPage);
		assertEquals("You did not specify a feedback. Please approve or reject before saving. ", modelMap.get("message").toString());
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
