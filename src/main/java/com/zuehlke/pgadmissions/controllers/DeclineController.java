package com.zuehlke.pgadmissions.controllers;

import org.apache.commons.lang.StringUtils;
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
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
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
	private static final String DECLINE_CONFIRMATION_VIEW_NAME = "/private/reviewers/decline_confirmation";
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
	public String declineReview(@RequestParam String activationCode, @RequestParam String applicationId, @RequestParam(required = false) String confirmation, ModelMap modelMap) {
	    RegisteredUser reviewer = getReviewer(activationCode);
	    ApplicationForm application = getApplicationForm(applicationId);
		if (StringUtils.equalsIgnoreCase(confirmation, "OK")) {
		    // the user clicked on "Confirm"
		    if (application.getStatus() != ApplicationFormStatus.REVIEW){
		        throw new ActionNoLongerRequiredException(applicationId);
		    }
		    if (!reviewer.isReviewerInLatestReviewRoundOfApplicationForm(application)) {
		        throw new InsufficientApplicationFormPrivilegesException(applicationId);
		    }
		    commentService.declineReview(reviewer, application);
		    modelMap.put("message", "Thank you for letting us know you are unable to act as a reviewer on this occasion.");
		    reviewer.setDirectToUrl(null);
		    userService.save(reviewer);
		    return DECLINE_SUCCESS_VIEW_NAME;
		} else if (StringUtils.equalsIgnoreCase(confirmation, "Cancel")) {
            // the user clicked on "Provide Review"
		    if (!reviewer.isEnabled()) {
                return "redirect:/register?activationCode=" + reviewer.getActivationCode() + "&directToUrl=/reviewFeedback?applicationId=" + application.getApplicationNumber();
		    } else {
		        return "redirect:/reviewFeedback?applicationId=" + application.getApplicationNumber() + "&activationCode=" + reviewer.getActivationCode();
		    }
		} else {
		    modelMap.put("message", "Please confirm that you wish to decline to provide a review. <b>You will not be able to reverse this decision.</b>");
		    modelMap.put("okButton", "Confirm");
		    modelMap.put("cancelButton", "Provide Review");
            return DECLINE_CONFIRMATION_VIEW_NAME;
		}
	}

	public Referee getReferee(String activationCode, ApplicationForm applicationForm) {
		RegisteredUser user = userService.getUserByActivationCode(activationCode);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		Referee referee = user.getRefereeForApplicationForm(applicationForm);
		if (referee == null) {
			throw new InsufficientApplicationFormPrivilegesException(applicationForm.getApplicationNumber());
		}
		return referee;
	}

	@RequestMapping(value = "/reference", method = RequestMethod.GET)
	public String declineReference(@RequestParam String activationCode, @RequestParam String applicationId, @RequestParam(required = false) String confirmation, ModelMap modelMap) {
	    ApplicationForm applicationForm = getApplicationForm(applicationId);
	    Referee referee = getReferee(activationCode, applicationForm);
	    RegisteredUser user = userService.getUserByActivationCode(activationCode);
	    if (StringUtils.equalsIgnoreCase(confirmation, "OK")) {
	        // the user clicked on "Confirm"
    		refereeService.declineToActAsRefereeAndSendNotification(referee);
    		modelMap.put("message", "Thank you for letting us know you are unable to act as a referee on this occasion.");
    		user.setDirectToUrl(null);
            userService.save(user);
    		return DECLINE_SUCCESS_VIEW_NAME;
	    } else if (StringUtils.equalsIgnoreCase(confirmation, "Cancel")) {
	        // the user clicked on "Provide Reference"
            if (referee != null && !referee.getUser().isEnabled()) {
                return "redirect:/register?activationCode=" + referee.getUser().getActivationCode() + "&directToUrl=/referee/addReferences?applicationId=" + applicationForm.getApplicationNumber();
            } else {
                return "redirect:/referee/addReferences?applicationId=" + applicationForm.getApplicationNumber() + "&activationCode=" + referee.getUser().getActivationCode();
            }
	    } else {
	        modelMap.put("message", "Please confirm that you wish to decline to provide a reference. <b>You will not be able to reverse this decision.</b>");
	        modelMap.put("okButton", "Confirm");
            modelMap.put("cancelButton", "Provide Reference");
            return DECLINE_CONFIRMATION_VIEW_NAME;
	    }
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
			throw new MissingApplicationFormException(applicationId);
		}
		return applicationForm;
	}
}
