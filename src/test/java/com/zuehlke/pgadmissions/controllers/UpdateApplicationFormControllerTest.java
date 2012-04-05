package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
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
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PhoneNumberJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

public class UpdateApplicationFormControllerTest {

	private UpdateApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;;

	private DatePropertyEditor datePropertyEditorMock;
	private CountryService countriesServiceMock;
	private RefereeService refereeServiceMock;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditorMock;
	private RegisteredUser currentUser;
	private Referee referee;
	private RefereeValidator refereeValidator;
	private LanguageService languageServiceMock;
	private LanguagePropertyEditor languagePropertyEditorMock;
	private CountryPropertyEditor countryPropertyEditor;
	private EncryptionUtils encryptionUtilsMock;

	@Test
	public void shouldSaveNewAddress(){
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		Country country = new Country();
		country.setName("UK");
		EasyMock.expect(countriesServiceMock.getAllCountries()).andReturn(Arrays.asList(country));
		EasyMock.expect(countriesServiceMock.getCountryById(6)).andReturn(country);
		EasyMock.expect(countriesServiceMock.getCountryById(6)).andReturn(country);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, countriesServiceMock);
		Address address = new Address();
		address.setCurrentAddressLocation("1, Main Street, London");
		address.setCurrentAddressCountry(6);
		address.setContactAddressLocation("NY");
		address.setContactAddressCountry(6);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 2, null, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/address_details", modelAndView.getViewName());
		com.zuehlke.pgadmissions.domain.Address currentAddr = ((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getAddresses().get(0);
		Assert.assertEquals("1, Main Street, London", currentAddr.getLocation());
		Assert.assertEquals("UK", currentAddr.getCountry().getName());
		com.zuehlke.pgadmissions.domain.Address contactAddr = ((PageModel) modelAndView.getModel().get("model")).getApplicationForm().getAddresses().get(1);
		Assert.assertEquals("NY", contactAddr.getLocation());
		Assert.assertEquals("UK", contactAddr.getCountry().getName());
	}

	@Test
	public void shouldReturnAddMessageIfAddParameterProvided() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		Country country = new Country();
		country.setName("UK");
		EasyMock.expect(countriesServiceMock.getAllCountries()).andReturn(Arrays.asList(country));
		EasyMock.expect(countriesServiceMock.getCountryById(6)).andReturn(country);
		EasyMock.expect(countriesServiceMock.getCountryById(6)).andReturn(country);
		EasyMock.replay(applicationsServiceMock, countriesServiceMock);
		Address address = new Address();
		address.setCurrentAddressLocation("1, Main Street, London");
		address.setCurrentAddressCountry(6);
		address.setContactAddressLocation("NY");
		address.setContactAddressCountry(6);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 2, "add", mappingResult, new ModelMap());
		Assert.assertEquals("add", modelAndView.getModel().get("add"));
	}

	@Test
	public void shouldNotSaveEmptyAddress() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		Address address = new Address();
		address.setContactAddressLocation("");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		ModelAndView modelAndView = applicationController.editAddress(address, 2, null, mappingResult, new ModelMap());
		Assert.assertEquals("private/pgStudents/form/components/address_details", modelAndView.getViewName());
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldNotSaveNewAddressWhenApplicationIsSubmitted() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		Address address = new Address();
		address.setContactAddressLocation("london, uk");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		applicationController.editAddress(address, 2, null, mappingResult, new ModelMap());
	}

	@Test
	public void shouldGetRefereeDetailsFromService() {
		Referee referee = new RefereeBuilder().id(1).toReferee();
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
		final Referee refereeDetails = new RefereeBuilder().id(1).toReferee();

		applicationController = new UpdateApplicationFormController(applicationsServiceMock, userPropertyEditorMock, datePropertyEditorMock,
				countriesServiceMock, refereeServiceMock, phoneNumberJSONPropertyEditorMock, applicationFormPropertyEditorMock, refereeValidator,
				languageServiceMock, languagePropertyEditorMock, countryPropertyEditor, encryptionUtilsMock) {
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
		Referee referee = new RefereeBuilder().id(1).toReferee();
		refereeValidator.validate(referee, errorsMock);
		refereeServiceMock.save(referee);
		EasyMock.expect(refereeServiceMock.processRefereeAndGetAsUser(referee)).andReturn(null);
		EasyMock.replay(refereeServiceMock, refereeValidator);
		applicationController.editReferee(referee, null, errorsMock);
		EasyMock.verify(refereeValidator);
	}

	@Test
	public void shouldSaveRefereeDetailsIfNewAndValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

		Referee referee = new RefereeBuilder().id(1).toReferee();
		refereeValidator.validate(referee, errorsMock);
		refereeServiceMock.save(EasyMock.same(referee));

		EasyMock.replay(errorsMock, refereeServiceMock, refereeValidator);

		applicationController.editReferee(referee, null, errorsMock);
		EasyMock.verify(refereeServiceMock);
	}

	@Test
	public void shouldNotSaveRefereeIfNewAndNotValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);

		Referee referee = new Referee();

		EasyMock.replay(errorsMock, refereeServiceMock);

		applicationController.editReferee(referee, null, errorsMock);
		EasyMock.verify(refereeServiceMock);

	}

	@Ignore
	@Test
	public void shouldSetRefereeDetailsOnApplicationForm() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		Referee referee = new RefereeBuilder().application(applicationForm).id(1).toReferee();
		refereeValidator.validate(referee, errorsMock);
		refereeServiceMock.save(referee);

		EasyMock.replay(errorsMock, refereeValidator, refereeServiceMock);

		ModelAndView modelAndView = applicationController.editReferee(referee, null, errorsMock);
		System.out.println(((ApplicationPageModel) modelAndView.getModel().get("model")).getApplicationForm());
		assertTrue(((ApplicationPageModel) modelAndView.getModel().get("model")).getApplicationForm().getReferees().contains(referee));
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowCannotUpdateApplicationExceptionIfApplicationFormNotInUnsubmmitedState() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		Referee referee = new RefereeBuilder().application(form).id(1).toReferee();
		applicationController.editReferee(referee, null, null);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionIfCurrentUserNotApplicant() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(6).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(applicant).toApplicationForm();
		Referee referee = new RefereeBuilder().application(form).id(1).toReferee();
		applicationController.editReferee(referee, null, null);
	}

	@Test
	public void shouldReturnApplicationPageModelWithCorrectValues() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

		Referee referee = new RefereeBuilder().id(1).toReferee();
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(currentUser).toApplicationForm();
		referee.setApplication(form);
		refereeValidator.validate(referee, errorsMock);
		refereeServiceMock.save(EasyMock.same(referee));

		EasyMock.replay(errorsMock, refereeServiceMock, refereeValidator);

		ModelAndView modelAndView = applicationController.editReferee(referee, null, errorsMock);
		EasyMock.verify(refereeServiceMock);
		assertEquals("private/pgStudents/form/components/references_details", modelAndView.getViewName());
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertNotNull(model);
		assertEquals(form, model.getApplicationForm());
		assertEquals(currentUser, model.getUser());
		assertEquals(errorsMock, model.getResult());
		assertEquals(PhoneType.values().length, model.getPhoneTypes().size());
		assertTrue(model.getPhoneTypes().containsAll(Arrays.asList(PhoneType.values())));
		assertEquals("open", modelAndView.getModel().get("formDisplayState"));
		assertNull(modelAndView.getModel().get("add"));
	}

	@Test
	public void shouldSetMessageIfRefereeAddMessageProvided() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

		Referee referee = new RefereeBuilder().id(1).toReferee();
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(currentUser).toApplicationForm();
		referee.setApplication(form);
		refereeValidator.validate(referee, errorsMock);
		refereeServiceMock.save(EasyMock.same(referee));
		EasyMock.expect(refereeServiceMock.processRefereeAndGetAsUser(referee)).andReturn(null);

		EasyMock.replay(errorsMock, refereeServiceMock, refereeValidator);

		ModelAndView modelAndView = applicationController.editReferee(referee, "add", errorsMock);
		assertEquals("private/pgStudents/form/components/references_details", modelAndView.getViewName());
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertNotNull(model);
		assertEquals(form, model.getApplicationForm());
		assertEquals(currentUser, model.getUser());
		assertEquals(errorsMock, model.getResult());
		assertEquals(PhoneType.values().length, model.getPhoneTypes().size());
		assertTrue(model.getPhoneTypes().containsAll(Arrays.asList(PhoneType.values())));
		assertEquals("open", modelAndView.getModel().get("formDisplayState"));
		assertEquals("add", modelAndView.getModel().get("add"));
	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(Telephone.class, phoneNumberJSONPropertyEditorMock);
		binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Language.class, languagePropertyEditorMock);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditor);

		EasyMock.replay(binderMock);
		applicationController.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Before
	public void setUp() throws ParseException {
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		languagePropertyEditorMock = EasyMock.createMock(LanguagePropertyEditor.class);

		refereeValidator = EasyMock.createMock(RefereeValidator.class);

		referee = new RefereeBuilder().application(new ApplicationFormBuilder().id(1).toApplicationForm()).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry(null).addressLocation("london").addressPostcode("postcode").jobEmployer("zuhlke").jobTitle("se")
				.messenger("skypeAddress").phoneNumbers(new Telephone()).relationship("friend").toReferee();

		currentUser = new RegisteredUserBuilder().id(1).toUser();

		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		phoneNumberJSONPropertyEditorMock = EasyMock.createMock(PhoneNumberJSONPropertyEditor.class);
		countryPropertyEditor = EasyMock.createMock(CountryPropertyEditor.class);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);

		countriesServiceMock = EasyMock.createMock(CountryService.class);

		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);

		refereeServiceMock = EasyMock.createMock(RefereeService.class);

		applicationController = new UpdateApplicationFormController(applicationsServiceMock, userPropertyEditorMock, datePropertyEditorMock,
				countriesServiceMock, refereeServiceMock, phoneNumberJSONPropertyEditorMock, applicationFormPropertyEditorMock, refereeValidator,
				languageServiceMock, languagePropertyEditorMock, countryPropertyEditor, encryptionUtilsMock) {

			@Override
			ApplicationForm newApplicationForm() {
				return applicationForm;
			}

			@Override
			Referee newReferee() {
				return new Referee();
			}

		};

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
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
