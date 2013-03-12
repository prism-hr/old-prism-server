package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.MissingApplicationFormException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "applicationinternal" })
public class ViewApplicationFormController {

	private static final String VIEW_APPLICATION_INTERNAL_VIEW_NAME = "private/staff/application/main_application_page";
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/main_application_page";
	
	private final ApplicationsService applicationService;
	private final ApplicationPageModelBuilder applicationPageModelBuilder;
	private final UserService userService;

	ViewApplicationFormController() {
		this(null, null, null);
	}

	@Autowired
	public ViewApplicationFormController(ApplicationsService applicationService, UserService userService, ApplicationPageModelBuilder applicationPageModelBuilder) {
		this.applicationService = applicationService;
		this.userService = userService;
		this.applicationPageModelBuilder = applicationPageModelBuilder;

	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getViewApplicationPage(@RequestParam(required = false) String view, @RequestParam String id,
			@RequestParam(required = false) String uploadErrorCode, @RequestParam(required = false) String uploadTwoErrorCode, 
			@RequestParam(required = false) String fundingErrors) {
		RegisteredUser currentuser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationService.getApplicationByApplicationNumber(id);
		if(applicationForm == null){
			throw new MissingApplicationFormException(id);
		}
		if (!currentuser.canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}

		if (applicationForm.getApplicant() != null && applicationForm.getApplicant().getId().equals(currentuser.getId()) && applicationForm.isModifiable()) {
			return new ModelAndView(VIEW_APPLICATION_APPLICANT_VIEW_NAME, "model", applicationPageModelBuilder.createAndPopulatePageModel(applicationForm,
					uploadErrorCode, view, uploadTwoErrorCode, fundingErrors));
		}
		
		return new ModelAndView(VIEW_APPLICATION_INTERNAL_VIEW_NAME, "model", applicationPageModelBuilder.createAndPopulatePageModel(applicationForm,
				uploadErrorCode, view, uploadTwoErrorCode, fundingErrors));
	}
}
