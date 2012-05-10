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
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
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
	private final InterviewService interviewService;
	
	MoveToInterviewController() {
		this(null, null, null, null, null, null);
	}
	
	@Autowired
	public MoveToInterviewController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator, 
			InterviewerService interviewerService, MessageSource messageSource, InterviewService interviewService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.validator = validator;
		this.interviewerService = interviewerService;
		this.messageSource = messageSource;
		this.interviewService = interviewService;
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
	public String createInterviewer(@RequestParam Integer applicationId, @Valid @ModelAttribute("interviewer") RegisteredUser interviewer,
			BindingResult bindingResult, ModelMap modelMap) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
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
	public String moveToInterview(@RequestParam Integer applicationId, @ModelAttribute("interview") Interview interview) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		interview.setApplication(applicationForm);
//		interview.setDueDate(interview.getDueDate());   // add one day more
		interviewService.save(interview);
		applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
		applicationsService.save(applicationForm);
//		return "redirect:/applications";
		return INTERVIEW_DETAILS_VIEW_NAME;

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
		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if (application == null || !getCurrentUser().isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram())) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	
	@ModelAttribute("programme")
	public Program getProgrammeForApplication(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		return applicationForm.getProgram();
	}
	
	@ModelAttribute("interviewer")
	public RegisteredUser getInterviewer() {
		RegisteredUser interviewer = new RegisteredUser();
		return interviewer;
	}
	
	
	@ModelAttribute("interview")
	public Interview getInterview() {
		Interview interview = new Interview();
		return interview;
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
	public List<RegisteredUser> getApplicationInterviewersAsUsers(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> existingInterviewers = new ArrayList<RegisteredUser>();
		List<Interviewer> appInterviewers = applicationForm.getInterviewers();
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
