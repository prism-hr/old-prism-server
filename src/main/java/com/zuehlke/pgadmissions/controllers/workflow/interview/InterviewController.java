package com.zuehlke.pgadmissions.controllers.workflow.interview;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public abstract class InterviewController {
	protected final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	protected final ApplicationsService applicationsService;
	protected final UserService userService;	
	protected final NewUserByAdminValidator interviewerValidator;
	protected final InterviewerService interviewerService;
	protected final MessageSource messageSource;
	protected final InterviewService interviewService;
	protected final InterviewValidator interviewValidator;
	protected final DatePropertyEditor datePropertyEditor;

	InterviewController() {
		this(null, null, null, null, null, null, null, null);
	}

	@Autowired
	public InterviewController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator,
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

	@InitBinder(value = "interviewer")
	public void registerInterviewerValidators(WebDataBinder binder) {
		binder.setValidator(interviewerValidator);
	}

	@InitBinder(value = "interview")
	public void registerInterviewValidatorsAndPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.setValidator(interviewValidator);
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


	@ModelAttribute("interviewer")
	public RegisteredUser getInterviewer() {
	
		return new RegisteredUser();
	}

	@ModelAttribute("programmeInterviewers")
	public List<RegisteredUser> getProgrammeInterviewers(@RequestParam Integer applicationId, String unsavedInterviewersRaw) {
		ApplicationForm application = getApplicationForm(applicationId);
		Program program = application.getProgram();
		List<RegisteredUser> availableInterviewers = new ArrayList<RegisteredUser>();
		List<RegisteredUser> programmeInterviewers = program.getInterviewers();
		for (RegisteredUser registeredUser : programmeInterviewers) {
			if (!registeredUser.isInterviewerOfApplicationForm(application)) {
				availableInterviewers.add(registeredUser);
			}
		}
		List<RegisteredUser> unsavedInterviewers = unsavedInterviewers(unsavedInterviewersRaw);
		if (unsavedInterviewers != null) {
			availableInterviewers.removeAll(unsavedInterviewers);
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
		Interview currentInterview = applicationForm.getLatestInterview();
		if(currentInterview != null){
			for (Interviewer interviewer : currentInterview.getInterviewers()) {
				existingInterviewers.add(interviewer.getUser());
			}
		}
		return existingInterviewers;
	}

	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}


	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {

		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if (application == null
				|| (!userService.getCurrentUser().isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram()) && !userService.getCurrentUser()
						.isInterviewerOfApplicationForm(application))) {			
			throw new ResourceNotFoundException();
		}
		return application;
	}

	
	public abstract Interview getInterview(@RequestParam Integer applicationId);


}

	