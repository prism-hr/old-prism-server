package com.zuehlke.pgadmissions.rest.controller;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.services.DepartmentService;
import com.zuehlke.pgadmissions.services.StaticDataService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/static")
public class StaticDataController {

    @Inject
    private DepartmentService departmentService;

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
        staticData.putAll(staticDataService.getInstitutionDomiciles());
        staticData.putAll(staticDataService.getPerformanceIndicatorGroups());
        staticData.putAll(staticDataService.getSimpleProperties());
        staticData.putAll(staticDataService.getFilterProperties());
        staticData.putAll(staticDataService.getConfigurations());
        staticData.putAll(staticDataService.getProgramCategories());
        staticData.putAll(staticDataService.getActionConditions());
        return staticData;
    }

    @RequestMapping(method = RequestMethod.GET, params = "institutionId")
    public Map<String, Object> getInstitutionStaticData(@RequestParam Integer institutionId) {
        Map<String, Object> staticData = staticDataService.getImportedData(institutionId);
        staticData.put("departments", departmentService.getDepartments(institutionId));
        return staticData;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/domiciles/{domicileId}/importedInstitutions")
    public List<ImportedInstitutionRepresentation> getImportedInstitutions(@PathVariable Integer domicileId) {
        return staticDataService.getImportedInstitutions(domicileId);
    }

}
