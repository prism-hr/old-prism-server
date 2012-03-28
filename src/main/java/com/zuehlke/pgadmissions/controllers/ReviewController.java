package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.CannotReviewApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ReviewersListModel;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/reviewer" })
public class ReviewController {

	private static final String ADD_REVIEWER_VIEW_NAME = "private/staff/admin/assign_reviewers_page";
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final UserPropertyEditor userPropertyEditor;

	ReviewController() {
		this(null, null, null);
	}

	@Autowired
	public ReviewController(ApplicationsService applicationsService, UserService userService, UserPropertyEditor userPropertyEditor) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.userPropertyEditor = userPropertyEditor;
	}

	@RequestMapping(value = "/assign", method = RequestMethod.GET)
	public ModelAndView getReviewerPage(@ModelAttribute ApplicationForm applicationForm) {	
		if (!applicationForm.isReviewable()) {
			throw new CannotReviewApplicationException();
		}

		ReviewersListModel model = new ReviewersListModel();
		model.setApplicationForm(applicationForm);
		model.setReviewers(userService.getReviewersForApplication(applicationForm));
		model.setUser((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails());
		return new ModelAndView(ADD_REVIEWER_VIEW_NAME, "model", model);
	}

	@RequestMapping(value = { "/reviewerSuccess" }, method = RequestMethod.POST)
	public ModelAndView updateReviewers(@ModelAttribute ApplicationForm applicationForm) {
	
		if (!applicationForm.isReviewable()) {
			throw new CannotReviewApplicationException();
		}
		applicationsService.save(applicationForm);
		return new ModelAndView("redirect:/reviewer/assign", "id", applicationForm.getId());

	}

	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(Integer id) {
		RegisteredUser approver = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationsService.getApplicationById(id);
		if(applicationForm == null || !approver.canSee(applicationForm)){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}


	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}
}
