package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WithdrawService;
import com.zuehlke.pgadmissions.services.WorkflowService;

@Controller
@RequestMapping("/withdraw")
public class WithdrawController {

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private ActionService actionService;

    @RequestMapping(method = RequestMethod.POST)
    public String withdrawApplicationAndGetApplicationList(ModelMap modelMap) {
        Application applicationForm = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, SystemAction.APPLICATION_WITHDRAW);

        withdrawService.withdrawApplication(applicationForm);
        withdrawService.sendToPortico(applicationForm);
        workflowService.applicationUpdated(applicationForm, user);
        return "redirect:/applications?messageCode=application.withdrawn&application=" + applicationForm.getApplicationNumber();
    }

    protected User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute
    public Application getApplicationForm(@RequestParam String applicationId) {
        Application applicationForm = applicationService.getByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new ResourceNotFoundException(applicationId);
        }
        return applicationForm;
    }

    @ModelAttribute("user")
    public User getUser() {
        return getCurrentUser();
    }
}