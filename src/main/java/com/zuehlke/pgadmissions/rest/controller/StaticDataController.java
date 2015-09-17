package com.zuehlke.pgadmissions.rest.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.services.StaticDataService;

@RestController
@RequestMapping("/api/static")
public class StaticDataController {

    @Inject
    private StaticDataService staticDataService;

    @Cacheable("staticData")
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> getStaticData() {
        Map<String, Object> staticData = Maps.newHashMap();
        staticData.putAll(staticDataService.getActions());
        staticData.putAll(staticDataService.getStates());
        staticData.putAll(staticDataService.getStateGroups());
        staticData.putAll(staticDataService.getRoles());
        staticData.putAll(staticDataService.getDomiciles());
        staticData.putAll(staticDataService.getPerformanceIndicatorGroups());
        staticData.putAll(staticDataService.getSimpleProperties());
        staticData.putAll(staticDataService.getFilterProperties());
        staticData.putAll(staticDataService.getConfigurations());
        staticData.putAll(staticDataService.getOpportunityCategories());
        staticData.putAll(staticDataService.getActionConditions());
        staticData.putAll(staticDataService.getRequiredSections());
        staticData.putAll(staticDataService.getWorkflowConstraints());
        return staticData;
    }

    @RequestMapping(method = RequestMethod.GET, params = "institutionId")
    public Map<String, Object> getInstitutionStaticData(@RequestParam Integer institutionId) {
        return staticDataService.getInstitutionData(institutionId);
    }

}
