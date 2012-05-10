package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/moveToInterview")
public class MoveToInterviewController {

	private final ApplicationsService applicationsService;
	private final UserService userService;
	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	private final NewUserByAdminValidator validator;
	private final InterviewerService interviewerService;
	private final MessageSource messageSource;
	
	MoveToInterviewController() {
		this(null, null, null, null, null);
	}
	
	@Autowired
	public MoveToInterviewController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator, InterviewerService interviewerService, MessageSource messageSource) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.validator = validator;
		this.interviewerService = interviewerService;
		this.messageSource = messageSource;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getInterviewDetailsPage() {
		return INTERVIEW_DETAILS_VIEW_NAME;
	}
	
	@InitBinder(value = "interviewer")
	public void registerValidators(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	RegisteredUser getCurrentUser() {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		return userService.getUser(currentUser.getId());
	}
	
	
	@RequestMapping(value="/createInterviewer",method = RequestMethod.POST)
	public String createInterviewer(@ModelAttribute ApplicationForm applicationForm, @Valid @ModelAttribute("interviewer") RegisteredUser interviewer,
			BindingResult bindingResult, ModelMap modelMap) {
		
		Program program = applicationForm.getProgram();
		checkAdminPermission(program);
		
		if (bindingResult.hasErrors()) {
			return INTERVIEW_DETAILS_VIEW_NAME;
		}
		
		RegisteredUser interviewerUser = userService.getUserByEmailIncludingDisabledAccounts(interviewer.getEmail());
		if(interviewerUser == null){
			RegisteredUser newUser = interviewerService.createNewUserWithInterviewerRoleInProgram(interviewer, program);
			modelMap.put("message", getMessage("assignInterviewer.user.created", newUser.getUsername(), newUser.getEmail()));
		}
		else {
			if (interviewer.isInterviewerOfApplicationForm(applicationForm)) {
				modelMap.put("message", getMessage("assignInterviewer.user.alreadyExistsInTheApplication", interviewerUser.getUsername(), interviewerUser.getEmail()));
			}
			else if (interviewer.isInterviewerOfProgram(program)) {
				interviewerService.addInterviewerToProgram(interviewerUser, program);
				modelMap.put("message", getMessage("assignInterviewer.user.addedToProgramme", interviewerUser.getUsername(), interviewerUser.getEmail()));
			}
			else {
				modelMap.put("message", getMessage("assignInterviewer.user.alreadyInProgramme", interviewerUser.getUsername(), interviewerUser.getEmail()));
			}
		}
		return INTERVIEW_DETAILS_VIEW_NAME;
		
	}
	
	@RequestMapping(value="/move",method = RequestMethod.POST)
	public String moveToInterview(@ModelAttribute ApplicationForm applicationForm) {
		applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
		applicationsService.save(applicationForm);
		return "redirect:/applications";

	}
	

	private void checkAdminPermission(Program programme) {
		RegisteredUser currentUser = getCurrentUser();
		if (!(programme.getAdministrators().contains(currentUser) || 
				currentUser.isInRole(Authority.SUPERADMINISTRATOR) || 
		programme.getProgramReviewers().contains(currentUser))) {
			throw new ResourceNotFoundException();
		}
	}
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		System.out.println("HEREEE ");
		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if (application == null || !getCurrentUser().isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram())) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	
	@ModelAttribute("programme")
	public Program getProgrammeForApplication(@ModelAttribute("applicationForm") ApplicationForm application) {
		return application.getProgram();
	}
	
	@ModelAttribute("interviewer")
	public RegisteredUser getInterviewer() {
		RegisteredUser interviewer = new RegisteredUser();
		return interviewer;
	}
	
	@ModelAttribute("programmeInterviewers")
	public List<RegisteredUser> getProgrammeInterviewers(@ModelAttribute("programme") Program program,
			@ModelAttribute("applicationForm") ApplicationForm application) {
		
		System.out.println("App " + application);
		System.out.println("App program " + application.getProgram());
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
	
	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}

	
}
