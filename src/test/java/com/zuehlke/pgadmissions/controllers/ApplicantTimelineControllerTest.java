package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicantTimelineControllerTest {
	private ApplicationsService applicationsServiceMock;
	private UserService userServiceMock;
	private ApplicantTimelineController controller;

	
	@Test
	public void shouldGetApplicationFormFromId() {
		RegisteredUser currentUser = new RegisteredUserBuilder().id(99).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicant(currentUser).build();		
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);		
		EasyMock.replay(userServiceMock);

		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		ApplicationForm returnedApplication = controller.getApplicationForm("5");
		assertEquals(returnedApplication, applicationForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserIsNotApplicantOfForm() {
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicant(new RegisteredUserBuilder().id(4).build()).build();		
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);		
		EasyMock.expect(currentUser.getId()).andReturn(99).anyTimes();
		EasyMock.replay(currentUser, userServiceMock);
		
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");

	}

	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);				
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);		
		EasyMock.replay(userServiceMock);
		assertEquals(currentUser, controller.getUser());
	}


	@Test
	public void shouldReturnApplicantTimelinePage(){
		assertEquals("/private/pgStudents/form/timelinepage", controller.getTimelinePage());
	}
	
	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);		
		controller = new ApplicantTimelineController(applicationsServiceMock, userServiceMock);

	}
}
