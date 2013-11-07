package com.zuehlke.pgadmissions.controllers;

import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.zuehlke.pgadmissions.components.ApplicationDescriptorProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProgramException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
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

    private final ApplicationFormAccessService accessService;

    private final ApplicationDescriptorProvider applicationDescriptorProvider;

    public SubmitApplicationFormController() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public SubmitApplicationFormController(ApplicationsService applicationService, UserService userService, ApplicationFormValidator applicationFormValidator,
                    StageDurationService stageDurationService, EventFactory eventFactory, final ApplicationFormAccessService accessService,
                    ApplicationDescriptorProvider applicationDescriptorProvider) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.applicationFormValidator = applicationFormValidator;
        this.stageDurationService = stageDurationService;
        this.eventFactory = eventFactory;
        this.accessService = accessService;
        this.applicationDescriptorProvider = applicationDescriptorProvider;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitApplication(@Valid ApplicationForm applicationForm, BindingResult result, HttpServletRequest request) {
        if (!getCurrentUser().getId().equals(applicationForm.getApplicant().getId()) || applicationForm.isDecided()) {
            throw new InsufficientApplicationFormPrivilegesException(applicationForm.getApplicationNumber());
        }

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
        
        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
        accessService.updateAccessTimestamp(applicationForm, getCurrentUser(), new Date());
        applicationForm.setLastUpdated(applicationForm.getSubmittedDate());
        
        applicationForm.setStatus(ApplicationFormStatus.VALIDATION);
        applicationForm.setSubmittedDate(DateUtils.truncateToDay(new Date()));
        assignValidationDueDate(applicationForm);
        applicationForm.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.VALIDATION));
        assignBatchDeadline(applicationForm);
        applicationService.sendSubmissionConfirmationToApplicant(applicationForm);
        accessService.applicationSubmitted(applicationForm);
        accessService.registerApplicationUpdate(applicationForm, new Date(), ApplicationUpdateScope.ALL_USERS);
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
        
        accessService.updateAccessTimestamp(applicationForm, user, new Date());
        
        accessService.deregisterApplicationUpdate(applicationForm, user);
        if (user.canEditAsApplicant(applicationForm)) {
            return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
        }

        if (request != null && request.getParameter("embeddedApplication") != null && request.getParameter("embeddedApplication").equals("true")) {
            return VIEW_APPLICATION_INTERNAL_PLAIN_VIEW_NAME;
        }

        if (user.canEditAsAdministrator(applicationForm)) {
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
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!getCurrentUser().canSee(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        return applicationForm;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return applicationDescriptorProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }
}