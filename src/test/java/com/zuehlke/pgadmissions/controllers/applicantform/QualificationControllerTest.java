package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

public class QualificationControllerTest {
	private RegisteredUser currentUser;
	private LanguageService languageServiceMock;
	private LanguagePropertyEditor languagePropertyEditorMock;
	private DatePropertyEditor datePropertyEditorMock;
	private CountryPropertyEditor countryPropertyEditor;
	private ApplicationsService applicationsServiceMock;
	private QualificationValidator qualificationValidatorMock;
	private CountryService countriesServiceMock;
	private QualificationService qualificationServiceMock;
	private QualificationController controller;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;

	private DocumentPropertyEditor documentPropertyEditorMock;
	private UserService userServiceMock;
	private EncryptionHelper encryptionHelperMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		Qualification qualification = new QualificationBuilder().id(1)
				.application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).toApplicationForm()).toQualification();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(qualificationServiceMock, errors);
		controller.editQualification(qualification, errors);
		EasyMock.verify(qualificationServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editQualification(null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getQualificationView();
	}

	@Test
	public void shouldReturnQualificationView() {
		assertEquals(QualificationController.APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, controller.getQualificationView());
	}

	@Test
	public void shouldReturnAllLanguages() {
		List<Language> languageList = Arrays.asList(new LanguageBuilder().id(1).toLanguage(), new LanguageBuilder().id(2).toLanguage());
		EasyMock.expect(languageServiceMock.getAllLanguages()).andReturn(languageList);
		EasyMock.replay(languageServiceMock);
		List<Language> allLanguages = controller.getAllLanguages();
		assertSame(languageList, allLanguages);
	}

	@Test
	public void shouldReturnAllCountries() {
		List<Country> countryList = Arrays.asList(new CountryBuilder().id(1).toCountry(), new CountryBuilder().id(2).toCountry());
		EasyMock.expect(countriesServiceMock.getAllCountries()).andReturn(countryList);
		EasyMock.replay(countriesServiceMock);
		List<Country> allCountries = controller.getAllCountries();
		assertSame(countryList, allCountries);
	}

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
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
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
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
		binderMock.setValidator(qualificationValidatorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Language.class, languagePropertyEditorMock);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditor);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetQualificationFromServiceIfIdProvided() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		Qualification qualification = new QualificationBuilder().id(1).toQualification();
		EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(qualification);
		EasyMock.replay(qualificationServiceMock, encryptionHelperMock);
		Qualification returnedQualification = controller.getQualification("bob");
		assertEquals(qualification, returnedQualification);
	}

	@Test
	public void shouldReturnNewQualificationIfIdIsNull() {
		Qualification returnedQualification = controller.getQualification(null);
		assertNull(returnedQualification.getId());
	}

	@Test
	public void shouldReturnNewQualificationIfIdIsBlank() {
		Qualification returnedQualification = controller.getQualification("");
		assertNull(returnedQualification.getId());
	}
	
	@Test(expected = ResourceNotFoundException.class)	
	public void shouldThrowResourceNotFoundExceptionIfQualificationDoesNotExist() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(null);
		EasyMock.replay(qualificationServiceMock,encryptionHelperMock);
		controller.getQualification("bob");

	}

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));

	}

	@Test
	public void shouldSaveQulificationAndRedirectIfNoErrors() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").toApplicationForm();
		Qualification qualification = new QualificationBuilder().id(1).application(applicationForm).toQualification();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		qualificationServiceMock.save(qualification);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(qualificationServiceMock, applicationsServiceMock, errors);
		String view = controller.editQualification(qualification, errors);
		EasyMock.verify(qualificationServiceMock, applicationsServiceMock);
		assertEquals("redirect:/update/getQualification?applicationId=ABC", view);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		Qualification qualification = new QualificationBuilder().id(1).application(new ApplicationFormBuilder().id(5).toApplicationForm()).toQualification();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);

		EasyMock.replay(qualificationServiceMock, errors);
		String view = controller.editQualification(qualification, errors);
		EasyMock.verify(qualificationServiceMock);
		assertEquals(QualificationController.APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, view);
	}

	@Before
	public void setUp() {
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		languagePropertyEditorMock = EasyMock.createMock(LanguagePropertyEditor.class);

		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

		countryPropertyEditor = EasyMock.createMock(CountryPropertyEditor.class);
		countriesServiceMock = EasyMock.createMock(CountryService.class);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);

		qualificationValidatorMock = EasyMock.createMock(QualificationValidator.class);
		qualificationServiceMock = EasyMock.createMock(QualificationService.class);

		documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);

		userServiceMock = EasyMock.createMock(UserService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);

		controller = new QualificationController(applicationsServiceMock, applicationFormPropertyEditorMock, datePropertyEditorMock, countriesServiceMock,
				languageServiceMock, languagePropertyEditorMock, countryPropertyEditor, qualificationValidatorMock, qualificationServiceMock,
				documentPropertyEditorMock, userServiceMock, encryptionHelperMock);

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
