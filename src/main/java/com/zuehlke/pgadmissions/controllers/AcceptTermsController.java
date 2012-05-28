package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/acceptTerms")
public class AcceptTermsController {

	private final ApplicationsService applicationsService;
	private static final String TERMS_AND_CONDITIONS_VIEW_NAME = "/private/pgStudents/form/components/terms_and_conditions";

	AcceptTermsController() {
		this(null);
	}

	@Autowired
	public AcceptTermsController(ApplicationsService applicationsServiceMock) {
		this.applicationsService = applicationsServiceMock;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String acceptTermsAndGetApplicationPage(@ModelAttribute ApplicationForm applicationForm) {
		applicationsService.save(applicationForm);
		return "redirect:/application?view=view&applicationId=" + applicationForm.getApplicationNumber();
	}

	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (applicationForm == null || !getCurrentUser().canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;

	}

	@RequestMapping(value = "/getTermsAndConditions", method = RequestMethod.GET)
	public String getAcceptedTermsView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return TERMS_AND_CONDITIONS_VIEW_NAME;
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

}
