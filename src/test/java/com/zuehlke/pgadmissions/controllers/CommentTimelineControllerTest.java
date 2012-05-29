package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.TimelineEntity;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.EventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class CommentTimelineControllerTest {

	private ApplicationsService applicationsServiceMock;
	private UserService userServiceMock;
	private CommentTimelineController controller;

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
	public void shouldGetAllVisibleCommentsAndEventsForApplication() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		RegisteredUser currentUser = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
				
		final ApplicationForm applicationForm = EasyMock.createMock(ApplicationForm.class);
		Comment commentOne = new CommentBuilder().id(1).date(format.parse("01 01 2011")).toComment();
		Comment commentTwo = new CommentBuilder().date(format.parse("01 10 2011")).id(2).toComment();
		List<Comment> commentsList = Arrays.asList(commentOne, commentTwo);
		Event eventOne = new EventBuilder().date(format.parse("01 05 2011")).id(1).toEvent();
		Event eventTwo = new EventBuilder().id(1).toEvent();
		List<Event> eventsList = Arrays.asList(eventOne, eventTwo);
		
		
		EasyMock.expect(applicationForm.getVisibleComments(currentUser)).andReturn(commentsList);
		EasyMock.expect(applicationForm.getEvents()).andReturn(eventsList);
		EasyMock.replay(userServiceMock, applicationForm);
		
		controller = new CommentTimelineController( applicationsServiceMock, userServiceMock){

			@Override
			public ApplicationForm getApplicationForm(String id) {			
				return applicationForm;
			}
			
		};
		List<TimelineEntity> sortedTimelineList = controller.getSortedTimelineList("5");
		assertEquals(4, sortedTimelineList.size());
		assertEquals(commentTwo, sortedTimelineList.get(0));
		assertEquals(eventOne, sortedTimelineList.get(1));
		assertEquals(commentOne, sortedTimelineList.get(2));
		assertEquals(eventTwo, sortedTimelineList.get(3));
	}
	
	@Test
	public void shouldReturnTimeLine(){
		assertEquals("private/staff/admin/comment/timeline", controller.getCommentsView());
	}
	
	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new CommentTimelineController(applicationsServiceMock, userServiceMock);

	}
}
