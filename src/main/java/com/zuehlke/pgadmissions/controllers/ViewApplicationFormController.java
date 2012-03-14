package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.EmploymentPosition;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DocumentService;
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
		ApplicationPageModel viewApplicationModel = new ApplicationPageModel();

		viewApplicationModel.setApplicationForm(applicationForm);
		viewApplicationModel.setAddress(new Address());
		viewApplicationModel.setFunding(new Funding());
		viewApplicationModel.setQualification(new QualificationDTO());
		viewApplicationModel.setReferee(new Referee());
		viewApplicationModel.setEmploymentPosition(new EmploymentPosition());
		viewApplicationModel.setCountries(countryService.getAllCountries());
		viewApplicationModel.setLanguages(languageService.getAllLanguages());
		viewApplicationModel.setResidenceStatuses(ResidenceStatus.values());
		viewApplicationModel.setStudyOptions(StudyOption.values());
		viewApplicationModel.setReferrers(Referrer.values());
		viewApplicationModel.setPhoneTypes(PhoneType.values());

		viewApplicationModel.setLanguageAptitudes(LanguageAptitude.values());
		viewApplicationModel.setGenders(Gender.values());
		viewApplicationModel.setDocumentTypes(DocumentType.values());
		if(applicationForm.isCVUploaded()){
			viewApplicationModel.getDocumentTypes().remove(DocumentType.CV);
		}
		if(applicationForm.isPersonalStatementUploaded()){
			viewApplicationModel.getDocumentTypes().remove(DocumentType.PERSONAL_STATEMENT);
		}
		if (view != null && view.equals("errors")) {
			viewApplicationModel.setMessage("There are missing required fields on the form, please review.");
		}
		viewApplicationModel.setUploadErrorCode(uploadErrorCode);
		viewApplicationModel.setUser(currentuser);
		if (applicationForm.hasComments()) {
			if (currentuser.isInRole(Authority.ADMINISTRATOR) || currentuser.isInRole(Authority.APPROVER)) {
				viewApplicationModel.setApplicationComments(applicationReviewService.getApplicationReviewsByApplication(applicationForm));
			} else if (currentuser.isInRole(Authority.REVIEWER)) {
				viewApplicationModel.setApplicationComments((applicationReviewService.getVisibleComments(applicationForm, currentuser)));
			}
		}

		if (currentuser.isInRole(Authority.APPLICANT)) {
			return new ModelAndView(VIEW_APPLICATION_APPLICANT_VIEW_NAME, "model", viewApplicationModel);
		}
		if (view != null)
			viewApplicationModel.setView(view);

		return new ModelAndView(VIEW_APPLICATION_INTERNAL_VIEW_NAME, "model", viewApplicationModel);
	}

}
