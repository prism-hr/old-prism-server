package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/comments" })
public class CommentTimelineController {
    // TODO finish it, comment service removed, comment should be used instead of timeline object, fix tests

	private static final String COMMENTS_VIEW = "private/staff/admin/comment/timeline";

	@Autowired
	private UserService userService;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private CommentService commentService;

	@ModelAttribute("applicationForm")
	public Application getApplicationForm(@RequestParam String id) {
		Application applicationForm = applicationService.getByApplicationNumber(id);
		if (applicationForm == null) {
			throw new ResourceNotFoundException(id);
		}
		return applicationForm;
	}


	@RequestMapping(value = { "/view" }, method = RequestMethod.GET)
	public String getCommentsView() {
		return COMMENTS_VIEW;
	}
	
	@ModelAttribute("comments")
	public List<Comment> getComments(@ModelAttribute Application applicationForm) {		
		return commentService.getVisibleComments(getUser(), applicationForm);		
		
	}

	@ModelAttribute("validationQuestionOptions")
	public ValidationQuestionOptions[] getValidationQuestionOptions() {
		return ValidationQuestionOptions.values();
	}
	
	@ModelAttribute("homeOrOverseasOptions")
	public ResidenceStatus[] getHomeOrOverseasOptions() {
		return ResidenceStatus.values();
	}

	@ModelAttribute("user")
	public User getUser() {		
		return userService.getCurrentUser();
	}

}