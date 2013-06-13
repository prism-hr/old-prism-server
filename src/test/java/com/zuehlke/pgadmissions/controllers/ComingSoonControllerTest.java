package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;

public class ComingSoonControllerTest {

	private UserService userServiceMock;
	private ComingSoonController controller;

	
	@Test
	public void shoulReturnCurrentUserAsUser(){
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		
		RegisteredUser user =  controller.getUser();
		assertEquals(currentUser, user);
	}
	
	@Test
	public void shouldReturnComingSoonView(){
		assertEquals("public/common/coming_soon", controller.getComingSoonView());
	}
	@Before
	public void setup(){
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new ComingSoonController(userServiceMock);
	}
	
}
