package com.zuehlke.pgadmissions.legacy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ProgramService;

@Controller
@RequestMapping("api/pgadmissions")
public class RedirectionController {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProgramService programService;

    @RequestMapping(method = RequestMethod.GET)
    public String redirect(@RequestParam String originalUrl) {
        try {
            String originalQuery = originalUrl.substring(originalUrl.lastIndexOf("?"));
            String originalParameterPairs[] = originalQuery.split("&");

            Map<String, String> originalParameters = Maps.newHashMap();
            for (String originalParameterPair : originalParameterPairs) {
                String[] keyValue = originalParameterPair.split("=");
                originalParameters.put(keyValue[0], keyValue[1]);
            }

            String redirect;
            if (originalParameters.containsKey("applicationId")) {
                Application application = applicationService.getByLegacyCode(originalParameters.get("applicationId"));
                redirect = "redirect: api/application/" + application.getId().toString();
            } else if (originalParameters.containsKey("advert")) {
                Advert advert = advertService.getById(Integer.parseInt(originalParameters.get("advert")));
                if (advert.isProgramAdvert()) {
                    redirect = "redirect:api/opportunities/?program[]=" + advert.getProgram().getId().toString();
                } else {
                    redirect = "redirect:api/opportunities/?project[]=" + advert.getProject().getId().toString();
                }
            } else if (originalParameters.containsKey("program")) {
                Integer programId = programService.getProgramByImportedCode(originalParameters.get("program")).getId();
                redirect = "redirect:api/opportunities/?program[]=" + programId.toString();
            } else {
                redirect = "redirect:api/applications";
            }

            if (originalParameters.containsKey("activationCode")) {
                redirect = redirect + "&activationCode=" + originalParameters.get("activationCode");
            }

            return redirect;
        } catch (Exception e) {
            return "redirect:api/applications";
        }
    }

}
