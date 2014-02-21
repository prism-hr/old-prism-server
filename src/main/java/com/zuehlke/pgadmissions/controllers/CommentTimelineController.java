package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/comments" })
public class CommentTimelineController {
    // TODO finish it, comment service removed, comment should be used instead of timeline object, fix tests

	private static final String COMMENTS_VIEW = "private/staff/admin/comment/timeline";

	private UserService userService;
	
	private ApplicationsService applicationService;
	
	private CommentService commentService;

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String id) {
		ApplicationForm applicationForm = applicationService.getApplicationByApplicationNumber(id);
		if (applicationForm == null) {
			throw new MissingApplicationFormException(id);
		}
		return applicationForm;
	}


	@RequestMapping(value = { "/view" }, method = RequestMethod.GET)
	public String getCommentsView() {
		return COMMENTS_VIEW;
	}
	
	@ModelAttribute("comments")
	public List<Comment> getComments(@ModelAttribute ApplicationForm applicationForm) {		
		return commentService.getVisibleComments(getUser(), applicationForm);		
		
	}

	@ModelAttribute("validationQuestionOptions")
	public ValidationQuestionOptions[] getValidationQuestionOptions() {
		return ValidationQuestionOptions.values();
	}
	
	@ModelAttribute("homeOrOverseasOptions")
	public HomeOrOverseas[] getHomeOrOverseasOptions() {
		return HomeOrOverseas.values();
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {		
		return userService.getCurrentUser();
	}

}