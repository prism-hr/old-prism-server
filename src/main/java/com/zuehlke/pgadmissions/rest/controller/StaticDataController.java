package com.zuehlke.pgadmissions.rest.controller;

import static com.zuehlke.pgadmissions.utils.PrismConstants.REINITIALIZE_SERVER_MESSAGE_FOR_JUAN;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedProgramResponse;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.exceptions.PrismExceptionForJuan;
import com.zuehlke.pgadmissions.services.StaticDataService;

@RestController
@RequestMapping("/api/static")
public class StaticDataController {

    @Inject
    private StaticDataService staticDataService;

    @Cacheable("staticData")
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> getStaticData() throws PrismExceptionForJuan {
        try {
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
            staticData.putAll(staticDataService.getAdvertCompetences());
            return staticData;
        } catch (Exception e) {
            throw new PrismExceptionForJuan(REINITIALIZE_SERVER_MESSAGE_FOR_JUAN, e);
        }
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

}
