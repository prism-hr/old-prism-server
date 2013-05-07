package com.zuehlke.pgadmissions.controllers.workflow.interview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;

@Controller
@RequestMapping("/interview")
public class MoveToInterviewController {

	private static final String INTERVIEWERS_SECTION = "/private/staff/interviewers/interviewer_section";
	private static final String INTERVIEW_PAGE = "/private/staff/interviewers/interview_details";
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final InterviewValidator interviewValidator;
	private final InterviewerPropertyEditor interviewerPropertyEditor;
	private final InterviewService interviewService;
	private final DatePropertyEditor datePropertyEditor;
    private final InterviewTimeslotsPropertyEditor interviewTimeslotsPropertyEditor;

	MoveToInterviewController() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public MoveToInterviewController(ApplicationsService applicationsService, UserService userService, InterviewService interviewService,
			InterviewValidator interviewValidator, InterviewerPropertyEditor interviewerPropertyEditor, DatePropertyEditor datePropertyEditor, InterviewTimeslotsPropertyEditor interviewTimeslotsPropertyEditor) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.interviewService = interviewService;
		this.interviewValidator = interviewValidator;
		this.interviewerPropertyEditor = interviewerPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.interviewTimeslotsPropertyEditor = interviewTimeslotsPropertyEditor;
	}

	@RequestMapping(method = RequestMethod.GET, value = "moveToInterview")
	public String getInterviewDetailsPage() {
		return INTERVIEW_PAGE;
	}

	@RequestMapping(method = RequestMethod.GET, value = "interviewers_section")
	public String getInterviewersSection() {
		return INTERVIEWERS_SECTION;

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
	
    @ModelAttribute("actionsDefinition")
    public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(getUser(), application);
    }

	@ModelAttribute("programmeInterviewers")
	public List<RegisteredUser> getProgrammeInterviewers(@RequestParam String applicationId) {
		return getApplicationForm(applicationId).getProgram().getInterviewers();
	}

	@ModelAttribute("previousInterviewers")
	public List<RegisteredUser> getPreviousInterviewersAndReviewersWillingToInterview(@RequestParam String applicationId) {
		List<RegisteredUser> availablePreviousInterviewers = new ArrayList<RegisteredUser>();
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> previousInterviewersOfProgram = userService.getAllPreviousInterviewersOfProgram(applicationForm.getProgram());
		
		for (RegisteredUser registeredUser : previousInterviewersOfProgram) {
			if (!listContainsId(registeredUser, applicationForm.getProgram().getInterviewers())) {
				availablePreviousInterviewers.add(registeredUser);
			}
		}
		List<RegisteredUser> reviewersWillingToInterview = applicationForm.getReviewersWillingToInterview();
		for (RegisteredUser registeredUser : reviewersWillingToInterview) {
			if (!listContainsId(registeredUser, applicationForm.getProgram().getInterviewers()) && !listContainsId(registeredUser, availablePreviousInterviewers)) {
				availablePreviousInterviewers.add(registeredUser);
			}
		}
		return availablePreviousInterviewers;

	}

	@ModelAttribute("interview")
	public Interview getInterview(@RequestParam String applicationId) {
		Interview interview = new Interview();
		ApplicationForm applicationForm = getApplicationForm((String) applicationId);
		Interview latestInterview = applicationForm.getLatestInterview();
		if (latestInterview != null) {
			interview.setInterviewers(latestInterview.getInterviewers());
		}
		List<RegisteredUser> reviewersWillingToInterview = applicationForm.getReviewersWillingToInterview();
		for (RegisteredUser registeredUser : reviewersWillingToInterview) {
			if(!registeredUser.isInterviewerInInterview(interview)){
				Interviewer interviewer = new Interviewer();
				interviewer.setUser(registeredUser);
				interview.getInterviewers().add(interviewer);
			}
		}
		return interview;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@InitBinder("interview")
	public void registerValidatorAndPropertyEditor(WebDataBinder binder) {
		binder.setValidator(interviewValidator);
		binder.registerCustomEditor(Interviewer.class, interviewerPropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
		binder.registerCustomEditor(null, "timeslots", interviewTimeslotsPropertyEditor);
		binder.registerCustomEditor(null, "duration", new CustomNumberEditor(Integer.class, true));
    }
        
    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }
    
	@RequestMapping(value = "/move", method = RequestMethod.POST)
	public String moveToInterview(@RequestParam String applicationId, @Valid @ModelAttribute("interview") Interview interview, BindingResult bindingResult) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (bindingResult.hasErrors()) {
			return INTERVIEWERS_SECTION;
		}
		interviewService.moveApplicationToInterview(interview, applicationForm);
		return "/private/common/ajax_OK";
	}
	
	private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }   
}
