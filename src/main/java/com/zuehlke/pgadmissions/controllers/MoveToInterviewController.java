package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/moveToInterview")
public class MoveToInterviewController {

	private final ApplicationsService applicationsService;
	private final UserService userService;
	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	private final NewUserByAdminValidator interviewerValidator;
	private final InterviewerService interviewerService;
	private final MessageSource messageSource;
	private final InterviewService interviewService;
	private final InterviewValidator interviewValidator;
	private final DatePropertyEditor datePropertyEditor;
	
	MoveToInterviewController() {
		this(null, null, null, null, null, null, null, null);
	}
	
	@Autowired
	public MoveToInterviewController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator, 
			InterviewerService interviewerService, MessageSource messageSource, InterviewService interviewService, InterviewValidator interviewValidator,
			DatePropertyEditor datePropertyEditor) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.interviewerValidator = validator;
		this.interviewerService = interviewerService;
		this.messageSource = messageSource;
		this.interviewService = interviewService;
		this.interviewValidator = interviewValidator;
		this.datePropertyEditor = datePropertyEditor;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getInterviewDetailsPage() {
		return INTERVIEW_DETAILS_VIEW_NAME;
	}
	
	@InitBinder(value = "interviewer")
	public void registerInterviewerValidators(WebDataBinder binder) {
		binder.setValidator(interviewerValidator);
	}
	
	@InitBinder(value = "interview")
	public void registerInterviewValidatorsAndPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.setValidator(interviewValidator);
	}

	@RequestMapping(value="/createInterviewer",method = RequestMethod.POST)
	public String createInterviewer(@RequestParam Integer applicationId, @Valid @ModelAttribute("interviewer") RegisteredUser interviewer,
			BindingResult bindingResult, ModelMap modelMap) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Program program = applicationForm.getProgram();
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
	public String moveToInterview(@RequestParam Integer applicationId, @Valid @ModelAttribute("interview") Interview interview,
			BindingResult bindingResult, ModelMap modelMap, @ModelAttribute("unsavedInterviewers") ArrayList<RegisteredUser> unsavedInterviewers) {
		
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		
		if (bindingResult.hasErrors()) {
			return INTERVIEW_DETAILS_VIEW_NAME;
		}
		
		for (RegisteredUser interviewerUser : unsavedInterviewers) {
			System.out.println("UNSAVED: " + interviewerUser.getUsername());
			if (!interviewerUser.isInterviewerOfApplicationForm(applicationForm)) {
				interviewerService.createInterviewerToApplication(interviewerUser, applicationForm);
			}
		}
		
		interview.setApplication(applicationForm);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(interview.getInterviewDueDate());
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		interview.setInterviewDueDate(calendar.getTime());   
		interviewService.save(interview);
		
		applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
		applicationsService.save(applicationForm);
		
		return "redirect:/applications";
	}
	
	@ModelAttribute("unsavedInterviewers")
	public List<RegisteredUser> unsavedInterviewers(String unsavedInterviewersRaw) {
		List<RegisteredUser> retval = new ArrayList<RegisteredUser>();
		if (unsavedInterviewersRaw == null || unsavedInterviewersRaw.isEmpty()) {
			return retval;
		}
		String[] tokens = unsavedInterviewersRaw.split("\\|");
		for (String idStr : tokens) {
			retval.add(userService.getUser(Integer.parseInt(idStr)));
		}
		return retval;
	}

	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		RegisteredUser user = getUser();
		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if (application == null || !user.isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram())|| !user.canSee(application) ) {
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
		return userService.getCurrentUser();
	}

	@ModelAttribute("applicationInterviewers")
	public Set<RegisteredUser> getApplicationInterviewersAsUsers(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Set<RegisteredUser> existingInterviewers = new HashSet<RegisteredUser>();
		for (Interviewer interviewer : applicationForm.getInterviewers()) {
			existingInterviewers.add(interviewer.getUser());
		}
		return existingInterviewers;
	}
	
	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}

	
}
