package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationFormControllerTest {

	private ProjectDAO projectDAOMock;
	private ApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserService userServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;


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
	public void shouldRedirectToApplicationFormView() {

		ModelAndView modelAndView = applicationController.createNewApplicationForm(12);
		assertEquals(applicationForm.getId(), modelAndView.getModel().get("id"));
		assertEquals("redirect:/application", modelAndView.getViewName());

	}

	@Test
	public void shouldSaveApplicationForm() {
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		applicationController.createNewApplicationForm(null);
		EasyMock.verify(applicationsServiceMock);

	}

	@Test
	public void shouldLoadApplicationFormByIdAndChangeSubmissionStatusToSubmitted() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		form.setApplicant(student);
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		assertEquals(SubmissionStatus.UNSUBMITTED, form.getSubmissionStatus());
		assertEquals("redirect:/applications?submissionSuccess=true", applicationController.submitApplication(id).getViewName());
		assertEquals(SubmissionStatus.SUBMITTED, form.getSubmissionStatus());
		EasyMock.verify(applicationsServiceMock);

	}

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmitterNotFormAcpplicant() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(form);		
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		
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
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(null);	
		EasyMock.replay(applicationsServiceMock);		
		applicationController.submitApplication(id);
	}
	
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowSubmitExceptionIfApplicationIsAlreadySubmitted() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(form);		
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		
		form.setApplicant(student);
		
		applicationController.submitApplication(id);
	}
	
	@Test
	public void shouldSaveNewPersonalDetails() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		userServiceMock.save(student);
		EasyMock.replay(userServiceMock);

		PersonalDetails personalDetails = new PersonalDetails();
		personalDetails.setFirstname("New First Name");
		personalDetails.setLastname("New Last Name");
		personalDetails.setEmailaddress("newemail@email.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "personalDetails");
		ModelAndView modelAndView = applicationController.editApplicationForm(personalDetails, student, 1, mappingResult);
		Assert.assertEquals("redirect:/application", modelAndView.getViewName());
	}
	
	@Test
	public void shouldGiveErrorsWhenIncorrectPersonalDetails() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		userServiceMock.save(student);
		EasyMock.replay(userServiceMock);

		PersonalDetails personalDetails = new PersonalDetails();
		personalDetails.setFirstname("");
		personalDetails.setLastname(" ");
		personalDetails.setEmailaddress("newemail");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "personalDetails");
		ModelAndView modelAndView = applicationController.editApplicationForm(personalDetails, student, 1, mappingResult);
		Assert.assertEquals("error/errors", modelAndView.getViewName());
		PageModel model = (PageModel) modelAndView.getModel().get("model");
		Assert.assertEquals(3, model.getErrorObjs().size());
	}
	

	@Before
	public void setUp() {

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		projectDAOMock = EasyMock.createMock(ProjectDAO.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);
		
		applicationController = new ApplicationFormController(projectDAOMock, applicationsServiceMock, userServiceMock, userPropertyEditorMock) {
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
