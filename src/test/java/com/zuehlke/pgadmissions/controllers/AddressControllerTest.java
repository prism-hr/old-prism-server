package com.zuehlke.pgadmissions.controllers;

import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;

public class AddressControllerTest {

	private AddressController addressController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;;

	private CountryService countriesServiceMock;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private CountryPropertyEditor countryPropertyEditor;

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
		ModelAndView modelAndView = addressController.editAddress(address, 2, null, mappingResult, new ModelMap());
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
		ModelAndView modelAndView = addressController.editAddress(address, 2, "add", mappingResult, new ModelMap());
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
		ModelAndView modelAndView = addressController.editAddress(address, 2, null, mappingResult, new ModelMap());
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
		addressController.editAddress(address, 2, null, mappingResult, new ModelMap());
	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMock);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditor);

		EasyMock.replay(binderMock);
		addressController.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Before
	public void setUp() {

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		countryPropertyEditor = EasyMock.createMock(CountryPropertyEditor.class);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);

		countriesServiceMock = EasyMock.createMock(CountryService.class);

		addressController = new AddressController(applicationsServiceMock, userPropertyEditorMock, countriesServiceMock, applicationFormPropertyEditorMock, countryPropertyEditor) {

			@Override
			ApplicationForm newApplicationForm() {
				return applicationForm;
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
