package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/decline" })
public class DeclineController {
	private final UserService userService;
	private final CommentService commentService;
	private final ApplicationsService applicationsService;
	private static final String DECLINE_SUCCESS_VIEW_NAME = "/private/reviewers/decline_success_confirmation";
	private final RefereeService refereeService;
	
	DeclineController() {
		this(null, null, null, null);
	}

	@Autowired
	public DeclineController(UserService userService, CommentService commentService, ApplicationsService applicationsService,
			RefereeService refereeService) {
		this.userService = userService;
		this.commentService = commentService;
		this.applicationsService = applicationsService;
		this.refereeService = refereeService;
	}
	
	@RequestMapping(value="/review", method = RequestMethod.GET)
	public String declineReview(@RequestParam Integer userId, @RequestParam Integer applicationId, ModelMap modelMap) {
		RegisteredUser reviewer = getReviewer(userId);
		ApplicationForm application = getApplicationForm(applicationId);
		commentService.declineReview(reviewer, application);
		modelMap.put("message", "You have been successfully decline to provide a review for user " + application.getApplicant().getFirstName() + " "+ application.getApplicant().getLastName());
		return DECLINE_SUCCESS_VIEW_NAME;
	}
	
	public Referee getReferee(@RequestParam Integer refereeId) {	
		Referee referee = refereeService.getRefereeById(refereeId);
		if(referee == null){
			throw new ResourceNotFoundException();
		}
		return referee;
	}

	@RequestMapping(value = "/reference", method = RequestMethod.GET)
	public String declineReference(@RequestParam Integer refereeId, ModelMap modelMap) {
		Referee referee = getReferee(refereeId);
		referee.setDeclined(true);
		refereeService.saveReferenceAndSendDeclineNotifications(referee);
		modelMap.put("message", "You have been successfully decline to provide a reference for user " + referee.getApplication().getApplicant().getFirstName() + " "+ referee.getApplication().getApplicant().getLastName());
		return DECLINE_SUCCESS_VIEW_NAME;
	}
	
	public RegisteredUser getReviewer(Integer userId) {
		RegisteredUser reviewer = userService.getUser(userId);
		if (reviewer == null){
			throw new ResourceNotFoundException();
		}
		return reviewer;
	}
	
	public ApplicationForm getApplicationForm(Integer applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationById(applicationId);
		if (applicationForm == null){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}
}
