package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

	RegisteredUser user;
	private ApplicationsService applicationsServiceMock;
	private ApplicationListController controller;
	private UserService userServiceMock;

	@Test
	public void shouldReturnCorrectViewForApplicationListPage() {

		assertEquals("private/my_applications_page", controller.getApplicationListPage());
	}
//  TODO: Kevin - Fix unit Tests
//	@Test
//	public void shouldReturnCorrectViewForApplicationListSection() {
//		assertEquals("private/my_applications_section", controller.getApplicationListSection());
//	}

	@Test
	public void shouldAddUserCurrentUser() {

		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
		EasyMock.replay(userServiceMock);
		assertEquals(user, controller.getUser());
	}

	@Test
	public void shouldReturnNullMessageForNullParams() {
		assertNull(controller.getMessage(false, null, null));
	}

	@Test
	public void shouldGetApplicationFormByNumber(){
		String appNumber = "abc";
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(appNumber)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		assertEquals(applicationForm, controller.getApplicationForm(appNumber));
	}
	
	@Test
	public void shouldAddSubmissionSuccesMessageIfRequired() {
		assertEquals("Your application has been successfully submitted.", controller.getMessage(true, null, null));
	}

	@Test
	public void shouldAddDecissionMessageIfRequired() {
		assertEquals("The application was successfully bobbed.", controller.getMessage(false, "bobbed", null));
	}
	
	@Test
	public void shouldAddPassedMessageIfRequired() {
		assertEquals("my message", controller.getMessage(false, null, "my message"));
	}
	
//  TODO: Kevin - Fix unit Tests
//	@Test
//	public void shouldAddAllApplications() {
//		ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).toApplicationForm();
//		ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
//		EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user,// 
//				SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4))//
//				.andReturn(Arrays.asList(applicationOne, applicationTwo));
//		EasyMock.replay(applicationsServiceMock);
//		controller = new ApplicationListController(applicationsServiceMock, userServiceMock) {
//
//			@Override
//			public RegisteredUser getUser() {
//				return user;
//			}
//
//		};
//		List<ApplicationForm> applications = controller.getApplications(SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4);
//		
//		EasyMock.verify(applicationsServiceMock);
//		Assert.assertEquals(2, applications.size());
//		Assert.assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));
//	}

//  TODO: Kevin - Fix unit Tests
//	@Test
//	public void shouldReturnFirstBlockOfApplications() {
//		ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).toApplicationForm();
//		ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
//		EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user,// 
//				SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 1))//
//				.andReturn(Arrays.asList(applicationOne, applicationTwo));
//		EasyMock.replay(applicationsServiceMock);
//		controller = new ApplicationListController(applicationsServiceMock, userServiceMock) {
//			
//			@Override
//			public RegisteredUser getUser() {
//				return user;
//			}
//			
//		};
//		
//		List<ApplicationForm> applications = controller.getApplications(SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, null);
//		
//		EasyMock.verify(applicationsServiceMock);
//		Assert.assertEquals(2, applications.size());
//		Assert.assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));
//	}

	@Before
	public void setUp() {
		user = new RegisteredUserBuilder().id(1).toUser();
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ApplicationListController(applicationsServiceMock, userServiceMock);
	}
}
