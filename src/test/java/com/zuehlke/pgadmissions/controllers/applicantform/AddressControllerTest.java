package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AddressSectionDTOValidator;

public class AddressControllerTest {

	private RegisteredUser currentUser;
	private ApplicationsService applicationsServiceMock;
	private AddressSectionDTOValidator addressSectionValidatorMock;
	private AddressController controller;
	
	private CountryService countriesServiceMock;
	private CountryPropertyEditor countryPropertyEditor;
	private UserService userServiceMock;
	private ApplicationFormAccessService accessServiceMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).build();

		AddressSectionDTO addressSectionDTO = new AddressSectionDTO();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(applicationsServiceMock, errors);
		controller.editAddresses(addressSectionDTO, errors, applicationForm);
		EasyMock.verify(applicationsServiceMock, errors);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editAddresses(null, null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getAddressView();
	}

	@Test
	public void shouldReturnAddressView() {
		assertEquals("/private/pgStudents/form/components/address_details", controller.getAddressView());
	}

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);		
		
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
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
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationsServiceMock, currentUser);
		controller.getApplicationForm("1");

	}

	@Test
	public void shouldBindPropertyEditors() {
	    WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(addressSectionValidatorMock);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditor);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldPopulateDTOFromApplicationFromAddresses() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		Country countryOne = new CountryBuilder().id(1).build();
		Address addressOne = new AddressBuilder().id(1).address1("location1").address2("location1-line2").country(countryOne).build();

		Country countryTwo = new CountryBuilder().id(2).build();
		Address addressTwo = new AddressBuilder().id(2).address1("location2").address2("location2-line2").country(countryTwo).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).contactAddress(addressOne).currentAddress(addressTwo).build();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		AddressSectionDTO returnedAddress = controller.getAddressDTO("1");
		assertEquals("location1", returnedAddress.getContactAddress1());
		assertEquals("location1-line2", returnedAddress.getContactAddress2());
		assertEquals(countryOne, returnedAddress.getContactAddressCountry());

		assertEquals("location2", returnedAddress.getCurrentAddress1());
		assertEquals("location2-line2", returnedAddress.getCurrentAddress2());
		assertEquals(countryTwo, returnedAddress.getCurrentAddressCountry());
		assertFalse(returnedAddress.isSameAddress());
	}

	@Test
	public void shouldGetDTOWithSameFlagSetIfAddressesIdentical() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		Country countryOne = new CountryBuilder().id(1).build();
		Address addressOne = new AddressBuilder().id(1).address1("location1").country(countryOne).build();

		Address addressTwo = new AddressBuilder().id(2).address1("location1").country(countryOne).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).contactAddress(addressOne).currentAddress(addressTwo).build();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);

		AddressSectionDTO returnedAddress = controller.getAddressDTO("1");

		assertEquals("location1", returnedAddress.getContactAddress1());
		assertEquals(countryOne, returnedAddress.getContactAddressCountry());

		assertEquals("location1", returnedAddress.getCurrentAddress1());
		assertEquals(countryOne, returnedAddress.getCurrentAddressCountry());
		assertTrue(returnedAddress.isSameAddress());
	}

	@Test
	public void shouldPopulateDTOWithNoDataIfApplicationFormAddressesNull() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);

		AddressSectionDTO returnedAddress = controller.getAddressDTO("1");
		assertEquals(applicationForm, returnedAddress.getApplication());
		assertNull(returnedAddress.getContactAddress1());
		assertNull(returnedAddress.getContactAddressCountry());

		assertNull(returnedAddress.getCurrentAddress1());
		assertNull(returnedAddress.getCurrentAddressCountry());
		assertFalse(returnedAddress.isSameAddress());
	}

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));

	}

	@Test
	public void shouldCreateAndSetNewAddressesAndSaveIfNoErrors() {
		Country countryOne = new CountryBuilder().id(1).build();
		Country countryTwo = new CountryBuilder().id(2).build();

		AddressSectionDTO addressSectionDTO = new AddressSectionDTO();
		addressSectionDTO.setContactAddressCountry(countryOne);
		addressSectionDTO.setContactAddress1("location1");

		addressSectionDTO.setCurrentAddressCountry(countryTwo);
		addressSectionDTO.setCurrentAddress1("location2");

		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();

		applicationsServiceMock.save(applicationForm);

		EasyMock.replay(applicationsServiceMock, errors);

		String view = controller.editAddresses(addressSectionDTO, errors, applicationForm);

		EasyMock.verify(applicationsServiceMock);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));

		assertEquals("location1", applicationForm.getContactAddress().getAddress1());
		assertEquals(countryOne, applicationForm.getContactAddress().getCountry());

		assertEquals("location2", applicationForm.getCurrentAddress().getAddress1());
		assertEquals(countryTwo, applicationForm.getCurrentAddress().getCountry());

		assertEquals("redirect:/update/getAddress?applicationId=ABC", view);
	}

	@Test
	public void shouldUpdateExistingAddressesAndSaveIfNoErrors() {
		Country countryOne = new CountryBuilder().id(1).build();
		Country countryTwo = new CountryBuilder().id(2).build();
		Country countryThree = new CountryBuilder().id(3).build();
		Country countryFour = new CountryBuilder().id(4).build();

		Address addressOne = new AddressBuilder().id(1).address1("location3").country(countryThree).build();

		Address addressTwo = new AddressBuilder().id(2).address1("location4").country(countryFour).build();

		AddressSectionDTO addressSectionDTO = new AddressSectionDTO();
		addressSectionDTO.setContactAddressCountry(countryOne);
		addressSectionDTO.setContactAddress1("location1");

		addressSectionDTO.setCurrentAddressCountry(countryTwo);
		addressSectionDTO.setCurrentAddress1("location2");

		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").currentAddress(addressOne).contactAddress(addressTwo)
				.build();

		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock, errors);

		String view = controller.editAddresses(addressSectionDTO, errors, applicationForm);

		EasyMock.verify(applicationsServiceMock);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));

		assertEquals("location1", applicationForm.getContactAddress().getAddress1());
		assertEquals(countryOne, applicationForm.getContactAddress().getCountry());
		assertSame(addressTwo, applicationForm.getContactAddress());

		assertEquals("location2", applicationForm.getCurrentAddress().getAddress1());
		assertEquals(countryTwo, applicationForm.getCurrentAddress().getCountry());
		assertSame(addressOne, applicationForm.getCurrentAddress());

		assertEquals("redirect:/update/getAddress?applicationId=ABC", view);
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		AddressSectionDTO addressSectionDTO = new AddressSectionDTO();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);
		EasyMock.replay(applicationsServiceMock, errors);
		String view = controller.editAddresses(addressSectionDTO, errors, new ApplicationFormBuilder().build());
		EasyMock.verify(applicationsServiceMock);
		assertEquals("/private/pgStudents/form/components/address_details", view);
	}

	@Test
	public void shouldReturnAllEnabledCountries() {
		List<Country> countryList = Arrays.asList(new CountryBuilder().id(1).enabled(true).build(), new CountryBuilder().id(2).enabled(false).build());
		EasyMock.expect(countriesServiceMock.getAllEnabledCountries()).andReturn(Collections.singletonList(countryList.get(0)));
		EasyMock.replay(countriesServiceMock);
		List<Country> allCountries = controller.getAllEnabledCountries();
		assertEquals(1, allCountries.size());
		assertEquals(countryList.get(0), allCountries.get(0));
	}

	@Before
	public void setUp() {
		countryPropertyEditor = EasyMock.createMock(CountryPropertyEditor.class);
		countriesServiceMock = EasyMock.createMock(CountryService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);

		addressSectionValidatorMock = EasyMock.createMock(AddressSectionDTOValidator.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
		controller = new AddressController(applicationsServiceMock, userServiceMock, countriesServiceMock,
		        countryPropertyEditor, addressSectionValidatorMock, accessServiceMock);

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);

	}

}
