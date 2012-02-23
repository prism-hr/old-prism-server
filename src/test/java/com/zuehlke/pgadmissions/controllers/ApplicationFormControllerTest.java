package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationFormModel;

public class ApplicationFormControllerTest {

	ProjectDAO projectDAOMock;
	ApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	ApplicationFormDAO applicationDAOMock;
	private RegisteredUser student;
	
	@Test
	public void shouldGetApplicationFormView() {
		
		ModelAndView modelAndView = applicationController.getNewApplicationForm(null);
		
		assertEquals("application/applicationForm", modelAndView.getViewName());
	}

	@Test
	public void shouldLoadProjectByIdANdSetOnApplicationForm() {
		
		Integer id = new Integer(12);
		Project project = new Project();
		project.setId(12);
		
		EasyMock.expect(projectDAOMock.getProjectById(id)).andReturn(project);
		EasyMock.replay(projectDAOMock);
		
		ModelAndView modelAndView = applicationController.getNewApplicationForm(12);
		ApplicationFormModel model = (ApplicationFormModel) modelAndView.getModel().get("model");
		
		assertEquals(SubmissionStatus.UNSUBMITTED, model.getApplicationForm().getSubmissionStatus());
		assertEquals(project, model.getApplicationForm().getProject());
	}
	
	@Test
	public void shouldGetUserFromSecurityContextAndSetOnApplicationForm() {

		ModelAndView modelAndView = applicationController.getNewApplicationForm(12);
		ApplicationFormModel model = (ApplicationFormModel) modelAndView.getModel().get("model");
		assertEquals(student, model.getApplicationForm().getUser());
	}

	@Test (expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionIfUserNotStudent(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).username("fred").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		authenticationToken.setDetails(reviewer);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		applicationController.getNewApplicationForm(null);
	}
	
	@Test
	public void shouldSaveApplicationForm(){
		applicationDAOMock.save(applicationForm);
		EasyMock.replay(applicationDAOMock);
		applicationController.getNewApplicationForm(null);
		EasyMock.verify(applicationDAOMock);
		
	}
	
	
	@Test
	public void shouldLoadApplicationFormByIdAndChangeSubmissionStatusToSubmitted(){
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationDAOMock.get(id)).andReturn(form);
		applicationDAOMock.save(form);
		EasyMock.replay(applicationDAOMock);
		assertEquals(SubmissionStatus.UNSUBMITTED, form.getSubmissionStatus());
		assertEquals("application/applicationFormSubmitted", applicationController.submitApplication(id));
		assertEquals(SubmissionStatus.SUBMITTED, form.getSubmissionStatus());
		EasyMock.verify(applicationDAOMock);
		
	}

	@Before
	public void setUp() {
		
		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		
		projectDAOMock = EasyMock.createMock(ProjectDAO.class);
		applicationDAOMock = EasyMock.createMock(ApplicationFormDAO.class);		
		
		applicationController = new ApplicationFormController(projectDAOMock, applicationDAOMock) {			
			ApplicationForm newApplicationForm() {
				return applicationForm;
			}

		};
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		student = new RegisteredUserBuilder().id(1).username("mark").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(student);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

	}
	
	@After
	public void tearDown(){
		SecurityContextHolder.clearContext();
	}
}
