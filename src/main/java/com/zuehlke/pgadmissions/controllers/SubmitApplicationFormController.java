package com.zuehlke.pgadmissions.controllers;

import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
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
    
    private final ActionsProvider actionsProvider;

    public SubmitApplicationFormController() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public SubmitApplicationFormController(ApplicationsService applicationService, UserService userService, ApplicationFormValidator applicationFormValidator,
            StageDurationService stageDurationService, EventFactory eventFactory, final ApplicationFormAccessService accessService, ActionsProvider actionsProvider) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.applicationFormValidator = applicationFormValidator;
        this.stageDurationService = stageDurationService;
        this.eventFactory = eventFactory;
        this.accessService = accessService;
        this.actionsProvider = actionsProvider;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitApplication(@Valid ApplicationForm applicationForm, BindingResult result, HttpServletRequest request) {
        if (!getCurrentUser().getId().equals(applicationForm.getApplicant().getId()) || applicationForm.isDecided()) {
            throw new InsufficientApplicationFormPrivilegesException(applicationForm.getApplicationNumber());
        }

        if (result.hasErrors()) {
            return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
        }

        try {
            applicationForm.setIpAddressAsString(request.getRemoteAddr());
        } catch (UnknownHostException e) {
            log.error("Error while setting ip address of: " + request.getRemoteAddr(), e);
        }

        //also batchDeadline needs to be set here once we got it working
        applicationForm.setStatus(ApplicationFormStatus.VALIDATION);
        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
        applicationForm.setSubmittedDate(new Date());
        calculateAndSetValidationDueDate(applicationForm);
        applicationForm.setLastUpdated(applicationForm.getSubmittedDate());
        applicationForm.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.VALIDATION));
        applicationService.sendSubmissionConfirmationToApplicant(applicationForm);
        accessService.updateAccessTimestamp(applicationForm, getCurrentUser(), new Date());
        return "redirect:/applications?messageCode=application.submitted&application=" + applicationForm.getApplicationNumber();
    }

    public void calculateAndSetValidationDueDate(ApplicationForm applicationForm) {
        DateTime dueDate = new DateTime(applicationForm.getSubmittedDate());
        StageDuration validationDuration = stageDurationService.getByStatus(ApplicationFormStatus.VALIDATION);
        dueDate = DateUtils.addWorkingDaysInMinutes(dueDate, validationDuration.getDurationInMinutes());
        applicationForm.setDueDate(dueDate.toDate());
    }

    @InitBinder("applicationForm")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(applicationFormValidator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getApplicationView(HttpServletRequest request, @ModelAttribute ApplicationForm applicationForm) {
        accessService.updateAccessTimestamp(applicationForm, getCurrentUser(), new Date());
        if (getCurrentUser().canEditAsApplicant(applicationForm)) {
            return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
        }

        if (request != null && request.getParameter("embeddedApplication") != null && request.getParameter("embeddedApplication").equals("true")) {
            return VIEW_APPLICATION_INTERNAL_PLAIN_VIEW_NAME;
        }

        if (getCurrentUser().canEditAsAdministrator(applicationForm)) {
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
    
    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return actionsProvider.calculateActions(getUser(), application);
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }
}
