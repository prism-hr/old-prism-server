package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WithdrawService;

@Controller
@RequestMapping("/withdraw")
public class WithdrawController {

    private final WithdrawService withdrawService;

    private final ApplicationFormService applicationService;

    private final UserService userService;

    private final WorkflowService applicationFormUserRoleService;

    private final ActionService actionService;

    public WithdrawController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public WithdrawController(ApplicationFormService applicationService, UserService userService, WithdrawService withdrawService,
            WorkflowService applicationFormUserRoleService, ActionsProvider actionsProvider) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.withdrawService = withdrawService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.actionsProvider = actionsProvider;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String withdrawApplicationAndGetApplicationList(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionService.validateAction(applicationForm, user, ApplicationFormAction.WITHDRAW);

        withdrawService.withdrawApplication(applicationForm);
        withdrawService.sendToPortico(applicationForm);
        applicationFormUserRoleService.deleteApplicationActions(applicationForm);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
        return "redirect:/applications?messageCode=application.withdrawn&application=" + applicationForm.getApplicationNumber();
    }

    protected RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationService.getByApplicationNumber(applicationId);
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