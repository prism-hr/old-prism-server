package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/institutions")
public class InstitutionResource {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ImportedEntityRepresentation> getInstitutions() {
        List<Institution> institutions = institutionService.list();
        List<ImportedEntityRepresentation> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            institutionRepresentations.add(dozerBeanMapper.map(institution, ImportedEntityRepresentation.class));
        }
        return institutionRepresentations;
    }

    @RequestMapping(value = "/{institutionId}/competencies", method = RequestMethod.GET)
    public List<String> getCompetencies(@PathVariable Integer institutionId, PrismLocale locale) throws Exception {
        Institution institution = institutionService.getById(institutionId);
        return institutionService.getCompetencies(institution, locale);
    }

}
