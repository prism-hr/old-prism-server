package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.AssertTrue;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class MoveToInterviewControllerTest {

	private UsernamePasswordAuthenticationToken authenticationToken;
	private MoveToInterviewController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private StageDurationDAO stageDurationDAOMock;

	@Test
	public void shouldGetApplicationFromId() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.getId()).andReturn(8).anyTimes();
		EasyMock.expect(userServiceMock.getUser(8)).andReturn(currentUserMock);

		authenticationToken.setDetails(currentUserMock);

		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock, userServiceMock);

		ApplicationForm returnedForm = controller.getApplicationForm(5);
		assertEquals(applicationForm, returnedForm);
		EasyMock.verify(userServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioNDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm(5);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminInApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.getId()).andReturn(8).anyTimes();
		EasyMock.expect(userServiceMock.getUser(8)).andReturn(currentUserMock);
		authenticationToken.setDetails(currentUserMock);

		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock, userServiceMock);

		controller.getApplicationForm(5);
	}

	@Test
	public void shouldChangeStateToApprovalAndSave() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		List<Referee> referees = Arrays.asList(new RefereeBuilder().id(1).toReferee(), new RefereeBuilder().id(2).toReferee());
		applicationForm.setReferees(referees);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock);
		String view = controller.moveToInterview(applicationForm);
		EasyMock.verify(applicationServiceMock);
		assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
		assertEquals("redirect:/applications", view);
	}
	

	@Test
	public void shouldChangeStateToInterviewAndSave(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().toProgram()).toApplicationForm();
		List<Interviewer> interviewers = Arrays.asList(new InterviewerBuilder().id(1).toInterviewer(), new InterviewerBuilder().id(2).toInterviewer());
		applicationForm.setInterviewers(interviewers);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock,stageDurationDAOMock);
		
		String view = controller.moveToInterview(applicationForm);
		
		EasyMock.verify(applicationServiceMock);
		assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
		assertEquals("redirect:/applications", view);
	}
	
	@Test
	public void shouldReturnExistingInterviewersBelongingToApplication(){
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		
		Interviewer inter1 = new InterviewerBuilder().user(interUser1).id(4).toInterviewer();
		Interviewer inter2 = new InterviewerBuilder().user(interUser2).id(5).toInterviewer();
		applicationForm.setInterviewers(Arrays.asList(inter1, inter2));
		
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.getId()).andReturn(8).anyTimes();
		EasyMock.expect(userServiceMock.getUser(8)).andReturn(currentUserMock);
		authenticationToken.setDetails(currentUserMock);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock,userServiceMock, currentUserMock);
		
		List<RegisteredUser> interviewersUsers = controller.getApplicationInterviewersAsUsers(applicationForm);
		assertEquals(2, interviewersUsers.size());
	}

	
	@Test
	public void shouldGetProgrammeInterviewers(){
		RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		Program program = new ProgramBuilder().interviewers(interUser1, interUser2).id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.getId()).andReturn(8).anyTimes();
		EasyMock.expect(userServiceMock.getUser(8)).andReturn(currentUserMock);
		authenticationToken.setDetails(currentUserMock);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock,userServiceMock, currentUserMock);
		
		List<RegisteredUser> interviewersUsers = controller.getProgrammeInterviewers(program, applicationForm);
		assertEquals(2, interviewersUsers.size());
	}
	
	
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock);
		
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}
			
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
