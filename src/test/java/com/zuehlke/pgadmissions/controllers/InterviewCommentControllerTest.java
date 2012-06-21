package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

public class InterviewCommentControllerTest {
	private ApplicationsService applicationsServiceMock;
	private UserService userServiceMock;
	private InterviewCommentController controller;
	private FeedbackCommentValidator reviewFeedbackValidatorMock;
	private CommentService commentServiceMock;
	private DocumentPropertyEditor documentPropertyEditorMock;

	
	@Test
	public void shouldGetApplicationFormFromId() {
		Program program = new ProgramBuilder().id(7).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInterviewerOfApplicationForm(applicationForm)).andReturn(true);;
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
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserNotInterviewerOfForm() {
		Program program = new ProgramBuilder().id(7).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInterviewerOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.replay(currentUser, userServiceMock);
		
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");

	}
	
	
	

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserCannotSeeApplication() {
		Program program = new ProgramBuilder().id(7).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInterviewerOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(currentUser, userServiceMock);
		
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");

	}

	@Test
	public void shouldReturnGenericCommentPage(){
		assertEquals("private/staff/interviewers/feedback/interview_feedback", controller.getInterviewFeedbackPage());
	}
	
	@Test
	public void shouldReturnCurrentUser(){
		RegisteredUser currentUser = new RegisteredUserBuilder().id(8).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		assertEquals(currentUser, controller.getUser());
	}
	
	@Test
	public void shouldCreateNewInterviewCommentForApplicationForm(){
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		final RegisteredUser currentUser =EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		Interviewer interviewer = new InterviewerBuilder().id(5).toInterviewer();
		EasyMock.expect(currentUser.getInterviewersForApplicationForm(applicationForm)).andReturn(Arrays.asList(interviewer));
		EasyMock.replay(userServiceMock, currentUser);
		controller = new  InterviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock, documentPropertyEditorMock){

			@Override
			public ApplicationForm getApplicationForm(String id) {
				return applicationForm;
			}
			
			@Override
			public RegisteredUser getUser() {
				return currentUser;
			}
			
		};
		InterviewComment comment = controller.getComment("5");
		
		assertNull(comment.getId());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals(currentUser, comment.getUser());
		assertEquals(CommentType.INTERVIEW, comment.getType());
		assertEquals(interviewer, comment.getInterviewer());
		
		
	}
	
	
	@Test
	public void shouldRegisterValidator(){
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(reviewFeedbackValidatorMock);
		binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerBinders(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldReturnToCommentsPageIfErrors(){
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		InterviewComment comment = new InterviewCommentBuilder().application(new ApplicationForm()).toInterviewComment();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock);
		assertEquals("private/staff/interviewers/feedback/interview_feedback", controller.addComment(comment,errorsMock));
	}
	
	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowResourceNotFoundIfApplicationAlreadyDecided(){
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		InterviewComment comment = new InterviewCommentBuilder().application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).toApplicationForm()).toInterviewComment();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock);
		controller.addComment(comment,errorsMock);
	}
	
	@Test
	public void shouldSaveCommentAndToApplicationListIfNoErrors(){
		InterviewComment comment = new InterviewCommentBuilder().id(1).application(new ApplicationFormBuilder().id(6).toApplicationForm()).toInterviewComment();		
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		commentServiceMock.save(comment);
		EasyMock.replay(errorsMock, commentServiceMock);
		assertEquals("redirect:/applications", controller.addComment(comment, errorsMock));
		EasyMock.verify(errorsMock, commentServiceMock);
		
		
	
	}
	
	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		reviewFeedbackValidatorMock = EasyMock.createMock(FeedbackCommentValidator.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
		controller = new InterviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock, documentPropertyEditorMock);

	}
}
