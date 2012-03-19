package com.zuehlke.pgadmissions.controllers;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.ProgramsService;


public class ManageUsersControllerTest {

	
	private RegisteredUser currentUser;
	private ProgramsService programsServiceMock;
	private ManageUsersController manageUsersController;

	@Test
	public void shouldReturnCorrectView() {
		Assert.assertEquals("private/staff/superAdmin/assign_roles_page", manageUsersController.getUsersPage().getViewName());
	}
	
	@Before
	public void setup() {

		currentUser = EasyMock.createMock(RegisteredUser.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		manageUsersController = new ManageUsersController(programsServiceMock);


		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
	
}
