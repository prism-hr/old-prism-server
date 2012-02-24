package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.pagemodels.ViewApplicationModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ViewApplicationFormControllerTest {
	

	private ViewApplicationFormController viewApplicationFormController;
	private ApplicationForm form;
	private RegisteredUser user;
	private ApplicationsService applicationsServiceMock;


	@Test
	public void shouldReturnViewApplicationViewName(){
		assertEquals("viewApplication", viewApplicationFormController.getViewApplicationPage(1).getViewName());
	}
	
	@Test
	public void shouldGetApplicationFromApplicationId(){
	
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView viewApplicationPage = viewApplicationFormController.getViewApplicationPage(1);
		assertEquals(form, ((ViewApplicationModel)viewApplicationPage.getModel().get("model")).getApplicationForm());
	}

	
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		viewApplicationFormController = new ViewApplicationFormController(applicationsServiceMock);
		form = new ApplicationFormBuilder().id(1).toApplicationForm();
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
	
}
