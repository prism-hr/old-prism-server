package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
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
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
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
		Program program = new ProgramBuilder().id(7).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5)
				.program(program).build();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);

		EasyMock.expect(userServiceMock.getCurrentUser())
				.andReturn(currentUser);
		EasyMock.expect(
				currentUser.isInterviewerOfApplicationForm(applicationForm))
				.andReturn(true);
		EasyMock.expect(
				currentUser
						.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(applicationForm))
				.andReturn(false);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(
				applicationsServiceMock.getApplicationByApplicationNumber("5"))
				.andReturn(applicationForm);

		EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
		ApplicationForm returnedApplication = controller
				.getApplicationForm("5");
		EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);

		assertEquals(returnedApplication, applicationForm);
	}

	@Test(expected = MissingApplicationFormException.class)
	public void shouldThrowExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(
				applicationsServiceMock.getApplicationByApplicationNumber("5"))
				.andReturn(null);

		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("5");
		EasyMock.verify(applicationsServiceMock);
	}

	@Test(expected = InsufficientApplicationFormPrivilegesException.class)
	public void shouldThrowExceptionIfCurrentUserNotInterviewerOfForm() {
		Program program = new ProgramBuilder().id(7).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5)
				.program(program).build();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);

		EasyMock.expect(userServiceMock.getCurrentUser())
				.andReturn(currentUser);
		EasyMock.expect(
				currentUser.isInterviewerOfApplicationForm(applicationForm))
				.andReturn(false);
		EasyMock.expect(
				applicationsServiceMock.getApplicationByApplicationNumber("5"))
				.andReturn(applicationForm);

		EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
		controller.getApplicationForm("5");
		EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);
	}

	@Test(expected = InsufficientApplicationFormPrivilegesException.class)
	public void shouldThrowExceptionIfCurrentUserCannotSeeApplication() {
		Program program = new ProgramBuilder().id(7).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5)
				.program(program).build();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);

		EasyMock.expect(userServiceMock.getCurrentUser())
				.andReturn(currentUser);
		EasyMock.expect(
				currentUser.isInterviewerOfApplicationForm(applicationForm))
				.andReturn(true);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);

		EasyMock.expect(
				applicationsServiceMock.getApplicationByApplicationNumber("5"))
				.andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
		controller.getApplicationForm("5");
	}

	@Test(expected = ActionNoLongerRequiredException.class)
	public void shouldThrowExceptionIfInterviewerAlreadyResponded() {
		Program program = new ProgramBuilder().id(7).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5)
				.program(program).build();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);

		EasyMock.expect(userServiceMock.getCurrentUser())
				.andReturn(currentUser);
		EasyMock.expect(
				currentUser.isInterviewerOfApplicationForm(applicationForm))
				.andReturn(true);
		EasyMock.expect(
				currentUser
						.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(applicationForm))
				.andReturn(true);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(
				applicationsServiceMock.getApplicationByApplicationNumber("5"))
				.andReturn(applicationForm);

		EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
		controller.getApplicationForm("5");
	}

	@Test(expected = ActionNoLongerRequiredException.class)
	public void shouldThrowExceptionIfApplicationFormAlreadyDecided() {
		Program program = new ProgramBuilder().id(7).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5)
				.program(program).status(ApplicationFormStatus.APPROVED)
				.build();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);

		EasyMock.expect(userServiceMock.getCurrentUser())
				.andReturn(currentUser);
		EasyMock.expect(
				currentUser.isInterviewerOfApplicationForm(applicationForm))
				.andReturn(true);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(
				applicationsServiceMock.getApplicationByApplicationNumber("5"))
				.andReturn(applicationForm);

		EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
		controller.getApplicationForm("5");
	}

	@Test
	public void shouldReturnGenericCommentPage() {
		assertEquals("private/staff/interviewers/feedback/interview_feedback",
				controller.getInterviewFeedbackPage());
	}

	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser currentUser = new RegisteredUserBuilder().id(8).build();
		EasyMock.expect(userServiceMock.getCurrentUser())
				.andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		assertEquals(currentUser, controller.getUser());
	}

	@Test
	public void shouldCreateNewInterviewCommentForApplicationForm() throws ScoringDefinitionParseException {
		final ApplicationForm applicationForm = new ApplicationFormBuilder()
				.id(5).build();
		final RegisteredUser currentUser = EasyMock
				.createMock(RegisteredUser.class);
		Interviewer interviewer = new InterviewerBuilder().id(5).build();

		EasyMock.expect(userServiceMock.getCurrentUser())
				.andReturn(currentUser);
		EasyMock.expect(
				currentUser.getInterviewersForApplicationForm(applicationForm))
				.andReturn(Arrays.asList(interviewer));
		EasyMock.replay(userServiceMock, currentUser);
		controller = new InterviewCommentController(applicationsServiceMock,
				userServiceMock, commentServiceMock,
				reviewFeedbackValidatorMock, documentPropertyEditorMock, null,
				null, null) {

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
	public void shouldRegisterValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(reviewFeedbackValidatorMock);
		binderMock.registerCustomEditor(Document.class,
				documentPropertyEditorMock);

		EasyMock.replay(binderMock);
		controller.registerBinders(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnToCommentsPageIfErrors() throws ScoringDefinitionParseException {
		InterviewComment comment = new InterviewCommentBuilder().application(
				new ApplicationForm()).build();
		BindingResult errorsMock = new BeanPropertyBindingResult(comment,
				"comment");
		errorsMock.reject("error");

		assertEquals("private/staff/interviewers/feedback/interview_feedback",
				controller.addComment(comment, errorsMock));
	}

	@Test
	public void shouldSaveCommentAndResetAppAdminAndToApplicationListIfNoErrors() throws ScoringDefinitionParseException {
		InterviewComment comment = new InterviewCommentBuilder()
				.id(1)
				.application(
						new ApplicationFormBuilder().id(6)
								.applicationNumber("abc")
								.applicationAdministrator(new RegisteredUser())
								.build()).build();
		BindingResult errorsMock = new BeanPropertyBindingResult(comment,
				"comment");
		ApplicationForm applicationForm = comment.getApplication();

		applicationsServiceMock.save(applicationForm);
		commentServiceMock.save(comment);

		EasyMock.replay(commentServiceMock);
		EasyMock.replay(applicationsServiceMock);
		assertEquals(
				"redirect:/applications?messageCode=interview.feedback&application=abc",
				controller.addComment(comment, errorsMock));
		EasyMock.verify(commentServiceMock);
		EasyMock.verify(applicationsServiceMock);

		assertEquals(null, applicationForm.getApplicationAdministrator());
	}

	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock
				.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		reviewFeedbackValidatorMock = EasyMock
				.createMock(FeedbackCommentValidator.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		documentPropertyEditorMock = EasyMock
				.createMock(DocumentPropertyEditor.class);
		controller = new InterviewCommentController(applicationsServiceMock,
				userServiceMock, commentServiceMock,
				reviewFeedbackValidatorMock, documentPropertyEditorMock, null,
				null, null);

	}
}
