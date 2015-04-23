package com.zuehlke.pgadmissions.rest.resource;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.services.StaticDataService;

@RestController
@RequestMapping("/api/static")
public class StaticDataResource {

    @Inject
    private StaticDataService staticDataService;

    @Cacheable("staticData")
    @RequestMapping(method = RequestMethod.GET, params = "locale")
    public Map<String, Object> getStaticData(@RequestParam(required = false) PrismLocale locale) {
        Map<String, Object> staticData = Maps.newHashMap();
        locale = locale == null ? getSystemLocale() : locale;
        staticData.putAll(staticDataService.getActions());
        staticData.putAll(staticDataService.getStates());
        staticData.putAll(staticDataService.getStateGroups());
        staticData.putAll(staticDataService.getRoles());
        staticData.putAll(staticDataService.getInstitutionDomiciles(locale));
        staticData.putAll(staticDataService.getSimpleProperties());
        staticData.putAll(staticDataService.getFilterProperties());
        staticData.putAll(staticDataService.getConfigurations());
        staticData.putAll(staticDataService.getProgramCategories());
        return staticData;
    }

    @Cacheable("institutionStaticData")
    @RequestMapping(method = RequestMethod.GET, params = "institutionId")
    public Map<String, Object> getImportedData(@RequestParam Integer institutionId) {
        return staticDataService.getImportedData(institutionId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/domiciles/{domicileId}/importedInstitutions")
    public List<ImportedInstitutionRepresentation> getImportedInstitutions(@PathVariable Integer domicileId) {
        return staticDataService.getImportedInstitutions(domicileId);
    }

}
