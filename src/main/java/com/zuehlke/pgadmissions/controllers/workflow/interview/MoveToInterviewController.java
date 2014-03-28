package com.zuehlke.pgadmissions.controllers.workflow.interview;

import java.util.Date;
import java.util.List;

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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignInterviewersComment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.TimeZoneList;

@Controller
@RequestMapping("/interview")
public class MoveToInterviewController {
    // TODO change interview to interviewComment, fix other mentioned problems and tests

    private static final String INTERVIEWERS_SECTION = "/private/staff/interviewers/interviewer_section";
    private static final String INTERVIEW_PAGE = "/private/staff/interviewers/interview_details";
    
    @Autowired
    private ApplicationsService applicationsService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private DatePropertyEditor datePropertyEditor;

    @Autowired
    private InterviewTimeslotsPropertyEditor interviewTimeslotsPropertyEditor;

    @Autowired
    private ActionsProvider actionsProvider;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }

    @RequestMapping(method = RequestMethod.GET, value = "moveToInterview")
    public String getInterviewDetailsPage(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.ASSIGN_INTERVIEWERS);
        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, user);
        return INTERVIEW_PAGE;
    }

    @RequestMapping(method = RequestMethod.GET, value = "interviewers_section")
    public String getInterviewersSection() {
        return INTERVIEWERS_SECTION;

    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public String moveToInterview(@Valid @ModelAttribute("interview") AssignInterviewersComment interviewComment, BindingResult bindingResult, ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.ASSIGN_INTERVIEWERS);

        if (bindingResult.hasErrors()) {
            return INTERVIEWERS_SECTION;
        }

        interviewService.moveApplicationToInterview(getUser(), interviewComment, applicationForm);

        // TODO check if the user is participant (applicant or interviewer) so he can be redirected to voting page directly
        // if (interview.isParticipant(getUser())) {
        // modelMap.addAttribute("message", "redirectToVote");
        // return "/private/common/simpleResponse";
        // }
        return "/private/common/ajax_OK";
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("usersInterestedInApplication")
    public List<RegisteredUser> getUsersInterestedInApplication(@RequestParam String applicationId) {
        // FIXME isInterviewerInInterview method has been removed from RegisteredUser class, provide this information in other way (by moving the method into
        // aservice, or this method can return a map)
        return applicationFormUserRoleService.getUsersInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("usersPotentiallyInterestedInApplication")
    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication(@RequestParam String applicationId) {
        return applicationFormUserRoleService.getUsersPotentiallyInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("interview")
    public AssignInterviewersComment getInterviewComment(@RequestParam String applicationId) {
        AssignInterviewersComment interviewComment = new AssignInterviewersComment();

        List<RegisteredUser> usersInterestedInApplication = getUsersInterestedInApplication(applicationId);

        if (usersInterestedInApplication != null) {
            for (RegisteredUser registeredUser : getUsersInterestedInApplication(applicationId)) {
                CommentAssignedUser interviewer = new CommentAssignedUser();
                interviewer.setUser(registeredUser);
                interviewComment.getAssignedUsers().add(interviewer);
            }
        }

        return interviewComment;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @InitBinder("interview")
    public void registerValidatorAndPropertyEditor(WebDataBinder binder) {
//        binder.setValidator(interviewValidator);
        // FIXME consider creating CommentAssignedUserPropertyEditor
//        binder.registerCustomEditor(Interviewer.class, interviewerPropertyEditor);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(null, "timeslots", interviewTimeslotsPropertyEditor);
        binder.registerCustomEditor(null, "duration", new CustomNumberEditor(Integer.class, true));
    }

    @ModelAttribute("availableTimeZones")
    public TimeZoneList getAvailableTimeZones() {
        return TimeZoneList.getInstance();
    }

}