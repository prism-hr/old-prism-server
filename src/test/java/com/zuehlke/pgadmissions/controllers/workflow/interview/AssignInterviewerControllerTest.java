package com.zuehlke.pgadmissions.controllers.workflow.interview;

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
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class AssignInterviewerControllerTest {

	
	private AssignInterviewerController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	
	private NewUserByAdminValidator userValidatorMock;
	private InterviewerService interviewerServiceMock;
	private InterviewService interviewServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;
	
	private InterviewValidator interviewValidator;
	private DatePropertyEditor datePropertyEditorMock;
	
	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	private RegisteredUser currentUserMock;
	

	@Test
	public void shouldGetInterviewPageWithOnlyAssignTrueAssignInterviewersFunctionality() {
		ModelMap modelMap = new ModelMap();
		String interviewDetailsPage = controller.getAssignInterviewersPage(modelMap);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, interviewDetailsPage);
		Assert.assertTrue((Boolean) modelMap.get("assignOnly"));
		
	}
	
	
	@Test
	public void shouldReturnLatestInterviewIfApplicationForm(){
		Program program = new ProgramBuilder().id(6).toProgram();
		Interview interview = new InterviewBuilder().id(1).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).latestInterview(interview).toApplicationForm();
		
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);		
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		
		assertEquals(interview, controller.getInterview(5));
	}

	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		interviewerServiceMock = EasyMock.createMock(InterviewerService.class);
		interviewServiceMock = EasyMock.createMock(InterviewService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		interviewValidator = EasyMock.createMock(InterviewValidator.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
	
		
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		
		controller = new AssignInterviewerController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock);
		
		
	}
			
}
