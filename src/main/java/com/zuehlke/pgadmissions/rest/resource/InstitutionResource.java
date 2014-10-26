package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetency;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/institutions")
public class InstitutionResource {

    private static final Logger log = LoggerFactory.getLogger(InstitutionResource.class);

    @Autowired
    private AdvertService advertService;

    @Autowired
    private InstitutionService institutionService;

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    @ResponseBody
    public List<ImportedEntityRepresentation> getInstitutions() {
        List<Institution> institutions = institutionService.list();
        List<ImportedEntityRepresentation> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            ImportedEntityRepresentation institutionRepresentation = new ImportedEntityRepresentation();
            institutionRepresentation.setId(institution.getId());
            institutionRepresentation.setName(institution.getTitle() + " - " + institution.getAddress().getLocationString());
            institutionRepresentations.add(institutionRepresentation);
        }
        return institutionRepresentations;
    }

    @RequestMapping(value = "/{institutionId}/categoryTags", method = RequestMethod.GET, params = "locale")
    public Map<String, List<String>> getCategoryTags(@PathVariable Integer institutionId, @RequestParam PrismLocale locale) throws Exception {
        Map<String, List<String>> categoryTags = Maps.newLinkedHashMap();
        Institution institution = institutionService.getById(institutionId);

        String category = "competencies";
        categoryTags.put(category, advertService.getLocalizedTags(institution, locale, AdvertCompetency.class));
        category = "themes";
        categoryTags.put(category, advertService.getLocalizedTags(institution, locale, AdvertTheme.class));

        return categoryTags;
    }

}
