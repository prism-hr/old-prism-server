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
import com.zuehlke.pgadmissions.pagemodels.ApplicationFormModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "application" })
public class ViewApplicationFormController {

	private static final String VIEW_APPLICATION_VIEW_NAME = "application/applicationForm";
	private ApplicationsService applicationService;

	ViewApplicationFormController() {
		this(null);
	}

	@Autowired
	public ViewApplicationFormController(ApplicationsService applicationService) {
		this.applicationService = applicationService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getViewApplicationPage(@RequestParam Integer id) {
		RegisteredUser currentuser = (RegisteredUser)SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationService.getApplicationById(id);
		if(applicationForm == null || !currentuser.canSee(applicationForm) ){
			throw new ResourceNotFoundException();
		}
		ApplicationFormModel viewApplicationModel = new ApplicationFormModel();
		viewApplicationModel.setUser(currentuser);
		viewApplicationModel.setApplicationForm(applicationForm);
		
		return new ModelAndView(VIEW_APPLICATION_VIEW_NAME, "model", viewApplicationModel);
	}

}
