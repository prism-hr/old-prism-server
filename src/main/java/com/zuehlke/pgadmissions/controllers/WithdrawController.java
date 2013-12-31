package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.security.ActionsProvider;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.WithdrawService;

@Controller
@RequestMapping("/withdraw")
public class WithdrawController {

    private final WithdrawService withdrawService;

    private final ApplicationsService applicationService;

    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    private final ActionsProvider actionsProvider;

    public WithdrawController() {
        this(null, null, null, null);
    }

    @Autowired
    public WithdrawController(ApplicationsService applicationService, WithdrawService withdrawService,
            ApplicationFormUserRoleService applicationFormUserRoleService, ActionsProvider actionsProvider) {
        this.applicationService = applicationService;
        this.withdrawService = withdrawService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.actionsProvider = actionsProvider;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String withdrawApplicationAndGetApplicationList(ModelMap modelMap, @ModelAttribute ApplicationForm applicationForm) {
        withdrawService.withdrawApplication(applicationForm);
        withdrawService.sendToPortico(applicationForm);
        applicationFormUserRoleService.moveToApprovedOrRejectedOrWithdrawn(applicationForm, getCurrentUser());
        return "redirect:/applications?messageCode=application.withdrawn&application=" + applicationForm.getApplicationNumber();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationService.getApplicationByApplicationNumber(applicationId);
        actionsProvider.validateAction(applicationForm, getCurrentUser(), ApplicationFormAction.WITHDRAW);
        return applicationForm;
    }

    @ModelAttribute("user")
    public RegisteredUser getCurrentUser() {
        return getCurrentUser();
    }
    
}