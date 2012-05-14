package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;


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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
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
	private InterviewValidator interviewValidator;
	private DatePropertyEditor datePropertyEditorMock;
	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	
	
	@Test
	public void shouldGetInterviewPage() {
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, controller.getInterviewDetailsPage(false, new ModelMap()));
	}
	
	@Test
	public void shouldGetInterviewPageWithOnlyAssignNewInterviewersFunctionality() {
		ModelMap modelMap = new ModelMap();
		String interviewDetailsPage = controller.getInterviewDetailsPage(true, modelMap);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, interviewDetailsPage);
		Assert.assertTrue((Boolean) modelMap.get("assignOnly"));
		
	}
	
	@Test
	public void shouldAddRegisteredUserValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(userValidatorMock);
		EasyMock.replay(binderMock);
		controller.registerInterviewerValidators(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldGetApplicationFromId() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.getId()).andReturn(8).anyTimes();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);

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
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock, userServiceMock);

		controller.getApplicationForm(5);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCannotSeeApplication() {
		
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock, userServiceMock);
		
		controller.getApplicationForm(5);
	}

	
	@Test
	public void shouldReturnExistingInterviewersBelongingToApplication(){
		Program program = new ProgramBuilder().id(6).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}
		};
		RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		
		Interviewer inter1 = new InterviewerBuilder().user(interUser1).id(4).toInterviewer();
		Interviewer inter2 = new InterviewerBuilder().user(interUser2).id(5).toInterviewer();
		applicationForm.setInterviewers(Arrays.asList(inter1, inter2));
		
		RegisteredUser currentUserMock = expectCurrentUser(applicationForm);
		
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock,userServiceMock, currentUserMock);
		
		Set<RegisteredUser> interviewersUsers = controller.getApplicationInterviewersAsUsers(applicationForm.getId());
		assertEquals(2, interviewersUsers.size());
	}

	
	@Test
	public void shouldGetProgrammeInterviewers(){
		final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();
		final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		final Program program = new ProgramBuilder().interviewers(interUser1, interUser2).id(6).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().acceptedTerms(CheckedStatus.NO).id(5).program(program).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
					applicationForm.setProgram(program);
				return applicationForm;
			}
		};
		
		RegisteredUser currentUserMock = expectCurrentUser(applicationForm);
		
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock,userServiceMock, currentUserMock);
		
		List<RegisteredUser> interviewersUsers = controller.getProgrammeInterviewers(5, null);
		assertEquals(2, interviewersUsers.size());
	}
	
	@Test
	public void shouldSaveInterview(){
		final ApplicationForm application = new ApplicationFormBuilder().id(2).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
				return application;
			}
		};
		ModelMap mmap = new ModelMap();
		Interview interview = new InterviewBuilder().furtherDetails("9 pm").locationURL("pgadmissions.com").dueDate(new Date()).toInterview();
		interviewServiceMock.save(interview);
		applicationServiceMock.save(application);
		EasyMock.replay(interviewerServiceMock, applicationServiceMock);
		controller.moveToInterview(application.getId(), interview, bindingResultMock, mmap, new ArrayList<RegisteredUser>());
		EasyMock.verify(interviewerServiceMock, applicationServiceMock);
	}

	private RegisteredUser expectCurrentUser(ApplicationForm applicationForm) {
		Program program = new ProgramBuilder().id(1).toProgram();
		applicationForm.setProgram(program);
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.getId()).andReturn(8).anyTimes();
		EasyMock.expect(userServiceMock.getUser(8)).andReturn(currentUserMock);
		authenticationToken.setDetails(currentUserMock);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		return currentUserMock;
	}
	
	private void prepareMessageSourceMock(String code, Object[] objects, String returnString) {
		EasyMock.expect(messageSourceMock.getMessage(EasyMock.eq(code), EasyMock.aryEq(objects), EasyMock.isNull(Locale.class))).andReturn(returnString);
	}
	
	@Test
	public void shouldNotAddOrCreateExistingInterviewerInProgramme() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		Interviewer interviewer = new InterviewerBuilder().id(1).user(userMock).toInterviewer();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().interviewers(interviewer).id(1).program(program).toApplicationForm();
		ModelMap mmap = new ModelMap();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}
		};
		EasyMock.expect(userMock.getEmail()).andReturn("test@test.com").anyTimes();
		EasyMock.expect(userMock.getUsername()).andReturn("test");
		prepareMessageSourceMock("assignInterviewer.user.alreadyExistsInTheApplication", new Object[] { "null", "null" }, "message");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("test@test.com")).andReturn(userMock);
		EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(userMock.isInterviewerOfProgram(program)).andReturn(true);
		EasyMock.replay(userServiceMock, interviewerServiceMock, userMock);
		String view = controller.createInterviewer(1, userMock, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, view);
		EasyMock.verify(interviewerServiceMock, userServiceMock, userMock);
		Assert.assertNull(mmap.get("interviewer"));
	}
	
	@Test
	public void shouldCreateNewInterviewUserIfUserDoesNotExists(){
		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).toApplicationForm();
		ModelMap mmap = new ModelMap();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}
		};
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(interviewerServiceMock.createNewUserWithInterviewerRoleInProgram(user, program)).andReturn(user);
		
		EasyMock.replay(userServiceMock, interviewerServiceMock);
		controller.createInterviewer(1, user, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
		EasyMock.verify(userServiceMock, interviewerServiceMock);
	}
	
	@Test
	public void shouldNotCreateNewInterviewUserIfUserAlreadyInterviewerInApplication(){
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		Interviewer interviewer = new InterviewerBuilder().id(1).user(userMock).toInterviewer();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().interviewers(interviewer).id(1).program(program).toApplicationForm();
		ModelMap mmap = new ModelMap();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}
		};
		EasyMock.expect(userMock.getEmail()).andReturn(null).anyTimes();
		EasyMock.expect(userMock.getUsername()).andReturn(null);
		prepareMessageSourceMock("assignInterviewer.user.alreadyExistsInTheApplication", new Object[] { "null", "null" }, "message");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(null)).andReturn(userMock);
		EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.replay(userServiceMock, interviewerServiceMock, userMock);
		String view = controller.createInterviewer(1, userMock, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, view);
		EasyMock.verify(interviewerServiceMock, userServiceMock, userMock);
		Assert.assertNull(mmap.get("interviewer"));
	}
	
	@Test
	public void shouldCreateInterviewerIfNotInProgramme() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		Interviewer interviewer = new InterviewerBuilder().id(1).user(userMock).toInterviewer();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().interviewers(interviewer).id(1).program(program).toApplicationForm();
		ModelMap mmap = new ModelMap();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}
		};
		EasyMock.expect(userMock.getEmail()).andReturn("test@test.com").anyTimes();
		EasyMock.expect(userMock.getUsername()).andReturn("test");
		prepareMessageSourceMock("assignInterviewer.user.alreadyExistsInTheApplication", new Object[] { "null", "null" }, "message");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("test@test.com")).andReturn(userMock);
		EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(userMock.isInterviewerOfProgram(program)).andReturn(false);
		interviewerServiceMock.addInterviewerToProgram(userMock, program);
		EasyMock.replay(userServiceMock, interviewerServiceMock, userMock);
		String view = controller.createInterviewer(1, userMock, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, view);
		EasyMock.verify(interviewerServiceMock, userServiceMock, userMock);
		Assert.assertNull(mmap.get("interviewer"));
	}
	
	
	@Test
	public void shouldRegisterInterviewValidatorAndPropertyEditor(){
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(interviewValidator);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerInterviewValidatorsAndPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}
	
	
	@Test
	public void shouldNotSaveInterviewAndReturnToInterviewPageIfHasErrors(){
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}
		};
		Interview interview = new InterviewBuilder().application(applicationForm).toInterview();
		EasyMock.expect(applicationServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock, applicationServiceMock);
		assertEquals(INTERVIEW_DETAILS_VIEW_NAME, controller.moveToInterview(1, interview, errorsMock, new ModelMap(), new ArrayList<RegisteredUser>()));

	}
	
	
	@Test
	public void shouldSaveInterviewIfNoErrorsAndMoveApplicationToInterviewStage(){
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = calendar.getTime();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock){
			@Override
			public
			ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}
		};
		Interview interview = new InterviewBuilder().furtherDetails("further").dueDate(new Date()).id(1).application(applicationForm).toInterview();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		interviewServiceMock.save(interview);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(errorsMock, interviewServiceMock, applicationServiceMock);
		assertEquals("redirect:/applications", controller.moveToInterview(1, interview, errorsMock, new ModelMap(), new ArrayList<RegisteredUser>()));
		EasyMock.verify(errorsMock, interviewServiceMock, applicationServiceMock);
		Assert.assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
		assertEquals(DateUtils.truncate(tomorrow, Calendar.DATE), DateUtils.truncate(interview.getInterviewDueDate(), Calendar.DATE));
		
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
		interviewValidator = EasyMock.createMock(InterviewValidator.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		interviewerUser1 = new RegisteredUserBuilder().id(7).username("rev 1").role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		interviewerUser2 = new RegisteredUserBuilder().id(8).username("rev 2").role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		
		otherReviewerUser = new RegisteredUserBuilder().id(4).username("inet")//
				.role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
	
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock);
		
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
