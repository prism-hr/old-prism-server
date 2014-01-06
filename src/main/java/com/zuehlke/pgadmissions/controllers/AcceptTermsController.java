package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/acceptTerms")
public class AcceptTermsController {

	private final ApplicationsService applicationsService;
	private static final String TERMS_AND_CONDITIONS_VIEW_NAME = "/private/pgStudents/form/components/terms_and_conditions";
	private final UserService userService;
	private final ContentAccessProvider contentAccessProvider;

	public AcceptTermsController() {
		this(null, null, null);
	}

	@Autowired
	public AcceptTermsController(ApplicationsService applicationsService, UserService userService, ContentAccessProvider contentAccessProvider) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.contentAccessProvider = contentAccessProvider;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String acceptTermsAndGetApplicationPage(@ModelAttribute ApplicationForm applicationForm) {
		applicationsService.save(applicationForm);
		return "redirect:/application?view=view&applicationId=" + applicationForm.getApplicationNumber();
	}

	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		contentAccessProvider.validateCanEditAsApplicant(application, userService.getCurrentUser());
		return application;

	}

	@RequestMapping(value = "/getTermsAndConditions", method = RequestMethod.GET)
	public String getAcceptedTermsView(@ModelAttribute ApplicationForm applicationForm) {
		return TERMS_AND_CONDITIONS_VIEW_NAME;
	}

}