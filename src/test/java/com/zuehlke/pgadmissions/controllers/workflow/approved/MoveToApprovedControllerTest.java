package com.zuehlke.pgadmissions.controllers.workflow.approved;

import static org.junit.Assert.assertEquals;

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
import com.zuehlke.pgadmissions.services.UserService;

public class MoveToApprovedControllerTest {

	private MoveToApprovedController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;


	private static final String APPROVED_DETAILS_VIEW_NAME = "/private/staff/approver/approve_page";
	private RegisteredUser currentUserMock;

	@Test
	public void shouldGetApprovedPage() {
		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).id(5).toApplicationForm();
		controller = new MoveToApprovedController(applicationServiceMock, userServiceMock){
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
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program)
				.status(ApplicationFormStatus.APPROVAL).id(5).toApplicationForm();

		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())).andReturn(true);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test
	public void shouldMoveApplicationToApproved() {
		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm application = new ApplicationFormBuilder().program(program).id(2).toApplicationForm();
		controller = new MoveToApprovedController(applicationServiceMock, userServiceMock){
			@Override
				public ApplicationForm getApplicationForm(String applicationId) {
				return application;
			}

		};	
		applicationServiceMock.save(application);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, application.getProgram())).andReturn(true);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		
		String view = controller.moveToApproved(application.getApplicationNumber());
		assertEquals("redirect:/applications", view);
		EasyMock.verify(applicationServiceMock);
		assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());
		
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldNotMoveApplicationToApprovedIfUserNotApproverInProgram() {
		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		controller = new MoveToApprovedController(applicationServiceMock, userServiceMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

		};
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(currentUserMock, applicationServiceMock);
		 controller.moveToApproved(applicationForm.getApplicationNumber());
	}


	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);

		controller = new MoveToApprovedController(applicationServiceMock, userServiceMock);
	}

}
