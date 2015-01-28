package com.zuehlke.pgadmissions.mvc.controllers;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("api/pgadmissions")
public class LegacyController {

    private static Logger LOGGER = LoggerFactory.getLogger(LegacyController.class);

    @Value("${application.url}")
    private String applicationUrl;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProgramService programService;

    @RequestMapping(method = RequestMethod.GET)
    public void redirect(HttpServletRequest request, HttpServletResponse response) {
        String redirectionPrefix = applicationUrl + "/" + Constants.ANGULAR_HASH + "/";
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        try {

            String redirect;

            if (request.getParameter("advert") != null) {
                Advert advert = advertService.getById(Integer.parseInt(request.getParameter("advert")));
                if (advert.isProgramAdvert()) {
                    redirect = redirectionPrefix + "?program=" + advert.getProgram().getId();
                } else {
                    redirect = redirectionPrefix + "?project=" + advert.getProject().getId();
                }
            } else if (request.getParameter("program") != null) {
                Integer programId = programService.getProgramByImportedCode(request.getParameter("program")).getId();
                redirect = redirectionPrefix + "?program=" + programId;
            } else {
                redirect = redirectionPrefix;
            }

            response.setHeader("Location", redirect);
        } catch (Exception e) {
            LOGGER.error("Redirection error", e);
            response.setHeader("Location", redirectionPrefix);
        }
    }

}
