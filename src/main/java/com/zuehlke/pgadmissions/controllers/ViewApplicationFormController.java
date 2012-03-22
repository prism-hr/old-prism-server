package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;

@Controller
@RequestMapping(value = { "application" })
public class ViewApplicationFormController {

	private static final String VIEW_APPLICATION_INTERNAL_VIEW_NAME = "private/staff/application/main_application_page";
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/main_application_page";
	private ApplicationsService applicationService;
	private ApplicationReviewService applicationReviewService;
	private final CountryService countryService;
	private final LanguageService languageService;

	ViewApplicationFormController() {
		this(null, null, null, null);
	}

	@Autowired
	public ViewApplicationFormController(ApplicationsService applicationService, ApplicationReviewService applicationReviewService,
			CountryService countryService, LanguageService languageService) {
		this.applicationService = applicationService;
		this.applicationReviewService = applicationReviewService;
		this.countryService = countryService;
		this.languageService = languageService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getViewApplicationPage(@RequestParam(required = false) String view, @RequestParam Integer id, @RequestParam(required = false) String uploadErrorCode) {
		RegisteredUser currentuser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationService.getApplicationById(id);
		if (applicationForm == null || !currentuser.canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}
			
		if(applicationForm.getApplicant().equals(currentuser)){
			return new ModelAndView(VIEW_APPLICATION_APPLICANT_VIEW_NAME, "model", createAndPopulatePageModel(applicationForm, uploadErrorCode, view));	
		}
		return new ModelAndView(VIEW_APPLICATION_INTERNAL_VIEW_NAME, "model", createAndPopulatePageModel(applicationForm, uploadErrorCode, view));
	

	}

	ApplicationPageModel createAndPopulatePageModel(ApplicationForm applicationForm, String uploadErrorCode, String view) {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		ApplicationPageModel viewApplicationModel = new ApplicationPageModel();
		viewApplicationModel.setApplicationForm(applicationForm);		
		viewApplicationModel.setUser(currentUser);
		viewApplicationModel.setUploadErrorCode(uploadErrorCode);
		viewApplicationModel.setCountries(countryService.getAllCountries());
		viewApplicationModel.setLanguages(languageService.getAllLanguages());
		viewApplicationModel.setResidenceStatuses(ResidenceStatus.values());
		viewApplicationModel.setStudyOptions(StudyOption.values());
		viewApplicationModel.setReferrers(Referrer.values());
		viewApplicationModel.setGenders(Gender.values());
		viewApplicationModel.setPhoneTypes(PhoneType.values());
		viewApplicationModel.setLanguageAptitudes(LanguageAptitude.values());
		viewApplicationModel.setQualificationLevels(QualificationLevel.values());
		viewApplicationModel.setFundingTypes(FundingType.values());
		viewApplicationModel.setAddressPurposes(AddressPurpose.values());
		viewApplicationModel.setDocumentTypes(DocumentType.values());		
		if(applicationForm != null && applicationForm.isCVUploaded()){
			viewApplicationModel.getDocumentTypes().remove(DocumentType.CV);
		}
		if(applicationForm != null && applicationForm.isPersonalStatementUploaded()){
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
		return viewApplicationModel;
	}

}
