package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ApplicationFormControllerTest {

	ProjectDAO projectDAOMock;
	ApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	ApplicationFormDAO applicationDAOMock;
	private RegisteredUser user;
	
	@Test
	public void shouldGetApplicationFormView() {
		assertEquals("applicationForm", applicationController.getNewApplicationForm(null, new ModelMap()));
	}

	@Test
	public void shouldLoadProjectByIdANdSetOnApplicationForm() {
		Integer id = new Integer(12);
		Project project = new Project();
		project.setId(12);
		EasyMock.expect(projectDAOMock.getProjectById(id)).andReturn(project);
		EasyMock.replay(projectDAOMock);
		ModelMap modelMap = new ModelMap();
		applicationController.getNewApplicationForm(12, modelMap);
		ApplicationForm application = (ApplicationForm) modelMap.get("application");
		assertEquals(project, application.getProject());
	}
	
	@Test
	public void shouldGetUserFromSecurityContextAndSetOnApplicationForm() {

		ModelMap modelMap = new ModelMap();
		applicationController.getNewApplicationForm(null, modelMap);
		ApplicationForm application = (ApplicationForm) modelMap.get("application");
		assertEquals(user, application.getUser());
	}

	@Test
	public void shouldSaveApplicationForm(){
		applicationDAOMock.save(applicationForm);
		EasyMock.replay(applicationDAOMock);
		applicationController.getNewApplicationForm(null, new ModelMap());
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
		user = new RegisteredUserBuilder().id(1).username("mark").toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

	}
}
