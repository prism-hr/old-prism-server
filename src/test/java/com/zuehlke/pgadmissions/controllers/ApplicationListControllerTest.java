package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationListControllerTest {

	private RegisteredUser user;
	private ApplicationsService applicationsServiceMock;
	private ApplicationListController controller;
	private UserService userServiceMock;

	@Test
	public void shouldReturnCorrectView() {

		assertEquals("private/my_applications_page", controller.getApplicationListPage());
	}

	@Test
	public void shouldAddUserCurrentUser() {
		
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
		EasyMock.replay(userServiceMock);
		assertEquals(user, controller.getUser());
	}
	
	@Test
	public void shouldReturnNullMessageForNullParams() {

		assertNull( controller.getMessage(false, null));
	
	}

	@Test
	public void shouldAddSubmissionSuccesMessageIfRequired() {

		assertEquals("Your application has been successfully submitted.", controller.getMessage(true, null));
	
	}

	@Test
	public void shouldAddDecissionMessageIfRequired() {
		assertEquals("The application was successfully bobbed.", controller.getMessage(false, "bobbed"));
	}

	@Test
	public void shouldAddAllApplications() {
		ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getVisibleApplications(user)).andReturn(Arrays.asList(applicationOne, applicationTwo));
		EasyMock.replay(applicationsServiceMock);			
		controller =new ApplicationListController(applicationsServiceMock, userServiceMock){

			@Override
			public RegisteredUser getUser() {
				return user;
			}
				
		};
		List<ApplicationForm> applications = controller.getApplications();
		assertEquals(2, applications.size());
		assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));

	}

	@Before
	public void setUp() {
		
		user = new RegisteredUserBuilder().id(1).toUser();
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ApplicationListController(applicationsServiceMock, userServiceMock);

	}



}
