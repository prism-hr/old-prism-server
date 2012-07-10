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
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
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

public class StateTransitionControllerTest {

	private StateTransitionController controller;
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

	@Test
	public void shouldRegisterValidator(){
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(stateChangeValidatorMock);
		binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerBinders(binderMock);
		EasyMock.verify(binderMock);
	}

	
	@Test
	public void shouldGetApplicationFromIdForAdminUser() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}
	
	@Test
	public void shouldGetApplicationFromIdForApproverUser() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
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
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminOrApproverInApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

		controller.getApplicationForm("5");
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
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicationNumber(applicationNumber).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock) {

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
		final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber(applicationNumber).id(5)
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock) {

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
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock) {

			@Override
			public ApplicationForm getApplicationForm(String application) {
				return applicationForm;
			}

		};
		assertArrayEquals(ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.VALIDATION), controller.getAvailableNextStati("5"));
	}

	@Before
	public void setUp() {
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
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
				stateTransitionViewResolverMock, encryptionHelperMock,documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
