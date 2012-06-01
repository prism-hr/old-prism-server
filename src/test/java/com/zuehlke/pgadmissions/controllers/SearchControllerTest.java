package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SearchCategories;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class SearchControllerTest {

	private SearchController controller;
	private ApplicationsService applicationsServiceMock;
	private UserService userServiceMock;
	private RegisteredUser user;
	
	
	@Test
	public void shouldReturnCorrectViewForApplicationListSection() {
		assertEquals("private/my_applications_section", controller.getApplicationsContainingTermInCategory());
	}
	
	@Test
	public void shouldAddUserCurrentUser() {
		
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
		EasyMock.replay(userServiceMock);
		assertEquals(user, controller.getUser());
	}
	
	@Test
	public void shouldReturnAllApplicationsMatchedGivenTermByProgramNameColumn(){
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().code("Program_Science_1").id(1).toProgram()).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications("sCiEnce", SearchCategories.PROGRAMME_NAME, user)).andReturn(Arrays.asList(application));
		EasyMock.replay(applicationsServiceMock);
		controller.getApplications("sCiEnce", SearchCategories.PROGRAMME_NAME);
		EasyMock.verify(applicationsServiceMock);
	}
	
	
	@Test
	public void shouldReturnAllApplicationsMatchedGivenTermByApplicationCodeColumn(){
		ApplicationForm application = new ApplicationFormBuilder().applicationNumber("Application_number_117_").id(1).program(new ProgramBuilder().code("Program_Science_1").id(1).toProgram()).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications("117", SearchCategories.APPLICATION_CODE, user)).andReturn(Arrays.asList(application));
		EasyMock.replay(applicationsServiceMock);
		controller.getApplications("117", SearchCategories.APPLICATION_CODE);
		EasyMock.verify(applicationsServiceMock);
	}
	
	
	@Test
	public void shouldReturnAllApplicationsMatchedGivenTermByApplicantNameColumn(){
		ApplicationForm application = new ApplicationFormBuilder().applicant(new RegisteredUserBuilder().id(1).lastName("Freeman").toUser()).applicationNumber("Application_number_117_").id(1).program(new ProgramBuilder().code("Program_Science_1").id(1).toProgram()).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications("free", SearchCategories.APPLICANT_NAME, user)).andReturn(Arrays.asList(application));
		EasyMock.replay(applicationsServiceMock);
		controller.getApplications("free", SearchCategories.APPLICANT_NAME);
		EasyMock.verify(applicationsServiceMock);
	}
	
	
	@Before
	public void setUp() {
		user = new RegisteredUserBuilder().id(1).toUser();
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new SearchController(applicationsServiceMock, userServiceMock){
			@Override 
			public RegisteredUser getUser() {
				return user;
			}
		};
	}
	
	
}
