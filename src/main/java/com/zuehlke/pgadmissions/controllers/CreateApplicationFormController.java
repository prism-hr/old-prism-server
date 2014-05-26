package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/apply")
public class CreateApplicationFormController {

    @Autowired
    private ApplicationService applicationFormService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programService;

    @RequestMapping(value = "/new", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView createNewApplicationForm(@RequestParam(value = "program", required = false) String programCode,
            @RequestParam(value = "advert", required = false) Integer advertId) throws Exception {
        if (advertId == null && programCode != null) {
            Program program = programService.getProgramByCode(programCode);
            advertId = program.getId();
        }

        Application application = applicationFormService.getOrCreate(userService.getCurrentUser(), advertId);
        return new ModelAndView(RedirectLocation.UPDATE_APPLICATION, "applicationId", application.getApplicationNumber());
    }
}
