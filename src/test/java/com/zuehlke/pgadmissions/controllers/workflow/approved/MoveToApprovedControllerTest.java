package com.zuehlke.pgadmissions.controllers.workflow.approved;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;

public class MoveToApprovedControllerTest {

	private MoveToApprovedController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private static final String APPROVED_DETAILS_VIEW_NAME = "/private/staff/approver/approve_page";
	private RegisteredUser currentUserMock;
	private ApprovalService approvalServiceMock;

	@Test
	public void shouldGetApprovedPage() {
		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).id(5).toApplicationForm();
		controller = new MoveToApprovedController(applicationServiceMock, userServiceMock, approvalServiceMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

		};
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
		EasyMock.replay(currentUserMock);
		Assert.assertEquals(APPROVED_DETAILS_VIEW_NAME, controller.getApprovedDetailsPage(applicationForm.getApplicationNumber()));
	}

	@Test
	public void shouldGetApplicationFromId() {
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.APPROVAL).id(5).toApplicationForm();

		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())).andReturn(true);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test
	public void shouldMoveApplicationToApprovedWithComment() {
		String strComment = "bob";
		List<String> documentIds = Arrays.asList("abc", "def");

		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm application = new ApplicationFormBuilder().program(program).id(2).toApplicationForm();
		controller = new MoveToApprovedController(applicationServiceMock, userServiceMock, approvalServiceMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return application;
			}

		};

		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, application.getProgram())).andReturn(true);
		approvalServiceMock.moveToApproved(application, strComment, documentIds);

		EasyMock.replay(currentUserMock, approvalServiceMock);

		String view = controller.moveToApproved(application.getApplicationNumber(), strComment, documentIds);

		assertEquals("redirect:/applications", view);
		EasyMock.verify(currentUserMock, approvalServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldNotMoveApplicationToApprovedIfUserNotApproverInProgram() {
		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		controller = new MoveToApprovedController(applicationServiceMock, userServiceMock, approvalServiceMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

		};
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
		EasyMock.replay(currentUserMock, approvalServiceMock);

		controller.moveToApproved(applicationForm.getApplicationNumber(), null, null);

		EasyMock.verify(currentUserMock, approvalServiceMock);
	}

	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);

		approvalServiceMock = EasyMock.createMock(ApprovalService.class);
		controller = new MoveToApprovedController(applicationServiceMock, userServiceMock, approvalServiceMock);
	}

}
