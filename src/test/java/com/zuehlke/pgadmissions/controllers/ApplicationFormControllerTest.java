package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
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
	private UserDAO userDAOMock;

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationDAOMock.get(1)).andReturn(null);
		EasyMock.replay(applicationDAOMock);
		applicationController.getNewApplicationFormPage(1);

	}
	
	@Test
	public void shouldGetApplicationFormView() {
		applicationForm.setApplicant(student);
		EasyMock.expect(applicationDAOMock.get(1)).andReturn(applicationForm);
		EasyMock.replay(applicationDAOMock);
		ModelAndView modelAndView = applicationController.getNewApplicationFormPage(1);
		assertEquals("application/applicationForm", modelAndView.getViewName());
	}

	@Test
	public void shouldGetApplicationFormFromIdAndSetOnModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationForm.setApplicant(student);
		EasyMock.expect(applicationDAOMock.get(1)).andReturn(applicationForm);
		EasyMock.replay(applicationDAOMock);
		ModelAndView modelAndView = applicationController.getNewApplicationFormPage(1);
		ApplicationFormModel model = (ApplicationFormModel) modelAndView.getModel().get("model");
		assertEquals(applicationForm, model.getApplicationForm());
	}

	@Test
	public void shouldGetCurrentUserFromSecutrityContextAndSetOnEditModel() {
		applicationForm.setApplicant(student);
		EasyMock.expect(applicationDAOMock.get(1)).andReturn(applicationForm);
		EasyMock.replay(applicationDAOMock);
		ModelAndView modelAndView = applicationController.getNewApplicationFormPage(1);
		ApplicationFormModel model = (ApplicationFormModel) modelAndView.getModel().get("model");
		assertEquals(student, model.getUser());
	}

	@Test
	public void shouldLoadProjectByIdAndSetOnApplicationForm() {

		Project project = new ProjectBuilder().id(12).toProject();
		EasyMock.expect(projectDAOMock.getProjectById(12)).andReturn(project);
		EasyMock.replay(projectDAOMock);
		applicationController.createNewApplicationForm(12);
		assertEquals(project, applicationForm.getProject());

	}

	@Test
	public void shouldGetUserFromSecurityContextAndSetOnApplicationForm() {

		applicationController.createNewApplicationForm(12);
		assertEquals(student, applicationForm.getApplicant());
	}

	@Test
	public void shouldRedirectToEditView() {

		ModelAndView modelAndView = applicationController.createNewApplicationForm(12);
		assertEquals(applicationForm.getId(), modelAndView.getModel().get("id"));
		assertEquals("redirect:edit", modelAndView.getViewName());

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionIfCurrecntUserNotTheApplicantStudent() {
		applicationForm.setApplicant(student);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser otherApplicant = new RegisteredUserBuilder().id(6).username("fred").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole())
				.toUser();
		authenticationToken.setDetails(otherApplicant);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		EasyMock.expect(applicationDAOMock.get(1)).andReturn(applicationForm);
		applicationController.getNewApplicationFormPage(1);
	}

	@Test
	public void shouldSaveApplicationForm() {
		applicationDAOMock.save(applicationForm);
		EasyMock.replay(applicationDAOMock);
		applicationController.createNewApplicationForm(null);
		EasyMock.verify(applicationDAOMock);

	}

	@Test
	public void shouldLoadApplicationFormByIdAndChangeSubmissionStatusToSubmitted() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		form.setApplicant(student);
		EasyMock.expect(applicationDAOMock.get(id)).andReturn(form);
		applicationDAOMock.save(form);
		EasyMock.replay(applicationDAOMock);
		assertEquals(SubmissionStatus.UNSUBMITTED, form.getSubmissionStatus());
		assertEquals("redirect:/pgadmissions/applications", applicationController.submitApplication(id).getViewName());
		assertEquals(SubmissionStatus.SUBMITTED, form.getSubmissionStatus());
		EasyMock.verify(applicationDAOMock);

	}

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmitterNotFormAcpplicant() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationDAOMock.get(id)).andReturn(form);		
		applicationDAOMock.save(form);
		EasyMock.replay(applicationDAOMock);
		
		form.setApplicant(student);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser otherApplicant = new RegisteredUserBuilder().id(6).username("fred").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole())
				.toUser();
		authenticationToken.setDetails(otherApplicant);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);		
		
		applicationController.submitApplication(id);
	}

	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationFormDoesNotExist() {
		Integer id = 2;		
		EasyMock.expect(applicationDAOMock.get(id)).andReturn(null);	
		EasyMock.replay(applicationDAOMock);		
		applicationController.submitApplication(id);
	}

	
	@Test
	@Ignore
	public void shouldDoStuffIfSaveFails() {
		//
		fail("not implemented");
	

	}
	
	
	@Test
	public void shouldSaveNewPersonalDetails() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationDAOMock.get(2)).andReturn(form);
		EasyMock.replay(applicationDAOMock);

		ModelAndView modelAndView = applicationController.editApplicationForm(2, "Jack", "Johnson");
		ApplicationFormModel model = (ApplicationFormModel) modelAndView.getModel().get("model");
		Assert.assertEquals("Jack", model.getUser().getFirstName());
		Assert.assertEquals("Johnson", model.getUser().getLastName());
	}

	@Before
	public void setUp() {

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		projectDAOMock = EasyMock.createMock(ProjectDAO.class);
		applicationDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		userDAOMock = EasyMock.createMock(UserDAO.class);

		applicationController = new ApplicationFormController(projectDAOMock, applicationDAOMock, userDAOMock) {
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
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
