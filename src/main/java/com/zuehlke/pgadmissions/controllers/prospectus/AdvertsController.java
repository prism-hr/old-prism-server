package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/opportunities")
public class AdvertsController {

    private static final String RELATED_OPPORTUNITIES_VIEW = "private/prospectus/opportunities";
    private final AdvertService advertService;
    private ApplicationsService applicationsService;

    public AdvertsController() {
        this(null, null);
    }

    @Autowired
    public AdvertsController(final AdvertService advertService, final ApplicationsService applicationsService) {
        this.advertService = advertService;
        this.applicationsService = applicationsService;
    }

    @RequestMapping(value = "related", method = RequestMethod.GET)
    public String getRelatedOpportunities(@RequestParam String id, ModelMap modelMap) {
        modelMap.put("applicationForm", applicationsService.getApplicationByApplicationNumber(id));
        return RELATED_OPPORTUNITIES_VIEW;
    }

    @RequestMapping(value = "embedded", method = RequestMethod.GET)
    @ResponseBody
    public String getOpportunities(@RequestParam(required = false) OpportunityListType feedKey, @RequestParam(required = false) String feedKeyValue,
            HttpServletRequest request) {
        List<AdvertDTO> adverts = advertService.getAdvertFeed(feedKey, feedKeyValue, request);
        Map<String, Object> map = Maps.newHashMap();
        map.put("adverts", adverts);
        return new Gson().toJson(map);
    }

    @RequestMapping(value = "/standaloneOpportunities", method = RequestMethod.GET)
    public String getStandaloneOpportunities(@RequestParam(required = false) OpportunityListType feedKey, @RequestParam(required = false) String feedKeyValue,
            ModelMap model) {
        if (!(feedKey == null || feedKeyValue == null)) {
            model.put("feedKey", feedKey);
            model.put("feedKeyValue", feedKeyValue);
        }
        model.put("shouldOpenNewTab", "true");
        return "public/login/standalone";
    }

}
