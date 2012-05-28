package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/approveOrReject" })
public class ApproveRejectController {

	
	private final ApplicationsService applicationsService;
	private final UserService userService;

	ApproveRejectController() {
		this(null, null);
	}

	@Autowired
	public ApproveRejectController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView applyDecision(@ModelAttribute ApplicationForm applicationForm, @RequestParam ApplicationFormStatus decision) {
		RegisteredUser approver = userService.getCurrentUser();
		if(!(approver.isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram()) || approver.hasAdminRightsOnApplication(applicationForm))){
			throw new ResourceNotFoundException();
		}
		
		if ( approver.hasAdminRightsOnApplication(applicationForm) && decision == ApplicationFormStatus.APPROVED){
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
	public ApplicationForm getApplicationForm(String id) {
		RegisteredUser approver = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(id);
		if(applicationForm == null || !approver.canSee(applicationForm)){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}

	@RequestMapping(value="/decisionmade", method = RequestMethod.GET)
	public ModelAndView getApprovedOrRejectedPage(ApplicationForm applicationForm) {
		PageModel pageModel = new PageModel();
		pageModel.setApplicationForm(applicationForm);
		pageModel.setUser(userService.getCurrentUser());
		return new ModelAndView("/reviewer/approvedOrRejected", "model", pageModel);
	}


}
