package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/moveToInterview")
public class MoveToInterviewController {

	private final ApplicationsService applicationsService;
	private final UserService userService;
	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";

	
	MoveToInterviewController() {
		this(null, null);
	}
	
	@Autowired
	public MoveToInterviewController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
	}
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationById(applicationId);
		if (applicationForm == null || !getCurrentUser().isInRoleInProgram(Authority.ADMINISTRATOR, applicationForm.getProgram())) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;

	}

	RegisteredUser getCurrentUser() {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		return userService.getUser(currentUser.getId());
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getInterviewDetailsPage(@ModelAttribute("applicationForm") ApplicationForm applicationForm) {
		return INTERVIEW_DETAILS_VIEW_NAME;
	}
	
	@RequestMapping(value="move",method = RequestMethod.POST)
	public String moveToInterview(@ModelAttribute ApplicationForm applicationForm) {
		applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
		applicationsService.save(applicationForm);
		return "redirect:/applications";

	}
	
	@ModelAttribute("programme")
	public Program getProgrammeForApplication(@ModelAttribute("applicationForm") ApplicationForm application) {
		return application.getProgram();
	}
	
	@ModelAttribute("programmeInterviewers")
	public List<RegisteredUser> getProgrammeInterviewers(@ModelAttribute("programme") Program program,
			@ModelAttribute("applicationForm") ApplicationForm application) {
		List<RegisteredUser> availableInterviewers = new ArrayList<RegisteredUser>();
		List<RegisteredUser> programmeInterviewers = program.getInterviewers();
		for (RegisteredUser registeredUser : programmeInterviewers) {
			if(!registeredUser.isInterviewerOfApplicationForm(application)){
				availableInterviewers.add(registeredUser);
			}
		}
				
		return availableInterviewers;
	}
	
	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

	@ModelAttribute("applicationInterviewers")
	public List<RegisteredUser> getApplicationInterviewersAsUsers(@ModelAttribute("applicationForm") ApplicationForm application) {
		List<RegisteredUser> existingInterviewers = new ArrayList<RegisteredUser>();
		List<Interviewer> appInterviewers = application.getInterviewers();
		for (Interviewer interviewer : appInterviewers) {
			if(!existingInterviewers.contains(interviewer.getUser())){
				existingInterviewers.add(interviewer.getUser());
			}
		}
		return existingInterviewers;
	}

	
}
