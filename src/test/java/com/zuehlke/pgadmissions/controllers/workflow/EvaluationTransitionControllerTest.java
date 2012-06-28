package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.StateTransitionViewResolver;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class EvaluationTransitionControllerTest {

	private EvaluationTransitionController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private CommentFactory commentFactoryMock;
	private CommentService commentServiceMock;
	private StateTransitionViewResolver stateTransitionViewResolverMock;
	private EncryptionHelper encryptionHelperMock;
	private DocumentService documentServiceMock;
	private ApprovalService approvalServiceMock;
	private StateChangeValidator stateChangeValidatorMock;
	private DocumentPropertyEditor documentPropertyEditorMock;
	private BindingResult bindingResultMock;
	private RegisteredUser user;
	
	@Test
	public void shouldCreateApprovalEvaluationCommentWithLatestReviewRound() {
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).toApprovalRound();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).toApplicationForm();
		StateChangeComment stateComment = new StateChangeComment();
		stateComment.setComment("comment");
		stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
		stateComment.setType(CommentType.APPROVAL_EVALUATION);
		ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().id(6).toApprovalEvaluationComment();
		controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock,documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
				
		};
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), stateComment.getNextStatus())).andReturn(comment);
		commentServiceMock.save(comment);
		EasyMock.replay(commentFactoryMock, commentServiceMock);
		
		controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, new ModelMap());
		
		EasyMock.verify(commentServiceMock);
		assertEquals(approvalRound, comment.getApprovalRound());
	}
	
	@Test
	public void shouldCreateApprovalEvaluationCommentWithLatestReviewRoundAndMoveToApprovedIdNextStageIsApproved() {
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).toApprovalRound();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).applicationNumber("abc").toApplicationForm();
		StateChangeComment stateComment = new StateChangeComment();
		stateComment.setComment("comment");
		stateComment.setNextStatus(ApplicationFormStatus.APPROVED);
		stateComment.setType(CommentType.APPROVAL_EVALUATION);
		ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.APPROVED).id(6).toApprovalEvaluationComment();
		controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock,documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
				
		};
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), stateComment.getNextStatus())).andReturn(comment);
		commentServiceMock.save(comment);
		approvalServiceMock.moveToApproved(applicationForm);
		EasyMock.replay(commentFactoryMock, commentServiceMock, approvalServiceMock);
		
		ModelMap modelMap = new ModelMap();
		controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, modelMap);
		
		EasyMock.verify(commentServiceMock, approvalServiceMock);
		assertEquals(approvalRound, comment.getApprovalRound());
		assertEquals("move.approved", modelMap.get("messageCode"));
		assertEquals("abc", modelMap.get("application"));
	}

	@Test
	public void shouldCreateApprovalEvaluationCommentWithLatestReviewRoundAndNotMoveToApprovedIdNextStageIsRejected() {
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).toApprovalRound();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).toApplicationForm();
		StateChangeComment stateComment = new StateChangeComment();
		stateComment.setComment("comment");
		stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
		stateComment.setType(CommentType.APPROVAL_EVALUATION);
		ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.REJECTED).id(6).toApprovalEvaluationComment();
		controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock,documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
				
		};
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), ApplicationFormStatus.REJECTED)).andReturn(comment);
		commentServiceMock.save(comment);
		EasyMock.replay(commentFactoryMock, commentServiceMock, approvalServiceMock);
		
		controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, new ModelMap());
		
		EasyMock.verify(commentServiceMock, approvalServiceMock);
		assertEquals(approvalRound, comment.getApprovalRound());
	}
	
//	@Test
//	public void shouldCreateInterviewEvaluationCommentWithLatestInterview() {
//		Interview interview = new InterviewBuilder().id(5).toInterview();
//		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestInterview(interview).toApplicationForm();
//		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
//		String strComment = "comment";
//		InterviewEvaluationComment comment = new InterviewEvaluationCommentBuilder().id(6).toInterviewEvaluationComment();
//		CommentType type = CommentType.INTERVIEW_EVALUATION;
//		EasyMock.expect(commentFactoryMock.createComment(applicationForm, user, strComment, type, ApplicationFormStatus.INTERVIEW)).andReturn(comment);
//		commentServiceMock.save(comment);
//		EasyMock.replay(commentFactoryMock, commentServiceMock);
//
//		controller.addComment(applicationForm, user, type, strComment, ApplicationFormStatus.INTERVIEW, null, ValidationQuestionOptions.NO,
//				ValidationQuestionOptions.UNSURE, HomeOrOverseas.OVERSEAS, new ModelMap());
//
//		EasyMock.verify(commentServiceMock);
//		assertEquals(interview, comment.getInterview());
//
//	}
//	
//	@Test
//	public void shouldCreateCommentWithDocumentsAndSaveAndRedirectToResolvedView() {
//		List<String> documentIds = Arrays.asList("abc", "def");
//		EasyMock.expect(encryptionHelperMock.decryptToInteger("abc")).andReturn(1);
//		EasyMock.expect(encryptionHelperMock.decryptToInteger("def")).andReturn(2);
//		Document documentOne = new DocumentBuilder().id(1).toDocument();
//		Document documentTwo = new DocumentBuilder().id(2).toDocument();
//		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(documentOne);
//		EasyMock.expect(documentServiceMock.getDocumentById(2)).andReturn(documentTwo);
//		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
//		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
//		String strComment = "comment";
//		Comment comment = new CommentBuilder().id(6).toComment();
//		CommentType type = CommentType.VALIDATION;
//		EasyMock.expect(commentFactoryMock.createComment(applicationForm, user, strComment, type, ApplicationFormStatus.INTERVIEW)).andReturn(comment);
//		commentServiceMock.save(comment);
//		EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("view");
//		EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock);
//
//		assertEquals("view", controller.addComment(applicationForm, user, type, strComment, ApplicationFormStatus.INTERVIEW, documentIds, null, null, null, new ModelMap()));
//
//		EasyMock.verify(commentServiceMock);
//		assertEquals(2, comment.getDocuments().size());
//		assertTrue(comment.getDocuments().containsAll(Arrays.asList(documentOne, documentTwo)));
//	}
//	
//	@Test
//	public void shouldCreateReviewEvaluationCommentWithLatestReviewRound() {
//		ReviewRound reviewRound = new ReviewRoundBuilder().id(5).toReviewRound();
//		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestReviewRound(reviewRound).toApplicationForm();
//		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
//		String strComment = "comment";
//		ReviewEvaluationComment comment = new ReviewEvaluationCommentBuilder().id(6).toReviewEvaluationComment();
//		CommentType type = CommentType.REVIEW_EVALUATION;
//		EasyMock.expect(commentFactoryMock.createComment(applicationForm, user, strComment, type, ApplicationFormStatus.INTERVIEW)).andReturn(comment);
//		commentServiceMock.save(comment);
//		EasyMock.replay(commentFactoryMock, commentServiceMock);
//
//		controller.addComment(applicationForm, user, type, strComment, ApplicationFormStatus.INTERVIEW, null, ValidationQuestionOptions.NO,
//				ValidationQuestionOptions.UNSURE, HomeOrOverseas.OVERSEAS, new ModelMap());
//
//		EasyMock.verify(commentServiceMock);
//		assertEquals(reviewRound, comment.getReviewRound());
//
//	}
	
	@Before
	public void setUp() {
		user = new RegisteredUserBuilder().id(1).toUser();
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
		stateChangeValidatorMock = EasyMock.createMock(StateChangeValidator.class);
		approvalServiceMock = EasyMock.createMock(ApprovalService.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		commentFactoryMock = EasyMock.createMock(CommentFactory.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		stateTransitionViewResolverMock = EasyMock.createMock(StateTransitionViewResolver.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		documentServiceMock = EasyMock.createMock(DocumentService.class);
		controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock,documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
