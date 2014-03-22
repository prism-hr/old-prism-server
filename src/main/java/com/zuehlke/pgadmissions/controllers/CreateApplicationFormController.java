package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/apply")
public class CreateApplicationFormController {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/new", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView createNewApplicationForm(@RequestParam(required = false) String program, @RequestParam(required = false) Integer advert) {
        return new ModelAndView(RedirectLocation.UPDATE_APPLICATION, "applicationId", applicationFormService.createOrGetUnsubmittedApplicationForm(
                userService.getCurrentUser(), program, advert).getApplicationNumber());
    }

}
