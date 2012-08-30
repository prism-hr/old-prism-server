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
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
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
	public DeclineController(UserService userService, CommentService commentService, ApplicationsService applicationsService, RefereeService refereeService) {
		this.userService = userService;
		this.commentService = commentService;
		this.applicationsService = applicationsService;
		this.refereeService = refereeService;

	}

	@RequestMapping(value = "/review", method = RequestMethod.GET)
	public String declineReview(@RequestParam String activationCode, @RequestParam String applicationId, ModelMap modelMap) {
		RegisteredUser reviewer = getReviewer(activationCode);
		ApplicationForm application = getApplicationForm(applicationId);
		if (application.getStatus() == ApplicationFormStatus.REVIEW && reviewer.isReviewerInLatestReviewRoundOfApplicationForm(application)) {
			commentService.declineReview(reviewer, application);
		}
		modelMap.put("message", "Thank you for letting us know you are unable to act as a reviewer on this occasion.");
		return DECLINE_SUCCESS_VIEW_NAME;
	}

	public Referee getReferee(String activationCode, ApplicationForm applicationForm) {
		RegisteredUser user = userService.getUserByActivationCode(activationCode);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		Referee referee = user.getRefereeForApplicationForm(applicationForm);
		if (referee == null) {
			throw new ResourceNotFoundException();
		}
		return referee;

	}

	@RequestMapping(value = "/reference", method = RequestMethod.GET)
	public String declineReference(@RequestParam String activationCode, @RequestParam String applicationId, ModelMap modelMap) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Referee referee = getReferee(activationCode, applicationForm);

		refereeService.declineToActAsRefereeAndSendNotification(referee);
		modelMap.put("message", "Thank you for letting us know you are unable to act as a referee on this occasion.");
		return DECLINE_SUCCESS_VIEW_NAME;
	}

	public RegisteredUser getReviewer(String activationCode) {
		RegisteredUser reviewer = userService.getUserByActivationCode(activationCode);
		if (reviewer == null) {
			throw new ResourceNotFoundException();
		}
		return reviewer;
	}

	public ApplicationForm getApplicationForm(String applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (applicationForm == null) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}
}
