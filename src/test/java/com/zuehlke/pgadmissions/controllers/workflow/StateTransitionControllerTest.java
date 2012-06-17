package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.StateTransitionViewResolver;

public class StateTransitionControllerTest {

	private StateTransitionController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private CommentFactory commentFactoryMock;
	private CommentService commentServiceMock;
	private StateTransitionViewResolver stateTransitionViewResolverMock;

	@Test
	public void shouldGetApplicationFromId() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioNDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm("5");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminInApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

		controller.getApplicationForm("5");
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
	public void shouldReturnCurrentUser() {
		RegisteredUser currentUser = new RegisteredUserBuilder().id(4).toUser();

		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		assertSame(currentUser, controller.getUser());
	}

	@Test
	public void shouldReturnReviewersWillingToInterviewIfAppliationInReview() {
		final String applicationNumber = "5";
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicationNumber(applicationNumber).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,stateTransitionViewResolverMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				
				if (application == applicationNumber) {
					return applicationForm;
				}
				return null;
			}

		};
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).toUser();
		
		EasyMock.expect(userServiceMock.getReviewersWillingToInterview(applicationForm)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> willingToInterview = controller.getReviewersWillingToInterview(applicationNumber);
		assertEquals(2, willingToInterview.size());
		assertTrue(willingToInterview.containsAll(Arrays.asList(userOne, userTwo)));
	}
	
	@Test
	public void shouldReturnNullIfppliationNotInReview() {
		final String applicationNumber = "5";
		final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber(applicationNumber).id(5).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,stateTransitionViewResolverMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				
				if (application == applicationNumber) {
					return applicationForm;
				}
				return null;
			}

		};
		
		EasyMock.replay(userServiceMock);
		assertNull(controller.getReviewersWillingToInterview(applicationNumber));
		EasyMock.verify(userServiceMock);
	}
	@Test
	public void shouldReturnAvaialableNextStati() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, stateTransitionViewResolverMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}

		};
		assertArrayEquals(ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.VALIDATION), controller.getAvailableNextStati("5"));
	}

	@Test
	public void shouldResolveViewForApplicationForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(4).toApplicationForm();
		EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("view");
		EasyMock.replay(stateTransitionViewResolverMock);
		assertEquals("view", controller.getStateTransitionView(applicationForm));
	}


	@Test
	public void shouldCreateCommentSaveAndRedirectToResolvedView() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		String strComment = "comment";
		Comment comment = new CommentBuilder().id(6).toComment();
		CommentType type = CommentType.VALIDATION;
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, user, strComment, type, ApplicationFormStatus.INTERVIEW)).andReturn(comment);
		commentServiceMock.save(comment);
		EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("view");		
		EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);
		
		assertEquals("view", controller.addComment(applicationForm, user, type, strComment, ApplicationFormStatus.INTERVIEW,  null, null, null));
		
		EasyMock.verify(commentServiceMock);
	}

	@Test
	public void shouldNotCreateCommentORSaveIfCommentParameterIsBlank() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		String strComment = "";
		CommentType type = CommentType.VALIDATION;

		EasyMock.replay(commentFactoryMock, commentServiceMock);
		controller.addComment(applicationForm, user, type, strComment, null,  null, null, null);
		EasyMock.verify(commentServiceMock);
	}
	
	@Test
	public void shouldCreateValidationCommentWithQUestionalues() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		String strComment = "comment";
		ValidationComment comment = new ValidationCommentBuilder().id(6).toValidationComment();
		CommentType type = CommentType.VALIDATION;
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, user, strComment, type, ApplicationFormStatus.INTERVIEW)).andReturn(comment);
		commentServiceMock.save(comment);
		EasyMock.replay(commentFactoryMock, commentServiceMock);
		controller.addComment(applicationForm, user, type, strComment, ApplicationFormStatus.INTERVIEW, ValidationQuestionOptions.NO, ValidationQuestionOptions.UNSURE, HomeOrOverseas.OVERSEAS);
		EasyMock.verify(commentServiceMock);
		assertEquals(ValidationQuestionOptions.NO, comment.getQualifiedForPhd());
		assertEquals(ValidationQuestionOptions.UNSURE, comment.getEnglishCompentencyOk());
		assertEquals(HomeOrOverseas.OVERSEAS, comment.getHomeOrOverseas());
	}
	
	@Test
	public void shouldCreateReviewEvaluationCommentWithLatestReviewRound() {
		ReviewRound reviewRound = new ReviewRoundBuilder().id(5).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestReviewRound(reviewRound).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		String strComment = "comment";
		ReviewEvaluationComment comment = new ReviewEvaluationCommentBuilder().id(6).toReviewEvaluationComment();
		CommentType type = CommentType.REVIEW_EVALUATION;
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, user, strComment, type, ApplicationFormStatus.INTERVIEW)).andReturn(comment);
		commentServiceMock.save(comment);
		EasyMock.replay(commentFactoryMock, commentServiceMock);
		
		controller.addComment(applicationForm, user, type, strComment, ApplicationFormStatus.INTERVIEW, ValidationQuestionOptions.NO, ValidationQuestionOptions.UNSURE, HomeOrOverseas.OVERSEAS);
	
		EasyMock.verify(commentServiceMock);
		assertEquals(reviewRound, comment.getReviewRound());

	}
	@Test
	public void shouldCreateInterviewEvaluationCommentWithLatestInterview() {
		Interview interview = new InterviewBuilder().id(5).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestInterview(interview).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		String strComment = "comment";
		InterviewEvaluationComment comment = new InterviewEvaluationCommentBuilder().id(6).toInterviewEvaluationComment();
		CommentType type = CommentType.INTERVIEW_EVALUATION;
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, user, strComment, type, ApplicationFormStatus.INTERVIEW)).andReturn(comment);
		commentServiceMock.save(comment);
		EasyMock.replay(commentFactoryMock, commentServiceMock);
		
		controller.addComment(applicationForm, user, type, strComment, ApplicationFormStatus.INTERVIEW, ValidationQuestionOptions.NO, ValidationQuestionOptions.UNSURE, HomeOrOverseas.OVERSEAS);
	
		EasyMock.verify(commentServiceMock);
		assertEquals(interview, comment.getInterview());

	}
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		commentFactoryMock = EasyMock.createMock(CommentFactory.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		stateTransitionViewResolverMock = EasyMock.createMock(StateTransitionViewResolver.class);
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, stateTransitionViewResolverMock);
		
		
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
