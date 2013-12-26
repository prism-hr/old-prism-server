package com.zuehlke.pgadmissions.controllers.workflow.interview;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/interview")
public class CreateNewInterviewerController {

    private static final String CREATE_INTERVIEWER_SECTION = "/private/staff/interviewers/create_interviewer_section";
    private static final String JSON_VIEW = "/private/staff/reviewer/reviewer_json";
    private final ApplicationFormUserRoleService applicationFormUserRoleService;
    private final ApplicationsService applicationsService;
    private final NewUserByAdminValidator interviewerValidator;

    CreateNewInterviewerController() {
        this(null, null, null);
    }

    @Autowired
    public CreateNewInterviewerController(ApplicationsService applicationsService, ApplicationFormUserRoleService applicationFormUserRoleService, NewUserByAdminValidator interviewerValidator) {
        this.applicationsService = applicationsService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.interviewerValidator = interviewerValidator;
    }

    @RequestMapping(value = "/createInterviewer", method = RequestMethod.POST)
    public ModelAndView createNewInterviewerUser(@Valid @ModelAttribute("interviewer") RegisteredUser suggestedNewInterviewerUser, BindingResult bindingResult,
            @ModelAttribute("applicationForm") ApplicationForm applicationForm) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView(CREATE_INTERVIEWER_SECTION);
        }
        
		ModelAndView modelAndView = new ModelAndView(JSON_VIEW);
		RegisteredUser userToAssign = applicationFormUserRoleService.createRegisteredUser(suggestedNewInterviewerUser.getFirstName(), suggestedNewInterviewerUser.getLastName(), suggestedNewInterviewerUser.getEmail());
		modelAndView.getModel().put("isNew", applicationFormUserRoleService.isNewlyCreatedUser(userToAssign));		
		modelAndView.getModel().put("user", userToAssign);
		return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "create_interviewer_section")
    public String getCreateInterviewerSection() {
        return CREATE_INTERVIEWER_SECTION;
    }

    @ModelAttribute("interviewer")
    public RegisteredUser getInterviewer() {
        return new RegisteredUser();
    }

    @InitBinder(value = "interviewer")
    public void registerInterviewerValidators(WebDataBinder binder) {
        binder.setValidator(interviewerValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
    	ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }
}