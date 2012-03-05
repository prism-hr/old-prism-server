package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

public class UpdateApplicationFormControllerTest {

	private UpdateApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserService userServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;
	private Qualification qualification;
	private QualificationDTO qualificationDto;
	private Qualification newQualification;
	private QualificationValidator qualificationValidator;

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
		Assert.assertEquals("private/pgStudents/form/components/personal_details", modelAndView.getViewName());
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
		Assert.assertEquals("private/pgStudents/form/components/personal_details", modelAndView.getViewName());
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
		Assert.assertEquals("private/pgStudents/form/components/address_details", modelAndView.getViewName());
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
		Assert.assertEquals("private/pgStudents/form/components/address_details", modelAndView.getViewName());
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
		Assert.assertEquals("private/pgStudents/form/components/funding_details", modelAndView.getViewName());
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
		Assert.assertEquals("private/pgStudents/form/components/funding_details", modelAndView.getViewName());
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
	
	@Test
	public void shouldSaveNewQualification() {
		Qualification qualification = new QualificationBuilder().id(3).degree("BSc Computer Science").date_taken("2006/09/03").grade("First Class").institution("UCL").toQualification();
		ApplicationForm form = new ApplicationFormBuilder().qualification(qualification).id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.expect(applicationsServiceMock.getQualificationById(3)).andReturn(qualification);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		userServiceMock.save(student);
		userServiceMock.saveQualification(qualification);
		EasyMock.replay(userServiceMock);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto,2, mappingResult);
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals("private/pgStudents/form/components/qualification_details", modelAndView.getViewName());
		Assert.assertEquals("private/pgStudents/form/components/qualification_details", modelAndView.getViewName());
		Assert.assertEquals("", ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getDegree());
		Assert.assertEquals("", ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getDate_taken());
		Assert.assertEquals("", ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getGrade());
		Assert.assertEquals("", ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getInstitution());
	}
	
	@Test
	public void shouldNotSaveIfQualificationHasValidationErrors() {
		BindingResult mappingResult = EasyMock.createMock(BindingResult.class);
		qualificationDto.setQualId(null);
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		qualificationValidator.validate(qualificationDto, mappingResult);
		EasyMock.expect(mappingResult.hasErrors()).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock, mappingResult, qualificationValidator);
		applicationController.editQualification(qualificationDto,2,mappingResult);
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals(0, form.getQualifications().size() );
	}
	
	@Test
	public void shouldCreateANewQualification(){
		BindingResult mappingResult = EasyMock.createMock(BindingResult.class);
		qualificationDto.setQualId(null);
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		qualificationValidator.validate(qualificationDto, mappingResult);
		EasyMock.expect(mappingResult.hasErrors()).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, mappingResult, qualificationValidator);
		applicationController.editQualification(qualificationDto,2,mappingResult);
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals(1, form.getQualifications().size() );
		Assert.assertEquals(newQualification, form.getQualifications().get(0) );
		
	}
	
	@Test
	
	public void shouldPopulateUserFromSecurityContext(){
		BindingResult mappingResult = EasyMock.createMock(BindingResult.class);
		qualificationDto.setQualId(null);
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		qualificationValidator.validate(qualificationDto, mappingResult);
		EasyMock.expect(mappingResult.hasErrors()).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, mappingResult, qualificationValidator);
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto,2,mappingResult);
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals(student,  ((PageModel)modelAndView.getModel().get("model")).getUser());
	}
	


	@Before
	public void setUp() {

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		newQualification = new QualificationBuilder().id(1).toQualification();
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);

		qualificationValidator = EasyMock.createMock(QualificationValidator.class);
		
		applicationController = new UpdateApplicationFormController(userServiceMock, applicationsServiceMock, userPropertyEditorMock, qualificationValidator) {
			ApplicationForm newApplicationForm() {
				return applicationForm;
			}
			Qualification newQualification() {
				return newQualification;
			}
		};

		
		qualificationDto = new QualificationDTO();
		qualificationDto.setQualId(3);
		qualificationDto.setDate_taken("");
		qualificationDto.setDegree("");
		qualificationDto.setGrade("");
		qualificationDto.setInstitution("");
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		qualification = new QualificationBuilder().id(2).degree("BSc Computer Science").date_taken("2006/09/03").grade("First Class").institution("UCL").toQualification();
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

