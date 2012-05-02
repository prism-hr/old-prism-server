package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;
import com.zuehlke.pgadmissions.validators.ReviewFeedbackValidator;

public class ReviewCommentControllerTest {
	private ApplicationsService applicationsServiceMock;
	private UserService userServiceMock;
	private ReviewCommentController controller;
	private ReviewFeedbackValidator reviewFeedbackValidatorMock;
	private CommentService commentServiceMock;
	private CommentFactory commentFactoryMock;

	
	@Test
	public void shouldGetApplicationFormFromId() {
		Program program = new ProgramBuilder().id(7).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.REVIEWER)).andReturn(true);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(currentUser, userServiceMock);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		ApplicationForm returnedApplication = controller.getApplicationForm(5);
		assertEquals(returnedApplication, applicationForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);
	}

	@Ignore
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserNotReviewer() {
		Program program = new ProgramBuilder().id(7).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.replay(currentUser, userServiceMock);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);

	}
	
	

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserCannotSeeApplication() {
		Program program = new ProgramBuilder().id(7).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.REVIEWER)).andReturn(true);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(currentUser, userServiceMock);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);

	}

	@Test
	public void shouldReturnGenericCommentPage(){
		assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.getReviewFeedbackPage());
	}
	
	@Test
	public void shouldReturnCurrentUser(){
		RegisteredUser currentUser = new RegisteredUserBuilder().id(8).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		assertEquals(currentUser, controller.getUser());
	}
	
	@Test
	public void shouldCreateNewReviewCommentForApplicationForm(){
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		final RegisteredUser currentUser = new RegisteredUserBuilder().id(8).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		commentFactoryMock = EasyMock.createMock(CommentFactory.class);
		controller = new  ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock, commentFactoryMock){

			@Override
			public ApplicationForm getApplicationForm(Integer id) {
				return applicationForm;
			}
			
			@Override
			public RegisteredUser getUser() {
				return currentUser;
			}
			
		};
		ReviewComment comment = controller.getComment(5);
		System.out.println(comment);
		assertNull(comment.getId());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals(currentUser, comment.getUser());
		
	}
	
	
	@Test
	public void shouldRegisterValidator(){
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(reviewFeedbackValidatorMock);
		EasyMock.replay(binderMock);
		controller.registerBinders(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldReturnToCommentsPageIfErrors(){
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		ReviewComment comment = new ReviewCommentBuilder().application(new ApplicationForm()).toReviewComment();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock);
		assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.addComment(comment,errorsMock));
	}
	
	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowResourceNotFoundIfApplicationAlreadyDecided(){
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		ReviewComment comment = new ReviewCommentBuilder().application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).toApplicationForm()).toReviewComment();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock);
		controller.addComment(comment,errorsMock);
	}
	
	@Test
	public void shouldSaveCommentAndRedirectBackToPageIfNoErrors(){
		ReviewComment comment = new ReviewCommentBuilder().id(1).application(new ApplicationFormBuilder().id(6).toApplicationForm()).toReviewComment();		
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		commentServiceMock.save(comment);
		EasyMock.replay(errorsMock, commentServiceMock);
		assertEquals("redirect:/reviewFeedback?applicationId=6", controller.addComment(comment, errorsMock));
	}
	
	@Before
	public void setUp() {
		commentFactoryMock = EasyMock.createMock(CommentFactory.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		reviewFeedbackValidatorMock = EasyMock.createMock(ReviewFeedbackValidator.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock, commentFactoryMock);

	}
}
