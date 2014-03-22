package com.zuehlke.pgadmissions.controllers.applicantform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.services.ApplicationFormService;

@Controller
@RequestMapping("/acceptTerms")
public class AcceptTermsController {
	
	@Autowired
	private ApplicationFormService applicationFormService;
	
	@RequestMapping(value = "/getTermsAndConditions", method = RequestMethod.GET)
    public String getAcceptedTermsView(@ModelAttribute ApplicationForm applicationForm) {
        return TemplateLocation.APPLICATION_APPLICANT_TERMS_AND_CONDITIONS;
    }

	@RequestMapping(method = RequestMethod.POST)
	public String acceptTermsAndGetApplicationPage(@ModelAttribute ApplicationForm applicationForm) {
		return RedirectLocation.UPDATE_APPLICATION_ACCEPTED_TERMS + applicationForm.getApplicationNumber();
	}

	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        return applicationFormService.getSecuredApplicationForm(applicationId, ApplicationFormAction.COMPLETE_APPLICATION,
                ApplicationFormAction.CORRECT_APPLICATION);
	}

}
