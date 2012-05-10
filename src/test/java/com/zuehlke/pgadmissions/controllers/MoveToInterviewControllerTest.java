package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.validation.constraints.AssertTrue;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class MoveToInterviewControllerTest {

	private UsernamePasswordAuthenticationToken authenticationToken;
	private MoveToInterviewController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private StageDurationDAO stageDurationDAOMock;
	private NewUserByAdminValidator userValidatorMock;
	private InterviewerService interviewerServiceMock;
	private InterviewService interviewServiceMock;
	private MessageSource messageSourceMock;
	private RegisteredUser interviewerUser1;
	private RegisteredUser interviewerUser2;
	private RegisteredUser otherInterviewerUser;
	private BindingResult bindingResultMock;
	private RegisteredUser otherReviewerUser;
	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	
	@Test
	public void shouldAddRegisteredUserValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(userValidatorMock);
		EasyMock.replay(binderMock);
		controller.registerValidators(binderMock);
		EasyMock.verify(binderMock);
	}
	
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
	public void shouldChangeStateToInterviewAndSave(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(new ProgramBuilder().toProgram()).toApplicationForm();
		List<Interviewer> interviewers = Arrays.asList(new InterviewerBuilder().id(1).toInterviewer(), new InterviewerBuilder().id(2).toInterviewer());
		applicationForm.setInterviewers(interviewers);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock,stageDurationDAOMock);
		
		String view = controller.moveToInterview(applicationForm, new Interview());
		
		EasyMock.verify(applicationServiceMock);
		assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
//		assertEquals("redirect:/applications", view);
		assertEquals(INTERVIEW_DETAILS_VIEW_NAME, view);
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
		
		RegisteredUser currentUserMock = expectCurrentUser(applicationForm);
		
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
		
		RegisteredUser currentUserMock = expectCurrentUser(applicationForm);
		
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock,userServiceMock, currentUserMock);
		
		List<RegisteredUser> interviewersUsers = controller.getProgrammeInterviewers(program, applicationForm);
		assertEquals(2, interviewersUsers.size());
	}
	
	@Test
	public void shouldSaveInterview(){
		ApplicationForm application = new ApplicationFormBuilder().id(2).toApplicationForm();
		Interview interview = new InterviewBuilder().furtherDetails("9 pm").locationURL("pgadmissions.com").dueDate(new Date()).toInterview();
		interviewServiceMock.save(interview);
		applicationServiceMock.save(application);
		EasyMock.replay(interviewerServiceMock, applicationServiceMock);
		controller.moveToInterview(application, interview);
		EasyMock.verify(interviewerServiceMock, applicationServiceMock);
//		Assert.assertNotNull(application.getInterview());
//		Assert.assertEquals("9 pm", application.getInterview().getFurtherDetails());
	}

	private RegisteredUser expectCurrentUser(ApplicationForm applicationForm) {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.getId()).andReturn(8).anyTimes();
		EasyMock.expect(userServiceMock.getUser(8)).andReturn(currentUserMock);
		authenticationToken.setDetails(currentUserMock);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		return currentUserMock;
	}
	
	@Test
	public void shouldCreateNewInterviewUserIfUserDoesNotExists(){
		
	}
	
	@Test
	public void shouldAddNewInterviewerRoleToUserIfExists() {
		
	}
	
	private void prepareMessageSourceMock(String code, Object[] objects, String returnString) {
		EasyMock.expect(messageSourceMock.getMessage(EasyMock.eq(code), EasyMock.aryEq(objects), EasyMock.isNull(Locale.class))).andReturn(returnString);
	}
	
	@Test
	public void shouldNotAddOrCreateExistingInterviewerInProgramme() {
		interviewerUser1.setEmail("rev1@bla.com");
		ApplicationForm applicationForm = new ApplicationForm();
		RegisteredUser currentUserMock = expectCurrentUser(applicationForm);
		Program program = new ProgramBuilder().administrators(currentUserMock).toProgram();
		applicationForm.setProgram(program);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("hui@blu.com")).andReturn(interviewerUser1);
		prepareMessageSourceMock("assignInterviewer.user.alreadyInProgramme", new Object[] { "rev 1", "rev1@bla.com" }, "SDFSDFSDFSDF");
		EasyMock.replay(interviewerServiceMock, userServiceMock, messageSourceMock, currentUserMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().email("hui@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controller.createInterviewer(applicationForm, inputUser, bindingResultMock, mmap);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, view);
		EasyMock.verify(interviewerServiceMock, userServiceMock, messageSourceMock);
		Assert.assertEquals("SDFSDFSDFSDF", mmap.get("message"));
		Assert.assertNull(mmap.get("interviewer"));
	}

	
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		interviewerServiceMock = EasyMock.createMock(InterviewerService.class);
		interviewServiceMock = EasyMock.createMock(InterviewService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		interviewerUser1 = new RegisteredUserBuilder().id(7).username("rev 1").role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		interviewerUser2 = new RegisteredUserBuilder().id(8).username("rev 2").role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		
		otherReviewerUser = new RegisteredUserBuilder().id(4).username("inet")//
				.role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
	
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock);
		
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
