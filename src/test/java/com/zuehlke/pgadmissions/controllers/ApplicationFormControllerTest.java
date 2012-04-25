package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApplicationFormControllerTest {

	private ProgramDAO programDAOMock;
	private ApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;

	@Test
	public void shouldCreateNewApplicationFormWithProgramAndUserFromSecurityContext() {

		Program program = new ProgramBuilder().id(12).toProgram();
		EasyMock.expect(programDAOMock.getProgramById(12)).andReturn(program);		
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(student, program)).andReturn(applicationForm);
		EasyMock.replay(programDAOMock, applicationsServiceMock);
		
		applicationController.createNewApplicationForm(12);
		EasyMock.verify(applicationsServiceMock);
		
	}

	@Test
	public void shouldRedirectToApplicationFormView() {
		Program program = new ProgramBuilder().id(12).toProgram();
		EasyMock.expect(programDAOMock.getProgramById(12)).andReturn(program);		
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(student, program)).andReturn(applicationForm);
		EasyMock.replay(programDAOMock, applicationsServiceMock);
		
		ModelAndView modelAndView = applicationController.createNewApplicationForm(12);
		assertEquals(applicationForm.getId(), modelAndView.getModel().get("applicationId"));
		assertEquals("redirect:/application", modelAndView.getViewName());

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

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		programDAOMock = EasyMock.createMock(ProgramDAO.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);

		applicationController = new ApplicationFormController(programDAOMock, applicationsServiceMock, userPropertyEditorMock) {
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

