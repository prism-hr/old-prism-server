package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
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
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

public class RefereeControllerTest {

	private UsernamePasswordAuthenticationToken authenticationToken;
	private RegisteredUser currentUser;
	private RefereeService refereeServiceMock;
	private RefereeController controller;	
	private CountryService countriesServiceMock;
	private ApplicationsService applicationsServiceMock;	
	private CountryPropertyEditor countryPropertyEditor;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private RefereeValidator refereeValidatorMock;
	private EncryptionUtils encryptionUtilsMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		Referee referee = new RefereeBuilder().id(1)
				.application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).toApplicationForm()).toReferee();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(refereeServiceMock, errors);
		controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editReferee(null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getRefereeView();
	}

	@Test
	public void shouldReturnRefereeView() {
		assertEquals("/private/pgStudents/form/components/references_details", controller.getRefereeView());
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
		binderMock.setValidator(refereeValidatorMock);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditor);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetRefereeFromServiceIfIdProvided() {
		Referee referee = new RefereeBuilder().id(1).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		Referee returnedReferee = controller.getReferee(1);
		assertEquals(referee, returnedReferee);
	}

	@Test
	public void shouldReturnNewRefereeIfIdIsNull() {
		Referee returnedReferee = controller.getReferee(null);
		assertNull(returnedReferee.getId());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfRefereeDoesNotExist() {
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(null);
		EasyMock.replay(refereeServiceMock);
		controller.getReferee(1);

	}
	
	

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));

	}

	@Test
	public void shouldSaveRefereeAndRedirectIfNoErrors() {
		Referee referee = new RefereeBuilder().id(1).application(new ApplicationFormBuilder().id(5).toApplicationForm()).toReferee();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		refereeServiceMock.save(referee);
		EasyMock.replay(refereeServiceMock, errors);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);
		assertEquals( "redirect:/update/getReferee?applicationId=5", view);
	}
	@Test
	public void shouldSaveRefereeAndSendEmailIfApplicationSubmittedAndIfNoErrors() {
		ApplicationForm application = new ApplicationFormBuilder().id(5).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(application).toReferee();
		application.setReferees(Arrays.asList(referee));
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		refereeServiceMock.processRefereesRoles(application.getReferees());
		refereeServiceMock.sendRefereeMailNotification(referee);
		EasyMock.replay(refereeServiceMock, errors);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);
		assertEquals( "redirect:/update/getReferee?applicationId=5", view);
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		Referee referee = new RefereeBuilder().id(1).application(new ApplicationFormBuilder().id(5).toApplicationForm()).toReferee();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);
	
		EasyMock.replay(refereeServiceMock, errors);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);
		assertEquals("/private/pgStudents/form/components/references_details", view);
	}
	
	
	@Before
	public void setUp() {

		refereeServiceMock = EasyMock.createMock(RefereeService.class);		
		countriesServiceMock = EasyMock.createMock(CountryService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		countryPropertyEditor = EasyMock.createMock(CountryPropertyEditor.class);
		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);

		refereeValidatorMock = EasyMock.createMock(RefereeValidator.class);

		controller = new RefereeController(refereeServiceMock, countriesServiceMock, applicationsServiceMock, countryPropertyEditor, applicationFormPropertyEditorMock, refereeValidatorMock, encryptionUtilsMock);

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
