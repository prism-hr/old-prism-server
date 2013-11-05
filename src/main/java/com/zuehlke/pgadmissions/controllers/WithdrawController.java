package com.zuehlke.pgadmissions.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WithdrawService;

@Controller
@RequestMapping("/withdraw")
public class WithdrawController {

    private final WithdrawService withdrawService;

    private final ApplicationsService applicationService;

    private final UserService userService;

    private final ApplicationFormAccessService accessService;

    private final ActionsProvider actionsProvider;

    public WithdrawController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public WithdrawController(ApplicationsService applicationService, UserService userService, WithdrawService withdrawService,
            ApplicationFormAccessService accessService, ActionsProvider actionsProvider) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.withdrawService = withdrawService;
        this.accessService = accessService;
        this.actionsProvider = actionsProvider;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String withdrawApplicationAndGetApplicationList(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.WITHDRAW);

        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
        accessService.updateAccessTimestamp(applicationForm, getUser(), new Date());
        withdrawService.withdrawApplication(applicationForm);
        withdrawService.sendToPortico(applicationForm);
        return "redirect:/applications?messageCode=application.withdrawn&application=" + applicationForm.getApplicationNumber();
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
        return applicationForm;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }
}