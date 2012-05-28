package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;
import java.util.List;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.validators.AddressSectionDTOValidator;
@RequestMapping("/update")
@Controller
public class AddressController {

	private static final String APPLICATION_ADDRESS_VIEW = "/private/pgStudents/form/components/address_details";
	private final ApplicationsService applicationService;	
	private final AddressSectionDTOValidator addressSectionDTOValidator;
	private final CountryService countryService;
	private final CountryPropertyEditor countryPropertyEditor;

	AddressController() {
		this(null, null, null, null);
	}

	@Autowired
	public AddressController(ApplicationsService applicationService, CountryService countryService, CountryPropertyEditor countryPropertyEditor, AddressSectionDTOValidator addressSectionDTOValidator) {
		this.applicationService = applicationService;
		this.countryService = countryService;
		this.countryPropertyEditor = countryPropertyEditor;
		this.addressSectionDTOValidator = addressSectionDTOValidator;
	}

	@RequestMapping(value = "/editAddress", method = RequestMethod.POST)
	public String editAddresses(@Valid AddressSectionDTO addressSectionDTO, BindingResult result, @ModelAttribute ApplicationForm applicationForm) {
		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if (applicationForm.isDecided()) {
			throw new CannotUpdateApplicationException();
		}
		if(result.hasErrors()){
			return APPLICATION_ADDRESS_VIEW;
		}
		Address contactAddress = applicationForm.getContactAddress();
		if(contactAddress == null){
			contactAddress = new Address();
			applicationForm.setContactAddress(contactAddress);
		}
		contactAddress.setCountry(addressSectionDTO.getContactAddressCountry());
		contactAddress.setLocation(addressSectionDTO.getContactAddressLocation());
		
		
		Address currentAddress = applicationForm.getCurrentAddress();
		if(currentAddress == null){
			currentAddress = new Address();
			applicationForm.setCurrentAddress(currentAddress);
		}
		currentAddress.setCountry(addressSectionDTO.getCurrentAddressCountry());
		currentAddress.setLocation(addressSectionDTO.getCurrentAddressLocation());
	
		applicationForm.setLastUpdated(new Date());
		applicationService.save(applicationForm);
		
		return "redirect:/update/getAddress?applicationId=" + applicationForm.getApplicationNumber();
	}

	@RequestMapping(value = "/getAddress", method = RequestMethod.GET)
	public String getAddressView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return APPLICATION_ADDRESS_VIEW;
	}

	@InitBinder(value = "addressSectionDTO")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(addressSectionDTOValidator);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
		if (application == null || !getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required = false) String message) {
		return message;
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	@ModelAttribute("addressSectionDTO")
	public AddressSectionDTO getAddressDTO(String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		AddressSectionDTO sectionDTO = new AddressSectionDTO();
		Address contactAddress = applicationForm.getContactAddress();
		if (contactAddress != null) {
			sectionDTO.setContactAddressCountry(contactAddress.getCountry());
			sectionDTO.setContactAddressLocation(contactAddress.getLocation());
		}

		Address currentAddress = applicationForm.getCurrentAddress();
		if (currentAddress != null) {
			sectionDTO.setCurrentAddressCountry(currentAddress.getCountry());
			sectionDTO.setCurrentAddressLocation(currentAddress.getLocation());
		}
		if (contactAddress != null && currentAddress != null && contactAddress.getCountry().equals(currentAddress.getCountry())
				&& contactAddress.getLocation().equals(currentAddress.getLocation())) {
			sectionDTO.setSameAddress(true);
		}
		return sectionDTO;
	}
	
	
	@ModelAttribute("countries")
	public List<Country> getAllCountries() {
		return countryService.getAllCountries();
	}

}
