package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/acceptTerms")
public class AcceptTermsController {

	private static final String TERMS_AND_CONDITIONS_VIEW_NAME = "/private/pgStudents/form/components/terms_and_conditions";
	
	@Autowired
	private final ApplicationsService applicationsService;

	public AcceptTermsController() {
		this(null);
	}

	@Autowired
	public AcceptTermsController(ApplicationsService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String acceptTermsAndGetApplicationPage(@ModelAttribute ApplicationForm applicationForm) {
		applicationsService.save(applicationForm);
		return "redirect:/application?view=view&applicationId=" + applicationForm.getApplicationNumber();
	}

	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        return applicationsService.getSecuredApplicationForm(applicationId, ApplicationFormAction.COMPLETE_APPLICATION,
                ApplicationFormAction.CORRECT_APPLICATION);

	}

	@RequestMapping(value = "/getTermsAndConditions", method = RequestMethod.GET)
	public String getAcceptedTermsView(@ModelAttribute ApplicationForm applicationForm) {
		return TERMS_AND_CONDITIONS_VIEW_NAME;
	}

}
