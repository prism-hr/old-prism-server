package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.junit.Assert.assertEquals;

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
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class AssignInterviewerControllerTest {

	
	private AssignInterviewerController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	
	private NewUserByAdminValidator userValidatorMock;
	
	private InterviewService interviewServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;
	
	private InterviewValidator interviewValidator;
	private DatePropertyEditor datePropertyEditorMock;
	
	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	private RegisteredUser currentUserMock;
	private InterviewerPropertyEditor interviewerPropertyEditorMock;
	
	

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
		
		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);		
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		
		assertEquals(interview, controller.getInterview("5"));
	}

	@Test
	public void shouldSaveInterviewIfNoErrors() {		
		Interview interview = new InterviewBuilder().id(4).toInterview();
		interviewServiceMock.save(interview);
		EasyMock.replay(interviewServiceMock);
		String view = controller.assignInterviewers(interview, bindingResultMock);
		assertEquals("redirect:/applications", view);
		EasyMock.verify(interviewServiceMock);
		
	}
	
	@Test
	public void shouldReturnToViewAndNotSaveIfErros() {
		EasyMock.reset(bindingResultMock);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		Interview interview = new InterviewBuilder().id(4).toInterview();	
		EasyMock.replay(interviewServiceMock);
		String view = controller.assignInterviewers(interview, bindingResultMock);
		assertEquals(INTERVIEW_DETAILS_VIEW_NAME, view);
		EasyMock.verify(interviewServiceMock);
		
	}
	
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
	
		interviewServiceMock = EasyMock.createMock(InterviewService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		interviewValidator = EasyMock.createMock(InterviewValidator.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		interviewerPropertyEditorMock = EasyMock.createMock(InterviewerPropertyEditor.class); 
	

		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		
		controller = new AssignInterviewerController(applicationServiceMock, userServiceMock, userValidatorMock,  messageSourceMock, interviewServiceMock, interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock);
		
		
	}
			
}
