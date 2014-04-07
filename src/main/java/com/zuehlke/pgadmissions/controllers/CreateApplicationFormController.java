package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/apply")
public class CreateApplicationFormController {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programService;

    @RequestMapping(value = "/new", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView createNewApplicationForm(@RequestParam(value = "program", required = false) String programCode,
            @RequestParam(value = "advert", required = false) Integer advertId) {
        if (advertId == null && programCode != null) {
            Program program = programService.getProgramByCode(programCode);
            advertId = program.getId();
        }

        ApplicationForm application = applicationFormService.getOrCreateApplication(userService.getCurrentUser(), advertId);
        return new ModelAndView(RedirectLocation.UPDATE_APPLICATION, "applicationId", application.getApplicationNumber());
    }
}
