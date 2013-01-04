package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.pagemodels.PageModel;

public class ErrorControllerTest {

	@Test
	public void shouldSetCurrentUserOnModelAndReturnErrorView(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		ModelAndView modelAndView = new ErrorController().getErrorPage();
		PageModel model = (PageModel) modelAndView.getModel().get("model");		
		assertEquals("/public/error/error", modelAndView.getViewName());
		assertEquals(currentUser, model.getUser());
		
	}
	
	@After
	public void tearDownw(){
		SecurityContextHolder.clearContext();
	}
}
