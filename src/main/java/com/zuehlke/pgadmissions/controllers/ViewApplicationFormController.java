package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.zuehlke.pgadmissions.utils.DTOUtils;

@Controller
@RequestMapping(value = { "application" })
public class ViewApplicationFormController {

	private static final String VIEW_APPLICATION_INTERNAL_VIEW_NAME = "private/staff/application/main_application_page";
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/main_application_page";
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
		viewApplicationModel.setPersonalDetails(DTOUtils.createPersonalDetails(applicationForm));
		viewApplicationModel.setAddress(DTOUtils.createAddress(applicationForm));
		viewApplicationModel.setFunding(DTOUtils.createFunding(applicationForm));
		
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

	/*private List<Qualification> createOrGetExistingQualifications(ApplicationForm applicationForm, RegisteredUser currentuser) {
		if(!applicationForm.hasQualifications()){
			Qualification qualification = new Qualification();
			qualification.setDegree("");
			qualification.setGrade("");
			qualification.setInstitution("");
			qualification.setDate_taken("");
			qualification.setApplicant(currentuser);
			qualification.setApplication(applicationForm);
			applicationService.save(applicationForm);
			return Arrays.asList(qualification);
		}*/
	
//		return applicationForm.getQualifications();
		
//		else{
//			List<Qualification> existingQualifications = applicationForm.getQualifications();
//			for (Qualification existingQualification : existingQualifications) {
//				if(!existingQualification.isAttachedToApplication(applicationForm, existingQualification)){
//					Qualification newQualification = attachQualificationToApplication(applicationForm, currentuser, existingQualification);
//					qualifications.add(newQualification);
//				}
//			}
//		}
		
//	}

//	private Qualification attachQualificationToApplication(ApplicationForm applicationForm, RegisteredUser currentuser, Qualification existingQualification) {
//				Qualification newQualification = new Qualification();
//				newQualification.setDate_taken(existingQualification.getDate_taken());
//				newQualification.setDegree(existingQualification.getDegree());
//				newQualification.setGrade(existingQualification.getDegree());
//				newQualification.setInstitution(existingQualification.getInstitution());
//				newQualification.setApplicant(currentuser);
//				newQualification.setApplication(applicationForm);
//				applicationService.save(applicationForm);
////				currentuser.getQualifications().add(newQualification);
////				applicationReviewService.saveUser(currentuser);
//				return newQualification;
//	}

}

