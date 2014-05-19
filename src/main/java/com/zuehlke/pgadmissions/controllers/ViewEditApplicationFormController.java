package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = "/application")
public class ViewEditApplicationFormController {

    @Autowired
    private ApplicationFormValidator applicationFormValidator;

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    @InitBinder("applicationForm")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(applicationFormValidator);
    }

    @RequestMapping(method = RequestMethod.GET, value = "application")
    public String getApplicationView(HttpServletRequest request, @RequestParam String applicationNumber) {
        User user = userService.getCurrentUser();

        ApplicationForm application = applicationFormService.getByApplicationNumber(applicationNumber);
        applicationFormService.openApplicationForView(application, user);
        if (request != null && request.getParameter("embeddedApplication") != null && request.getParameter("embeddedApplication").equals("true")) {
            return TemplateLocation.APPLICATION_STAFF_EMBEDDED_FORM;
        }
        return TemplateLocation.APPLICATION_STAFF_FORM;

    }

}
