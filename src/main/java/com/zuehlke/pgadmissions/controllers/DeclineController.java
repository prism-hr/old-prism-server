package com.zuehlke.pgadmissions.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/decline" })
public class DeclineController {
    
    @Autowired
	private UserService userService;
    
    @Autowired
	private CommentService commentService;
    
    @Autowired
	private ApplicationFormService applicationsService;
    
    @Autowired
	private RefereeService refereeService;
    
    @Autowired
	private ActionService actionService;

	@RequestMapping(value = "/review", method = RequestMethod.GET)
	public String declineReview(@RequestParam String activationCode, @RequestParam String applicationId, @RequestParam(required = false) String confirmation, ModelMap modelMap) {
	    RegisteredUser reviewer = getReviewer(activationCode);
	    ApplicationForm application = getApplicationForm(applicationId);
	    
	    actionService.validateAction(application, reviewer, ApplicationFormAction.PROVIDE_REVIEW);
	    
		if (StringUtils.equalsIgnoreCase(confirmation, "OK")) {
		    commentService.declineReview(reviewer, application);
		    modelMap.put("message", "Thank you for letting us know you are unable to act as a reviewer on this occasion.");
		    reviewer.setDirectToUrl(null);
		    userService.save(reviewer);
		    return TemplateLocation.DECLINE_SUCCESS_VIEW_NAME;
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
            return TemplateLocation.DECLINE_CONFIRMATION_VIEW_NAME;
		}
	}

	public Referee getReferee(String activationCode, ApplicationForm applicationForm) {
		RegisteredUser user = userService.getUserByActivationCode(activationCode);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		// FIXME get referee for a user
		return null; //user.getRefereeForApplicationForm(applicationForm);
	}

	@RequestMapping(value = "/reference", method = RequestMethod.GET)
	public String declineReference(@RequestParam String activationCode, @RequestParam String applicationId, @RequestParam(required = false) String confirmation, ModelMap modelMap) {
	    ApplicationForm applicationForm = getApplicationForm(applicationId);
	    Referee referee = getReferee(activationCode, applicationForm);
	    RegisteredUser user = userService.getUserByActivationCode(activationCode);
	    
	    actionService.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_REFERENCE);
	    
	    if (StringUtils.equalsIgnoreCase(confirmation, "OK")) {
	        // the user clicked on "Confirm"
    		refereeService.declineToActAsRefereeAndSendNotification(referee);
    		modelMap.put("message", "Thank you for letting us know you are unable to act as a referee on this occasion.");
    		user.setDirectToUrl(null);
            userService.save(user);
    		return TemplateLocation.DECLINE_SUCCESS_VIEW_NAME;
	    } else if (StringUtils.equalsIgnoreCase(confirmation, "Cancel")) {
	        // the user clicked on "Provide Reference"
	        if (!referee.getUser().isEnabled()) {
	            return "redirect:/register?activationCode=" + referee.getUser().getActivationCode() + "&directToUrl=/referee/addReferences?applicationId=" + applicationForm.getApplicationNumber();
	        } else {
	            return "redirect:/referee/addReferences?applicationId=" + applicationForm.getApplicationNumber() + "&activationCode=" + referee.getUser().getActivationCode();
	        }
	    } else {
	        modelMap.put("message", "Please confirm that you wish to decline to provide a reference. <b>You will not be able to reverse this decision.</b>");
	        modelMap.put("okButton", "Confirm");
            modelMap.put("cancelButton", "Provide Reference");
            return TemplateLocation.DECLINE_CONFIRMATION_VIEW_NAME;
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
		ApplicationForm applicationForm = applicationsService.getByApplicationNumber(applicationId);
		if (applicationForm == null) {
			throw new MissingApplicationFormException(applicationId);
		}
		return applicationForm;
	}
}