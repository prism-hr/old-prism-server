package com.zuehlke.pgadmissions.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.validators.AddressValidator;

@Controller
@RequestMapping("/editAddress")
public class AddressController {

	private static final String APPLICATION_ADDRESS_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/address_details";

	private final ApplicationsService applicationService;
	private final UserPropertyEditor userPropertyEditor;
	private final CountryService countryService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final CountryPropertyEditor countryPropertyEditor;

	AddressController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public AddressController(ApplicationsService applicationService, UserPropertyEditor userPropertyEditor,
			CountryService countryService, ApplicationFormPropertyEditor applicationFormPropertyEditor,
			CountryPropertyEditor countryPropertyEditor) {

		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.countryService = countryService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.countryPropertyEditor = countryPropertyEditor;
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);

	}

	@RequestMapping(value = "/editAddress", method = RequestMethod.POST)
	public ModelAndView editAddress(@ModelAttribute Address addr, @RequestParam Integer appId, @RequestParam(required = false) String add,
			BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);
		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		AddressValidator addressValidator = new AddressValidator();
		addressValidator.validate(addr, result);
		ApplicationPageModel model = new ApplicationPageModel();
		ApplicationForm applicationForm = application;
		model.setUser(getCurrentUser());
		model.setApplicationForm(applicationForm);
		model.setResult(result);
		model.setCountries(countryService.getAllCountries());

		if (!result.hasErrors()) {
			//update current address
			com.zuehlke.pgadmissions.domain.Address currentAddress;
			if (addr.getCurrentAddressId() == null) {
				currentAddress = new com.zuehlke.pgadmissions.domain.Address();
			} else {
				currentAddress = applicationService.getAddressById(addr.getCurrentAddressId());
			}

			currentAddress.setApplication(application);
			currentAddress.setLocation(addr.getCurrentAddressLocation());
			currentAddress.setCountry(countryService.getCountryById(addr.getCurrentAddressCountry()));

			if (addr.getCurrentAddressId() == null) {
				application.getAddresses().add(currentAddress);
			}

			//update contact address
			com.zuehlke.pgadmissions.domain.Address contactAddress;
			if (addr.getCurrentAddressId() == null) {
				contactAddress = new com.zuehlke.pgadmissions.domain.Address();
			} else {
				contactAddress = applicationService.getAddressById(addr.getContactAddressId());
			}

			contactAddress.setApplication(application);
			contactAddress.setLocation(addr.getContactAddressLocation());
			contactAddress.setCountry(countryService.getCountryById(addr.getContactAddressCountry()));

			if (addr.getContactAddressId() == null) {
				application.getAddresses().add(contactAddress);
			}

			applicationService.save(application);
		}
		if (application.getAddresses().size() > 0) {
			addr.setCurrentAddressId(application.getAddresses().get(0).getId());
			addr.setContactAddressId(application.getAddresses().get(1).getId());
		}
		model.setAddress(addr);

		modelMap.put("model", model);

		if (StringUtils.isNotBlank(add)) {
			modelMap.put("add", "add");
		}
		return new ModelAndView(APPLICATION_ADDRESS_APPLICANT_VIEW_NAME, modelMap);
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
}
