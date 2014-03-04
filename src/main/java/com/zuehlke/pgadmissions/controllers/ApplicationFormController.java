package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.services.ApplicationFormCreationService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

    private final ApplicationFormCreationService applicationFormCreationService;
    private final ProgramsService programsService;
    private final UserService userService;

    ApplicationFormController() {
        this(null, null, null);
    }

    @Autowired
    public ApplicationFormController(ApplicationFormCreationService applicationFormCreationService,
            ProgramsService programsService, UserService userService) {
        this.applicationFormCreationService = applicationFormCreationService;
        this.programsService = programsService;
        this.userService = userService;
    }

    @RequestMapping(value = "/new", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView createNewApplicationForm(@RequestParam(required = false) String program, @RequestParam(required = false) Integer advert) {
        Advert advertObject = programsService.getValidProgramProjectAdvert(program, advert);
        ApplicationForm applicationForm = applicationFormCreationService.createOrGetUnsubmittedApplicationForm(userService.getCurrentUser(), advertObject);
        return new ModelAndView("redirect:/application", "applicationId", applicationForm.getApplicationNumber());
    }

}
