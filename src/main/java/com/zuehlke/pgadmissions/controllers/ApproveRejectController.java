package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotApproveApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "/approveOrReject" })
public class ApproveRejectController {

	
	private final ApplicationsService applicationsService;

	ApproveRejectController() {
		this(null);
	}

	@Autowired
	public ApproveRejectController(ApplicationsService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView applyDecision(@ModelAttribute ApplicationForm applicationForm, @RequestParam ApprovalStatus decision) {
		RegisteredUser approver = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(applicationForm == null || !approver.isInRole(Authority.APPROVER) || !approver.canSee(applicationForm)){
			throw new ResourceNotFoundException();
		}
		if(!applicationForm.isReviewable()){
			throw new CannotApproveApplicationException();
		}
				
		applicationForm.setApprovalStatus(decision);
		applicationForm.setApprover(approver);
		applicationsService.save(applicationForm);
		
		return new ModelAndView("redirect:/reviewer/assign", "id", applicationForm.getId());
	}

	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(Integer id) {
		return applicationsService.getApplicationById(id);
	}


}
