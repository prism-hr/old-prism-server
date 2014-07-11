package com.zuehlke.pgadmissions.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
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
	private ApplicationService applicationsService;
    
    @Autowired
	private RefereeService refereeService;
    
    @Autowired
	private ActionService actionService;

	public ApplicationReferee getReferee(String activationCode, Application applicationForm) {
		User user = userService.getUserByActivationCode(activationCode);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		// FIXME get referee for a user
		return null; //user.getRefereeForApplicationForm(applicationForm);
	}

	@RequestMapping(value = "/reference", method = RequestMethod.GET)
	public String declineReference(@RequestParam String activationCode, @RequestParam String applicationId, @RequestParam(required = false) String confirmation, ModelMap modelMap) {
	    Application application = getApplicationForm(applicationId);
	    ApplicationReferee referee = getReferee(activationCode, application);
	    User user = userService.getUserByActivationCode(activationCode);
	    
	    // TOTO: comment posting
	    Action action = actionService.getById(PrismAction.APPLICATION_PROVIDE_REFERENCE);
	    actionService.validateAction((Resource) application, action, referee.getUser(), null);
	    
	    if (StringUtils.equalsIgnoreCase(confirmation, "OK")) {
	        // the user clicked on "Confirm"
    		refereeService.declineToActAsRefereeAndSendNotification(referee.getId());
    		modelMap.put("message", "Thank you for letting us know you are unable to act as a referee on this occasion.");

            userService.save(user);
    		return TemplateLocation.DECLINE_SUCCESS_VIEW_NAME;
	    } else if (StringUtils.equalsIgnoreCase(confirmation, "Cancel")) {
	        // the user clicked on "Provide Reference"
	        if (!referee.getUser().isEnabled()) {
	            return "redirect:/register?activationCode=" + referee.getUser().getActivationCode() + "&directToUrl=/referee/addReferences?applicationId=" + application.getCode();
	        } else {
	            return "redirect:/referee/addReferences?applicationId=" + application.getCode() + "&activationCode=" + referee.getUser().getActivationCode();
	        }
	    } else {
	        modelMap.put("message", "Please confirm that you wish to decline to provide a reference. <b>You will not be able to reverse this decision.</b>");
	        modelMap.put("okButton", "Confirm");
            modelMap.put("cancelButton", "Provide Reference");
            return TemplateLocation.DECLINE_CONFIRMATION_VIEW_NAME;
	    }
	}

	public User getReviewer(String activationCode) {
		User reviewer = userService.getUserByActivationCode(activationCode);
		if (reviewer == null) {
			throw new ResourceNotFoundException();
		}
		return reviewer;
	}

	public Application getApplicationForm(String applicationId) {
		Application applicationForm = applicationsService.getByApplicationNumber(applicationId);
		if (applicationForm == null) {
			throw new ResourceNotFoundException(applicationId);
		}
		return applicationForm;
	}
}