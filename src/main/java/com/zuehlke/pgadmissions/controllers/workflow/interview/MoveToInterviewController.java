package com.zuehlke.pgadmissions.controllers.workflow.interview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.TimeZoneList;
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
            InterviewValidator interviewValidator, InterviewerPropertyEditor interviewerPropertyEditor, DatePropertyEditor datePropertyEditor,
            InterviewTimeslotsPropertyEditor interviewTimeslotsPropertyEditor) {
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
        RegisteredUser currentUser = userService.getCurrentUser();
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.hasAdminRightsOnApplication(application) && !currentUser.isApplicationAdministrator(application)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        return application;
    }

    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId) {
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.calculateActions(getUser(), application);
    }

    @ModelAttribute("nominatedSupervisors")
    public List<RegisteredUser> getNominatedSupervisors(@RequestParam String applicationId) {
        List<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        if (applicationForm.getLatestInterview() == null) {
            nominatedSupervisors.addAll(getOrCreateRegisteredUsersForForm(applicationForm));
        }
        return nominatedSupervisors;
    }

    @ModelAttribute("programmeInterviewers")
    public List<RegisteredUser> getProgrammeInterviewers(@RequestParam String applicationId) {
        List<RegisteredUser> programmeInterviewers = getApplicationForm(applicationId).getProgram().getInterviewers();
        List<RegisteredUser> nominatedSupervisors = getNominatedSupervisors(applicationId);
        programmeInterviewers.removeAll(nominatedSupervisors);
        return programmeInterviewers;
    }

    @ModelAttribute("previousInterviewers")
    public List<RegisteredUser> getPreviousInterviewersAndReviewersWillingToInterview(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        List<RegisteredUser> previousInterviewersOfProgram = userService.getAllPreviousInterviewersOfProgram(applicationForm.getProgram());

        List<RegisteredUser> reviewersWillingToInterview = applicationForm.getReviewersWillingToInterview();
        for (RegisteredUser registeredUser : reviewersWillingToInterview) {
            if (!listContainsId(registeredUser, applicationForm.getProgram().getInterviewers())
                    && !listContainsId(registeredUser, previousInterviewersOfProgram)) {
                previousInterviewersOfProgram.add(registeredUser);
            }
        }
        previousInterviewersOfProgram.removeAll(getNominatedSupervisors(applicationId));
        previousInterviewersOfProgram.removeAll(getProgrammeInterviewers(applicationId));
        return previousInterviewersOfProgram;

    }

    @ModelAttribute("interview")
    public Interview getInterview(@RequestParam String applicationId) {
        Interview interview = new Interview();
        ApplicationForm applicationForm = getApplicationForm((String) applicationId);
        Interview latestInterview = applicationForm.getLatestInterview();
        if (latestInterview != null) {
            interview.setInterviewers(latestInterview.getInterviewers());
        }
        Set<RegisteredUser> defaultInterviewers = Sets.newLinkedHashSet(applicationForm.getReviewersWillingToInterview());
        if (applicationForm.getApplicationAdministrator() != null) {
            defaultInterviewers.add(applicationForm.getApplicationAdministrator());
        }
        for (RegisteredUser registeredUser : defaultInterviewers) {
            if (!registeredUser.isInterviewerInInterview(interview)) {
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
    public String moveToInterview(@RequestParam String applicationId, @Valid @ModelAttribute("interview") Interview interview, BindingResult bindingResult,
            ModelMap model) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        if (bindingResult.hasErrors()) {
            return INTERVIEWERS_SECTION;
        }
        interviewService.moveApplicationToInterview(interview, applicationForm);
        if (interview.isParticipant(getUser())) {
            model.addAttribute("message", "redirectToVote");
            return "/private/common/simpleResponse";
        }
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

    private List<RegisteredUser> getOrCreateRegisteredUsersForForm(ApplicationForm applicationForm) {
        List<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
        List<SuggestedSupervisor> suggestedSupervisors = applicationForm.getProgrammeDetails().getSuggestedSupervisors();
        for (SuggestedSupervisor suggestedSupervisor : suggestedSupervisors) {
            nominatedSupervisors.add(findOrCreateRegisterUserFromSuggestedSupervisorForForm(suggestedSupervisor, applicationForm));
        }
        return nominatedSupervisors;
    }

    private RegisteredUser findOrCreateRegisterUserFromSuggestedSupervisorForForm(SuggestedSupervisor suggestedSupervisor, ApplicationForm applicationForm) {
        String supervisorEmail = suggestedSupervisor.getEmail();
        RegisteredUser possibleUser = userService.getUserByEmailIncludingDisabledAccounts(supervisorEmail);
        if (possibleUser == null) {
            possibleUser = userService.createNewUserInRole(suggestedSupervisor.getFirstname(), suggestedSupervisor.getLastname(), supervisorEmail, null,
                    applicationForm, Authority.REVIEWER);
        }
        return possibleUser;
    }

    @ModelAttribute("availableTimeZones")
    public TimeZoneList getAvailableTimeZones() {
        return TimeZoneList.getInstance();
    }

}
