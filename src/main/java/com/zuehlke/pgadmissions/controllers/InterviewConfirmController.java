package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_INTERVIEW_TIME;

import java.util.Date;

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
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
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

    private final ApplicationFormAccessService accessService;

    private final ActionsProvider actionsProvider;
    
    private final InterviewConfirmDTOValidator interviewConfirmDTOValidator;

    public InterviewConfirmController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public InterviewConfirmController(ApplicationsService applicationsService, UserService userService, InterviewService interviewService,
            final ApplicationFormAccessService accessService, ActionsProvider actionsProvider, InterviewConfirmDTOValidator interviewConfirmDTOValidator) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.interviewService = interviewService;
        this.accessService = accessService;
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

    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId) {
        ApplicationForm application = getApplicationForm(applicationId);
        return actionsProvider.calculateActions(userService.getCurrentUser(), application);
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
        actionsProvider.validateAction(applicationForm, user, CONFIRM_INTERVIEW_TIME);
        return INTERVIEW_CONFIRM_PAGE;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String submitInterviewConfirmation(
            @ModelAttribute(value = "interviewConfirmDTO") @Valid InterviewConfirmDTO interviewConfirmDTO, BindingResult result, ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, CONFIRM_INTERVIEW_TIME);
        
        if (result.hasErrors()) {
            return INTERVIEW_CONFIRM_PAGE;
        }
        Interview interview = applicationForm.getLatestInterview();
        interviewService.confirmInterview(user, interview, interviewConfirmDTO);

        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
        applicationsService.save(applicationForm);

        accessService.updateAccessTimestamp(applicationForm, user, new Date());
        return "redirect:/applications?messageCode=interview.confirm&application=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    public String restartInterviewScheduling(@ModelAttribute ApplicationForm applicationForm) {
        return "redirect:/interview/moveToInterview?applicationId=" + applicationForm.getApplicationNumber();
    }
}
