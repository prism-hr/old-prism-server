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
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotApproveApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
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
	public ModelAndView applyDecision(@ModelAttribute ApplicationForm applicationForm, @RequestParam ApplicationFormStatus decision) {
		RegisteredUser approver = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(!(approver.isInRole(Authority.APPROVER) || approver.isInRole(Authority.ADMINISTRATOR))){
			throw new ResourceNotFoundException();
		}
		
		if (approver.isInRole(Authority.ADMINISTRATOR) && decision == ApplicationFormStatus.APPROVED){
			throw new ResourceNotFoundException();
		}
		if(!applicationForm.isModifiable()){
			throw new CannotApproveApplicationException();
		}
				
		applicationForm.setStatus(decision);
		applicationForm.setApprover(approver);
		applicationsService.save(applicationForm);		
		return new ModelAndView("redirect:/applications", "decision", decision.toString().toLowerCase()); 
	}

	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(Integer id) {
		RegisteredUser approver = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationsService.getApplicationById(id);
		if(applicationForm == null || !approver.canSee(applicationForm)){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}

	@RequestMapping(value="/decisionmade", method = RequestMethod.GET)
	public ModelAndView getApprovedOrRejectedPage(ApplicationForm applicationForm) {
		PageModel pageModel = new PageModel();
		pageModel.setApplicationForm(applicationForm);
		pageModel.setUser((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails());
		return new ModelAndView("/reviewer/approvedOrRejected", "model", pageModel);
	}


}
