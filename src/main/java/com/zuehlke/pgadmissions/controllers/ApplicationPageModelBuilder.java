package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;
import com.zuehlke.pgadmissions.errors.ValidationErrorsUtil;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.SourcesOfInterestService;
import com.zuehlke.pgadmissions.utils.AddressUtils;

@Component
public class ApplicationPageModelBuilder {

	private final CountryService countryService;
	private final LanguageService languageService;
	private final SourcesOfInterestService sourcesOfInterestService;

	ApplicationPageModelBuilder() {
		this(null, null, null);
	}

	@Autowired
    public ApplicationPageModelBuilder(CountryService countryService, LanguageService languageService,
            SourcesOfInterestService sourcesOfInterestService) {
		this.countryService = countryService;
		this.languageService = languageService;
		this.sourcesOfInterestService = sourcesOfInterestService;
	}

	public ApplicationPageModel createAndPopulatePageModel(ApplicationForm applicationForm, 
	        String uploadErrorCode, String view, String uploadTwoErrorCode, String fundingErrors) {
		RegisteredUser currentUser = null;
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof RegisteredUser) {
			currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		}

		ApplicationPageModel viewApplicationModel = new ApplicationPageModel();
		if (currentUser != null) {
			viewApplicationModel.setUser(currentUser);
		}
		viewApplicationModel.setApplicationForm(applicationForm);
		if (applicationForm != null) {
			viewApplicationModel.setAddress(buildAddress(applicationForm));
		}
		viewApplicationModel.setUploadErrorCode(uploadErrorCode);
		viewApplicationModel.setUploadTwoErrorCode(uploadTwoErrorCode);
		viewApplicationModel.setCountries(countryService.getAllEnabledCountries());
		viewApplicationModel.setLanguages(languageService.getAllEnabledLanguages());
		viewApplicationModel.setSourcesOfInterests(sourcesOfInterestService.getAllEnabledSourcesOfInterest());
		viewApplicationModel.setGenders(Gender.values());
		viewApplicationModel.setPhoneTypes(PhoneType.values());
		viewApplicationModel.setFundingTypes(FundingType.values());
		viewApplicationModel.setDocumentTypes(DocumentType.values());
		viewApplicationModel.setTitles(Title.values());

		viewApplicationModel.setView(view);
		if (view != null && view.equals("errors")) {
			viewApplicationModel.setMessage("There are missing required fields on the form, please review.");
		}

		viewApplicationModel.setFundingErrors(ValidationErrorsUtil.convertFundingErrors(fundingErrors));
		return viewApplicationModel;
	}

	private AddressSectionDTO buildAddress(ApplicationForm applicationForm) {
		AddressSectionDTO address = new AddressSectionDTO();

		com.zuehlke.pgadmissions.domain.Address currentAddress = applicationForm.getCurrentAddress();
		if (currentAddress != null) {
			address.setCurrentAddressDomicile(currentAddress.getDomicile());

			address.setCurrentAddress1(currentAddress.getAddress1());
			address.setCurrentAddress2(currentAddress.getAddress2());
			address.setCurrentAddress3(currentAddress.getAddress3());
			address.setCurrentAddress4(currentAddress.getAddress4());
			address.setCurrentAddress5(currentAddress.getAddress5());
		}
		com.zuehlke.pgadmissions.domain.Address contactAddress = applicationForm.getContactAddress();
		if(contactAddress != null){
		address.setContactAddressDomicile(contactAddress.getDomicile());

		address.setContactAddress1(contactAddress.getAddress1());
		address.setContactAddress2(contactAddress.getAddress2());
		address.setContactAddress3(contactAddress.getAddress3());
		address.setContactAddress4(contactAddress.getAddress4());
		address.setContactAddress5(contactAddress.getAddress5());
		}
		if (AddressUtils.addressesEqual(contactAddress, currentAddress)) {
			address.setSameAddress(true);
		}

		return address;
	}
}
