package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.BadgeService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.StateTransitionViewResolver;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class ValidationTransitionControllerTest {

	private ValidationTransitionController controller;
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
	private BadgeService badgeServiceMock;
	
	@Test
	public void shouldReturnAllValidationQuestionOptions() {
		assertArrayEquals(ValidationQuestionOptions.values(), controller.getValidationQuestionOptions());
	}

	@Test
	public void shouldReturnHomeOrOverseasOptions() {
		assertArrayEquals(HomeOrOverseas.values(), controller.getHomeOrOverseasOptions());
	}
	
	@Test
	public void shouldResolveViewForApplicationForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(4).toApplicationForm();
		EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("view");
		EasyMock.replay(stateTransitionViewResolverMock);
		assertEquals("view", controller.getStateTransitionView(applicationForm));
	}
	
	@Test
	public void shouldCreateValidationCommentWithQUestionaluesIfNoValidationErrors() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).toApplicationForm();
		ValidationComment comment = new ValidationCommentBuilder().qualifiedForPhd(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.UNSURE).nextStatus(ApplicationFormStatus.APPROVAL).comment("comment").type(CommentType.VALIDATION).id(6).toValidationComment();
		commentServiceMock.save(comment);
		controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock,documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, badgeServiceMock){
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
				
		};

		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);
		controller.addComment(applicationForm.getApplicationNumber(), "13 Aug 2013", "projectTitle", comment, bindingResultMock, new ModelMap());
		EasyMock.verify(commentServiceMock);
	}
	
	@Test
	public void shouldCreateCommentWithDocumentsAndSaveAndRedirectToResolvedView() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("abc")).andReturn(1);
		EasyMock.expect(encryptionHelperMock.decryptToInteger("def")).andReturn(2);
		Document documentOne = new DocumentBuilder().id(1).toDocument();
		Document documentTwo = new DocumentBuilder().id(2).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(documentOne);
		EasyMock.expect(documentServiceMock.getDocumentById(2)).andReturn(documentTwo);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock,documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, badgeServiceMock){
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
				
		};
		ValidationComment comment = new ValidationCommentBuilder().comment("comment").type(CommentType.VALIDATION).documents(documentOne, documentTwo).id(6).toValidationComment();
		commentServiceMock.save(comment);
		EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("view");
		EasyMock.replay(commentServiceMock, stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock);

		assertEquals("view", controller.addComment(applicationForm.getApplicationNumber(), "13 Aug 2013", "projectTitle", comment, bindingResultMock, new ModelMap()));

		EasyMock.verify(commentServiceMock);
		assertEquals(2, comment.getDocuments().size());
		assertTrue(comment.getDocuments().containsAll(Arrays.asList(documentOne, documentTwo)));
	}
	
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
		badgeServiceMock = EasyMock.createMock(BadgeService.class);
		controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock,documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, badgeServiceMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
