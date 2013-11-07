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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewConfirmDTOValidator;

@Controller
@RequestMapping(value = { "/interviewConfirm" })
public class InterviewConfirmController {

    private static final String INTERVIEW_CONFIRM_PAGE = "private/staff/interviewers/interview_confirm";

    private final ApplicationsService applicationsService;

    private final UserService userService;

    private final InterviewService interviewService;

    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    private final ActionsProvider actionsProvider;

    private final InterviewConfirmDTOValidator interviewConfirmDTOValidator;


    public InterviewConfirmController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public InterviewConfirmController(ApplicationsService applicationsService, UserService userService, InterviewService interviewService,
                    final ApplicationFormUserRoleService applicationFormUserRoleService, ActionsProvider actionsProvider,
                    InterviewConfirmDTOValidator interviewConfirmDTOValidator) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.interviewService = interviewService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.actionsProvider = actionsProvider;
        this.interviewConfirmDTOValidator = interviewConfirmDTOValidator;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
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
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.CONFIRM_INTERVIEW_ARRANGEMENTS);
        
        InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
        interviewConfirmDTO.setFurtherDetails(applicationForm.getLatestInterview().getFurtherDetails());
        interviewConfirmDTO.setFurtherInterviewerDetails(applicationForm.getLatestInterview().getFurtherInterviewerDetails());
        interviewConfirmDTO.setLocationUrl(applicationForm.getLatestInterview().getLocationURL());
        modelMap.put("interviewConfirmDTO", interviewConfirmDTO);
        applicationFormUserRoleService.deregisterApplicationUpdate(applicationForm, user);
        return INTERVIEW_CONFIRM_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitInterviewConfirmation(@ModelAttribute(value = "interviewConfirmDTO") @Valid InterviewConfirmDTO interviewConfirmDTO,
                    BindingResult result, ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.CONFIRM_INTERVIEW_ARRANGEMENTS);

        if (result.hasErrors()) {
            return INTERVIEW_CONFIRM_PAGE;
        }
        
        Interview interview = applicationForm.getLatestInterview();
        interviewService.confirmInterview(user, interview, interviewConfirmDTO);
        applicationsService.save(applicationForm);
        
        return "redirect:/applications?messageCode=interview.confirm&application=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    public String restartInterviewScheduling(@ModelAttribute ApplicationForm applicationForm) {
        return "redirect:/interview/moveToInterview?applicationId=" + applicationForm.getApplicationNumber();
    }
}
