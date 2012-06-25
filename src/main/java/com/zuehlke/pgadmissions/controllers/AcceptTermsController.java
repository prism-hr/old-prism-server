package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/acceptTerms")
public class AcceptTermsController {

	private final ApplicationsService applicationsService;
	private static final String TERMS_AND_CONDITIONS_VIEW_NAME = "/private/pgStudents/form/components/terms_and_conditions";
	private final UserService userService;

	AcceptTermsController() {
		this(null, null);
	}

	@Autowired
	public AcceptTermsController(ApplicationsService applicationsServiceMock, UserService userService) {
		this.applicationsService = applicationsServiceMock;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String acceptTermsAndGetApplicationPage(@ModelAttribute ApplicationForm applicationForm) {
		applicationsService.save(applicationForm);
		return "redirect:/application?view=view&applicationId=" + applicationForm.getApplicationNumber();
	}

	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (applicationForm == null || !userService.getCurrentUser().canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;

	}

	@RequestMapping(value = "/getTermsAndConditions", method = RequestMethod.GET)
	public String getAcceptedTermsView() {

		if (!userService.getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return TERMS_AND_CONDITIONS_VIEW_NAME;
	}

}
