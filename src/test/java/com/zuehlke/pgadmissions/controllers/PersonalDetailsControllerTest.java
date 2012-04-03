package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.NationalityJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PhoneNumberJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailValidator;

public class PersonalDetailsControllerTest {

	private RegisteredUser currentUser;
	private CountryService countryServiceMock;
	private PersonalDetailsController controller;
	private PersonalDetailsService personalDetailsServiceMock;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private CountryPropertyEditor countryPropertyEditorMock;
	private DatePropertyEditor datePropertyEditorMock;
	private PersonalDetailValidator personalDetailValidatorMock;
	private PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditorMock;
	private LanguageService languageServiceMok;
	private LanguagePropertyEditor languagePropertyEditorMopck;
	private NationalityJSONPropertyEditor nationalityJSONPropertyEditorMock;

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditorMock);
		binderMock.registerCustomEditor(Language.class, languagePropertyEditorMopck);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Telephone.class, phoneNumberJSONPropertyEditorMock);
		binderMock.registerCustomEditor(Nationality.class, nationalityJSONPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);

	}

	@Test
	public void shouldGetPersonalDetailsFromService() {
		PersonalDetail personalDetails = new PersonalDetailsBuilder().id(1).toPersonalDetails();
		EasyMock.expect(personalDetailsServiceMock.getPersonalDetailsById(1)).andReturn(personalDetails);
		EasyMock.replay(personalDetailsServiceMock);

		PersonalDetail details = controller.getPersonalDetails(1);
		assertEquals(personalDetails, details);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfPersonalDetailsDoNotExist() {
		EasyMock.expect(personalDetailsServiceMock.getPersonalDetailsById(1)).andReturn(null);
		EasyMock.replay(personalDetailsServiceMock);

		controller.getPersonalDetails(1);

	}

	@Test
	public void shouldGetNewPersonalDetailsFromServiceIfIdIsNull() {
		final PersonalDetail personalDetails = new PersonalDetailsBuilder().id(1).toPersonalDetails();

		controller = new PersonalDetailsController(personalDetailsServiceMock, countryServiceMock, languageServiceMok, applicationFormPropertyEditorMock,
				countryPropertyEditorMock, languagePropertyEditorMopck, datePropertyEditorMock, personalDetailValidatorMock, phoneNumberJSONPropertyEditorMock,
				nationalityJSONPropertyEditorMock) {
			@Override
			PersonalDetail newPersonalDetail() {
				return personalDetails;
			}
		};
		PersonalDetail details = controller.getPersonalDetails(null);
		assertEquals(personalDetails, details);
	}

	@Test
	public void validatePersonalDetails() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		PersonalDetail personalDetail = new PersonalDetailsBuilder().id(1).toPersonalDetails();
		personalDetailValidatorMock.validate(personalDetail, errorsMock);
		EasyMock.replay(personalDetailValidatorMock);
		controller.editPersonalDetails(personalDetail, errorsMock);
		EasyMock.verify(personalDetailValidatorMock);

	}

	@Test
	public void shouldSavePersonalDetailsIfNewAndValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

		PersonalDetail personalDetail = new PersonalDetail();
		personalDetailsServiceMock.save(EasyMock.same(personalDetail));
		EasyMock.replay(errorsMock, personalDetailsServiceMock);

		controller.editPersonalDetails(personalDetail, errorsMock);
		EasyMock.verify(personalDetailsServiceMock);

	}

	@Test
	public void shouldNotSavePersonalDetailsIfNewAndNotValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);

		PersonalDetail personalDetail = new PersonalDetail();

		EasyMock.replay(errorsMock, personalDetailsServiceMock);

		controller.editPersonalDetails(personalDetail, errorsMock);
		EasyMock.verify(personalDetailsServiceMock);

	}
	

	@Test
	public void shouldSaveDBIfNotNewAndValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

		PersonalDetail personalDetail = new PersonalDetailsBuilder().id(1).toPersonalDetails();
		personalDetailsServiceMock.save(personalDetail);

		EasyMock.replay(errorsMock, personalDetailsServiceMock);

		controller.editPersonalDetails(personalDetail, errorsMock);
		EasyMock.verify(personalDetailsServiceMock);

	}

	@Test
	public void shoulNotdFlushToDBIfNotNewButNotdValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);

		PersonalDetail personalDetail = new PersonalDetailsBuilder().id(1).toPersonalDetails();

		EasyMock.replay(errorsMock, personalDetailsServiceMock);

		controller.editPersonalDetails(personalDetail, errorsMock);
		EasyMock.verify(personalDetailsServiceMock);

	}

	@Test
	public void shouldSetPersonalDetailsOnApplicationForm() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		PersonalDetail personalDetail = new PersonalDetailsBuilder().id(1).applicationForm(applicationForm).toPersonalDetails();

		EasyMock.replay(errorsMock);

		controller.editPersonalDetails(personalDetail, errorsMock);
		assertEquals(personalDetail, applicationForm.getPersonalDetails());

	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowCannotUpdateApplicationExceptionIfApplicationFormNotInUnsubmmitedState() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		PersonalDetail personalDetail = new PersonalDetailsBuilder().id(1).applicationForm(form).toPersonalDetails();
		controller.editPersonalDetails(personalDetail, null);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionIfCurrentUserNotApplicant() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(6).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(applicant).toApplicationForm();
		PersonalDetail personalDetail = new PersonalDetailsBuilder().id(1).applicationForm(form).toPersonalDetails();
		controller.editPersonalDetails(personalDetail, null);

	}

	@Test
	public void shouldReturnApplicationPageModelWithCorrectValues() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(currentUser).toApplicationForm();
		PersonalDetail personalDetail = new PersonalDetailsBuilder().id(5).applicationForm(form).englishFirstLanguage(CheckedStatus.YES).messengers("skypeAddress").toPersonalDetails();
		personalDetailsServiceMock.save(personalDetail);
		Country country1 = new CountryBuilder().id(1).toCountry();
		Country country2 = new CountryBuilder().id(2).toCountry();
		List<Country> countryList = Arrays.asList(country1, country2);
		EasyMock.expect(countryServiceMock.getAllCountries()).andReturn(countryList);
		List<Language> languages = Arrays.asList(new LanguageBuilder().id(1).toLanguage());
		EasyMock.expect(languageServiceMok.getAllLanguages()).andReturn(languages);

		EasyMock.replay(errorsMock, personalDetailsServiceMock, countryServiceMock, languageServiceMok);

		ModelAndView modelAndView = controller.editPersonalDetails(personalDetail, errorsMock);
		assertEquals("private/pgStudents/form/components/personal_details", modelAndView.getViewName());
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertNotNull(model);
		assertEquals(form, model.getApplicationForm());
		assertEquals(form, model.getApplicationForm());
		assertEquals("skypeAddress", model.getApplicationForm().getPersonalDetails().getMessenger());
		assertEquals(CheckedStatus.YES, model.getApplicationForm().getPersonalDetails().getEnglishFirstLanguage());
		assertTrue(model.getApplicationForm().getPersonalDetails().isEnglishFirstLanguage());
		assertEquals(errorsMock, model.getResult());
		assertSame(countryList, model.getCountries());
		assertSame(languages, model.getLanguages());
		assertEquals(Gender.values().length, model.getGenders().size());
		assertTrue(model.getGenders().containsAll(Arrays.asList(Gender.values())));
		assertEquals(PhoneType.values().length, model.getPhoneTypes().size());
		assertTrue(model.getPhoneTypes().containsAll(Arrays.asList(PhoneType.values())));


	}

	@Before
	public void setup() {

		countryServiceMock = EasyMock.createMock(CountryService.class);
		languageServiceMok = EasyMock.createMock(LanguageService.class);
		personalDetailsServiceMock = EasyMock.createMock(PersonalDetailsService.class);
		personalDetailValidatorMock = EasyMock.createMock(PersonalDetailValidator.class);

		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		countryPropertyEditorMock = EasyMock.createMock(CountryPropertyEditor.class);
		languagePropertyEditorMopck = EasyMock.createMock(LanguagePropertyEditor.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		phoneNumberJSONPropertyEditorMock = EasyMock.createMock(PhoneNumberJSONPropertyEditor.class);
		nationalityJSONPropertyEditorMock = EasyMock.createMock(NationalityJSONPropertyEditor.class);
		
		
		controller = new PersonalDetailsController(personalDetailsServiceMock, countryServiceMock, languageServiceMok, applicationFormPropertyEditorMock,
				countryPropertyEditorMock, languagePropertyEditorMopck, datePropertyEditorMock, personalDetailValidatorMock, phoneNumberJSONPropertyEditorMock,
				nationalityJSONPropertyEditorMock);

		currentUser = new RegisteredUserBuilder().id(1).toUser();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
