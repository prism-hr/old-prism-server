package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Controller
@RequestMapping("/opportunities")
public class AdvertsController {

    private static final String RELATED_OPPORTUNITIES_VIEW = "private/prospectus/opportunities";
    private final AdvertService advertService;
    private ApplicationService applicationsService;
    private ProgramDAO programDAO;

    public AdvertsController() {
        this(null, null, null);
    }

    @Autowired
    public AdvertsController(final AdvertService advertService, final ApplicationService applicationsService, final ProgramDAO programDAO) {
        this.advertService = advertService;
        this.applicationsService = applicationsService;
        this.programDAO = programDAO;
    }

    @RequestMapping(value = "/related", method = RequestMethod.GET)
    public String getRelatedOpportunities(@RequestParam String id, ModelMap modelMap) {
        modelMap.put("applicationForm", applicationsService.getByApplicationNumber(id));
        return RELATED_OPPORTUNITIES_VIEW;
    }

    @RequestMapping(value = "/embedded", method = RequestMethod.GET)
    @ResponseBody
    public String getOpportunities(@RequestParam(required = false) OpportunityListType feedKey, @RequestParam(required = false) String feedKeyValue,
            HttpServletRequest request) { 
        List<AdvertDTO> adverts = advertService.getAdvertFeed(feedKey, feedKeyValue, getAdvertIdFromRequestOrSavedRequest(request));
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
    
    private String getAdvertIdFromRequestOrSavedRequest(HttpServletRequest request) {
        List<String> possibleRequestParameters = Arrays.asList("advert", "project", "program");
        List<String> advertSynonyms = Arrays.asList("advert", "project");
        
        String found = null;
        
        for (String parameter : possibleRequestParameters) {
            found = request.getParameter(parameter);
            if (!StringUtils.isBlank(found)) {
                if (advertSynonyms.contains(parameter)) {
                    return found;
                }
                return programDAO.getProgramIdByCode(found);
            }
        }
        
        DefaultSavedRequest savedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        
        if (savedRequest != null) {
            for (String parameter : possibleRequestParameters) {
                String[] values = savedRequest.getParameterValues(parameter);
                if (!ArrayUtils.isEmpty(values)) {
                    found = values[0];
                    if (!StringUtils.isBlank(found)) {
                        if (advertSynonyms.contains(parameter)) {
                            return found;
                        }
                        return programDAO.getProgramIdByCode(found);
                    }
                }
            }
        }
        
        return null;
    }

}
