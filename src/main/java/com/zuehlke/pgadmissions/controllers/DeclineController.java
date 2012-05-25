package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/decline" })
public class DeclineController {
	private final UserService userService;
	private final CommentService commentService;
	private final ApplicationsService applicationsService;
	private static final String DECLINE_REVIEW_SUCCESS_VIEW_NAME = "/private/reviewers/decline_success_confirmation";
	
	DeclineController() {
		this(null, null, null);
	}

	@Autowired
	public DeclineController(UserService userService, CommentService commentService, ApplicationsService applicationsService) {
		this.userService = userService;
		this.commentService = commentService;
		this.applicationsService = applicationsService;
	}
	
	@RequestMapping(value="/review", method = RequestMethod.GET)
	public String declineReview(@RequestParam Integer userId, @RequestParam Integer applicationId, ModelMap modelMap) {
		RegisteredUser reviewer = getReviewer(userId);
		ApplicationForm application = getApplicationForm(applicationId);
		commentService.declineReview(reviewer, application);
		modelMap.put("applicant", application.getApplicant());
		return DECLINE_REVIEW_SUCCESS_VIEW_NAME;
	}
	
	@ModelAttribute("reviewer")
	public RegisteredUser getReviewer(@RequestParam Integer userId) {
		RegisteredUser reviewer = userService.getUser(userId);
		if (reviewer == null){
			throw new ResourceNotFoundException();
		}
		return reviewer;
	}
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationById(applicationId);
		if (applicationForm == null){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}
}
