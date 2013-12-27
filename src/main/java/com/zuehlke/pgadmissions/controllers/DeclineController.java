package com.zuehlke.pgadmissions.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.security.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;

@Controller
@RequestMapping(value = { "/decline" })
public class DeclineController {
	private final ApplicationsService applicationsService;
	private static final String DECLINE_SUCCESS_VIEW_NAME = "/private/reviewers/decline_success_confirmation";
	private static final String DECLINE_CONFIRMATION_VIEW_NAME = "/private/reviewers/decline_confirmation";
	private final RefereeService refereeService;
	private final ActionsProvider actionsProvider;
	private final ApplicationFormUserRoleService applicationFormUserRoleService;

	DeclineController() {
		this(null, null, null, null);
	}

	@Autowired
	public DeclineController(ApplicationFormUserRoleService applicationFormUserRoleService, ApplicationsService applicationsService, RefereeService refereeService, ActionsProvider actionsProvider) {
		this.applicationFormUserRoleService = applicationFormUserRoleService;
		this.applicationsService = applicationsService;
		this.refereeService = refereeService;
		this.actionsProvider = actionsProvider;
	}

	@RequestMapping(value = "/reference", method = RequestMethod.GET)
	public String declineReference(@RequestParam String activationCode, @RequestParam String applicationId, 
			@RequestParam(required = false) String confirmation, ModelMap modelMap, @ModelAttribute ApplicationForm applicationForm) {
	    Referee referee = getReferee(activationCode, applicationForm);
	    RegisteredUser user = applicationFormUserRoleService.getUserByActivationCode(activationCode); 
	    actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_REFERENCE);
	    
	    if (StringUtils.equalsIgnoreCase(confirmation, "OK")) {
	        // the user clicked on "Confirm"
    		refereeService.declineToActAsRefereeAndSendNotification(referee);
    		modelMap.put("message", "Thank you for letting us know that you are unable to act as a referee on this occasion.");
    		refereeService.addReferenceEventToApplication(referee);
    		return DECLINE_SUCCESS_VIEW_NAME;
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
            return DECLINE_CONFIRMATION_VIEW_NAME;
	    }
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(String applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		return applicationForm;
	}
	
	public Referee getReferee(String activationCode, ApplicationForm applicationForm) {
		RegisteredUser user = applicationFormUserRoleService.getUserByActivationCode(activationCode);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		return user.getRefereeForApplicationForm(applicationForm);
	}
	
}