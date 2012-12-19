package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/viewprogress")
public class ApplicantTimelineController {

	private static final String APPLICANT_TIMELINEPAGE = "/private/pgStudents/form/timelinepage";
	private final UserService userService;
	private final ApplicationsService applicationsService;
	
	ApplicantTimelineController(){
		this(null, null);
	}
	@Autowired
	public ApplicantTimelineController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;

	}
	
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (applicationForm == null || (applicationForm.getApplicant() != null && !currentUser.getId().equals(applicationForm.getApplicant().getId()))) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}
	
	@ModelAttribute("user")
	public RegisteredUser getUser() {		
		return userService.getCurrentUser();
	}
	
	@RequestMapping( method = RequestMethod.GET)
	public String getTimelinePage() {
		return APPLICANT_TIMELINEPAGE;
	}


}
