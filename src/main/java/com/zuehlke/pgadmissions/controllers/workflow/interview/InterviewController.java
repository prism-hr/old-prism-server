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
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public abstract class InterviewController {
	protected final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	protected final ApplicationsService applicationsService;
	protected final UserService userService;
	protected final NewUserByAdminValidator interviewerValidator;
	protected final MessageSource messageSource;
	protected final InterviewService interviewService;
	protected final InterviewValidator interviewValidator;
	protected final DatePropertyEditor datePropertyEditor;
	protected final InterviewerPropertyEditor interviewerPropertyEditor;
	protected final EncryptionHelper encryptionHelper;

	InterviewController() {
		this(null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public InterviewController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator,//
			MessageSource messageSource, InterviewService interviewService, InterviewValidator interviewValidator, DatePropertyEditor datePropertyEditor,//
			InterviewerPropertyEditor interviewerPropertyEditor, EncryptionHelper encryptionHelper) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.interviewerValidator = validator;

		this.messageSource = messageSource;
		this.interviewService = interviewService;
		this.interviewValidator = interviewValidator;
		this.datePropertyEditor = datePropertyEditor;
		this.interviewerPropertyEditor = interviewerPropertyEditor;
		this.encryptionHelper = encryptionHelper;
	}

	@InitBinder(value = "interviewer")
	public void registerInterviewerValidators(WebDataBinder binder) {
		binder.setValidator(interviewerValidator);
	}

	@InitBinder(value = "interview")
	public void registerInterviewValidatorsAndPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(Interviewer.class, interviewerPropertyEditor);
		binder.setValidator(interviewValidator);
	}

	@ModelAttribute("interviewer")
	public RegisteredUser getInterviewer() {
		return new RegisteredUser();
	}

	@ModelAttribute("programmeInterviewers")
	public List<RegisteredUser> getProgrammeInterviewers(@RequestParam String applicationId, @RequestParam(required = false) List<String> pendingInterviewer) {
		ApplicationForm application = getApplicationForm(applicationId);
		Program program = application.getProgram();
		List<RegisteredUser> availableInterviewers = new ArrayList<RegisteredUser>();
		List<RegisteredUser> programmeInterviewers = program.getInterviewers();
		for (RegisteredUser registeredUser : programmeInterviewers) {
			if (!registeredUser.isInterviewerOfApplicationForm(application)) {
				availableInterviewers.add(registeredUser);
			}
		}
		for (RegisteredUser registeredUser : getPendingInterviewers(pendingInterviewer, applicationId)) {
			if (availableInterviewers.contains(registeredUser)) {
				availableInterviewers.remove(registeredUser);
			}
		}

		return availableInterviewers;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("applicationInterviewers")
	public Set<RegisteredUser> getApplicationInterviewersAsUsers(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Set<RegisteredUser> existingInterviewers = new HashSet<RegisteredUser>();
		Interview latestInterview = applicationForm.getLatestInterview();
		if (latestInterview != null) {
			for (Interviewer interviewer : latestInterview.getInterviewers()) {
				existingInterviewers.add(interviewer.getUser());
			}
		}
		return existingInterviewers;
	}

	protected String getCreateInterviewerMessage(String code, RegisteredUser user) {
		return getMessage(code, new Object[] { user.getFirstName() + " " + user.getLastName(), user.getEmail() });
	}

	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {

		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (application == null//
				|| (!userService.getCurrentUser().hasAdminRightsOnApplication(application) && !userService.getCurrentUser()//
						.isInterviewerOfApplicationForm(application))) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	public abstract Interview getInterview(@RequestParam Object id);

	@ModelAttribute("pendingInterviewers")
	public List<RegisteredUser> getPendingInterviewers(@RequestParam(required = false) List<String> pendingInterviewerId, @RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> newUsers = new ArrayList<RegisteredUser>();
		if (pendingInterviewerId != null) {
			for (String encryptedId : pendingInterviewerId) {
				Integer id = encryptionHelper.decryptToInteger(encryptedId);
				RegisteredUser user = userService.getUser(id);
				if (!user.isInterviewerOfApplicationForm(applicationForm)) {
					newUsers.add(user);
				}
			}
		}

		return newUsers;
	}

	@ModelAttribute("previousInterviewers")
	public List<RegisteredUser> getPreviousInterviewers(@RequestParam String applicationId, @RequestParam(required = false) List<String> pendingInterviewer) {
		List<RegisteredUser> availablePreviousInterviewers = new ArrayList<RegisteredUser>();
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> previousInterviewersOfProgram = userService.getAllPreviousInterviewersOfProgram(applicationForm.getProgram());

		List<RegisteredUser> pendingInterviewers = getPendingInterviewers(pendingInterviewer, applicationId);

		for (RegisteredUser registeredUser : previousInterviewersOfProgram) {
			if (!registeredUser.isInterviewerOfApplicationForm(applicationForm) && !pendingInterviewers.contains(registeredUser)//
					&& !applicationForm.getProgram().getInterviewers().contains(registeredUser)) {
				availablePreviousInterviewers.add(registeredUser);
			}
		}

		return availablePreviousInterviewers;
	}

	@ModelAttribute("willingToInterviewReviewers")
	public List<RegisteredUser> getWillingToInterviewReviewers(@RequestParam String applicationId, @RequestParam(required = false) List<String> pendingInterviewer) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> availableReviewersWillingToInterview = new ArrayList<RegisteredUser>();
		List<RegisteredUser> reviewersWillingToInterview = applicationForm.getReviewersWillingToInterview();
		List<RegisteredUser> pendingInterviewers = getPendingInterviewers(pendingInterviewer, applicationId);
		for (RegisteredUser registeredUser : reviewersWillingToInterview) {
			if(!pendingInterviewers.contains(registeredUser) && !registeredUser.isInterviewerOfApplicationForm(applicationForm)){
				availableReviewersWillingToInterview.add(registeredUser);
			}
		}
		return availableReviewersWillingToInterview;

	}
}