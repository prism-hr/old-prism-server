package com.zuehlke.pgadmissions.controllers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
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
	private DatePropertyEditor datePropertyEditorMock;
	private CountriesDAO countriesDAOMock;

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


	@SuppressWarnings("deprecation")
	@Test
	public void shouldSaveNewAddress() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		Address address = new Address();
		address.setAddressLocation("1, Main Street, London");
		address.setAddressPostCode("NW2345");
		address.setAddressPurpose("parents");
		address.setAddressCountry("UK");
		address.setAddressStartDate(new Date(2011, 11, 11));
		address.setAddressEndDate(new Date(2012, 11, 11));
		address.setAddressContactAddress(AddressStatus.YES);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/address_details", modelAndView.getViewName());
		com.zuehlke.pgadmissions.domain.Address addr = ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getAddresses().get(0);
		Assert.assertEquals("1, Main Street, London", addr.getLocation());
		Assert.assertEquals("NW2345", addr.getPostCode());
		Assert.assertEquals("parents", addr.getPurpose());
		Assert.assertEquals("UK", addr.getCountry());
	}

	@Test
	public void shouldNotSaveEmptyAddress() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		Address address = new Address();
		address.setAddressLocation("");
		address.setAddressStartDate(new Date());
		address.setAddressEndDate(new Date());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/address_details", modelAndView.getViewName());
	}
	
	@Test(expected=CannotUpdateApplicationException.class)
	public void shouldNotSaveNewAddressWhenApplicationIsSubmitted() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		Address address = new Address();
		address.setAddressLocation("london, uk");
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
		funding.setFundingType("scholarship");
		funding.setFundingDescription("my description");
		funding.setFundingValue("2000");
		funding.setFundingAwardDate(new Date());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		ModelAndView modelAndView = applicationController.addFunding(funding, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/funding_details", modelAndView.getViewName());
		Assert.assertEquals("scholarship", ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getFundings().get(0).getType());
	}

	@Test
	public void shouldNotSaveNewFunding() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		Funding funding = new Funding();
		funding.setFundingType("         ");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		ModelAndView modelAndView = applicationController.addFunding(funding, 1, 2, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/funding_details", modelAndView.getViewName());
	}
	
	@Test(expected=CannotUpdateApplicationException.class)
	public void shouldNotSaveNewFundingWhenAplicationIsSubmitted() {
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(student);
		EasyMock.replay(userServiceMock);
		
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);

		Funding funding = new Funding();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		applicationController.addFunding(funding, 1, 2, mappingResult, new ModelMap());
	}
	
	@Test
	public void shouldSaveNewQualification() {
		ApplicationForm form = new ApplicationFormBuilder().qualification(qualification).id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.expect(applicationsServiceMock.getQualificationById(3)).andReturn(qualification);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto,2, mappingResult);
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals("wip/qualifications", modelAndView.getViewName());
	}
	
	@Test
	public void shouldPopulateQualificationFromDTO() {
		ApplicationForm form = new ApplicationFormBuilder().qualification(qualification).id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.expect(applicationsServiceMock.getQualificationById(3)).andReturn(qualification);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualification, "qualification");
		
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto,2, mappingResult);
		Assert.assertEquals(qualificationDto.getAward_date(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getAward_date());
		Assert.assertEquals(qualificationDto.getCountry(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getCountry());
		Assert.assertEquals(qualificationDto.getGrade(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getGrade());
		Assert.assertEquals(qualificationDto.getInstitution(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getInstitution());
		Assert.assertEquals(qualificationDto.getLanguage_of_study(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getLanguage_of_study());
		Assert.assertEquals(qualificationDto.getLevel(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getLevel());
		Assert.assertEquals(qualificationDto.getName_of_programme(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getName_of_programme());
		Assert.assertEquals(qualificationDto.getScore(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getScore());
		Assert.assertEquals(qualificationDto.getStart_date(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getStart_date());
		Assert.assertEquals(qualificationDto.getTermination_date(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getTermination_date());
		Assert.assertEquals(qualificationDto.getTermination_reason(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getQualification_termination_reason());
		Assert.assertEquals(qualificationDto.getType(), ((PageModel)modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0).getQualification_type());
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
	public void shouldReturnInputQualificationDtoIfHasErrors() {
		BindingResult mappingResult = EasyMock.createMock(BindingResult.class);
		qualificationDto.setQualId(null);
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		qualificationValidator.validate(qualificationDto, mappingResult);
		EasyMock.expect(mappingResult.hasErrors()).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock, mappingResult, qualificationValidator);
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto,2,mappingResult);
		Assert.assertEquals(qualificationDto, ((ApplicationPageModel)modelAndView.getModel().get("model")).getQualification());
	}
	@Test
	public void shouldReturnNewQualificationDtoIfHasNoErrors() {
		BindingResult mappingResult = EasyMock.createMock(BindingResult.class);
		qualificationDto.setQualId(null);
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		qualificationValidator.validate(qualificationDto, mappingResult);
		EasyMock.expect(mappingResult.hasErrors()).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, mappingResult, qualificationValidator);
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto,2,mappingResult);
		Assert.assertNotNull(((ApplicationPageModel)modelAndView.getModel().get("model")).getQualification());
		Assert.assertNull(((ApplicationPageModel)modelAndView.getModel().get("model")).getQualification().getInstitution());
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
	
	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		EasyMock.replay(binderMock);
		applicationController.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}
	


	@Before
	public void setUp() throws ParseException {
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		newQualification = new QualificationBuilder().id(1).toQualification();
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);

		qualificationValidator = EasyMock.createMock(QualificationValidator.class);
		countriesDAOMock = EasyMock.createMock(CountriesDAO.class);
		
		applicationController = new UpdateApplicationFormController(userServiceMock, applicationsServiceMock, userPropertyEditorMock, 
				datePropertyEditorMock, qualificationValidator, countriesDAOMock) {
			ApplicationForm newApplicationForm() {
				return applicationForm;
			}
			Qualification newQualification() {
				return newQualification;
			}
		};

		
		qualificationDto = new QualificationDTO();
		qualificationDto.setQualId(3);
		qualificationDto.setAward_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"));
		qualificationDto.setCountry("UK");
		qualificationDto.setGrade("first");
		qualificationDto.setInstitution("UCL");
		qualificationDto.setLanguage_of_study("EN");
		qualificationDto.setLevel("advance");
		qualificationDto.setName_of_programme("CS");
		qualificationDto.setScore("100");
		qualificationDto.setStart_date(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		qualificationDto.setTermination_date(new SimpleDateFormat("yyyy/MM/dd").parse("2011/03/02"));
		qualificationDto.setTermination_reason("finished");
		qualificationDto.setTermination_date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/02"));
		qualificationDto.setType("degree");
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		qualification = new QualificationBuilder().id(3).q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).q_country("").q_grade("").q_institution("").q_language_of_study("").q_level("").q_name_of_programme("").q_score("").q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_termination_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/10/10")).q_termination_reason("").q_type("").toQualification();
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

