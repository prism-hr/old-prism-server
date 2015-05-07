package com.zuehlke.pgadmissions.mvc.controllers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.utils.PrismConstants;

@Controller
@RequestMapping("api/pgadmissions")
public class LegacyController {

    private static Logger LOGGER = LoggerFactory.getLogger(LegacyController.class);

    @Value("${application.url}")
    private String applicationUrl;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ProgramService programService;

    @RequestMapping(method = RequestMethod.GET)
    public void redirect(HttpServletRequest request, HttpServletResponse response) {
        String redirectionPrefix = applicationUrl + "/" + PrismConstants.ANGULAR_HASH + "/";
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        try {

            String redirect;

            Integer advertId = Ints.tryParse(Strings.nullToEmpty(request.getParameter("advert")));
            if (advertId != null) {
                Advert advert = advertService.getById(advertId);
                if (advert.isAdvertOfScope(PROGRAM)) {
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
