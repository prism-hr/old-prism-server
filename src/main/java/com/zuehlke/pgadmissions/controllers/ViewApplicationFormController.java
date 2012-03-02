package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "application" })
public class ViewApplicationFormController {

	private static final String VIEW_APPLICATION_INTERNAL_VIEW_NAME = "application/applicationForm_internal";
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "application/applicationForm_applicant";
	private ApplicationsService applicationService;
	private ApplicationReviewService applicationReviewService;

	ViewApplicationFormController() {
		this(null, null);
	}

	@Autowired
	public ViewApplicationFormController(
			ApplicationsService applicationService,
			ApplicationReviewService applicationReviewService) {
		this.applicationService = applicationService;
		this.applicationReviewService = applicationReviewService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getViewApplicationPage(@RequestParam Integer id, @RequestParam(required=false) String view) {
		RegisteredUser currentuser = (RegisteredUser) SecurityContextHolder
				.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationService
				.getApplicationById(id);
		if (applicationForm == null || !currentuser.canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		ApplicationPageModel viewApplicationModel = new ApplicationPageModel();
		
		viewApplicationModel.setApplicationForm(applicationForm);
		viewApplicationModel.setPersonalDetails(createPersonalDetails(applicationForm));
		viewApplicationModel.setAddress(createAddress(applicationForm));
		viewApplicationModel.setFunding(createFunding(applicationForm));
		if(!currentuser.hasQualifications()){
			Qualification qualification = new Qualification();
			qualification.setDegree("");
			qualification.setGrade("");
			qualification.setInstitution("");
			qualification.setDate_taken("");
			qualification.setApplicant(currentuser);
			qualification.setApplication(applicationForm);
			applicationReviewService.saveQualification(qualification);
			currentuser.getQualifications().add(qualification);
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

	private Funding createFunding(ApplicationForm applicationForm) {
		Funding funding = new Funding();
		funding.setFunding(applicationForm.getFunding());
		return funding;
	}

	private Address createAddress(ApplicationForm applicationForm) {
		Address address = new Address();
		if (applicationForm.getApplicant() != null) {
			address.setAddress(applicationForm.getApplicant().getAddress());
		}
		return address;
	}

	private PersonalDetails createPersonalDetails(ApplicationForm applicationForm) {
		PersonalDetails personalDetails = new PersonalDetails();
		if(applicationForm.getApplicant() != null){
			personalDetails.setFirstName(applicationForm.getApplicant().getFirstName());
			personalDetails.setLastName(applicationForm.getApplicant().getLastName());
			personalDetails.setEmail(applicationForm.getApplicant().getEmail());
		}
		return personalDetails;
	}
	
	
	

	

}
