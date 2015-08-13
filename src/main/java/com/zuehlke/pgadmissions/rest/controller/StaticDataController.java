package com.zuehlke.pgadmissions.rest.controller;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.rest.representation.advert.CompetenceRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.StaticDataService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedProgramResponse;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/static")
public class StaticDataController {

    @Inject
    private StaticDataService staticDataService;

    @Inject
    private AdvertService advertService;

    @Cacheable("staticData")
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> getStaticData() {
        Map<String, Object> staticData = Maps.newHashMap();
        staticData.putAll(staticDataService.getScopes());
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
        return staticDataService.getInstitutionData(institutionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/institutions/{institutionId}/importedInstitutions")
    public List<ImportedEntityResponse> getImportedInstitutions(@PathVariable Integer institutionId, @RequestParam Integer importedDomicileId) {
        return staticDataService.getImportedInstitutions(institutionId, importedDomicileId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/institutions/{institutionId}/importedPrograms", params = "importedInstitutionId")
    public List<ImportedEntityResponse> getImportedPrograms(@PathVariable Integer institutionId, @RequestParam Integer importedInstitutionId) {
        return staticDataService.getImportedPrograms(institutionId, importedInstitutionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/institutions/{institutionId}/importedPrograms", params = "q")
    public List<ImportedProgramResponse> searchImportedPrograms(
            @PathVariable Integer institutionId, @RequestParam String q, @RequestParam Optional<Boolean> restrictToInstitution) {
        return staticDataService.searchImportedPrograms(institutionId, q, restrictToInstitution.orElse(false));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/competences", params = "q")
    public List<CompetenceRepresentation> searchCompetences(@RequestParam String q) {
        return advertService.searchCompetences(q);
    }

}
