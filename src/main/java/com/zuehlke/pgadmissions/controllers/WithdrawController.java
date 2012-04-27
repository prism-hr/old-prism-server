package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/withdraw")
public class WithdrawController {

	private final ApplicationsService applicationsService;

	public WithdrawController() {
		this(null);
	}
	
	@Autowired
	public WithdrawController(ApplicationsService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String withdrawApplicationAndGetApplicationList(@ModelAttribute ApplicationForm applicationForm) {
		applicationForm.setStatus(ApplicationFormStatus.WITHDRAWN);
		applicationsService.save(applicationForm);
		return "redirect:/applications";
	}
	
	
	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationById(applicationId);
		if(applicationForm == null || !getCurrentUser().canSee(applicationForm) ){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
		
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	

}