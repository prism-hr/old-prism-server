package com.zuehlke.pgadmissions.dwr;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.HibernateFlusher;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApplicationManagementDWRServiceTest {

	private ApplicationsService applicationsServiceMock;
	private ApplicationManagementDWRService dwrService;
	private RegisteredUser currentUser;
	private Project project;
	private HibernateFlusher hibernateFlusherMock;

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdIsNull() {
		dwrService.acceptApplication(null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
		Integer id = 1;
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		dwrService.acceptApplication(id);

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfApplicationAlreadyAccepted() {
		Integer id = 1;
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(
				new ApplicationFormBuilder().id(1).project(project).approvedSatus(ApprovalStatus.APPROVED).toApplicationForm());
		EasyMock.replay(applicationsServiceMock);
		dwrService.acceptApplication(id);

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfApplicationAlreadyRejected() {
		Integer id = 1;
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(
				new ApplicationFormBuilder().id(1).project(project).approvedSatus(ApprovalStatus.REJECTED).toApplicationForm());
		EasyMock.replay(applicationsServiceMock);
		dwrService.acceptApplication(id);

	}

	@Test(expected = AccessDeniedException.class)
	public void shouldThrowAccessDeniedExceptionIfCurrentUserNotInRoleApprover() {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		dwrService.acceptApplication(1);

	}

	@Test(expected = AccessDeniedException.class)
	public void shouldThrowAccessDeniedExceptionIfCurrentUserNotApproverOfProgramToWhichProjectOfApplicationFormBelongs() {
		Integer id = 1;
		Project projectBelongingToProgramForWhichUserIsNotApprover = new ProjectBuilder().id(1).program(new ProgramBuilder().id(1).toProgram()).toProject();
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(
				new ApplicationFormBuilder().id(1).project(projectBelongingToProgramForWhichUserIsNotApprover).toApplicationForm());
		EasyMock.replay(applicationsServiceMock);
		dwrService.acceptApplication(id);
	}

	@Test
	public void shouldSetApproverAndApprovedStatusOnFormAndPersist() {
		Integer id = 1;
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).project(project).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(applicationForm);
		hibernateFlusherMock.flush();
		EasyMock.replay(applicationsServiceMock, hibernateFlusherMock);
		dwrService.acceptApplication(id);
		EasyMock.verify(hibernateFlusherMock);
		assertEquals(currentUser, applicationForm.getApprover());
		assertEquals(ApprovalStatus.APPROVED, applicationForm.getApprovalStatus());

	}
	
	@Before
	public void setup() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		hibernateFlusherMock = EasyMock.createMock(HibernateFlusher.class);
		dwrService = new ApplicationManagementDWRService(applicationsServiceMock, hibernateFlusherMock);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		project = new ProjectBuilder().id(1).program(new ProgramBuilder().id(1).approver(currentUser).toProgram()).toProject();

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}

