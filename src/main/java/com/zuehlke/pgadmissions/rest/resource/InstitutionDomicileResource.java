package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;

@RestController
@RequestMapping("api/domiciles/{domicileId}")
public class InstitutionDomicileResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
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
