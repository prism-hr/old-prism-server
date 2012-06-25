package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DisabilityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EthnicityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DisabilityService;
import com.zuehlke.pgadmissions.services.EthnicityService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

public class PersonalDetailsControllerTest {
	private RegisteredUser currentUser;
	private DatePropertyEditor datePropertyEditorMock;
	private ApplicationsService applicationsServiceMock;
	private PersonalDetailsValidator personalDetailsValidatorMock;
	private PersonalDetailsService personalDetailsServiceMock;
	private PersonalDetailsController controller;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	
	private CountryService countryServiceMock;
	private EthnicityService ethnicityServiceMock;
	private DisabilityService disabilityServiceMock;
	private LanguageService languageServiceMok;
	private CountryPropertyEditor countryPropertyEditorMock;
	private EthnicityPropertyEditor ethnicityPropertyEditorMock;
	private DisabilityPropertyEditor disabilityPropertyEditorMock;
	private LanguagePropertyEditor languagePropertyEditorMopck;
	private UserService userServiceMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).toApplicationForm()).toPersonalDetails();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(personalDetailsServiceMock, errors);
		controller.editPersonalDetails(personalDetails, errors);
		EasyMock.verify(personalDetailsServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editPersonalDetails(null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getPersonalDetailsView();
	}

	@Test
	public void shouldReturnPersonalDetailsView() {
		assertEquals("/private/pgStudents/form/components/personal_details", controller.getPersonalDetailsView());
	}

	@Test
	public void shouldReturnAllLanguages() {
		List<Language> languageList = Arrays.asList(new LanguageBuilder().id(1).toLanguage(), new LanguageBuilder().id(2).toLanguage());
		EasyMock.expect(languageServiceMok.getAllLanguages()).andReturn(languageList);
		EasyMock.replay(languageServiceMok);
		List<Language> allLanguages = controller.getAllLanguages();
		assertSame(languageList, allLanguages);
	}

	@Test
	public void shouldReturnAllCountries() {
		List<Country> countryList = Arrays.asList(new CountryBuilder().id(1).toCountry(), new CountryBuilder().id(2).toCountry());
		EasyMock.expect(countryServiceMock.getAllCountries()).andReturn(countryList);
		EasyMock.replay(countryServiceMock);
		List<Country> allCountries = controller.getAllCountries();
		assertSame(countryList, allCountries);
	}

	@Test
	public void returnAllEthnicities() {
		List<Ethnicity> ethnicityList = Arrays.asList(new EthnicityBuilder().id(1).toEthnicity(), new EthnicityBuilder().id(2).toEthnicity());
		EasyMock.expect(ethnicityServiceMock.getAllEthnicities()).andReturn(ethnicityList);
		EasyMock.replay(ethnicityServiceMock);
		List<Ethnicity> allEthnicities = controller.getAllEthnicities();
		assertSame(ethnicityList, allEthnicities);
	}

	@Test
	public void returnAllDisabilities() {
		List<Disability> disabilityList = Arrays.asList(new DisabilityBuilder().id(1).toDisability(), new DisabilityBuilder().id(2).toDisability());
		EasyMock.expect(disabilityServiceMock.getAllDisabilities()).andReturn(disabilityList);
		EasyMock.replay(disabilityServiceMock);
		List<Disability> allDisabilities = controller.getAllDisabilities();
		assertSame(disabilityList, allDisabilities);
	}

	@Test
	public void shouldReturnCurrentUser() {
		assertEquals(currentUser, controller.getUser());
	}

	@Test
	public void shouldReturnAllGenders() {
		Gender[] genders = controller.getGenders();
		assertArrayEquals(genders, Gender.values());
	}

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
		assertEquals(applicationForm, returnedApplicationForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("1");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserCAnnotSeeApplFormOnGet() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationsServiceMock, currentUser);
		controller.getApplicationForm("1");

	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(personalDetailsValidatorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Language.class, languagePropertyEditorMopck);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditorMock);
		binderMock.registerCustomEditor(Ethnicity.class, ethnicityPropertyEditorMock);
		binderMock.registerCustomEditor(Disability.class, disabilityPropertyEditorMock);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetPersonalDetailsFromApplicationForm() {

		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).toPersonalDetails();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.setPersonalDetails(personalDetails);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(applicationsServiceMock, currentUser);

		PersonalDetails returnedPersonalDetails = controller.getPersonalDetails("5");
		assertEquals(personalDetails, returnedPersonalDetails);
	}

	@Test
	public void shouldReturnNewPersonalDetailsIfApplicationFormHasNoPersonalDetails() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(applicationsServiceMock, currentUser);
		PersonalDetails returnedPersonalDetails = controller.getPersonalDetails("5");
		assertNull(returnedPersonalDetails.getId());
	}

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));
	}

	@Test
	public void shouldReturnErrorCode() {
		assertEquals("bob", controller.getErrorCode("bob"));
	}

	@Test
	public void shouldSaveQulificationAndRedirectIfNoErrors() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").toApplicationForm();
		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(applicationForm).toPersonalDetails();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		personalDetailsServiceMock.save(personalDetails);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(personalDetailsServiceMock,applicationsServiceMock,  errors);
		String view = controller.editPersonalDetails(personalDetails, errors);
		EasyMock.verify(personalDetailsServiceMock, applicationsServiceMock);
		assertEquals("redirect:/update/getPersonalDetails?applicationId=ABC", view);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(),Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(new ApplicationFormBuilder().id(5).toApplicationForm()).toPersonalDetails();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);

		EasyMock.replay(personalDetailsServiceMock, errors);
		String view = controller.editPersonalDetails(personalDetails, errors);
		EasyMock.verify(personalDetailsServiceMock);
		assertEquals("/private/pgStudents/form/components/personal_details", view);
	}

	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		countryServiceMock = EasyMock.createMock(CountryService.class);
		disabilityServiceMock = EasyMock.createMock(DisabilityService.class);
		ethnicityServiceMock = EasyMock.createMock(EthnicityService.class);
		languageServiceMok = EasyMock.createMock(LanguageService.class);
		personalDetailsServiceMock = EasyMock.createMock(PersonalDetailsService.class);

		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		countryPropertyEditorMock = EasyMock.createMock(CountryPropertyEditor.class);
		ethnicityPropertyEditorMock = EasyMock.createMock(EthnicityPropertyEditor.class);
		disabilityPropertyEditorMock = EasyMock.createMock(DisabilityPropertyEditor.class);
		languagePropertyEditorMopck = EasyMock.createMock(LanguagePropertyEditor.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

		personalDetailsValidatorMock = EasyMock.createMock(PersonalDetailsValidator.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new PersonalDetailsController(applicationsServiceMock, userServiceMock, applicationFormPropertyEditorMock,// 
				datePropertyEditorMock, countryServiceMock, ethnicityServiceMock, disabilityServiceMock,// 
				languageServiceMok, languagePropertyEditorMopck, countryPropertyEditorMock,// 
				disabilityPropertyEditorMock, ethnicityPropertyEditorMock,// 
				personalDetailsValidatorMock, personalDetailsServiceMock);

	

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
	

	}

	
}
