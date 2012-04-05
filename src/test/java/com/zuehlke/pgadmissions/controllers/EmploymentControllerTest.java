package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;

public class EmploymentControllerTest {

	private UsernamePasswordAuthenticationToken authenticationToken;
	private RegisteredUser currentUser;
	private EmploymentPositionService employmentServiceMock;
	private EmploymentController controller;
	private LanguageService languageServiceMock;
	private CountryService countriesServiceMock;
	private ApplicationsService applicationsServiceMock;
	private LanguagePropertyEditor languagePropertyEditorMock;
	private DatePropertyEditor datePropertyEditorMock;
	private CountryPropertyEditor countryPropertyEditor;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private EmploymentPositionValidator employmentValidatorMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		EmploymentPosition employment = new EmploymentPositionBuilder().id(1)
				.application(new ApplicationFormBuilder().id(5).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm()).toEmploymentPosition();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(employmentServiceMock, errors);
		controller.editEmployment(employment, errors);
		EasyMock.verify(employmentServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editEmployment(null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getEmploymentView();
	}

	@Test
	public void shouldReturnEmploymentView() {
		assertEquals("/private/pgStudents/form/components/employment_position_details.ftl", controller.getEmploymentView());
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
		authenticationToken.setDetails(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		ApplicationForm returnedApplicationForm = controller.getApplicationForm(1);
		assertEquals(applicationForm, returnedApplicationForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(1);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserCAnnotSeeApplFormOnGet() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationsServiceMock, currentUser);
		controller.getApplicationForm(1);

	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(employmentValidatorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Language.class, languagePropertyEditorMock);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditor);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetEmploymentFromServiceIfIdProvided() {
		EmploymentPosition employment = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
		EasyMock.expect(employmentServiceMock.getEmploymentPositionById(1)).andReturn(employment);
		EasyMock.replay(employmentServiceMock);
		EmploymentPosition returnedEmploymentPosition = controller.getEmploymentPosition(1);
		assertEquals(employment, returnedEmploymentPosition);
	}

	@Test
	public void shouldReturnNewEmploymentIfIdIsNull() {
		EmploymentPosition returnedEmploymentPosition = controller.getEmploymentPosition(null);
		assertNull(returnedEmploymentPosition.getId());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfEmploymentDoesNotExist() {
		EasyMock.expect(employmentServiceMock.getEmploymentPositionById(1)).andReturn(null);
		EasyMock.replay(employmentServiceMock);
		controller.getEmploymentPosition(1);

	}
	
	

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));

	}

	@Test
	public void shouldSaveEmploymentAndRedirectIfNoErrors() {
		EmploymentPosition employment = new EmploymentPositionBuilder().id(1).application(new ApplicationFormBuilder().id(5).toApplicationForm()).toEmploymentPosition();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		employmentServiceMock.save(employment);
		EasyMock.replay(employmentServiceMock, errors);
		String view = controller.editEmployment(employment, errors);
		EasyMock.verify(employmentServiceMock);
		assertEquals( "redirect:/update/getEmploymentPosition?applicationId=5", view);
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		EmploymentPosition employment = new EmploymentPositionBuilder().id(1).application(new ApplicationFormBuilder().id(5).toApplicationForm()).toEmploymentPosition();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);
	
		EasyMock.replay(employmentServiceMock, errors);
		String view = controller.editEmployment(employment, errors);
		EasyMock.verify(employmentServiceMock);
		assertEquals(EmploymentController.STUDENTS_EMPLOYMENT_DETAILS_VIEW, view);
	}
	
	
	@Before
	public void setUp() throws ParseException {

		employmentServiceMock = EasyMock.createMock(EmploymentPositionService.class);
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		countriesServiceMock = EasyMock.createMock(CountryService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);

		languagePropertyEditorMock = EasyMock.createMock(LanguagePropertyEditor.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		countryPropertyEditor = EasyMock.createMock(CountryPropertyEditor.class);
		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);

		employmentValidatorMock = EasyMock.createMock(EmploymentPositionValidator.class);

		controller = new EmploymentController(employmentServiceMock, languageServiceMock, countriesServiceMock, applicationsServiceMock,
				languagePropertyEditorMock, datePropertyEditorMock, countryPropertyEditor, applicationFormPropertyEditorMock, employmentValidatorMock);

		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
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
