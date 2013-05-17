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
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotTerminateApplicationException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WithdrawService;

@Controller
@RequestMapping("/withdraw")
public class WithdrawController{

	private final WithdrawService withdrawService;
	
	private final ApplicationsService applicationService;
	
	private final EventFactory eventFactory;
	
	private final UserService userService;
	
	public WithdrawController() {
		this(null, null, null, null);
	}

	@Autowired
    public WithdrawController(ApplicationsService applicationService, UserService userService,
            WithdrawService withdrawService, EventFactory eventFactory) {
		this.applicationService = applicationService;
		this.userService = userService;
		this.withdrawService = withdrawService;
		this.eventFactory = eventFactory;
	}

    @RequestMapping(method = RequestMethod.POST)
    public String withdrawApplicationAndGetApplicationList(@ModelAttribute ApplicationForm applicationForm,
            ModelMap modelMap) {
        if (applicationForm.getStatus() == ApplicationFormStatus.APPROVED
                || applicationForm.getStatus() == ApplicationFormStatus.REJECTED
                || applicationForm.getStatus() == ApplicationFormStatus.WITHDRAWN) {
            throw new CannotTerminateApplicationException(applicationForm.getApplicationNumber());
        }
        
        if (applicationForm.getStatus().equals(ApplicationFormStatus.UNSUBMITTED)) {
            applicationForm.setWithdrawnBeforeSubmit(true);
        }
        
        applicationForm.setStatus(ApplicationFormStatus.WITHDRAWN);
        applicationForm.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.WITHDRAWN));
        withdrawService.saveApplicationFormAndSendMailNotifications(applicationForm);
        withdrawService.sendToPortico(applicationForm);
        return "redirect:/applications?messageCode=application.withdrawn&application=" + applicationForm.getApplicationNumber();
    }
	
	protected RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}
	
	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm applicationForm = applicationService.getApplicationByApplicationNumber(applicationId);
		if(applicationForm == null || !getCurrentUser().canSee(applicationForm) ){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}


	@ModelAttribute("user")
	public RegisteredUser getUser() {		
		return getCurrentUser();
	}
}