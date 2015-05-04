package com.zuehlke.pgadmissions.rest.controller;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.dozer.Mapper;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("api/domiciles/{domicileId}")
public class InstitutionDomicileController {

    @Inject
    private EntityService entityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private Mapper dozerBeanMapper;

    @RequestMapping(value = "institutions", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<InstitutionRepresentation> getInstitutions(@PathVariable String domicileId) {
        InstitutionDomicile domicile = entityService.getByProperty(InstitutionDomicile.class, "id", domicileId);
        List<Institution> institutions = institutionService.getApprovedInstitutionsByCountry(domicile);
        List<InstitutionRepresentation> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            institutionRepresentations.add(dozerBeanMapper.map(institution, InstitutionRepresentation.class));
        }
        return institutionRepresentations;
    }

}
