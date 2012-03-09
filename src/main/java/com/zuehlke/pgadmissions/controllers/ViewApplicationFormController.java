package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.EmploymentPosition;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.dto.Referee;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.utils.DTOUtils;

@Controller
@RequestMapping(value = { "application" })
public class ViewApplicationFormController {

	private static final String VIEW_APPLICATION_INTERNAL_VIEW_NAME = "private/staff/application/main_application_page";
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/main_application_page";
	private ApplicationsService applicationService;
	private ApplicationReviewService applicationReviewService;
	private final CountriesDAO countriesDAO;

	ViewApplicationFormController() {
		this(null, null, null);
	}

	@Autowired
	public ViewApplicationFormController(
			ApplicationsService applicationService,
			ApplicationReviewService applicationReviewService, CountriesDAO countriesDAO) {
		this.applicationService = applicationService;
		this.applicationReviewService = applicationReviewService;
		this.countriesDAO = countriesDAO;
		
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getViewApplicationPage(@RequestParam(required=false) String view, @RequestParam Integer id) {
		RegisteredUser currentuser = (RegisteredUser) SecurityContextHolder
		.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationService
		.getApplicationById(id);
		if (applicationForm == null || !currentuser.canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		ApplicationPageModel viewApplicationModel = new ApplicationPageModel();

		viewApplicationModel.setApplicationForm(applicationForm);
		viewApplicationModel.setPersonalDetails(DTOUtils.createPersonalDetails(new PersonalDetail()));
		viewApplicationModel.setAddress(new Address());
		viewApplicationModel.setFunding(new Funding());
		viewApplicationModel.setQualification(new QualificationDTO());
		viewApplicationModel.setEmploymentPosition(new EmploymentPosition());
		viewApplicationModel.setReferee(new Referee());
		viewApplicationModel.setCountries(countriesDAO.getAllCountries());
		viewApplicationModel.setResidenceStatuses(ResidenceStatus.values());
		if (view != null && view.equals("errors")) {
			viewApplicationModel.setMessage("There are missing required fields on the form, please review.");
		}
		
		viewApplicationModel.setUser(currentuser);
		if (applicationForm.hasComments()) {
			if (currentuser.isInRole(Authority.ADMINISTRATOR)|| currentuser.isInRole(Authority.APPROVER)) {
				viewApplicationModel.setApplicationComments(applicationReviewService.getApplicationReviewsByApplication(applicationForm));
			} else if (currentuser.isInRole(Authority.REVIEWER)) {
				viewApplicationModel.setApplicationComments((applicationReviewService.getVisibleComments(applicationForm,currentuser)));
			}
		}

		if (currentuser.isInRole(Authority.APPLICANT)) {
			return new ModelAndView(VIEW_APPLICATION_APPLICANT_VIEW_NAME,
					"model", viewApplicationModel);
		}
		if(view!=null) viewApplicationModel.setView(view);

		return new ModelAndView(VIEW_APPLICATION_INTERNAL_VIEW_NAME, "model",
				viewApplicationModel);
	}

}

