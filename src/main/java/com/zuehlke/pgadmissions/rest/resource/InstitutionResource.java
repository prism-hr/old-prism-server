package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetency;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/institutions")
public class InstitutionResource {

    private static final Logger log = LoggerFactory.getLogger(InstitutionResource.class);

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

    @RequestMapping(value = "/{institutionId}/categoryTags", method = RequestMethod.GET, params = {"locale", "category"})
    public List<String> getCategoryTags(@PathVariable Integer institutionId, @RequestParam PrismLocale locale, @RequestParam String category) throws Exception {
        Institution institution = institutionService.getById(institutionId);
        if (category.equals("competencies")) {
            return institutionService.getCategoryTags(institution, locale, AdvertCompetency.class);
        } else if (category.equals("themes")) {
            return institutionService.getCategoryTags(institution, locale, AdvertTheme.class);
        }
        log.error("Unknown category: " + category);
        throw new ResourceNotFoundException();
    }

}
