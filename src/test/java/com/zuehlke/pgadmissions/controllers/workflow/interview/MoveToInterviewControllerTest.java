package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

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
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;

public class MoveToInterviewControllerTest {
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private RegisteredUser currentUserMock;
	private MoveToInterviewController controller;
	private InterviewValidator interviewValidatorMock;
	private InterviewerPropertyEditor interviewerPropertyEditorMock;
	private InterviewService interviewServiceMock;
	private BindingResult bindingResultMock;
	private DatePropertyEditor datePropertyEditorMock;

	@Test
	public void shouldGetInterviewPage() {
		Assert.assertEquals("/private/staff/interviewers/interview_details", controller.getInterviewDetailsPage());
	}
	
	@Test
	public void shouldGetInterviewersSection() {
		Assert.assertEquals("/private/staff/interviewers/interviewer_section", controller.getInterviewersSection());
	}
	
	@Test
	public void shouldGetProgrammeInterviewers() {
		final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();

		final Program program = new ProgramBuilder().interviewers(interUser1, interUser2).id(6).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, interviewServiceMock, interviewValidatorMock, interviewerPropertyEditorMock,datePropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("5")) {
					return applicationForm;
				}
				return null;
			}

		};

		List<RegisteredUser> interviewersUsers = controller.getProgrammeInterviewers("5");
		assertEquals(2, interviewersUsers.size());
		assertTrue(interviewersUsers.containsAll(Arrays.asList(interUser1, interUser2)));
	}
	
	
	@Test
	public void shouldGetListOfPreviousInterviewersAndAddReviewersWillingToInterviewWitDefaultInterviewersRemoved() {
		EasyMock.reset(userServiceMock);
		final RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(9).toUser();
		final RegisteredUser reviewerWillingToIntergviewOne = new RegisteredUserBuilder().id(8).toUser();
		final RegisteredUser reviewerWillingToIntergviewTwo = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser previousInterviewer = new RegisteredUserBuilder().id(6).toUser();
		ReviewComment reviewOne = new ReviewCommentBuilder().id(1).user(reviewerWillingToIntergviewOne).willingToInterview(true).toReviewComment();
		ReviewComment reviewTwo = new ReviewCommentBuilder().id(1).user(defaultInterviewer).willingToInterview(true).toReviewComment();
		ReviewComment reviewThree = new ReviewCommentBuilder().id(1).user(reviewerWillingToIntergviewTwo).willingToInterview(true).toReviewComment();
		
		

		final Program program = new ProgramBuilder().id(6).interviewers(defaultInterviewer).toProgram();

		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).comments(reviewOne, reviewTwo, reviewThree).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, interviewServiceMock, interviewValidatorMock, interviewerPropertyEditorMock,datePropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("5")) {
					return applicationForm;
				}
				return null;
			}

		};

		EasyMock.expect(userServiceMock.getAllPreviousInterviewersOfProgram(program)).andReturn(Arrays.asList(previousInterviewer, defaultInterviewer, reviewerWillingToIntergviewOne));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> interviewerUsers = controller.getPreviousInterviewersAndReviewersWillingToInterview("5");
		assertEquals(3, interviewerUsers.size());
		assertTrue(interviewerUsers.containsAll(Arrays.asList(previousInterviewer, reviewerWillingToIntergviewOne, reviewerWillingToIntergviewTwo)));
	}
	@Test
	public void shouldReturnNewInterviewWithExistingRoundsInterviewersIfAny() {
		Interviewer interviewerOne = new InterviewerBuilder().id(1).toInterviewer();
		Interviewer interviewerTwo = new InterviewerBuilder().id(2).toInterviewer();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").latestInterview(new InterviewBuilder().interviewers(interviewerOne, interviewerTwo).toInterview()).toApplicationForm();
		
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock,interviewServiceMock,  interviewValidatorMock, interviewerPropertyEditorMock,datePropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if(applicationId.equals("bob")){
					return application;
				}
				return null;
			}

		};	
		Interview returnedInterview = controller.getInterview("bob");
		assertNull(returnedInterview.getId());
		assertEquals(2, returnedInterview.getInterviewers().size());
		assertTrue(returnedInterview.getInterviewers().containsAll(Arrays.asList(interviewerOne, interviewerTwo)));
	}
	
	@Test
	public void shouldReturnInterviewWithWillingToInterviewWithInterviewersOfPreviousInterviewRemoved() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		ReviewComment reviewOne = new ReviewCommentBuilder().id(1).user(userOne).willingToInterview(true).toReviewComment();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		ReviewComment reviewTwo = new ReviewCommentBuilder().id(2).user(userTwo).willingToInterview(true).toReviewComment();
		RegisteredUser userThree = new RegisteredUserBuilder().id(3).toUser();
		ReviewComment reviewThree = new ReviewCommentBuilder().id(3).user(userThree).willingToInterview(true).toReviewComment();
		Interviewer interviewerOne = new InterviewerBuilder().id(1).user(userOne).toInterviewer();
		Interviewer interviewerTwo = new InterviewerBuilder().id(2).user(userTwo).toInterviewer();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").comments(reviewOne, reviewTwo, reviewThree).latestInterview(new InterviewBuilder().interviewers(interviewerOne, interviewerTwo).toInterview()).toApplicationForm();
		
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock,interviewServiceMock,  interviewValidatorMock, interviewerPropertyEditorMock,datePropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if(applicationId.equals("bob")){
					return application;
				}
				return null;
			}

		};	
		Interview returnedInterview = controller.getInterview("bob");
		assertNull(returnedInterview.getId());
		assertEquals(3, returnedInterview.getInterviewers().size());
		assertTrue(returnedInterview.getInterviewers().containsAll(Arrays.asList(interviewerOne, interviewerTwo)));
		assertNull(returnedInterview.getInterviewers().get(2).getId() );
		assertEquals(userThree, returnedInterview.getInterviewers().get(2).getUser());
	}
	@Test
	public void shouldReturnNewInterviewWithEmtpyInterviewersIfNoLatestInterview() {
	
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").toApplicationForm();
		
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, interviewServiceMock, interviewValidatorMock, interviewerPropertyEditorMock,datePropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if(applicationId.equals("bob")){
					return application;
				}
				return null;
			}

		};	
		Interview returnedInterview = controller.getInterview("bob");
		assertNull(returnedInterview.getId());
		assertTrue(returnedInterview.getInterviewers().isEmpty());

	}
	
	@Test
	public void shouldGetApplicationFromIdForAdmin() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test
	public void shouldGetApplicationFromIdForInterviewer() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm("5");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminOrInterviewerOfApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(false);

		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		controller.getApplicationForm("5");
	}
	
	@Test
	public void shouldGetCurrentUserAsUser(){
		assertEquals(currentUserMock, controller.getUser());
	}
	
	
	
	@Test
	public void shouldMoveApplicationToInterview() {
		Interview interview = new InterviewBuilder().id(4).toInterview();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").toApplicationForm();
		
		controller =new MoveToInterviewController(applicationServiceMock, userServiceMock, interviewServiceMock, interviewValidatorMock, interviewerPropertyEditorMock,datePropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return application;
			}

		};	
		
		interviewServiceMock.moveApplicationToInterview(interview, application);
		EasyMock.replay(interviewServiceMock);
		
		String view = controller.moveToInterview("abc", interview, bindingResultMock);
		assertEquals("/private/common/ajax_OK", view);
		EasyMock.verify(interviewServiceMock);
		
	}

	@Test
	public void shouldNotSaveInterviewAndReturnToInterviewPageIfHasErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller =new MoveToInterviewController(applicationServiceMock, userServiceMock, interviewServiceMock, interviewValidatorMock, interviewerPropertyEditorMock, datePropertyEditorMock){
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

		};
		Interview interview = new InterviewBuilder().application(applicationForm).toInterview();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock, applicationServiceMock);
		assertEquals("/private/staff/interviewers/interviewer_section", controller.moveToInterview("abc", interview, errorsMock));

	}
	
	
	@Test
	public void shouldAddInterviewValidatorAndInterviewerPropertyEditor() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(interviewValidatorMock);		
		binderMock.registerCustomEditor(Interviewer.class, interviewerPropertyEditorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerValidatorAndPropertyEditor(binderMock);
		EasyMock.verify(binderMock);
	}
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		interviewValidatorMock = EasyMock.createMock(InterviewValidator.class);
		interviewerPropertyEditorMock = EasyMock.createMock(InterviewerPropertyEditor.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		interviewServiceMock = EasyMock.createMock(InterviewService.class);
		
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);

		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock,interviewServiceMock,  interviewValidatorMock, interviewerPropertyEditorMock, datePropertyEditorMock);

	}
}
