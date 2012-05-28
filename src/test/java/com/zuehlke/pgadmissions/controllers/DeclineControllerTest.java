package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

public class DeclineControllerTest {
	
	private DeclineController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private CommentService commentServiceMock;
	private RefereeService refereeServiceMock;
	private static final String DECLINE_REVIEW_SUCCESS_VIEW_NAME = "/private/reviewers/decline_success_confirmation";

	@Test
	public void shouldGetReviewerFromId() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(reviewer);
		EasyMock.replay(userServiceMock);
		RegisteredUser returnedReviewer = controller.getReviewer(5);
		assertEquals(reviewer, returnedReviewer);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUsernotFound() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(null);
		EasyMock.replay(userServiceMock);
		RegisteredUser returnedReviewer = controller.getReviewer(5);
		assertEquals(reviewer, returnedReviewer);
	}


	@Test
	public void shouldGetApplicationFromId() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock);
		ApplicationForm returnedForm = controller.getApplicationForm(5);
		assertEquals(applicationForm, returnedForm);

	}


	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationNotExists() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationServiceMock);
		ApplicationForm returnedForm = controller.getApplicationForm(5);
		assertEquals(applicationForm, returnedForm);
	}
	
	
	@Test
	public void shouldGetRefereeFromId() {
		Referee referee = new RefereeBuilder().id(5).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeById(5)).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		Referee returnedReferee = controller.getReferee(5);
		assertEquals(referee, returnedReferee);
	}
	
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfRefereeNotExists() {
		Referee referee = new RefereeBuilder().id(5).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeById(5)).andReturn(null);
		EasyMock.replay(refereeServiceMock);
		Referee returnedReferee = controller.getReferee(5);
		assertEquals(referee, returnedReferee);
	}
	
	@Test
	public void shouldDeclineReviewAndReturnMessageView() {
		final RegisteredUser reviewer = new RegisteredUserBuilder().id(5).toUser();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(new RegisteredUserBuilder().firstName("").lastName("").toUser()).id(5).toApplicationForm();
		controller = new DeclineController(userServiceMock, commentServiceMock, applicationServiceMock, refereeServiceMock){
			@Override
			public RegisteredUser getReviewer(Integer userId){
				return reviewer;
			}
			@Override
			public ApplicationForm getApplicationForm(Integer applicationId){
				return applicationForm;
			}
		}; 
		commentServiceMock.declineReview(reviewer, applicationForm);
		EasyMock.replay(commentServiceMock);
		String view = controller.declineReview(reviewer.getId(), applicationForm.getId(), new ModelMap());
		EasyMock.verify(commentServiceMock);
		assertEquals(DECLINE_REVIEW_SUCCESS_VIEW_NAME, view);
	}

	
	@Test
	public void shouldDeclineReferenceAndReturnMessageView() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(new RegisteredUserBuilder().firstName("").lastName("").toUser()).id(5).toApplicationForm();
		final Referee referee = new RefereeBuilder().application(applicationForm).id(5).toReferee();
		controller = new DeclineController(userServiceMock, commentServiceMock, applicationServiceMock, refereeServiceMock){
			@Override
			public Referee getReferee(Integer refereeId){
				return referee;
			}
		}; 
		refereeServiceMock.saveReferenceAndSendDeclineNotifications(referee);
		EasyMock.replay(refereeServiceMock);
		String view = controller.declineReference(referee.getId(), new ModelMap());
		EasyMock.verify(refereeServiceMock);
		assertEquals(DECLINE_REVIEW_SUCCESS_VIEW_NAME, view);
	}
	
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		
		
		controller = new DeclineController(userServiceMock, commentServiceMock, applicationServiceMock, refereeServiceMock); 

	}
}
