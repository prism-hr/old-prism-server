package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.MessengerJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PhoneNumberJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

public class UpdateApplicationFormControllerTest {

	private UpdateApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;
	private Qualification qualification;
	private QualificationDTO qualificationDto;
	private Qualification newQualification;
	private QualificationValidator qualificationValidator;
	private DatePropertyEditor datePropertyEditorMock;
	private CountryService countriesServiceMock;
	private RefereeService refereeServiceMock;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditorMock;
	private MessengerJSONPropertyEditor messengerJSONPropertyEditorMock;
	private RegisteredUser currentUser;
	private Referee referee;
	private RefereeValidator refereeValidator;


	
	


	@SuppressWarnings("deprecation")
	@Test
	public void shouldSaveNewAddress() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);
		Address address = new Address();
		address.setAddressLocation("1, Main Street, London");
		address.setAddressPostCode("NW2345");
		address.setAddressPurpose("industrial sponsor");
		address.setAddressCountry("UK");
		address.setAddressStartDate(new Date(2011, 11, 11));
		address.setAddressEndDate(new Date(2012, 11, 11));
		address.setAddressContactAddress("YES");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 2, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/address_details", modelAndView.getViewName());
		com.zuehlke.pgadmissions.domain.Address addr = ((PageModel) modelAndView.getModel().get("model"))
				.getApplicationForm().getAddresses().get(0);
		Assert.assertEquals("1, Main Street, London", addr.getLocation());
		Assert.assertEquals("NW2345", addr.getPostCode());
		Assert.assertEquals("industrial sponsor", addr.getPurpose());
		Assert.assertEquals("UK", addr.getCountry());
	}

	@Test
	public void shouldNotSaveEmptyAddress() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		Address address = new Address();
		address.setAddressLocation("");
		address.setAddressStartDate(new Date());
		address.setAddressEndDate(new Date());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 2, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/address_details", modelAndView.getViewName());
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldNotSaveNewAddressWhenApplicationIsSubmitted() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED)
				.toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		Address address = new Address();
		address.setAddressLocation("london, uk");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		applicationController.editAddress(address, 2, mappingResult, new ModelMap());
	}

	@Test
	public void shouldSaveNewFunding() {

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
		ModelAndView modelAndView = applicationController.addFunding(funding, 2, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/funding_details", modelAndView.getViewName());
		Assert.assertEquals("scholarship", ((PageModel) modelAndView.getModel().get("model")).getApplicationForm()
				.getFundings().get(0).getType());
	}

	@Test
	public void shouldNotSaveNewFunding() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		Funding funding = new Funding();
		funding.setFundingType("         ");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		ModelAndView modelAndView = applicationController.addFunding(funding, 2, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/funding_details", modelAndView.getViewName());
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldNotSaveNewFundingWhenAplicationIsSubmitted() {

		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED)
				.toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);

		Funding funding = new Funding();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		applicationController.addFunding(funding, 2, mappingResult, new ModelMap());
	}

	@Test
	public void shouldSaveNewEmploymentPosition() throws ParseException {

		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);

		com.zuehlke.pgadmissions.dto.EmploymentPosition positionDto = new com.zuehlke.pgadmissions.dto.EmploymentPosition();
		positionDto.setPosition_employer("Mark");
		positionDto.setPosition_endDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		positionDto.setPosition_language("English");
		positionDto.setPosition_remit("cashier");
		positionDto.setPosition_startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		positionDto.setPosition_title("head of department");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		ModelAndView modelAndView = applicationController.addEmploymentPosition(positionDto, 2, mappingResult,
				new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/employment_position_details",
				modelAndView.getViewName());
		Assert.assertEquals("English", ((PageModel) modelAndView.getModel().get("model")).getApplicationForm()
				.getEmploymentPositions().get(0).getPosition_language());
	}

	@Test
	public void shouldNotSaveNewEmploymentPosition() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		com.zuehlke.pgadmissions.dto.EmploymentPosition positionDto = new com.zuehlke.pgadmissions.dto.EmploymentPosition();
		positionDto.setPosition_employer("");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "position");
		applicationController.addEmploymentPosition(positionDto, 2, mappingResult, new ModelMap());
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldNotSaveNewEmploymentpositionWhenAplicationIsSubmitted() {

		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED)
				.toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);

		com.zuehlke.pgadmissions.dto.EmploymentPosition positionDto = new com.zuehlke.pgadmissions.dto.EmploymentPosition();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(positionDto, "funding");
		applicationController.addEmploymentPosition(positionDto, 2, mappingResult, new ModelMap());
	}
	
	@Test
	public void shouldGetRefereeDetailsFromService() {
		Referee referee = new RefereeBuilder().refereeId(1).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock);

		Referee refereeDetails = applicationController.getRefereeDetails(1);
		assertEquals(refereeDetails, referee);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfPersonalDetailsDoNotExist() {
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(null);
		EasyMock.replay(refereeServiceMock);

		applicationController.getRefereeDetails(1);

	}
	
	@Ignore
	@Test
	public void shouldGetNewRefereeDetailsFromServiceIfIdIsNull() {
		final Referee refereeDetails = new RefereeBuilder().refereeId(1).toReferee();

		applicationController = new UpdateApplicationFormController(applicationsServiceMock, userPropertyEditorMock,
				datePropertyEditorMock, countriesServiceMock, refereeServiceMock, phoneNumberJSONPropertyEditorMock, messengerJSONPropertyEditorMock, applicationFormPropertyEditorMock, refereeValidator){
			Referee newReferee() {
				return new Referee();
			}
			
		};
		
		Referee details = applicationController.getRefereeDetails(null);
		assertEquals(refereeDetails, details);
	}
	
	@Test
	public void validateRefereeDetails() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		Referee referee = new RefereeBuilder().refereeId(1).toReferee();
		refereeValidator.validate(referee, errorsMock);
		EasyMock.replay(refereeValidator);
		applicationController.editReferee(referee, errorsMock);
		EasyMock.verify(refereeValidator);
	}
	
	@Test
	public void shouldSaveRefereeDetailsIfNewAndValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		
		Referee referee = new RefereeBuilder().refereeId(1).toReferee();
		refereeValidator.validate(referee, errorsMock);
		refereeServiceMock.save(EasyMock.same(referee));
		
		EasyMock.replay(errorsMock, refereeServiceMock, refereeValidator);

		applicationController.editReferee(referee, errorsMock);
		EasyMock.verify(refereeServiceMock);
	}
	
	@Test
	public void shouldNotSaveRefereeIfNewAndNotValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);

		Referee referee = new Referee();

		EasyMock.replay(errorsMock, refereeServiceMock);

		applicationController.editReferee(referee, errorsMock);
		EasyMock.verify(refereeServiceMock);

	}
	
	@Test
	public void shouldSetRefereeDetailsOnApplicationForm() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		Referee referee = new RefereeBuilder().application(applicationForm).refereeId(1).toReferee();
		refereeValidator.validate(referee, errorsMock);
		refereeServiceMock.save(EasyMock.same(referee));
		
		referee.setApplication(applicationForm);
		EasyMock.replay(errorsMock, refereeValidator);

		applicationController.editReferee(referee, errorsMock);
		assertTrue(applicationForm.getReferees().contains(referee));
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowCannotUpdateApplicationExceptionIfApplicationFormNotInUnsubmmitedState() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		Referee referee = new RefereeBuilder().application(form).refereeId(1).toReferee();
		applicationController.editReferee(referee, null);

	}
	

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionIfCurrentUserNotApplicant() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(6).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(applicant).toApplicationForm();
		Referee referee = new RefereeBuilder().application(form).refereeId(1).toReferee();
		applicationController.editReferee(referee, null);
	}
	
	@Test
	public void shouldReturnApplicationPageModelWithCorrectValues() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(currentUser).toApplicationForm();
		referee.setFirstname("firstname");
		referee.setApplication(form);
		refereeServiceMock.save(referee);

		ModelAndView modelAndView = applicationController.editReferee(referee, errorsMock);
		assertEquals("private/pgStudents/form/components/references_details", modelAndView.getViewName());
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertNotNull(model);
		assertEquals(form, model.getApplicationForm());
		assertEquals(currentUser, model.getUser());
		assertEquals(errorsMock, model.getResult());
		assertEquals(PhoneType.values().length, model.getPhoneTypes().size());
		assertTrue(model.getPhoneTypes().containsAll(Arrays.asList(PhoneType.values())));
		assertEquals("open", modelAndView.getModel().get("formDisplayState"));

	}
	
	@Test
	public void shouldSaveNewQualification() {
		ApplicationForm form = new ApplicationFormBuilder().qualification(qualification).id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.expect(applicationsServiceMock.getQualificationById(3)).andReturn(qualification);
		applicationsServiceMock.update(qualification);
		EasyMock.replay(applicationsServiceMock);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto, 2, mappingResult,
				new ModelMap());
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals("private/pgStudents/form/components/qualification_details", modelAndView.getViewName());
	}

	@Test
	public void shouldPopulateQualificationFromDTO() {
		ApplicationForm form = new ApplicationFormBuilder().qualification(qualification).id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.expect(applicationsServiceMock.getQualificationById(3)).andReturn(qualification);
		applicationsServiceMock.save(form);
		applicationsServiceMock.update(qualification);
		EasyMock.replay(applicationsServiceMock);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");

		ModelAndView modelAndView = applicationController.editQualification(qualificationDto, 2, mappingResult,
				new ModelMap());
		Assert.assertEquals(qualificationDto.getQualificationAwardDate(),
				((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0)
						.getQualificationAwardDate());
		Assert.assertEquals(qualificationDto.getQualificationGrade(),
				((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0)
						.getQualificationGrade());
		Assert.assertEquals(qualificationDto.getQualificationInstitution(),
				((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0)
						.getQualificationInstitution());
		Assert.assertEquals(qualificationDto.getQualificationLanguage(),
				((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0)
						.getQualificationLanguage());
		Assert.assertEquals(qualificationDto.getQualificationLevel(),
				((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0)
						.getQualificationLevel());
		Assert.assertEquals(qualificationDto.getQualificationProgramName(),
				((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0)
						.getQualificationProgramName());
		Assert.assertEquals(qualificationDto.getQualificationScore(),
				((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0)
						.getQualificationScore());
		Assert.assertEquals(qualificationDto.getQualificationStartDate(),
				((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getQualifications().get(0)
						.getQualificationStartDate());
		Assert.assertEquals(qualificationDto.getQualificationType(), ((PageModel) modelAndView.getModel().get("model"))
				.getApplicationForm().getQualifications().get(0).getQualificationType());
	}

	@Test
	public void shouldReturnInputQualificationDtoIfHasErrors() {
		qualificationDto.setQualificationId(null);
		qualificationDto.setQualificationLevel("");
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock, qualificationValidator);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto, 2, mappingResult,
				new ModelMap());
		Assert.assertEquals(qualificationDto,
				((ApplicationPageModel) modelAndView.getModel().get("model")).getQualification());
	}

	@Test
	public void shouldReturnNewQualificationDtoIfHasNoErrors() {
		qualificationDto.setQualificationId(null);
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, qualificationValidator);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto, 2, mappingResult,
				new ModelMap());
		Assert.assertNotNull(((ApplicationPageModel) modelAndView.getModel().get("model")).getQualification());
		Assert.assertNull(((ApplicationPageModel) modelAndView.getModel().get("model")).getQualification()
				.getQualificationInstitution());
	}

	@Test
	public void shouldCreateANewQualification() {
		qualificationDto.setQualificationId(null);
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, qualificationValidator);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto, 2, mappingResult,
				new ModelMap());
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals(1, form.getQualifications().size());
		Assert.assertEquals("first", ((PageModel) modelAndView.getModel().get("model")).getApplicationForm()
				.getQualifications().get(0).getQualificationGrade());

	}

	@Test
	public void shouldEditExistingQualification() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		form.getQualifications().add(qualification);
		qualification.setApplication(form);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.expect(applicationsServiceMock.getQualificationById(3)).andReturn(qualification);
		applicationsServiceMock.update(qualification);
		EasyMock.replay(applicationsServiceMock, qualificationValidator);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(qualificationDto, "qualification");
		ModelAndView modelAndView = applicationController.editQualification(qualificationDto, 2, mappingResult,
				new ModelMap());
		EasyMock.verify(applicationsServiceMock);
		Assert.assertEquals(1, ((PageModel) modelAndView.getModel().get("model")).getApplicationForm()
				.getQualifications().size());
		Assert.assertEquals("CS", ((PageModel) modelAndView.getModel().get("model")).getApplicationForm()
				.getQualifications().get(0).getQualificationProgramName());

	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(Telephone.class, phoneNumberJSONPropertyEditorMock);
		binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Messenger.class, messengerJSONPropertyEditorMock);
		EasyMock.replay(binderMock);
		applicationController.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Before
	public void setUp() throws ParseException {
		
		refereeValidator = EasyMock.createMock(RefereeValidator.class);
		
		referee = new RefereeBuilder().application(new ApplicationFormBuilder().id(1).toApplicationForm()).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry("uk").addressLocation("london").addressPostcode("postcode").jobEmployer("zuhlke").jobTitle("se")
				.messenger(new Messenger()).phoneNumbers(new Telephone()).relationship("friend").toReferee();
		
		
		currentUser = new RegisteredUserBuilder().id(1).toUser();
		
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		newQualification = new QualificationBuilder().id(1).toQualification();

		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		phoneNumberJSONPropertyEditorMock = EasyMock.createMock(PhoneNumberJSONPropertyEditor.class);
		messengerJSONPropertyEditorMock = EasyMock.createMock(MessengerJSONPropertyEditor.class);
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);

		qualificationValidator = EasyMock.createMock(QualificationValidator.class);
		countriesServiceMock = EasyMock.createMock(CountryService.class);

		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		
		applicationController = new UpdateApplicationFormController(applicationsServiceMock, userPropertyEditorMock,
				datePropertyEditorMock, countriesServiceMock,  refereeServiceMock, phoneNumberJSONPropertyEditorMock, messengerJSONPropertyEditorMock, applicationFormPropertyEditorMock, refereeValidator){
			ApplicationForm newApplicationForm() {
				return applicationForm;
			}

			Qualification newQualification() {
				return newQualification;
			}
			

			Referee newReferee() {
				return new Referee();
			}
			
		};

		
		qualificationDto = new QualificationDTO();
		qualificationDto.setQualificationId(3);
		qualificationDto.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"));
		qualificationDto.setQualificationGrade("first");
		qualificationDto.setQualificationInstitution("UCL");
		qualificationDto.setQualificationLanguage("EN");
		qualificationDto.setQualificationLevel("advance");
		qualificationDto.setQualificationProgramName("CS");
		qualificationDto.setQualificationScore("100");
		qualificationDto.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
		qualificationDto.setQualificationType("degree");
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		qualification = new QualificationBuilder().id(3)
				.q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).q_grade("").q_institution("")
				.q_language_of_study("").q_level("").q_name_of_programme("").q_score("")
				.q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_type("").toQualification();
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark")
				.lastName("ham").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
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

