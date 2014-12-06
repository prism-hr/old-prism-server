package com.zuehlke.pgadmissions.legacy;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("api/pgadmissions")
public class RedirectionController {

    private static Logger log = LoggerFactory.getLogger(RedirectionController.class);

    @Value("${application.url}")
    private String applicationUrl;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProgramService programService;

    @RequestMapping(method = RequestMethod.GET)
    public void redirect(@RequestParam String originalUrl, HttpServletResponse response) {
        String redirectionPrefix = "redirect:" + applicationUrl + "/#/";
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        try {
            if (!originalUrl.contains("?")) {
                response.setHeader("Location", redirectionPrefix);
                return;
            }
            String originalQuery = originalUrl.substring(originalUrl.indexOf("?") + 1);
            String originalParameterPairs[] = originalQuery.split("&");

            String redirect;
            Map<String, String> originalParameters = Maps.newHashMap();
            for (String originalParameterPair : originalParameterPairs) {
                String[] keyValue = originalParameterPair.split("=");
                originalParameters.put(keyValue[0], keyValue[1]);
            }


            if (originalParameters.containsKey("activationCode") && originalParameters.containsKey("applicationId")) {
                Application application = applicationService.getByCodeLegacy(originalParameters.get("applicationId"));
                if (application == null) {
                    redirect = redirectionPrefix;
                } else {
                    redirect = redirectionPrefix + "activate?activationCode=" + originalParameters.get("activationCode");
                    redirect += "&resourceId=" + application.getId();
                    redirect += "&actionId=" + "APPLICATION_VIEW_EDIT";
                }
            } else if (originalParameters.containsKey("advert")) {
                Advert advert = advertService.getById(Integer.parseInt(originalParameters.get("advert")));
                if (advert.isProgramAdvert()) {
                    redirect = redirectionPrefix + "?program=" + advert.getProgram().getId();
                } else {
                    redirect = redirectionPrefix + "?project=" + advert.getProject().getId();
                }
            } else if (originalParameters.containsKey("program")) {
                Integer programId = programService.getProgramByImportedCode(originalParameters.get("program")).getId();
                redirect = redirectionPrefix + "?program=" + programId;
            } else {
                log.warn("Unexpected legacy URL: " + originalQuery);
                redirect = redirectionPrefix;
            }

            response.setHeader("Location", redirect);
        } catch (Exception e) {
            log.error("Redirection error", e);
            response.setHeader("Location", redirectionPrefix);
        }
    }

}
