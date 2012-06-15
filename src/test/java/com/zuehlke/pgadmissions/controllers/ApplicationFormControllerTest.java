package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.propertyeditors.PlainTextUserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApplicationFormControllerTest {

	private ProgramDAO programDAOMock;
	private ApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private PlainTextUserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;
	private ProgramInstanceDAO programInstanceDAOMock;

	@Test
	public void shouldCreateNewApplicationFormWithProgramAndUserFromSecurityContext() throws ParseException {

		Program program = new ProgramBuilder().id(12).title("Program 1").toProgram();
		ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption(StudyOption.FULL_TIME).applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).toProgramInstance();
		program.setInstances(Arrays.asList(programInstance));
		
		EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);		
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(student, program, null)).andReturn(applicationForm);
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();
		
		EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
		
		applicationController.createNewApplicationForm("ABC", null);
		EasyMock.verify(applicationsServiceMock);
		
	}

	@Test
	public void shouldRedirectToApplicationFormView() throws ParseException {
		Program program = new ProgramBuilder().id(12).title("Program 1").toProgram();
		ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption(StudyOption.FULL_TIME).applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).toProgramInstance();
		program.setInstances(Arrays.asList(programInstance));
		
		EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);		
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(student, program, null)).andReturn(applicationForm);
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();
		EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
		
		ModelAndView modelAndView = applicationController.createNewApplicationForm("ABC", null);
		assertEquals(applicationForm.getApplicationNumber(), modelAndView.getModel().get("applicationId"));
		assertEquals("redirect:/application", modelAndView.getViewName());

	}
	
	
	@Test
	public void shouldRedirectToApplicationFormViewAndSaveBatchDedline() throws ParseException {
		Program program = new ProgramBuilder().id(12).title("Program 1").toProgram();
		ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption(StudyOption.FULL_TIME).applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).toProgramInstance();
		program.setInstances(Arrays.asList(programInstance));
		EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);		
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(student, program, "2012/12/12")).andReturn(applicationForm);
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();
		EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
		
		ModelAndView modelAndView = applicationController.createNewApplicationForm("ABC", "2012/12/12");
		assertEquals(applicationForm.getApplicationNumber(), modelAndView.getModel().get("applicationId"));
		assertEquals("redirect:/application", modelAndView.getViewName());
		EasyMock.verify(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
	}
	
	@Test
	public void shouldReturnProgramDoesNotExistPageIfProgramDoesNotExists() throws ParseException{
		Program program1 = new ProgramBuilder().toProgram();
		EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(null);		
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program1)).andReturn(null);
		EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
		
		ModelAndView modelAndView = applicationController.createNewApplicationForm("ABC", null);
		assertEquals("private/pgStudents/programs/program_does_not_exist", modelAndView.getViewName());
	}
	
	
	@Test
	public void shouldReturnProgramDoesNotExistPageIfProgramExistsButDoesntHaveAnyActiveInstances() throws ParseException{
		Program program = new ProgramBuilder().id(12).toProgram();
		EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(null);		
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(student, program, null)).andReturn(applicationForm);
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(null);
		EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
		
		ModelAndView modelAndView = applicationController.createNewApplicationForm("ABC", null);
		assertEquals("private/pgStudents/programs/program_does_not_exist", modelAndView.getViewName());
	}
	
	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMock);
		EasyMock.replay(binderMock);
		applicationController.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}


	@Before
	public void setUp() {

		applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC").toApplicationForm();

		programDAOMock = EasyMock.createMock(ProgramDAO.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMock = EasyMock.createMock(PlainTextUserPropertyEditor.class);
		programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
		
		
		applicationController = new ApplicationFormController(programDAOMock, applicationsServiceMock, userPropertyEditorMock, programInstanceDAOMock) {
			ApplicationForm newApplicationForm() {
				return applicationForm;
			}
		};

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(student);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}

