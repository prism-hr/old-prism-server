package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class MoveToApprovalControllerTest {

	private MoveToApprovalController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private NewUserByAdminValidator userValidatorMock;

	private ApprovalService approvalServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;

	private ApprovalRoundValidator approvalroundValidator;

	private static final String APROVAL_DETAILS_VIEW_NAME = "/private/staff/supervisors/approval_details";
	private RegisteredUser currentUserMock;

	private SupervisorPropertyEditor supervisorProertyEditorMock;

	@Test
	public void shouldGetApprovalRoundPageWithOnlyAssignFalseNewSupervisorsFunctionality() {
		ModelMap modelMap = new ModelMap();
		String approvalRoundDetailsPage = controller.getApprovalRoundDetailsPage(modelMap);
		Assert.assertEquals(APROVAL_DETAILS_VIEW_NAME, approvalRoundDetailsPage);
		Assert.assertFalse((Boolean) modelMap.get("assignOnly"));

	}

	@Test
	public void shouldGetApplicationFromId() {
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test
	public void shouldReturnNewApprovalRound() {

		ApprovalRound returnedApprovalRound = controller.getApprovalRound(null);
		assertNull(returnedApprovalRound.getId());
	}

	@Test
	public void shouldMoveApplicationToReview() {
		ApprovalRound approvalround = new ApprovalRoundBuilder().id(4).toApprovalRound();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).toApplicationForm();
		
		controller = new MoveToApprovalController(applicationServiceMock, userServiceMock, userValidatorMock,null, approvalServiceMock, messageSourceMock, supervisorProertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return application;
			}

		};	
		
		approvalServiceMock.moveApplicationToApproval(application, approvalround);
		EasyMock.replay(approvalServiceMock);
		
		String view = controller.moveToApproval(application.getApplicationNumber(), approvalround, bindingResultMock);
		assertEquals("redirect:/applications", view);
		EasyMock.verify(approvalServiceMock);
		
	}

	@Test
	public void shouldNotSaveReviewRoundAndReturnToReviewRoundPageIfHasErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new MoveToApprovalController(applicationServiceMock, userServiceMock, userValidatorMock,null, approvalServiceMock, messageSourceMock, supervisorProertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

		};
		ApprovalRound approvalround = new ApprovalRoundBuilder().application(applicationForm).toApprovalRound();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock, applicationServiceMock);
		assertEquals(APROVAL_DETAILS_VIEW_NAME, controller.moveToApproval("1", approvalround, errorsMock));

	}


	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);

		approvalServiceMock = EasyMock.createMock(ApprovalService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		approvalroundValidator = EasyMock.createMock(ApprovalRoundValidator.class);
		supervisorProertyEditorMock = EasyMock.createMock(SupervisorPropertyEditor.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);

		controller = new MoveToApprovalController(applicationServiceMock, userServiceMock, userValidatorMock,null, approvalServiceMock, messageSourceMock, supervisorProertyEditorMock);
	}

}
