package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
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

	@Test
	public void shouldReturnCorrectViewForApplicationListSection() {

		assertEquals("private/my_applications_section", controller.getApplicationListSection());
	}

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

	@Test
	public void shouldAddAllApplications() {
		ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user,// 
				SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4))//
				.andReturn(Arrays.asList(applicationOne, applicationTwo));
		EasyMock.replay(applicationsServiceMock);
		controller = new ApplicationListController(applicationsServiceMock, userServiceMock) {

			@Override
			public RegisteredUser getUser() {
				return user;
			}

		};
		List<ApplicationForm> applications = controller.getApplications(SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4);
		
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals(2, applications.size());
		Assert.assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));
	}
	@Test
	public void shouldReturnFirstBlockOfApplications() {
		ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user,// 
				SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 1))//
				.andReturn(Arrays.asList(applicationOne, applicationTwo));
		EasyMock.replay(applicationsServiceMock);
		controller = new ApplicationListController(applicationsServiceMock, userServiceMock) {
			
			@Override
			public RegisteredUser getUser() {
				return user;
			}
			
		};
		List<ApplicationForm> applications = controller.getApplications(SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, null);
		
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals(2, applications.size());
		Assert.assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));
	}

	@Before
	public void setUp() {

		user = new RegisteredUserBuilder().id(1).toUser();
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ApplicationListController(applicationsServiceMock, userServiceMock);

	}

}
