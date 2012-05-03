package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.CannotWithdrawApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationListModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.WithdrawService;

@Controller
@RequestMapping("/withdraw")
public class WithdrawController {

	private final WithdrawService withdrawService;
	private final ApplicationsService applicationService;

	public WithdrawController() {
		this(null, null);
	}
	
	@Autowired
	public WithdrawController(ApplicationsService applicationService, WithdrawService withdrawService) {
		this.applicationService = applicationService;
		this.withdrawService = withdrawService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String withdrawApplicationAndGetApplicationList(@ModelAttribute ApplicationForm applicationForm) {
		if(applicationForm.getStatus() != ApplicationFormStatus.VALIDATION ){
			throw new CannotWithdrawApplicationException();
		}
		applicationForm.setStatus(ApplicationFormStatus.WITHDRAWN);
		withdrawService.saveApplicationFormAndSendMailNotifications(applicationForm);
		return "redirect:/applications";
	}
	
	
	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = applicationService.getApplicationById(applicationId);
		if(applicationForm == null || !getCurrentUser().canSee(applicationForm) ){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
		
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	

}