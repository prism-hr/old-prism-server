package com.zuehlke.pgadmissions.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignInterviewersComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.InterviewConfirmDTOValidator;

@Controller
@RequestMapping(value = { "/interviewConfirm" })
public class InterviewConfirmController {
    // TODO interview details combined into one field (fix in ftl), fix tests

    private static final String INTERVIEW_CONFIRM_PAGE = "private/staff/interviewers/interview_confirm";

    @Autowired
    private ApplicationFormService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private InterviewConfirmDTOValidator interviewConfirmDTOValidator;



    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getByApplicationNumber(applicationId);
        if (application == null) {
            throw new ResourceNotFoundException(applicationId);
        }
        return application;
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute(value = "interviewConfirmDTO")
    public InterviewConfirmDTO getInterviewConfirmDTO() {
        return new InterviewConfirmDTO();
    }

    @InitBinder(value = "interviewConfirmDTO")
    public void registerInterviewConfirmDTOEditors(WebDataBinder binder) {
        binder.setValidator(interviewConfirmDTOValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getInterviewConfirmPage(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, ApplicationFormAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS);
        
        AssignInterviewersComment latestComment = (AssignInterviewersComment) applicationsService.getLatestStateChangeComment(applicationForm, ApplicationFormAction.APPLICATION_ASSIGN_INTERVIEWERS);
        
        InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
        
        interviewConfirmDTO.setInterviewInstructions(latestComment.getAppointmentInstructions());
        interviewConfirmDTO.setLocationUrl(latestComment.getLocationUrl());
        modelMap.put("interviewConfirmDTO", interviewConfirmDTO);
        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, user);
        return INTERVIEW_CONFIRM_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitInterviewConfirmation(@ModelAttribute(value = "interviewConfirmDTO") @Valid InterviewConfirmDTO interviewConfirmDTO,
                    BindingResult result, ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, ApplicationFormAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS);

        if (result.hasErrors()) {
            return INTERVIEW_CONFIRM_PAGE;
        }
        
        interviewService.confirmInterview(user, applicationForm, interviewConfirmDTO);
        applicationsService.save(applicationForm);
        
        return "redirect:/applications?messageCode=interview.confirm&application=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    public String restartInterviewScheduling(@ModelAttribute ApplicationForm applicationForm) {
        return "redirect:/interview/moveToInterview?applicationId=" + applicationForm.getApplicationNumber();
    }
}
