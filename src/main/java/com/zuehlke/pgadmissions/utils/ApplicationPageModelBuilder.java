package com.zuehlke.pgadmissions.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;
import com.zuehlke.pgadmissions.errors.ValidationErrorsUtil;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;

@Component
public class ApplicationPageModelBuilder {

	private final CountryService countryService;
	private final LanguageService languageService;
	private final CommentService commentService;

	ApplicationPageModelBuilder() {
		this(null, null, null);
	}

	@Autowired
	public ApplicationPageModelBuilder(CommentService commentService, CountryService countryService, LanguageService languageService) {
		this.commentService = commentService;
		this.countryService = countryService;
		this.languageService = languageService;
	}

	public ApplicationPageModel createAndPopulatePageModel(ApplicationForm applicationForm, String uploadErrorCode, String view, String uploadTwoErrorCode,
			String fundingErrors) {
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
		viewApplicationModel.setCountries(countryService.getAllCountries());
		viewApplicationModel.setLanguages(languageService.getAllLanguages());
		viewApplicationModel.setStudyOptions(StudyOption.values());
		viewApplicationModel.setReferrers(Referrer.values());
		viewApplicationModel.setGenders(Gender.values());
		viewApplicationModel.setPhoneTypes(PhoneType.values());
		viewApplicationModel.setFundingTypes(FundingType.values());
		viewApplicationModel.setDocumentTypes(DocumentType.values());

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
			address.setCurrentAddressCountry(currentAddress.getCountry());

			address.setCurrentAddressLocation(currentAddress.getLocation());
		}
		com.zuehlke.pgadmissions.domain.Address contactAddress = applicationForm.getContactAddress();
		if(contactAddress != null){
		address.setContactAddressCountry(contactAddress.getCountry());

		address.setContactAddressLocation(contactAddress.getLocation());
		}
		if (currentAddress != null && contactAddress != null && currentAddress.getLocation().equals(contactAddress.getLocation()) && currentAddress.getCountry().equals(contactAddress.getCountry())) {
			address.setSameAddress(true);
		}

		return address;
	}
}
