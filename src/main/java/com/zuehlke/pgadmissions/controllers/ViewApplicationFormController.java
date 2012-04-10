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
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.utils.ApplicationPageModelBuilder;

@Controller
@RequestMapping(value = { "application" })
public class ViewApplicationFormController {

	private static final String VIEW_APPLICATION_INTERNAL_VIEW_NAME = "private/staff/application/main_application_page";
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/main_application_page";
	
	private final ApplicationsService applicationService;
	private final ApplicationPageModelBuilder applicationPageModelBuilder;

	ViewApplicationFormController() {
		this(null, null);
	}

	@Autowired
	public ViewApplicationFormController(ApplicationsService applicationService, ApplicationPageModelBuilder applicationPageModelBuilder) {
		this.applicationService = applicationService;
		this.applicationPageModelBuilder = applicationPageModelBuilder;

	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getViewApplicationPage(@RequestParam(required = false) String view, @RequestParam Integer id,
			@RequestParam(required = false) String uploadErrorCode, @RequestParam(required = false) String uploadTwoErrorCode, 
			@RequestParam(required = false) String fundingErrors) {
		RegisteredUser currentuser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationService.getApplicationById(id);
		if (applicationForm == null || !currentuser.canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}

		if (applicationForm.getApplicant().equals(currentuser)) {
			return new ModelAndView(VIEW_APPLICATION_APPLICANT_VIEW_NAME, "model", applicationPageModelBuilder.createAndPopulatePageModel(applicationForm,
					uploadErrorCode, view, uploadTwoErrorCode, fundingErrors));
		}
		return new ModelAndView(VIEW_APPLICATION_INTERNAL_VIEW_NAME, "model", applicationPageModelBuilder.createAndPopulatePageModel(applicationForm,
				uploadErrorCode, view, uploadTwoErrorCode, fundingErrors));

	}

}
