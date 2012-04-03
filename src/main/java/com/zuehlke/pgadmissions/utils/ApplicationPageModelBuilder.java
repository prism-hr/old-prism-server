package com.zuehlke.pgadmissions.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.errors.ValidationErrorsUtil;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;

@Component
public class ApplicationPageModelBuilder {

	private final CountryService countryService;
	private final LanguageService languageService;
	private final ApplicationReviewService applicationReviewService;

	ApplicationPageModelBuilder() {
		this(null, null, null);
	}

	@Autowired
	public ApplicationPageModelBuilder(ApplicationReviewService applicationReviewService, CountryService countryService, LanguageService languageService) {
		this.applicationReviewService = applicationReviewService;
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

		viewApplicationModel.setUploadErrorCode(uploadErrorCode);
		viewApplicationModel.setUploadTwoErrorCode(uploadTwoErrorCode);
		viewApplicationModel.setCountries(countryService.getAllCountries());
		viewApplicationModel.setLanguages(languageService.getAllLanguages());
		viewApplicationModel.setStudyOptions(StudyOption.values());
		viewApplicationModel.setReferrers(Referrer.values());
		viewApplicationModel.setGenders(Gender.values());
		viewApplicationModel.setPhoneTypes(PhoneType.values());
		viewApplicationModel.setQualificationLevels(QualificationLevel.values());
		viewApplicationModel.setFundingTypes(FundingType.values());
		viewApplicationModel.setAddressPurposes(AddressPurpose.values());
		viewApplicationModel.setDocumentTypes(DocumentType.values());
		if (applicationForm != null && applicationForm.isCVUploaded()) {
			viewApplicationModel.getDocumentTypes().remove(DocumentType.CV);
		}
		if (applicationForm != null && applicationForm.isPersonalStatementUploaded()) {
			viewApplicationModel.getDocumentTypes().remove(DocumentType.PERSONAL_STATEMENT);
		}
		viewApplicationModel.setView(view);
		if (view != null && view.equals("errors")) {
			viewApplicationModel.setMessage("There are missing required fields on the form, please review.");
		}

		if (applicationForm != null && applicationForm.hasComments() && !applicationForm.getApplicant().equals(currentUser)) {
			if (currentUser.isInRole(Authority.SUPERADMINISTRATOR) || currentUser.isInRole(Authority.ADMINISTRATOR) || currentUser.isInRole(Authority.APPROVER)) {
				viewApplicationModel.setApplicationComments(applicationForm.getApplicationComments());
			} else if (currentUser.isInRole(Authority.REVIEWER)) {
				viewApplicationModel.setApplicationComments((applicationReviewService.getVisibleComments(applicationForm, currentUser)));
			}
		}
		
		viewApplicationModel.setFundingErrors(ValidationErrorsUtil.convertFundingErrors(fundingErrors));
		return viewApplicationModel;
	}
}
