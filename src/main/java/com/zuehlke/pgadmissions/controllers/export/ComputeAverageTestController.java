package com.zuehlke.pgadmissions.controllers.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.services.ApplicantRatingService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/obliczSrednie")
public class ComputeAverageTestController {

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private ApplicantRatingService applicantRatingService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String sendMails() {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber("DDNCIVSUSR09-2013-000030");
        for (Interview interview : application.getInterviews()) {
            applicantRatingService.computeAverageRating(interview);
        }
        return "Done!";
    }
}
