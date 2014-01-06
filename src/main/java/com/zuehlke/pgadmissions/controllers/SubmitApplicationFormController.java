package com.zuehlke.pgadmissions.controllers;

import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProgramException;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.StageDurationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = { "/submit", "application" })
public class SubmitApplicationFormController {

    private final Logger log = LoggerFactory.getLogger(SubmitApplicationFormController.class);

    private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "/private/pgStudents/form/main_application_page";

    private static final String VIEW_APPLICATION_STAFF_VIEW_NAME = "/private/staff/application/main_application_page";

    private static final String VIEW_APPLICATION_INTERNAL_PLAIN_VIEW_NAME = "/private/staff/application/main_application_page_without_headers";

    private final ApplicationFormValidator applicationFormValidator;

    private final StageDurationService stageDurationService;

    private final ApplicationsService applicationService;

    private final EventFactory eventFactory;

    private final UserService userService;
    
    private final ApplicationFormUserRoleService applicationFormUserRoleService;
    
    private final ContentAccessProvider contentAccessProvider;

    public SubmitApplicationFormController() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public SubmitApplicationFormController(ApplicationsService applicationService, UserService userService, ApplicationFormValidator applicationFormValidator,
            StageDurationService stageDurationService, EventFactory eventFactory, ApplicationFormUserRoleService applicationFormUserRoleService, 
            ContentAccessProvider contentAccessProvider) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.applicationFormValidator = applicationFormValidator;
        this.stageDurationService = stageDurationService;
        this.eventFactory = eventFactory;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.contentAccessProvider = contentAccessProvider;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitApplication(@Valid @ModelAttribute ApplicationForm applicationForm, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            if (result.getFieldError("program") != null) {
                throw new CannotApplyToProgramException(applicationForm.getProgram());
            }
            return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
        }

        try {
            applicationForm.setIpAddressAsString(request.getRemoteAddr());
        } catch (UnknownHostException e) {
            log.error("Error while setting ip address of: " + request.getRemoteAddr(), e);
        }
        
        applicationForm.setStatus(ApplicationFormStatus.VALIDATION);
        applicationForm.setSubmittedDate(DateUtils.truncateToDay(new Date()));
        assignValidationDueDate(applicationForm);
        applicationForm.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.VALIDATION));
        assignBatchDeadline(applicationForm);
        applicationService.sendSubmissionConfirmationToApplicant(applicationForm);
        applicationFormUserRoleService.applicationSubmitted(applicationForm);
        return "redirect:/applications?messageCode=application.submitted&application=" + applicationForm.getApplicationNumber();
    }

    public void assignValidationDueDate(ApplicationForm applicationForm) {
        StageDuration validationDuration = stageDurationService.getByStatus(ApplicationFormStatus.VALIDATION);
        Date dueDate = DateUtils.addWorkingDaysInMinutes(applicationForm.getSubmittedDate(), validationDuration.getDurationInMinutes());
        applicationForm.setDueDate(dueDate);
    }

    public void assignBatchDeadline(ApplicationForm applicationForm) {
        if (applicationForm.getProject() != null) {
            applicationForm.setBatchDeadline(applicationForm.getProject().getClosingDate());
        } else {
            applicationForm.setBatchDeadline(applicationService.getBatchDeadlineForApplication(applicationForm));
        }
    }

    @InitBinder("applicationForm")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(applicationFormValidator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getApplicationView(HttpServletRequest request, @ModelAttribute ApplicationForm applicationForm) {
        RegisteredUser user = getCurrentUser();    
        applicationFormUserRoleService.applicationViewed(applicationForm, user);
        
        if (contentAccessProvider.checkCanEditAsApplicant(applicationForm, user)) {
            return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
        }

        if (request != null && request.getParameter("embeddedApplication") != null && request.getParameter("embeddedApplication").equals("true")) {
            return VIEW_APPLICATION_INTERNAL_PLAIN_VIEW_NAME;
        }

        if (BooleanUtils.isTrue(contentAccessProvider.checkActionAvailable(applicationForm, user, ApplicationFormAction.VIEW_EDIT))) {
            return "redirect:/editApplicationFormAsProgrammeAdmin?applicationId=" + applicationForm.getApplicationNumber();
        }

        return VIEW_APPLICATION_STAFF_VIEW_NAME;
    }

    protected RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationService.getApplicationByApplicationNumber(applicationId);
        contentAccessProvider.validateAction(applicationForm, getCurrentUser(), ApplicationFormAction.VIEW);  
        return applicationForm;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        return contentAccessProvider.getApplicationDescriptorForUser(getApplicationForm(applicationId), getCurrentUser());
    }
    
}