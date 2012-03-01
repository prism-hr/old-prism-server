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
import org.springframework.ui.ModelMap;
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
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
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
		personalDetails.setFirstName("New First Name");
		personalDetails.setLastName("New Last Name");
		personalDetails.setEmail("newemail@email.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "personalDetails");
		ModelAndView modelAndView = applicationController.editPersonalDetails(personalDetails, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("application/personal_details_applicant", modelAndView.getViewName());
		PageModel model = (PageModel) modelAndView.getModel().get("model");
		RegisteredUser user = model.getUser();
		Assert.assertEquals("New First Name", user.getFirstName());
		Assert.assertEquals("New Last Name", user.getLastName());
		Assert.assertEquals("newemail@email.com", user.getEmail());
	}
	
	
	@Test(expected=CannotUpdateApplicationException.class)
	public void shouldNotSaveNewPersonalDetailsWhenApplicationSubmitted() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);

		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);

		PersonalDetails personalDetails = new PersonalDetails();
		personalDetails.setFirstName("New First Name");
		personalDetails.setLastName("New Last Name");
		personalDetails.setEmail("newemail@email.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "personalDetails");
		applicationController.editPersonalDetails(personalDetails, 1, 2, mappingResult, new ModelMap());
	}

	@Test
	public void shouldNotSaveNewPersonalDetails() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);

		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);

		PersonalDetails personalDetails = new PersonalDetails();
		personalDetails.setFirstName("");
		personalDetails.setLastName("   ");
		personalDetails.setEmail("newemail@email");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "personalDetails");
		ModelAndView modelAndView = applicationController.editPersonalDetails(personalDetails, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("application/personal_details_applicant", modelAndView.getViewName());
		PageModel model = (PageModel) modelAndView.getModel().get("model");
		RegisteredUser user = model.getUser();
		Assert.assertEquals("mark", user.getFirstName());
		Assert.assertEquals("ham", user.getLastName());
		Assert.assertEquals("mark@gmail.com", user.getEmail());
	}


	@Test
	public void shouldSaveNewAddress() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		userServiceMock.save(student);
		EasyMock.replay(userServiceMock);
		Address address = new Address();
		address.setAddress("london, uk");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("application/address_applicant", modelAndView.getViewName());
		Assert.assertEquals("london, uk", ((PageModel)modelAndView.getModel().get("model")).getUser().getAddress());
	}

	@Test
	public void shouldNotSaveEmptyAddress() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		Address address = new Address();
		address.setAddress("");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("application/address_applicant", modelAndView.getViewName());
		Assert.assertEquals("london", ((PageModel)modelAndView.getModel().get("model")).getUser().getAddress());
	}
	
	@Test(expected=CannotUpdateApplicationException.class)
	public void shouldNotSaveNewAddressWhenApplicationIsSubmitted() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		Address address = new Address();
		address.setAddress("london, uk");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		applicationController.editAddress(address, 1, 2, mappingResult, new ModelMap());
	}

	@Test
	public void shouldSaveNewFunding() {
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);

		Funding funding = new Funding();
		funding.setFunding("self-funded");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		ModelAndView modelAndView = applicationController.addFunding(funding, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("application/funding_applicant", modelAndView.getViewName());
		Assert.assertEquals("self-funded", ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getFunding());
	}

	@Test
	public void shouldNotSaveNewFunding() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).funding("scholarship").toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		Funding funding = new Funding();
		funding.setFunding("         ");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		ModelAndView modelAndView = applicationController.addFunding(funding, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("application/funding_applicant", modelAndView.getViewName());
		Assert.assertEquals("scholarship", ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getFunding());
	}
	
	@Test(expected=CannotUpdateApplicationException.class)
	public void shouldNotSaveNewFundingWhenAplicationIsSubmitted() {
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);

		Funding funding = new Funding();
		funding.setFunding("self-funded");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		applicationController.addFunding(funding, 1, 2, mappingResult, new ModelMap());
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
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").address("london").firstName("mark").lastName("ham").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
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
