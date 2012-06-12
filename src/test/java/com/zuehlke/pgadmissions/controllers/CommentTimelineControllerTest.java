package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.EventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.TimelineObject;
import com.zuehlke.pgadmissions.dto.TimelinePhase;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.TimelineService;
import com.zuehlke.pgadmissions.services.UserService;

public class CommentTimelineControllerTest {

	private ApplicationsService applicationsServiceMock;
	private UserService userServiceMock;
	private CommentTimelineController controller;
	private TimelineService timelineServiceMock;

	@Test
	public void shouldGetApplicationFormFromId() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(currentUser, userServiceMock);

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
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserIsApplicant() {
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.replay(currentUser, userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentCannotSeeApplicationForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(currentUser, userServiceMock);

		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");

	}

	@Test
	public void shouldGetAllPhasesForApplication() throws ParseException {

		RegisteredUser currentUser = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);

		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		controller = new CommentTimelineController(applicationsServiceMock, userServiceMock, timelineServiceMock) {

			@Override
			public ApplicationForm getApplicationForm(String id) {
				if ("applicationNumber".equals(id)) {
					return applicationForm;
				}
				return null;
			}

		};
		TimelineObject timelineObjectOne = new TimelinePhase();
		TimelineObject timelineObjectTwo = new TimelinePhase();
		EasyMock.expect(timelineServiceMock.getPhases(applicationForm)).andReturn(
				Arrays.asList((TimelinePhase) timelineObjectOne, (TimelinePhase) timelineObjectTwo));
		EasyMock.replay(timelineServiceMock);

		List<TimelineObject> timelineObjects = controller.getTimelineObjects("applicationNumber");
		assertEquals(2, timelineObjects.size());
		assertEquals(timelineObjectOne, timelineObjects.get(0));
		assertEquals(timelineObjectTwo, timelineObjects.get(1));

	}
	@Test
	public void shouldReturnAllValidationQuestionOptions(){
		assertArrayEquals(ValidationQuestionOptions.values(), controller.getValidationQuestionOptions());
	}
	
	@Test
	public void shouldReturnHomeOrOverseasOptions(){
		assertArrayEquals(HomeOrOverseas.values(), controller.getHomeOrOverseasOptions());
	}
	@Test
	public void shouldReturnTimeLine() {
		assertEquals("private/staff/admin/comment/timeline", controller.getCommentsView());
	}

	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		timelineServiceMock = EasyMock.createMock(TimelineService.class);
		controller = new CommentTimelineController(applicationsServiceMock, userServiceMock, timelineServiceMock);

	}
}
